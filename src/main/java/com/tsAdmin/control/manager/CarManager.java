package com.tsAdmin.control.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.tsAdmin.common.ConfigLoader;
import com.tsAdmin.common.Coordinate;
import com.tsAdmin.control.DBManager;
import com.tsAdmin.control.Main;
import com.tsAdmin.model.Car;
import com.tsAdmin.model.Car.CarState;

public class CarManager
{
    public static Map<String, Car> carList = new HashMap<>();

    private static final int[] LOADS = { 2, 5, 8, 12, 18, 24, 30, 35 };
    private static final int[] VOLUMES = { 12, 16, 32, 48, 64, 86, 108, 140 };
    private static final Coordinate defaultLocation = new Coordinate(30.67646, 104.10248);

    public static void init()
    {
        carList.clear();

        int carNum = ConfigLoader.getInt("CarManager.car_num", 100);
        int realNum = (int)DBManager.getCount("car");

        if (realNum < carNum)
        {
            for (int i = 0; i < carNum - realNum; i++)
            {
                String uuid = UUID.randomUUID().toString().replace("-", "");

                int loadIdx = Main.RANDOM.nextInt(LOADS.length);
                int volumeIdx = Main.RANDOM.nextInt(VOLUMES.length);
                int maxLoad = LOADS[loadIdx];
                int maxVolume = VOLUMES[volumeIdx];

                Car car = new Car(uuid, maxLoad, maxVolume, new Coordinate(getRandomLocation()));
                DBManager.saveCar(car);
            }
        }

        List<Map<String, Object>> dataSet = DBManager.getCarList();
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

        double angle = Main.RANDOM.nextDouble() * 2 * Math.PI;
        double distance = Math.sqrt(Main.RANDOM.nextDouble()) * maxRadius;

        // 计算偏移量
        double latOffset = distance * Math.sin(angle);
        double lngOffset = distance * Math.cos(angle) ;

        return new Coordinate(
            defaultLocation.lat + latOffset,
            defaultLocation.lon + lngOffset
        );
    }
}
