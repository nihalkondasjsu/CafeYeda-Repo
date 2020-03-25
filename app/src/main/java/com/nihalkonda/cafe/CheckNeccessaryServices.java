package com.nihalkonda.cafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.gms.common.GoogleApiAvailability;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CheckNeccessaryServices extends AppCompatActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.message)
    TextView message;

    @BindView(R.id.try_again)
    Button tryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_neccessary_services);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        tryAgain.setVisibility(View.INVISIBLE);
        if(isNetworkConnected()){
            if(isGooglePlayServicesAvailable(this)){
                if(isGPSEnabled()){
                    checkForPermissions();
                }else{
                    showErrorMessage("Please enable the GPS and Try again.");
                }
            }else{
                showErrorMessage("Please update/enable the Google Play Service and Try again.");
            }
        }else{
            showErrorMessage("Please resolve the Network Connection Issues and Try again.");
        }
    }

    private void checkForPermissions() {
        startActivity(new Intent(this,AcquireNeccessaryPermissions.class));
        finish();
    }

    private void showErrorMessage(String s) {
        Snackbar.make(findViewById(android.R.id.content), s, Snackbar.LENGTH_LONG).show();
        message.setText(s);
        progressBar.setVisibility(View.INVISIBLE);
        tryAgain.setVisibility(View.VISIBLE);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public boolean isGooglePlayServicesAvailable(Context context){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

    public boolean isGPSEnabled(){
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        return gps_enabled&&network_enabled;
    }

    @OnClick(R.id.try_again)
    public void tryAgain(View view) {
        init();
    }
}