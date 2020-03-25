package com.nihalkonda.cafe.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.tabs.TabLayout;
import com.nihalkonda.cafe.MainActivity;
import com.nihalkonda.cafe.R;
import com.nihalkonda.cafe.home.tabs.MapFragment;
import com.nihalkonda.cafe.home.tabs.RatingFragment;
import com.nihalkonda.cafe.home.tabs.TabAdapter;
import com.nihalkonda.cafe.home.tabs.UpdateLocationAndPlaces;
import com.nihalkonda.cafe.model.MyPlace;
import com.nihalkonda.cafe.model.PlaceParser;
import com.nihalkonda.cafe.utils.ResponseHandler;
import com.nihalkonda.cafe.utils.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {


    private static final String TAG = "HomeActivity";
    double longitude=0.0;
    double latitude=0.0;

    @BindView(R.id.search_bar)
    EditText editText;

    private ResponseHandler responseHandler;
    private ArrayList<MyPlace> placesList;

    Fragment mapFragment,ratingFragment;

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());

        mapFragment = new MapFragment();
        ratingFragment = new RatingFragment();

        adapter.addFragment( mapFragment, "MAP");
        adapter.addFragment( ratingFragment, "RATINGS");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        loadLatLng();

        notifyNewLocation(new LatLng(latitude,longitude));

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key), Locale.US);
        }

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        responseHandler = new ResponseHandler() {
            @Override
            public void onSuccessfulCallback(String response) {
                System.out.println(response);
                placesList = PlaceParser.parsePlaces(response);
                //refreshMarkers();
                notifyNewPlacesList(placesList);
            }
        };

        Server.getInstance().getAllCafes(this, latitude, longitude, 500, null,responseHandler);

    }

    public void notifyNewLocation(LatLng newLocation){
        ((UpdateLocationAndPlaces)mapFragment).newLocation(newLocation);
        ((UpdateLocationAndPlaces)ratingFragment).newLocation(newLocation);
    }

    public void notifyNewPlacesList(ArrayList<MyPlace> placesList){
        ((UpdateLocationAndPlaces)mapFragment).newPlacesList(placesList);
        ((UpdateLocationAndPlaces)ratingFragment).newPlacesList(placesList);
    }

    private void performSearch() {
        editText.clearFocus();
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        performSearch(editText.getText().toString());
    }

    private void performSearch(String search) {
        Toast.makeText(this,search,Toast.LENGTH_LONG).show();
        Server.getInstance().getAllCafes(this, latitude, longitude, 500, search,responseHandler);
    }

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
                latitude = final_loc.getLatitude();
                longitude = final_loc.getLongitude();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}