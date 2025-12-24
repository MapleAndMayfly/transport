package com.tsAdmin.model.poi;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.control.manager.PoiManager;
import com.tsAdmin.model.ProductType;

public abstract class Poi
{
    protected String uuid;
    protected ProductType productType;
    protected Coordinate position;
    protected double stock;
    protected int maxStock;
    /** 是否在可用列表中 */
    protected boolean inQueque;
    /**是否存在运往本 POI 的订单  */
    protected boolean hasDemand;

    protected static final double lowBound = 0.2;
    protected static final double upBound = 0.8;
    protected static double stockAlterSpeed;

    public Poi(String uuid, ProductType productType, Coordinate position, double stock, int maxStock)
    {
        this.uuid = uuid;
        this.productType = productType;
        this.position = position;
        this.stock = stock;
        this.maxStock = maxStock;
    }

    /** 更新当前兴趣点 */
    public abstract void update();

    /** 进行判断并在符合条件时将自己加入可用列表 */
    protected void tryMarkAvailable()
    {
        if (!inQueque && stock > maxStock * lowBound)
        {
            PoiManager.markAvailable(this);
        }
    }

    /**
     * 进行判断并在符合条件时生成订单
     */
    protected void tryGenerateDemand()
    {
        // TODO
    }

    public String getUUID() { return uuid; }
    public ProductType getType() { return productType; }
    public Coordinate getPosition() { return position; }
    public double getStock() { return stock; }

    public static void setStockAlterSpeed(int speed) { stockAlterSpeed = speed / 100.0; }

    public void setInQueque(boolean inQueque) { this.inQueque = inQueque; }
    public void setHasDemand(boolean hasDemand) { this.hasDemand = hasDemand; }
}
