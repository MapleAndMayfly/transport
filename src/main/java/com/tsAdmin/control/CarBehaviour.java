package com.tsAdmin.control;

import java.util.Random;

import com.tsAdmin.model.Car;
import com.tsAdmin.model.Car.CarState;
import com.tsAdmin.model.DemandList;

/**
 * 车辆行为类，负责管理车辆的状态流转和相关业务逻辑。
 * 由于车辆的行为逻辑较为复杂，单独抽离为此类，避免Car类过于臃肿。
 * 每辆车都拥有一个CarBehaviour对象，负责其状态变化。
 */
public class CarBehaviour
{
    // 关联的车辆对象，代表当前行为操作的目标车辆
    Car car;

    /**
     * 构造方法，初始化行为对象时绑定一辆车
     * @param car 指向拥有行为的车辆自身
     */
    public CarBehaviour(Car car)
    {
        this.car = car;
    }

    /**
     * 车辆状态机主逻辑，根据当前状态和随机数决定车辆的下一个状态，并处理装卸货、冻结等逻辑。
     * 状态流转顺序及概率均有业务规则，部分状态下会递归调用自身以实现连续状态流转。
     */
    public void changeState()
    {
        Random random = new Random(); // 用于生成随机数，模拟现实中的不确定性
        int randomNumber = random.nextInt(100); // 生成0-99的随机数，用于概率判断

        CarState nextState = car.getState(); // 默认下一个状态为当前状态，后续根据业务逻辑调整

        // 根据车辆当前状态决定状态转移
        switch (car.getState())
        {
            case ORDER_TAKEN: // 已接单，准备装货
            car.setPosition(car.getCurrDemand().getOrigin());
                // 96%概率进入装货状态，4%概率进入冻结状态（如遇到突发状况）
                if      (randomNumber < 96) nextState = CarState.LOADING;
                else                        nextState = CarState.FREEZE;
                break;

            case LOADING: // 装货完成
                // 装货时，车辆的载重和体积会增加，剩余载重和体积随之减少
                car.setLoad(car.getLoad()+car.getCurrDemand().productVehicleAssignments(car.getUUID()).getQuantity()); // 增加当前订单的货物数量
                car.setRemainingLoad(car.getMaxLoad()-car.getLoad());         // 更新剩余载重
                car.setVolume(car.getVolume()+car.getCurrDemand().productVehicleAssignments(car.getUUID()).getVolume()); // 增加当前订单的货物体积
                car.setRemainingVolume(car.getMaxVolume()-car.getVolume());     // 更新剩余体积
                // 统计本次装货的浪费载重
                if (car.getCurrDemand() != null) {
                    int wasted = car.getMaxLoad() - car.getCurrDemand().getQuantity();
                    if (wasted > 0) {
                        car.getCarStat().setWastedLoad(car.getCarStat().getWastedLoad() + wasted);
                    }
                }
                // 97%概率进入下一个节点配置的状态（如运输/接单），2%概率直接卸货，1%概率冻结
                if      (randomNumber < 97) nextState = configFromNextNode();
                else if (randomNumber < 99) nextState = CarState.UNLOADING;
                else                        nextState = CarState.FREEZE;
                break;

            case TRANSPORTING: // 运输完成
            car.setPosition(car.getCurrDemand().getDestination());
                // 99%概率运输完成后进入卸货，1%概率运输途中遇到异常进入冻结
                if (randomNumber < 99) {
                    nextState = CarState.UNLOADING;
                } else {
                    nextState = CarState.FREEZE;
                }
                break;

            case UNLOADING: // 卸货完成
                car.getCarStat().settripCount(car.getCarStat().gettripCount()+1);
                // 卸货时，车辆的载重和体积会减少，剩余载重和体积随之增加
                car.setLoad(car.getLoad() - car.getCurrDemand().productVehicleAssignments(car.getUUID()).getQuantity()); // 减去当前订单的货物数量
                car.setRemainingLoad(car.getMaxLoad() - car.getLoad());         // 更新剩余载重
                car.setVolume(car.getVolume() - car.getCurrDemand().productVehicleAssignments(car.getUUID()).getVolume()); // 减去当前订单的货物体积
                car.setRemainingVolume(car.getMaxVolume() - car.getVolume());     // 更新剩余体积
                // 累加本次卸货的货物重量到统计参数
                if (car.getCurrDemand() != null) {
                    int quantity = car.getCurrDemand().getQuantity();
                    car.getCarStat().setTotalWeight(car.getCarStat().getTotalWeight() + quantity);
                }
                // 从订单里移除该车的分配情况
                car.getCurrDemand().finishVehicleAssignments(car.getUUID());
                car.getCurrDemand().cutAssignedVehicles();
                // 若订单全部完成从需求列表中移除已完成的订单，若移除失败则打印错误
                if(car.getCurrDemand().getAssignedVehicles()==0)
                {if(DemandList.demandList.remove(car.getCurrDemand().getUUID()) == null) System.err.println("删除订单出错");}
                // 97%概率变为可用，3%概率冻结（如卸货异常）
                if (randomNumber < 97)      nextState = CarState.AVAILABLE;
                else                        nextState = CarState.FREEZE;
                break;

            case FREEZE: // 冻结状态，车辆因故障或异常暂停服务
                // 冻结恢复后根据上一个状态决定如何继续
                switch (car.getPrevState())
                {
                    case ORDER_TAKEN:       // 上一状态为已接单，恢复后应进入装货
                        nextState = CarState.LOADING;
                        break;
                    case LOADING:           // 上一状态为装货，97%概率继续后续节点，3%概率直接卸货
                        if (randomNumber < 97)
                        {                   nextState=configFromNextNode(); }
                        else                nextState = CarState.UNLOADING;
                        break;
                    case TRANSPORTING:      // 上一状态为运输，恢复后应进入卸货
                        nextState = CarState.UNLOADING;
                        break;
                    case UNLOADING:         // 上一状态为卸货，恢复后应变为可用
                        nextState = CarState.AVAILABLE;
                        break;
                    default:                // 理论上不会进入此分支，防御性编程
                        break;
                }
                break;

            case AVAILABLE: // 可用状态，车辆空闲，尝试分配新任务
                // 若有新需求则分配，否则保持可用
                nextState = configFromNextNode();
                break;
            default:
                // 其他未知状态，保持原状态
                break;
        }

        // 特殊情况：如果车辆刚刚装货后立马卸货，强制下一状态为装货，防止状态机异常跳转
        if (car.getState() == CarState.UNLOADING && car.getPrevState() == CarState.LOADING)
        {
            nextState = CarState.LOADING;
        }

        // 如果当前不是可用状态，但下一个状态是可用，递归调用changeState，
        // 以实现连续状态流转（如装货后直接变为可用，再自动分配新任务）
        if(car.getState()!=CarState.AVAILABLE && nextState == CarState.AVAILABLE)
        {
            car.setState(nextState); // 先设置为可用
            changeState();           // 递归调用，继续分配任务
            return;                  // 递归出口
        }
        // 设置状态计时器（行为持续时间），如装卸货/冻结等有耗时
        car.setstateTimer(getBehaviourTime(nextState));
        // 更新车辆状态为下一个状态
        car.setState(nextState);
        // 同步车辆状态到数据库，保证数据一致性
        DBManager.updateCarState(car);

        // 在状态切换前，统计等待时间（只统计等待类状态）
        if (car.getState() == CarState.LOADING || car.getState() == CarState.UNLOADING || car.getState() == CarState.FREEZE || car.getState() == CarState.AVAILABLE) {
            int duration = car.getStateTimer().getTime(); // 获取上一个状态持续时间（单位：秒）
            car.getCarStat().setWaitingTime(car.getCarStat().getWaitingTime() + duration);
        }
        // 在状态切换前，统计运输任务时间（只统计运输相关状态）
        if (car.getState() == CarState.ORDER_TAKEN || car.getState() == CarState.LOADING || car.getState() == CarState.TRANSPORTING || car.getState() == CarState.UNLOADING) {
            int duration = car.getStateTimer().getTime(); // 获取上一个状态持续时间（单位：秒）
            car.getCarStat().setTransportingTime(car.getCarStat().getTransportingTime() + duration);
        }
        // 在状态切换前，统计空闲时间（只统计AVAILABLE状态）
        if (car.getState() == CarState.AVAILABLE) {
            int duration = car.getStateTimer().getTime();
            car.getCarStat().setFreeTime(car.getCarStat().getFreeTime() + duration);
        }
        // 每次状态切换后，更新空闲比freeRate
        int existTime = car.getCarStat().getExistTime();
        if (existTime > 0) {
            double freeRate = (double) car.getCarStat().getFreeTime() / existTime;
            car.getCarStat().setFreeRate(freeRate);
        }
    }

