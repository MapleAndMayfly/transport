package com.tsAdmin.control;

import com.tsAdmin.model.DemandList;
import com.tsAdmin.common.ConfigLoader;
import com.tsAdmin.control.scheduler.*;

public class DataUpdater implements Runnable
{
    Scheduler scheduler = new SimulatedAnnealingScheduler();

    @Override
    public void run()
    {
        // ms
        final long UPDATE_INTERVAL = ConfigLoader.getLong("DataUpdater.update_interval", (long)5e3);
        final int DEMAND_PER_CYCLE = ConfigLoader.getInt("DataUpdater.demand_per_cycle", 0);

        // 线程运行旗标
        boolean isRunning = true;
        long lastUpdate = System.currentTimeMillis();
        long currentTime;

        while (isRunning)
        {
            currentTime = System.currentTimeMillis();

            if (currentTime - lastUpdate > UPDATE_INTERVAL)
            {
                lastUpdate = currentTime;

                // 每周期调用以下内容
                DemandList.generateDemand(DEMAND_PER_CYCLE);
                scheduler.schedule();
            }
        }
    }
}
