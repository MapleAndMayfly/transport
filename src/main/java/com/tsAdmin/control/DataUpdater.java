package com.tsAdmin.control;

import com.tsAdmin.common.ConfigLoader;
import com.tsAdmin.control.manager.DemandManager;
import com.tsAdmin.control.scheduler.*;

public class DataUpdater implements Runnable
{
    Scheduler scheduler;

    @Override
    public void run()
    {
        // s => ms
        final long UPDATE_INTERVAL = ConfigLoader.getInt("Main.update_interval", 5) * 1000;
        final int DEMAND_PER_CYCLE = ConfigLoader.getInt("DataUpdater.demand_per_cycle");

        // 根据配置文件设置相应的调度器
        switch (ConfigLoader.getString("DataUpdater.applied_scheduler"))
        {
            case "FCFS":
                scheduler = new FcfsScheduler();
                break;

            case "Greedy":
                scheduler = new GreedyScheduler();
                break;

            case "SA":
            default:
                scheduler = new SimulatedAnnealingScheduler();
                break;
        }

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
                DemandManager.generateDemand(DEMAND_PER_CYCLE);
                scheduler.schedule();
            }
        }
    }
}
