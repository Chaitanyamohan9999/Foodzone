package com.food.foodzone.activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.MenuItemDo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MenuListActivity extends BaseActivity {

    private static final String TAG = "MenuList";
    private View llMenu;
    private RecyclerView rvMenuList;
    private TextView tvNoData;
    private Button btnContinue;
    private MenuListAdapter menuListAdapter;
    private FloatingActionButton fabAddItem;

    @Override
    public void initialise() {
        llMenu =  inflater.inflate(R.layout.menu_list_layout, null);
        addBodyView(llMenu);
        lockMenu();
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        tvTitle.setText("Menu");
        initialiseControls();
        if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)){
            flCart.setVisibility(View.VISIBLE);
            fabAddItem.setVisibility(View.GONE);
            btnContinue.setVisibility(View.VISIBLE);
        }
        else {
            flCart.setVisibility(View.GONE);
            fabAddItem.setVisibility(View.VISIBLE);
            btnContinue.setVisibility(View.GONE);
        }
        rvMenuList.setLayoutManager(new LinearLayoutManager(MenuListActivity.this));
        menuListAdapter = new MenuListAdapter(MenuListActivity.this, new ArrayList<MenuItemDo>());
        rvMenuList.setAdapter(menuListAdapter);
        fabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuListActivity.this, AddMenuItemActivity.class);
                startActivityForResult(intent, 103);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppConstants.Cart_Items != null && AppConstants.Cart_Items.size()>0) {

                }
            }
        });
        getData();
    }

    private void initialiseControls() {
        rvMenuList                  = llMenu.findViewById(R.id.rvMenuList);
        tvNoData                    = llMenu.findViewById(R.id.tvNoData);
        btnContinue                 = llMenu.findViewById(R.id.btnContinue);
        fabAddItem                  = llMenu.findViewById(R.id.fabAddItem);
    }

    @Override
    public void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Item);
        showLoader();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hideLoader();
                ArrayList<MenuItemDo> menuItemDos = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    MenuItemDo menuItemDo = postSnapshot.getValue(MenuItemDo.class);
                    Log.e("Get Data", menuItemDo.toString());
                    menuItemDos.add(menuItemDo);
                }
                if(menuItemDos.size() > 0){
                    tvNoData.setVisibility(View.GONE);
                    rvMenuList.setVisibility(View.VISIBLE);
                    menuListAdapter.refreshAdapter(menuItemDos);
                }
                else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rvMenuList.setVisibility(View.GONE);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 103 && resultCode == 103) {
            getData();
        }
    }

    private void deleteItem(String tableId) {
        showLoader();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Item);
        databaseReference.child(tableId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                hideLoader();
                showAppCompatAlert("", "Congratulations! You have successfully deleted a Item.", "OK", "", "DeleteItem", false);
            }
        });
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if (from.equalsIgnoreCase("DeleteItem")) {
            getData();
        }
    }

    private class MenuListAdapter extends RecyclerView.Adapter<MenuHolder> {

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
            if(menuItemDos.get(position).isAvailable){
                holder.ivUnAvailable.setVisibility(View.GONE);
            }
            else {
                holder.ivUnAvailable.setVisibility(View.VISIBLE);
            }
            if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
                holder.llAddToCart.setVisibility(View.VISIBLE);
                holder.ivDeleteItem.setVisibility(View.GONE);
            }
            else {
                holder.llAddToCart.setVisibility(View.GONE);
                holder.ivDeleteItem.setVisibility(View.VISIBLE);
            }
            holder.tvMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!holder.tvQty.getText().toString().equalsIgnoreCase("")){
                        int qty = Integer.parseInt(holder.tvQty.getText().toString());
                        if(qty > 1){
                            qty = qty - 1;
                            holder.tvQty.setText(""+qty);
                        }
                        else {
                            holder.tvQty.setText("0");
                        }
                    }
                }
            });
            holder.tvPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!holder.tvQty.getText().toString().equalsIgnoreCase("")){
                        int qty = Integer.parseInt(holder.tvQty.getText().toString());
                        if(qty < 100) {
                            qty = qty + 1;
                            holder.tvQty.setText("" + qty);
                        }
                        else {
                            Toast.makeText(context, "You cannot add more than "+qty+" items", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            holder.ivDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(menuItemDos.get(position).itemId);
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
                        Intent intent = new Intent(MenuListActivity.this, AddMenuItemActivity.class);
                        intent.putExtra("MenuItemDo", menuItemDos.get(position));
                        startActivityForResult(intent, 103);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
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
