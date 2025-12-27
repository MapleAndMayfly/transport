package com.tsAdmin.control.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.tsAdmin.control.DBManager;
import com.tsAdmin.model.Demand;
import com.tsAdmin.model.Product;
import com.tsAdmin.model.ProductType;
import com.tsAdmin.model.poi.*;

public class DemandManager
{
    public static Map<String, Demand> demandList = new HashMap<>();

    public static void init()
    {
        demandList.clear();

        List<Map<String, String>> records = DBManager.getDemandList();
        for (Map<String, String> record : records)
        {
            String uuid = record.get("UUID");
            int quantity = Integer.parseInt(record.get("quantity"));
            int volume = Integer.parseInt(record.get("volume"));
            ProductType type = ProductType.valueOf(record.get("type"));

            Poi origin = PoiManager.poiList.get(record.get("origin_UUID"));
            Poi destination = PoiManager.poiList.get(record.get("destination_UUID"));
            Product product = new Product(type, quantity, volume);

            Demand demand = new Demand(uuid, origin, destination, product);
            demandList.put(uuid, demand);
        }
    }

    /**
     * 生成新的订单并自动将其加入订单列表
     * @param origin 起点，必须是 {@code Dumper} 的实现
     * @param destination 终点，必须是 {@code Purchaser} 或其子类，兴趣点调用时一般为 {@code this}
     * @param quantity 需求的质量
     * @return 生成的订单
     */
    public static Demand generateDemand(Poi origin, Poi destination, int quantity)
    {
        Product product = ((Dumper)origin).packProduct(quantity);
        String uuid = UUID.randomUUID().toString().replace("-", "");

        Demand demand = new Demand(uuid, origin, destination, product);
        demandList.put(uuid, demand);
        return demand;
    }

    public static void removeDemand(String uuid)
    {
        demandList.remove(uuid);
    }
}
