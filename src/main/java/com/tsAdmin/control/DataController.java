package com.tsAdmin.control;

import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;

import com.tsAdmin.model.Car;
import com.tsAdmin.model.CarList;
import com.tsAdmin.model.Demand;
import com.tsAdmin.model.PathNode;

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

    public void getDestination()
    {
        String uuid = getPara("UUID");
        Car car = CarList.carList.get(uuid);
        Map<String, String> dest = null;
        car.tick();
        if(car.getStateTimer().timeUp()){ car.changeState(); }
        switch (car.getState())
        {
            case ORDER_TAKEN:
            {
                PathNode pathnode = car.getFirstNode();
                dest = Map.of(
                    "lat", String.valueOf(pathnode.getDemand().getOrigin().lat),
                    "lon", String.valueOf(pathnode.getDemand().getOrigin().lon)
                );
                car.deleteFirstNode();
                break;
            }
            case TRANSPORTING:
            {
                PathNode pathnode = car.getFirstNode();
                dest = Map.of(
                    "lat", String.valueOf(pathnode.getDemand().getDestination().lat),
                    "lon", String.valueOf(pathnode.getDemand().getDestination().lon)
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
