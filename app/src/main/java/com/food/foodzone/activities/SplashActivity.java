package com.food.foodzone.activities;


import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.TableDo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import androidx.annotation.NonNull;


public class SplashActivity extends BaseActivity {

    private LinearLayout llSplash;

    @Override
    public void initialise() {
        llSplash = (LinearLayout) inflater.inflate(R.layout.activity_splash, null);
        addBodyView(llSplash);
        lockMenu();
        flCart.setVisibility(View.GONE);
        ivBack.setVisibility(View.GONE);
        llToolbar.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                for (int i=1;i<=10;i++) {
//                    createTable(i, AppConstants.Table_Names[i-1], AppConstants.Table_Capacity[i-1]);
//                }
                moveToNext();
            }
        }, 500);
    }

    @Override
    public void getData() {

    }

    private void moveToNext() {
        Intent intent = new Intent(SplashActivity.this, UserTypeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        finish();
    }
}