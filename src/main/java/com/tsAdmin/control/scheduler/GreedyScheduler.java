package com.tsAdmin.control.scheduler;

import com.tsAdmin.common.PathNode;
import com.tsAdmin.model.Assignment;
import com.tsAdmin.model.car.Car;
import com.tsAdmin.model.car.CarList;
import com.tsAdmin.model.demand.Demand;
import com.tsAdmin.model.demand.DemandList;

import java.util.ArrayList;
import java.util.List;

/**
 * 贪心算法调度器
 * <p>仅对新产生/未分配的订单进行分配，将订单分配给有能力且运送所需成本最小的车辆
 */
public class GreedyScheduler extends Scheduler
{
    @Override
    public List<Assignment> schedule()
    {
        List<Assignment> assignments = new ArrayList<>();
        List<Demand> demandsToAssign = new ArrayList<>();

        // 筛选出未处理的订单
        for (Demand demand : DemandList.demandList.values())
        {
            if (!demand.isAssigned()) demandsToAssign.add(demand);
        }
        if (demandsToAssign.isEmpty()) return assignments;

        // 有订单未处理，根据调度算法进行调度
        List<Car> carsCopy = new ArrayList<>();
        for (Car car : CarList.carList.values()) carsCopy.add(new Car(car));

        for (Demand demand : demandsToAssign)
        {
            Car targetCar = null;
            double minCost = Double.POSITIVE_INFINITY;

            // 为当前demand创建起点和终点PathNode
            // TODO: 修改PathNode生成逻辑
            PathNode startNode = new PathNode(demand, true);
            PathNode endNode = new PathNode(demand, false);

            // 遍历所有车辆，选择最佳车辆来处理当前demand
            for (Car car : carsCopy)
            {
                if (!canAssign(car, demand)) continue;

                // TODO:这里有点抽象，有时间可以改改
                car.addPathNode(startNode);
                car.addPathNode(endNode);

                double cost = totalCost(car, car.getNodeList());

                // 移除临时添加的节点，恢复原序列
                car.getNodeList().remove(car.getNodeList().size() - 1); // 移除终点
                car.getNodeList().remove(car.getNodeList().size() - 1); // 移除起点

                if (cost < minCost)
                {
                    minCost = cost;
                    targetCar = car;
                }
            }

            if (targetCar != null)
            {
                Assignment assignment = getAssignmentForCar(assignments, targetCar);

                assignment.addPathNode(startNode);  // 先添加起点
                assignment.addPathNode(endNode);    // 再添加终点，保持顺序

                demand.setAssigned();
            }
        }

        syncAssignmentsToCars(assignments);

        return assignments;
    }

    /**
     * 判断订单是否可以分配给特定车辆
     * @param car 订单分配的车辆对象
     * @param demand 将要分配的订单
     * @return {@code true}如果该订单可以分配给该车辆
     */
    private boolean canAssign(Car car, Demand demand)
    {
        int load = car.getLoad();
        int volume = car.getVolume();

        // 模拟车辆执行现有分配，获取预期剩余载重
        for (PathNode existingNode : car.getNodeList())
        {
            if (existingNode.isOrigin())
            {
                load += existingNode.getDemand().getQuantity();
                volume += existingNode.getDemand().getVolume();
            }
            else
            {
                load -= existingNode.getDemand().getQuantity();
                volume -= existingNode.getDemand().getVolume();
            }
        }

        load += demand.getQuantity();
        volume += demand.getVolume();

        return load <= car.getMaxLoad() && volume <= car.getMaxVolume();
    }

    // 获取该车辆所对应的Assignment对象，如果没有则创建一个新的
    private Assignment getAssignmentForCar(List<Assignment> assignments, Car car)
    {
        for (Assignment assignment : assignments)
        {
            if (assignment.getCar().getUUID().equals(car.getUUID()))
            {
                return assignment;
            }
        }
        Assignment newAssignment = new Assignment(car);
        assignments.add(newAssignment);
        return newAssignment;
    }
}
