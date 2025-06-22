package com.tsAdmin.model.producer;

import java.util.UUID;
import java.util.Random;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.Demand;
import com.tsAdmin.model.Manufacturer;
import com.tsAdmin.model.Product;
import com.tsAdmin.model.Product.ProductType;
import com.tsAdmin.model.processor.Processor;

/** 生产厂(抽象) */
public abstract class Producer extends Manufacturer
{
    protected static final Random RANDOM = new Random();

    protected Producer(String uuid, String name, Coordinate position)
    {
        super(uuid, name, position);
    }

    protected abstract int getMinDensity();
    protected abstract int getMaxDensity();
    protected abstract int getMinQuantity();
    protected abstract int getMaxQuantity();

    public Demand createDemand(ProductType type, Processor processor)
    {
        // 质量按吨生成，按千克存储和计算
        int quantity = getRandQuantity() * 1000;
        Product product = new Product(type, quantity, getRandVolume(quantity));
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return new Demand(uuid, position, processor.getPosition(), product);
    }

    private int getRandQuantity()
    {
        return RANDOM.nextInt(getMinQuantity(), getMaxQuantity() + 1);
    }
    private int getRandVolume(int quantity)
    {
        int density = RANDOM.nextInt(getMinDensity(), getMaxDensity() + 1);
        return quantity / density;
    }
}
