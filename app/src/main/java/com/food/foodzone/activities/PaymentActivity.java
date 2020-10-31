package com.food.foodzone.activities;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.OrderDo;
import com.food.foodzone.models.TableDo;
import com.food.foodzone.utils.PreferenceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class PaymentActivity extends BaseActivity {

    private static final String TAG = "PaymentActivity";
    private View llPayments;
    private Button btnProceed;
    private EditText etName, etCardNumber, etExpiryDate, etCVV;
    private LinearLayout llPaymentBody;
    private RadioButton rbPayByCard, rbPayByCash;
    private RadioGroup rgPaymentType;
    private TextView tvPickupTime;
    private String paymentType = "Card", from = "", pickupTime = "", pickupMessage = "";
    private double amount;
    private TableDo tableDo;
    private OrderDo mOrderDo;

    @Override
    public void initialise() {
        llPayments = inflater.inflate(R.layout.payment_layout, null);
        addBodyView(llPayments);
        lockMenu();
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        flCart.setVisibility(View.GONE);
        ivMenu.setVisibility(View.GONE);
        tvTitle.setText("Payment");
        if (getIntent().hasExtra("Amount")){
            amount = getIntent().getDoubleExtra("Amount", 0);
        }
        if (getIntent().hasExtra("TableDo")){
            tableDo = (TableDo) getIntent().getSerializableExtra("TableDo");
        }
        if (getIntent().hasExtra("OrderDo")){
            mOrderDo = (OrderDo) getIntent().getSerializableExtra("OrderDo");
        }
        if (getIntent().hasExtra("From")){
            from = getIntent().getStringExtra("From");
        }
        if (getIntent().hasExtra("PickupTime")){
            pickupTime = getIntent().getStringExtra("PickupTime");
        }
        if (getIntent().hasExtra("PickupMessage")){
            pickupMessage = getIntent().getStringExtra("PickupMessage");
        }
        initialiseControls();
        tvPickupTime.setText(pickupMessage);
        if(from.equalsIgnoreCase(AppConstants.DineInNow)) {
            rgPaymentType.setVisibility(View.VISIBLE);
        }
        else {
            rgPaymentType.setVisibility(View.GONE);
        }
        rgPaymentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbPayByCard){
                    paymentType = "Card";
                    paymentType(1f, true);
                }
                else if(checkedId == R.id.rbPayByCash){
                    paymentType = "Cash";
                    paymentType(0.3f, false);
                }
            }
        });

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rbPayByCard.isChecked()) {
                    String errorMessage = validateCard();
                    if (!errorMessage.equalsIgnoreCase("")){
                        Toast.makeText(PaymentActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                    else {
                        if(from.equalsIgnoreCase(AppConstants.TakeOut)) {
                            placeOrder();
                        }
                        else {
                            isTableReserved(tableDo);
                        }
                    }
                }
                else {
                    if(from.equalsIgnoreCase(AppConstants.TakeOut)) {
                        placeOrder();
                    }
                    else {
                        isTableReserved(tableDo);
                    }
                }
            }
        });
    }

    private void reserveTable() {
        showLoader();
        tableDo.reservedBy = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        if(from.equalsIgnoreCase(AppConstants.DineInNow)) {
            tableDo.reservedAt = System.currentTimeMillis()+3600000;// adding 1 hour
            tableDo.reservedFor = 1; // 1 hour
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Tables);
        databaseReference.child(tableDo.tableId).setValue(tableDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        placeOrder();
                    }
                });
    }
    private void initialiseControls() {
        btnProceed      = findViewById(R.id.btnProceed);
        etName          = findViewById(R.id.etName);
        etCardNumber    = findViewById(R.id.etCardNumber);
        etExpiryDate    = findViewById(R.id.etExpiryDate);
        etCVV           = findViewById(R.id.etCVV);
        rbPayByCard     = findViewById(R.id.rbPayByCard);
        rbPayByCash     = findViewById(R.id.rbPayByCash);
        tvPickupTime    = findViewById(R.id.tvPickupTime);
        rgPaymentType   = findViewById(R.id.rgPaymentType);
        llPaymentBody   = findViewById(R.id.llPaymentBody);
    }

    private void paymentType(float alpha, boolean isPayByCard) {
        llPaymentBody.setAlpha(alpha);
        etName.setEnabled(isPayByCard);
        etCardNumber.setEnabled(isPayByCard);
        etExpiryDate.setEnabled(isPayByCard);
        etCVV.setEnabled(isPayByCard);
        etName.setText("");
        etCardNumber.setText("");
        etExpiryDate.setText("");
        etCVV.setText("");
    }

    @Override
    public void getData() {}

    private String validateCard() {
        String errorMsg = "";
        if (etName.getText().toString().trim().isEmpty()) {
            errorMsg = "Please Enter Name";
        }
        else if (etCardNumber.getText().toString().trim().isEmpty()) {
            errorMsg = "Please Enter CardNumber";
        }
        else if (etExpiryDate.getText().toString().trim().isEmpty()) {
            errorMsg = "Please Enter Date";
        }
        else if (etCVV.getText().toString().trim().isEmpty()) {
            errorMsg = "Please Enter CVV";
        }
        return errorMsg;
    }

    private void placeOrder() {
        final OrderDo orderDo = new OrderDo();
        orderDo.orderId = ""+System.currentTimeMillis();
        orderDo.customerEmail = preferenceUtils.getStringFromPreference(PreferenceUtils.EmailId, "");
        orderDo.customerName = preferenceUtils.getStringFromPreference(PreferenceUtils.UserName, "");
        orderDo.customerId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        orderDo.customerPhone = preferenceUtils.getStringFromPreference(PreferenceUtils.PhoneNo, "");
        orderDo.pickupTime = pickupTime;
        orderDo.orderType = from;
        orderDo.paymentType = paymentType;
        orderDo.totalAmount = amount;
        orderDo.reservedAt = System.currentTimeMillis();
        orderDo.orderStatus = AppConstants.Status_Pending;
        orderDo.menuItemDos = AppConstants.Cart_Items;
        if(from.equalsIgnoreCase("Reservations")){
            orderDo.orderId = mOrderDo.orderId;
            orderDo.pickupTime = mOrderDo.pickupTime;
            orderDo.orderType = mOrderDo.orderType;
            orderDo.pickupTime = mOrderDo.pickupTime;
        }
        if(!from.equalsIgnoreCase(AppConstants.TakeOut)) {
            orderDo.tableId = tableDo.tableId;
            orderDo.tableNumber = tableDo.tableNumber;
            orderDo.reservedAt = tableDo.reservedAt;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Orders);
        showLoader();
        databaseReference.child(orderDo.orderId).setValue(orderDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        showAppCompatAlert("", "Your order has been placed successfully!", "OK", "", "PlaceOrder", false);
                    }
                });

    }

    private void isTableReserved(final TableDo bookingTableDo) {
        showLoader();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Tables);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hideLoader();
                boolean isTableReserved = false;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    TableDo tableDo = postSnapshot.getValue(TableDo.class);
                    if(tableDo.tableId.equalsIgnoreCase(bookingTableDo.tableId)
                            && !tableDo.reservedBy.equalsIgnoreCase("")) {
                        isTableReserved = true;
                        break;
                    }
                }
                if(isTableReserved) {
                    showAppCompatAlert("", "Sorry, this table is already reserved. Please choose another table", "Ok", "", "", false);
                }
                else {
                    reserveTable();
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
        if (from.equalsIgnoreCase("PlaceOrder")) {
            AppConstants.Cart_Items.clear();
            Intent intent = new Intent(PaymentActivity.this, CustomerDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }
}
