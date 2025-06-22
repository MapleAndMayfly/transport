package com.tsAdmin.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Db;

import com.tsAdmin.model.Car;
import com.tsAdmin.model.Demand;

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
     * 获取POI数据列表
     * @param type 要获取的对象类型，只能为POI_TABLES中的键所对应的字符串
     * @return 所有该类型POI数据的列表，每一条数据包含id, name, location_lat, location_lon
     * @throws IllegalAgumentException 传入的POI类型未定义
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

    /**
     * 获取车辆数据列表
     * @return 所有车辆数据的列表，每一条数据包含id, location_lat, location_lon
     */
    public static List<Map<String, String>> getCarData()
    {
        List<Map<String, String>> carData = new ArrayList<>();

        String sql = "SELECT UUID, location_lat, location_lon FROM car";
        List<Record> rawData = Db.find(sql);

        if (rawData != null && !rawData.isEmpty())
        {
            for (Record record : rawData)
            {
                Map<String, String> element = Map.of(
                    "UUID", record.get("UUID"),
                    "lat", record.get("location_lat").toString(),
                    "lon", record.get("location_lon").toString()
                );
                carData.add(element);
            }
        }
        return carData;
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

    public static boolean isTableEmpty(String tableName)
    {
        try
        {
            String sql = "SELECT COUNT(*) AS count FROM " + tableName;
            Record record = Db.findFirst(sql);
            if (record == null) {
                throw new RuntimeException("Query failed");
            }
            return record.getLong("count") == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void updateCarPos(Car car)
    {
        String sql = "UPDATE car SET location_lat = ?, location_lon = ? WHERE UUID = ?";
        Db.update(sql, car.getPosition().lat, car.getPosition().lon, car.getUUID());
    }

    public static void updateCarState(Car car)
    {
        String sql = "UPDATE car SET currState = ?, prevState = ? WHERE UUID = ?";
        Db.update(sql, car.getState(), car.getPrevState(), car.getUUID());
    }

    public static void updateCarTime(Car car)
    {
        String sql = "UPDATE car SET time = ? WHERE UUID = ?";
        Db.update(sql, car.getStateTimer().getTime(), car.getUUID());
    }

    public static List<Record> getDemands()
    {
        String sql = "SELECT UUID, origin_lat, origin_lon, destination_lat, destination_lon, type, quantity FROM demand";
        return Db.find(sql);
    }

    public static void saveDemand(Demand demand)
    {
        Record demandRecord = new Record();
        demandRecord.set("UUID", demand.getUUID())
                    .set("origin_lat", demand.getOrigin().lat)
                    .set("origin_lon", demand.getOrigin().lon)
                    .set("destination_lat",demand.getDestination().lat)
                    .set("destination_lon", demand.getDestination().lon)
                    .set("type", demand.getType().name())
                    .set("quantity",demand.getQuantity());
        Db.save("demand", demandRecord);
    }

    /** 更新刚被接单的需求所剩质量*/
    public static void updateDemandQuantity(Demand demand)
    {
        String sql = "UPDATE demand SET quantity = ? WHERE UUID = ?";
        Db.update(sql, demand.getQuantity(), demand.getUUID());
    }

    /** 删除数据库中的需求*/
    public static boolean deleteDemand(String UUID)
    {
        try
        {
            String sql = "DELETE FROM demand WHERE UUID = ?";
            Db.update(sql, UUID);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
