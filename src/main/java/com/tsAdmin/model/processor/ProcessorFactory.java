package com.tsAdmin.model.processor;

import java.util.Map;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.control.DBManager;
import com.tsAdmin.model.Product.ProductType;

public class ProcessorFactory
{
    private static Map<ProductType, String> typeStr = Map.of(
        ProductType.PHARMACEUTICAL, "pharmaProcessor",
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

            return factory(type, uuid, name, new Coordinate(lat, lon));
        }
        else throw new RuntimeException("数据库中没找到生产厂");
    }

    private static Processor factory(ProductType type, String uuid, String name, Coordinate coordinate) 
    {
        Processor ret = null;

        switch (type)
        {
            case WOOD:
                ret = new WoodProcessor(uuid, name, coordinate);
                break;
            case STEEL:
                ret = new SteelProcessor(uuid, name, coordinate);
                break;
            case PHARMACEUTICAL:
                ret = new PharmaceuticalProcessor(uuid, name, coordinate);
                break;
            default:
                break;
        }
        return ret;
    }
}
