package com.tsAdmin.model.poi;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.ProductType;

public class Producer extends Poi
{
    public Producer(String uuid, ProductType productType, Coordinate position, int maxStock)
    {
        super(uuid, productType, position, maxStock);
    }

    @Override
    public void update()
    {
        
    }
}
