package com.food.foodzone.activities;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoginActivity extends com.food.foodzone.activities.BaseActivity {

    private View llLogin;
    private LinearLayout llUserType, llForCustomer;
    private EditText etEmail, etPassword;
    private TextView tvForgotPassword, tvRegister, tvLogin, tvUserTypeLabel;
    private Spinner spUserRole;
    private String userType = "", userRole = "";

    @Override
    public void initialise() {
        llLogin = inflater.inflate(R.layout.login_layout, null);
        addBodyView(llLogin);
        lockMenu();
        tvTitle.setText("Login");
        ivBack.setVisibility(View.GONE);
        ivMenu.setVisibility(View.GONE);
        llToolbar.setVisibility(View.VISIBLE);
        initialiseControls();
        if(getIntent().hasExtra(AppConstants.User_Type)){
            userType = getIntent().getExtras().getString(AppConstants.User_Type);
        }
        if(userType.equalsIgnoreCase(AppConstants.Customer_Role)){
            tvUserTypeLabel.setVisibility(View.GONE);
            llUserType.setVisibility(View.GONE);
            llForCustomer.setVisibility(View.VISIBLE);
        }
        else {
            tvUserTypeLabel.setVisibility(View.VISIBLE);
            llUserType.setVisibility(View.VISIBLE);
            llForCustomer.setVisibility(View.GONE);
        }
        final ArrayList<String> userRolesList = new ArrayList<>();
        userRolesList.add("Select User Role");
        userRolesList.add("Chef");
        userRolesList.add("Manager");

        spUserRole.setAdapter(new ArrayAdapter<String>(LoginActivity.this, R.layout.spinner_dropdown, userRolesList){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View row = LayoutInflater.from(LoginActivity.this).inflate(R.layout.spinner_dropdown, parent, false);
                final TextView tvDropdown = (TextView)row.findViewById(R.id.tvDropdown);
                tvDropdown.setText(userRolesList.get(position));
                return row;
            }
        });
        spUserRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position > 0){
                    userRole = userRolesList.get(position);
                }
                else {
                    userRole = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard(v.getRootView());
                if (etEmail.getText().toString().equalsIgnoreCase("")) {
                    showErrorMessage("Please enter email");
                }
                else if (!isValidEmail(etEmail.getText().toString().trim())) {
                    showErrorMessage("Please enter valid email");
                }
                else if (etPassword.getText().toString().equalsIgnoreCase("")) {
                    showErrorMessage("Please enter password");
                }
                else if (etPassword.getText().toString().trim().length() < 6) {
                    showErrorMessage("Please enter password minimum 6 characters");
                }
                else if (!userType.equalsIgnoreCase(AppConstants.Customer_Role) && userRole.equalsIgnoreCase("")) {
                    showErrorMessage("Please select user type");
                }
                else {
                    if (isNetworkConnectionAvailable(LoginActivity.this)) {
                         doLogin();
                    } else {
                        showInternetDialog("Login");
                    }
                }
            }
        });
    }

    private void initialiseControls() {
        etEmail             = findViewById(R.id.etEmail);
        etPassword          = findViewById(R.id.etPassword);
        spUserRole          = findViewById(R.id.spUserRole);
        tvUserTypeLabel     = findViewById(R.id.tvUserTypeLabel);
        llUserType          = findViewById(R.id.llUserType);
        llForCustomer       = findViewById(R.id.llForCustomer);
        tvRegister          = findViewById(R.id.tvRegister);
        tvForgotPassword    = findViewById(R.id.tvForgotPassword);
        tvLogin             = findViewById(R.id.tvLogin);
    }

    private void doLogin(){
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.putExtra(AppConstants.User_Role, userRole);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    public void getData() {

    }
}
