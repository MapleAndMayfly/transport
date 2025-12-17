package com.tsAdmin.control.scheduler;

import java.util.ArrayList;
import java.util.List;

import com.tsAdmin.common.PathNode;
import com.tsAdmin.control.manager.CarManager;
import com.tsAdmin.control.manager.DemandManager;
import com.tsAdmin.model.Assignment;
import com.tsAdmin.model.Car;
import com.tsAdmin.model.Demand;

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
        for (Demand demand : DemandManager.demandList.values())
        {
            if (!demand.isAssigned()) demandsToAssign.add(demand);
        }

        for (Demand demand : demandsToAssign)
        {
            for (Car car : CarManager.carList.values())
            {
                // 只分配给无订单的空车
                if (car.getNodeList().isEmpty() && car.getMaxLoad() >= demand.getQuantity())
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
