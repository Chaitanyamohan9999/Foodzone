package com.food.foodzone.activities;

import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.OrderDo;
import com.food.foodzone.models.TableDo;
import com.food.foodzone.utils.PreferenceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;

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
                intent.putExtra(AppConstants.From, AppConstants.ManageMenu);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        getData();
        getOrders();
    }

    private void initialiseControls() {
        tvManageEmployees    = llDashboard.findViewById(R.id.tvManageEmployees);
        tvManageTables       = llDashboard.findViewById(R.id.tvManageTables);
        tvManageMenu         = llDashboard.findViewById(R.id.tvManageMenu);
    }
    @Override
    public void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Tables);
        showLoader();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hideLoader();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    TableDo tableDo = postSnapshot.getValue(TableDo.class);
                    Log.e("Get Data", tableDo.toString());
                    if(tableDo.reservedAt<System.currentTimeMillis()){
                        updateTableStatus(tableDo);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoader();
            }
        });
    }

    private void updateTableStatus(final TableDo tableDo) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tableDo.tableStatus = "";
        tableDo.reservedBy = "";
        tableDo.reservedAt = 0;
        tableDo.reservedFor = 0;
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Tables);
        databaseReference.child(tableDo.tableId).setValue(tableDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        updateOrderStatus(tableDo);
                    }
                });
    }

    private void updateOrderStatus(final TableDo tableDo) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (orderDos!=null && orderDos.size()>0){
            for (int i=0;i<orderDos.size();i++) {
                if(tableDo.reservedAt<System.currentTimeMillis() && orderDos.get(i).tableId.equalsIgnoreCase(tableDo.tableId)) {
                    final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Orders);
                    databaseReference.child(orderDos.get(i).orderId).setValue(orderDos.get(i)).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    hideLoader();
                                }
                            });
                }
//                else if() {// expired takeout orders
//
//                }
            }

        }
    }

    private void updateOrder(OrderDo orderDo) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Orders);
        databaseReference.child(orderDo.orderId).setValue(orderDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    private ArrayList<OrderDo> orderDos;

    private void getOrders() {
        final String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Orders);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hideLoader();
                orderDos = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    OrderDo orderDo = postSnapshot.getValue(OrderDo.class);
                    Log.e("Get Data", orderDo.toString());
                    orderDos.add(orderDo);
                    long curTime = System.currentTimeMillis();
                    if (orderDo.reservedAt < curTime) {
                        orderDo.reservedAt = curTime;
                        orderDo.orderStatus = AppConstants.Status_Completed;
                        updateOrder(orderDo);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoader();
            }
        });
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
