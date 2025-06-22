package com.tsAdmin.control;

import java.util.ArrayList;
import java.util.List;

import com.tsAdmin.model.Assignment;
import com.tsAdmin.model.Car;
import com.tsAdmin.model.Demand;

/** 贪心算法 */
public class GreedyScheduler implements SchedulerStrategy
{
    @Override
    public List<Assignment> schedule(List<Car> cars, List<Demand> demands)
    {
        List<Assignment> assignments = new ArrayList<>();
        List<Demand> remainingDemands = new ArrayList<>(demands);

        // 初始化车辆载重容积
        for (Car car : cars)
        {
            car.setLoad(car.getMaxLoad());
            car.setVolume(car.getMaxVolume());
        }

        while (!remainingDemands.isEmpty())
        {
            Demand currentDemand = remainingDemands.get(0);
            Car bestCar = null;
            double minCost = Double.MAX_VALUE;

            for (Car car : cars)
            {
                if (car.getLoad() >= currentDemand.getQuantity() &&
                    car.getVolume() >= currentDemand.getVolume())
                {

                    double cost = cost(car, currentDemand); // 直接调用接口中的 cost 方法
                    if (cost < minCost)
                    {
                        minCost = cost;
                        bestCar = car;
                    }
                }
            }

            if (bestCar != null) {
                Assignment assignment = getAssignmentForCar(assignments, bestCar);
                assignment.addDemand(currentDemand);

                bestCar.setLoad(bestCar.getLoad() - currentDemand.getQuantity());
                bestCar.setVolume(bestCar.getVolume() - currentDemand.getVolume());
                bestCar.setPosition(currentDemand.getDestination());

                remainingDemands.remove(currentDemand);
            } else {
                System.out.println("无法完成需求: " + currentDemand.getId());
                remainingDemands.remove(currentDemand);
            }
        }

        return assignments;
    }

    private Assignment getAssignmentForCar(List<Assignment> assignments, Car car) {
        for (Assignment assignment : assignments) {
            if (assignment.getCar().getId() == car.getId()) {
                return assignment;
            }
        }
        Assignment newAssignment = new Assignment(car);
        assignments.add(newAssignment);
        return newAssignment;
    }
}
