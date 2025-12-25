package com.tsAdmin.control.manager;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tsAdmin.model.poi.*;

public class PoiManager
{
    private static final Logger logger = LogManager.getLogger(PoiManager.class);

    public static Map<String, Poi> poiList = new HashMap<>();

    public static void init()
    {
        poiList.clear();
    }

    /** 更新所有兴趣点，每周期调用 */
    public static void update()
    {
        // 操作量不大，使用同步操作简单点
        for (Poi poi : poiList.values())
        {
            poi.update();
        }

        logger.trace("POI updating completed.");
    }
}
