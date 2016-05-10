using System;
using Android.App;
using Android.Content;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.OS;
using navNUS.Graph;
using System.Collections.Generic;

namespace navNUS
{
    [Activity(Label = "navNUS", MainLauncher = true, Icon = "@drawable/icon")]
    public class MainActivity : Activity
    {
        int count = 1;

        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);

            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.Main);

            //Unit test for graphs
            EdgeWeightedDigraph graph = new EdgeWeightedDigraph(5);
            Console.WriteLine("Hello Test");
            graph.addEdge(0, 1, 3);
            graph.addEdge(0, 3, 1);
            graph.addEdge(1, 2, 0);
            graph.addEdge(2, 3, 0);
            graph.addEdge(3, 4, 1);
            graph.addEdge(4, 0, 5);
			graph.addEdge(4, 1, 1);
            Console.WriteLine(graph.ToString());

            //Find shortest path from 0
            int from = 0;
            DijkstraSP shortestPath = new DijkstraSP(graph, from);
            Stack<DirectedEdge> stack = shortestPath.pathTo(4);
            Console.Write("Testing Shortest Path " + from + " ");
            foreach (DirectedEdge edge in stack)
            {
                Console.Write(edge.to + " ");
            }
            Console.WriteLine("("+shortestPath.distanceTo(4)+")");
        }

        [Java.Interop.Export("clickme")]
        public void clickMe(View v)
        {
            FindViewById<Button>(Resource.Id.MyButton).Text = string.Format("{0} clicks!", count++);
        }

        [Java.Interop.Export("test")]
        public void test(View v)
        {
            Toast.MakeText(this, "Hello", ToastLength.Long).Show();
        }
    }
}

