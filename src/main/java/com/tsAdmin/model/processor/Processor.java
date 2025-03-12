package com.tsAdmin.model.processor;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.Manufacturer;

/** 加工厂(抽象) */
public abstract class Processor extends Manufacturer
{
    protected Processor(String uuid, String name, Coordinate position)
    {
        super(uuid, name, position);
    }

    //Getter
    public String getProcessorName() { return name; }
    public Coordinate getPosition() { return position; }
}
