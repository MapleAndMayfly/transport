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

    private final ProductType type;
    private int quantity;
    private int volume;

    public Product(ProductType type, int quantity, int volume)
    {
        this.type = type;
        this.quantity = quantity;
        this.volume = volume;
    }

    // Setter
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setVolume(int volume) { this.volume = volume; }

    // Getter
    public ProductType getType() { return type; }
    public int getQuantity() { return quantity; }
    public int getVolume() { return volume; }

    public static ProductType getRandType()
    {
        ProductType[] types = ProductType.values();
        int idx = random.nextInt(types.length);
        return types[idx];
    }
}
