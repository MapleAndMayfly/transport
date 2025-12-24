package com.tsAdmin.model.poi;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.ProductType;

public class Processor extends Poi
{
    public Processor(String uuid, ProductType productType, Coordinate position, double stock, int maxStock)
    {
        super(uuid, productType, position, stock, maxStock);
    }

    @Override
    public void update()
    {
        
    }
}
