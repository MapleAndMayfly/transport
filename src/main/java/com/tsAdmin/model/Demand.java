package com.tsAdmin.model;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.Product.ProductType;

/** 需求 */
public class Demand
{
    private String uuid;            // 唯一标识符
    private Coordinate origin;      // 起点
    private Coordinate destination; // 终点
    private ProductType productType;// 货物种类
    private int quantity;           // 货物质量

    public Demand(String uuid, Coordinate origin, Coordinate destination, ProductType productType)
    {
        this.uuid = uuid;
        this.origin = origin;
        this.destination = destination;
        this.productType = productType;
    }

    // Setter
    // public void setOrigin(Coordinate origin) { this.origin = origin; }
    // public void setDestination(Coordinate destination) { this.destination = destination; }
    // public void setProductType(ProductType productType) { this.productType = productType; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Getter
    public String getUUID() { return uuid; }
    public Coordinate getOrigin() { return origin; }
    public Coordinate getDestination() { return destination; }
    public ProductType getProductType() { return productType; }
    public int getQuantity() { return quantity; }

    public int routeLength()
    {
        return (int)Coordinate.distance(origin, destination);
    }
}