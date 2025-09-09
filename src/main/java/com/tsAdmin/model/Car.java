package com.tsAdmin.model;

import java.util.ArrayList;
import java.util.List;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.common.Timer;
import com.tsAdmin.control.CarBehaviour;

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
        /** 减震车辆（运送精密仪器，例如卫星，导弹，高端服务器等等） */
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

    private String uuid;                                // 唯一标识符
    private int maxLoad, maxVolume;                     // 车辆荷载
    private int load, volume;                           // 车辆载重
    private double remainingLoad, remainingVolume;      // 车辆剩余
    private CarType carType;                            // 车辆类型
    private Coordinate position;                        // 车辆位置
    private List<PathNode> nodeList = new ArrayList<>();// 车辆订单二元组
    private CarBehaviour behaviour;                     // 车辆行为
    private CarState currState;                         // 当前车辆状态
    private CarState prevState;                         // 上一车辆状态
    private Timer stateTimer;                           // 状态计时器
    private Demand currDemand;                          // 目前订单缓存
    private CarStat carStat;                            // 车辆统计参数

    public Car(Car c)
    {
        this(c.uuid, c.carType, c.maxLoad, c.maxVolume, c.position);
        this.remainingLoad = c.remainingLoad;
        this.remainingVolume = c.remainingVolume;
        this.carStat = new CarStat();
    }
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
    public void setRemainingLoad(double remainingLoad) { this.remainingLoad = remainingLoad; }
    public void setRemainingVolume(double remainingVolume) { this.remainingVolume = remainingVolume; }
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
    public int getMaxLoad() { return maxLoad; }
    public int getMaxVolume() { return maxVolume; }
    public int getLoad() { return load; }
    public int getVolume() { return volume; }
    public double getRemainingLoad() { return remainingLoad; }
    public double getRemainingVolume() { return remainingVolume; }
    public Coordinate getPosition() { return position; }
    public List<PathNode> getNodeList() { return nodeList; }
    public PathNode getFirstNode() { return nodeList.getFirst(); }
    public Demand getCurrDemand() { return currDemand; }
    public boolean isDemandEmpty() { return nodeList.isEmpty(); }
    public CarState getState() { return currState; }
    public CarState getPrevState() { return prevState; }
    public Timer getStateTimer() { return stateTimer; }
    public CarStat getCarStat() { return carStat; }

    public boolean isType(CarType carType) { return this.carType == carType; }
    public void tick() { 
        stateTimer.tick(); 
        // 每次仿真推进，累计车辆总时间（单位：秒，步长为30）
        carStat.setExistTime(carStat.getExistTime() + 30);
    }
    public void changeState(){ behaviour.changeState(); }
    public void addPathNode(PathNode node) { nodeList.add(node); }
    public void deleteFirstNode() { nodeList.removeFirst(); }
}
