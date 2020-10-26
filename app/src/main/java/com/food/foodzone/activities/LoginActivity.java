package com.food.foodzone.activities;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.food.foodzone.R;
import com.food.foodzone.common.*;
import com.food.foodzone.utils.PreferenceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoginActivity extends BaseActivity {

    private View llLogin;
    private LinearLayout llUserType, llForCustomer;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister, tvUserTypeLabel;
    private Spinner spUserRole;
    private String userType = "", userRole = "";
    private FirebaseAuth auth;

    @Override
    public void initialise() {
        llLogin = inflater.inflate(R.layout.login_layout, null);
        addBodyView(llLogin);
        lockMenu();
        auth = FirebaseAuth.getInstance();
        tvTitle.setText("Login");
        flCart.setVisibility(View.GONE);
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
            etEmail.setText("yamini@gmail.com");
            etPassword.setText("123456");
        }
        else {
            tvUserTypeLabel.setVisibility(View.VISIBLE);
            llUserType.setVisibility(View.VISIBLE);
            llForCustomer.setVisibility(View.GONE);
            etEmail.setText("yaminireddy@gmail.com");
            etPassword.setText("yamini");
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
                    userType = userRole = userRolesList.get(position);
                }
                else {
                    userType = userRole = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra(AppConstants.User_Type, userType);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

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

        btnLogin.setOnClickListener(new View.OnClickListener() {
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
                        if(userType.equalsIgnoreCase(AppConstants.Customer_Role)) {
                            showLoader();
                            auth.signInWithEmailAndPassword(etEmail.getText().toString().trim(), etPassword.getText().toString().trim())
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            hideLoader();
                                            if (task.isSuccessful()) {
                                                doLogin();
                                            }
                                            else {
                                                showAppCompatAlert("", getString(R.string.auth_failed), "Register", "Cancel", "AuthFailed", false);
                                            }
                                        }
                                    });
                        }
                        else {
                            doLogin();
                        }
                    }
                    else {
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
        btnLogin            = findViewById(R.id.btnLogin);
    }

    private void doLogin(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Users);
        showLoader();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userId = etEmail.getText().toString().trim().replace("@", "").replace(".", "");
                if(dataSnapshot!=null && dataSnapshot.exists()){
                    hideLoader();
                    HashMap hashMap = (HashMap) ((HashMap) dataSnapshot.getValue()).get(userId);
                    if (hashMap != null && hashMap.get("userId").toString().equalsIgnoreCase(userId)
                            && hashMap.get("password").toString().equalsIgnoreCase(etPassword.getText().toString().trim())) {
                        String loggedInUser = hashMap.get("userType").toString();
                        if(loggedInUser.equalsIgnoreCase(userType)){
                            preferenceUtils.saveString(PreferenceUtils.UserId, userId);
                            preferenceUtils.saveString(PreferenceUtils.UserName, hashMap.get("name").toString());
                            preferenceUtils.saveString(PreferenceUtils.EmailId, etEmail.getText().toString().trim());
                            preferenceUtils.saveString(PreferenceUtils.PhoneNo, hashMap.get("phone").toString());
                            preferenceUtils.saveString(PreferenceUtils.Password, etPassword.getText().toString().trim());
                            Intent intent = null;
                            if(loggedInUser.equalsIgnoreCase(AppConstants.Customer_Role)) {
                                intent = new Intent(LoginActivity.this, CustomerDashboardActivity.class);
                            }
                            else {
                                intent = new Intent(LoginActivity.this, EmployeeDashboardActivity.class);
                            }
                            intent.putExtra(AppConstants.User_Type, userType);
                            AppConstants.LoggedIn_User_Type = userType;
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            finish();
                        }
                        else {// invalid user type
                            showAppCompatAlert("", "Invalid User Type", "Ok", "", "", false);
                        }
                    }
                    else {
                        //Username does not exist
                        hideLoader();
                        databaseReference.orderByChild("userId")
                                .equalTo(etEmail.getText().toString().trim()).removeEventListener(this);
//                        etPassword.setText("");
                        showAppCompatAlert("", "The entered email and password are not exist.", "OK", "", "", true);
                    }
                }
                else {
                    //Username does not exist
                    hideLoader();
                    databaseReference.child(AppConstants.Table_Users).orderByChild("userId")
                            .equalTo(etEmail.getText().toString().trim()).removeEventListener(this);
//                    etPassword.setText("");
                    showAppCompatAlert("", "The entered email and password are not exist.", "OK", "", "", true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoader();
                Log.e("LoginActivity", "Failed to reading email.", databaseError.toException());
            }
        });
    }

    private void moveToDashboard(){
        preferenceUtils.saveString(PreferenceUtils.EmailId, etEmail.getText().toString());
        preferenceUtils.saveString(PreferenceUtils.Password, etPassword.getText().toString());
        Intent intent = new Intent(LoginActivity.this, CustomerDashboardActivity.class);
        intent.putExtra(AppConstants.User_Role, userRole);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    public void getData() {

    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if(from.equalsIgnoreCase("AuthFailed")){
            tvRegister.performClick();
        }
    }
}
