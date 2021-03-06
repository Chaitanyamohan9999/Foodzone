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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.TableDo;
import com.food.foodzone.utils.PreferenceUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/** This is the screen for displaying list of tables information which are added by manager. User can filter the tables by selcting the Now or Later radio buttons. And user can delete the tables*/

public class TablesListActivity extends BaseActivity {

    private static final String TAG = "TablesList";
    private View llTables;
    private RecyclerView rvTables;
    private TextView tvNoData;
    private RadioGroup rgDineInType;
    private RadioButton rbNow, rbLater;
    private FloatingActionButton fabAddTable;
    private TablesListAdapter tablesListAdapter;
    private String from = "", dineInType = AppConstants.DineInNow;
    private ArrayList<TableDo> tableDos;

    @Override
    public void initialise() {
        llTables = inflater.inflate(R.layout.tables_list_layout, null);
        addBodyView(llTables);
        lockMenu();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        if (getIntent().hasExtra("From")) {
            from = getIntent().getStringExtra("From");
        }
        initialiseControls();
        if (from.equalsIgnoreCase(AppConstants.DineIn)) {
            flCart.setVisibility(View.VISIBLE);
        } else {
            flCart.setVisibility(View.GONE);
        }
        if (AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
            fabAddTable.setVisibility(View.GONE);
            rgDineInType.setVisibility(View.VISIBLE);
            tvTitle.setText("Book Table");
        } else {
            fabAddTable.setVisibility(View.VISIBLE);
            rgDineInType.setVisibility(View.GONE);
            tvTitle.setText("Tables List");
        }
        rvTables.setLayoutManager(new GridLayoutManager(TablesListActivity.this, 2, GridLayoutManager.VERTICAL, false));
        tablesListAdapter = new TablesListAdapter(TablesListActivity.this, new ArrayList<TableDo>());
        rvTables.setAdapter(tablesListAdapter);
        getData();
        rgDineInType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ArrayList<TableDo> tableDos = new ArrayList<>();
                if (checkedId == R.id.rbNow) {
                    dineInType = AppConstants.DineInNow;
                    tableDos = dineInNowTables(dineInType);
                } else if (checkedId == R.id.rbLater) {
                    dineInType = AppConstants.DineInLater;
                    tableDos = dineInNowTables(dineInType);
                }
                if (tableDos != null && tableDos.size() > 0) {
                    tvNoData.setVisibility(View.GONE);
                    rvTables.setVisibility(View.VISIBLE);
                    tablesListAdapter.refreshAdapter(tableDos);
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rvTables.setVisibility(View.GONE);
                }
            }
        });
        fabAddTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TablesListActivity.this, AddTableActivity.class);
                startActivityForResult(intent, 101);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    showAppCompatAlert("GPS settings", "GPS is not enabled. Please enable your GPS, from settings menu?", "Enable", "Cancel", "EnableGPS", false);
                }
                else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, locationListener);
                }
            }
            else {
                ActivityCompat.requestPermissions(TablesListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1210);
            }
        }
    }

    private void initialiseControls() {
        rgDineInType                       = llTables.findViewById(R.id.rgDineInType);
        rbNow                              = llTables.findViewById(R.id.rbNow);
        rbLater                            = llTables.findViewById(R.id.rbLater);
        rvTables                           = llTables.findViewById(R.id.rvTables);
        tvNoData                           = llTables.findViewById(R.id.tvNoData);
        fabAddTable                        = llTables.findViewById(R.id.fabAddTable);
    }

    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {

        }
        @Override
        public void onProviderDisabled(String provider) {
            showAppCompatAlert("GPS settings", "GPS is not enabled. Please enable your GPS, from settings menu?", "Enable", "Cancel", "EnableGPS", false);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
        }
    };
    private Location mLocation;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1210) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    showAppCompatAlert("GPS settings", "GPS is not enabled. Please enable your GPS, from settings menu?", "Enable", "Cancel", "EnableGPS", false);
                }
                else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, locationListener);
                }
            }
            else {
                ActivityCompat.requestPermissions(TablesListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1210);
            }
        }
    }

    @Override
    public void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Tables);
        showLoader();
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hideLoader();
                tableDos = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    TableDo tableDo = postSnapshot.getValue(TableDo.class);
                    Log.e("Get Data", tableDo.toString());
                    tableDos.add(tableDo);
                }
                if (AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {

                }
                else {

                }
                ArrayList<TableDo>  dineInTableDos = dineInNowTables(dineInType);
                if(dineInTableDos != null && dineInTableDos.size() > 0){
                    tvNoData.setVisibility(View.GONE);
                    rvTables.setVisibility(View.VISIBLE);
                    tablesListAdapter.refreshAdapter(dineInTableDos);
                }
                else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rvTables.setVisibility(View.GONE);
                }
