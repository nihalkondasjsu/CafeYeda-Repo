package com.nihalkonda.cafe.home.navdr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.nihalkonda.cafe.MainActivity;
import com.nihalkonda.cafe.R;
import com.nihalkonda.cafe.model.MyPlace;
import com.nihalkonda.cafe.model.PlaceManager;
import com.nihalkonda.cafe.utils.ResponseHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MapActivity extends BaseNavigationActivity implements OnMapReadyCallback {

    private ResponseHandler responseHandler;
    GoogleMap googleMap;
    LatLng myLocation;
    private ArrayList<Marker> markersShown;

    private ArrayList<MyPlace> placeList;
    private LatLng primaryPlace;
    private Circle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.content_map);

        markersShown = new ArrayList<Marker>();

        responseHandler = new ResponseHandler() {
            @Override
            public void onSuccessfulCallback(String response) {
                loadMarkers(PlaceManager.getInstance().getPlaceList(),PlaceManager.getInstance().getPrimaryPlace());
            }
        };

        loadLatLng();

        setFloatingButton(
                getResources().getDrawable(android.R.drawable.ic_menu_mylocation),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(googleMap==null)
                            return;
                        if(myLocation==null)
                            return;

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,16));
                    }
                }
        );

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key), Locale.US);
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(MapActivity.this.getLocalClassName(), "Place: " + place.getName() + ", " + place.getId());
                primaryPlace=null;
                placeList=null;
                PlaceManager.getInstance().loadPlacesNearLocation(MapActivity.this, place.getLatLng(), responseHandler);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(MapActivity.this.getLocalClassName(), "An error occurred: " + status);
            }
        });

        PlaceManager.getInstance().loadPlacesNearLocation(MapActivity.this,myLocation, responseHandler);

    }

    private void loadMarkers(ArrayList<MyPlace> placeList, LatLng primaryPlace) {
        this.placeList=placeList;
        this.primaryPlace=primaryPlace;
        refreshMarkers();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        refreshMarkers();
        //googleMap.animateCamera(CameraUpdateFactory.zoomBy(18));
    }

    public void refreshMarkers(){
        System.out.println("refreshMarkers");
        System.out.println(myLocation);
        System.out.println(googleMap);
        System.out.println(placeList);
        if(myLocation==null)
            return;
        if(googleMap==null)
            return;
        clearMarkers();
        showMyMarker(myLocation);
        if(placeList==null) {
            //drawCircle(myLocation);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,16));
            return;
        }

        System.out.println(placeList.size());

        for(MyPlace place:placeList){
            System.out.println(place);
            showPlaceMarker(place);
        }

        LatLngBounds.Builder latLngBoundsBuilder = LatLngBounds.builder();
        latLngBoundsBuilder.include(myLocation);
        for(Marker marker:markersShown)
            latLngBoundsBuilder.include(marker.getPosition());
        //drawCircle(primaryPlace);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),50));
    }

    private void clearMarkers() {
        for(Marker marker:markersShown)
            marker.remove();
    }

    private void showMyMarker(LatLng myLocation){
        showMarker("My Location",myLocation, BitmapDescriptorFactory.HUE_GREEN);
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
        showMarker(place.getName(),place.getLocation(),
                place.getLocation().equals(primaryPlace)?
                        BitmapDescriptorFactory.HUE_RED:
                        BitmapDescriptorFactory.HUE_BLUE
        );
    }

//    private void drawCircle(LatLng point){
//
//        if(circle!=null)
//            circle.remove();
//
//        // Instantiating CircleOptions to draw a circle around the marker
//        CircleOptions circleOptions = new CircleOptions();
//
//        // Specifying the center of the circle
//        circleOptions.center(point);
//
//        // Radius of the circle
//        circleOptions.radius(700);
//
//        // Border color of the circle
//        circleOptions.strokeColor(Color.BLACK);
//
//        // Fill color of the circle
//        circleOptions.fillColor(0x10ff0000);
//
//        // Border width of the circle
//        circleOptions.strokeWidth(2);
//
//        // Adding the circle to the GoogleMap
//        circle =googleMap.addCircle(circleOptions);
//
//    }

    private void loadLatLng() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

            startActivity(new Intent(this, MainActivity.class));
            finish();

            return;
        }

        try {
            Location gps_loc;
            Location network_loc;
            Location final_loc;

            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            final_loc = gps_loc!=null ? gps_loc : network_loc;

            if(final_loc!=null){
                myLocation = new LatLng(final_loc.getLatitude(),final_loc.getLongitude());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}