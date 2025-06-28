package com.tsAdmin.control;

import com.jfinal.core.JFinal;

import com.tsAdmin.model.CarList;
import com.tsAdmin.model.DemandList;

public class Main
{
    public static DataUpdater updater = new DataUpdater();

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

        JFinal.start("src/main/webapp", 8080, "/", 5);
    }

    public static void init()
    {
        CarList.init();
        //DemandList.init();
        // updater.run();
    }
}