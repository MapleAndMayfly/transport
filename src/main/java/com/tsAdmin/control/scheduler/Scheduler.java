package com.tsAdmin.control.scheduler;

import java.util.ArrayList;
import java.util.List;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.common.PathNode;
import com.tsAdmin.control.manager.CarManager;
import com.tsAdmin.model.Assignment;
import com.tsAdmin.model.Car;
import com.tsAdmin.model.Demand;

/** 调度器抽象类，作为所有调度器父类 */
public abstract class Scheduler
{
    /**
     * 调度方法，根据车辆与订单进行调度
     * @return 调度结果
     */
    abstract public List<Assignment> schedule();

    protected void syncAssignmentsToCars(List<Assignment> assignments)
    {
        for (Assignment assignment : assignments)
        {
            Car car = assignment.getCar();
            CarManager.carList.get(car.getUUID()).setNodeList(assignment.getNodeList());
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
     * 计算车辆在当前状态下前往某地的代价
     * <p><b><i>注意：该函数会将传入的车辆坐标移动到目的地并根据目的地种类增减载重量</i></b>
     * @param maxLoad 车辆核载量
     * @param pathNode 车辆前往的目的地
     * @param load 当前车辆载重
     * @param totalTime 路途花费总时间，用于调度器计算总代价，其他情况应为{@code 0}
     * @param carPosition 当前车辆坐标
     * @return 代价
     */
    public static double cost(int maxLoad, PathNode pathNode, Integer load, Double totalTime, Coordinate carPosition)
    {
        // TODO: 完善代价计算，使其更加合理
        double distanceWeight = 0.5;
        double loadWeight = 0.2;
        double waitTimeWeight = 0.3;

        double distance = 0;
        // 空载代价，载重越高代价越小
        double loadCost = (1 - (double)load / maxLoad) * loadWeight;

        if(pathNode.isOrigin())
        {
            distance = Coordinate.distance(carPosition, pathNode.getDemand().getOrigin());
            carPosition.set(pathNode.getDemand().getOrigin());
            load += pathNode.getDemand().getQuantity();
        }
        else
        {
            distance = Coordinate.distance(carPosition, pathNode.getDemand().getDestination());
            carPosition.set(pathNode.getDemand().getDestination());
            load -= pathNode.getDemand().getQuantity();
        }
        double time = distance / 60;

        // 若当前载重大于核载，则将代价设为无穷大
        if (load > maxLoad) loadCost = Double.POSITIVE_INFINITY;

        // 距离代价，距离越远代价越大
        double distanceCost = distance * distanceWeight;
        // 接单时间代价，时间越长代价越大
        double timeCost = pathNode.isOrigin() ? (totalTime + time) * waitTimeWeight : 0;
        totalTime += time;

        return distanceCost + loadCost + timeCost;
    }

    double totalCost(Car car,List<PathNode> nodeList)
    {
        double totalCost = 0;
        Double totalTime = 0.0;
        Integer load = car.getLoad();

        Coordinate carPosition = new Coordinate(car.getPosition());
        for (int i = 0; i < nodeList.size(); i++)
        {
            double cost = cost(car.getMaxLoad(), nodeList.get(i), load, totalTime, carPosition);
            totalCost += cost;
        }

        return totalCost;
    }
}
