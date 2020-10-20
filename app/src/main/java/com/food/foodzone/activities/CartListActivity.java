package com.food.foodzone.activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.MenuItemDo;
import com.food.foodzone.utils.RangeTimePickerDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CartListActivity extends BaseActivity {

    private static final String TAG = "CartList";
    private View llMenu;
    private RecyclerView rvCartList;
    private TextView tvItemsCount, tvPrice, tvDiscountLabel, tvDiscount, tvChargesLabel, tvCharges, tvTotalAmount;
    private Button btnPlaceOrder;
    private LinearLayout llDiscount;
    private CartListAdapter cartListAdapter;
    private double totalAmount;
    private String from = "";

    @Override
    public void initialise() {
        llMenu =  inflater.inflate(R.layout.cart_list_layout, null);
        addBodyView(llMenu);
        lockMenu();
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        tvTitle.setText("Cart");
        if (getIntent().hasExtra("From")){
            from = getIntent().getStringExtra("From");
        }
        initialiseControls();
        rvCartList.setLayoutManager(new LinearLayoutManager(CartListActivity.this));
        cartListAdapter = new CartListAdapter(CartListActivity.this, new ArrayList<MenuItemDo>());
        rvCartList.setAdapter(cartListAdapter);
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        pickupTime = ""+calendar.get(Calendar.DAY_OF_MONTH)+""+AppConstants.TwoDigitsNumber.format(calendar.get(Calendar.MONTH)+1)+""+calendar.get(Calendar.YEAR)
                                +""+AppConstants.TwoDigitsNumber.format(hour)+""+AppConstants.TwoDigitsNumber.format(minute);
                        pickupMessage = "Your order will be ready at "+AppConstants.TwoDigitsNumber.format(hour)+" : "+AppConstants.TwoDigitsNumber.format(minute)+" to pickup";
                        showAppCompatAlert("", pickupMessage, "Ok", "Cancel", "Takeout_Pickup", false);
                    }
                };
