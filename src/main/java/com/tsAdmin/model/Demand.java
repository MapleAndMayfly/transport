package com.tsAdmin.model;

import com.tsAdmin.common.Coordinate;

/** 需求 */
public class Demand
{
    private String uuid;
    private Coordinate origin;
    private Coordinate destination;
    private Product product;

    private boolean isAssigned;

    public Demand(String uuid, Coordinate origin, Coordinate destination, Product product)
    {
        this.uuid = uuid;
        this.origin = origin;
        this.destination = destination;
        this.product = product;
        this.isAssigned = false;
    }

    // Setter
    public void setQuantity(int quantity) { this.product.setQuantity(quantity); }
    public void setVolume(int volume) { this.product.setVolume(volume); }
    public void setAssigned() { this.isAssigned = true; }

    // Getter
    public String getUUID() { return uuid; }
    public Coordinate getOrigin() { return origin; }
    public Coordinate getDestination() { return destination; }
    public ProductType getType() { return product.getType(); }
    public int getQuantity() { return product.getQuantity(); }
    public int getVolume() { return product.getVolume(); }
    public boolean isAssigned() { return isAssigned; }

    public int routeLength()
    {
        return (int)Coordinate.distance(origin, destination);
    }
}