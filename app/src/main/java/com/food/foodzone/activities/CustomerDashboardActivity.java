package com.food.foodzone.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.food.foodzone.R;
import com.food.foodzone.common.*;
import com.food.foodzone.utils.PreferenceUtils;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class CustomerDashboardActivity extends BaseActivity {

    private View llDashboard;
    private TextView tvUserName, tvDineIn, tvTakeOut, tvMenu;
    private LocationManager locationManager;
    private Location mLocation;

    @Override
    public void initialise() {
        llDashboard = inflater.inflate(R.layout.customer_dashboard_layout, null);
        addBodyView(llDashboard);
        flCart.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.GONE);
        ivMenu.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        customerLeftMenu();
        initialiseControls();
        String welcomeText = "Hi "+preferenceUtils.getStringFromPreference(PreferenceUtils.UserName, "")+", Welcome to Food Zone";
        tvUserName.setText(welcomeText);
        tvDineIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AppConstants.from.equalsIgnoreCase(AppConstants.TakeOut) &&
                        AppConstants.Cart_Items != null && AppConstants.Cart_Items.size() > 0) {
                    showAppCompatAlert("", "Your previous cart items will be cleared, Do you want to proceed?", "Proceed", "Cancel", "DineIn", false);
                }
                else {
                    Intent intent = new Intent(CustomerDashboardActivity.this, TablesListActivity.class);
                    intent.putExtra(AppConstants.From, AppConstants.DineIn);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        });
        tvTakeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//                        !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
//                    showAppCompatAlert("GPS settings", "GPS is not enabled. Please enable your GPS, from settings menu?", "Enable", "Cancel", "EnableGPS", false);
//                }
//                else {
//                    if(isFoodZoneArea(mLocation)) {
                if((AppConstants.from.equalsIgnoreCase(AppConstants.DineInNow)
                        || AppConstants.from.equalsIgnoreCase(AppConstants.DineInLater))&&
                        AppConstants.Cart_Items != null && AppConstants.Cart_Items.size() > 0) {
                    showAppCompatAlert("", "Your previous cart items will be cleared, Do you want to proceed?", "Proceed", "Cancel", "TakeOut", false);
                }
                else {
                    Intent intent = new Intent(CustomerDashboardActivity.this, MenuListActivity.class);
                    intent.putExtra(AppConstants.From, AppConstants.TakeOut);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
//                    }
//                    else {
//                        showAppCompatAlert("", "You are not in FoodZone area to reserve a table now.", "Ok", "", "", false);
//                    }
//                }
            }
        });
        tvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerDashboardActivity.this, MenuListActivity.class);
                intent.putExtra(AppConstants.From, AppConstants.Menu);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
//        if (ActivityCompat.checkSelfPermission(CustomerDashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(CustomerDashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//                    !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
//                showAppCompatAlert("GPS settings", "GPS is not enabled. Please enable your GPS, from settings menu?", "Enable", "Cancel", "EnableGPS", false);
//            }
//            else {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, locationListener);
//            }
//        }
//        else {
//            ActivityCompat.requestPermissions(CustomerDashboardActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1210);
//        }
    }

    private void initialiseControls() {
        tvUserName           = llDashboard.findViewById(R.id.tvUserName);
        tvDineIn             = llDashboard.findViewById(R.id.tvDineIn);
        tvTakeOut            = llDashboard.findViewById(R.id.tvTakeOut);
        tvMenu                = llDashboard.findViewById(R.id.tvMenu);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == 1210) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//                        !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
//                    showAppCompatAlert("GPS settings", "GPS is not enabled. Please enable your GPS, from settings menu?", "Enable", "Cancel", "EnableGPS", false);
//                }
//                else {
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, locationListener);
//                }
//            }
//            else {
//                ActivityCompat.requestPermissions(CustomerDashboardActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1210);
//            }
//        }
//    }
//    private LocationListener locationListener = new LocationListener() {
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//        }
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//        @Override
//        public void onProviderDisabled(String provider) {
//            showAppCompatAlert("GPS settings", "GPS is not enabled. Please enable your GPS, from settings menu?", "Enable", "Cancel", "EnableGPS", false);
//        }
//
//        @Override
//        public void onLocationChanged(Location location) {
//            mLocation = location;
//        }
//    };

    @Override
    public void getData() {

    }

    @Override
    public void onBackPressed() {
        dlCareer.closeDrawer(Gravity.LEFT);
        showAppCompatAlert("", "Do you want to exit from app?", "Exit", "Cancel", AppConstants.Exit, false);
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if(from.equalsIgnoreCase(AppConstants.Exit)){
            finish();
        }
//        else if (from.equalsIgnoreCase("EnableGPS")) {
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(intent);
//        }
        else if(from.equalsIgnoreCase("DineIn")) {
            AppConstants.Cart_Items.clear();
            AppConstants.from = "";
            addToCart();
            Intent intent = new Intent(CustomerDashboardActivity.this, TablesListActivity.class);
            intent.putExtra(AppConstants.From, AppConstants.DineIn);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
        else if(from.equalsIgnoreCase("TakeOut")) {
            AppConstants.Cart_Items.clear();
            AppConstants.from = "";
            addToCart();
            Intent intent = new Intent(CustomerDashboardActivity.this, MenuListActivity.class);
            intent.putExtra(AppConstants.From, AppConstants.TakeOut);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }
}
