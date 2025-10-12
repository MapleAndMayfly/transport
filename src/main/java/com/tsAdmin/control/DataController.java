package com.tsAdmin.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.tsAdmin.common.PathNode;
import com.tsAdmin.model.car.Car;
import com.tsAdmin.model.car.CarList;

/**
 * 数据控制器
 * 主要处理前端发出的请求，返回JSON数据
 */
public class DataController extends Controller
{
    private static int globalPeriod = 0;
    private static final int MAX_PERIOD = 100;
    private static final List<Double> systemCostRatios = new ArrayList<>();
    private static final Map<String, List<Double>> vehicleRatioMap = new HashMap<>();
    private static final List<String> vehicleIds = List.of("车1", "车2", "车3", "车4", "车5");
    private static int lastCost = 100;
    // 保留历史总代价
    private static final List<Integer> costHistory = new ArrayList<>();

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

    // FIXME: 修改数据获取
    public void getDashboardData()
    {
        Random rand = new Random();

        // 模拟新数据追加
        lastCost += rand.nextInt(50) - 20;
        costHistory.add(lastCost);

        // 限制最大长度
        if (costHistory.size() > 1000) {
            costHistory.remove(0);
        }
        // 保留最新5个值
        int total = costHistory.size();
        int displayCount = 5;
        int startIndex = Math.max(0, total - displayCount);
        List<Integer> recentHistory = costHistory.subList(startIndex, total);

        // 构造对应的标签 周期16, ..., 周期20
        int startPeriod = total - recentHistory.size() + 1;
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < recentHistory.size(); i++) {
        labels.add("周期" + (startPeriod + i));
        }

        List<Map<String, Object>> carMetrics = new ArrayList<>();
        // 模拟车辆数据
        for (Car car:CarList.carList.values()) 
        {
            Map<String, Object> myMap = new HashMap<>();
            myMap.put("UUID", String.format("车%03d", carMetrics.size()+1));
            myMap.put("status", car.getState());
            myMap.put("lat", car.getPosition().lat);
            myMap.put("lon", car.getPosition().lon);
            myMap.put("tripCount", car.getCarStat().getTripCount());
            myMap.put("totalWeight", car.getCarStat().getTotalWeight());
            myMap.put("totalDistance", car.getCarStat().getTotalDistance());
            myMap.put("waitingTime", car.getCarStat().getWaitingTime());
            myMap.put("emptyDistance", Math.abs(car.getCarStat().getEmptyDistance()));
            myMap.put("totalTime", car.getCarStat().getExistTime());
            myMap.put("transportingTime", car.getCarStat().getTransportingTime());
            myMap.put("idleRatio", car.getCarStat().getFreeRate()*100);//空闲比
            myMap.put("wastedWeight", car.getCarStat().getWastedLoad());
            myMap.put("utilization", car.getCarStat().getutilization());
            carMetrics.add(myMap);
        } 

