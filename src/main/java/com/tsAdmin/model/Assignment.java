package com.tsAdmin.model;

import java.util.LinkedList;
import java.util.List;

public class Assignment
{
    private List<Demand> demands = new LinkedList<>();

    public void addDemand(Demand demand)
    {
        demands.add(demand);
    }

    public Demand getNextDemand()
    {
        // 返回队首元素并将其移动到队尾
        Demand ret = demands.removeFirst();
        demands.addLast(ret);
        return ret;
    }

    public Demand getLastDemand()
    {
        Demand ret = demands.removeLast();
        demands.addFirst(ret);
        return ret;
    }
}
