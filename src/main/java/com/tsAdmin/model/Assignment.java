package com.tsAdmin.model;

import java.util.ArrayList;
import java.util.List;

public class Assignment
{
    private Car car;
    private List<PathNode> nodeList = new ArrayList<>();

    public Assignment(Car car) { this.car = new Car(car); }

    public void addPathNode(PathNode pathNode) { nodeList.add(pathNode); }

    public Car getCar() { return car; }
    public List<PathNode> getNodeList() { return nodeList; }
    public double getCurrentLoad()
    {
        return nodeList.stream().mapToDouble(op -> op.isOrigin() ? op.getDemand().getQuantity() : -op.getDemand().getQuantity())
                                .sum();
    }
}
