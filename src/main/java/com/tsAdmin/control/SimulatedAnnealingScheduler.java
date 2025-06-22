package com.tsAdmin.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.tsAdmin.model.Assignment;
import com.tsAdmin.model.Car;
import com.tsAdmin.model.Demand;

/** 退火算法 */
public class SimulatedAnnealingScheduler implements SchedulerStrategy
{
    @Override
    public List<Assignment> schedule(List<Car> cars, List<Demand> demands)
    {
        double temperature = 1000.0;
        final double minTemperature = 1.0;
        final double coolingRate = 0.95;
        final int maxIterations = 1000;

        List<Assignment> currentAssignments = new GreedyScheduler().schedule(cars, demands);
        double currentCost = calculateTotalCost(currentAssignments);

        Random random = new Random();
        for (int i = 0; i < maxIterations && temperature > minTemperature; i++)
        {
            List<Assignment> newAssignments = generateNeighbor(currentAssignments);
            double newCost = calculateTotalCost(newAssignments);

            double deltaCost = newCost - currentCost;
            if (deltaCost < 0 || Math.exp(-deltaCost / temperature) > random.nextDouble())
            {
                currentAssignments = newAssignments;
                currentCost = newCost;
            }

            temperature *= coolingRate;

            // 增加扰动：如果长期没有改进，则轻微增加温度
            if (i % (maxIterations / 10) == 0)
            {
                temperature *= 1.05;
            }
        }

        return currentAssignments;
    }

    private double calculateTotalCost(List<Assignment> assignments)
    {
        double totalCost = 0;
        for (Assignment assignment : assignments)
        {
            for (Demand demand : assignment.getDemands())
            {
                totalCost += cost(assignment.getCar(), demand);
            }
        }
        return totalCost;
    }

    private List<Assignment> generateNeighbor(List<Assignment> assignments)
    {
        List<Assignment> newAssignments = deepCopyAssignments(assignments);
        Random random = new Random();

        if (random.nextBoolean())
        {
            // **50% 概率进行 "交换"**
            int index1 = random.nextInt(newAssignments.size());
            int index2 = random.nextInt(newAssignments.size());
            if (index1 == index2) return newAssignments;

            Assignment assignment1 = newAssignments.get(index1);
            Assignment assignment2 = newAssignments.get(index2);

            if (!assignment1.getDemands().isEmpty() && !assignment2.getDemands().isEmpty())
            {
                // 随机选择一个 Demand 进行交换
                int demandIndex1 = random.nextInt(assignment1.getDemands().size());
                int demandIndex2 = random.nextInt(assignment2.getDemands().size());

                Demand demand1 = assignment1.getDemands().get(demandIndex1);
                Demand demand2 = assignment2.getDemands().get(demandIndex2);

                // 确保交换不会导致车辆超载
                if (canSwap(assignment1, demand1, demand2) && canSwap(assignment2, demand2, demand1))
                {
                    // 交换 Demand
                    assignment1.getDemands().set(demandIndex1, demand2);
                    assignment2.getDemands().set(demandIndex2, demand1);
                }
            }
        }
        else
        {
            // **50% 概率进行 "单向移动"**
            int fromIndex = random.nextInt(newAssignments.size());
            int toIndex = random.nextInt(newAssignments.size());
            if (fromIndex == toIndex) return newAssignments;

            Assignment fromAssignment = newAssignments.get(fromIndex);
            Assignment toAssignment = newAssignments.get(toIndex);

            if (!fromAssignment.getDemands().isEmpty())
            {
                // 选择要移动的 Demand
                int demandIndex = random.nextInt(fromAssignment.getDemands().size());
                Demand demandToMove = fromAssignment.getDemands().get(demandIndex);

                // 目标车辆是否能接收？
                if (canAddDemand(toAssignment, demandToMove))
                {
                    fromAssignment.getDemands().remove(demandToMove);
                    toAssignment.addDemand(demandToMove);
                }
            }
        }

        return newAssignments;
    }

    private boolean canSwap(Assignment assignment, Demand removeDemand, Demand addDemand)
    {
        Car car = assignment.getCar();
        double newLoad = car.getLoad() + removeDemand.getQuantity() - addDemand.getQuantity();
        double newVolume = car.getVolume() + removeDemand.getVolume() - addDemand.getVolume();
        return newLoad >= 0 && newVolume >= 0;
    }

    private boolean canAddDemand(Assignment assignment, Demand demand)
    {
        Car car = assignment.getCar();
        return car.getLoad() >= demand.getQuantity() && car.getVolume() >= demand.getVolume();
    }

    private List<Assignment> deepCopyAssignments(List<Assignment> assignments)
    {
        List<Assignment> newAssignments = new ArrayList<>();
        for (Assignment assignment : assignments)
        {
            Assignment newAssignment = new Assignment(new Car(assignment.getCar())); // 深拷贝车辆

            // 深拷贝每个 Demand
            for (Demand demand : assignment.getDemands())
            {
                newAssignment.addDemand(new Demand(demand)); // ✅ 逐个深拷贝
            }

            newAssignments.add(newAssignment);
        }
        return newAssignments;
    }
}
