package com.tsAdmin.model.poi;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.ProductType;

public class Market extends Purchaser
{
    public Market(String uuid, String name, ProductType productType, Coordinate position, int maxStock)
    {
        super(uuid, name, productType, position, maxStock);
    }

    @Override
    public void update()
    {
        stock -= stock * stockAlterSpeed;
        tryGenerateDemand(stock);
    }
}
