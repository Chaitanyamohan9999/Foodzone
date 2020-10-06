package com.food.foodzone.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.food.foodzone.R;
import com.food.foodzone.models.UserDo;
import com.food.foodzone.utils.CircleImageView;
import com.food.foodzone.utils.PreferenceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = "ProfileActivity";
    private View llProfile;
    private CircleImageView civProfile;
    private EditText etName, etEmail, etContactNumber, etCity, etState, etCountry;
    private ImageView ivEdit;
    private Button btnUpdate;
    private UserDo userDo = new UserDo();
    private Uri imageUri;

    @Override
    public void initialise() {
        llProfile =  inflater.inflate(R.layout.profile_layout, null);
        addBodyView(llProfile);
        lockMenu();
        ivBack.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        flCart.setVisibility(View.GONE);
        tvTitle.setText("PROFILE");
        initialiseControls();
        civProfile.setEnabled(false);
        civProfile.setAlpha(0.5f);
        etName.setEnabled(false);
        etEmail.setEnabled(false);
        etContactNumber.setEnabled(false);
        etCity.setEnabled(false);
        etState.setEnabled(false);
        etCountry.setEnabled(false);
        btnUpdate.setVisibility(View.GONE);
        etName.setBackgroundResource(R.drawable.edit_text_bg);
        etEmail.setBackgroundResource(R.drawable.edit_text_bg);
        etContactNumber.setBackgroundResource(R.drawable.edit_text_bg);
        etCity.setBackgroundResource(R.drawable.edit_text_bg);
        etState.setBackgroundResource(R.drawable.edit_text_bg);
        etCountry.setBackgroundResource(R.drawable.edit_text_bg);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getData();
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                civProfile.setAlpha(1f);
                civProfile.setEnabled(true);
                etName.setEnabled(true);
                etEmail.setEnabled(true);
                etContactNumber.setEnabled(true);
                etCity.setEnabled(true);
                etState.setEnabled(true);
                etCountry.setEnabled(true);
                btnUpdate.setVisibility(View.VISIBLE);
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etName.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter name");
                }
                else if(etEmail.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter email");
                }
                else if(!isValidEmail(etEmail.getText().toString().trim())){
                    showErrorMessage("Please enter valid email");
                }
                else if(etContactNumber.getText().toString().trim().equalsIgnoreCase("")){
                    showErrorMessage("Please enter contact number");
                }
                else if(etCity.getText().toString().trim().equalsIgnoreCase("")){
                    showErrorMessage("Please enter city");
                }
                else if(etState.getText().toString().trim().equalsIgnoreCase("")){
                    showErrorMessage("Please enter province");
                }
                else if(etCountry.getText().toString().trim().equalsIgnoreCase("")){
                    showErrorMessage("Please enter country");
                }
                else {
                    userDo.name   = etName.getText().toString().trim();
                    userDo.phone  = etContactNumber.getText().toString().trim();
                    userDo.city   = etCity.getText().toString().trim();
                    userDo.state  = etState.getText().toString().trim();
                    userDo.country  = etCountry.getText().toString().trim();
                    updateProfile(userDo);
                }
            }
        });
        civProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if(!hasPermissions(ProfileActivity.this, PERMISSIONS)){
                    ActivityCompat.requestPermissions(ProfileActivity.this, PERMISSIONS, 1210);
                }
                else {
                    selectProfilePic(110);
                }
            }
        });
    }

    private void initialiseControls(){
        civProfile              = llProfile.findViewById(R.id.civProfile);
        ivEdit                  = llProfile.findViewById(R.id.ivEdit);
        etName                  = llProfile.findViewById(R.id.etName);
        etEmail                 = llProfile.findViewById(R.id.etEmail);
        etContactNumber         = llProfile.findViewById(R.id.etContactNumber);
        etCity                  = llProfile.findViewById(R.id.etCity);
        etState                 = llProfile.findViewById(R.id.etState);
        etCountry               = llProfile.findViewById(R.id.etCountry);
        btnUpdate               = llProfile.findViewById(R.id.btnUpdate);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == 1210 || requestCode == 1211|| requestCode == 1212) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permissions", "Granted");
                selectProfilePic(requestCode);
            }
            else {
                Log.e("Permission", "Denied");
                String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if(!hasPermissions(ProfileActivity.this, PERMISSIONS)){
                    ActivityCompat.requestPermissions(ProfileActivity.this, PERMISSIONS, 201);
                }
            }
        }
    }

    private void selectProfilePic(int requestCode){
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imagePath = "Profile_"+userId+"_" + System.currentTimeMillis() + ".png";
        File file = new File(Environment.getExternalStorageDirectory(), imagePath);
        imageUri = Uri.fromFile(file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        Intent chooser = Intent.createChooser(galleryIntent, "Select an option");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { cameraIntent });
        startActivityForResult(chooser, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 110 && resultCode == RESULT_OK){
            if(data == null){// from camer
                civProfile.setImageURI(imageUri);
            }
            else {// from file storage
                imageUri = data.getData();
                civProfile.setImageURI(imageUri);
            }
            uploadProfilePic();
        }
    }

    private void uploadProfilePic(){
    }

    private void updateProfile(UserDo userDo){
        finish();
    }

    @Override
    public void getData() {
    }

    private void bindData(UserDo userDo){
        try {
            etName.setText(userDo.name);
            etEmail.setText(userDo.email);
            etContactNumber.setText(userDo.phone);
            etCity.setText(userDo.city);
            etState.setText(userDo.state);
            etCountry.setText(userDo.country);
            if(!userDo.profilePicUrl.equalsIgnoreCase("")){
                Picasso.get().load(userDo.profilePicUrl).placeholder(R.drawable.user_icon)
                        .error(R.drawable.user_icon).into(civProfile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
