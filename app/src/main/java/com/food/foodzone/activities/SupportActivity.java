package com.food.foodzone.activities;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.food.foodzone.R;

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

    private void postCommentsForSupport() {
        finish();
    }
}