package IdentifyScope;

/**
 * Created by liangchenzhou on 6/07/16.
 */
public class Point
{
    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double x;
    public double y;

    @Override
    public String toString()
    {
        return String.format("(%.8f,%.7f)", x, y);
    }
}
