package com.indoor_navigation.model;

import java.io.Serializable;

/**
 * Created by Ö¾ºÀ on 2015/7/22.
 */
public class Point implements Serializable {
    private String name;
    private double x;
    private double y;
    private int    z;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
