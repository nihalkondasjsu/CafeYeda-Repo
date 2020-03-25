package com.nihalkonda.cafe.home.tabs;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nihalkonda.cafe.R;
import com.nihalkonda.cafe.model.MyPlace;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback,UpdateLocationAndPlaces {

    GoogleMap googleMap;

    private ArrayList<Marker> markersShown;

    LatLng myLocation;

    private ArrayList<MyPlace> placesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        markersShown = new ArrayList<Marker>();
        placesList = new ArrayList<MyPlace>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View main = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        return main;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        refreshMarkers();
        //googleMap.animateCamera(CameraUpdateFactory.zoomBy(18));
    }

    public void refreshMarkers(){
        if(myLocation==null)
            return;
        if(googleMap==null)
            return;
        clearMarkers();
        showMyMarker(myLocation);
        if(placesList==null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,16));
            return;
        }

        for(MyPlace place:placesList){
            showPlaceMarker(place);
        }

        LatLngBounds.Builder latLngBoundsBuilder = LatLngBounds.builder();
        latLngBoundsBuilder.include(myLocation);
        for(Marker marker:markersShown)
            latLngBoundsBuilder.include(marker.getPosition());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),50));
    }

    private void clearMarkers() {
        for(Marker marker:markersShown)
            marker.remove();
    }

    private void showMyMarker(LatLng myLocation){
        showMarker("MyLocation",myLocation, BitmapDescriptorFactory.HUE_RED);
        drawCircle(myLocation);
    }

    private void showMarker(String name, LatLng latLng, float color) {
        MarkerOptions markerOptions = new MarkerOptions()
                .title(name)
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(color));

        Marker marker = googleMap.addMarker(markerOptions);

        markersShown.add(marker);
    }

    private void showPlaceMarker(MyPlace place) {
        showMarker(place.getName(),place.getLocation(),BitmapDescriptorFactory.HUE_YELLOW);
    }

    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(700);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        googleMap.addCircle(circleOptions);

    }

    @Override
    public void newLocation(LatLng newLocation) {
        this.myLocation = newLocation;
        refreshMarkers();
    }

    @Override
    public void newPlacesList(ArrayList<MyPlace> newPlacesList) {
        this.placesList = newPlacesList;
        refreshMarkers();
    }
}