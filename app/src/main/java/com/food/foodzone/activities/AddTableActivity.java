package com.food.foodzone.activities;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.TableDo;
import com.food.foodzone.models.UserDo;
import com.food.foodzone.utils.PreferenceUtils;
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


public class AddTableActivity extends BaseActivity {

    private static final String TAG = "AddTable";
    private View llTables;
    private EditText etTableNumber, etTableCapacity;
    private Button btnAddTable;
    private RadioGroup rgDineInType;
    private RadioButton rbNow, rbSchedule;
    private String dineInType = AppConstants.DineInNow;

    @Override
    public void initialise() {
        llTables =  inflater.inflate(R.layout.add_table_layout, null);
        addBodyView(llTables);
        lockMenu();
        flCart.setVisibility(View.GONE);
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        tvTitle.setText("Add Table");
        initialiseControls();
        rgDineInType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbNow){
                    dineInType = AppConstants.DineInNow;
                }
                else if(checkedId == R.id.rbSchedule){
                    dineInType = AppConstants.DineInLater;
                }
            }
        });
        btnAddTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dineInType.equalsIgnoreCase("")){
                    showErrorMessage("Please select DineIn Type");
                }
                else if(etTableCapacity.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter Table Capacity");
                }
                else {
                    getData();
                }
            }
        });
    }

    private void initialiseControls() {
        rgDineInType                           = llTables.findViewById(R.id.rgDineInType);
        rbNow                                  = llTables.findViewById(R.id.rbNow);
        rbSchedule                             = llTables.findViewById(R.id.rbSchedule);
        etTableNumber                            = llTables.findViewById(R.id.etTableNumber);
        etTableCapacity                        = llTables.findViewById(R.id.etTableCapacity);
        btnAddTable                            = llTables.findViewById(R.id.btnAddTable);
    }
    @Override
    public void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Tables);
        showLoader();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hideLoader();
                ArrayList<TableDo> tableDos = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    TableDo tableDo = postSnapshot.getValue(TableDo.class);
                    Log.e("Get Data", tableDo.toString());
                    tableDos.add(tableDo);
                }
                if (tableDos != null && tableDos.size()>0) {
                    int tableNumber = Integer.parseInt(etTableNumber.getText().toString().trim());
                    for (int i=0;i<tableDos.size();i++) {
                        if(tableDos.get(i).tableNumber == tableNumber) {
                            showErrorMessage("Table number is already exist, please enter different Table number");
                            return;
                        }
                    }
                }
                addTable();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoader();
                Log.e(TAG, "Failed to reading email.", databaseError.toException());
            }
        });
    }


    private void addTable() {
        showLoader();
        String tableId = "T"+new Random().nextInt();
        int tableNumber = Integer.parseInt(etTableNumber.getText().toString().trim());
        int tableCapacity = Integer.parseInt(etTableCapacity.getText().toString().trim());
        final TableDo tableDo = new TableDo(tableId, tableNumber, dineInType, tableCapacity, "", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Tables);
        databaseReference.child(tableId).setValue(tableDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        showAppCompatAlert("", "Congratulations! You have successfully added a Table.", "OK", "", "AddTable", false);
                    }
                });
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if(from.equalsIgnoreCase("AddTable")){
            setResult(101);
            finish();
        }
    }
}
