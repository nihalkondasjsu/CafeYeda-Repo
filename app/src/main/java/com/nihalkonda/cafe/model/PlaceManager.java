package com.nihalkonda.cafe.model;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;
import com.nihalkonda.cafe.utils.ResponseHandler;
import com.nihalkonda.cafe.utils.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PlaceManager {

    private static PlaceManager instance;

    private ArrayList<MyPlace> placeList;
    private LatLng primaryPlace;

    private PlaceManager(){

    }

    public static PlaceManager getInstance() {
        if(instance == null)
            instance = new PlaceManager();
        return instance;
    }

    public ArrayList<MyPlace> getPlaceList() {
        return placeList;
    }

    private void setPlaceList(ArrayList<MyPlace> placeList) {
        this.placeList = placeList;
    }

    public LatLng getPrimaryPlace() {
        return primaryPlace;
    }

    private void setPrimaryPlace(LatLng primaryPlace) {
        this.primaryPlace = primaryPlace;
    }

    public void loadPlacesNearLocation(Activity activity, LatLng latLng, ResponseHandler responseHandler){
        Server.getInstance().getAllCafes(activity, latLng.latitude, latLng.longitude, 500, null, new ResponseHandler() {
            @Override
            public void onSuccessfulCallback(String response) {
                setPrimaryPlace(latLng);
                setPlaceList(PlaceParser.parsePlaces(response));
                responseHandler.onSuccessfulCallback(response);
            }
        });
    }

}
