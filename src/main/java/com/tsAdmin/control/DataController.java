package com.tsAdmin.control;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.tsAdmin.common.PathNode;
import com.tsAdmin.model.car.Car;
import com.tsAdmin.model.car.CarList;
import com.tsAdmin.model.car.CarStatistics;

/**
 * 数据控制器
 * 主要处理前端发出的请求，返回JSON数据
 */
public class DataController extends Controller
{
    /**
     * 获取所有POI数据
     * <p>返回数据格式：
     * {{"UUID": {@code String}, "name": {@code String}, "lat": {@code Double}, "lon": {@code Double}}, {...}, ...}
     */
    public void getPoiData()
    {
        String type = getPara("type");
        List<Map<String, Object>> dataList = DBManager.getPoiData(type);
        renderJson(JsonKit.toJson(dataList));
    }

    /**
     * 获取所有车辆数据
     * <p>返回数据格式：
     * {{"UUID": {@code String}, "type": {@code String}, "maxload": {@code Integer}, "maxvolume": {@code Integer}, "lat": {@code Double}, "lon": {@code Double}}, {...}, ...}
     */
    public void getCarData()
    {
        List<Map<String, Object>> posList = DBManager.getCarData();
        renderJson(JsonKit.toJson(posList));
    }

    /**
     * 获取仪表盘数据
     * TODO: 可优化
     */
    public void getDashboardData()
    {
        List<Map<String,String>> carData = new ArrayList<>();
        Double cycleCost = 0.0;
        for(Car car : CarList.carList.values())
        {
            CarStatistics statistics = car.getStatistics();
            Map<String, String> data = new HashMap<>();
            data.put("UUID", car.getUUID());
            cycleCost += statistics.getCost();
            try
            {
                // 获取 CarStatistics 类的所有 Getter
                Method[] methods = CarStatistics.class.getMethods();
                for (Method method : methods)
                {
                    if (method.getName().startsWith("get"))
                    {
                        // 去掉 get 前缀，首字母小写
                        String methodName = method.getName();
                        String varName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);

                        // 调用 Getter 方法获取值并转换为字符串
                        Object value = method.invoke(statistics);
                        data.put(varName, String.valueOf(value));
                    }
                }
                carData.add(data);
            }
            catch (Exception e)
            {
                System.err.println("获取统计数据失败: " + e.getMessage());
            }
        }

