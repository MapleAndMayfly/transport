package com.tsAdmin.control.scheduler;

import java.util.ArrayList;
import java.util.List;

import com.tsAdmin.common.PathNode;
import com.tsAdmin.model.Assignment;
import com.tsAdmin.model.Car;
import com.tsAdmin.model.CarList;
import com.tsAdmin.model.Demand;
import com.tsAdmin.model.DemandList;

/**先来先服务调度算法 */
public class FcfsScheduler extends Scheduler
{
    @Override
    public List<Assignment> schedule()
    {
        List<Demand> demandlist=new ArrayList<>();
        for(Demand demand:DemandList.demandList.values())
        {
            if (!demand.isFullyAssigned()) demandlist.add(new Demand(demand));
        }

        for(Demand demand : demandlist)
        {
            for(Car car : CarList.carList.values())
            {
                if(car.isDemandEmpty() && car.getMaxLoad()>=demand.getQuantity())
                {
                    car.getNodeList().add(new PathNode(DemandList.demandList.get(demand.getUUID()), true));
                    car.getNodeList().add(new PathNode(DemandList.demandList.get(demand.getUUID()), false));
                    demand.setProcessed();
                    break;
                }
            }
        }
        return null;
    }
}