//                        }
//                        else {
//                            if(tableDos != null && tableDos.size() > 0){
//                                tvNoData.setVisibility(View.GONE);
//                                rvTables.setVisibility(View.VISIBLE);
//                                tablesListAdapter.refreshAdapter(tableDos);
//                            }
//                            else {
//                                tvNoData.setVisibility(View.VISIBLE);
//                                rvTables.setVisibility(View.GONE);
//                            }
//                        }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoader();
                Log.e(TAG, "Failed to reading email.", databaseError.toException());
            }
        });
    }


    private ArrayList<TableDo> dineInNowTables(String dineInType) {
        ArrayList<TableDo> dineInFilteredTables = new ArrayList<>();
        if (AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
            if (tableDos != null && tableDos.size() > 0) {
                for (int i = 0; i < tableDos.size(); i++) {
                    if (tableDos.get(i).tableType.equalsIgnoreCase(dineInType)) {
                        dineInFilteredTables.add(tableDos.get(i));
                    }
                }
            }
        }
        else {
            return dineInFilteredTables = tableDos;
        }
        return dineInFilteredTables;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode == 101) {
            getData();
        }
    }

    private void deleteTable(String tableId) {
        showLoader();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Tables);
        databaseReference.child(tableId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                hideLoader();
                showAppCompatAlert("", "Congratulations! You have successfully deleted a Table.", "OK", "", "DeleteTable", false);
            }
        });
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if (from.equalsIgnoreCase("DeleteTable")) {
            getData();
        }
        else if (from.equalsIgnoreCase("EnableGPS")) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    public void onButtonNoClick(String from) {
        super.onButtonNoClick(from);
        if(from.equalsIgnoreCase("EnableGPS")) {
            finish();
        }
    }

    private class TablesListAdapter extends RecyclerView.Adapter<TableHolder> {

        private Context context;
        private ArrayList<TableDo> tableDos;

        public TablesListAdapter(Context context, ArrayList<TableDo> tableDos) {
            this.context  = context;
            this.tableDos = tableDos;
        }
        @NonNull
        @Override
        public TableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.table_item_cell, parent, false);
            return new TableHolder(convertView);
        }

        private void refreshAdapter(ArrayList<TableDo> tableDos) {
            this.tableDos = tableDos;
            notifyDataSetChanged();
        }
        @Override
        public void onBindViewHolder(@NonNull TableHolder holder, final int position) {
            holder.tvTableName.setText("Table Number : "+AppConstants.TwoDigitsNumber.format(tableDos.get(position).tableNumber));
            holder.tvTableCapacity.setText("Table Capacity : "+AppConstants.TwoDigitsNumber.format(tableDos.get(position).tableCapacity));
            if(tableDos.get(position).tableType.equalsIgnoreCase(AppConstants.DineInNow)) {

            }
            else {

            }
            if (AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
                holder.ivDeleteTable.setVisibility(View.INVISIBLE);
                holder.ivDeleteTable.setEnabled(false);
                if(tableDos.get(position).reservedBy.trim().equalsIgnoreCase("")){
                    holder.ivReserved.setVisibility(View.INVISIBLE);
                    holder.itemView.setEnabled(true);
                }
                else {
                    holder.ivReserved.setVisibility(View.VISIBLE);
                    holder.itemView.setEnabled(false);
                }
            }
            else if (AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Manager_Role)) {
                if(tableDos.get(position).reservedBy.trim().equalsIgnoreCase("")){
                    holder.ivReserved.setVisibility(View.INVISIBLE);
                    holder.ivDeleteTable.setVisibility(View.VISIBLE);
                }
                else {
                    holder.ivReserved.setVisibility(View.VISIBLE);
                    holder.ivDeleteTable.setVisibility(View.INVISIBLE);
                }
            }
            else {
                holder.ivDeleteTable.setVisibility(View.GONE);
                if(tableDos.get(position).reservedBy.trim().equalsIgnoreCase("")){
                    holder.ivReserved.setVisibility(View.GONE);
                }
                else {
                    holder.ivReserved.setVisibility(View.VISIBLE);
                }
            }
            holder.ivDeleteTable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteTable(tableDos.get(position).tableId);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
                        if(dineInType.equalsIgnoreCase(AppConstants.DineInLater)) {
                            Intent intent = new Intent(TablesListActivity.this, ScheduleTimeActivity.class);
                            intent.putExtra(AppConstants.From, dineInType);
                            intent.putExtra("TableDo", tableDos.get(position));
                            startActivityForResult(intent, 1001);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                        else if(dineInType.equalsIgnoreCase(AppConstants.DineInNow)) {
                            if(isFoodZoneArea(mLocation)) {
                                Intent intent = new Intent(TablesListActivity.this, MenuListActivity.class);
                                intent.putExtra(AppConstants.From, dineInType);
                                intent.putExtra("TableDo", tableDos.get(position));
                                startActivityForResult(intent, 1001);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                            else {
                                showAppCompatAlert("", "You are not in FoodZone area to reserve a table now.", "Ok", "", "", false);
                            }
                        }
                        else {
                            Intent intent = new Intent(TablesListActivity.this, MenuListActivity.class);
                            intent.putExtra(AppConstants.From, dineInType);
                            intent.putExtra("TableDo", tableDos.get(position));
                            startActivityForResult(intent, 1001);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return tableDos.size();
        }
    }

    private static class TableHolder extends RecyclerView.ViewHolder {
        private TextView tvTableName, tvTableCapacity;
        private ImageView ivDeleteTable, ivReserved;
        public TableHolder(@NonNull View itemView) {
            super(itemView);
            tvTableName             = itemView.findViewById(R.id.tvTableName);
            tvTableCapacity         = itemView.findViewById(R.id.tvTableCapacity);
            ivReserved              = itemView.findViewById(R.id.ivReserved);
            ivDeleteTable           = itemView.findViewById(R.id.ivDeleteTable);
        }
    }
}
