package com.navnus.entity;

import android.content.Context;
import com.navnus.utility.Util;
import com.navnus.entity.Vertex;
import java.io.InputStream;
import java.util.LinkedList;
import edu.princeton.cs.algs4.FloydWarshall;

/**
 * Created by Legend on 15/5/2016.
 */
public class Map {
    public static FloydWarshall graph;
    public static LinkedList<Vertex> vertices;

    public static void initialize(Context context) {
        loadVertices(context);
        loadFloydWarshall(context);
    }

    public static void loadVertices(Context context) {
        try {
            InputStream is = context.getAssets().open("vertices");
            vertices = (LinkedList<Vertex>) Util.readSerializable(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadFloydWarshall(Context context) {
        try {
            InputStream is = context.getAssets().open("floydwarshall");
            graph = (FloydWarshall) Util.readSerializable(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Estimates distance between two points in latitude and longitude
     *
     * lat1, lon1 Start point, lat2, lon2 End point
     * @returns Distance in Meters
     */
    public static double estimateDistance(Vertex vertice1, Vertex vertice2) {
        final int R = 6378137; // Radius of the earth
        double lat1 = vertice1.coordinate.latitude;
        double lon1 = vertice1.coordinate.longitude;
        double lat2 = vertice2.coordinate.latitude;
        double lon2 = vertice2.coordinate.longitude;

        Double latDistance = lat2 - lat1;
        Double lonDistance = lon2 - lon1;
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to meters

        return Math.sqrt(distance);
    }
}
