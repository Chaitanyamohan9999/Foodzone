package com.food.foodzone.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
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

import com.food.foodzone.BuildConfig;
import com.food.foodzone.R;
import com.food.foodzone.common.*;
import com.food.foodzone.utils.PreferenceUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
/** BaseAcivity is an abstract class with initialise() and getData() abtract methods.
 This is super class for remainig activities in the application.
 Here to maintain comon methods and varoiables. */
public abstract class BaseActivity extends AppCompatActivity implements AddToCartListener {

    public Context context;
    public LayoutInflater inflater;
    public PreferenceUtils preferenceUtils;
    private Dialog dialog;
    private AlertDialog.Builder alertDialog;
    public FrameLayout llToolbar, flCart;
    public LinearLayout llDrawerLayoutPrent, llDrawerLayout, llBody;
    public DrawerLayout dlCareer;
    public ImageView ivBack, ivMenu, ivRefresh;
    public TextView tvCartCount, tvTitle;
    private TextView tvOrders, tvReservations, tvOrdersHistory, tvProfile, tvChangePassword, tvSupport, tvLogout, tvVersion;
    public static BaseActivity mInstance;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.foodzone_base_layout);
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
        tvVersion.setText("Version : "+ BuildConfig.VERSION_NAME);
        ivMenu.setVisibility(View.INVISIBLE);
        ivBack.setVisibility(View.INVISIBLE);
        ivRefresh.setVisibility(View.GONE);
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
        tvReservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuOperates();
                Intent intent = new Intent(context, ReservationsListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        tvOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuOperates();
                Intent intent = new Intent(context, OrdersHistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        tvOrdersHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOperates();
                Intent intent = new Intent(context, OrdersHistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        tvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuOperates();
                Intent intent = new Intent(context, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuOperates();
                Intent intent = new Intent(context, ChangePasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        tvSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuOperates();
                Intent intent = new Intent(context, SupportActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuOperates();
                showAppCompatAlert("", "Do you want ot logout from app?", "Logout", "Cancel", "Logout", false);
            }
        });
        flCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppConstants.Cart_Items!=null && AppConstants.Cart_Items.size()>0) {
                    if(!(context instanceof CartListActivity)) {
                        Intent intent = new Intent(context, CartListActivity.class);
                        intent.putExtra("From", AppConstants.from);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                }
            }
        });
        initialise();
    }

    protected void employeeLeftMenu () {
        tvReservations.setVisibility(View.VISIBLE);
        tvOrdersHistory.setVisibility(View.GONE);
        findViewById(R.id.vwPendingOrders).setVisibility(View.VISIBLE);
        findViewById(R.id.vwOrdersHistory).setVisibility(View.GONE);
        tvOrders.setVisibility(View.VISIBLE);
        findViewById(R.id.vwOrders).setVisibility(View.VISIBLE);
        tvProfile.setVisibility(View.VISIBLE);
        tvChangePassword.setVisibility(View.VISIBLE);
        tvSupport.setVisibility(View.VISIBLE);
        tvLogout.setVisibility(View.VISIBLE);
        tvVersion.setVisibility(View.VISIBLE);
    }

    protected void chefLeftMenu() {
        tvReservations.setVisibility(View.GONE);
        tvOrdersHistory.setVisibility(View.GONE);
        findViewById(R.id.vwPendingOrders).setVisibility(View.GONE);
        findViewById(R.id.vwOrdersHistory).setVisibility(View.GONE);
        tvOrders.setVisibility(View.GONE);
        findViewById(R.id.vwOrders).setVisibility(View.GONE);
        tvProfile.setVisibility(View.VISIBLE);
        tvChangePassword.setVisibility(View.VISIBLE);
        tvSupport.setVisibility(View.VISIBLE);
        tvLogout.setVisibility(View.VISIBLE);
        tvVersion.setVisibility(View.VISIBLE);
    }

    protected void customerLeftMenu() {
        tvReservations.setVisibility(View.VISIBLE);
        tvOrdersHistory.setVisibility(View.VISIBLE);
        findViewById(R.id.vwPendingOrders).setVisibility(View.VISIBLE);
        findViewById(R.id.vwOrdersHistory).setVisibility(View.VISIBLE);
        tvOrders.setVisibility(View.GONE);
        findViewById(R.id.vwOrders).setVisibility(View.GONE);
        tvProfile.setVisibility(View.VISIBLE);
        tvChangePassword.setVisibility(View.VISIBLE);
        tvSupport.setVisibility(View.VISIBLE);
        tvLogout.setVisibility(View.VISIBLE);
        tvVersion.setVisibility(View.VISIBLE);
    }

    protected void lockMenu() {
        dlCareer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.Customer_Role)) {
            addToCart();
        }
    }

    private void initialiseControls() {
        dlCareer                    = findViewById(R.id.dlCareer);
        llDrawerLayout              = findViewById(R.id.llDrawerLayout);
        llDrawerLayoutPrent         = findViewById(R.id.llDrawerLayoutPrent);
        llBody                      = findViewById(R.id.llBody);

        llToolbar                   = findViewById(R.id.llToolbar);
        flCart                      = findViewById(R.id.flCart);
        ivBack                      = findViewById(R.id.ivBack);
        ivMenu                      = findViewById(R.id.ivMenu);
        ivRefresh                   = findViewById(R.id.ivRefresh);
        tvTitle                     = findViewById(R.id.tvTitle);
        tvCartCount                 = findViewById(R.id.tvCartCount);
        tvOrders                    = findViewById(R.id.tvOrders);
        tvReservations             = findViewById(R.id.tvReservations);
        tvOrdersHistory             = findViewById(R.id.tvOrdersHistory);
        tvProfile                   = findViewById(R.id.tvProfile);
        tvChangePassword            = findViewById(R.id.tvChangePassword);
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
        if (dlCareer.isDrawerOpen(Gravity.LEFT)) {
            dlCareer.closeDrawer(Gravity.LEFT);
        } else {
            dlCareer.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void addToCart() {
        if(AppConstants.Cart_Items != null && AppConstants.Cart_Items.size() > 0) {
            tvCartCount.setText(""+AppConstants.Cart_Items.size());
        }
        else {
            tvCartCount.setText("0");
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

    public boolean isFoodZoneArea(Location mLocation) {
        if(mLocation!=null) {
            double latitude1 = mLocation.getLatitude();
            double longitude1 = mLocation.getLongitude();

            Location locationA = new Location("UserLocation");
            locationA.setLatitude(latitude1);
            locationA.setLongitude(longitude1);

            Location locationB = new Location("FoodZoneLocation");
            locationB.setLatitude(AppConstants.FoodZone_Latitude);
            locationB.setLongitude(AppConstants.FoodZone_Longitude);

            return locationA.distanceTo(locationB) < AppConstants.FoodZone_Area;
        }

        return false;
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
        if (from.equalsIgnoreCase("Logout")){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(context, UserTypeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
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

    private class RunShowLoader implements Runnable {
        private String strMsg;

        public RunShowLoader(String strMsg) {
            this.strMsg = strMsg;
        }

        @Override
        public void run() {
            try {
                dialog = new Dialog(BaseActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                View view = LayoutInflater.from(context).inflate(R.layout.loader_custom, null);
                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
                progressBar.setIndeterminate(true);
                progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
                dialog.setContentView(view);
                if (!dialog.isShowing())
                    dialog.show();
            } catch (Exception e) {
            }
        }
    }

    public boolean isNetworkConnectionAvailable(Context context) {
        boolean isNetworkConnectionAvailable = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            if (activeNetworkInfo != null) {
                isNetworkConnectionAvailable = activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isNetworkConnectionAvailable;
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void applyFont(final View root) {
        String fontName = AppConstants.GILL_SANS_TYPE_FACE;
        try {
            fontName = fontStyleName(fontName);
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                for (int i = 0; i < viewGroup.getChildCount(); i++)
                    applyFont(viewGroup.getChildAt(i));
            }
            else if (root instanceof TextView){
                ((TextView) root).setTypeface(Typeface.createFromAsset(getAssets(), fontName));
            }
        } catch (Exception e) {
            Log.e("KARYA", String.format("Error occured when trying to apply %s font for %s view", fontName, root));
            e.printStackTrace();
        }
    }

    public void applyFontFromSuccess(final View root, String fontName) {
        try {
            fontName = fontStyleName(fontName);
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                for (int i = 0; i < viewGroup.getChildCount(); i++)
                    applyFont(viewGroup.getChildAt(i));
            }
            else if (root instanceof TextView){
                ((TextView) root).setTypeface(Typeface.createFromAsset(getAssets(), fontName));
                ((TextView) root).setTypeface(((TextView) root).getTypeface(), Typeface.BOLD);
            }
        } catch (Exception e) {
            Log.e("KARYA", String.format("Error occured when trying to apply %s font for %s view", fontName, root));
            e.printStackTrace();
        }
    }

    private String fontStyleName(String name){
        String fontStyleName = "";
        switch (name){
            case AppConstants.GILL_SANS_TYPE_FACE:
                fontStyleName =  "gillsansstd.otf";
                break;
            case AppConstants.MONTSERRAT_MEDIUM_TYPE_FACE:
                fontStyleName =  "montserrat_medium.ttf";
                break;
            case "":
                fontStyleName =  "gillsansstd.otf";
                break;
        }
        return fontStyleName;
    }

    public void showErrorMessage(String message){
        showAppCompatAlert("", message, "OK", "", "", false);
    }

    public static String getPickupTime(String dateTime) {
        String pickupTime = "";
        if(dateTime.length() == 12) {
            try {
                pickupTime = dateTime.substring(0, 2)+":"+dateTime.substring(2, 4)+":"+dateTime.substring(4, 8)+"  "
                        +dateTime.substring(8, 10)+":"+dateTime.substring(10, 12);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pickupTime;
    }
}
