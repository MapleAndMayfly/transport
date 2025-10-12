package com.tsAdmin.model;

import com.tsAdmin.common.Coordinate;

/** 厂商类，是所有厂商的父类 */
public abstract class Manufacturer
{
    protected String uuid;
    protected String name;
    protected Coordinate position;

    protected Manufacturer(String uuid, String name, Coordinate position)
    {
        this.uuid = uuid;
        this.name = name;
        this.position = position;
    }

    public String getName() { return name; }
    public Coordinate getPosition() { return position; }
}
