package com.food.foodzone.activities;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;


public class UserTypeActivity extends BaseActivity {

    private View llUserType;
    private Button btnCustomer, btnEmployee;
    private TextView tvContinue;
    private String userType = "";

    @Override
    public void initialise() {
        llUserType = inflater.inflate(R.layout.user_type_layout, null);
        addBodyView(llUserType);
        lockMenu();
        ivBack.setVisibility(View.GONE);
        ivMenu.setVisibility(View.GONE);
        flCart.setVisibility(View.GONE);
        llToolbar.setVisibility(View.VISIBLE);

        btnCustomer         = llUserType.findViewById(R.id.btnCustomer);
        btnEmployee         = llUserType.findViewById(R.id.btnEmployee);
        tvContinue          = llUserType.findViewById(R.id.tvContinue);

        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCustomer.setBackgroundResource(R.drawable.btn_bg);
                btnCustomer.setTextColor(getResources().getColor(R.color.white));
                btnEmployee.setBackgroundResource(R.drawable.edit_text_bg);
                btnEmployee.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                userType = btnCustomer.getText().toString();

            }
        });
        btnEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEmployee.setBackgroundResource(R.drawable.btn_bg);
                btnEmployee.setTextColor(getResources().getColor(R.color.white));
                btnCustomer.setBackgroundResource(R.drawable.edit_text_bg);
                btnCustomer.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                userType = btnEmployee.getText().toString();
            }
        });
        tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userType.equalsIgnoreCase("")){
                    showToast("Please select user type");
                }
                else {
                    moveToLogin();
                }
            }
        });
    }

    private void moveToLogin(){
        Intent intent = new Intent(UserTypeActivity.this, LoginActivity.class);
        intent.putExtra(AppConstants.User_Type, userType);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);

    }
    @Override
    public void getData() {

    }
}
