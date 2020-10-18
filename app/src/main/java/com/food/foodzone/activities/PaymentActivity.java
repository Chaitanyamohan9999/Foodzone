package com.food.foodzone.activities;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.PaymentDo;
import com.food.foodzone.utils.PreferenceUtils;

import java.util.Random;

public class PaymentActivity extends BaseActivity {

    private View llPayments;
    private Button btnProceed;
    private EditText etName, etCardNumber, etExpiryDate, etCVV;
    private LinearLayout llPaymentBody;
    private RadioButton rbPayByCard, rbPayByCash;
    private RadioGroup rgPaymentType;
    private String paymentType = "Card", from = "";
    private double amount;

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
        if (getIntent().hasExtra("From")){
            from = getIntent().getStringExtra("From");
        }
        initialiseControls();
        if(from.equalsIgnoreCase("DineInNow")) {
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
                        Toast.makeText(PaymentActivity.this, "Your order has been placed successfully!", Toast.LENGTH_LONG).show();
                        AppConstants.Cart_Items.clear();
                        Intent intent = new Intent(PaymentActivity.this, CustomerDashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                else {
                    Toast.makeText(PaymentActivity.this, "Your order has been placed successfully!", Toast.LENGTH_LONG).show();
                    AppConstants.Cart_Items.clear();
                    Intent intent = new Intent(PaymentActivity.this, CustomerDashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
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

    private void insertPaymentDetails() {
        String paymentId = "P"+new Random().nextInt();
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        String userName = preferenceUtils.getStringFromPreference(PreferenceUtils.UserName, "");
        PaymentDo paymentDo = new PaymentDo(paymentId, paymentType, userId, userName, etCardNumber.getText().toString().trim(),
                etExpiryDate.getText().toString().trim(), etCVV.getText().toString().trim(), amount);
    }
}
