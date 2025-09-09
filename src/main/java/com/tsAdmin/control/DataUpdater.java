package com.tsAdmin.control;

import com.tsAdmin.model.DemandList;

public class DataUpdater implements Runnable
{
    Scheduler scheduler = new SimulatedAnnealingScheduler();

    @Override
    public void run()
    {
        final double UPDATE_INTERVAL = 5e3;             // 每5s一个周期
        final int GENERATE_NUMBER = 50;                  // 每周期生成2个

        boolean isRunning = true;                       // 线程运行旗标
        long lastUpdate = System.currentTimeMillis();
        long currentTime;

        while (isRunning)
        {
            currentTime = System.currentTimeMillis();

            if (currentTime - lastUpdate > UPDATE_INTERVAL)
            {
                lastUpdate = currentTime;

                // 每周期调用以下内容
                //DemandList.generateDemand(GENERATE_NUMBER);
                scheduler.schedule();
            }
        }
    }
}
