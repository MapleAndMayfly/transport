package com.tsAdmin.model;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.Product.ProductType;;

/** 需求 */
public class Demand
{
    /** 唯一标识符 */
    private String uuid;
    /** 起点 */
    private Coordinate origin;
    /** 终点 */
    private Coordinate destination;
    /** 货物 */
    private Product product;
    /** 是否已经分配完毕 */
    private boolean isFullyAssigned;
    /** 该订单分配予的车辆数 */
    private int assignedVehicles;
    /** 该订单的具体分配情况 */
    private TransportAssignment<String,Product> vehicleAssignments;

    public Demand(String uuid, Coordinate origin, Coordinate destination, Product product)
    {
        this.uuid = uuid;
        this.origin = origin;
        this.destination = destination;
        this.product = product;
        this.isFullyAssigned = false;
        this.assignedVehicles = 0;
        this.vehicleAssignments=new TransportAssignment<String,Product>();
    }

    /**  拷贝构造方法 */
    public Demand(Demand other)
    {
        this.uuid = other.uuid;
        this.product = other.product;
        this.origin = new Coordinate(other.origin.lat, other.origin.lon);
        this.destination = new Coordinate(other.destination.lat, other.destination.lon);
        this.assignedVehicles = other.assignedVehicles;
        this.isFullyAssigned = other.isFullyAssigned;
        this.vehicleAssignments=new TransportAssignment<String,Product>(other.vehicleAssignments.entries());
    }

    // Setter
    // public void setOrigin(Coordinate origin) { this.origin = origin; }
    // public void setDestination(Coordinate destination) { this.destination = destination; }
    // public void setProduct(Product product) { this.product = product; }
    public void setQuantity(int quantity) { this.product.setQuantity(quantity); }
    public void setVolume(int volume) { this.product.setVolume(volume); }
    public void setProcessed() { this.isFullyAssigned = true; }
    public void addAssignedVehicles() { this.assignedVehicles++; }
    public void cutAssignedVehicles() { this.assignedVehicles--; }
    public void addVehicleAssignments(String uuid,Product product) { this.vehicleAssignments.put(uuid,product); }
    public void finishVehicleAssignments(String uuid) { this.vehicleAssignments.removeByKey(uuid); }

    // Getter
    public String getUUID() { return uuid; }
    public Coordinate getOrigin() { return origin; }
    public Coordinate getDestination() { return destination; }
    public ProductType getType() { return product.getType(); }
    public int getQuantity() { return product.getQuantity(); }
    public int getVolume() { return product.getVolume(); }
    public boolean isFullyAssigned() { return isFullyAssigned; }
    public int getAssignedVehicles() { return assignedVehicles; }
    public Product productVehicleAssignments(String uuid) { return this.vehicleAssignments.getValueByKey(uuid); }

    public int routeLength()
    {
        return (int)Coordinate.distance(origin, destination);
    }
}