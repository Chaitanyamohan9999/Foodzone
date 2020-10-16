package com.food.foodzone.activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.MenuItemDo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CartListActivity extends BaseActivity {

    private static final String TAG = "CartList";
    private View llMenu;
    private RecyclerView rvCartList;
    private TextView tvPrice, tvDiscount, tvDeliveryCharges, tvTotalAmount;
    private Button btnPlaceOrder;
    private CartListAdapter cartListAdapter;

    @Override
    public void initialise() {
        llMenu =  inflater.inflate(R.layout.cart_list_layout, null);
        addBodyView(llMenu);
        lockMenu();
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        tvTitle.setText("Cart");
        initialiseControls();
        rvCartList.setLayoutManager(new LinearLayoutManager(CartListActivity.this));
        cartListAdapter = new CartListAdapter(CartListActivity.this, new ArrayList<MenuItemDo>());
        rvCartList.setAdapter(cartListAdapter);
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        getData();
    }

    private void initialiseControls() {
        rvCartList                  = llMenu.findViewById(R.id.rvCartList);
        tvPrice                     = llMenu.findViewById(R.id.tvPrice);
        tvDiscount                  = llMenu.findViewById(R.id.tvDiscount);
        tvDeliveryCharges           = llMenu.findViewById(R.id.tvDeliveryCharges);
        tvTotalAmount               = llMenu.findViewById(R.id.tvTotalAmount);
        btnPlaceOrder                 = llMenu.findViewById(R.id.btnPlaceOrder);
    }

    @Override
    public void getData() {
        cartListAdapter.refreshAdapter(AppConstants.Cart_Items);
    }

    private void calcTotalAmount(ArrayList<MenuItemDo> menuItemDos) {
        tvDeliveryCharges.setText("");
        tvPrice.setText("");
        tvDiscount.setText("");
        tvTotalAmount.setText("");
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

        private TextView tvItemName, tvItemPrice, tvItemDescription, tvMinus, tvPlus, tvQty;
        private ImageView ivItemImage, ivDeleteItem,ivUnAvailable;
        private LinearLayout llAddToCart;

        public MenuHolder(@NonNull View itemView) {
            super(itemView);
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
