package com.food.foodzone.activities;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.food.foodzone.R;
import com.food.foodzone.common.*;
import com.food.foodzone.models.UserDo;
import com.food.foodzone.utils.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import androidx.annotation.NonNull;

public class ForgotPasswordActivity extends BaseActivity {

    private static final String TAG = "ForgotPassword";
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
                        String userId = etEmail.getText().toString().trim().replace("@", "").replace(".", "");
                        getData();
                    }
                    else {
                        showInternetDialog("ForgotPassword");
                    }
                }
            }
        });
    }

    private void updateProfile(UserDo userDo, final String newPassword){
        showLoader();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Users);
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        databaseReference.child(userId).setValue(userDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        sendMail(newPassword);//new password will come from backend
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

    private String getNewPassword(String userId) {
        String newPassword = "";
        Random random = new Random();
        newPassword = ""+random.nextInt();
        return newPassword;
    }

    @Override
    public void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Users);
        showLoader();
        String userId = etEmail.getText().toString().trim().replace("@", "").replace(".", "");
        databaseReference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        hideLoader();
                        if (dataSnapshot != null && dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                UserDo userDo = postSnapshot.getValue(UserDo.class);
                                Log.e("Get Data", userDo.toString());
                                updatePassword(userDo);
                                break;
                            }
                        }
                        else {
                            showAppCompatAlert("", "The entered email does not exist", "OK", "", "", false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        hideLoader();
                        Log.e(TAG, "Failed to reading email.", databaseError.toException());
                    }
                });
    }

    private void updatePassword(UserDo userDo) {
        String newPassword = getNewPassword(userDo.userId);
        userDo.password = newPassword;
        updateProfile(userDo, newPassword);
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
    }
}
