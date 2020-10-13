package com.food.foodzone.activities;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.food.foodzone.R;
import com.food.foodzone.common.*;
import com.food.foodzone.utils.PreferenceUtils;

public class CustomerDashboardActivity extends BaseActivity {

    private View llDashboard;
    private TextView tvUserName, tvDineIn, tvTakeOut, tvReservation;

    @Override
    public void initialise() {
        llDashboard = inflater.inflate(R.layout.customer_dashboard_layout, null);
        addBodyView(llDashboard);
        flCart.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.GONE);
        ivMenu.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        customerLeftMenu();
        initialiseControls();
        String welcomeText = "Hi "+preferenceUtils.getStringFromPreference(PreferenceUtils.UserName, "")+", Welcome to Food Zone";
        tvUserName.setText(welcomeText);
        tvDineIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerDashboardActivity.this, TablesListActivity.class);
                intent.putExtra(AppConstants.From, AppConstants.DiveIn);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        tvTakeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerDashboardActivity.this, MenuListActivity.class);
                intent.putExtra(AppConstants.From, AppConstants.TakeOut);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        tvReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerDashboardActivity.this, TablesListActivity.class);
                intent.putExtra(AppConstants.From, AppConstants.Reservation);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    private void initialiseControls() {
        tvUserName           = llDashboard.findViewById(R.id.tvUserName);
        tvDineIn             = llDashboard.findViewById(R.id.tvDineIn);
        tvTakeOut            = llDashboard.findViewById(R.id.tvTakeOut);
        tvReservation        = llDashboard.findViewById(R.id.tvReservation);
    }
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
    }
}
