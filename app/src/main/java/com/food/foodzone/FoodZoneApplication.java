package com.food.foodzone;

import android.app.Application;

import com.google.firebase.FirebaseApp;
/**FoodZoneApplication  class is uses to initialize firebase**/
public class FoodZoneApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
