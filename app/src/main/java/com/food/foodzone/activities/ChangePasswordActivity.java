package com.food.foodzone.activities;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.food.foodzone.R;
import com.food.foodzone.utils.PreferenceUtils;

public class ChangePasswordActivity extends BaseActivity {

    private View llChangePwd;
    private Button btnSubmit;
    private EditText etCurrentPassword, etNewPassword, etRenterPassword;

    @Override
    public void initialise() {
        llChangePwd = inflater.inflate(R.layout.change_password_layout, null);
        addBodyView(llChangePwd);
        lockMenu();
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        llToolbar.setVisibility(View.VISIBLE);
        flCart.setVisibility(View.GONE);
        tvTitle.setText("Change Password");

        btnSubmit               = llChangePwd.findViewById(R.id.btnSubmit);
        etCurrentPassword       = llChangePwd.findViewById(R.id.etCurrentPassword);
        etNewPassword           = llChangePwd.findViewById(R.id.etNewPassword);
        etRenterPassword        = llChangePwd.findViewById(R.id.etRenterPassword);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etCurrentPassword.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter Current Password");
                }
                else if(!etCurrentPassword.getText().toString().equalsIgnoreCase(preferenceUtils.getStringFromPreference(PreferenceUtils.Password, ""))){
                    showErrorMessage("Please enter valid Current Password");
                }
                else if(etNewPassword.getText().toString().trim().equalsIgnoreCase("")){
                    showErrorMessage("Please enter password");
                }
                else if(etNewPassword.getText().toString().trim().length() < 6){
                    showErrorMessage("Please enter password minimum 6 characters");
                }
                else if(etNewPassword.getText().toString().equalsIgnoreCase(etCurrentPassword.getText().toString())){
                    showErrorMessage("Current Password and New Password must not be same");
                }
                else if(etRenterPassword.getText().toString().trim().equalsIgnoreCase("")){
                    showErrorMessage("Please re-enter password");
                }
                else if(etRenterPassword.getText().toString().trim().length() < 6){
                    showErrorMessage("Please re-enter password minimum 6 characters");
                }
                else if(!etNewPassword.getText().toString().trim().equalsIgnoreCase(etRenterPassword.getText().toString().trim())){
                    showErrorMessage("Please enter password and re-enter password are same");
                }
                else {
                    if(isNetworkConnectionAvailable(ChangePasswordActivity.this)){
                        changePassword();
                    }
                    else {
                        showInternetDialog("");
                    }
                }
            }
        });
    }

    private void changePassword(){
        showAppCompatAlert("", "Your passwprd has been changed successfully!", "OK", "", "ChangePassword", false);
    }

    @Override
    public void getData() {

    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if(from.equalsIgnoreCase("ChangePassword")){
            Intent intent = new Intent(context, UserTypeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

}
