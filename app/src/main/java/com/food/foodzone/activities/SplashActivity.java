package com.food.foodzone.activities;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.food.foodzone.R;


public class SplashActivity extends BaseActivity {

    private LinearLayout llSplash;

    @Override
    public void initialise() {
        llSplash = (LinearLayout) inflater.inflate(R.layout.activity_splash, null);
        addBodyView(llSplash);
        lockMenu();
        ivBack.setVisibility(View.GONE);
        llToolbar.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, UserTypeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        }, 500);
    }

    @Override
    public void getData() {

    }
}
