package com.kdz.navikandradio;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

import com.kdz.utils.Constant;
import com.kdz.utils.JsonUtils;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Configuration configuration = getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(new Locale("fa"));
        }

//        hideStatusBar();
        setStatusColor();

        try {
           Constant.isFromPush = getIntent().getExtras().getBoolean("ispushnoti", false);
           Constant.pushID = getIntent().getExtras().getString("noti_nid");
        } catch (Exception e) {
            Constant.isFromPush = false;
        }
        try {
            Constant.isFromNoti = getIntent().getExtras().getBoolean("isnoti", false);
        } catch (Exception e) {
            Constant.isFromNoti = false;
        }

        JsonUtils jsonUtils = new JsonUtils(SplashActivity.this);

        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constant.GRID_PADDING, r.getDisplayMetrics());
        Constant.columnWidth = (int) ((jsonUtils.getScreenWidth() - ((Constant.NUM_OF_COLUMNS + 1) * padding)) / Constant.NUM_OF_COLUMNS);

        if(!Constant.isFromNoti) {
            new Handler().postDelayed(() -> openMainActivity(), 2000);
        } else {
            openMainActivity();
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void setStatusColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusBar));
        }
    }
}