package com.tsAdmin.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;

import com.tsAdmin.model.Car;
import com.tsAdmin.model.Car.CarState;
import com.tsAdmin.model.CarList;

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
        System.out.println(JsonKit.toJson(posList));
        renderJson(JsonKit.toJson(posList));
    }

    public void modifyCarStatus()
    {
        List<String> cachedRoute = new ArrayList<>();

        for (Car car : CarList.carList.values())
        {
            if (!(car.getState() == CarState.AVAILABLE)) 
            {
                car.getStateTimer().tick();
                if (car.getStateTimer().timeUp())
                {
                    car.getBehaviour().changeState();
                    car.getStateTimer().setTime(car.getBehaviour().getBehaviourTime());
                    DBManager.updateCarTime(car);
                }
            }
            /*
            else
            {
                for (Demand demand : DemandList.demandList)
                {
                    if (car.getBehaviour().doAccept(demand))
                    {
                        car.setState(CarState.ORDER_TAKEN);
                        car.setDemand(demand);
                        car.setLoad();
                        car.getStateTimer().setTime(car.getBehaviour().getBehaviourTime());
                        DBManager.updateCarState(car, CarList.carList.indexOf(car));
                        DBManager.updateCarDemandIdx(car, CarList.carList.indexOf(car));
                        DBManager.updateCarTime(car, CarList.carList.indexOf(car));
                        int remainQuantity = Math.max(demand.getQuantity() - car.getMaxLoad(), 0);
                        demand.setQuantity(remainQuantity);
                        DBManager.updateDemandQuantity(demand);
                        //删除订单在需求完成后。@see CarBehaviour.changeState()
                        String json = String.format(
                            "{ 'status':'unfinished'" +
                            ", 'carIndex':" + JsonKit.toJson(CarList.carList.indexOf(car)) +
                            ", 'carPosition':" + JsonKit.toJson(car.getPosition().toArray()) +
                            ", 'demandOrigin':" + JsonKit.toJson(demand.getOrigin().toArray()) +
                            ", 'demandDestination':" + JsonKit.toJson(demand.getDestination().toArray()) +
                            "}"
                        );

                        cachedRoute.add(json); // 添加到当前周期数据
                        break;
                    }
                }
            } */
        }
        renderJson(cachedRoute);
    }
}
