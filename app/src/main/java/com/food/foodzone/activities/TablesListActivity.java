package com.food.foodzone.activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.TableDo;
import com.food.foodzone.utils.PreferenceUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class TablesListActivity extends BaseActivity {

    private static final String TAG = "TablesList";
    private View llTables;
    private RecyclerView rvTables;
    private TablesListAdapter tablesListAdapter;
    @Override
    public void initialise() {
        llTables =  inflater.inflate(R.layout.tables_list_layout, null);
        addBodyView(llTables);
        lockMenu();
        flCart.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        tvTitle.setText("Book Table");
        initialiseControls();
        rvTables.setLayoutManager(new GridLayoutManager(TablesListActivity.this, 2, GridLayoutManager.VERTICAL, false));
        tablesListAdapter = new TablesListAdapter(TablesListActivity.this, new ArrayList<TableDo>());
        rvTables.setAdapter(tablesListAdapter);
        getData();
    }

    private void initialiseControls() {
        rvTables                        = llTables.findViewById(R.id.rvTables);
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
                            tablesListAdapter.refreshAdapter(tableDos);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        hideLoader();
                        Log.e(TAG, "Failed to reading email.", databaseError.toException());
                    }
                });
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
        private final DecimalFormat decimalFormat = new DecimalFormat("00");
        @Override
        public void onBindViewHolder(@NonNull TableHolder holder, int position) {
            holder.tvTableName.setText(tableDos.get(position).tableName);
            holder.tvTableCapacity.setText(""+decimalFormat.format(tableDos.get(position).tableCapacity));
            if(tableDos.get(position).reservedBy.equalsIgnoreCase("")){
                holder.ivReserved.setVisibility(View.GONE);
                holder.itemView.setEnabled(true);
            }
            else {
                holder.ivReserved.setVisibility(View.VISIBLE);
                holder.itemView.setEnabled(false);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MenuListActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
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
        private ImageView ivReserved;
        public TableHolder(@NonNull View itemView) {
            super(itemView);
            tvTableName             = itemView.findViewById(R.id.tvTableName);
            tvTableCapacity         = itemView.findViewById(R.id.tvTableCapacity);
            ivReserved              = itemView.findViewById(R.id.ivReserved);
        }
    }
}
