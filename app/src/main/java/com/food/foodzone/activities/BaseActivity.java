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

    protected void lockMenu() {
        dlCareer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
    }

    private void initialiseControls() {
        dlCareer                    = findViewById(R.id.dlCareer);
        llDrawerLayout              = findViewById(R.id.llDrawerLayout);
        llDrawerLayoutPrent         = findViewById(R.id.llDrawerLayoutPrent);
        llBody                      = findViewById(R.id.llBody);

        llToolbar                   = findViewById(R.id.llToolbar);
        ivBack                      = findViewById(R.id.ivBack);
        ivMenu                      = findViewById(R.id.ivMenu);
        tvTitle                     = findViewById(R.id.tvTitle);
        tvFavourites                = findViewById(R.id.tvFavourites);
        tvEvents                    = findViewById(R.id.tvEvents);
        tvMeetings                  = findViewById(R.id.tvMeetings);
        tvSupport                   = findViewById(R.id.tvSupport);
        tvLogout                    = findViewById(R.id.tvLogout);
        tvVersion                   = findViewById(R.id.tvVersion);

    }

    protected void addBodyView(View body) {
        llBody.addView(body, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        applyFont(body);
    }

    public void menuOperates() {
        hideKeyBoard(llBody);
        if (dlCareer.isDrawerOpen(Gravity.RIGHT)) {
            dlCareer.closeDrawer(Gravity.RIGHT);
        } else {
            dlCareer.openDrawer(Gravity.RIGHT);
        }
    }

    public abstract void initialise();

    public abstract void getData();

    public void showToast(String strMsg) {
        Toast.makeText(BaseActivity.this, strMsg, Toast.LENGTH_LONG).show();
    }

    public static BaseActivity getInstance() {
        return mInstance;
    }

    public void showAppCompatAlert(String mTitle, String mMessage, String posButton, String negButton, final String from, boolean isCancelable) {
        if (alertDialog == null)
            alertDialog = new AlertDialog.Builder(BaseActivity.this, R.style.AppCompatAlertDialogStyle);
        alertDialog.setTitle(mTitle);
        alertDialog.setMessage(mMessage);
        alertDialog.setPositiveButton(posButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onButtonYesClick(from);
            }
        });
        if (negButton != null && !negButton.equalsIgnoreCase(""))
            alertDialog.setNegativeButton(negButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onButtonNoClick(from);
                }
            });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void onButtonNoClick(String from) {

    }

    public void onButtonYesClick(String from) {
    }

    public void hideKeyBoard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSoftKeyboard(View view) {
        try {
            if (view.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showInternetDialog(String from) {
        showAppCompatAlert("", getResources().getString(R.string.network_error), "Ok", "Cancel", from, false);

    }

    protected boolean isValidEmail(String email) {
        Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+");
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    //Method to Show loader without text
    public void showLoader() {
        runOnUiThread(new RunShowLoader(""));
    }

    //Method to show loader with text
    public void showLoader(String msg) {
        runOnUiThread(new RunShowLoader(msg));
    }

    public void hideLoader() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setFont(ViewGroup group, Typeface tf) {
        int count = group.getChildCount();
        View v;
        for (int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if (v instanceof TextView || v instanceof Button /* etc. */)
                ((TextView) v).setTypeface(tf);
            else if (v instanceof ViewGroup)
                setFont((ViewGroup) v, tf);
        }

    }