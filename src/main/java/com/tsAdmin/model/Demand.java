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

    public Demand(String uuid, Coordinate origin, Coordinate destination, Product product)
    {
        this.uuid = uuid;
        this.origin = origin;
        this.destination = destination;
        this.product = product;
    }

    /**  拷贝构造方法 */
    public Demand(Demand other)
    {
        this.uuid = other.uuid;
        this.product = other.product;
        this.origin = new Coordinate(other.origin.lat, other.origin.lon);
        this.destination = new Coordinate(other.destination.lat, other.destination.lon);
    }

    // Setter
    // public void setOrigin(Coordinate origin) { this.origin = origin; }
    // public void setDestination(Coordinate destination) { this.destination = destination; }
    // public void setProduct(Product product) { this.product = product; }
    public void setQuantity(int quantity) { this.product.setQuantity(quantity); }
    public void setVolume(int volume) { this.product.setVolume(volume); }

    // Getter
    public String getUUID() { return uuid; }
    public Coordinate getOrigin() { return origin; }
    public Coordinate getDestination() { return destination; }
    public ProductType getType() { return product.getType(); }
    public int getQuantity() { return product.getQuantity(); }
    public int getVolume() { return product.getVolume(); }

    public int routeLength()
    {
        return (int)Coordinate.distance(origin, destination);
    }
}