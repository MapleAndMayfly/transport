package com.tsAdmin.model.processor;

import com.tsAdmin.common.Coordinate;

/** 药材加工厂 */
public class PharmaceuticalProcessor extends Processor
{
    public PharmaceuticalProcessor(String uuid, String name, Coordinate position)
    {
        super(uuid, name, position);
    }

    @Override
    public String getProcessorName()
    {
        if (name.isEmpty())
        return "药材加工厂";
        else
        return name;
    }
}
