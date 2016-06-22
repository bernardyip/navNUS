package com.navnus.entity;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.FloydWarshall;

/**
 * Created by Legend on 15/5/2016.
 */
public class Map {
    public static FloydWarshall graph;
    public static HashMap<Integer, Vertex> vertices;

    public static void initialize(Context context) {
        loadVertices(context);
        loadFloydWarshall(context);
    }

    public static void loadVertices(Context context) {
        try {
            //Read the data
            StringBuilder data = new StringBuilder();
            InputStream json = context.getAssets().open("vertices");
            BufferedReader in = new BufferedReader(new InputStreamReader(json));
            String line;
            while ((line=in.readLine()) != null) {
                data.append(line);
            }
            in.close();

            //Convert to object
            Gson gson = new Gson();
            vertices = gson.fromJson(data.toString(), new TypeToken<HashMap<Integer, Vertex>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadFloydWarshall(Context context) {
        try {
            //Read the data
            StringBuilder data = new StringBuilder();
            InputStream json = context.getAssets().open("floydwarshall");
            BufferedReader in = new BufferedReader(new InputStreamReader(json));
            String line;
            while ((line=in.readLine()) != null) {
                data.append(line);
            }
            in.close();

            //Convert to object
            Gson gson = new Gson();
            graph = gson.fromJson(data.toString(), FloydWarshall.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Vertex getVertex(int id) {
        return vertices.get(id);
    }

    public static LinkedList<DirectedEdge> getPath(int src, int dest) {
        return graph.path(src, dest);
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
