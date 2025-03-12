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

    private static final int[] LOADS = { 2, 3, 5, 8, 10, 15, 20, 25, 30 };
    private static final Coordinate defaultLocation = new Coordinate(30.67646, 104.10248);
    private static int carNum = 10;

    public static int getRandLoad() { return LOADS[RANDOM.nextInt(LOADS.length)]; }

    public static void init()
    {
        if (DBManager.isTableEmpty("car"))
        {
            // TODO: 车辆应手动注册
            for (int i = 0; i < carNum; i++)
            {
                Record carRecord = new Record();
                carRecord.set("UUID", UUID.randomUUID().toString().replace("-", ""))
                           .set("location_lat", defaultLocation.lat)
                           .set("location_lon", defaultLocation.lon)
                           .set("state", "AVAILABLE")
                           .set("prestate", "AVAILABLE");
                if (i <= 0.6*carNum) 
                {
                    carRecord.set("type", "COMMON").set("maxload", getRandLoad());
                }
                else if (i <= 0.8*carNum) 
                {
                    carRecord.set("type", "INSULATED_VAN").set("maxload", getRandLoad());
                }
                else 
                {
                    carRecord.set("type", "OVERSIZED").set("maxload", 30);
                }

                Db.save("car", carRecord);
            }
        }

        String tableName = "car";
        String sql = "SELECT UUID, type, maxload, `load`, location_lat, location_lon, state, prestate, `time` FROM " + tableName;
        List<Record> records = Db.find(sql);
        for (Record record : records) 
        {
            String uuid = record.getStr("UUID");
            CarType carType = CarType.valueOf(record.getStr("type"));
            int maxload = record.getInt("maxload");
            int load = record.getInt("load");
            double lat = record.getDouble("location_lat");
            double lon = record.getDouble("location_lon");
            CarState currState = CarState.valueOf(record.getStr("state"));
            CarState prevState = CarState.valueOf(record.getStr("prestate"));
            int time = record.getInt("time");

            Car car = new Car(uuid, carType, maxload, new Coordinate(lat, lon));
            car.setLoad(load);
            // 两次setState: 第一次将上一状态set为当前状态，第二次set会自动将其转移至上一状态
            car.setState(prevState);
            car.setState(currState);
            car.getStateTimer().setTime(time);

            carList.put(uuid, car);
        }
    }
}
