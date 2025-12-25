package com.tsAdmin.model.poi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.ProductType;

public class Processor extends Poi
{
    private static final Logger logger = LogManager.getLogger(Processor.class);

    private double processing = 0;

    public Processor(String uuid, ProductType productType, Coordinate position, double stock, int maxStock)
    {
        super(uuid, productType, position, stock, maxStock);
    }

    @Override
    public void update()
    {
        double completed = Math.min(processing, maxStock * stockAlterSpeed);

        processing -= completed;
        stock += completed;
        if (stock > maxStock)
        {
            logger.warn("Stock(value:{}) is larger than maxStock(value:{})", stock, maxStock);
        }
    }
}
