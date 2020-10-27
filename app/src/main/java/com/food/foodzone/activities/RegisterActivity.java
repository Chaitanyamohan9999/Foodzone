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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.UserDo;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RegisterActivity extends BaseActivity {

    private View llSignup;
    private EditText etName, etEmail, etPhone, etCity, etPassword, etRenterPassword;
    private RadioGroup rgGender;
    private RadioButton rbFemale, rbMale;
    private Button btnRegister;
    private Spinner spCountry, spProvince;
    private String gender = "";
    private String userType = "";
    private String country = "";
    private String province = "";
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;

    @Override
    public void initialise() {
        llSignup =  inflater.inflate(R.layout.register_layout, null);
        addBodyView(llSignup);
        lockMenu();
        auth = FirebaseAuth.getInstance();
        tvTitle.setText("Register");
        flCart.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        initialiseControls();
        if(getIntent().hasExtra(AppConstants.User_Type)){
            userType = getIntent().getStringExtra(AppConstants.User_Type);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
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

        final ArrayList<String> countryList = AppConstants.getCountryList();
        spCountry.setAdapter(new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_dropdown, countryList){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View row = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.spinner_dropdown, parent, false);
                final TextView tvDropdown = (TextView)row.findViewById(R.id.tvDropdown);
                tvDropdown.setText(countryList.get(position));
                return row;
            }
        });
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position > 0){
                    country = countryList.get(position);
                }
                else {
                    country = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final ArrayList<String> provinceList = AppConstants.getProvince();
        spProvince.setAdapter(new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_dropdown, provinceList){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View row = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.spinner_dropdown, parent, false);
                final TextView tvDropdown = (TextView)row.findViewById(R.id.tvDropdown);
                tvDropdown.setText(provinceList.get(position));
                return row;
            }
        });
        spProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position > 0){
                    province = provinceList.get(position);
                }
                else {
                    province = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
                else if(country.equalsIgnoreCase("")){
                    showErrorMessage("Please select country");
                }
                else if(etCity.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter city");
                }
                else if(province.equalsIgnoreCase("")){
                    showErrorMessage("Please select province");
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
                        showLoader();
                        auth.createUserWithEmailAndPassword(etEmail.getText().toString().trim(), etPassword.getText().toString().trim())
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        hideLoader();
                                        if (task.isSuccessful()) {
                                            doRegistration();
                                        }
                                        else {
                                            Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else {
                        showInternetDialog("Register");
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
        spCountry                            = llSignup.findViewById(R.id.spCountry);
        spProvince                          = llSignup.findViewById(R.id.spProvince);
        etCity                              = llSignup.findViewById(R.id.etCity);
        etPassword                          = llSignup.findViewById(R.id.etPassword);
        etRenterPassword                    = llSignup.findViewById(R.id.etRenterPassword);
        btnRegister                          = llSignup.findViewById(R.id.btnRegister);
    }

    @Override
    public void getData() {

    }

    private void doRegistration(){
        final String userId = etEmail.getText().toString().trim().replace("@", "").replace(".", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Users);
        showLoader();
        databaseReference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.exists()) {
                            hideLoader();
                            showAppCompatAlert("", "The entered email already exist, please register with different email", "OK", "", "", true);
                        }
                        else {
                            //Username does not exist
                            databaseReference.orderByChild("userId").equalTo(userId).removeEventListener(this);
                            insertIntoDB(userId, databaseReference);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        hideLoader();
                        Log.e("SignupActivity", "Failed to reading email.", databaseError.toException());
                    }
                });
    }

    private void insertIntoDB(String userId, DatabaseReference databaseReference){
        final UserDo userDo = new UserDo(userId, etName.getText().toString().trim(), etEmail.getText().toString().trim(),
                etPhone.getText().toString().trim(), country, province, etCity.getText().toString().trim(), gender,
                etPassword.getText().toString().trim(), "", userType);

        databaseReference.child(userId).setValue(userDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        showAppCompatAlert("", "Congratulations! You have successfully registered.", "OK", "", "Registration", false);
                    }
                });
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if(from.equalsIgnoreCase("Registration")){
            preferenceUtils.saveString(PreferenceUtils.EmailId, etEmail.getText().toString().trim());
            preferenceUtils.saveString(PreferenceUtils.Password, etPassword.getText().toString().trim());
            AppConstants.LoggedIn_User_Type = userType;
            Intent intent = null;
            if(userType.equalsIgnoreCase(AppConstants.Customer_Role)){
                intent = new Intent(RegisterActivity.this, LoginActivity.class);
            }
            else {
                intent = new Intent(RegisterActivity.this, EmployeeDashboardActivity.class);
            }
            intent.putExtra(AppConstants.User_Type, userType);
            intent.putExtra("Email", etEmail.getText().toString().trim());
            intent.putExtra("Phone", etPhone.getText().toString().trim());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