//
//                final TimePickerDialog timePickerDialog = new TimePickerDialog(CartListActivity.this,timePickerListener,
//                        calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)+AppConstants.Pickup_Min_Time,true);
//                timePickerDialog.show();
                RangeTimePickerDialog rangeTimePickerDialog = new RangeTimePickerDialog(CartListActivity.this, timePickerListener,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)+30, true);
                rangeTimePickerDialog.setMin(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)+30);
                rangeTimePickerDialog.setMax(calendar.get(Calendar.HOUR_OF_DAY)+AppConstants.Pickup_Max_Time, 0);
                rangeTimePickerDialog.show();
            }
        });
        getData();
    }

    private String pickupTime = "", pickupMessage = "";
    private void placeOrder() {
        Intent intent = new Intent(CartListActivity.this, PaymentActivity.class);
        intent.putExtra("From", from);
        intent.putExtra("Amount", totalAmount);
        intent.putExtra("PickupTime", pickupTime);
        intent.putExtra("PickupMessage", pickupMessage);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);


    }

    private void initialiseControls() {
        rvCartList                  = llMenu.findViewById(R.id.rvCartList);
        tvItemsCount                = llMenu.findViewById(R.id.tvItemsCount);
        tvPrice                     = llMenu.findViewById(R.id.tvPrice);
        tvDiscountLabel             = llMenu.findViewById(R.id.tvDiscountLabel);
        tvDiscount                  = llMenu.findViewById(R.id.tvDiscount);
        tvChargesLabel               = llMenu.findViewById(R.id.tvChargesLabel);
        llDiscount                  = llMenu.findViewById(R.id.llDiscount);
        tvCharges                   = llMenu.findViewById(R.id.tvCharges);
        tvTotalAmount               = llMenu.findViewById(R.id.tvTotalAmount);
        btnPlaceOrder               = llMenu.findViewById(R.id.btnPlaceOrder);
    }

    @Override
    public void getData() {
        cartListAdapter.refreshAdapter(AppConstants.Cart_Items);
    }

    private void calcTotalAmount(ArrayList<MenuItemDo> menuItemDos) {
        if(menuItemDos!=null && menuItemDos.size()>0){
            double price = 0;
            for (int i=0;i<menuItemDos.size();i++) {
                price = price +(menuItemDos.get(i).itemPrice*menuItemDos.get(i).quantity);
            }
            double discount = 0;
            tvItemsCount.setText("Price ("+menuItemDos.size()+" Items)");
            tvPrice.setText("$"+AppConstants.Decimal_Number.format(price));
            if(AppConstants.Discount > 0){
                llDiscount.setVisibility(View.VISIBLE);
                tvDiscountLabel.setText("Discount("+AppConstants.Discount+"%)");
                discount = (price*AppConstants.Discount)/100;
                tvDiscount.setText("$"+AppConstants.Decimal_Number.format(discount));
            }
            else {
                llDiscount.setVisibility(View.GONE);
            }
            tvChargesLabel.setText("Charges("+AppConstants.Charges+"%)");
            double charges = (price*AppConstants.Charges)/100;
            tvCharges.setText("$"+AppConstants.Decimal_Number.format(charges));
            totalAmount = price - discount + charges;
            tvTotalAmount.setText("$"+AppConstants.Decimal_Number.format(totalAmount));
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 103 && resultCode == 103) {
            getData();
        }
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if (from.equalsIgnoreCase("DeleteItem")) {
            getData();
        }
        else if(from.equalsIgnoreCase("Takeout_Pickup")) {
            placeOrder();
        }
    }

    private class CartListAdapter extends RecyclerView.Adapter<MenuHolder> {

        private Context context;
        private ArrayList<MenuItemDo> menuItemDos;

        public CartListAdapter(Context context, ArrayList<MenuItemDo> menuItemDos) {
            this.context = context;
            this.menuItemDos = menuItemDos;
        }

        private void refreshAdapter(ArrayList<MenuItemDo> menuItemDos) {
            this.menuItemDos = menuItemDos;
            calcTotalAmount(menuItemDos);
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
            holder.tvCategoryName.setVisibility(View.GONE);
            holder.tvItemName.setText(menuItemDos.get(position).itemName);
            holder.tvItemPrice.setText("Price : $"+menuItemDos.get(position).itemPrice);
            holder.tvItemDescription.setText(""+menuItemDos.get(position).itemDescription);
            if(!menuItemDos.get(position).itemImage.equalsIgnoreCase("")){
                Picasso.get().load(menuItemDos.get(position).itemImage).placeholder(R.drawable.food_placeholder)
                        .error(R.drawable.food_placeholder).into(holder.ivItemImage);
            }
            else {
                holder.ivItemImage.setImageResource(R.drawable.food_placeholder);
            }
            holder.tvMinus.setVisibility(View.GONE);
            holder.tvPlus.setVisibility(View.GONE);
            holder.ivUnAvailable.setVisibility(View.GONE);
            holder.tvQty.setText("Qty : "+menuItemDos.get(position).quantity);
            if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
                holder.llAddToCart.setVisibility(View.VISIBLE);
                holder.ivDeleteItem.setVisibility(View.GONE);
            }
            else {
                holder.llAddToCart.setVisibility(View.GONE);
                holder.ivDeleteItem.setVisibility(View.VISIBLE);
            }
            holder.ivDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(AppConstants.Cart_Items.contains(menuItemDos.get(position))){
                        AppConstants.Cart_Items.remove(menuItemDos.get(position));
                        refreshAdapter(AppConstants.Cart_Items);
                    }
                }
            });
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

        public MenuHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName              = itemView.findViewById(R.id.tvCategoryName);
            tvItemName                  = itemView.findViewById(R.id.tvItemName);
            tvItemPrice                 = itemView.findViewById(R.id.tvItemPrice);
            tvItemDescription           = itemView.findViewById(R.id.tvItemDescription);
            ivItemImage                 = itemView.findViewById(R.id.ivItemImage);
            ivDeleteItem                = itemView.findViewById(R.id.ivDeleteItem);
            ivUnAvailable               = itemView.findViewById(R.id.ivUnAvailable);
            llAddToCart                 = itemView.findViewById(R.id.llAddToCart);
            tvMinus                     = itemView.findViewById(R.id.tvMinus);
            tvPlus                      = itemView.findViewById(R.id.tvPlus);
            tvQty                       = itemView.findViewById(R.id.tvQty);
        }
    }

}
