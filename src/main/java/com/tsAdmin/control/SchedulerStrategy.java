package com.tsAdmin.control;

import java.util.List;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.model.Assignment;
import com.tsAdmin.model.Car;
import com.tsAdmin.model.Demand;

public interface SchedulerStrategy
{
    List<Assignment> schedule(List<Car> cars, List<Demand> demands);

    /** 统一的 cost 计算方法 */
    default double cost(Car car, Demand demand)
    {
        double alpha = 0.5; // 距离权重
        double beta = 0.2;  // 费用权重
        double gamma = 0.2; // 等待时间权重
        double delta = 0.1; // 超时时间权重

        double distanceToFactory = Coordinate.distance(car.getPosition(), demand.getOrigin());
        double distanceToDestination = Coordinate.distance(demand.getOrigin(), demand.getDestination());
        double totalDistance = distanceToFactory + distanceToDestination;

        double vehicleCost = car.getMaxLoad() * demand.getQuantity();
        double waitingTime = totalDistance / 60;
        double transportTime = totalDistance / 60;
        double overtime = Math.max(0, transportTime - 10);

        return alpha * totalDistance + beta * vehicleCost + gamma * waitingTime + delta * overtime;
    }
}
