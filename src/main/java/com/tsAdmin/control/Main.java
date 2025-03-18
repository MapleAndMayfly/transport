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
        DemandList.init();//如果数据库中存在，则初始化订单数组
        CarList.init();//已有车辆信息存在
        // updater.run();
    }
}