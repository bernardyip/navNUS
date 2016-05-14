package com.navnus.standalone_scripts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

/**
 * Created by Legend on 14/5/2016.
 */
public class VerticeRetriever {
    public static void main(String[] args) throws Exception{
        //Get the places
        URL url = new URL("http://map.nus.edu.sg/index.php/search/");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        StringBuffer response = new StringBuffer();
        String line;
        while ((line=in.readLine()) != null) {
            response.append(line);
        }

        String[] dataArray = response.toString().split("stdClass Object");
        int id = 0;
        HashSet<String> geoCoord = new HashSet<String>();
        for (String data : dataArray) {
            if (data.contains("Kent Ridge") &&
                    data.contains("[long]") &&
                    data.contains("[lat]")) {
                String key = "[place_name] => ";
                String spacer = "[";
                String place_name = data.substring(data.indexOf(key)+key.length(), data.indexOf(spacer, data.indexOf(key)+key.length())).trim();

                //Regex is to remove trailing zeroes
                key = "[long] => ";
                String longitude = data.substring(data.indexOf(key)+key.length(), data.indexOf(spacer, data.indexOf(key)+key.length())).trim().replaceAll("()\\.0+$|(\\..+?)0+$", "$2");

                //Regex is to remove trailing zeroes
                key = "[lat] => ";
                String latitude = data.substring(data.indexOf(key)+key.length(), data.indexOf(spacer, data.indexOf(key)+key.length())).trim().replaceAll("()\\.0+$|(\\..+?)0+$", "$2");

                key = "[building_name] => ";
                String building_name = data.substring(data.indexOf(key)+key.length(), data.indexOf(spacer, data.indexOf(key)+key.length())).trim();

                //Usually the last option. Thus, ends with a )
                key = "[tbl] => ";
                String type = data.substring(data.indexOf(key)+key.length(), data.indexOf(")", data.indexOf(key)+key.length())).trim();

                //Append location of ATM
                if (type.equals("atm")) {
                    place_name += " @ " + building_name;
                }

                //Ensure no duplicate of the same building
                String geo = latitude+":"+longitude;
                if (!longitude.equals("") && !latitude.equals("") && !geoCoord.contains(geo)) {
                    System.out.printf("%d : %s;%s;%s\n", id++, place_name, latitude, longitude);
                    //geoCoord.add(geo);
                    //For plotting
                    //System.out.printf("%s,%s\n", latitude, longitude);
                }
            }
        }
    }
}
