package com.food.foodzone.activities;

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

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.TableDo;
import com.food.foodzone.models.UserDo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class AddEmployeeActivity extends BaseActivity {

    private static final String TAG = "AddEmployee";
    private View llEmployees;
    private EditText etName, etEmail, etPhone, etCountry, etCity, etState, etPassword, etRenterPassword;
    private RadioGroup rgGender;
    private RadioButton rbFemale, rbMale;
    private Button btnRegister;
    private String gender = "";
    private String userType = "";
    private Spinner spUserRole;
    private UserDo userDo;

    @Override
    public void initialise() {
        llEmployees =  inflater.inflate(R.layout.add_employee_layout, null);
        addBodyView(llEmployees);
        lockMenu();
        flCart.setVisibility(View.GONE);
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        tvTitle.setText("Add Employee");
        initialiseControls();
        if(getIntent().hasExtra("UserDo")) {
            userDo = (UserDo) getIntent().getSerializableExtra("UserDo");
        }
        final ArrayList<String> userRolesList = new ArrayList<>();

        if(userDo !=null){
            etName.setEnabled(false);
            etEmail.setEnabled(false);
            etPhone.setEnabled(false);
            etCountry.setEnabled(false);
            etCity.setEnabled(false);
            etState.setEnabled(false);
            etPassword.setEnabled(false);
            etRenterPassword.setEnabled(false);
            rbFemale.setEnabled(false);
            rbMale.setEnabled(false);
            spUserRole.setEnabled(false);

            etName.setText(userDo.name);
            etEmail.setText(userDo.email);
            etPhone.setText(userDo.phone);
            etCountry.setText(userDo.country);
            etCity.setText(userDo.city);
            etState.setText(userDo.state);
            etPassword.setText(userDo.password);
            etRenterPassword.setText(userDo.password);

            btnRegister.setText("Close");
            if(userDo.gender.equalsIgnoreCase("Male")){
                rbMale.setChecked(true);
                rbFemale.setChecked(false);
            }
            else {
                rbMale.setChecked(false);
                rbFemale.setChecked(true);
            }
            userRolesList.add(userDo.userType);
        }
        else {
            userRolesList.add("Select User Role");
            userRolesList.add("Chef");
            userRolesList.add("Manager");
        }

        spUserRole.setAdapter(new ArrayAdapter<String>(AddEmployeeActivity.this, R.layout.spinner_dropdown, userRolesList){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View row = LayoutInflater.from(AddEmployeeActivity.this).inflate(R.layout.spinner_dropdown, parent, false);
                final TextView tvDropdown = (TextView)row.findViewById(R.id.tvDropdown);
                tvDropdown.setText(userRolesList.get(position));
                return row;
            }
        });
        spUserRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position > 0){
                    userType = userRolesList.get(position);
                }
                else {
                    userType = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
                if(userDo !=null){
                    finish();
                }
                else {
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
                        if(isNetworkConnectionAvailable(AddEmployeeActivity.this)){
                            addEmployee();
                        }
                        else {
                            showInternetDialog("Register");
                        }
                    }
                }

            }
        });

    }

    private void initialiseControls() {
        rgGender                            = llEmployees.findViewById(R.id.rgGender);
        rbFemale                            = llEmployees.findViewById(R.id.rbFemale);
        rbMale                              = llEmployees.findViewById(R.id.rbMale);

        etName                              = llEmployees.findViewById(R.id.etName);
        etEmail                             = llEmployees.findViewById(R.id.etEmail);
        etPhone                             = llEmployees.findViewById(R.id.etPhone);
        etCountry                           = llEmployees.findViewById(R.id.etCountry);
        etState                             = llEmployees.findViewById(R.id.etState);
        etCity                              = llEmployees.findViewById(R.id.etCity);
        spUserRole                          = findViewById(R.id.spUserRole);
        etPassword                          = llEmployees.findViewById(R.id.etPassword);
        etRenterPassword                    = llEmployees.findViewById(R.id.etRenterPassword);
        btnRegister                         = llEmployees.findViewById(R.id.btnRegister);
    }
    @Override
    public void getData() {

    }

    private void addEmployee(){
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
                            showAppCompatAlert("", "The entered email is already exist", "OK", "", "", true);
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
                etPhone.getText().toString().trim(), etCountry.getText().toString().trim(), etState.getText().toString().trim(),
                etCity.getText().toString().trim(), gender, etPassword.getText().toString().trim(), "", userType);

        databaseReference.child(userId).setValue(userDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        showAppCompatAlert("", "Congratulations! You have successfully added an Employee.", "OK", "", "AddEmployee", false);
                    }
                });
    }


    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if(from.equalsIgnoreCase("AddEmployee")){
            setResult(102);
            finish();
        }
    }
}
