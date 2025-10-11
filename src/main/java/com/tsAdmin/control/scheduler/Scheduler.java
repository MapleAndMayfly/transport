package com.tsAdmin.control.scheduler;

import java.util.ArrayList;
import java.util.List;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.common.PathNode;
import com.tsAdmin.model.Assignment;
import com.tsAdmin.model.Car;
import com.tsAdmin.model.CarList;
import com.tsAdmin.model.Demand;

/**
 * 调度器抽象类，作为所有调度器父类
 */
public abstract class Scheduler
{
    abstract public List<Assignment> schedule();

    protected void syncAssignmentsToCars(List<Assignment> assignments)
    {
        for (Assignment assignment : assignments)
        {
            Car car = assignment.getCar();
            CarList.carList.get(car.getUUID()).setNodeList(assignment.getNodeList());
        }
    }

    /**
     * 将原始Demand列表转换为PathNode节点对 (每个Demand生成两个节点)
     * @param demands 需求列表
     * @return 节点对列表
     */
    protected List<PathNode> createPathNodes(List<Demand> demands)
    {
        List<PathNode> pairs = new ArrayList<>();
        for (Demand demand : demands)
        {
            pairs.add(new PathNode(demand, true));
            pairs.add(new PathNode(demand, false));
        }
        return pairs;
    }

    /**
     * 计算车辆在当前状态下执行某需求的成本
     * @param car 车辆对象
     * @param pathNode 带装载状态的需求
     * @param LoadandTimenow 当前车辆载重和时间数组
     * @param carPosition 当前车辆坐标
     * @return 成本
     */
    double cost(Car car, PathNode pathNode, Double remainingLoad, Double totalTime, Coordinate carPosition)
    {
        double distanceWeight = 0.5;    // 距离权重
        double loadWeight = 0.2;        // 载重权重
        double waitTimeWeight = 0.3;    // 等待时间权重
        // double timeoutWeight = 1;    // 超时时间权重

        double distance = 0;
        double time = distance / 60;
        double loadCost = (remainingLoad / car.getMaxLoad()) * loadWeight;

        double timeCost = 0;
        if(pathNode.isOrigin())
        {
            distance = Coordinate.distance(carPosition, pathNode.getDemand().getOrigin());
            carPosition.set(pathNode.getDemand().getOrigin());
            remainingLoad = Math.max(0, remainingLoad-pathNode.getDemand().getQuantity());
        }
        else
        {
            distance = Coordinate.distance(carPosition, pathNode.getDemand().getDestination());
            carPosition.set(pathNode.getDemand().getDestination());
            remainingLoad = Math.min(car.getMaxLoad(), remainingLoad+pathNode.getDemand().getQuantity());
        }

        double distanceCost = distance * distanceWeight;
        timeCost += pathNode.isOrigin() ? (totalTime + time) * waitTimeWeight : 0;
        totalTime += time;
        return distanceCost + loadCost + timeCost;
    }

    double totalCost(Car car,List<PathNode> nodeList)
    {
        double totalCost = 0;
        Double totalTime = 0.0;
        Double remainingLoad = car.getRemainingLoad();

        Coordinate carPosition = new Coordinate(car.getPosition());
        for (int i = 0; i < nodeList.size(); i++)
        {
            double cost = cost(car, nodeList.get(i), remainingLoad, totalTime, carPosition);
            totalCost += cost;
            // 记录本次任务cost（最后一次为最新任务）
            car.getCarStat().setCost(cost);
        }

        // 记录累计totalCost
        car.getCarStat().setTotalCost(totalCost);

        return totalCost;
    }
}
