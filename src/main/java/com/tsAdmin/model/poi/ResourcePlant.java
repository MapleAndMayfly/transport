package com.tsAdmin.model.poi;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.ProductType;

public class ResourcePlant extends Poi implements Dumper
{
    public ResourcePlant(String uuid, ProductType productType, Coordinate position, int maxStock)
    {
        super(uuid, productType, position, maxStock);
    }

    @Override
    public void update()
    {
        stock += (maxStock - stock) * stockAlterSpeed;
    }

    @Override
    public double getStock() { return stock; }
}
