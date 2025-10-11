package com.tsAdmin.common;

import com.tsAdmin.model.Demand;

/** 车辆路径点 */
public class PathNode
{
    private Demand demand;
    private boolean isOrigin;

    public PathNode(Demand demand, boolean isOrigin)
    {
        this.demand = demand;
        this.isOrigin = isOrigin;
    }

    public Demand getDemand() { return demand; }
    public boolean isOrigin() { return isOrigin; }
}
