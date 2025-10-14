package com.tsAdmin.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.tsAdmin.model.Demand;
import com.tsAdmin.model.car.Car;

public class DBManager
{
    private static final Map<String, String> POI_TABLES = Map.of(
        "pharmaProducer", "pharmaceutical_producer",
        "steelProducer", "steel_producer",
        "woodProducer", "wood_producer",
        "pharmaProcessor", "pharmaceutical_processor",
        "steelProcessor", "steel_processor",
        "woodProcessor", "wood_processor"
    );

    /**
     * 判断数据表是否为空
     * @param tableName 数据表名
     * @return {@code true} 如果查询出错（如表格不存在）或表格为空
     */
    public static boolean isTableEmpty(String tableName) { return getCount(tableName) <= 0; }

    public static long getCount(String tableName)
    {
        try
        {
            String sql = "SELECT COUNT(*) AS count FROM " + tableName;
            Record record = Db.findFirst(sql);
            if (record == null)
            {
                throw new RuntimeException("Query failed");
            }
            return record.getLong("count");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public static Map<String, String> getRandPoi(String type)
    {
        String table = POI_TABLES.get(type);
        if (table == null) throw new IllegalArgumentException("Invalid table type: " + type);

        String sql = "SELECT location_ID, name, location_lat, location_lon FROM " + table + " ORDER BY RAND() LIMIT 1";
        Record rawData = Db.findFirst(sql);

        Map<String, String> POIData = Map.of(
            "UUID", rawData.get("location_ID"),
            "name", rawData.get("name"),
            "lat", rawData.get("location_lat").toString(),
            "lon", rawData.get("location_lon").toString()
        );
        return POIData;
    }

    /**
     * 获取POI数据列表
     * @param type 要获取的对象类型，只能为{@code POI_TABLES}中的键所对应的字符串
     * @return 所有该类型POI数据的列表，每一条数据包含id, name, location_lat, location_lon
     * @throws IllegalAgumentException 如果传入的POI类型未定义
     */
    public static List<Map<String, String>> getPoiData(String type)
    {
        List<Map<String, String>> poiData = new ArrayList<>();
        String table = POI_TABLES.get(type);
        if (table == null) throw new IllegalArgumentException("Invalid table type: " + type);

        String sql = "SELECT location_ID, name, location_lat, location_lon FROM " + table;
        List<Record> rawData = Db.find(sql);

        if (rawData != null && !rawData.isEmpty())
        {
            for (Record record : rawData)
            {
                Map<String, String> element = Map.of(
                    "UUID", record.get("location_ID"),
                    "name", record.get("name"),
                    "lat", record.get("location_lat").toString(),
                    "lon", record.get("location_lon").toString()
                );
                poiData.add(element);
            }
        }
        return poiData;
    }

    public static List<Map<String, String>> getDemandData()
    {
        List<Map<String, String>> demandData = new ArrayList<>();
        
        try
        {
            String sql = "SELECT UUID, origin_lat, origin_lon, destination_lat, destination_lon, type, quantity, volume FROM demand";
            List<Record> rawData = Db.find(sql);
            
            if (rawData != null && !rawData.isEmpty())
            {
                for (Record record : rawData)
                {
                    Map<String, String> element = Map.of(
                        "UUID", record.get("UUID"),
                        "origin_lat", record.get("origin_lat").toString(),
                        "origin_lon", record.get("origin_lon").toString(),
                        "destination_lat", record.get("destination_lat").toString(),
                        "destination_lon", record.get("destination_lon").toString(),
                        "type", record.get("type"),
                        "quantity", record.get("quantity").toString(),
                        "volume", record.get("volume").toString()
                    );
                    demandData.add(element);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return demandData;
    }

    public static List<Map<String, String>> getCarData()
    {
        List<Map<String, String>> carData = new ArrayList<>();
        
        try
        {
            String sql = "SELECT UUID, type, maxload, maxvolume, location_lat, location_lon FROM car";
            List<Record> rawData = Db.find(sql);
            
            if (rawData != null && !rawData.isEmpty())
            {
                for (Record record : rawData)
                {
                    Map<String, String> element = Map.of(
                        "UUID", record.get("UUID"),
                        "type", record.get("type"),
                        "maxload", record.get("maxload").toString(),
                        "maxvolume", record.get("maxvolume").toString(),
                        "location_lat", record.get("location_lat").toString(),
                        "location_lon", record.get("location_lon").toString()
                    );
                    carData.add(element);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return carData;
    }

    public static String getPreset(String name)
    {
        String sql = "SELECT content FROM preset WHERE name = ? LIMIT 1";
        String jsonString = Db.find(sql, name).toString();
        return jsonString;
    }

    public static List<String> getAllPresets()
    {
        List<String> presets = new ArrayList<>();
        try
        {
            String sql = "SELECT content FROM preset ORDER BY name";
            List<Record> raws = Db.find(sql);
            if (raws != null && !raws.isEmpty())
            {
                for (Record r : raws)
                {
                    String content = r.getStr("content");
                    if (content != null)
                    {
                        presets.add(content);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return presets;
    }

    public static void savePreset(String fullString)
    {
        JSONObject json = JSON.parseObject(fullString);
        // 若json结构不合法，则将名字置为Default
        String name = json != null && json.getString("name") != null ? json.getString("name") : "Illegal";
        String sql = "SELECT content FROM preset WHERE name = ? LIMIT 1";
        Record exist = Db.findFirst(sql, name);
        if (exist != null)
        {
            String updateSql = "UPDATE preset SET content = ? WHERE name = ?";
            Db.update(updateSql, fullString, name);
        }
        else
        {
            Record presetRecord = new Record();
            presetRecord.set("name", name)
                        .set("content", fullString);
            Db.save("preset", presetRecord);
        }
    }

    /**
     * 保存订单到数据库
     * @param demand 要保存的订单
     */
    public static void saveDemand(Demand demand)
    {
        Record demandRecord = new Record();
        demandRecord.set("UUID", demand.getUUID())
                    .set("origin_lat", demand.getOrigin().lat)
                    .set("origin_lon", demand.getOrigin().lon)
                    .set("destination_lat",demand.getDestination().lat)
                    .set("destination_lon", demand.getDestination().lon)
                    .set("type", demand.getType().name())
                    .set("quantity",demand.getQuantity())
                    .set("volume", demand.getVolume());
        Db.save("demand", demandRecord);
    }

    /**
     * 保存车辆到数据库
     * <p><i>仅在初始化车辆数不足时调用</i>
     * @param car 要保存的车辆
     */
    public static void saveCar(Car car)
    {
        Record carRecord = new Record();
        carRecord.set("UUID", car.getUUID())
                 .set("type", car.getType().toString())
                 .set("maxLoad", car.getMaxLoad())
                 .set("maxVolume", car.getMaxVolume())
                 .set("location_lat", car.getPosition().lat)
                 .set("location_lon", car.getPosition().lon);
        Db.save("car", carRecord);
    }

    /* ================== 以下内容会导致模拟时无法保证情况相同，暂时废弃 ================== */

    @Deprecated
    public static void updateCarPos(Car car)
    {
        String sql = "UPDATE car SET location_lat = ?, location_lon = ? WHERE UUID = ?";
        Db.update(sql, car.getPosition().lat, car.getPosition().lon, car.getUUID());
    }

    @Deprecated
    public static void updateCarState(Car car)
    {
        String sql = "UPDATE car SET state = ?, prestate = ? WHERE UUID = ?";
        Db.update(sql, car.getState().toString(), car.getPrevState().toString(), car.getUUID().toString());
    }

    @Deprecated
    public static void updateCarTime(Car car)
    {
        String sql = "UPDATE car SET time = ? WHERE UUID = ?";
        Db.update(sql, car.getStateTimer().getTime(), car.getUUID());
    }

    /**
     * 在数据库中更新订单剩余质量
     * @param demand 更新的订单
     */
    @Deprecated
    public static void updateDemandQuantity(Demand demand)
    {
        String sql = "UPDATE demand SET quantity = ? WHERE UUID = ?";
        Db.update(sql, demand.getQuantity(), demand.getUUID());
    }

    /**
     * 从数据库删除订单
     * @param demand 要删除订单
     * @return {@code true} 如果删除成功
     */
    @Deprecated
    public static boolean deleteDemand(Demand demand)
    {
        try
        {
            String sql = "DELETE FROM demand WHERE UUID = ?";
            Db.update(sql, demand.getUUID());
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
