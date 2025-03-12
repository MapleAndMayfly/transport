package com.tsAdmin.model;

import java.util.Random;

/** 产品 */
public class Product
{
    private static final Random random = new Random();

    /** 货物类型 */
    public static enum ProductType
    {
        WOOD,           // 木材
        STEEL,          // 钢材
        PHARMACEUTICAL  // 药材
    }

    private ProductType type;
    private int quantity;

    public Product(ProductType type)
    {
        this.type = type;
        this.quantity = 0;
    }

    // Setter
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Getter
    public ProductType getType() { return type; }
    public int getQuantity() { return quantity; }

    public static ProductType getRandType()
    {
        ProductType[] types = ProductType.values();
        int idx = random.nextInt(types.length);
        return types[idx];
    }
}
