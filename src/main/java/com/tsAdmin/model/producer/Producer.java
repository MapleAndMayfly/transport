package com.tsAdmin.model.producer;

import java.util.UUID;
import java.util.Random;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.Demand;
import com.tsAdmin.model.Manufacturer;
import com.tsAdmin.model.Product;
import com.tsAdmin.model.processor.Processor;

/** 生产厂(抽象) */
public abstract class Producer extends Manufacturer
{
    protected static final Random RANDOM = new Random();

    protected Producer(String uuid, String name, Coordinate position)
    {
        super(uuid, name, position);
    }

    protected abstract int getMinQuantity();
    protected abstract int getMaxQuantity();

    public Demand createDemand(Product product, Processor processor)
    {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        product.setQuantity(getRandQuantity());

        Demand demand = new Demand(uuid, position, processor.getPosition(), product.getType());
        demand.setQuantity(product.getQuantity());

        return demand;
    }

    public int getRandQuantity()
    {
        return RANDOM.nextInt(getMinQuantity(), getMaxQuantity() + 1);
    }
}
