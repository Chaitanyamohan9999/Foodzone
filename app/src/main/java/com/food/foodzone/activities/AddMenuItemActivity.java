package com.food.foodzone.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.MenuItemDo;
import com.food.foodzone.utils.PreferenceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

/** This activity will be accessed by the manager to adding the new menu items by proving the required information along with item photo. */
public class AddMenuItemActivity extends BaseActivity {

    private static final String TAG = "AddTable";
    private View llTables;
    private LinearLayout llAvailable;
    private SwitchCompat swAvailable;
    private EditText etItemName, etItemPrice, etItemDescription;
    private Button btnAddItem;
    private ImageView ivItemImage;
    private MenuItemDo menuItemDo;
    private Spinner spCategory;
    private Uri imageUri;
    private String selectedCategory = "";

    @Override
    public void initialise() {
        llTables =  inflater.inflate(R.layout.add_menu_item_layout, null);
        addBodyView(llTables);
        lockMenu();
        flCart.setVisibility(View.GONE);
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        tvTitle.setText("Add Menu Item");
        initialiseControls();
        if(getIntent().hasExtra("MenuItemDo")) {
            menuItemDo = (MenuItemDo) getIntent().getSerializableExtra("MenuItemDo");
        }
        final ArrayList<String> categoryList = new ArrayList<>();
        if(menuItemDo != null){
            categoryList.add(menuItemDo.itemCategory);
            selectedCategory = menuItemDo.itemCategory;
            llAvailable.setVisibility(View.VISIBLE);
            etItemName.setText(menuItemDo.itemName);
            etItemPrice.setText(""+menuItemDo.itemPrice);
            etItemDescription.setText(menuItemDo.itemDescription);
            swAvailable.setChecked(menuItemDo.isAvailable);
            if(!menuItemDo.itemImage.equalsIgnoreCase("")){
                Picasso.get().load(menuItemDo.itemImage).placeholder(R.drawable.food_placeholder)
                        .error(R.drawable.food_placeholder).into(ivItemImage);
            }
            btnAddItem.setText("Update Item");
        }
        else {
            categoryList.add("Select Category");
            categoryList.add("Appetizers");
            categoryList.add("Drinks");
            categoryList.add("Main Dishes");
            categoryList.add("Sides");
            llAvailable.setVisibility(View.GONE);
            btnAddItem.setText("Add Item");
        }

        spCategory.setAdapter(new ArrayAdapter<String>(AddMenuItemActivity.this, R.layout.spinner_dropdown, categoryList){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View row = LayoutInflater.from(AddMenuItemActivity.this).inflate(R.layout.spinner_dropdown, parent, false);
                final TextView tvDropdown = (TextView)row.findViewById(R.id.tvDropdown);
                tvDropdown.setText(categoryList.get(position));
                return row;
            }
        });
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position > 0){
                    selectedCategory = categoryList.get(position);
                }
                else {
                    if(menuItemDo == null) {
                        selectedCategory = "";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ivItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if(!hasPermissions(AddMenuItemActivity.this, PERMISSIONS)){
                    ActivityCompat.requestPermissions(AddMenuItemActivity.this, PERMISSIONS, 1210);
                }
                else {
                    selectItemImage(110);
                }
            }
        });
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedCategory.equalsIgnoreCase("")){
                    showErrorMessage("Please select Item Category");
                }
                else if(etItemName.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter Item Name");
                }
                else if(etItemPrice.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter Item Price");
                }
                else if(etItemDescription.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter Item Description");
                }
                else {
                    if(menuItemDo != null) {
                        double price = Double.parseDouble(etItemPrice.getText().toString().trim());
                        menuItemDo.itemCategory = selectedCategory;
                        menuItemDo.itemName = etItemName.getText().toString().trim();
                        menuItemDo.itemPrice = price;
                        menuItemDo.itemDescription = etItemDescription.getText().toString().trim();
                        menuItemDo.isAvailable = swAvailable.isChecked();
                        if(imageUri!=null) {
                            uploadMenuItemPic(menuItemDo, "Congratulations! You have successfully updated a Menu Item.");
                        }
                        else {
                            addMenuItem(menuItemDo, "Congratulations! You have successfully updated a Menu Item.");
                        }
                    }
                    else {
                        String itemId = "I"+System.currentTimeMillis();
                        double price = Double.parseDouble(etItemPrice.getText().toString().trim());
                        final MenuItemDo menuItemDo = new MenuItemDo(itemId, selectedCategory, etItemName.getText().toString().trim(), price, etItemDescription.getText().toString().trim(),
                                "", true, 0);
                        if(imageUri!=null) {
                            uploadMenuItemPic(menuItemDo, "Congratulations! You have successfully added a Menu Item.");
                        }
                        else {
                            addMenuItem(menuItemDo, "Congratulations! You have successfully added a Menu Item.");
                        }
                    }
                }
            }
        });
    }

    private void initialiseControls() {
        spCategory                             = llTables.findViewById(R.id.spCategory);
        etItemName                             = llTables.findViewById(R.id.etItemName);
        etItemPrice                            = llTables.findViewById(R.id.etItemPrice);
        etItemDescription                      = llTables.findViewById(R.id.etItemDescription);
        ivItemImage                            = llTables.findViewById(R.id.ivItemImage);
        btnAddItem                             = llTables.findViewById(R.id.btnAddItem);
        llAvailable                            = llTables.findViewById(R.id.llAvailable);
        swAvailable                            = llTables.findViewById(R.id.swAvailable);
    }

    @Override
    public void getData() {

    }

    private void selectItemImage(int requestCode){
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
            if(data != null){// from camera
                imageUri = data.getData();
            }
            ivItemImage.setImageURI(imageUri);
        }
        else {
            imageUri = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == 1210 || requestCode == 1211|| requestCode == 1212) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permissions", "Granted");
                selectItemImage(requestCode);
            }
            else {
                Log.e("Permission", "Denied");
                String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if(!hasPermissions(AddMenuItemActivity.this, PERMISSIONS)){
                    ActivityCompat.requestPermissions(AddMenuItemActivity.this, PERMISSIONS, 201);
                }
            }
        }
    }

    private void uploadMenuItemPic(final MenuItemDo menuItemDo, final String message){
        showLoader();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final String profileImgPath = AppConstants.Food_Storage_Path  + menuItemDo.itemId;
        storageReference.child(profileImgPath).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                uri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        hideLoader();
                        String profileImgPath = uri.toString();
                        menuItemDo.itemImage = profileImgPath;
                        addMenuItem(menuItemDo, message);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideLoader();
                Log.e("Image Upload", "Exception : "+e.getMessage());
                showToast("Error while uploading : "+e.getMessage());
            }
        });
    }

    private void addMenuItem(final MenuItemDo menuItemDo, final String message) {
        showLoader();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Item);
        databaseReference.child(menuItemDo.itemId).setValue(menuItemDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        showAppCompatAlert("", message, "OK", "", "AddMenuItem", false);
                    }
                });
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if(from.equalsIgnoreCase("AddMenuItem")){
            setResult(103);
            finish();
        }
    }
}
