package com.navnus.entity;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.princeton.cs.algs4.EdgeWeightedGraph;

/**
 * Created by Legend on 15/5/2016.
 */
public class Map {
    public static int numberOfVertices = 826;
    public static EdgeWeightedGraph graph;
    public static Vertice[] vertices;

    /*
     * Estimates distance between two points in latitude and longitude
     *
     * lat1, lon1 Start point, lat2, lon2 End point
     * @returns Distance in Meters
     */
    public static double estimateDistance(int vertice1Id, int vertice2Id) {
        final int R = 6371; // Radius of the earth
        double lat1 = vertices[vertice1Id].latitude;
        double lon1 = vertices[vertice1Id].longitude;
        double lat2 = vertices[vertice2Id].latitude;
        double lon2 = vertices[vertice2Id].longitude;

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return Math.sqrt(distance);
    }

    public static void initialize() {
        graph = new EdgeWeightedGraph(numberOfVertices); //To modify later on
        vertices = new Vertice[numberOfVertices];
    }

    public static void loadVertices(Context context) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("vertices"), "UTF-8"));
            // do reading, usually loop until end of file reading
            String fileLine;
            int index = 0;
            while ((fileLine = reader.readLine()) != null) {
                String[] vertice = fileLine.split(";");
                Vertice newVertice = new Vertice(index, vertice[1], Double.parseDouble(vertice[2]), Double.parseDouble(vertice[3]));
                vertices[index++] = newVertice;
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }

    public void loadEdges(String file) {

    }
}
