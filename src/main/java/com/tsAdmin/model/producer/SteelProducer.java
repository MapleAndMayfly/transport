package com.tsAdmin.model.producer;

import java.util.Random;

import com.tsAdmin.common.Coordinate;

/** 钢材生产厂 */
public class SteelProducer extends Producer
{
    protected static final Random RANDOM = new Random();

    public SteelProducer(String uuid, String name, Coordinate position)
    {
        super(uuid, name, position);
    }

    @Override
    protected int getMinDensity() { return 7000; }

    @Override
    protected int getMaxDensity() { return 8000; }

    @Override
    protected int getMinQuantity() { return 20; }

    @Override
    protected int getMaxQuantity() { return 50; }
}
