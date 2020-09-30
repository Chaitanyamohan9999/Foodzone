package com.food.foodzone.activities;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.food.foodzone.R;

public class DashboardActivity extends BaseActivity {

    private View llDashboard;
    private TextView tvDineIn, tvTakeOut, tvReservation;

    @Override
    public void initialise() {
        llDashboard = inflater.inflate(R.layout.dashboard_layout, null);
        addBodyView(llDashboard);
        lockMenu();
        flCart.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.GONE);
        ivMenu.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        initialiseControls();
        tvDineIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, TablesListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        tvReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initialiseControls() {
        tvDineIn             = llDashboard.findViewById(R.id.tvDineIn);
        tvTakeOut            = llDashboard.findViewById(R.id.tvTakeOut);
        tvReservation        = llDashboard.findViewById(R.id.tvReservation);
    }
    @Override
    public void getData() {

    }
}
