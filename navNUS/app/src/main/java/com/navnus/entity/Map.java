package com.navnus.entity;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;

/**
 * Created by Legend on 15/5/2016.
 */
public class Map {
    public static int numberOfVertices = 826;
    public static EdgeWeightedDigraph graph;
    public static Vertice[] vertices;

    public static void initialize() {
        graph = new EdgeWeightedDigraph(numberOfVertices); //To modify later on
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

    public static Iterable<DirectedEdge> getNeighbours(int verticeId) {
        return graph.adj(verticeId);
    }

    public static void loadEdges(String file) {

    }

    /*
     * Estimates distance between two points in latitude and longitude
     *
     * lat1, lon1 Start point, lat2, lon2 End point
     * @returns Distance in Meters
     */
    public static double estimateDistance(Vertice vertice1, Vertice vertice2) {
        final int R = 6371; // Radius of the earth
        double lat1 = vertice1.latitude;
        double lon1 = vertice1.longitude;
        double lat2 = vertice2.latitude;
        double lon2 = vertice2.longitude;

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return Math.sqrt(distance);
    }

    public static Stack<DirectedEdge> getPath(int startVerticeId, int endVerticeId) {
        LinkedList<DirectedEdge> openList = new LinkedList<DirectedEdge>();
        HashSet<DirectedEdge> closedList = new HashSet<DirectedEdge>();
        LinkedList<DirectedEdge> result = new LinkedList<DirectedEdge>();
        int min, max, length;

        //Iterate through openlist until none are left
        while (!openList.isEmpty()) {
            min = -1;
            max = Integer.MAX_VALUE;

            for(int i=0; i<openList.size(); i++) {
                DirectedEdge edge = openList.removeFirst();
            }

        }
        return null;
    }
}
