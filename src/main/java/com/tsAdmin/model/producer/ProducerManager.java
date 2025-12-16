package com.tsAdmin.model.producer;

import java.util.Map;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.control.DBManager;
import com.tsAdmin.model.ProductType;

public class ProducerManager
{
    public static Producer getRandProducer(ProductType type)
    {
        String typeStr = type.toString().toLowerCase() + "Producer";
        Map<String, String> data = DBManager.getRandPoi(typeStr);   // TODO

        if (data != null)
        {
            String uuid = data.get("UUID");
            String name = data.get("name");
            double lat = Double.parseDouble(data.get("lat"));
            double lon = Double.parseDouble(data.get("lon"));

            return new Producer(uuid, name, type, new Coordinate(lat, lon));
        }
        else throw new RuntimeException("数据库中没找到生产厂");
    }
}