        renderJson(Map.of(
        "costHistory", recentHistory,
        "labels", labels,
        "carMetrics", carMetrics
        ));
    }

    // FIXME: 修改数据获取
    public void getComparisonData()
    {
        Random rand = new Random();
    
        globalPeriod++;
    
        // 模拟系统成本（系统维度）
        double newSystemCost = 0.1 + rand.nextDouble() * 1.11;
        systemCostRatios.add(roundTo3(newSystemCost));
        if (systemCostRatios.size() > MAX_PERIOD) systemCostRatios.remove(0);
    
        // 定义所有指标
        String[] keys = {
            "totalDistance", "waitingTime", "emptyDistance", "utilization",
            "transportingTime", "idleRatio", "totalWeight", "wastedWeight", "tripCount"
        };
        String[] names = {
            "总行驶距离", "等待时间", "空驶里程", "利用率",
            "运输时间", "空闲比", "运输重量", "浪费载重", "运输次数"
        };
    
        // 初始化每个车辆的每个指标列表
        for (String key : keys)
        {
            for (String vid : vehicleIds)
            {
                String compoundKey = key + "_" + vid;
                vehicleRatioMap.putIfAbsent(compoundKey, new ArrayList<>());
                List<Double> list = vehicleRatioMap.get(compoundKey);
                double val;
                switch (key)
                {
                    case "totalDistance" -> val = 0.1 + rand.nextDouble() * 0.88+rand.nextDouble() * 0.09;  // 0.81 ~ 1.2
                    case "waitingTime" -> val = 0.12 + rand.nextDouble() * 0.75+rand.nextDouble() * 0.09;   // 0.72 ~ 1.03
                    case "emptyDistance" -> val = 0.35 + rand.nextDouble() * 0.60+rand.nextDouble() * 0.09; // 0.65 ~ 1.06
                    case "utilization" -> val = 0.34+rand.nextDouble() * 0.86+rand.nextDouble() * 0.09;
                    case "transportingTime" -> val = 0.92 + rand.nextDouble() * 0.15+rand.nextDouble() * 0.09;
                    case "idleRatio" -> val = 0.15 + rand.nextDouble() * 0.91+rand.nextDouble() * 0.09;
                    case "totalWeight" -> val = 0.2 + rand.nextDouble() * 0.92+rand.nextDouble() * 0.09;
                    case "wastedWeight" -> val = 0.27 + rand.nextDouble() * 0.86+rand.nextDouble() * 0.09;
                    case "tripCount" -> val = 0.1 + rand.nextDouble() * 0.72+rand.nextDouble() * 0.09;
                    default -> val = 0.9 + rand.nextDouble() * 0.1;
                }
                
                list.add(roundTo3(val));
                if (list.size() > MAX_PERIOD) list.remove(0);
            }
        }
    
        // 周期范围控制
        int count = systemCostRatios.size();
        int displayCount = 10;
        int startPeriod = Math.max(1, globalPeriod - displayCount + 1);
    
        List<Map<String, Object>> metrics = new ArrayList<>();
    
        // 系统总费用
        List<Double> costValues = systemCostRatios.subList(Math.max(0, count - displayCount), count);
        List<Map<String, Object>> systemSeries = List.of(
            Map.of("vehicle", "SYSTEM", "values", costValues)
        );
        metrics.add(Map.of("metric", "cost", "name", "系统cost", "series", systemSeries));
    
        // 其他9个指标
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            String name = names[i];
            List<Map<String, Object>> series = new ArrayList<>();
            for (String vid : vehicleIds) {
                String compoundKey = key + "_" + vid;
                List<Double> full = vehicleRatioMap.get(compoundKey);
                List<Double> values = full.subList(Math.max(0, full.size() - displayCount), full.size());
                series.add(Map.of("vehicle", vid, "values", values));
            }
            metrics.add(Map.of("metric", key, "name", name, "series", series));
        }
    
        renderJson(Map.of(
            "startPeriod", startPeriod,
            "metrics", metrics
        ));
    }

    private double roundTo3(double d)
    {
        return Math.round(d * 1000.0) / 1000.0;
    }

    /**
     * 调用后车辆计时器tick一次，若计时器归零
     */
    public void getDestination()
    {
        String uuid = getPara("UUID");
        Car car = CarList.carList.get(uuid);
        Map<String, Double> dest = null;

        car.tick();
        if(car.getStateTimer().timeUp()) { car.changeState(); }

        switch (car.getState())
        {
            case ORDER_TAKEN:
            {
                PathNode pathnode = car.getFirstNode();
                dest = Map.of(
                    "lng", pathnode.getDemand().getOrigin().lon,
                    "lat", pathnode.getDemand().getOrigin().lat
                );
                car.deleteFirstNode();
                break;
            }

            case TRANSPORTING:
            {
                PathNode pathnode = car.getFirstNode();
                dest = Map.of(
                    "lng", pathnode.getDemand().getDestination().lon,
                    "lat", pathnode.getDemand().getDestination().lat
                );
                car.deleteFirstNode();
                break;
            }

            default:
                break;
        }
        renderJson(JsonKit.toJson(dest));
    }
}
