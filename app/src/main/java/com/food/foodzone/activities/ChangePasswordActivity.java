package com.food.foodzone.activities;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.UserDo;
import com.food.foodzone.utils.PreferenceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
/**ChangePasswordActivity is uses to change password of all three users(manager,customer,chef)**/
public class ChangePasswordActivity extends BaseActivity {

    private static final String TAG = "ChangePasswor";
    private View llChangePwd;
    private Button btnSubmit;
    private EditText etCurrentPassword, etNewPassword, etRenterPassword;
    private FirebaseAuth auth;

    @Override
    public void initialise() {
        llChangePwd = inflater.inflate(R.layout.change_password_layout, null);
        addBodyView(llChangePwd);
        lockMenu();
        auth = FirebaseAuth.getInstance();
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
                else if(etNewPassword.getText().toString().trim().equalsIgnoreCase(etCurrentPassword.getText().toString().trim())){
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
                        getData();
                    }
                    else {
                        showInternetDialog("");
                    }
                }
            }
        });
    }

    private void changePassword(final UserDo userDo){
        showLoader();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Users);
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        databaseReference.child(userId).setValue(userDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        if(auth!=null && auth.getCurrentUser()!=null){
                            auth.getCurrentUser().updatePassword(userDo.password);
                        }
                        showAppCompatAlert("", "Your password has been changed successfully!", "OK", "", "ChangePassword", false);
                    }
                });
    }

    @Override
    public void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Users);
        showLoader();
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        databaseReference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.exists()) {
                            hideLoader();
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                UserDo userDo = postSnapshot.getValue(UserDo.class);
                                Log.e("Get Data", userDo.toString());
                                userDo.password = etNewPassword.getText().toString().trim();
                                changePassword(userDo);
                                break;
                            }
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
        if(from.equalsIgnoreCase("ChangePassword")){
            Intent intent = new Intent(context, UserTypeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

}
