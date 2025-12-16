package com.tsAdmin.model.producer;

import java.util.UUID;
import java.util.Random;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.Product;
import com.tsAdmin.model.ProductType;
import com.tsAdmin.model.demand.Demand;
import com.tsAdmin.model.processor.Processor;

/** 生产厂类 */
public class Producer
{
    protected static final Random RANDOM = new Random();

    private String uuid;
    private String name;
    private ProductType type;
    private Coordinate position;

    protected Producer(String uuid, String name, ProductType type, Coordinate position)
    {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.position = position;
    }

    public Demand createDemand(ProductType type, Processor processor)
    {
        // FIXME: 已无法运行，需要修改
        // 质量按吨生成，按千克存储和计算
        int quantity = 200;
        Product product = new Product(type, quantity, 0);
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return new Demand(uuid, position, processor.getPosition(), product);
    }
}
