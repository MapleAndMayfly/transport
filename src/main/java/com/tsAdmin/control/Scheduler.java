package com.tsAdmin.control;

import java.util.List;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.Assignment;
import com.tsAdmin.model.Car;
import com.tsAdmin.model.Demand;
import com.tsAdmin.model.PathNode;

public interface Scheduler
{
    List<Assignment> schedule(List<Car> cars, List<Demand> demands);

    /**
     * 计算车辆在当前状态下执行某需求的成本
     * @param car 车辆对象
     * @param pathNode 带装载状态的需求
     * @param LoadandTimenow 当前车辆载重和时间数组
     * @param carPosition 当前车辆坐标
     * @return 成本
     */
    default double cost(Car car, PathNode pathNode, Double remainingLoad, Double totalTime, Coordinate carPosition)
    {
        double distanceWeight = 0.5;    // 距离权重
        double loadWeight = 0.2;        // 载重权重
        double waitTimeWeight = 0.2;    // 等待时间权重
        // double timeoutWeight = 1;    // 超时时间权重

        double distance = 0;
        double time = distance / 60;
        double loadCost = (remainingLoad / car.getMaxLoad()) * loadWeight;

        double timeCost = 0;
        if(pathNode.isOrigin())
        {
            distance = Coordinate.distance(carPosition, pathNode.getDemand().getOrigin());
            carPosition = new Coordinate(pathNode.getDemand().getOrigin());
            remainingLoad -= pathNode.getDemand().getQuantity();
        }
        else
        {
            distance = Coordinate.distance(carPosition, pathNode.getDemand().getDestination());
            carPosition = new Coordinate(pathNode.getDemand().getDestination());
            remainingLoad += pathNode.getDemand().getQuantity();
        }

        double distanceCost = distance * distanceWeight;
        timeCost += pathNode.isOrigin() ? (totalTime + time) * waitTimeWeight : 0;
        totalTime += time;
        return distanceCost + loadCost + timeCost;
    }

    default double totalCost(Car car,List<PathNode> nodeList)
    {
        double totalCost = 0;
        Double totalTime = 0.0;
        Double remainingLoad = car.getRemainingLoad();

        Coordinate carPosition = new Coordinate(car.getPosition());
        for (int i = 0; i < nodeList.size(); i++)
        {
            totalCost += cost(car, nodeList.get(i), remainingLoad, totalTime, carPosition);
        }

        return totalCost;
    }
}
