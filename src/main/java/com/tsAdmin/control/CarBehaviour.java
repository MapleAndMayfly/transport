package com.tsAdmin.control;

import java.util.Random;

import com.tsAdmin.model.Car;
import com.tsAdmin.model.Car.CarState;

/**
 * 车辆行为类，因为放在车辆类里会导致很长，故独立出来
 * 每辆车都有一个CarBehaviour对象
 */
public class CarBehaviour
{
    Car car;

    /**
     * @param car 指向拥有行为的车辆自身
     */
    public CarBehaviour(Car car)
    {
        this.car = car;
    }

    public void tick()
    {
        // TODO: 车辆向前进一个周期
    }

    public void changeState()
    {
        Random random = new Random();
        int randomNumber = random.nextInt(100);

        CarState nextState = car.getState();

        switch (car.getState())
        {
            case ORDER_TAKEN:
                if      (randomNumber < 96) nextState = CarState.LOADING;
                else                        nextState = CarState.FREEZE;
                break;

            case LOADING:
                if      (randomNumber < 97) 
                {                           nextState=configFromNextNode(); }
                else if (randomNumber < 99) nextState = CarState.UNLOADING;
                else                        nextState = CarState.FREEZE;
                break;

            case TRANSPORTING:
                if      (randomNumber < 99) nextState = CarState.UNLOADING;
                else                        nextState = CarState.FREEZE;
                break;

            case UNLOADING:
                if (randomNumber < 97)      nextState = CarState.AVAILABLE;
                else                        nextState = CarState.FREEZE;
                break;

            case FREEZE:
                switch (car.getPrevState())
                {
                    case ORDER_TAKEN:       nextState = CarState.LOADING;
                        break;
                    case LOADING:
                        if      (randomNumber < 97) 
                        {                   nextState=configFromNextNode(); }
                        else                nextState = CarState.UNLOADING;
                        break;

                    case TRANSPORTING:      nextState = CarState.UNLOADING;
                        break;

                    case UNLOADING:         nextState = CarState.AVAILABLE;
                        break;
                    default://正常来讲任何情况都不会进入default
                        break;
                }
                break;

            case AVAILABLE:
                nextState = configFromNextNode();
                break;
            default:
                break;
        }

        // 装货后立马卸货的情况下，下一状态必定是装货
        if (car.getState() == CarState.UNLOADING && car.getPrevState() == CarState.LOADING)
        {
            nextState = CarState.LOADING;
        }

        if(car.getState()!=CarState.AVAILABLE && nextState == CarState.AVAILABLE)
        {
            car.setState(nextState);
            changeState();
            return;
        }
        car.setstateTimer(getBehaviourTime(nextState));
        car.setState(nextState);
        DBManager.updateCarState(car);
    }

    /** 状态转换后获取任务时间 */
    private int getBehaviourTime(CarState carState) 
    {
        switch (carState) 
        {
        case LOADING:
            return (int)(1.2 * car.getCurrDemand().getQuantity());
        case UNLOADING:
            return (int)(0.9 * car.getCurrDemand().getQuantity());
        case FREEZE:
            return 30;
        default:
            return 0;
        }
    }

    private CarState configFromNextNode()
    {
        if(!car.getDemandEmpty())
        {
            if(car.getFirstNode().isOrigin())
            { 
                car.setCurrDemand(car.getFirstNode().getDemand()); 
                return CarState.ORDER_TAKEN;
            }
            else
            {
                car.setCurrDemand(car.getFirstNode().getDemand());
                return CarState.TRANSPORTING;
            }
        }
        return CarState.AVAILABLE;
    }
}
