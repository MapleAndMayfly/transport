package com.tsAdmin.model;

/** 产品 */
public class Product
{
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
}
