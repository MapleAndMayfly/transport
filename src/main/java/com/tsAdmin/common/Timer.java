package com.tsAdmin.common;

/** 计时器 */
public class Timer
{
    private int time = 0;

    public void setTime(int time) { this.time = time; }
    public int getTime() { return time; }

    public boolean timeUp() { return time <= 0; }

    public void tick()
    {
        time = Math.max(time - 30, 0);
    }
}
