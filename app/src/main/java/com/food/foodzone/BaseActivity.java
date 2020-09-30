package com.food.foodzone.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.utils.PreferenceUtils;

import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


public abstract class BaseActivity extends AppCompatActivity {

    public Context context;
    public LayoutInflater inflater;
    public PreferenceUtils preferenceUtils;
    private Dialog dialog;
    private AlertDialog.Builder alertDialog;
    public FrameLayout llToolbar;
    public LinearLayout llDrawerLayoutPrent, llDrawerLayout, llBody;
    private DrawerLayout dlCareer;
    public ImageView ivBack, ivMenu;
    public TextView tvTitle;
    private TextView tvFavourites, tvEvents, tvMeetings, tvSupport, tvLogout, tvVersion;
    public static BaseActivity mInstance;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.find_base_layout);
        mInstance = this;
        inflater = getLayoutInflater();
        context = BaseActivity.this;
        preferenceUtils = new PreferenceUtils(context);
        initialiseControls();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        lockMenu();
        ivMenu.setVisibility(View.INVISIBLE);
        ivBack.setVisibility(View.INVISIBLE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOperates();
            }
        });
        llDrawerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOperates();
            }
        });
        llDrawerLayoutPrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOperates();
            }
        });


        tvFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuOperates();
            }
        });
        tvEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuOperates();
            }
        });
        tvMeetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuOperates();
            }
        });
        tvSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuOperates();
            }
        });
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuOperates();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        initialise();
    }
