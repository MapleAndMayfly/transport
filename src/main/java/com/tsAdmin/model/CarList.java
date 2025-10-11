package com.tsAdmin.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.jfinal.plugin.activerecord.Record;
import com.tsAdmin.common.ConfigLoader;
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

    public static int getLoad(int randIdx) { return LOADS[randIdx]; }
    public static int getVolume(int randIdx) { return VOLUMES[randIdx]; }

    public static void init()
    {
        int carNum = ConfigLoader.getInt("CarList.car_num", 100);
        // TODO: 可改为查看车辆数：若多则只取所需；若少则增加车辆
        if (DBManager.isTableEmpty("car"))
        {
            for (int i = 0; i < carNum; i++)
            {
                String uuid = UUID.randomUUID().toString().replace("-", "");
                CarType carType = null;
                int maxLoad, maxVolume;

                int loadIdx = RANDOM.nextInt(LOADS.length);
                int volumeIdx = RANDOM.nextInt(VOLUMES.length);
                if (i <= 0.6 * carNum)
                {
                    carType = CarType.COMMON;
                    maxLoad = getLoad(loadIdx);
                    maxVolume = getVolume(volumeIdx);
                }
                else if (i <= 0.8 * carNum)
                {
                    carType = CarType.INSULATED_VAN;
                    maxLoad = getLoad(loadIdx);
                    maxVolume = getVolume(volumeIdx);
                }
                else
                {
                    carType = CarType.OVERSIZED;
                    maxLoad = getLoad(LOADS.length - 1);
                    maxVolume = getVolume(VOLUMES.length - 1);
                }

                Car car = new Car(uuid, carType, maxLoad, maxVolume, new Coordinate(getNaturalRandomLocation()));
                DBManager.saveCar(car);
            }
        }

        List<Record> records = DBManager.getCars();
        for (Record record : records)
        {
            String uuid = record.getStr("UUID");
            CarType carType = CarType.valueOf(record.getStr("type"));
            int maxLoad = record.getInt("maxload");
            int maxVolume = record.getInt("maxvolume");
            double lat = record.getDouble("location_lat");
            double lon = record.getDouble("location_lon");

            Car car = new Car(uuid, carType, maxLoad, maxVolume, new Coordinate(lat, lon));
            // 两次setState: 第一次将上一状态set为当前状态，第二次set会自动将其转移至上一状态
            car.setState(CarState.AVAILABLE);
            car.setState(CarState.AVAILABLE);
            car.setLoad(0);
            car.setVolume(0);

            carList.put(uuid, car);
        }
    }

    /**
     * 生成随机车辆
     * @param num 生成数
     */
    public static void generateCar(int num)
    {
        // TODO: 封装车辆生成函数，以适配存在车辆但数量不足的情况
    }

    /**
     * 生成随机方位点
     * @return 随机方位点
     */
    private static final Coordinate getNaturalRandomLocation()
    {
        double maxRadius = 0.12; // 最大半径约2公里

        // 随机角度
        double angle = RANDOM.nextDouble() % 2 * Math.PI;
        
        // 随机距离（使用平方根使分布更均匀）
        double distance = Math.sqrt(RANDOM.nextDouble()) * maxRadius;

        // 计算偏移量（经度需要根据纬度调整）
        double latOffset = distance * Math.sin(angle);
        double lngOffset = distance * Math.cos(angle) / Math.cos(Math.toRadians(defaultLocation.lat));

        return new Coordinate(
            defaultLocation.lat + latOffset,
            defaultLocation.lon + lngOffset
        );
    }

    /** 彩蛋: 圆周分布 */
    @SuppressWarnings("unused")
    private static final Coordinate getCircularLocation(int i)
    {
        double radius = 1; // 分布半径

        double angle = 2 * Math.PI * i / 100;
        return new Coordinate(
            defaultLocation.lat + radius * Math.sin(angle),
            defaultLocation.lon + radius * Math.cos(angle)
        );
    }
}
