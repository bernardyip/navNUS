/*
    Created By : Team Prop (Orbital 2016)
 */
package com.navnus.entity;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.navnus.util.StringSimilarity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.FloydWarshall;

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
            InputStream json = context.getAssets().open("vertices");
            JsonReader in = new JsonReader(new InputStreamReader(json));

            //Convert to object
            Gson gson = new Gson();
            vertices = gson.fromJson(in, new TypeToken<HashMap<Integer, Vertex>>(){}.getType());
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadFloydWarshall(Context context) {
        try {
            //Read the data
            InputStream json = context.getAssets().open("floydwarshall");
            JsonReader in = new JsonReader(new InputStreamReader(json));

            //Convert to object
            Gson gson = new Gson();
            graph = gson.fromJson(in, FloydWarshall.class);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Vertex getVertex(int id) {
        return vertices.get(id);
    }

    public static String getVertexNameFromID(int id) {
        return vertices.get(id).name;
    }

    public static int getIdFromName(String name){
        for (int key : vertices.keySet()) {
            Vertex v = vertices.get(key);
            if(v.name.equals(name)){
                return v.id;
            }
        }
        return -1;
    }

    public static ArrayList<String> getSimilarNamesFromName(String name){
        System.out.println("SearchFor:"+ name);
        ArrayList<String> similarNames = new ArrayList<String>();
        for (int key : vertices.keySet()) {
            Vertex v = vertices.get(key);
            double percentage = StringSimilarity.similarity(name, v.name);
            if(percentage>=0.20 || v.name.toLowerCase().contains(name.toLowerCase())){
                similarNames.add(v.name);
                System.out.println(v.name);
            }
        }
        System.out.println("SizeOfResults:"+similarNames.size());
        if(similarNames.size() ==0)
            similarNames.add("No Location Found");
        return similarNames;
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
