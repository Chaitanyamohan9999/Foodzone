package com.food.foodzone.activities;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.TableDo;
import com.food.foodzone.utils.PreferenceUtils;
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

    private void createTable(final int i, String tableName, int capacity) {
        showLoader();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Tables);
        final String supportId = "Tables_"+i;
        String commentDate = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+Calendar.getInstance().get(Calendar.MINUTE)+"  "
                +Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"-"+AppConstants.decimalFormat.format(Calendar.getInstance().get(Calendar.MONTH)+1)
                + "-" +Calendar.getInstance().get(Calendar.YEAR);

        TableDo tableDo = new TableDo(supportId, tableName, capacity, "", "");
        databaseReference.child(supportId).setValue(tableDo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        if(i==10) {
                            moveToNext();
                        }
                    }
                });
    }

    private void moveToNext() {
        Intent intent = new Intent(SplashActivity.this, UserTypeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        finish();
    }
}
