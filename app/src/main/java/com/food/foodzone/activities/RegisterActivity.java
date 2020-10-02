package com.food.foodzone.activities;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.food.foodzone.R;

public class RegisterActivity extends BaseActivity {

    private View llSignup;
    private EditText etName, etEmail, etPhone, etCountry, etCity, etState, etPassword, etRenterPassword;
    private RadioGroup rgGender;
    private RadioButton rbFemale, rbMale;
    private Button btnRegister;
    private String gender = "";


    @Override
    public void initialise() {
        llSignup =  inflater.inflate(R.layout.register_layout, null);
        addBodyView(llSignup);
        lockMenu();
        tvTitle.setText("Register");
        flCart.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        initialiseControls();

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbFemale){
                    gender = "Female";
                }
                else if(checkedId == R.id.rbMale){
                    gender = "Male";
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etName.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter name");
                }
                else if(etEmail.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter email");
                }
                else if(etPhone.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter phone number");
                }
                else if(etPhone.getText().toString().trim().length() !=10){
                    showErrorMessage("Please enter valid phone number");
                }
                else if(!isValidEmail(etEmail.getText().toString().trim())){
                    showErrorMessage("Please enter email");
                }
                else if(etCountry.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter country");
                }
                else if(etCity.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter city");
                }
                else if(etState.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter province");
                }
                else if(gender.equalsIgnoreCase("")){
                    showErrorMessage("Please select gender");
                }
                else if(etPassword.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter password");
                }
                else if(etPassword.getText().toString().length() < 6){
                    showErrorMessage("Please enter password minimum 6 characters");
                }
                else if(etRenterPassword.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please Re-enter password");
                }
                else if(!etPassword.getText().toString().equalsIgnoreCase(etRenterPassword.getText().toString())){
                    showErrorMessage("Please enter password and Re-enter password are same");
                }
                else {
                    if(isNetworkConnectionAvailable(RegisterActivity.this)){
//                        doRegistration();
                    }
                    else {
                        showInternetDialog("Signup");
                    }
                }
            }
        });

    }

    private void initialiseControls(){
        rgGender                            = llSignup.findViewById(R.id.rgGender);
        rbFemale                            = llSignup.findViewById(R.id.rbFemale);
        rbMale                              = llSignup.findViewById(R.id.rbMale);

        etName                              = llSignup.findViewById(R.id.etName);
        etEmail                             = llSignup.findViewById(R.id.etEmail);
        etPhone                             = llSignup.findViewById(R.id.etPhone);
        etCountry                           = llSignup.findViewById(R.id.etCountry);
        etState                             = llSignup.findViewById(R.id.etState);
        etCity                              = llSignup.findViewById(R.id.etCity);
        etPassword                          = llSignup.findViewById(R.id.etPassword);
        etRenterPassword                    = llSignup.findViewById(R.id.etRenterPassword);
        btnRegister                          = llSignup.findViewById(R.id.btnRegister);
    }

    @Override
    public void getData() {

    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
