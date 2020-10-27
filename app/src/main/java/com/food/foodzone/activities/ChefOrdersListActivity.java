package com.food.foodzone.activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ChefOrdersListActivity extends BaseActivity {

    private static final String TAG = "ReserveList";
    private View llTables;
    private RecyclerView rvTables, rvOrders;
    private TextView tvNoData;
    private RadioGroup rgDineInType;
    private RadioButton rbNow, rbLater;
    private FloatingActionButton fabAddTable;
    private TablesListAdapter tablesListAdapter;
    private OrderHistoryAdapter orderHistoryAdapter;
    private String from = "";
    private ArrayList<TableDo> tableDos;
    private ArrayList<OrderDo> orderDos = new ArrayList<>();

    @Override
    public void initialise() {
        llTables =  inflater.inflate(R.layout.tables_list_layout, null);
        addBodyView(llTables);
        chefLeftMenu();
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.GONE);
        ivMenu.setVisibility(View.VISIBLE);
        tvTitle.setText("Orders");
        flCart.setVisibility(View.GONE);
        initialiseControls();
        if (getIntent().hasExtra("From")){
            from = getIntent().getStringExtra("From");
        }
        rbNow.setText("Reservations");
        rbLater.setText("TakeOut");
        fabAddTable.setVisibility(View.GONE);

        rvTables.setLayoutManager(new GridLayoutManager(ChefOrdersListActivity.this, 1, GridLayoutManager.VERTICAL, false));
        tablesListAdapter = new TablesListAdapter(ChefOrdersListActivity.this, new ArrayList<TableDo>());
        rvTables.setAdapter(tablesListAdapter);

        rvOrders.setLayoutManager(new LinearLayoutManager(ChefOrdersListActivity.this));
        orderHistoryAdapter = new OrderHistoryAdapter(ChefOrdersListActivity.this, new ArrayList<OrderDo>());
        rvOrders.setAdapter(orderHistoryAdapter);
        
        getData();
        rgDineInType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ArrayList<TableDo> tableDos = new ArrayList<>();
                if(checkedId == R.id.rbNow){//reservations
                    getData();
                }
                else if(checkedId == R.id.rbLater){// TakeOut
                    getOrders();
                }
            }
        });
    }

    private void initialiseControls() {
        rgDineInType                       = llTables.findViewById(R.id.rgDineInType);
        rbNow                              = llTables.findViewById(R.id.rbNow);
        rbLater                            = llTables.findViewById(R.id.rbLater);
        rvTables                           = llTables.findViewById(R.id.rvTables);
        rvOrders                           = llTables.findViewById(R.id.rvOrders);
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
//                        ArrayList<TableDo> dineInTableDos = dineInNowTables(dineInType);
                        if (tableDos != null && tableDos.size() > 0) {
                            tvNoData.setVisibility(View.GONE);
                            rvTables.setVisibility(View.VISIBLE);
                            rvOrders.setVisibility(View.GONE);
                            tablesListAdapter.refreshAdapter(tableDos);
                        }
                        else {
                            tvNoData.setVisibility(View.VISIBLE);
                            rvOrders.setVisibility(View.GONE);
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

    private void getOrders(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Orders);
        showLoader();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hideLoader();
                orderDos = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    OrderDo orderDo = postSnapshot.getValue(OrderDo.class);
                    Log.e("Get Data", orderDo.toString());
                    orderDos.add(orderDo);
                }
                if (orderDos != null && orderDos.size() > 0) {
                    rvOrders.setVisibility(View.VISIBLE);
                    tvNoData.setVisibility(View.GONE);
                    rvTables.setVisibility(View.GONE);
                    orderHistoryAdapter.refreshAdapter(orderDos);
                }
                else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rvOrders.setVisibility(View.GONE);
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
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        ArrayList<TableDo> dineInFilteredTables = new ArrayList<>();
        if(tableDos != null && tableDos.size() > 0){
            for (int i=0;i<tableDos.size();i++) {
                if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
                    if(userId.equalsIgnoreCase(tableDos.get(i).reservedBy) && !tableDos.get(i).reservedBy.equalsIgnoreCase("")
                            && dineInType.equalsIgnoreCase(tableDos.get(i).tableType)) {
                        dineInFilteredTables.add(tableDos.get(i));
                    }
                }
                else {
                    if(!tableDos.get(i).reservedBy.equalsIgnoreCase("")
                            &&dineInType.equalsIgnoreCase(tableDos.get(i).tableType)) {
                        dineInFilteredTables.add(tableDos.get(i));
                    }
                }
            }
        }
        return dineInFilteredTables;
    }

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
                        Intent intent = new Intent(ChefOrdersListActivity.this, ChefOrderDetailsActivity.class);
                        intent.putExtra(AppConstants.From, orderDos.get(i).orderType);
                        intent.putExtra("TableDo", tableDo);
                        intent.putExtra("OrderDo", orderDos.get(i));
                        startActivityForResult(intent, 5001);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        return;
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 5001 && resultCode == 5001) {
            getData();
        }
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if(from.equalsIgnoreCase(AppConstants.Exit)){
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        dlCareer.closeDrawer(Gravity.LEFT);
        showAppCompatAlert("", "Do you want to exit from app?", "Exit", "Cancel", AppConstants.Exit, false);
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
            View convertView = LayoutInflater.from(context).inflate(R.layout.chef_order_item_cell, parent, false);
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
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOrdersOnTable(tableDos.get(position));
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
        public TableHolder(@NonNull View itemView) {
            super(itemView);
            tvTableName             = itemView.findViewById(R.id.tvTableName);
            tvTableCapacity         = itemView.findViewById(R.id.tvTableCapacity);
        }
    }

    private class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHolder> {

        private Context context;
        private ArrayList<OrderDo> orderDos;

        public OrderHistoryAdapter(Context context, ArrayList<OrderDo> orderDos) {
            this.context  = context;
            this.orderDos = orderDos;
        }

        private void refreshAdapter(ArrayList<OrderDo> orderDos) {
            this.orderDos = orderDos;
            notifyDataSetChanged();
        }
        @NonNull
        @Override
        public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.order_history_item_cell, parent, false);
            return new OrderHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull final OrderHolder holder, final int position) {
            holder.tvOrderId.setText(orderDos.get(position).orderId);
            holder.tvAmount.setText("$"+AppConstants.Decimal_Number.format(orderDos.get(position).totalAmount));
            holder.tvCustomerName.setText("C.Name : "+orderDos.get(position).customerName);
            holder.tvPickupTime.setText(getPickupTime(orderDos.get(position).pickupTime));
            holder.tvCustomerPhone.setText(orderDos.get(position).customerPhone);;
            holder.tvPaymentType.setText(orderDos.get(position).paymentType);
            holder.tvOrderType.setText(orderDos.get(position).orderType);
            holder.tvOrderStatus.setText(orderDos.get(position).orderStatus);
            if(orderDos.get(position).orderStatus.equalsIgnoreCase(AppConstants.Status_Pending)) {
                holder.tvOrderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            }
            else {
                holder.tvOrderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ChefOrdersListActivity.this, ChefOrderDetailsActivity.class);
                    intent.putExtra("OrderDo", orderDos.get(position));
                    startActivityForResult(intent, 5001);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderDos.size();
        }

    }

    private static class OrderHolder extends RecyclerView.ViewHolder {

        private TextView tvOrderId, tvAmount, tvCustomerName, tvPickupTime, tvCustomerPhone,
                tvPaymentType, tvOrderType, tvOrderStatus;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId                   = itemView.findViewById(R.id.tvOrderId);
            tvAmount                    = itemView.findViewById(R.id.tvAmount);
            tvCustomerName              = itemView.findViewById(R.id.tvCustomerName);
            tvPickupTime                = itemView.findViewById(R.id.tvPickupTime);
            tvCustomerPhone             = itemView.findViewById(R.id.tvCustomerPhone);
            tvPaymentType               = itemView.findViewById(R.id.tvPaymentType);
            tvOrderType                 = itemView.findViewById(R.id.tvOrderType);
            tvOrderStatus               = itemView.findViewById(R.id.tvOrderStatus);
        }
    }


}
