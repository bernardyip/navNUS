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
    class DirectedEdge
    {
        public int from { get; set; }
        public int to { get; set; }
        public double weight { get; set; }

        public DirectedEdge(int from, int to, double weight)
        {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        public String toString()
        {
            return from + "->" + to + " (" + weight + ")";
        }
    }
}