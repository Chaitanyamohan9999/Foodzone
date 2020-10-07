package com.food.foodzone.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.food.foodzone.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MenuListActivity extends BaseActivity {

    private View llMenu;
    private RecyclerView rvMenuList;
    private MenuListAdapter menuListAdapter;
    @Override
    public void initialise() {
        llMenu =  inflater.inflate(R.layout.menu_list_layout, null);
        addBodyView(llMenu);
        lockMenu();
        flCart.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        tvTitle.setText("Menu");
        initialiseControls();
        rvMenuList.setLayoutManager(new LinearLayoutManager(MenuListActivity.this));
        menuListAdapter = new MenuListAdapter(MenuListActivity.this);
        rvMenuList.setAdapter(menuListAdapter);

    }

    private void initialiseControls() {
        rvMenuList                  = llMenu.findViewById(R.id.rvMenuList);
    }
    @Override
    public void getData() {

    }

    private class MenuListAdapter extends RecyclerView.Adapter<MenuHolder> {

        private Context context;
        public MenuListAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.menu_item_cell, parent, false);
            return new MenuHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull MenuHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }

    }
    private static class MenuHolder extends RecyclerView.ViewHolder {

        public MenuHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
