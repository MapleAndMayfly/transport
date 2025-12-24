package com.tsAdmin.model.poi;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.ProductType;

public class Producer extends Poi
{
    private static int produceSpeed;

    public Producer(String uuid, ProductType productType, Coordinate position, double stock, int maxStock)
    {
        super(uuid, productType, position, stock, maxStock);
    }

    @Override
    public void update()
    {
        stock += produceSpeed * maxStock / 100.0;
        if (stock > maxStock) stock = maxStock;
    }

    public static void setProduceSpeed(int speed) { produceSpeed = speed; }
}
