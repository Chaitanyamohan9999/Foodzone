package com.food.foodzone.activities;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.OrderDo;
import com.food.foodzone.utils.PreferenceUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class OrdersHistoryActivity extends BaseActivity {

    private static final String TAG = "CartList";
    private View llOrderHistory;
    private RecyclerView rvOrderHistory;
    private TextView tvNoData;
    private CheckBox cbPending, cbAccepted, cbRejected;
    private Button btnClose;
    private ArrayList<OrderDo> orderDos;
    private LinearLayout llOrderFilters;
    private OrderHistoryAdapter orderHistoryAdapter;

    @Override
    public void initialise() {
        llOrderHistory =  inflater.inflate(R.layout.order_history_layout, null);
        addBodyView(llOrderHistory);
        lockMenu();
        llToolbar.setVisibility(View.VISIBLE);
        flCart.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        tvTitle.setText("Order History");
        initialiseControls();
        tvNoData.setVisibility(View.VISIBLE);
        rvOrderHistory.setVisibility(View.GONE);
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(OrdersHistoryActivity.this));
        orderHistoryAdapter = new OrderHistoryAdapter(OrdersHistoryActivity.this, new ArrayList<OrderDo>());
        rvOrderHistory.setAdapter(orderHistoryAdapter);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cbPending.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                applyFilters();
            }
        });
        cbAccepted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                applyFilters();
            }
        });
        cbRejected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                applyFilters();
            }
        });
        getData();
    }

    private void initialiseControls() {
        llOrderFilters              = llOrderHistory.findViewById(R.id.llOrderFilters);
        cbPending                   = llOrderHistory.findViewById(R.id.cbPending);
        cbAccepted                  = llOrderHistory.findViewById(R.id.cbAccepted);
        cbRejected                  = llOrderHistory.findViewById(R.id.cbRejected);
        rvOrderHistory              = llOrderHistory.findViewById(R.id.rvOrderHistory);
        tvNoData                    = llOrderHistory.findViewById(R.id.tvNoData);
        btnClose                    = llOrderHistory.findViewById(R.id.btnClose);
    }

    @Override
    public void getData() {
        final String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
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
                    if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
                        if(userId.equalsIgnoreCase(orderDo.customerId)) {
                            orderDos.add(orderDo);
                        }
                    }
                    else {
                        orderDos.add(orderDo);
                    }
                }
                if(orderDos!=null && orderDos.size() > 0){
                    tvNoData.setVisibility(View.GONE);
                    rvOrderHistory.setVisibility(View.VISIBLE);
                    orderHistoryAdapter.refreshAdapter(orderDos);
                }
                else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rvOrderHistory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoader();
                Log.e(TAG, "Failed to reading email.", databaseError.toException());
            }
        });
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
    }

    private void applyFilters() {
        if(orderDos!= null && orderDos.size()>0) {
            ArrayList<OrderDo> filteredOrders = new ArrayList<>();
            for(int i=0;i<orderDos.size();i++) {
                if (cbPending.isChecked() && orderDos.get(i).orderStatus.equalsIgnoreCase(AppConstants.Status_Pending)) {
                    filteredOrders.add(orderDos.get(i));
                }
                if (cbAccepted.isChecked() && orderDos.get(i).orderStatus.equalsIgnoreCase(AppConstants.Status_Accepted)) {
                    filteredOrders.add(orderDos.get(i));
                }
                if (cbRejected.isChecked() && orderDos.get(i).orderStatus.equalsIgnoreCase(AppConstants.Status_Rejected)) {
                    filteredOrders.add(orderDos.get(i));
                }
            }
            if(!cbPending.isChecked() && !cbAccepted.isChecked()  && !cbRejected.isChecked() ) {
                if(orderDos!=null && orderDos.size() > 0){
                    tvNoData.setVisibility(View.GONE);
                    rvOrderHistory.setVisibility(View.VISIBLE);
                    orderHistoryAdapter.refreshAdapter(orderDos);
                }
                else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rvOrderHistory.setVisibility(View.GONE);
                }
            }
            else {
                if(filteredOrders!=null && filteredOrders.size() > 0){
                    tvNoData.setVisibility(View.GONE);
                    rvOrderHistory.setVisibility(View.VISIBLE);
                    orderHistoryAdapter.refreshAdapter(filteredOrders);
                }
                else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rvOrderHistory.setVisibility(View.GONE);
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 121 && resultCode == 121) {
            getData();
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
                    Intent intent = new Intent(OrdersHistoryActivity.this, OrderDetailsActivity.class);
                    intent.putExtra("OrderDo", orderDos.get(position));
                    startActivityForResult(intent, 121);
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