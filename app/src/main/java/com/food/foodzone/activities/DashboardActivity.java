package com.food.foodzone.activities;

import android.view.View;
import android.widget.LinearLayout;

import com.food.foodzone.R;

public class DashboardActivity extends BaseActivity {

    private View llDashboard;
    @Override
    public void initialise() {
        llDashboard = (LinearLayout) inflater.inflate(R.layout.dashboard_layout, null);
        addBodyView(llDashboard);
        lockMenu();
        ivBack.setVisibility(View.GONE);
        ivMenu.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void getData() {

    }
}
