package com.tsAdmin.model.poi;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.Demand;
import com.tsAdmin.model.ProductType;

public abstract class Poi
{
    protected String uuid;
    protected ProductType productType;
    protected Coordinate position;
    protected double stock;
    protected int maxStock;

    protected String upstreamPoiUuid[];
    /** 运往本 POI 的订单，若无则为 {@code null} */
    protected Demand demand = null;

    protected static final double lowBound = 0.2;
    protected static final double upBound = 0.8;
    protected static double stockAlterSpeed;
    protected static double processingLoss;

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

    protected void onStockChange()
    {
        if (demand != null && stock < maxStock * upBound)
        {

        }
    }

    public String getUUID() { return uuid; }
    public ProductType getType() { return productType; }
    public Coordinate getPosition() { return position; }
    public double getStock() { return stock; }

    public static void setStockAlterSpeed(int speed) { stockAlterSpeed = speed / 100.0; }
    public static void setProcessingLoss(int loss) { processingLoss = loss / 100.0; }

    protected boolean isAvailable(int need) { return stock >= need; }
}
