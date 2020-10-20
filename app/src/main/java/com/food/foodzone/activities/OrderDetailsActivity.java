package com.food.foodzone.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.MenuItemDo;
import com.food.foodzone.models.OrderDo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class OrderDetailsActivity extends BaseActivity {

    private LinearLayout llDetails;
    private RecyclerView rvMenuList;
    private TextView tvOrderId, tvAmount, tvCustomerName, tvPickupTime, tvCustomerPhone,
            tvCustomerEmail, tvPaymentType, tvOrderType, tvOrderStatus;
    private Button btnCancel, btnApprove;
    private LinearLayout llPhone, llEmail;
    private MenuListAdapter menuListAdapter;
    private OrderDo orderDo;
    
    @Override
    public void initialise() {
        llDetails = (LinearLayout) inflater.inflate(R.layout.order_details_layout, null);
        addBodyView(llDetails);
        lockMenu();
        flCart.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        tvTitle.setText("Order Details");
        if(getIntent().hasExtra("OrderDo")) {
            orderDo = (OrderDo) getIntent().getSerializableExtra("OrderDo");
        }
        initialiseControls();
        if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
            btnApprove.setText("CLOSE");
            btnCancel.setText("CANCEL ORDER");
        }
        else {
            btnApprove.setText("ACCEPT ORDER");
            btnCancel.setText("REJECT ORDER");
        }
        bindData();
        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
                    finish();
                }
                else {
                    actionOnOrder(AppConstants.Status_Accepted);
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
                    actionOnOrder(AppConstants.Status_Cancelled);
                }
                else {
                    actionOnOrder(AppConstants.Status_Rejected);
                }
            }
        });
        llPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + orderDo.customerPhone));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        llEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{  orderDo.customerEmail});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FoodZone Order Status");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi,\n Your FoodZone order");
                emailIntent.setType("message/rfc822");
                try {
                    startActivity(Intent.createChooser(emailIntent, "Sending email..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context, "No email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initialiseControls() {
        tvOrderId                   = llDetails.findViewById(R.id.tvOrderId);
        tvAmount                    = llDetails.findViewById(R.id.tvAmount);
        tvCustomerName              = llDetails.findViewById(R.id.tvCustomerName);
        tvPickupTime                = llDetails.findViewById(R.id.tvPickupTime);
        tvCustomerPhone             = llDetails.findViewById(R.id.tvCustomerPhone);
        tvPaymentType               = llDetails.findViewById(R.id.tvPaymentType);
        tvCustomerEmail             = llDetails.findViewById(R.id.tvCustomerEmail);
        tvOrderType                 = llDetails.findViewById(R.id.tvOrderType);
        tvOrderStatus               = llDetails.findViewById(R.id.tvOrderStatus);
        llPhone                     = llDetails.findViewById(R.id.llPhone);
        llEmail                     = llDetails.findViewById(R.id.llEmail);
        rvMenuList                  = llDetails.findViewById(R.id.rvMenuList);
        btnCancel                   = llDetails.findViewById(R.id.btnCancel);
        btnApprove                  = llDetails.findViewById(R.id.btnApprove);
    }
    
    private void bindData() {
        tvOrderId.setText(orderDo.orderId);
        tvAmount.setText("$"+ AppConstants.Decimal_Number.format(orderDo.totalAmount));
        tvCustomerName.setText(""+orderDo.customerName);
        tvPickupTime.setText(getPickupTime(orderDo.pickupTime));
        tvCustomerPhone.setText(orderDo.customerPhone);;
        tvCustomerEmail.setText(orderDo.customerPhone);;
        tvPaymentType.setText(orderDo.paymentType);
        tvOrderType.setText(orderDo.orderType);
        tvOrderStatus.setText(orderDo.orderStatus);
        if(orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Pending)) {
            tvOrderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }
        else {
            tvOrderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        }
        rvMenuList.setLayoutManager(new LinearLayoutManager(OrderDetailsActivity.this));
        menuListAdapter = new MenuListAdapter(OrderDetailsActivity.this, orderDo.menuItemDos);
        rvMenuList.setAdapter(menuListAdapter);
    }
    @Override
    public void getData() {

    }

    private void actionOnOrder(String orderAction) {
        orderDo.orderStatus = orderAction;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Orders);
        showLoader();
        databaseReference.child(orderDo.orderId).setValue(orderDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        showAppCompatAlert("", "The order has been Accepted successfully!", "OK", "", "AcceptOrder", false);
                    }
                });
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if(from.equalsIgnoreCase("AcceptOrder")) {
            setResult(121);
            finish();
        }
    }

    private static class MenuListAdapter extends RecyclerView.Adapter<MenuHolder> {

        private Context context;
        private ArrayList<MenuItemDo> menuItemDos;

        public MenuListAdapter(Context context, ArrayList<MenuItemDo> menuItemDos) {
            this.context = context;
            this.menuItemDos = menuItemDos;
        }

        private void refreshAdapter(ArrayList<MenuItemDo> menuItemDos) {
            this.menuItemDos = menuItemDos;
            notifyDataSetChanged();
        }
        @NonNull
        @Override
        public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.menu_item_cell, parent, false);
            return new MenuHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MenuHolder holder, final int position) {
            if(menuItemDos.get(position).isCategory){
                holder.cvItem.setVisibility(View.GONE);
                holder.tvCategoryName.setVisibility(View.VISIBLE);
            }
            else {
                holder.cvItem.setVisibility(View.VISIBLE);
                holder.tvCategoryName.setVisibility(View.GONE);
            }
            holder.tvCategoryName.setText(menuItemDos.get(position).itemCategory);
            holder.tvItemName.setText(menuItemDos.get(position).itemName);
            holder.tvItemPrice.setText("$"+menuItemDos.get(position).itemPrice);
            holder.tvItemDescription.setText(""+menuItemDos.get(position).itemDescription);
            if(!menuItemDos.get(position).itemImage.equalsIgnoreCase("")){
                Picasso.get().load(menuItemDos.get(position).itemImage).placeholder(R.drawable.food_placeholder)
                        .error(R.drawable.food_placeholder).into(holder.ivItemImage);
            }
            else {
                holder.ivItemImage.setImageResource(R.drawable.food_placeholder);
            }
            holder.ivUnAvailable.setVisibility(View.GONE);
            holder.ivDeleteItem.setVisibility(View.GONE);
            holder.tvMinus.setVisibility(View.GONE);
            holder.tvPlus.setVisibility(View.GONE);
            
        }

        @Override
        public int getItemCount() {
            return menuItemDos.size();
        }

    }

    private static class MenuHolder extends RecyclerView.ViewHolder {

        private TextView tvCategoryName, tvItemName, tvItemPrice, tvItemDescription, tvMinus, tvPlus, tvQty;
        private ImageView ivItemImage, ivDeleteItem,ivUnAvailable;
        private LinearLayout llAddToCart;
        private CardView cvItem;

        public MenuHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName              = itemView.findViewById(R.id.tvCategoryName);
            tvItemName                  = itemView.findViewById(R.id.tvItemName);
            tvItemPrice                 = itemView.findViewById(R.id.tvItemPrice);
            tvItemDescription           = itemView.findViewById(R.id.tvItemDescription);
            ivItemImage                 = itemView.findViewById(R.id.ivItemImage);
            ivDeleteItem                = itemView.findViewById(R.id.ivDeleteItem);
            ivUnAvailable               = itemView.findViewById(R.id.ivUnAvailable);
            cvItem                      = itemView.findViewById(R.id.cvItem);
            llAddToCart                 = itemView.findViewById(R.id.llAddToCart);
            tvMinus                     = itemView.findViewById(R.id.tvMinus);
            tvPlus                      = itemView.findViewById(R.id.tvPlus);
            tvQty                       = itemView.findViewById(R.id.tvQty);
        }
    }
    
}
