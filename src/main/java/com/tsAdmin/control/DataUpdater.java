package com.tsAdmin.control;

import com.tsAdmin.common.ConfigLoader;
import com.tsAdmin.control.manager.PoiManager;
import com.tsAdmin.control.scheduler.*;

public class DataUpdater implements Runnable
{
    private static MOSAScheduler mosaScheduler;

    public DataUpdater() {
        mosaScheduler = new MOSAScheduler(); // 直接创建
    }

    @Override
    public void run() {
        final long UPDATE_INTERVAL = ConfigLoader.getInt("Main.update_interval", 5) * 1000;
        boolean isRunning = true;
        long lastUpdate = System.currentTimeMillis();

        while (isRunning) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdate > UPDATE_INTERVAL) {
                lastUpdate = currentTime;
                PoiManager.update();
                mosaScheduler.schedule(); // 直接调用静态字段
            }
        }
    }

    public static MOSAScheduler getMosaScheduler() {
        return mosaScheduler;
    }
}
