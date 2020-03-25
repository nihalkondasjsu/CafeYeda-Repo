package com.nihalkonda.cafe.utils;

import android.app.Activity;

import com.nihalkonda.cafe.R;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Server {

    private static Server instance;

    private OkHttpClient client ;

    private Server(){
        this.client = new OkHttpClient();
    }

    public static Server getInstance(){
        if(instance == null)
            instance = new Server();
        return instance;
    }

    public void getAllCafes(final Activity activity, final double lat, final double lng, final int radius, final String name, final ResponseHandler responseHandler){

        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpUrl.Builder httpBuilder = HttpUrl.parse("https://maps.googleapis.com/maps/api/place/nearbysearch/json").newBuilder();

                httpBuilder.addQueryParameter("key", activity.getResources().getString(R.string.google_maps_key));
                httpBuilder.addQueryParameter("location",lat+","+lng);
                httpBuilder.addQueryParameter("radius", String.valueOf(radius));
                httpBuilder.addQueryParameter("fields","name,rating,geometry,formatted_address");
                httpBuilder.addQueryParameter("type","cafe");

                if(name!=null&&name.equals("")==false)
                    httpBuilder.addQueryParameter("name",name);


                Request request = new Request.Builder().url(httpBuilder.build()).build();
                //client.newCall(request).enqueue(responseCallback);

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    final String responseString = response.body().string();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            responseHandler.onSuccessfulCallback(responseString);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            responseHandler.onSuccessfulCallback(null);
                        }
                    });
                }
            }
        }).start();

    }

    public String getPlaceImage(final Activity activity,String photoReference) {
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1100&photoreference="+photoReference+"&key="+activity.getResources().getString(R.string.google_maps_key);
    }
}
