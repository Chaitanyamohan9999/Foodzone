package com.food.foodzone.activities;

import android.view.View;

import com.food.foodzone.R;


public class MenuListActivity extends BaseActivity {

    private View llMenu;

    @Override
    public void initialise() {
        llMenu =  inflater.inflate(R.layout.menu_list_layout, null);
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
