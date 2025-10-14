package com.tsAdmin.control;

import com.jfinal.core.JFinal;
import com.tsAdmin.common.ConfigLoader;
import com.tsAdmin.model.DemandList;
import com.tsAdmin.model.car.CarList;

public class Main
{
    public static DataUpdater updater = new DataUpdater();

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

        JFinal.start("src/main/webapp", port, "/", updateInterval);
    }

    public static void start()
    {
        CarList.init();
        DemandList.init();
        // 将DataUpdater作为独立线程运行，避免阻塞主线程
        Thread updaterThread = new Thread(updater);
        // 设置为守护线程，主程序结束时自动结束
        updaterThread.setDaemon(true);
        updaterThread.start();
    }
}