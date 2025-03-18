package com.tsAdmin.control;

import com.tsAdmin.model.DemandList;

public class DataUpdater implements Runnable
{
    @Override
    public void run()
    {
        final double updateInterval = 5e3;              // 每5s更新一次
        long lastUpdate = System.currentTimeMillis();
        long currentTime;

        boolean isRunning = true;                       // 线程运行旗标

        while (isRunning)
        {
            currentTime = System.currentTimeMillis();

            if (currentTime - lastUpdate > updateInterval)
            {
                lastUpdate = currentTime;

                // 每周期调用以下内容
                DemandList.generateDemand(2);
            }
        }
    }
}
