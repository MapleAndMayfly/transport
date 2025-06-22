package com.tsAdmin.model.producer;

import com.tsAdmin.common.Coordinate;

/** 药品生产厂 */
public class PharmaceuticalProducer extends Producer
{
    public PharmaceuticalProducer(String uuid, String name, Coordinate position)
    {
        super(uuid, name, position);
    }

    @Override
    protected int getMinDensity() { return 700; }

    @Override
    protected int getMaxDensity() { return 1000; }

    @Override
    protected int getMinQuantity() { return 5; }

    @Override
    protected int getMaxQuantity() { return 20; }
}
