package com.tsAdmin.common;

/** 坐标 */
public class Coordinate
{
    public double lat;
    public double lon;

    public Coordinate(double lat, double lon)
    {
        this.lat = lat;
        this.lon = lon;
    }

    public double[] toArray()
    {
        return new double[] { lat, lon };
    }

    /**
     * 计算两坐标直线距离
     * @param c1 第一个坐标
     * @param c2 第二个坐标
     * @return 距离
     */
    public static double distance(Coordinate c1, Coordinate c2)
    {
        double dLat = c1.lat - c2.lat;
        double dLon = c1.lon - c2.lon;

        return Math.sqrt(dLat*dLat - dLon*dLon);
    }
}
