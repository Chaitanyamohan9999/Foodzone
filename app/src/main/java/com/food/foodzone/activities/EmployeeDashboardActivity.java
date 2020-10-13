package com.food.foodzone.activities;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;

public class EmployeeDashboardActivity extends BaseActivity {

    private View llDashboard;
    private TextView tvManageEmployees, tvManageTables, tvManageMenu;

    @Override
    public void initialise() {
        llDashboard = inflater.inflate(R.layout.employee_dashboard_layout, null);
        addBodyView(llDashboard);
        flCart.setVisibility(View.GONE);
        ivBack.setVisibility(View.GONE);
        ivMenu.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        employeeLeftMenu();
        initialiseControls();
        tvManageEmployees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeeDashboardActivity.this, EmployeesListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        tvManageTables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeeDashboardActivity.this, TablesListActivity.class);
                intent.putExtra(AppConstants.From, AppConstants.TakeOut);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        tvManageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeeDashboardActivity.this, MenuListActivity.class);
                intent.putExtra(AppConstants.From, AppConstants.Reservation);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    private void initialiseControls() {
        tvManageEmployees    = llDashboard.findViewById(R.id.tvManageEmployees);
        tvManageTables       = llDashboard.findViewById(R.id.tvManageTables);
        tvManageMenu         = llDashboard.findViewById(R.id.tvManageMenu);
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
