package com.tsAdmin.control.manager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tsAdmin.model.poi.Poi;
import com.tsAdmin.model.poi.Processor;
import com.tsAdmin.model.poi.Producer;

public class PoiManager
{
    private static final Logger logger = LogManager.getLogger(PoiManager.class);

    private static List<Poi> poiList = new ArrayList<>();

    private static Deque<Producer> availableProducer = new ArrayDeque<>();
    private static Deque<Processor> availableProcessor = new ArrayDeque<>();

    public static void init()
    {
        poiList.clear();
    }

    /** 更新所有兴趣点，每周期调用 */
    public static void update()
    {
        // 操作量不大，使用同步操作简单点
        for (Poi poi : poiList)
        {
            poi.update();
        }

        logger.trace("POI updating completed.");
    }

    /**
     * 将兴趣点加入可用列表
     * @param poi 加入的兴趣点，一般使用{@code this}指向自己
     */
    public static void markAvailable(Poi poi)
    {
        String type = poi.getClass().getSimpleName();
        try
        {
            switch (type)
            {
                case "Producer":
                    availableProducer.add((Producer)poi);
                    break;

                case "Processor":
                    availableProcessor.add((Processor)poi);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid POI type!");
            }

            poi.setInQueque(true);
        }
        catch (Exception e)
        {
            logger.error("Failed to add POI(type:{}) to available queque", type, e);
        }
    }
}
