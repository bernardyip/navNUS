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
    class DijkstraSP
    {
        public bool[] marked;        // has vertex v been relaxed?
        public double[] distTo;         // distTo[v] = length of shortest s->v path
        public DirectedEdge[] edgeTo;   // edgeTo[v] = last edge on shortest s->v path
        public SimplePriorityQueue<DirectedEdge> pq;  // PQ of fringe edges

        public DijkstraSP(EdgeWeightedDigraph G, int s)
        {
            pq = new SimplePriorityQueue<DirectedEdge>();
            marked = new Boolean[G.V];
            edgeTo = new DirectedEdge[G.V];
            distTo = new double[G.V];

            // initialize
            for (int v = 0; v < G.V; v++)
                distTo[v] = Double.MaxValue;
            distTo[s] = 0.0;
            relax(G, s);

            // run Dijkstra's algorithm
            while (!pq.isEmpty())
            {
                DirectedEdge e = pq.Dequeue();
                int v = e.from, w = e.to;
                if (!marked[w]) relax(G, w);   // lazy, so w might already have been relaxed
            }
        }

        // relax vertex v
        private void relax(EdgeWeightedDigraph G, int v)
        {
            marked[v] = true;
            foreach (DirectedEdge e in G.getNeighbours(v))
            {
                int w = e.to;
                if (distTo[w] > distTo[v] + e.weight)
                {
                    distTo[w] = distTo[v] + e.weight;
                    edgeTo[w] = e;
                    pq.Enqueue(e, e.weight);
                }
            }
        }

        // length of shortest path from s to v, infinity if unreachable
        public double distanceTo(int v)
        {
            return distTo[v];
        }

        // is there a path from s to v?
        public bool hasPathTo(int v)
        {
            return marked[v];
        }

        // return view of shortest path from s to v, null if no such path
        public Stack<DirectedEdge> pathTo(int v)
        {
            if (!hasPathTo(v)) return null;
            Stack<DirectedEdge> path = new Stack<DirectedEdge>();
            for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from])
            {
                path.Push(e);
            }
            return path;
        }
    }
}