package com.nihalkonda.cafe.model;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlaceParser {

    public static ArrayList<MyPlace> parsePlaces(String response){
        System.out.println(response);
        ArrayList<MyPlace> list = new ArrayList<MyPlace>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray results = jsonObject.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {

                MyPlace myPlace = new MyPlace();

                JSONObject result = results.getJSONObject(i);

                myPlace.setId(result.getString("id"));

                myPlace.setPlaceId(result.getString("place_id"));

                myPlace.setName(result.getString("name"));

                myPlace.setRating(result.has("rating")?result.getDouble("rating"):0);

                myPlace.setRatingCount(result.has("user_ratings_total")?result.getInt("user_ratings_total"):0);

                myPlace.setIcon(result.has("icon")?result.getString("icon"):"");

                myPlace.setFormattedAddress(result.has("vicinity")?result.getString("vicinity"):"Unknown");

                myPlace.setPriceRange(result.has("price_level")?result.getInt("price_level"):0);

                try {
                    myPlace.setPhotoReference(result.getJSONArray("photos").getJSONObject(0).getString("photo_reference"));
                }catch (Exception e){

                }


                myPlace.setLocation(
                        new LatLng(
                                result.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                                result.getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                        )
                );

                list.add(myPlace);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return list;
    }
}
