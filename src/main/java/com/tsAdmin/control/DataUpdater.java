package com.tsAdmin.control;

import com.tsAdmin.common.ConfigLoader;
import com.tsAdmin.control.manager.PoiManager;
import com.tsAdmin.control.scheduler.*;

public class DataUpdater implements Runnable
{
    private volatile boolean running = true;
    private static int UPDATE_INTERVAL;

    Scheduler scheduler;

    public void stop() { running = false; }

    @Override
    public void run()
    {
        // s => ms
        UPDATE_INTERVAL = ConfigLoader.getInt("Main.update_interval", 5) * 1000;

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

        while (running)
        {
            long start = System.currentTimeMillis();

            // 这里开始数据更新逻辑

            PoiManager.update();
            scheduler.schedule();

            // 这里结束数据更新逻辑

            long cost = System.currentTimeMillis() - start;
            long sleep = UPDATE_INTERVAL - cost;
            if (sleep > 0)
            {
                try
                {
                    Thread.sleep(sleep);
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
