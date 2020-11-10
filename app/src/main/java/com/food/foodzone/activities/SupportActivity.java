package com.food.foodzone.activities;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.SupportDo;
import com.food.foodzone.utils.PreferenceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import androidx.annotation.NonNull;
/**SupportActivity is uses to store the data and to show the support_layout**/

public class SupportActivity extends BaseActivity {

    private TextView tvSubmit;
    private View llSupport;
    private EditText etName, etPhone, etComments;
    private TextView tvSupportPhone;

    @Override
    public void initialise() {
        llSupport = inflater.inflate(R.layout.support_layout, null);
        addBodyView(llSupport);
        lockMenu();
        flCart.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        llToolbar.setVisibility(View.VISIBLE);
        tvTitle.setText("Support");
        initialiseControls();
        tvSupportPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tvSupportPhone.getText().toString().trim().equalsIgnoreCase("")){
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + tvSupportPhone.getText().toString().trim()));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etName.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter your name");
                }
                else if(etPhone.getText().toString().trim().length() != 10){
                    showErrorMessage("Please valid phone number to call back");
                }
                else if(etComments.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter comments");
                }
                else {
                    postCommentsForSupport();
                }
            }
        });
    }

    private void initialiseControls() {
        etName                  = llSupport.findViewById(R.id.etName);
        tvSubmit                = llSupport.findViewById(R.id.tvSubmit);
        etPhone                 = llSupport.findViewById(R.id.etPhone);
        etComments              = llSupport.findViewById(R.id.etComments);
        tvSupportPhone          = llSupport.findViewById(R.id.tvSupportPhone);
    }
    @Override
    public void getData() {

    }



    private void postCommentsForSupport(){
        showLoader();
        final String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Support);
        final String supportId = "Support_"+System.currentTimeMillis();
        String commentDate = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+Calendar.getInstance().get(Calendar.MINUTE)+"  "
                +Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"-"+AppConstants.TwoDigitsNumber.format(Calendar.getInstance().get(Calendar.MONTH)+1)
                + "-" +Calendar.getInstance().get(Calendar.YEAR);
        SupportDo supportDo = new SupportDo(supportId, userId, etComments.getText().toString().trim(), commentDate);
        databaseReference.child(supportId).setValue(supportDo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        showToast("Your query has been submitted");
                        finish();
                    }
                });
    }

}