    /**
     * 根据状态获取行为持续时间（单位：秒），用于模拟现实中操作耗时。
     * @param carState 车辆状态
     * @return 行为持续时间，装卸货与货物数量成正比，冻结为固定时间，其他为0
     */
    private int getBehaviourTime(CarState carState) 
    {
        switch (carState)
        {
        case LOADING:
            // 装货时间与货物数量成正比，比例系数0.01
            return (int)(0.01 * car.getCurrDemand().productVehicleAssignments(car.getUUID()).getQuantity());
        case UNLOADING:
            // 卸货时间与货物数量成正比，比例系数0.01
            return (int)(0.01 * car.getCurrDemand().productVehicleAssignments(car.getUUID()).getQuantity());
        case FREEZE:
            // 冻结状态固定30秒，模拟车辆故障或等待恢复
            return 30;
        default:
            // 其他状态（如运输、可用等）无等待时间
            return 0;
        }
    }

    /**
     * 根据车辆下一个节点配置状态。
     * 若车辆还有未完成的需求，则分配新任务，否则变为可用。
     * @return 下一个状态（ORDER_TAKEN/TRANSPORTING/AVAILABLE）
     */
    private CarState configFromNextNode()
    {
        // 判断车辆是否还有未完成的需求（如有则分配新任务）
        if(!car.isDemandEmpty())
        {
            // 设置当前需求为第一个节点的需求，保证任务顺序
            car.setCurrDemand(car.getFirstNode().getDemand());
            if(car.getFirstNode().isOrigin())
            {
                com.tsAdmin.common.Coordinate lastPos = car.getPosition();
                com.tsAdmin.common.Coordinate destPos = car.getCurrDemand().getOrigin();
                double distance = com.tsAdmin.common.Coordinate.distance(lastPos, destPos);
                // 统计空驶：如果移动前载重为0，则本次为"空驶"
                if (car.getLoad() == 0) {
                    car.getCarStat().setEmptyDistance(car.getCarStat().getEmptyDistance() + distance);
                }
                // 总里程也要累加
                car.getCarStat().setTotalDistance(car.getCarStat().getTotalDistance() + distance);
            }
            else
            {
                com.tsAdmin.common.Coordinate lastPos = car.getPosition();
                com.tsAdmin.common.Coordinate destPos = car.getCurrDemand().getDestination();
                double distance = com.tsAdmin.common.Coordinate.distance(lastPos, destPos);
                // 总里程也要累加
                car.getCarStat().setTotalDistance(car.getCarStat().getTotalDistance() + distance);
            }
            // 如果下一个节点是起点，则进入接单状态，否则进入运输状态
            return car.getFirstNode().isOrigin() ? CarState.ORDER_TAKEN : CarState.TRANSPORTING;
        }
        // 没有需求则为可用状态，等待新任务
        return CarState.AVAILABLE;
    }
}
