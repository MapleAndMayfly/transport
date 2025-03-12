package com.tsAdmin.model;

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

    private String uuid;            // 唯一标识符
    private int maxLoad;            // 车辆核载
    // private int maxVolume;       // 车辆容积
    private int load;               // 车辆载重
    private CarType carType;        // 车辆类型
    private Coordinate position;    // 车辆位置
    private Demand demand;          // 车辆订单
    private CarBehaviour behaviour; // 车辆行为
    private CarState currState;     // 当前车辆状态
    private CarState prevState;     // 上一车辆状态
    private Timer stateTimer;       // 状态计时器

    // TODO: 改以下
    private double maxStartDistance=Double.POSITIVE_INFINITY; // 可接受的最大当前位置到需求起点距离
    private double maxDemandLength=Double.POSITIVE_INFINITY;   // 可接受的最大需求路线长度
    private double maxDistanceToLengthRatio=1000; // 最大起点距离与需求长度的比值
    private double minLoadPercent=0.45; // 最小载荷和车辆载重的比值
    // ENDTODO

    public Car(String uuid, CarType carType, int maxLoad, Coordinate position)
    {
        this.uuid = uuid;
        this.carType = carType;
        this.maxLoad = maxLoad;
        this.position = position;
        this.behaviour = new CarBehaviour(this);
        this.stateTimer = new Timer();
    }

    // Setter
    public void setMaxLoad(int authorizedLoad) { this.maxLoad = authorizedLoad; }
    // public void setMaxVolume(int volume) { this.maxVolume = volume; }
    public void setLoad(int load) { this.load = load; }
    public void setType(CarType carType) { this.carType = carType; }
    public void setPosition(Coordinate position) { this.position = position; }
    public void setDemand(Demand demand) { this.demand = demand; }
    public void setState(CarState newState)
    {
        prevState = currState;
        currState = newState;
    }

    public void setLoad() { load = Math.max(demand.getQuantity(), maxLoad); }

    // Getter
    public String getUUID() { return uuid; }
    public int getMaxLoad() { return maxLoad; }
    // public int getMaxVolume() { return maxVolume; }
    public int getLoad() { return load; }
    public Coordinate getPosition() { return position; }
    public Demand getDemand() { return demand; }
    public CarBehaviour getBehaviour() { return behaviour; }
    public CarState getState() { return currState; }
    public CarState getPrevState() { return prevState; }
    public Timer getStateTimer() { return stateTimer; }

    public boolean isType(CarType carType) { return this.carType == carType; }

    // TODO:改以下
    public double getMaxStartDistance() { return maxStartDistance; }
    public double getMaxDemandLength() { return maxDemandLength; }
    public double getMaxDistanceToLengthRatio() { return maxDistanceToLengthRatio; }
    public double getMinLoadPercent() { return minLoadPercent; }
    // ENDTODO

    public int orderTakenLength()
    {
        return (int)Coordinate.distance(position, demand.getOrigin());
    }
}
