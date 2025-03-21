package com.tsAdmin.model.producer;

import com.tsAdmin.common.Coordinate;

/** 木材生产厂 */
public class WoodProducer extends Producer
{
    public WoodProducer(String uuid, String name, Coordinate position)
    {
        super(uuid, name, position);
    }

    @Override
    protected int getMinQuantity() { return 10; }

    @Override
    protected int getMaxQuantity() { return 40; }
}