        Map<Double,List<Map<String,String>>> finaldata = new HashMap<>();
        finaldata.put(cycleCost, carData);
        renderJson(JsonKit.toJson(finaldata));
    }

    // XXX: 比较页面的可以先不整，先把仪表盘整出来
    // FIXME: 修改数据获取
    // public void getComparisonData()
    // {
    //     Random rand = new Random();
    
    //     globalPeriod++;
    
    //     // 模拟系统成本（系统维度）
    //     double newSystemCost = 0.1 + rand.nextDouble() * 1.11;
    //     systemCostRatios.add(roundTo3(newSystemCost));
    //     if (systemCostRatios.size() > MAX_PERIOD) systemCostRatios.remove(0);
    
    //     // 定义所有指标
    //     String[] keys = {
    //         "totalDistance", "waitingTime", "emptyDistance", "utilization",
    //         "transportingTime", "idleRatio", "totalWeight", "wastedWeight", "tripCount"
    //     };
    //     String[] names = {
    //         "总行驶距离", "等待时间", "空驶里程", "利用率",
    //         "运输时间", "空闲比", "运输重量", "浪费载重", "运输次数"
    //     };
    
    //     // 初始化每个车辆的每个指标列表
    //     for (String key : keys)
    //     {
    //         for (String vid : vehicleIds)
    //         {
    //             String compoundKey = key + "_" + vid;
    //             vehicleRatioMap.putIfAbsent(compoundKey, new ArrayList<>());
    //             List<Double> list = vehicleRatioMap.get(compoundKey);
    //             double val;
    //             switch (key)
    //             {
    //                 case "totalDistance" -> val = 0.1 + rand.nextDouble() * 0.88+rand.nextDouble() * 0.09;  // 0.81 ~ 1.2
    //                 case "waitingTime" -> val = 0.12 + rand.nextDouble() * 0.75+rand.nextDouble() * 0.09;   // 0.72 ~ 1.03
    //                 case "emptyDistance" -> val = 0.35 + rand.nextDouble() * 0.60+rand.nextDouble() * 0.09; // 0.65 ~ 1.06
    //                 case "utilization" -> val = 0.34+rand.nextDouble() * 0.86+rand.nextDouble() * 0.09;
    //                 case "transportingTime" -> val = 0.92 + rand.nextDouble() * 0.15+rand.nextDouble() * 0.09;
    //                 case "idleRatio" -> val = 0.15 + rand.nextDouble() * 0.91+rand.nextDouble() * 0.09;
    //                 case "totalWeight" -> val = 0.2 + rand.nextDouble() * 0.92+rand.nextDouble() * 0.09;
    //                 case "wastedWeight" -> val = 0.27 + rand.nextDouble() * 0.86+rand.nextDouble() * 0.09;
    //                 case "tripCount" -> val = 0.1 + rand.nextDouble() * 0.72+rand.nextDouble() * 0.09;
    //                 default -> val = 0.9 + rand.nextDouble() * 0.1;
    //             }
                
    //             list.add(roundTo3(val));
    //             if (list.size() > MAX_PERIOD) list.remove(0);
    //         }
    //     }
    
    //     // 周期范围控制
    //     int count = systemCostRatios.size();
    //     int displayCount = 10;
    //     int startPeriod = Math.max(1, globalPeriod - displayCount + 1);
    
    //     List<Map<String, Object>> metrics = new ArrayList<>();
    
    //     // 系统总费用
    //     List<Double> costValues = systemCostRatios.subList(Math.max(0, count - displayCount), count);
    //     List<Map<String, Object>> systemSeries = List.of(
    //         Map.of("vehicle", "SYSTEM", "values", costValues)
    //     );
    //     metrics.add(Map.of("metric", "cost", "name", "系统cost", "series", systemSeries));
    
    //     // 其他9个指标
    //     for (int i = 0; i < keys.length; i++)
    //     {
    //         String key = keys[i];
    //         String name = names[i];
    //         List<Map<String, Object>> series = new ArrayList<>();
    //         for (String vid : vehicleIds) {
    //             String compoundKey = key + "_" + vid;
    //             List<Double> full = vehicleRatioMap.get(compoundKey);
    //             List<Double> values = full.subList(Math.max(0, full.size() - displayCount), full.size());
    //             series.add(Map.of("vehicle", vid, "values", values));
    //         }
    //         metrics.add(Map.of("metric", key, "name", name, "series", series));
    //     }
    
    //     renderJson(Map.of(
    //         "startPeriod", startPeriod,
    //         "metrics", metrics
    //     ));
    // }

    // private double roundTo3(double d)
    // {
    //     return Math.round(d * 1000.0) / 1000.0;
    // }

    /**
     * 前端尝试获取特定车辆的下一个目的地时调用，是车辆更新的关键函数
     * <p>在车辆滴答一次后，若进入需要规划路线的状态，则返回目的地坐标，否则返回{@code null}
     * <p>返回坐标格式：{"lat": {@code double}, "lon": {@code double}}
     */
    public void getDestination()
    {
        String uuid = getPara("UUID");
        Car car = CarList.carList.get(uuid);
        Map<String, Double> dest = null;

        // 车辆计时器滴答一次并在计时器归零时进行车辆状态转换
        car.tick(car.getState());
        if(car.getStateTimer().timeUp()) car.changeState();

        // 仅在车辆进入了接单行驶/运货行驶状态时给dest赋值，其他状态返回的dest为null
        switch (car.getState())
        {
            case ORDER_TAKEN:
            {
                PathNode pathnode = car.fetchFirstNode();
                dest = Map.of(
                    "lat", pathnode.getDemand().getOrigin().lat,
                    "lon", pathnode.getDemand().getOrigin().lon
                );
                break;
            }

            case TRANSPORTING:
            {
                PathNode pathnode = car.fetchFirstNode();
                dest = Map.of(
                    "lat", pathnode.getDemand().getDestination().lat,
                    "lon", pathnode.getDemand().getDestination().lon
                );
                break;
            }

            default:
                break;
        }
        renderJson(JsonKit.toJson(dest));
    }
}
