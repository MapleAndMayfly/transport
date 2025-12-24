package com.tsAdmin.model.poi;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.ProductType;

public class Producer extends Poi
{
    public Producer(String uuid, ProductType productType, Coordinate position, double stock, int maxStock)
    {
        super(uuid, productType, position, stock, maxStock);
    }

    @Override
    public void update()
    {
        stock += stockAlterSpeed * maxStock;
        if (stock > maxStock) stock = maxStock;

        tryMarkAvailable();
    }
}
