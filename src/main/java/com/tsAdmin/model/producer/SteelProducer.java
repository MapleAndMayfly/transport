package com.tsAdmin.model.producer;

import com.tsAdmin.common.Coordinate;

/** 钢材生产厂 */
public class SteelProducer extends Producer
{
    public SteelProducer(String uuid, String name, Coordinate position)
    {
        super(uuid, name, position);
    }

    @Override
    protected int getMinQuantity() { return 20; }

    @Override
    protected int getMaxQuantity() { return 50; }
}
