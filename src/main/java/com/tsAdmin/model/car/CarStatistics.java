package com.tsAdmin.model.car;

/**
 * 车辆统计参数类，用于存储每辆车的运营统计数据。
 * 后续可逐步添加更多参数。
 */
public class CarStatistics
{
    private int completeCount = 0;      // 车辆累计完成定单数
    private double totalDistance = 0.0; // 车辆累计行驶距离
    private double emptyDistance = 0.0; // 空驶里程
    private int busyTime = 0;           // 累计工作时间
    private int idleTime = 0;           // 累计空闲时间
    private int totalWeight = 0;        // 累计货运质量
    private int accWastedLoad = 0;      // 累计浪费载重
    private double cost = 0.0;          // 当前货运成本
    private double totalCost = 0.0;     // 累计货运成本

    public CarStatistics() {}

    // Getter
    public int  getCompleteCount() {return completeCount;}
    public double getTotalDistance() { return totalDistance; }
    public double getEmptyDistance() { return emptyDistance; }
    public int getBusyTime() { return busyTime; }
    public int getIdleTime() { return idleTime; }
    public int getTotalWeight() { return totalWeight; }
    public int getAccWastedLoad() { return accWastedLoad; }
    public double getCost() { return cost; }
    public double getTotalCost() { return totalCost; }
    /** 获取空闲比 */
    public double getIdleRate() { return idleTime / (double)(busyTime + idleTime); }
    /** 获取货运资源利用率 */
    public double getUtilization() { return (totalDistance - emptyDistance) / totalDistance; }

    /** 累计完成订单数 +1 */
    public void plusCompleteCount(){ completeCount++; }
    /**
     * 增加总行驶里程
     * @param distance 增加的里程数
     */
    public void plusTotalDistance(double distance) { totalDistance += distance; }
    /**
     * 增加空驶里程
     * @param distance 增加的里程数
     */
    public void plusEmptyDistance(double distance) { emptyDistance += distance; }
    /**
     * 增加累计工作时间
     * @param time 增加的时间
     */
    public void plusBusyTime(int time) { busyTime += time; }
    /**
     * 增加累计空闲时间
     * @param time 增加的时间
     */
    public void plusIdleTime(int time) { idleTime += time; }
    /**
     * 增加累计货运质量
     * @param weight 增加的质量
     */
    public void plusTotalWeight(int weight) { totalWeight += weight; }
    /**
     * 增加累计浪费载重
     * @param load 增加的载重
     */
    public void plusAccWastedLoad(int load) { accWastedLoad += load; }
    public void setCost(double cost) { this.cost = cost; }
    /**
     * 增加累计货运成本
     * @param cost 增加的成本
     */
    public void plusTotalCost(double cost) { totalCost += cost; }
} 