package com.tsAdmin.control.scheduler;

import java.util.ArrayList;
import java.util.List;

import com.tsAdmin.common.PathNode;
import com.tsAdmin.model.Assignment;
import com.tsAdmin.model.Demand;
import com.tsAdmin.model.DemandList;
import com.tsAdmin.model.car.Car;
import com.tsAdmin.model.car.CarList;

/**
 * 先来先服务算法调度器
 * <p>仅对新产生/未处理的订单进行分配，且仅将订单分给无订单的空闲车辆
 */
public class FcfsScheduler extends Scheduler
{
    @Override
    public List<Assignment> schedule()
    {
        List<Demand> demandsToAssign = new ArrayList<>();

        // 筛选出未处理过的新订单
        for (Demand demand : DemandList.demandList.values())
        {
            if (!demand.isAssigned()) demandsToAssign.add(demand);
        }

        for (Demand demand : demandsToAssign)
        {
            for (Car car : CarList.carList.values())
            {
                // 只分配给无订单的空车
                if (car.isDemandEmpty() && car.getMaxLoad() >= demand.getQuantity())
                {
                    car.getNodeList().add(new PathNode(demand, true));
                    car.getNodeList().add(new PathNode(demand, false));
                    demand.setAssigned();
                    break;
                }
            }
        }
        return null;
    }
}
