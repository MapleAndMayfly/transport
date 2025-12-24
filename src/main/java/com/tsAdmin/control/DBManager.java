package com.tsAdmin.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Db;
import com.tsAdmin.model.Car;
import com.tsAdmin.model.Demand;

public class DBManager
{
    private static final Logger logger = LogManager.getLogger(DBManager.class);

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
            if (record == null) throw new RuntimeException("Query failed");

            return record.getLong("count");
        }
        catch (Exception e)
        {
            logger.error("Failed to get count for {}", tableName, e);
            return -1;
        }
    }

    /**
     * 获取POI数据列表
     * @param type 要获取的对象类型，只能为 {@code POI_TABLES} 中的键所对应的字符串
     * @return 所有该类型 POI 数据的列表，每一条数据包含 id, name, location_lat, location_lon
     * @throws IllegalAgumentException 如果传入的POI类型未定义
     */
    public static List<Map<String, Object>> getPoiList(String type)
    {
        try
        {
            List<Map<String, Object>> poiList = new ArrayList<>();
            String table = POI_TABLES.get(type);
            if (table == null) throw new IllegalArgumentException("Invalid table type: " + type);

            String sql = "SELECT location_ID, name, location_lat, location_lon FROM " + table;
            List<Record> rawData = Db.find(sql);

            if (rawData != null && !rawData.isEmpty())
            {
                for (Record record : rawData)
                {
                    Map<String, Object> element = Map.of(
                        "UUID", record.get("location_ID"),
                        "name", record.get("name"),
                        "lat", record.get("location_lat"),
                        "lon", record.get("location_lon")
                    );
                    poiList.add(element);
                }
            }

            logger.debug("Got POI list({} total) form SQL table: {}", rawData != null ? rawData.size() : 0, table);
            return poiList;
        }
        catch (Exception e)
        {
            logger.error("Failed to get POI list from SQL", e);
            return null;
        }
    }

    public static List<Map<String, String>> getDemandList()
    {
        try
        {
            List<Map<String, String>> demandList = new ArrayList<>();
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
                    demandList.add(element);
                }
            }

            logger.debug("Got demand list({} total) from SQL", rawData != null ? rawData.size() : 0);
            return demandList;
        }
        catch (Exception e)
        {
            logger.error("Failed to get demand list from SQL", e);
            return null;
        }
    }

    public static List<Map<String, Object>> getCarList()
    {
        try
        {
            List<Map<String, Object>> carList = new ArrayList<>();
            String sql = "SELECT UUID, type, maxload, maxvolume, location_lat, location_lon FROM car";
            List<Record> rawData = Db.find(sql);

            if (rawData != null && !rawData.isEmpty())
            {
                for (Record record : rawData)
                {
                    Map<String, Object> element = Map.of(
                        "UUID", record.get("UUID"),
                        "type", record.get("type"),
                        "maxload", record.get("maxload"),
                        "maxvolume", record.get("maxvolume"),
                        "lat", record.get("location_lat"),
                        "lon", record.get("location_lon")
                    );
                    carList.add(element);
                }
            }

            logger.debug("Got car list({} total) from SQL", rawData != null ? rawData.size() : 0);
            return carList;
        }
        catch (Exception e)
        {
            logger.error("Failed to get car list from SQL", e);
            return null;
        }
    }

    public static List<Map<String, String>> getPresetList()
    {

        try
        {
            List<Map<String, String>> presets = new ArrayList<>();
            String sql = "SELECT UUID, content FROM preset";
            List<Record> rawData = Db.find(sql);

            if (rawData != null && !rawData.isEmpty())
            {
                for (Record record : rawData)
                {
                    Map<String, String> element = Map.of(
                        "UUID", record.getStr("UUID"),
                        "content", record.getStr("content")
                    );
                    presets.add(element);
                }
            }

            logger.debug("Got preset list({} total) from SQL", rawData != null ? rawData.size() : 0);
            return presets;
        }
        catch (Exception e)
        {
            logger.error("Failed to get preset list from SQL", e);
            return null;
        }
    }

    public static String getPreset(String uuid)
    {
        try
        {
            String sql = "SELECT content FROM preset WHERE UUID = ? LIMIT 1";
            Record record = Db.findFirst(sql, uuid);

            if (record != null)
            {
                String content = record.getStr("content");
                int length = content != null ? content.length() : 0;

                logger.debug("Got preset(UUID:{}) from SQL, content length: {}", uuid, length);
                return content;
            }
            else
            {
                logger.warn("Preset(UUID:{}) not found, null returned", uuid);
                return null;
            }
        }
        catch (Exception e)
        {
            logger.error("Failed to get preset(UUID:{}) from SQL", uuid, e);
            return null;
        }
    }

    /**
     * 保存订单到数据库
     * @param demand 要保存的订单
     * @return 此次保存成功与否
     */
    public static boolean saveDemand(Demand demand)
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
        return Db.save("demand", demandRecord);
    }

    /**
     * 保存车辆到数据库
     * <p><i>仅在初始化车辆数不足时调用</i>
     * @param car 要保存的车辆
     * @return 此次保存成功与否
     */
    public static boolean saveCar(Car car)
    {
        Record carRecord = new Record();
        carRecord.set("UUID", car.getUUID())
                 .set("maxLoad", car.getMaxLoad())
                 .set("maxVolume", car.getMaxVolume())
                 .set("location_lat", car.getPosition().lat)
                 .set("location_lon", car.getPosition().lon);
        return Db.save("car", carRecord);
    }

    /**
     * 保存预设到数据库
     * @param isNew 是否为新预设
     * @param uuid 将保存预设的 UUID
     * @param content 预设内容，格式同resources/config.json
     * @return 此次保存操作成功与否
     */
    public static boolean savePreset(boolean isNew, String uuid, String content)
    {
        try
        {
            boolean success = false;

            if (isNew)
            {
                Record presetRecord = new Record();
                presetRecord.set("UUID", uuid)
                            .set("content", content);
                success = Db.save("preset", presetRecord);
            }
            else
            {
                String updateSql = "UPDATE preset SET content = ? WHERE UUID = ?";
                success = Db.update(updateSql, content, uuid) > 0;
            }

            logger.debug("Saved preset(UUID:{}), success status: {}", uuid, success);
            return success;
        }
        catch (Exception e)
        {
            logger.error("Failed to save preset(UUID:{})", uuid, e);
            return false;
        }
    }

    public static boolean rmvPreset(String uuid)
    {
        try
        {
            boolean success = false;
            String sql = "DELETE FROM preset WHERE UUID = ?";
            success = Db.delete(sql, uuid) > 0;

            logger.debug("Removed preset(UUID:{}), success status: {}", uuid, success);
            return success;
        }
        catch (Exception e)
        {
            logger.error("Failed to remove preset(UUID:{})", uuid, e);
            return false;
        }
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
    public static boolean rmvDemand(Demand demand)
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
