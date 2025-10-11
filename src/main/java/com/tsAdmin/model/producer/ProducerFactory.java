package com.tsAdmin.model.producer;

import java.util.Map;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.control.DBManager;
import com.tsAdmin.model.Product.ProductType;

public class ProducerFactory
{
    private static Map<ProductType, String> typeStr = Map.of(
        ProductType.PHARMACEUTICAL, "pharmaProducer",
        ProductType.STEEL, "steelProducer",
        ProductType.WOOD, "woodProducer"
    );

    public static Producer getRandProducer(ProductType type)
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

    private static Producer factory(ProductType type, String uuid, String name, Coordinate coordinate) 
    {
        Producer ret = null;

        switch (type) 
        {
            case WOOD:
                ret = new WoodProducer(uuid, name, coordinate);
                break;
            case STEEL:
                ret = new SteelProducer(uuid, name, coordinate);
                break;
            case PHARMACEUTICAL:
                ret = new PharmaceuticalProducer(uuid, name, coordinate);
                break;
            default:
                break;
        }
        return ret;
    }
}