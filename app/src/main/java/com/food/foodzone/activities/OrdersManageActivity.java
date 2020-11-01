package com.food.foodzone.activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class OrdersManageActivity extends BaseActivity {

    private static final String TAG = "CartList";
    private View llOrderManage;
    private RecyclerView rvOrdersManage;
    private TextView tvNoData;
    private Button btnClose;
    private OrdersManageAdapter ordersManageAdapter;

    @Override
    public void initialise() {
        llOrderManage =  inflater.inflate(R.layout.order_history_layout, null);
        addBodyView(llOrderManage);
        lockMenu();
        llToolbar.setVisibility(View.VISIBLE);
        flCart.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        tvTitle.setText("Order Manage");
        initialiseControls();
        tvNoData.setVisibility(View.VISIBLE);
        rvOrdersManage.setVisibility(View.GONE);
        rvOrdersManage.setLayoutManager(new LinearLayoutManager(OrdersManageActivity.this));
        ordersManageAdapter = new OrdersManageAdapter(OrdersManageActivity.this, new ArrayList<OrderDo>());
        rvOrdersManage.setAdapter(ordersManageAdapter);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getData();
    }


    private void initialiseControls() {
        rvOrdersManage              = llOrderManage.findViewById(R.id.rvOrderHistory);
        tvNoData                    = llOrderManage.findViewById(R.id.tvNoData);
        btnClose                    = llOrderManage.findViewById(R.id.btnClose);
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
                ArrayList<OrderDo> orderDos = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    OrderDo orderDo = postSnapshot.getValue(OrderDo.class);
                    Log.e("Get Data", orderDo.toString());
                    if (!orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Completed)) {
                        if(userId.equalsIgnoreCase(orderDo.customerId)) {
                            orderDos.add(orderDo);
                        }
                    }
                }
                if(orderDos!=null && orderDos.size() > 0){
                    tvNoData.setVisibility(View.GONE);
                    rvOrdersManage.setVisibility(View.VISIBLE);
                    ordersManageAdapter.refreshAdapter(orderDos);
                }
                else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rvOrdersManage.setVisibility(View.GONE);
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

    private class OrdersManageAdapter extends RecyclerView.Adapter<OrderHolder> {

        private Context context;
        private ArrayList<OrderDo> orderDos;

        public OrdersManageAdapter(Context context, ArrayList<OrderDo> orderDos) {
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
            View convertView = LayoutInflater.from(context).inflate(R.layout.orders_manage_item_cell, parent, false);
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
                    Intent intent = new Intent(OrdersManageActivity.this, OrderDetailsActivity.class);
                    intent.putExtra("OrderDo", orderDos.get(position));
                    startActivity(intent);
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
