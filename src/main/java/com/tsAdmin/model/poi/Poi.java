package com.tsAdmin.model.poi;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.ProductType;

public abstract class Poi
{
    protected String uuid;
    protected ProductType productType;
    protected Coordinate position;
    protected int stock, maxStock;

    public Poi(String uuid, ProductType productType, Coordinate position, int maxStock)
    {
        this.uuid = uuid;
        this.productType = productType;
        this.position = position;
        this.maxStock = maxStock;
        stock = 0;
    }

    public String getUUID() { return uuid; }
    public ProductType getType() { return productType; }
    public Coordinate getPosition() { return position; }
    public int getStock() { return stock; }

    /** 更新当前兴趣点 */
    public abstract void update();
}
