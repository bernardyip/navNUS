using System;
using Android.App;
using Android.Content;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.OS;

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

