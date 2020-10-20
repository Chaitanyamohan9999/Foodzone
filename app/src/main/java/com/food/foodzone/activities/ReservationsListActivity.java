package com.food.foodzone.activities;

import android.content.Context;
import android.content.Intent;
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
import com.food.foodzone.models.OrderDo;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ReservationsListActivity extends BaseActivity {

    private static final String TAG = "ReserveList";
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
        llTables =  inflater.inflate(R.layout.tables_list_layout, null);
        addBodyView(llTables);
        lockMenu();
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        tvTitle.setText("Reservations");
        flCart.setVisibility(View.GONE);
        initialiseControls();
        if (getIntent().hasExtra("From")){
            from = getIntent().getStringExtra("From");
        }
        fabAddTable.setVisibility(View.GONE);
        rvTables.setLayoutManager(new GridLayoutManager(ReservationsListActivity.this, 2, GridLayoutManager.VERTICAL, false));
        tablesListAdapter = new TablesListAdapter(ReservationsListActivity.this, new ArrayList<TableDo>());
        rvTables.setAdapter(tablesListAdapter);
        getData();
        rgDineInType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ArrayList<TableDo> tableDos = new ArrayList<>();
                if(checkedId == R.id.rbNow){
                    dineInType = AppConstants.DineInNow;
                    tableDos = dineInNowTables(dineInType);
                }
                else if(checkedId == R.id.rbLater){
                    dineInType = AppConstants.DineInLater;
                    tableDos = dineInNowTables(dineInType);
                }
                if(tableDos != null && tableDos.size() > 0){
                    tvNoData.setVisibility(View.GONE);
                    rvTables.setVisibility(View.VISIBLE);
                    tablesListAdapter.refreshAdapter(tableDos);
                }
                else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rvTables.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initialiseControls() {
        rgDineInType                       = llTables.findViewById(R.id.rgDineInType);
        rbNow                              = llTables.findViewById(R.id.rbNow);
        rbLater                            = llTables.findViewById(R.id.rbLater);
        rvTables                           = llTables.findViewById(R.id.rvTables);
        tvNoData                           = llTables.findViewById(R.id.tvNoData);
        fabAddTable                        = llTables.findViewById(R.id.fabAddTable);
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
                tableDos = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    TableDo tableDo = postSnapshot.getValue(TableDo.class);
                    Log.e("Get Data", tableDo.toString());
                    tableDos.add(tableDo);
                }
//                        if(from.equalsIgnoreCase(AppConstants.DineIn)) {
                getOrdersOnTable(null);
                ArrayList<TableDo> dineInTableDos = dineInNowTables(dineInType);
                if (dineInTableDos != null && dineInTableDos.size() > 0) {
                    tvNoData.setVisibility(View.GONE);
                    rvTables.setVisibility(View.VISIBLE);
                    tablesListAdapter.refreshAdapter(dineInTableDos);
                }
                else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rvTables.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoader();
                Log.e(TAG, "Failed to reading email.", databaseError.toException());
            }
        });
    }


    private ArrayList<TableDo> dineInNowTables(String dineInType) {

    private final ArrayList<OrderDo> orderDos = new ArrayList<>();
    private void getOrdersOnTable(TableDo tableDo) {
        if(tableDo == null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Orders);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    hideLoader();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        OrderDo orderDo = postSnapshot.getValue(OrderDo.class);
                        Log.e("Get Data", orderDo.toString());
                        orderDos.add(orderDo);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    hideLoader();
                    Log.e(TAG, "Failed to reading email.", databaseError.toException());
                }
            });
        }
        else {
            if(orderDos!=null && orderDos.size()>0) {
                for (int i=0;i<orderDos.size();i++) {
                    if(tableDo.tableId.equalsIgnoreCase(orderDos.get(i).tableId)) {
                        Intent intent = new Intent(ReservationsListActivity.this, OrderDetailsActivity.class);
                        intent.putExtra(AppConstants.From, dineInType);
                        intent.putExtra("TableDo", tableDo);
                        intent.putExtra("OrderDo", orderDos.get(i));
                        startActivityForResult(intent, 5001);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                }
            }
        }
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
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
