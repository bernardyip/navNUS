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
