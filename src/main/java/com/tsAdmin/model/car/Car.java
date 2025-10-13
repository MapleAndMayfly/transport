package com.tsAdmin.model.car;

import java.util.ArrayList;
import java.util.List;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.common.PathNode;
import com.tsAdmin.common.Timer;
import com.tsAdmin.model.Demand;

/** 车辆 */
public class Car
{
    /** 车辆类型 */
    public static enum CarType
    {
        /** 普通车 */
        COMMON,
        /** 保温车(运输食品/医药) */
        INSULATED_VAN,
        /** 危险品车 */
        DANGEROUS,
        /** 超大车 */
        OVERSIZED,
        /** 油罐车 */
        TANKER,
        /** 减震车辆（运送精密仪器，例如卫星，导弹，高端服务器等） */
        SHOCK_ABSORBER
    }

    /** 车辆状态 */
    public static enum CarState
    {
        /** 空闲 */
        AVAILABLE,
        /** 接单行驶 */
        ORDER_TAKEN,
        /** 装货 */
        LOADING,
        /** 运货行驶 */
        TRANSPORTING,
        /** 卸货 */
        UNLOADING,
        /** 停车等待 */
        FREEZE
    }

    private String uuid;                                // 车辆唯一标识符
    private int maxLoad, maxVolume;                     // 车辆荷载量
    private int load, volume;                           // 车辆当前载重量
    private CarType carType;                            // 车辆类型
    private Coordinate position;                        // 车辆当前位置
    private List<PathNode> nodeList = new ArrayList<>();// 车辆订单路径列表
    private CarBehaviour behaviour;                     // 车辆行为
    private CarState currState;                         // 当前车辆状态
    private CarState prevState;                         // 上一车辆状态
    private Timer stateTimer;                           // 状态计时器
    private Demand currDemand;                          // 目前订单缓存
    private CarStat carStat;                            // 车辆统计参数

    /**
     * 车辆的拷贝构造方法
     * <p><i>拷贝得到的车辆不带有行为类、计时器以及统计数据等数据</i>
     * @param others 被拷贝的车辆
     */
    public Car(Car others)
    {
        this(others.uuid, others.carType, others.maxLoad, others.maxVolume, others.position);
        this.load = others.load;
        this.volume = others.volume;
    }

    /** 车辆构造函数 */
    public Car(String uuid, CarType carType, int maxLoad, int maxVolume, Coordinate position)
    {
        this.uuid = uuid;
        this.carType = carType;
        this.maxLoad = maxLoad;
        this.maxVolume = maxVolume;
        this.position = position;
        this.behaviour = new CarBehaviour(this);
        this.stateTimer = new Timer();
        this.carStat = new CarStat();
    }

    // Setter
    // public void setMaxLoad(int authorizedLoad) { this.maxLoad = authorizedLoad; }
    // public void setMaxVolume(int volume) { this.maxVolume = volume; }
    // public void setType(CarType carType) { this.carType = carType; }
    public void setLoad(int load) { this.load = load; }
    public void setVolume(int volume) { this.volume = volume; }
    public void setPosition(Coordinate position) { this.position = position; }
    public void setNodeList(List<PathNode> nodeList) { this.nodeList = nodeList; }
    public void setCurrDemand(Demand currDemand) { this.currDemand = currDemand; }
    public void setstateTimer(int time){ this.stateTimer.setTime(time); }
    public void setState(CarState newState)
    {
        prevState = currState;
        currState = newState;
    }

    // Getter
    public String getUUID() { return uuid; }
    public CarType getType() { return carType; }
    public int getMaxLoad() { return maxLoad; }
    public int getMaxVolume() { return maxVolume; }
    public int getLoad() { return load; }
    public int getVolume() { return volume; }
    public Coordinate getPosition() { return position; }
    public List<PathNode> getNodeList() { return nodeList; }
    public PathNode getFirstNode() { return nodeList.getFirst(); }
    public Demand getCurrDemand() { return currDemand; }
    public boolean isDemandEmpty() { return nodeList.isEmpty(); }
    public CarState getState() { return currState; }
    public CarState getPrevState() { return prevState; }
    public Timer getStateTimer() { return stateTimer; }
    public CarStat getCarStat() { return carStat; }
    public double getRemainingLoad() { return maxLoad - load; }
    public double getRemainingVolume() { return maxVolume - volume; }

    public boolean isType(CarType carType) { return this.carType == carType; }
    public void tick()
    {
        stateTimer.tick();
        carStat.setExistTime(carStat.getExistTime() + 30);
    }
    public void changeState() { behaviour.changeState(); }
    public void addPathNode(PathNode node) { nodeList.add(node); }
    public void rmvFirstNode() { nodeList.removeFirst(); }
}
