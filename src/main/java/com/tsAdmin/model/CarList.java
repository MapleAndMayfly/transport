package com.tsAdmin.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import com.tsAdmin.common.Coordinate;
import com.tsAdmin.control.DBManager;
import com.tsAdmin.model.Car.CarState;
import com.tsAdmin.model.Car.CarType;

public class CarList
{
    public static Map<String, Car> carList = new HashMap<>();

    private static final Random RANDOM = new Random();

    private static final int[] LOADS = { 2000, 3000, 5000, 8000, 10000, 15000, 20000, 25000, 30000 };
    private static final int[] VOLUMES = { 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000 };
    private static final Coordinate defaultLocation = new Coordinate(30.67646, 104.10248);
    private static int carNum = 100;

    public static int getLoad(int randIdx) { return LOADS[randIdx]; }
    public static int getVolume(int randIdx) { return VOLUMES[randIdx]; }

    public static void init()
    {
        int x=1;
        if (DBManager.isTableEmpty("car"))
        {
            for (int i = 0; i < carNum; i++)
            {
                Record carRecord = new Record();
                carRecord.set("UUID", UUID.randomUUID().toString().replace("-", ""))
                           .set("location_lat", defaultLocation.lat)
                           .set("location_lon", defaultLocation.lon)
                           .set("state", "AVAILABLE")
                           .set("prestate", "AVAILABLE");
                int idx = RANDOM.nextInt(LOADS.length);
                if (i <= 0.6*carNum) 
                {
                    carRecord.set("type", "COMMON").set("maxload", getLoad(idx)).set("maxvolume", getVolume(idx));
                }
                else if (i <= 0.8*carNum) 
                {
                    carRecord.set("type", "INSULATED_VAN").set("maxload", getLoad(idx)).set("maxvolume", getVolume(idx));
                }
                else 
                {
                    carRecord.set("type", "OVERSIZED").set("maxload", getLoad(LOADS.length-1)).set("maxvolume", getVolume(VOLUMES.length-1));
                }

                Db.save("car", carRecord);
            }
        }

        String tableName = "car";
        String sql = "SELECT UUID, type, maxload, maxvolume, `load`, location_lat, location_lon, state, prestate, `time` FROM " + tableName;
        List<Record> records = Db.find(sql);
        for (Record record : records) 
        {
            String uuid = record.getStr("UUID");
            CarType carType = CarType.valueOf(record.getStr("type"));
            int maxLoad = record.getInt("maxload");
            int maxVolume = record.getInt("maxvolume");
            int load = record.getInt("load");
            double lat = record.getDouble("location_lat");
            double lon = record.getDouble("location_lon");
            CarState currState = CarState.valueOf(record.getStr("state"));
            CarState prevState = CarState.valueOf(record.getStr("prestate"));
            int time = record.getInt("time");

            Car car = new Car(uuid, carType, maxLoad, maxVolume, new Coordinate(lat, lon));
            car.setLoad(load);
            // 两次setState: 第一次将上一状态set为当前状态，第二次set会自动将其转移至上一状态
            car.setState(prevState);
            car.setState(currState);
            car.getStateTimer().setTime(time);
            car.setRemainingLoad(maxLoad);
            car.setRemainingVolume(maxVolume);
            car.setPosition(getNaturalRandomLocation(x));x++;
            DBManager.updateCarPos(car);

            carList.put(uuid, car);
        }
    }

    @SuppressWarnings("unused")
    private static final Coordinate getCircularLocation(int i) 
    {
    double baseLat = 30.67646;
    double baseLng = 104.10248;
    double radius = 1; // 分布半径
    
    double angle = 2 * Math.PI * i / 100;
    return new Coordinate(
        baseLat + radius * Math.sin(angle),
        baseLng + radius * Math.cos(angle)
    );   
    }

    private static final Coordinate getNaturalRandomLocation(int i) 
    {
        double baseLat = 30.67646;
        double baseLng = 104.10248;
        double maxRadius = 0.12; // 最大半径约2公里
        
        // 使用种子确保每次运行分布一致（可选）
        Random random = new Random(i * 12345L);
        
        // 随机角度
        double angle = random.nextDouble() * 2 * Math.PI;
        
        // 随机距离（使用平方根使分布更均匀）
        double distance = Math.sqrt(random.nextDouble()) * maxRadius;
        
        // 计算偏移量（经度需要根据纬度调整）
        double latOffset = distance * Math.sin(angle);
        double lngOffset = distance * Math.cos(angle) / Math.cos(Math.toRadians(baseLat));
        
        return new Coordinate(
            baseLat + latOffset,
            baseLng + lngOffset
        );
    }
}
