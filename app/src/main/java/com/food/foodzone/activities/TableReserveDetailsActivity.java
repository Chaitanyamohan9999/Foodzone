package com.food.foodzone.activities;

import android.view.View;

import com.food.foodzone.R;
/**TableReserveDetailsActivity is uses to dispaly table_reserve_details layout**/
public class TableReserveDetailsActivity extends BaseActivity {

    private View llReserve;

    @Override
    public void initialise() {
        llReserve           = inflater.inflate(R.layout.table_reserve_details, null);
        addBodyView(llReserve);
        lockMenu();
        ivBack.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        flCart.setVisibility(View.GONE);
        llToolbar.setVisibility(View.VISIBLE);
        initialiseControls();
    }

    private void initialiseControls() {

    }

    @Override
    public void getData() {

    }
}
