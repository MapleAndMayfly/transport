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
            String sql = "SELECT UUID, type, quantity, volume, origin_UUID, destination_UUID FROM demand";
            List<Record> rawData = Db.find(sql);
            
            if (rawData != null && !rawData.isEmpty())
            {
                for (Record record : rawData)
                {
                    Map<String, String> element = Map.of(
                        "UUID", record.get("UUID"),
                        "type", record.get("type"),
                        "quantity", record.get("quantity").toString(),
                        "volume", record.get("volume").toString(),
                        "origin_UUID", record.get("origin_UUID").toString(),
                        "destination_UUID", record.get("destination_UUID").toString()
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
        /**根据关键字和城市搜索POI点并插入数据库 */
    public static void insertIntoDB(String keywords) 
	{
        String[] cities= {"成都市","自贡市","攀枝花市","泸州市","德阳市","绵阳市","广元市","遂宁市","内江市","乐山市","南充市","眉山市","宜宾市","广安市","达州市","雅安市","巴中市","资阳市","阿坝州","甘孜州","凉山州"};
        for(String city:cities){
            String RequestURL="https://restapi.amap.com/v3/place/text?"
                    + "keywords="+keywords+"&city="+city+"&offset=100&key=3a58ca26430baffeffba0a4e1698f51a&extensions=base"; 
            String resText=HttpKit.get(RequestURL);
            //解析并存储到数据库
            JSONObject resOBJ=JSONObject.parseObject(resText);//把文本格式的json字符转为对象，方便取值
            if(resOBJ.getJSONArray("pois")==null) continue;//如果没有pois数组，跳过
            JSONArray pois=resOBJ.getJSONArray("pois");//拿到JSON里的pois数组
            
            String tablename = "pharmaceutical_market";
            String tablename2 = "pharmaceutical_producer";
            String tablename3 = "pharmaceutical_processor";
            //pois中，数据是数组形式，用循环解析
            for(int i=0;i<100 && i<pois.size();i++) 
            {
                String objString=pois.get(i).toString();//第i个对象转为字符串
                JSONObject singleObj=JSONObject.parseObject(objString);//再把对象由String转为jsonObj
                String locationID=singleObj.getString("id");//ID
                String POI_pname=singleObj.get("pname").toString();//省
                String POI_cityname=singleObj.get("cityname").toString();//市
                String POI_location=singleObj.get("location").toString();
                String POI_lat=POI_location.split(",")[1];//,之后的为纬度
                String POI_lon=POI_location.split(",")[0];//,之前的为经度
                String POI_name=singleObj.get("name").toString();//具体名称
                //调试输出一下试试
                System.out.println(i+"  location_ID  "+locationID+"  地点是  "+POI_name+"  纬度是  "+POI_lat+"  经度是  "+POI_lon);
                //经过调试，可以解析到这些数据，把这些数据存放到数据库中，注意如果数据库组已经存在该地点，则不用再添加
                try 
                {
                    if(!(ifexist(tablename,locationID)||ifexist(tablename2, locationID)||ifexist(tablename3, locationID))) //不存在时插入数据
                    {
                        //插入数据
                        Record e_poiRecord=new Record();
                        e_poiRecord.set("location_ID", locationID).set("pname", POI_pname).set("cityname", POI_cityname);
                        e_poiRecord.set("location_lat",POI_lat).set("location_lon",POI_lon).set("name", POI_name);
                        Db.save(tablename,e_poiRecord);
                    }
                }
                catch(Exception e) 
                {
                    e.printStackTrace();  // 打印异常信息
                }
            }
        }
	}
    /**POI点是否已存在于数据库 */
    public static boolean ifexist(String table,String locationID) 
	{
        List<Record> results = Db.find("SELECT * FROM " + table + " WHERE location_id = ?", locationID);
		// 检查结果集是否为空
        if (!results.isEmpty()) 
        {
            return true;//表示已经存在
        } else 
        {
            return false;//表示不存在
        }	
	}


    /**
     * 厂商类
     */
    private static class Supplier {
        String id;
        Coordinate coordinate;
        
        Supplier(String id, Coordinate coordinate) {
            this.id = id;
            this.coordinate = coordinate;
        }
    }
    /**插入上游厂商ID 
     * 虽然这种多对多的关系规范做法是建立关系表，但为了简化数据库设计，这里直接将上游供应商ID列表存储在下游表的upstream_suppliers字段中
    */
    public static void insert_upstream_suppliers(String downstream_table,int n)
    {
        Map<String, String> downstream_upstream = Map.of(
            "pharmaceutical_market", "pharmaceutical_processor",
            "pharmaceutical_processor", "pharmaceutical_producer",
            "steel_market", "steel_processor",
            "steel_processor", "steel_producer",
            "wood_market", "wood_processor",
            "wood_processor", "wood_producer"
        );
        String upstream_table = downstream_upstream.get(downstream_table);
        if (upstream_table == null) {
            System.err.println("错误: 找不到下游表 " + downstream_table + " 对应的上游表");
            return;
        }
        
        System.out.println("开始为 " + downstream_table + " 表匹配上游供应商，上游表: " + upstream_table);
        
        try {
            // 1. 获取所有下游厂商数据
            List<Supplier> downstreamSuppliers = getSuppliers(downstream_table);
            System.out.println("获取到 " + downstreamSuppliers.size() + " 个下游厂商");
            
            // 2. 获取所有上游厂商数据
            List<Supplier> upstreamSuppliers = getSuppliers(upstream_table);
            System.out.println("获取到 " + upstreamSuppliers.size() + " 个上游厂商");
            
            // 如果上游厂商数量为0，直接返回
            if (upstreamSuppliers.isEmpty()) {
                System.out.println("警告: 上游表 " + upstream_table + " 中没有数据");
                return;
            }
            
            // 3. 为每个下游厂商匹配最近的n个上游厂商
            int updatedCount = 0;
            for (Supplier ds : downstreamSuppliers) {
                List<String> nearestUpstreamIds = findNearestSuppliers(ds.coordinate, upstreamSuppliers, n);
                updateDownstreamSupplier(downstream_table, ds.id, nearestUpstreamIds);
                updatedCount++;
                
                // 每处理10个打印一次进度
                if (updatedCount % 10 == 0) {
                    System.out.println("已处理 " + updatedCount + "/" + downstreamSuppliers.size() + " 个下游厂商");
                }
            }
            
            System.out.println("完成! 共为 " + updatedCount + " 个下游厂商匹配了上游供应商");
            
        } catch (SQLException e) {
            System.err.println("数据库操作失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * 获取厂商列表
     */
    private static List<Supplier> getSuppliers(String tableName) throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String query = "SELECT location_ID, location_lat, location_lon FROM " + tableName;
        List<Record> rawData = Db.find(query);
        for (Record record : rawData) {
            String id = record.getStr("location_ID");
            Double lat = record.getDouble("location_lat");
            Double lon = record.getDouble("location_lon");
            
            // 处理可能为 null 的情况
            if (lat == null || lon == null || lat == 0.0 && lon == 0.0) {
                System.out.println("警告: 跳过ID=" + id + " 的无效坐标记录");
                continue;
            }
            
            suppliers.add(new Supplier(id, new Coordinate(lat, lon)));
        }
        
        return suppliers;
    }
    /**
     * 供应商距离类（用于优先队列）
     */
    private static class SupplierDistance {
        String supplierId;
        double distance;
        
        SupplierDistance(String supplierId, double distance) {
            this.supplierId = supplierId;
            this.distance = distance;
        }
    }
    /**
     * 查找距离最近的上游供应商
     */
    private static List<String> findNearestSuppliers(Coordinate target, 
                                                      List<Supplier> upstreamSuppliers, 
                                                      int n) {
        // 使用优先队列（大顶堆）来维护最近的n个供应商
        PriorityQueue<SupplierDistance> maxHeap = new PriorityQueue<>((a, b) -> 
            Double.compare(b.distance, a.distance));
        
        for (Supplier us : upstreamSuppliers) {
            double distance = Coordinate.distance(target, us.coordinate);
            
            if (maxHeap.size() < n) {
                maxHeap.offer(new SupplierDistance(us.id, distance));
            } else if (distance < maxHeap.peek().distance) {
                maxHeap.poll(); // 移除最远的
                maxHeap.offer(new SupplierDistance(us.id, distance));
            }
        }
        
        // 将结果转换为ID列表
        List<String> result = new ArrayList<>();
        while (!maxHeap.isEmpty()) {
            result.add(0, maxHeap.poll().supplierId); // 按距离从小到大排序
        }        
        return result;
    }
    /**
     * 更新下游厂商的上游供应商列表
     */
    private static void updateDownstreamSupplier(String table, String downstreamId, 
                                                 List<String> upstreamIds) throws SQLException {
        // 将ID列表转换为JSON数组字符串
        String jsonString = JSONArray.toJSONString(upstreamIds);
        
        String updateSQL = "UPDATE " + table + " SET upstream_suppliers = ? WHERE location_ID = ?";
        Db.update(updateSQL, jsonString, downstreamId);
    }
}
