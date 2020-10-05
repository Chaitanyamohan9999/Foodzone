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
    private UserDo userDo;
    private Uri imageUri;

    @Override
    public void initialise() {
        llProfile =  inflater.inflate(R.layout.profile_layout, null);
        addBodyView(llProfile);
        lockMenu();
        ivBack.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        flCart.setVisibility(View.VISIBLE);
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
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
}
