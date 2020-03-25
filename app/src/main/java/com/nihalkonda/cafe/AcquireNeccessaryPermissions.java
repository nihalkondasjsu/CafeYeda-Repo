package com.nihalkonda.cafe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nihalkonda.cafe.home.HomeActivity;

import java.util.List;

public class AcquireNeccessaryPermissions extends AppCompatActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.message)
    TextView message;

    @BindView(R.id.try_again)
    Button tryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquire_neccessary_permissions);
        ButterKnife.bind(this);
        allPermissions();

    }

    private void allPermissions() {
        tryAgain.setVisibility(View.INVISIBLE);
        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        android.Manifest.permission.ACCESS_WIFI_STATE,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted() == false){
                    //allPermissions();
                    showErrorMessage("Please grant all permissions and Try again.");
                    tryAgain.setVisibility(View.VISIBLE);
                    tryAgain.setText("Try Again");
                    if (report.isAnyPermissionPermanentlyDenied()) {
                        // check for permanent denial of any permission show alert dialog
                        // navigating to Settings
                        //openSettings();
                        showErrorMessage("Please grant all permissions");
                        tryAgain.setText("Open Settings");
                    }
                }else{
                    //go ahead
                    goToHome();
                }
            }


            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void goToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void showErrorMessage(String s) {
        Snackbar.make(findViewById(android.R.id.content), s, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.try_again)
    public void tryAgain(View v){
        if(tryAgain.getText().toString().equals("Try Again")){
            allPermissions();
        }else{
            openSettings();
            tryAgain.setText("Try Again");
        }
    }

}