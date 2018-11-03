package com.kdz.navikandradio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kdz.adapter.AdapterCat;
import com.kdz.item.ItemCat;
import com.kdz.utils.Constant;
import com.kdz.utils.JsonUtils;
import com.kdz.utils.RecyclerItemClickListener;
import com.kdz.utils.ZProgressHUD;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class FragmentMagList extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ItemCat> arrayList;
    ArrayList<magazineAdapter> items;
    AdapterCat adapterCat;
    ZProgressHUD progressHUD;
    GridLayoutManager gridLayoutManager;
    SearchView searchView;
    FragmentActivity context;

    public FragmentMagList(FragmentActivity  context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View rootView = inflater.inflate(R.layout.fragment_cat, container, false);

        progressHUD = ZProgressHUD.getInstance(getActivity());
        progressHUD.setMessage(getActivity().getResources().getString(R.string.loading));
        progressHUD.setSpinnerType(ZProgressHUD.FADED_ROUND_SPINNER);

        arrayList = new ArrayList<>();
        items = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView_cat);
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new LoadCat().execute(Constant.URL_MAGAZINE);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
        }

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), (view, position) -> {
            Constant.isBackStack = true;
            LinearLayout itemLayout = (LinearLayout) view;
            TextView txt=itemLayout.findViewById(R.id.textView_cat_name);

            FragmentManager fm = getFragmentManager();

            for (magazineAdapter item :items){
                if(item.mag_name.contentEquals(txt.getText())){
                    FragmentMag f1 = new FragmentMag(item.mag_pdf_url);
                    loadFrag(f1, getResources().getString(R.string.magazine), fm);
                }
            }





        }));

        setHasOptionsMenu(true);
        return rootView;
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {

        if (Constant.isBackStack) {
            FragmentManager fragm = this.context.getSupportFragmentManager();
            for (int i = 0; i < fragm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            Constant.isBackStack = false;
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment, f1, name);
        ft.commit();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(name);



    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {

            if (searchView.isIconified()) {
                recyclerView.setAdapter(adapterCat);
                adapterCat.notifyDataSetChanged();
            } else {
                adapterCat.getFilter().filter(s);
                adapterCat.notifyDataSetChanged();
            }
            return true;
        }
    };

    private class LoadCat extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressHUD.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String json = JsonUtils.getJSONString(strings[0]);

                JSONObject mainJson = new JSONObject(json);
                JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
                JSONObject objJson;
                for (int i = 0; i < jsonArray.length(); i++) {
                    objJson = jsonArray.getJSONObject(i);

                    String id = objJson.getString(Constant.TAG_MAGAZINE_ID);
                    String name = String.format("%s(%s)", objJson.getString(Constant.TAG_MAGAZINE_NAME), objJson.getString(Constant.TAG_MAGAZINE_DATE));
                    items.add(new magazineAdapter(String.format("%s(%s)", objJson.getString(Constant.TAG_MAGAZINE_NAME), objJson.getString(Constant.TAG_MAGAZINE_DATE)),
                            objJson.getString(Constant.TAG_MAGAZINE_PDF), objJson.getString(Constant.TAG_MAGAZINE_ID)));
                    ItemCat objItem = new ItemCat(id, name);
                    arrayList.add(objItem);
                }

                return "1";
            } catch (JSONException e) {
                e.printStackTrace();
                return "0";
            } catch (Exception ee) {
                ee.printStackTrace();
                return "0";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("1")) {
                progressHUD.dismissWithSuccess(getResources().getString(R.string.success));

                adapterCat = new AdapterCat(getActivity(), arrayList);
                recyclerView.setAdapter(adapterCat);

            } else {
                progressHUD.dismissWithFailure(getResources().getString(R.string.error));
                Toast.makeText(getActivity(), getResources().getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
    
    @Override
    public void onResume() {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.category));
        super.onResume();
    }
}

class magazineAdapter {
    String mag_name, mag_pdf_url, mag_id;

    public magazineAdapter(String mag_name, String mag_pdf_url, String mag_id) {
        this.mag_name = mag_name;

        this.mag_pdf_url = mag_pdf_url;
        this.mag_id = mag_id;
    }
    
    
}
