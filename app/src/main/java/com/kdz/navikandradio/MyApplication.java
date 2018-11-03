package com.kdz.navikandradio;

import android.app.Application;

import com.kdz.utils.DBHelper;
import com.onesignal.OneSignal;

import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iranYekanRegularFaNum.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        OneSignal.startInit(getApplicationContext()).init();

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        try {
            dbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
