package com.tsAdmin.control.scheduler;


import java.util.List;


import com.tsAdmin.model.Assignment;

/**
 * 调度器接口，定义所有调度器的公共契约
 */
public interface Scheduler {
    List<Assignment> schedule();
}