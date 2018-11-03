package com.kdz.navikandradio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kdz.item.ItemAbout;
import com.kdz.utils.Constant;
import com.kdz.utils.JsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AboutActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView webView;
    TextView textView_appname, textView_email, textView_website, textView_contact, textView_version;
    ImageView imageView_logo;
    LinearLayout ll_email, ll_website, ll_company, ll_contact;
    String website, email, desc, applogo, appname, appversion, appauthor, appcontact, privacy, developedby;
    //	DBHelper dbHelper;
    ProgressDialog pbar;
    JsonUtils utils;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Configuration configuration = getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(new Locale("fa"));
        }

        setStatusColor();

        toolbar = this.findViewById(R.id.toolbar_about);
        toolbar.setTitle(getString(R.string.menu_about));
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		toolbar.setBackgroundColor(Constant.color);

        pbar = new ProgressDialog(this);
        pbar.setMessage(getResources().getString(R.string.loading));
        pbar.setCancelable(false);

        webView =  findViewById(R.id.webView);
        textView_appname = findViewById(R.id.textView_about_appname);
        textView_email = findViewById(R.id.textView_about_email);
        textView_website = findViewById(R.id.textView_about_site);

        textView_contact = findViewById(R.id.textView_about_contact);
        textView_version = findViewById(R.id.textView_about_appversion);
        imageView_logo = findViewById(R.id.imageView_about_logo);

        ll_email = findViewById(R.id.ll_email);
        ll_website = findViewById(R.id.ll_website);
        ll_contact = findViewById(R.id.ll_contact);
        ll_company = findViewById(R.id.ll_company);

        if (Constant.itemAbout == null) {
            if (JsonUtils.isNetworkAvailable(AboutActivity.this)) {
                new MyTask().execute(Constant.APP_DETAILS_URL);
            } else {
                Toast.makeText(AboutActivity.this, getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
            }
        } else {
            appname = Constant.itemAbout.getAppName();
            applogo = Constant.itemAbout.getAppLogo();
            desc = Constant.itemAbout.getAppDesc();
            appversion = Constant.itemAbout.getAppVersion();
            appauthor = Constant.itemAbout.getAuthor();
            appcontact = Constant.itemAbout.getContact();
            email = Constant.itemAbout.getEmail();
            website = Constant.itemAbout.getWebsite();
            privacy = Constant.itemAbout.getPrivacy();
            developedby = Constant.itemAbout.getDevelopedby();
            setVariables();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pbar.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            pbar.dismiss();

            if (null == result || result.length() == 0) {
                Toast.makeText(AboutActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
                    JSONObject c;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        c = jsonArray.getJSONObject(i);

                        appname = c.getString("app_name");
                        applogo = c.getString("app_logo");
                        desc = c.getString("app_description");
                        appversion = c.getString("app_version");
                        appauthor = c.getString("app_author");
                        appcontact = c.getString("app_contact");
                        email = c.getString("app_email");
                        website = c.getString("app_website");
                        privacy = c.getString("app_privacy_policy");
                        developedby = c.getString("app_developed_by");

                        Constant.itemAbout = new ItemAbout(appname, applogo, desc, appversion, appauthor, appcontact, email, website, privacy, developedby);
//						dbHelper.addtoAbout();
                    }

                    setVariables();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setVariables() {
        textView_appname.setText(appname);
        if (!email.trim().isEmpty()) {
            ll_email.setVisibility(View.VISIBLE);
            textView_email.setText(email);
        }

        if (!website.trim().isEmpty()) {
            ll_website.setVisibility(View.VISIBLE);
            textView_website.setText(website);
        }
        if (!appcontact.trim().isEmpty()) {
            ll_contact.setVisibility(View.VISIBLE);
            textView_contact.setText(appcontact);
        }

        if (!appversion.trim().isEmpty()) {
            textView_version.setText(appversion);
        }
//		textView_desc.setText(desc);
        if (applogo.trim().isEmpty()) {
            imageView_logo.setVisibility(View.GONE);
        } else {
            Picasso
                    .get()
                    .load(Constant.URL_ABOUT_US_LOGO + applogo)
                    .into(imageView_logo);
        }

        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";

        String text = desc.replace("<p>","");


        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setText(text.replace("</p>",""));
    }

    public void setStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusBar));
        }
    }
}
