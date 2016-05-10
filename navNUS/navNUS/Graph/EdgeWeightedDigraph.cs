using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;

namespace navNUS.Graph
{
    class EdgeWeightedDigraph
    {
        public int V { get; set; }         // Number of Vertices
        public int E { get; set; }         // Number of Edges
        public List<DirectedEdge>[] adj;   // Adjacency List for Graph
        public int[] indegree;             // indegree[v] = indegree of vertex v

        public EdgeWeightedDigraph(int v)
        {
            this.V = v;
            indegree = new int[v];
            adj = new List<DirectedEdge>[v];
            for (int i = 0; i < v; i++)
            {
                adj[i] = new List<DirectedEdge>();
            }
        }

        public List<DirectedEdge> getNeighbours(int current)
        {
            return adj[current];
        }

        public void addEdge(int from, int to, double weight)
        {
            DirectedEdge edge = new DirectedEdge(from, to, weight);
            adj[from].Add(edge);
            indegree[to]++;
            E++;
        }

        override
        public String ToString()
        {
            String graphString = "";
            for (int i=0; i<V; i++)
            {
                graphString += i + ": ";
                foreach (DirectedEdge edge in this.getNeighbours(i))
                {
                    graphString += edge.to + " ";
                }
                graphString += "\n";
            }
            return graphString;
        }
    }
}