package com.nihalkonda.cafe.home.tabs;

import com.google.android.gms.maps.model.LatLng;
import com.nihalkonda.cafe.model.MyPlace;

import java.util.ArrayList;

public interface UpdateLocationAndPlaces {
    public void newLocation(LatLng newLocation);
    public void newPlacesList(ArrayList<MyPlace> newPlacesList);
}
