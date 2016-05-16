package com.navnus.entity;

/**
 * Created by Legend on 15/5/2016.
 */
public class Vertice {
    public int id;
    public String name;
    public GeoCoordinate coordinate;

    public Vertice(int id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.coordinate = new GeoCoordinate(latitude, longitude);
    }

    @Override
    public String toString() {
        return id + " : " + name + " (" + coordinate.latitude + ", " + coordinate.longitude + ")";
    }
}
