package com.tsAdmin.control;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfinal.core.JFinal;

import com.tsAdmin.common.ConfigLoader;
import com.tsAdmin.control.manager.CarManager;
import com.tsAdmin.control.manager.DemandManager;
import com.tsAdmin.model.poi.Producer;

public class Main
{
    public static Random RANDOM;
    private static DataUpdater updater = new DataUpdater();
    private static final Logger logger = LogManager.getLogger("App");

    public static void main(String[] args)
    {
        try
        {
            // 打开浏览器
            java.awt.Desktop.getDesktop().browse(new java.net.URI("http://localhost:8080"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        logger.info("Server starts at http://localhost:8080");
        JFinal.start("src/main/webapp", 8080, "/", 5);
    }

    public static void start()
    {
        RANDOM = new Random(ConfigLoader.getInt("Main.random_seed"));
        Producer.setStockAlterSpeed(ConfigLoader.getInt("Poi.stock_alter_speed"));

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