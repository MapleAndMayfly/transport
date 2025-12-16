package com.tsAdmin.model.processor;

import java.util.Map;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.control.DBManager;
import com.tsAdmin.model.ProductType;

public class ProcessorManager
{
    private static final Map<ProductType, String> typeStr = Map.of(
        ProductType.PHARMA, "pharmaProcessor",
        ProductType.STEEL, "steelProcessor",
        ProductType.WOOD, "woodProcessor"
    );

    public static Processor getRandProcessor(ProductType type)
    {
        Map<String, String> data = DBManager.getRandPoi(typeStr.get(type));

        if (data != null)
        {
            String uuid = data.get("UUID");
            String name = data.get("name");
            double lat = Double.parseDouble(data.get("lat"));
            double lon = Double.parseDouble(data.get("lon"));

            return new Processor(uuid, name, type, new Coordinate(lat, lon));
        }
        else throw new RuntimeException("数据库中没找到生产厂");
    }
}
