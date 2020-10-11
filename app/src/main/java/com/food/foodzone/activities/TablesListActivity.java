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
import com.food.foodzone.utils.PreferenceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class TablesListActivity extends BaseActivity {

    private static final String TAG = "TablesList";
    private View llTables;
    private RecyclerView rvTables;
    private TextView tvNoData;
    private FloatingActionButton fabAddTable;
    private TablesListAdapter tablesListAdapter;
    @Override
    public void initialise() {
        llTables =  inflater.inflate(R.layout.tables_list_layout, null);
        addBodyView(llTables);
        lockMenu();
        flCart.setVisibility(View.GONE);
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);

        initialiseControls();
        if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)){
            fabAddTable.setVisibility(View.GONE);
            tvTitle.setText("Book Table");
        }
        else {
            fabAddTable.setVisibility(View.VISIBLE);
            tvTitle.setText("Tables List");
        }
        rvTables.setLayoutManager(new GridLayoutManager(TablesListActivity.this, 2, GridLayoutManager.VERTICAL, false));
        tablesListAdapter = new TablesListAdapter(TablesListActivity.this, new ArrayList<TableDo>());
        rvTables.setAdapter(tablesListAdapter);
        getData();
        fabAddTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TablesListActivity.this, AddTableActivity.class);
                startActivityForResult(intent, 101);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    private void initialiseControls() {
        rvTables                           = llTables.findViewById(R.id.rvTables);
        tvNoData                           = llTables.findViewById(R.id.tvNoData);
        fabAddTable                        = llTables.findViewById(R.id.fabAddTable);
    }

    @Override
    public void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Tables);
        showLoader();
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
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
                if(tableDos.size() > 0){
                    tvNoData.setVisibility(View.GONE);
                    rvTables.setVisibility(View.VISIBLE);
                    tablesListAdapter.refreshAdapter(tableDos);
                }
                else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rvTables.setVisibility(View.GONE);
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
        if(requestCode == 101 && resultCode == 101) {
            getData();
        }
    }

    private void deleteTable(String tableId) {
        showLoader();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Tables);
        databaseReference.child(tableId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                hideLoader();
                showAppCompatAlert("", "Congratulations! You have successfully deleted a Table.", "OK", "", "DeleteTable", false);
            }
        });
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if (from.equalsIgnoreCase("DeleteTable")) {
            getData();
        }
    }

    private class TablesListAdapter extends RecyclerView.Adapter<TableHolder> {

        private Context context;
        private ArrayList<TableDo> tableDos;

        public TablesListAdapter(Context context, ArrayList<TableDo> tableDos) {
            this.context  = context;
            this.tableDos = tableDos;
        }
        @NonNull
        @Override
        public TableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.table_item_cell, parent, false);
            return new TableHolder(convertView);
        }

        private void refreshAdapter(ArrayList<TableDo> tableDos) {
            this.tableDos = tableDos;
            notifyDataSetChanged();
        }
        @Override
        public void onBindViewHolder(@NonNull TableHolder holder, final int position) {
            holder.tvTableName.setText(tableDos.get(position).tableName);
            holder.tvTableCapacity.setText(""+AppConstants.decimalFormat.format(tableDos.get(position).tableCapacity));

            if (AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
                holder.ivDeleteTable.setVisibility(View.GONE);
                holder.ivDeleteTable.setEnabled(false);
                if(tableDos.get(position).reservedBy.trim().equalsIgnoreCase("")){
                    holder.ivReserved.setVisibility(View.GONE);
                    holder.itemView.setEnabled(true);
                }
                else {
                    holder.ivReserved.setVisibility(View.VISIBLE);
                    holder.itemView.setEnabled(false);
                }
            }
            else if (AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Manager_Role)) {
                if(tableDos.get(position).reservedBy.trim().equalsIgnoreCase("")){
                    holder.ivReserved.setVisibility(View.GONE);
                    holder.ivDeleteTable.setVisibility(View.VISIBLE);
                }
                else {
                    holder.ivReserved.setVisibility(View.VISIBLE);
                    holder.ivDeleteTable.setVisibility(View.GONE);
                }
            }
            else {
                holder.ivDeleteTable.setVisibility(View.GONE);
                if(tableDos.get(position).reservedBy.trim().equalsIgnoreCase("")){
                    holder.ivReserved.setVisibility(View.GONE);
                }
                else {
                    holder.ivReserved.setVisibility(View.VISIBLE);
                }
            }
            holder.ivDeleteTable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteTable(tableDos.get(position).tableId);
                }
            });
        }

        @Override
        public int getItemCount() {
            return tableDos.size();
        }
    }

    private static class TableHolder extends RecyclerView.ViewHolder {
        private TextView tvTableName, tvTableCapacity;
        private ImageView ivDeleteTable, ivReserved;
        public TableHolder(@NonNull View itemView) {
            super(itemView);
            tvTableName             = itemView.findViewById(R.id.tvTableName);
            tvTableCapacity         = itemView.findViewById(R.id.tvTableCapacity);
            ivReserved              = itemView.findViewById(R.id.ivReserved);
            ivDeleteTable           = itemView.findViewById(R.id.ivDeleteTable);
        }
    }
}
