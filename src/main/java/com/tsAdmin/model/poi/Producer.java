package com.tsAdmin.model.poi;

import java.util.UUID;
import java.util.Random;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.Demand;
import com.tsAdmin.model.Product;
import com.tsAdmin.model.ProductType;

/** 生产厂类 */
public class Producer extends Poi
{
    protected static final Random RANDOM = new Random();

    public Producer(String uuid, ProductType productType, Coordinate position)
    {
        super(uuid, productType, position);
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
