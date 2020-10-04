package com.food.foodzone.activities;

import android.view.View;

import com.food.foodzone.R;


public class TablesListActivity extends BaseActivity {

    private View llMenu;

    @Override
    public void initialise() {
        llMenu =  inflater.inflate(R.layout.tables_list_layout, null);
        addBodyView(llMenu);
        lockMenu();
        flCart.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);

    }

    @Override
    public void getData() {

    }
}
