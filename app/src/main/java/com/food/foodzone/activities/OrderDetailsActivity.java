package com.food.foodzone.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import com.food.foodzone.models.OrderDo;
import com.food.foodzone.models.TableDo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/**OrderDetailsActivity is uses to display the orders information**/

public class OrderDetailsActivity extends BaseActivity {

    private LinearLayout llDetails;
    private RecyclerView rvMenuList;
    private TextView tvOrderId, tvAmount, tvCustomerName, tvPickupTime, tvCustomerPhone,
            tvCustomerEmail, tvPaymentType, tvOrderType, tvOrderStatus;
    private Button btnCancel, btnApprove;
    private LinearLayout llPhone, llEmail, llPickup, llAddMenu;
    private MenuListAdapter menuListAdapter;
    private OrderDo orderDo;
    private TableDo tableDo;

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
        if(getIntent().hasExtra("TableDo")) {
            tableDo = (TableDo) getIntent().getSerializableExtra("TableDo");
        }
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        initialiseControls();
        btnCancel.setVisibility(View.GONE);
        if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
            if(orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Pending)) {
                if (orderDo.orderType.equalsIgnoreCase(AppConstants.DineInLater)) {
                    btnApprove.setVisibility(View.VISIBLE);
                    btnApprove.setText("ARRIVE");
                    btnCancel.setVisibility(View.VISIBLE);
                    btnCancel.setText("CANCEL");
                }
                else if (orderDo.orderType.equalsIgnoreCase(AppConstants.DineInNow)) {
                    btnApprove.setVisibility(View.VISIBLE);
                    btnApprove.setText("CANCEL");
                }
                else {
                    btnApprove.setVisibility(View.VISIBLE);
                    btnApprove.setText("ARRIVE");
                    btnCancel.setVisibility(View.VISIBLE);
                    btnCancel.setText("CANCEL");
                }
            }
            else if (orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Accepted)) {
//                btnApprove.setVisibility(View.VISIBLE);
//                btnApprove.setText("CANCEL");
                btnApprove.setVisibility(View.VISIBLE);
                btnApprove.setText("ARRIVE");
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setText("CANCEL");
            }
            else {
                btnApprove.setVisibility(View.GONE);
                btnApprove.setText("");
            }
        }
        else {
            if(orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Pending)){
                btnApprove.setVisibility(View.VISIBLE);
                btnApprove.setText("ACCEPT ORDER");
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setText("REJECT");
            }
            else {
                btnApprove.setVisibility(View.GONE);
                btnApprove.setText("");
                btnCancel.setVisibility(View.GONE);
                btnCancel.setText("");
            }
        }
        bindData();
        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
                    if(orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Pending)) {
                        if(orderDo.orderType.equalsIgnoreCase(AppConstants.DineInLater)
                                || orderDo.orderType.equalsIgnoreCase(AppConstants.TakeOut)) {
                            if(isFoodZoneArea(mLocation)) {
                                if(orderDo.menuItemDos != null && orderDo.menuItemDos.size()>0) {
                                    actionOnOrder(AppConstants.Status_Arrived);
                                }
                                else {
                                    showAppCompatAlert("", "Please add menu items", "Add Menu", "Cancel", "AddMenu", false);
                                }
                            }
                            else {
                                showAppCompatAlert("", "You are not in FoodZone area to reserve a table now.", "Ok", "", "", false);
                            }
                        }
                        else {
                            actionOnOrder(AppConstants.Status_Cancelled);
                        }
                    }
                    else if(orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Accepted)) {
                        if(isFoodZoneArea(mLocation)) {
                            if(orderDo.menuItemDos != null && orderDo.menuItemDos.size()>0) {
                                actionOnOrder(AppConstants.Status_Arrived);
                            }
                            else {
                                showAppCompatAlert("", "Please add menu items", "Add Menu", "Cancel", "AddMenu", false);
                            }
                        }
                        else {
                            showAppCompatAlert("", "You are not in FoodZone area to reserve a table now.", "Ok", "", "", false);
                        }
                    }
                }
                else {
                    if(orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Pending)) {
                        actionOnOrder(AppConstants.Status_Accepted);
                    }
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnCancel.getText().toString().equalsIgnoreCase("Cancel")) {
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
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{  orderDo.customerEmail});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "FoodZone Order Status");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hi,\n Your FoodZone order");
                emailIntent.setType("message/rfc822");
                try {
                    startActivity(Intent.createChooser(emailIntent, "Sending email..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context, "No email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)
                && btnApprove.getText().toString().equalsIgnoreCase("Arrive")) {
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
                ActivityCompat.requestPermissions(OrderDetailsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1210);
            }
        }
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
        llPickup                    = llDetails.findViewById(R.id.llPickup);
        llAddMenu                   = llDetails.findViewById(R.id.llAddMenu);
        rvMenuList                  = llDetails.findViewById(R.id.rvMenuList);
        btnCancel                   = llDetails.findViewById(R.id.btnCancel);
        btnApprove                  = llDetails.findViewById(R.id.btnApprove);
    }

    private void bindData() {
        tvOrderId.setText(orderDo.orderId);
        tvAmount.setText("$"+AppConstants.Decimal_Number.format(orderDo.totalAmount));
        tvCustomerName.setText("C.Name : "+orderDo.customerName);
        if(orderDo.orderType.equalsIgnoreCase(AppConstants.TakeOut)) {
            llPickup.setVisibility(View.VISIBLE);
        }
        else {
            llPickup.setVisibility(View.GONE);
        }
        tvPickupTime.setText(getPickupTime(orderDo.pickupTime));
        tvCustomerPhone.setText(orderDo.customerPhone);
        tvCustomerEmail.setText(orderDo.customerEmail);
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
        if(orderDo.menuItemDos != null && orderDo.menuItemDos.size()>0) {
            menuListAdapter = new MenuListAdapter(OrderDetailsActivity.this, orderDo.menuItemDos);
            rvMenuList.setAdapter(menuListAdapter);
            llAddMenu.setVisibility(View.GONE);
            rvMenuList.setVisibility(View.VISIBLE);
        }
        else {
            if(orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Pending)
                    || orderDo.orderStatus.equalsIgnoreCase(AppConstants.Status_Accepted)) {
                if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
                    llAddMenu.setVisibility(View.VISIBLE);
                }
                else {
                    llAddMenu.setVisibility(View.INVISIBLE);
                }
            }
            else {
                llAddMenu.setVisibility(View.GONE);
            }
            rvMenuList.setVisibility(View.GONE);
        }
        llAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailsActivity.this, MenuListActivity.class);
                intent.putExtra(AppConstants.From, "Reservations");
                intent.putExtra("OrderDo", orderDo);
                intent.putExtra("TableDo", tableDo);
                startActivityForResult(intent, 150);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }
    @Override
    public void getData() {

    }

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
                ActivityCompat.requestPermissions(OrderDetailsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1210);
            }
        }
    }

    private void actionOnOrder(final String orderAction) {
        orderDo.orderStatus = orderAction;
        if (AppConstants.Status_Rejected.equalsIgnoreCase(orderAction)
                || AppConstants.Status_Cancelled.equalsIgnoreCase(orderAction)){
            getTableByOrder(orderDo.tableId, "", 0);
            orderDo.reservedAt = 0;
        }
        else {
            orderDo.reservedAt = System.currentTimeMillis();
            getTableByOrder(orderDo.tableId, orderAction, orderDo.reservedAt);
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Orders);
        showLoader();
        databaseReference.child(orderDo.orderId).setValue(orderDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        showAppCompatAlert("", "The order has been "+orderAction+" successfully!", "OK", "", "AcceptOrder", false);
                    }
                });
    }

    private void getTableByOrder(final String tableId, final String tableStatus, final long actionTime) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Tables);
        showLoader();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hideLoader();
                ArrayList<TableDo> tableDos = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    TableDo tableDo = postSnapshot.getValue(TableDo.class);
                    Log.e("Get Data", tableDo.toString());
                    if (tableDo.tableId.equalsIgnoreCase(tableId)){
                        updateStatus(tableDo, tableStatus, actionTime);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoader();
            }
        });
    }

    private void updateStatus(final TableDo tableDo, String tableStatus, long actionTime) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tableDo.tableStatus = tableStatus;
        if(actionTime == 0) {
            tableDo.reservedBy = "";
            tableDo.reservedAt = 0;
            tableDo.reservedFor = 0;
        }
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Tables);
        databaseReference.child(tableDo.tableId).setValue(tableDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                    }
                });
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if(from.equalsIgnoreCase("AcceptOrder")) {
            setResult(5001);
            finish();
        }
        else if (from.equalsIgnoreCase("EnableGPS")) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        else if (from.equalsIgnoreCase("AddMenu")) {
            llAddMenu.performClick();
        }
    }

    @Override
    public void onButtonNoClick(String from) {
        super.onButtonNoClick(from);
        if(from.equalsIgnoreCase("EnableGPS")) {
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
            holder.tvQty.setText(""+AppConstants.TwoDigitsNumber.format(menuItemDos.get(position).quantity));
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
