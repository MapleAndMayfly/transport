package com.tsAdmin.model.processor;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.ProductType;

/** 加工厂类 */
public class Processor
{
    private String uuid;
    private String name;
    private ProductType type;
    private Coordinate position;

    public Processor(String uuid, String name, ProductType type, Coordinate position)
    {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.position = position;
    }

    public String getUUID() { return uuid; }
    public String getName() { return !name.isEmpty() ? name : type.getName() + "加工厂"; }
    public ProductType getType() { return type; }
    public Coordinate getPosition() { return position; }
}
