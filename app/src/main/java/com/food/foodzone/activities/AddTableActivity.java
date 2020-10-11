package com.food.foodzone.activities;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.TableDo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;


public class AddTableActivity extends BaseActivity {

    private static final String TAG = "AddTable";
    private View llTables;
    private EditText etTableName, etTableCapacity;
    private Button btnAddTable;

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

        btnAddTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etTableName.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter Table Name");
                }
                else if(etTableCapacity.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter Table Capacity");
                }
                else {
                    addTable();
                }
            }
        });
    }

    private void initialiseControls() {
        etTableName                            = llTables.findViewById(R.id.etTableName);
        etTableCapacity                        = llTables.findViewById(R.id.etTableCapacity);
        btnAddTable                            = llTables.findViewById(R.id.btnAddTable);
    }
    @Override
    public void getData() {

    }

    private void addTable() {
        showLoader();
        String tableId = "T"+new Random().nextInt();
        int tableCapacity = Integer.parseInt(etTableCapacity.getText().toString().trim());
        final TableDo tableDo = new TableDo(tableId, etTableName.getText().toString().trim(), tableCapacity, "", "");
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
