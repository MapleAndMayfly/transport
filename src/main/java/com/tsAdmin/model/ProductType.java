package com.tsAdmin.model;

import java.util.HashMap;
import java.util.Map;

public enum ProductType
{
    /** 木材 */ WOOD(0),
    /** 钢材 */ STEEL(1),
    /** 药材 */ PHARMA(2);

    private static final Map<String, Object[]> params;
    static {
        params = new HashMap<>();
        params.put("name", new String[] {"木材", "钢材", "药材"});
        params.put("minQuantity", new Integer[] {10, 20, 5});        // t
        params.put("maxQuantity", new Integer[] {40, 50, 20});       // t
        params.put("minDensity", new Integer[] {300, 8000, 700});    // kg/m^3
        params.put("maxDensity", new Integer[] {1200, 7000, 1000});  // kg/m^3
    }

    private final int index;
    ProductType(int index) { this.index = index; }

    public String getName() { return (String)params.get("name")[index]; }
    public int getMinQuantity() { return (int)params.get("minQuantity")[index]; }
    public int getMaxQuantity() { return (int)params.get("maxQuantity")[index]; }
    public int getMinDensity() { return (int)params.get("minDensity")[index]; }
    public int getMaxDensity() { return (int)params.get("maxDensity")[index]; }
}
