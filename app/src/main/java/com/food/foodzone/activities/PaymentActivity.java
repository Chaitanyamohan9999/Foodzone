package com.food.foodzone.activities;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.OrderDo;
import com.food.foodzone.models.PaymentDo;
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
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
/** Customer can process payment the order amount by cash or credit/debit card. */
public class PaymentActivity extends BaseActivity {

    private static final String TAG = "PaymentActivity";
    private View llPayments;
    private Button btnProceed;
    private ImageView ivCardType;
    private Spinner spMonth, spYear;
    private EditText etName, etCardNumber, etExpiryDate, etCVV;
    private LinearLayout llPaymentBody;
    private RadioButton rbPayByCard, rbPayByCash;
    private RadioGroup rgPaymentType;
    private TextView tvPickupTime;
    private String paymentType = "Card", from = "", pickupTime = "", pickupMessage = "";
    private double amount;
    private String selectedMonth = "", selectedYear = "";
    private TableDo tableDo;
    private OrderDo mOrderDo;
    private static final char space = ' ';

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
        ivCardType.setVisibility(View.GONE);
        final ArrayList<String> monthsList = AppConstants.getMonths();
        final ArrayList<String> yearsList = AppConstants.getYears();

        spMonth.setAdapter(new ArrayAdapter<String>(PaymentActivity.this, R.layout.spinner_dropdown, monthsList){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View row = LayoutInflater.from(PaymentActivity.this).inflate(R.layout.spinner_dropdown, parent, false);
                final TextView tvDropdown = (TextView)row.findViewById(R.id.tvDropdown);
                tvDropdown.setText(monthsList.get(position));
                return row;
            }
        });
        spMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position > 0){
                    selectedMonth = monthsList.get(position);
                }
                else {
                    selectedMonth = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spYear.setAdapter(new ArrayAdapter<String>(PaymentActivity.this, R.layout.spinner_dropdown, yearsList){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View row = LayoutInflater.from(PaymentActivity.this).inflate(R.layout.spinner_dropdown, parent, false);
                final TextView tvDropdown = (TextView)row.findViewById(R.id.tvDropdown);
                tvDropdown.setText(yearsList.get(position));
                return row;
            }
        });
        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position > 0){
                    selectedYear = yearsList.get(position);
                }
                else {
                    selectedYear = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        etCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().startsWith("4536")) {
                    ivCardType.setVisibility(View.VISIBLE);
                    ivCardType.setImageResource(R.drawable.visa_icon);
                }
//                else if(s.toString().startsWith("50")) {
//                    ivCardType.setVisibility(View.VISIBLE);
//                    ivCardType.setImageResource(R.drawable.maestro_icon);
//                }
                else if(s.toString().startsWith("4537")) {
                    ivCardType.setVisibility(View.VISIBLE);
                    ivCardType.setImageResource(R.drawable.master_icon);
                }
                else if(s.toString().startsWith("")) {
                    ivCardType.setVisibility(View.VISIBLE);
                    ivCardType.setImageResource(R.drawable.dummy_card_icon);
                }
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                // Insert char where needed.
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    char c = s.charAt(s.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(space));
                    }
                }

            }
        });
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
        ivCardType      = findViewById(R.id.ivCardType);
        spMonth         = findViewById(R.id.spMonth);
        spYear          = findViewById(R.id.spYear);
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
            errorMsg = "Please Enter Card Number";
        }
        else if (etCardNumber.getText().toString().replace(" ", "").length() != 16) {
            errorMsg = "Please valid Card Number";
        }
        else if (selectedMonth.equalsIgnoreCase("")) {
            errorMsg = "Please select Month";
        }
        else if (selectedYear.equalsIgnoreCase("")) {
            errorMsg = "Please select Year";
        }
        else if(!isValidExpDate()) {
            errorMsg = "Please select valid date";
        }
        else if (etCVV.getText().toString().trim().isEmpty()) {
            errorMsg = "Please Enter CVV";
        }
        return errorMsg;
    }

    private boolean isValidExpDate() {
        try {
            Calendar calendar = Calendar.getInstance();
            if(calendar.get(Calendar.YEAR) < Integer.parseInt(selectedYear)) {
                return true;
            }
            else if(calendar.get(Calendar.YEAR) == Integer.parseInt(selectedYear)) {
                if((calendar.get(Calendar.MONTH)+1) <= Integer.parseInt(selectedMonth)){
                    return true;
                }
            }
            else {
                return false;
            }
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
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
                        payAmount(orderDo.orderId);
                    }
                });

    }

    private void payAmount(String orderId) {
        PaymentDo paymentDo = new PaymentDo();
        try {
            paymentDo.paymentId = ""+System.currentTimeMillis();
            paymentDo.orderId = orderId;
            paymentDo.paymentType = paymentType;
            paymentDo.cardNumber = etCardNumber.getText().toString().replace(" ", "");
            paymentDo.cardCvv = etCVV.getText().toString();
            paymentDo.personName = etName.getText().toString();
            paymentDo.amount = amount;
            paymentDo.expDate = selectedMonth+selectedYear;
            paymentDo.personId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Payments);
        showLoader();
        databaseReference.child(paymentDo.paymentId).setValue(paymentDo).
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
