package com.food.foodzone.activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.TableDo;
import com.food.foodzone.models.UserDo;
import com.food.foodzone.utils.CircleImageView;
import com.food.foodzone.utils.PreferenceUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class EmployeesListActivity extends BaseActivity {

    private static final String TAG = "EmployeesList";
    private View llTables;
    private RecyclerView rvEmployees;
    private TextView tvNoData;
    private FloatingActionButton fabAddEmployee;
    private EmployeeListAdapter employeeListAdapter;
    @Override
    public void initialise() {
        llTables =  inflater.inflate(R.layout.employees_list_layout, null);
        addBodyView(llTables);
        lockMenu();
        flCart.setVisibility(View.GONE);
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        tvTitle.setText("Employee List");
        initialiseControls();
        rvEmployees.setLayoutManager(new LinearLayoutManager(EmployeesListActivity.this, LinearLayoutManager.VERTICAL, false));
        employeeListAdapter = new EmployeeListAdapter(EmployeesListActivity.this, new ArrayList<UserDo>());
        rvEmployees.setAdapter(employeeListAdapter);
        getData();
        fabAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeesListActivity.this, AddEmployeeActivity.class);
                startActivityForResult(intent, 102);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    private void initialiseControls() {
        rvEmployees                           = llTables.findViewById(R.id.rvEmployees);
        tvNoData                              = llTables.findViewById(R.id.tvNoData);
        fabAddEmployee                        = llTables.findViewById(R.id.fabAddEmployee);
    }

    @Override
    public void getData() {
        final String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Users);
        showLoader();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hideLoader();
                ArrayList<UserDo> userDos = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserDo userDo = postSnapshot.getValue(UserDo.class);
                    Log.e("Get Data", userDo.toString());
                    if(!userDo.userType.equalsIgnoreCase(AppConstants.Customer_Role)
                            &&!userId.equalsIgnoreCase(userDo.userId)){
                        userDos.add(userDo);
                    }
                }
                if(userDos.size() > 0){
                    tvNoData.setVisibility(View.GONE);
                    rvEmployees.setVisibility(View.VISIBLE);
                    employeeListAdapter.refreshAdapter(userDos);
                }
                else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rvEmployees.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoader();
                Log.e(TAG, "Failed to reading email.", databaseError.toException());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 102 && resultCode == 102) {
            getData();
        }
    }

    private void deleteEmployee(String userId) {
        showLoader();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Users);
        databaseReference.child(userId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                hideLoader();
                showAppCompatAlert("", "Congratulations! You have successfully deleted an Employee.", "OK", "", "DeleteEmployee", false);
            }
        });
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if (from.equalsIgnoreCase("DeleteEmployee")) {
            getData();
        }
    }

    private class EmployeeListAdapter extends RecyclerView.Adapter<TableHolder> {

        private Context context;
        private ArrayList<UserDo> userDos;

        public EmployeeListAdapter(Context context, ArrayList<UserDo> userDos) {
            this.context  = context;
            this.userDos = userDos;
        }
        @NonNull
        @Override
        public TableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.employee_item_cell, parent, false);
            return new TableHolder(convertView);
        }

        private void refreshAdapter(ArrayList<UserDo> userDos) {
            this.userDos = userDos;
            notifyDataSetChanged();
        }
        @Override
        public void onBindViewHolder(@NonNull TableHolder holder, final int position) {
            holder.tvEmpName.setText(userDos.get(position).name);
            holder.tvEmpPhone.setText("Phone : "+userDos.get(position).phone);
            holder.tvEmpEmail.setText("Email : "+userDos.get(position).email);
            holder.ivDeleteEmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteEmployee(userDos.get(position).userId);
                }
            });
            if(!userDos.get(position).profilePicUrl.equalsIgnoreCase("")){
                Picasso.get().load(userDos.get(position).profilePicUrl).placeholder(R.drawable.user_icon)
                        .error(R.drawable.user_icon).into(holder.civProfile);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EmployeesListActivity.this, AddEmployeeActivity.class);
                    intent.putExtra("UserDo", userDos.get(position));
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            });
        }

        @Override
        public int getItemCount() {
            return userDos.size();
        }
    }

    private static class TableHolder extends RecyclerView.ViewHolder {
        private TextView tvEmpName, tvEmpPhone, tvEmpEmail;
        private ImageView ivDeleteEmp;
        private CircleImageView civProfile;
        public TableHolder(@NonNull View itemView) {
            super(itemView);
            tvEmpName               = itemView.findViewById(R.id.tvEmpName);
            tvEmpPhone              = itemView.findViewById(R.id.tvEmpPhone);
            tvEmpEmail              = itemView.findViewById(R.id.tvEmpEmail);
            civProfile              = itemView.findViewById(R.id.civProfile);
            ivDeleteEmp             = itemView.findViewById(R.id.ivDeleteEmp);
        }
    }
}
