package com.tsAdmin.control.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.control.DBManager;
import com.tsAdmin.model.Demand;
import com.tsAdmin.model.Product;
import com.tsAdmin.model.ProductType;
import com.tsAdmin.model.poi.*;

public class DemandManager
{
    public static List<Demand> demandList = new ArrayList<>();

    public static void init()
    {
        demandList.clear();

        if (!DBManager.isTableEmpty("demand"))
        {
            List<Map<String, String>> records = DBManager.getDemandList();
            for (Map<String, String> record : records)
            {
                String uuid = record.get("UUID");
                double origLat = Double.parseDouble(record.get("origin_lat"));
                double origLon = Double.parseDouble(record.get("origin_lon"));
                double destLat = Double.parseDouble(record.get("destination_lat"));
                double destLon = Double.parseDouble(record.get("destination_lon"));
                ProductType type = ProductType.valueOf(record.get("type"));
                int quantity = Integer.parseInt(record.get("quantity"));
                int volume = Integer.parseInt(record.get("volume"));

                Product product = new Product(type, quantity, volume);

                Demand demand = new Demand(
                    uuid,
                    new Coordinate(origLat, origLon),
                    new Coordinate(destLat, destLon),
                    product
                );

                // 添加到 demand 列表
                demandList.add(demand);
            }
        }
    }

    public static void generateDemand(Product product, Poi from, Poi to)
    {

        // for (; num > 0; num--)
        // {
        //     // 随机生成一个产品
        //     ProductType type = null;

        //     //根据产品类型选择起终点
        //     Producer producer = ProducerManager.getRandProducer(type);
        //     Processor processor = ProcessorManager.getRandProcessor(type);

        //     // 生产需求，并把需求存入demandList与数据库当中
        //     Demand demand = processor.createDemand(type, processor);
        //     DemandManager.demandList.put(demand.getUUID(), demand);
        //     DBManager.saveDemand(demand);
        // }
    }
}
