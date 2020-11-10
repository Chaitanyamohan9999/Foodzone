package com.food.foodzone.activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.OrderDo;
import com.food.foodzone.models.TableDo;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
/**ChefOrdersListActivity is uses to display all the orders placed by customers**/

public class ChefOrdersListActivity extends BaseActivity {

    private static final String TAG = "ChefOrders";
    private View llTables;
    private RecyclerView rvOrders;
    private TextView tvNoOrders;
    private OrderListAdapter orderListAdapter;
    private String from = "";
    private SwipeRefreshLayout srlOrders;
    private ArrayList<OrderDo> orderDos;

    @Override
    public void initialise() {
        llTables =  inflater.inflate(R.layout.chef_orders_list_layout, null);
        addBodyView(llTables);
        chefLeftMenu();
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.GONE);
        ivMenu.setVisibility(View.VISIBLE);
        ivRefresh.setVisibility(View.VISIBLE);
        tvTitle.setText("Orders");
        flCart.setVisibility(View.GONE);
        initialiseControls();
        if (getIntent().hasExtra("From")){
            from = getIntent().getStringExtra("From");
        }
        rvOrders.setLayoutManager(new LinearLayoutManager(ChefOrdersListActivity.this));
        orderListAdapter = new OrderListAdapter(ChefOrdersListActivity.this, new ArrayList<OrderDo>());
        rvOrders.setAdapter(orderListAdapter);
        getData();
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        srlOrders.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
    }

    private void initialiseControls() {
        srlOrders                          = llTables.findViewById(R.id.srlOrders);
        rvOrders                           = llTables.findViewById(R.id.rvOrders);
        tvNoOrders                         = llTables.findViewById(R.id.tvNoOrders);
    }

    @Override
    public void getData() {
        srlOrders.setRefreshing(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        orderDos = new ArrayList<>();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Orders);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                srlOrders.setRefreshing(false);
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    OrderDo orderDo = postSnapshot.getValue(OrderDo.class);
                    Log.e("Get Data", orderDo.toString());
                    if (orderDo.orderType.equalsIgnoreCase(AppConstants.TakeOut)
                            && (orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Pending)
                            || orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Arrived)
                            || orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Accepted))) {
                        orderDos.add(orderDo);
                    }
                    else if (orderDo.orderType.equalsIgnoreCase(AppConstants.DineInNow)
                            && (orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Pending)
                            || orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Arrived))
                            || orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Accepted)) {
                        orderDos.add(orderDo);
                    }
                    else if (orderDo.orderType.equalsIgnoreCase(AppConstants.DineInLater)
                            && orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Arrived)) {
                        orderDos.add(orderDo);
                    }
                }
                if (orderDos != null && orderDos.size() > 0) {
                    rvOrders.setVisibility(View.VISIBLE);
                    tvNoOrders.setVisibility(View.GONE);
                    orderListAdapter.refreshAdapter(orderDos);
                } else {
                    tvNoOrders.setVisibility(View.VISIBLE);
                    rvOrders.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                srlOrders.setRefreshing(false);
                Log.e(TAG, "Failed to reading email.", databaseError.toException());
            }
        });
    }


    private void getOrderByTable(TableDo tableDo){
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


    private class OrderListAdapter extends RecyclerView.Adapter<OrderHolder> {

        private Context context;
        private ArrayList<OrderDo> orderDos;

        public OrderListAdapter(Context context, ArrayList<OrderDo> orderDos) {
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
            View convertView = LayoutInflater.from(context).inflate(R.layout.chef_order_item_cell, parent, false);
            return new OrderHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull final OrderHolder holder, final int position) {
            holder.tvOrderId.setText(orderDos.get(position).orderId);
            holder.tvOrderType.setText(orderDos.get(position).orderType);
            holder.tvOrderStatus.setText(orderDos.get(position).orderStatus);
            holder.tvPickupTime.setText(getPickupTime(orderDos.get(position).pickupTime));
            holder.llOrderStatus.setVisibility(View.GONE);
            if(orderDos.get(position).orderType.equalsIgnoreCase(AppConstants.TakeOut)) {
                holder.ivOrderType.setImageResource(R.drawable.takeout);
                holder.llPickupTime.setVisibility(View.VISIBLE);
            }
            else {
                holder.llPickupTime.setVisibility(View.GONE);
                holder.ivOrderType.setImageResource(R.drawable.reservation);
            }
            if (orderDos.get(position).orderSeen.equalsIgnoreCase("Y")) {//N -> No
                holder.llOrderCell.setBackgroundColor(getResources().getColor(android.R.color.white));
            }
            else {
                holder.llOrderCell.setBackgroundColor(getResources().getColor(R.color.unread_bg_color));//change color code later
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

        private TextView tvOrderId, tvPickupTime, tvOrderType, tvOrderStatus;
        private ImageView ivOrderType;
        private LinearLayout llOrderCell, llPickupTime, llOrderStatus;
        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            llOrderCell                 = itemView.findViewById(R.id.llOrderCell);
            llPickupTime                = itemView.findViewById(R.id.llPickupTime);
            llOrderStatus               = itemView.findViewById(R.id.llOrderStatus);
            ivOrderType                 = itemView.findViewById(R.id.ivOrderType);
            tvOrderId                   = itemView.findViewById(R.id.tvOrderId);
            tvPickupTime                = itemView.findViewById(R.id.tvPickupTime);
            tvOrderType                 = itemView.findViewById(R.id.tvOrderType);
            tvOrderStatus               = itemView.findViewById(R.id.tvOrderStatus);
        }
    }


}
