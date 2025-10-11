package com.tsAdmin.common;

/** 计时器 */
public class Timer
{
    private static final int TICK_SPEED = ConfigLoader.getInt("Timer.tick_speed", 20);
    private int time = 0;

    public void setTime(int time) { this.time = time; }
    public int getTime() { return time; }

    public boolean timeUp() { return time == 0; }

    public void tick()
    {
        time = Math.max(time - Math.max(TICK_SPEED, 0), 0);
    }
}
