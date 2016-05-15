package com.navnus.entity;

/**
 * Created by Legend on 15/5/2016.
 */
public class Vertice {
    public int id;
    public String name;
    public double latitude;
    public double longitude;

    public Vertice(int id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return id + " : " + name + " (" + latitude + ", " + longitude + ")";
    }
}
