package com.tsAdmin.control;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfinal.core.JFinal;

import com.tsAdmin.common.ConfigLoader;
import com.tsAdmin.control.manager.CarManager;
import com.tsAdmin.control.manager.DemandManager;

public class Main
{
    public static DataUpdater updater = new DataUpdater();
    private static final Logger logger = LogManager.getLogger("App");

    public static void main(String[] args)
    {
        int port = ConfigLoader.getInt("Main.port", 8080);
        int updateInterval = ConfigLoader.getInt("Main.update_interval", 5);

        try
        {
            // 打开浏览器
            java.awt.Desktop.getDesktop().browse(new java.net.URI("http://localhost:" + port));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        logger.info("Server starts at http://localhost:{}", port);
        JFinal.start("src/main/webapp", port, "/", updateInterval);
    }

    public static void start()
    {
        CarManager.init();
        DemandManager.init();
        // 将 DataUpdater 作为独立线程运行，避免阻塞主线程
        Thread updaterThread = new Thread(updater);
        // 设置为守护线程，主程序结束时自动结束
        updaterThread.setDaemon(true);
        updaterThread.start();

        logger.info("Simulation started successfully, preset uuid: {}", ConfigLoader.getConfigUUID());
    }
}