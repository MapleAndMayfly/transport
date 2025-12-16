package com.tsAdmin.model.car;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.tsAdmin.common.ConfigLoader;
import com.tsAdmin.common.Coordinate;
import com.tsAdmin.control.DBManager;
import com.tsAdmin.model.car.Car.CarState;

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
        carList.clear();

        int carNum = ConfigLoader.getInt("CarList.car_num", 100);
        int realNum = (int)DBManager.getCount("car");

        if (realNum < carNum)
        {
            for (int i = 0; i < carNum - realNum; i++)
            {
                String uuid = UUID.randomUUID().toString().replace("-", "");

                int loadIdx = RANDOM.nextInt(LOADS.length);
                int volumeIdx = RANDOM.nextInt(VOLUMES.length);
                int maxLoad = getLoad(loadIdx);
                int maxVolume = getVolume(volumeIdx);

                Car car = new Car(uuid, maxLoad, maxVolume, new Coordinate(getRandomLocation()));
                DBManager.saveCar(car);
            }
        }

        List<Map<String, Object>> dataSet = DBManager.getCarData();
        for (Map<String, Object> carData : dataSet)
        {
            String uuid = carData.get("UUID").toString();
            int maxLoad = (int)carData.get("maxload");
            int maxVolume = (int)carData.get("maxvolume");
            double lat = (double)carData.get("lat");
            double lon = (double)carData.get("lon");

            Car car = new Car(uuid, maxLoad, maxVolume, new Coordinate(lat, lon));

            // 两次setState: 第一次将上一状态set为currState，第二次set会自动将其转移至prevState
            car.setState(CarState.AVAILABLE);
            car.setState(CarState.AVAILABLE);
            car.setLoad(0);
            car.setVolume(0);

            carList.put(uuid, car);
        }
    }

    /**
     * 生成随机方位点
     * @return 随机方位点
     */
    private static Coordinate getRandomLocation()
    {
        // 最大半径约2公里
        double maxRadius = 0.12;

        double angle = RANDOM.nextDouble() * 2 * Math.PI;
        double distance = Math.sqrt(RANDOM.nextDouble()) * maxRadius;

        // 计算偏移量（经度需要根据纬度调整）
        double latOffset = distance * Math.sin(angle);
        double lngOffset = distance * Math.cos(angle) ;// Math.cos(Math.toRadians(defaultLocation.lat));

        return new Coordinate(
            defaultLocation.lat + latOffset,
            defaultLocation.lon + lngOffset
        );
    }

    /** 圆周分布 */
    @SuppressWarnings("unused")
    private static Coordinate getCircularLocation(int i)
    {
        // 分布半径
        double radius = 1;

        double angle = 2 * Math.PI * i / 100;
        return new Coordinate(
            defaultLocation.lat + radius * Math.sin(angle),
            defaultLocation.lon + radius * Math.cos(angle)
        );
    }
}
