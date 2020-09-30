package com.food.foodzone.activities;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.food.foodzone.R;

public class ForgotPasswordActivity extends BaseActivity {

    private View llGuestLogin;
    private TextView tvLogin;
    private EditText etEmail, etPassword, etRenterPassword;

    @Override
    public void initialise() {
        llGuestLogin = inflater.inflate(R.layout.forgot_password_login, null);
        addBodyView(llGuestLogin);
        lockMenu();
        tvTitle.setText("Forgot Password");
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        llToolbar.setVisibility(View.VISIBLE);

        tvLogin                 = llGuestLogin.findViewById(R.id.tvLogin);
        etEmail                 = llGuestLogin.findViewById(R.id.etEmail);
        etPassword              = llGuestLogin.findViewById(R.id.etPassword);
        etRenterPassword        = llGuestLogin.findViewById(R.id.etRenterPassword);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etEmail.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter email");
                }
                else if(!isValidEmail(etEmail.getText().toString().trim())){
                    showErrorMessage("Please enter valid email");
                }
                else if(etPassword.getText().toString().trim().equalsIgnoreCase("")){
                    showErrorMessage("Please enter password");
                }
                else if(etPassword.getText().toString().trim().length() < 6){
                    showErrorMessage("Please enter password minimum 6 characters");
                }
                else if(etRenterPassword.getText().toString().trim().equalsIgnoreCase("")){
                    showErrorMessage("Please re-enter password");
                }
                else if(etRenterPassword.getText().toString().trim().length() < 6){
                    showErrorMessage("Please re-enter password minimum 6 characters");
                }
                else if(!etPassword.getText().toString().trim().equalsIgnoreCase(etRenterPassword.getText().toString().trim())){
                    showErrorMessage("Please password and re-enter password are same");
                }
                else {
                    if(isNetworkConnectionAvailable(ForgotPasswordActivity.this)){
//                        setNewPassword();
                    }
                    else {
                        showInternetDialog("ForgotPassword");
                    }
                }
            }
        });
    }


    @Override
    public void getData() {

    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
    }
}
