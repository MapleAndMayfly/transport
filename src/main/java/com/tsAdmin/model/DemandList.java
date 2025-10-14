package com.tsAdmin.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.tsAdmin.common.Coordinate;
import com.tsAdmin.control.DBManager;
import com.tsAdmin.model.Product.ProductType;
import com.tsAdmin.model.producer.ProducerManager;
import com.tsAdmin.model.processor.ProcessorManager;
import com.tsAdmin.model.producer.Producer;
import com.tsAdmin.model.processor.Processor;

public class DemandList
{
    public static Map<String, Demand> demandList = new HashMap<>();

    public static void init()
    {
        if (!DBManager.isTableEmpty("demand"))
        {
            List<Map<String, String>> records = DBManager.getDemandData();
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
                demandList.put(uuid, demand);
            }
        }
    }

    /**
     * 生成随机需求
     * @param num 需要生成的数量
     */
    public static void generateDemand(int num)
    {
        for (; num > 0; num--)
        {
            // 随机生成一个产品
            ProductType type = Product.ProductType.getRandType();

            //根据产品类型选择起终点
            Producer producer = ProducerManager.getRandProducer(type);
            Processor processor = ProcessorManager.getRandProcessor(type);

            // 生产需求，并把需求存入demandList与数据库当中
            Demand demand = producer.createDemand(type, processor);
            DemandList.demandList.put(demand.getUUID(), demand);
            DBManager.saveDemand(demand);
        }
    }
}
