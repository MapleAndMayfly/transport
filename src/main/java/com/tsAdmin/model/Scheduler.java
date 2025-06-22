package com.tsAdmin.model;

import java.util.List;

import com.tsAdmin.control.SchedulerStrategy;

public class Scheduler
{
    private SchedulerStrategy strategy;

    public Scheduler(SchedulerStrategy strategy)
    {
        this.strategy = strategy;
    }

    public List<Assignment> schedule(List<Car> cars, List<Demand> demands)
    {
        return strategy.schedule(cars, demands);
    }
}
