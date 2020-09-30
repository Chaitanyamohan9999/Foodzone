package com.food.foodzone;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.food.foodzone.common.AppConstants;
import com.food.foodzone.utils.GMailSender;

public class ForgotPasswordActivity extends com.food.foodzone.activities.BaseActivity {

    private View llGuestLogin;
    private Button btnSendNewPassword;
    private EditText etEmail;

    @Override
    public void initialise() {
        llGuestLogin = inflater.inflate(R.layout.forgot_password_login, null);
        addBodyView(llGuestLogin);
        lockMenu();
        tvTitle.setText("Forgot Password");
        flCart.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        llToolbar.setVisibility(View.VISIBLE);

        btnSendNewPassword      = llGuestLogin.findViewById(R.id.btnSendNewPassword);
        etEmail                 = llGuestLogin.findViewById(R.id.etEmail);

        btnSendNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etEmail.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter email");
                }
                else if(!isValidEmail(etEmail.getText().toString().trim())){
                    showErrorMessage("Please enter valid email");
                }
                else {
                    if(isNetworkConnectionAvailable(ForgotPasswordActivity.this)){
                        sendMail("12345678");//new password will come from backend
                    }
                    else {
                        showInternetDialog("ForgotPassword");
                    }
                }
            }
        });
    }

    private void sendMail(String newPassword) {
        try {
            showLoader();
            String emailBody = "Hi Foodian,\n\n" +
                    "Your FOODZONE's new password is "+newPassword+"\n"+
                    "Please use this code as a your new password to login into FOODZONE app\n\n\n" +
                    "Thanks\n" +
                    "FoodZone";
            GMailSender sender = new GMailSender(AppConstants.GmailSenderMail, AppConstants.GmailSenderPassword);
            sender.sendMail(AppConstants.EmailSubject, emailBody, AppConstants.GmailSenderMail, etEmail.getText().toString().trim());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideLoader();
                    showToast("The new password has been sent to your email");
                    finish();
                }
            }, 5000);
        }
        catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
            e.printStackTrace();
        }

    }

    @Override
    public void getData() {

    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
    }
}
