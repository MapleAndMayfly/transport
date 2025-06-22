package com.tsAdmin.control;

import java.util.Random;

import com.tsAdmin.model.Car;
import com.tsAdmin.model.Car.CarState;

/**
 * 车辆行为类，因为放在车辆类里会导致很长，故独立出来
 * 每辆车都有一个CarBehaviour对象
 */
public class CarBehaviour
{
    Car car;

    /**
     * @param car 指向拥有行为的车辆自身
     */
    public CarBehaviour(Car car)
    {
        this.car = car;
    }

    public void tick()
    {
        // TODO: 车辆向前进一个周期
    }

    public void changeState()
    {
        Random random = new Random();
        int randomNumber = random.nextInt(100);

        CarState nextState = car.getState();

        switch (car.getState())
        {
            case ORDER_TAKEN:
                if      (randomNumber < 95) nextState = CarState.LOADING;
                else if (randomNumber < 98) nextState = CarState.ORDER_TAKEN;
                else                        nextState = CarState.FREEZE;
                break;

            case LOADING:
                if      (randomNumber < 95) nextState = CarState.TRANSPORTING;
                else if (randomNumber < 97) nextState = CarState.LOADING;
                else if (randomNumber < 99) nextState = CarState.UNLOADING;
                else                        nextState = CarState.FREEZE;
                break;

            case TRANSPORTING:
                if      (randomNumber < 98) nextState = CarState.UNLOADING;
                else if (randomNumber < 99) nextState = CarState.FREEZE;
                else                        nextState = CarState.AVAILABLE;
                break;

            case UNLOADING:
                if      (randomNumber < 97) nextState = CarState.AVAILABLE;
                else                        nextState = CarState.FREEZE;
                break;

            case FREEZE:
                switch (car.getPrevState())
                {
                    case ORDER_TAKEN:
                        if      (randomNumber < 90) nextState = CarState.LOADING;
                        else if (randomNumber < 95) nextState = CarState.ORDER_TAKEN;
                        else                        nextState = CarState.FREEZE;
                        break;

                    case LOADING:
                        if      (randomNumber < 95) nextState = CarState.TRANSPORTING;
                        else if (randomNumber < 97) nextState = CarState.LOADING;
                        else if (randomNumber < 99) nextState = CarState.UNLOADING;
                        else                        nextState = CarState.FREEZE;
                        break;

                    case TRANSPORTING:
                        if      (randomNumber < 98) nextState = CarState.UNLOADING;
                        else if (randomNumber < 99) nextState = CarState.FREEZE;
                        else                        nextState = CarState.AVAILABLE;
                        break;

                    case UNLOADING:
                        if      (randomNumber < 95) nextState = CarState.AVAILABLE;
                        else if (randomNumber < 98) nextState = CarState.FREEZE;
                        else                        nextState = CarState.LOADING;
                        break;

                    default:
                        break;
                }
                break;

            case AVAILABLE:
                break;

            default:
                break;
        }

        // 装货后立马卸货的情况下，下一状态必定是装货
        if (car.getPrevState() == CarState.LOADING && car.getState() == CarState.UNLOADING)
        {
            nextState = CarState.LOADING;
        }

        car.setState(nextState);
        DBManager.updateCarState(car);
    }

    /** 状态转换后获取任务时间 */
    public int getBehaviourTime() 
    {
        switch (car.getState()) 
        {
        case ORDER_TAKEN:
            return 0;   // (int)(car.orderTakenLength() / 1000);
        case TRANSPORTING:
            return 0;   // (int)(car.getDemand().routeLength() / 1000);
        case LOADING:
            return (int)(1.2 * car.getLoad());
        case UNLOADING:
            return (int)(0.9 * car.getLoad());
        case FREEZE:
            return 30;
        default:
            return 0;
        }
    }
}
