package com.tsAdmin.model.car;

/**
 * 车辆统计参数类，用于存储每辆车的运营统计数据。
 * 后续可逐步添加更多参数。
 */
public class CarStat
{
    // 车辆累计接单数
    private int tripCount = 0;
    // 车辆累计行驶距离（单位：米或公里，视业务需求）
    private double totalDistance = 0.0;
    // 停车等待时间（单位：秒）
    private int waitingTime = 0;
    // 空驶里程（单位：与总里程一致，通常为公里）
    private double emptyDistance = 0.0;
    // 车辆从出生到现在的总时间（单位：秒）
    private int existTime = 0;
    // 有运输任务的累计时间（单位：秒）
    private int transportingTime = 0;
    // 累计空闲时间（单位：秒）
    private int freeTime = 0;
    // 空闲比（freeTime/existTime）
    private double freeRate = 0.0;
    // 累计完成运输的货物总重量（单位与quantity一致）
    private int totalWeight = 0;
    // 累计浪费的载重（单位与maxLoad一致）
    private int wastedLoad = 0;
    // 本次任务成本
    private double cost = 0.0;
    // 累计任务成本
    private double totalCost = 0.0;

    /**
     * 构造方法，初始化所有参数
     */
    public CarStat() {}

    /**
     * 获取车辆累计行驶距离
     * @return 行驶距离
     */
    public double getTotalDistance() { return totalDistance; }
    public void setTripCount(int tripCount){ this.tripCount=tripCount; }
    public int  getTripCount() {return tripCount;}
    /**
     * 设置车辆累计行驶距离
     * @param totalDistance 行驶距离
     */
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

    
    public int getWaitingTime() { return waitingTime; }
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }

    public double getEmptyDistance() { return emptyDistance; }
    public void setEmptyDistance(double emptyDistance) { this.emptyDistance = emptyDistance; }

    public int getExistTime() { return existTime; }
    public void setExistTime(int existTime) { this.existTime = existTime; }

    public int getTransportingTime() { return transportingTime; }
    public void setTransportingTime(int transportingTime) { this.transportingTime = transportingTime; }

    public int getFreeTime() { return freeTime; }
    public void setFreeTime(int freeTime) { this.freeTime = freeTime; }

    public double getFreeRate() { return freeRate; }
    public void setFreeRate(double freeRate) { this.freeRate = freeRate; }

    public int getTotalWeight() { return totalWeight; }
    public void setTotalWeight(int totalWeight) { this.totalWeight = totalWeight; }

    public int getWastedLoad() { return wastedLoad; }
    public void setWastedLoad(int wastedLoad) { this.wastedLoad = wastedLoad; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public double getutilization()
    {
        double utilization=((totalDistance-emptyDistance)/totalDistance)*100;
        if(utilization==0)return 0;
        else return utilization;
    }
} 