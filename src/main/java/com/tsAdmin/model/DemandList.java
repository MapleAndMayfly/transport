package com.tsAdmin.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.control.DBManager;
import com.tsAdmin.model.Product.ProductType;
import com.tsAdmin.model.processor.Processor;
import com.tsAdmin.model.processor.ProcessorFactory;
import com.tsAdmin.model.producer.Producer;
import com.tsAdmin.model.producer.ProducerFactory;

public class DemandList
{
    public static Map<String, Demand> demandList = new HashMap<>();

    public static void init()
    {
        if (!DBManager.isTableEmpty("demand"))
        {
            List<Record> records = DBManager.getDemands();
            for (Record record : records)
            {
                String uuid = record.getStr("UUID");
                double origLat = record.getDouble("origin_lat");
                double origLon = record.getDouble("origin_lon");
                double destLat = record.getDouble("destination_lat");
                double destLon = record.getDouble("destination_lon");
                ProductType type = ProductType.valueOf(record.getStr("type"));
                int quantity = record.getInt("quantity");

                Demand demand = new Demand(
                    uuid,
                    new Coordinate(origLat, origLon),
                    new Coordinate(destLat, destLon),
                    type
                );
                demand.setQuantity(quantity);

                // 添加到 demandList 列表
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
            // 随机选择一个产品类型
            ProductType type = Product.getRandType();

            Product product = new Product(type);
            Producer producer = ProducerFactory.getRandProducer(type);

            //根据产品类型选择加工厂（目的地）
            Processor processor = ProcessorFactory.getRandProcessor(type);

            // 生产需求，并把需求存入demandList动态数组当中且存入数据库
            Demand demand = producer.createDemand(product, processor);
            DemandList.demandList.put(demand.getUUID(), demand);
            DBManager.saveDemand(demand);
        }
    }
}
