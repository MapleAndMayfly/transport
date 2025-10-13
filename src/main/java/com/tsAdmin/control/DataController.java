package com.tsAdmin.control;

import java.lang.reflect.Method;
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
    public void getPoiData()
    {
        String type = getPara("type");
        List<Map<String, String>> dataList = DBManager.getPoiData(type);
        renderJson(JsonKit.toJson(dataList));
    }

    public void getCarData()
    {
        List<Map<String, String>> posList = DBManager.getCarData();
        renderJson(JsonKit.toJson(posList));
    }

    /**
     * 获取仪表盘数据
     */
    public void getDashboardData()
    {
        // XXX: 这里看胡少怎么写，如果一次性获取所有就在后端遍历车辆for (Car car : CarList.carList)，返回ArrayList<Map<String, String>>
        // XXX: 如果一辆辆获取，前端遍历的话就根据uuid获取车辆uuid = getPara("UUID"); car = CarList.carList.get(uuid)，返回Map<String, String>
        Car car = new Car(null);    // FIXME: temp code
        CarStatistics statistics = car.getStatistics();
        Map<String, String> data = new HashMap<>();

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
        }
        catch (Exception e)
        {
            System.err.println("获取统计数据失败: " + e.getMessage());
        }
        renderJson(JsonKit.toJson(data));
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

    private double roundTo3(double d)
    {
        return Math.round(d * 1000.0) / 1000.0;
    }

    /**
     * 前端尝试获取特定车辆的下一个目的地时调用，是车辆更新的关键函数
     */
    public void getDestination()
    {
        String uuid = getPara("UUID");
        Car car = CarList.carList.get(uuid);
        Map<String, Double> dest = null;

        // 车辆计时器滴答一次并在计时器归零时进行车辆状态转换
        car.tick();
        if(car.getStateTimer().timeUp()) car.changeState();

        // 仅在车辆进入了接单行驶/运货行驶状态时给dest赋值，其他状态返回的dest为null
        switch (car.getState())
        {
            case ORDER_TAKEN:
            {
                PathNode pathnode = car.fetchFirstNode();
                dest = Map.of(
                    "lng", pathnode.getDemand().getOrigin().lon,
                    "lat", pathnode.getDemand().getOrigin().lat
                );
                break;
            }

            case TRANSPORTING:
            {
                PathNode pathnode = car.fetchFirstNode();
                dest = Map.of(
                    "lng", pathnode.getDemand().getDestination().lon,
                    "lat", pathnode.getDemand().getDestination().lat
                );
                break;
            }

            default:
                break;
        }
        renderJson(JsonKit.toJson(dest));
    }
}
