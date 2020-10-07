package com.food.foodzone.activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.food.foodzone.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class TablesListActivity extends BaseActivity {

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
        tablesListAdapter = new TablesListAdapter(TablesListActivity.this);
        rvTables.setAdapter(tablesListAdapter);

    }

    private void initialiseControls() {
        rvTables                        = llTables.findViewById(R.id.rvTables);
    }
    @Override
    public void getData() {

    }

    private class TablesListAdapter extends RecyclerView.Adapter<TableHolder> {

        private Context context;
        public TablesListAdapter(Context context) {
            this.context = context;
        }
        @NonNull
        @Override
        public TableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.table_item_cell, parent, false);
            return new TableHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull TableHolder holder, int position) {
            holder.tvTableName.setText("Table1");
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
            return 10;
        }
    }

    private static class TableHolder extends RecyclerView.ViewHolder {
        private TextView tvTableName;
        public TableHolder(@NonNull View itemView) {
            super(itemView);
            tvTableName             = itemView.findViewById(R.id.tvTableName);
        }
    }
}
