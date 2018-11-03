package com.kdz.navikandradio;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kdz.adapter.AdapterRecent;
import com.kdz.item.ItemSong;
import com.kdz.utils.Constant;
import com.kdz.utils.DBHelper;
import com.kdz.utils.JsonUtils;
import com.kdz.utils.RecyclerItemClickListener;
import com.kdz.utils.ZProgressHUD;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentHome extends Fragment {

    DBHelper dbHelper;
    RecyclerView recyclerView;
    ArrayList<ItemSong> arrayList;
    ArrayList<ItemSong> arrayList_recent;
    AdapterRecent adapterRecent;
    ZProgressHUD progressHUD;
    LinearLayoutManager linearLayoutManager;
    public ViewPager viewpager;
    ImagePagerAdapter adapter;
    TextView textView_empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);

        dbHelper = new DBHelper(getActivity());

        progressHUD = ZProgressHUD.getInstance(getActivity());
        progressHUD.setMessage(getActivity().getResources().getString(R.string.loading));
        progressHUD.setSpinnerType(ZProgressHUD.FADED_ROUND_SPINNER);

        textView_empty = rootView.findViewById(R.id.textView_recent_empty);

        adapter = new ImagePagerAdapter();
        viewpager = rootView.findViewById(R.id.viewPager_home);
        viewpager.setPadding(80,20,80,20);
        viewpager.setClipToPadding(false);
        viewpager.setPageMargin(40);
        viewpager.setClipChildren(false);
//        viewpager.setPageTransformer(true,new BackgroundToForegroundTransformer());

        arrayList = new ArrayList<>();
        arrayList_recent = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView_home_recent);
        linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new LoadLatestNews().execute(Constant.URL_LATEST);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
        }

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), (view, position) -> {
            if(JsonUtils.isNetworkAvailable(getActivity())) {
                Constant.isOnline = true;
                Constant.arrayList_play.clear();
                Constant.arrayList_play.addAll(arrayList_recent);
                Constant.playPos = position;
                ((MainActivity)getActivity()).changeText(arrayList_recent.get(position).getMp3Name(),arrayList_recent.get(position).getCategoryName(),position+1,arrayList_recent.size(),arrayList_recent.get(position).getDuration(),arrayList_recent.get(position).getImageBig(),"home");

                Constant.context = getActivity();
                if(position == 0) {
                    Intent intent = new Intent(getActivity(), PlayerService.class);
                    intent.setAction(PlayerService.ACTION_FIRST_PLAY);
                    getActivity().startService(intent);
                }
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
            }
        }));

        return rootView;
    }

    private class LoadLatestNews extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressHUD.show();
            arrayList.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String json = JsonUtils.getJSONString(strings[0]);

                JSONObject mainJson = new JSONObject(json);
                JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
                JSONObject objJson = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    objJson = jsonArray.getJSONObject(i);

                    String id = objJson.getString(Constant.TAG_ID);
                    String cid = objJson.getString(Constant.TAG_CAT_ID);
                    String cname = objJson.getString(Constant.TAG_CAT_NAME);
                    String artist = objJson.getString(Constant.TAG_ARTIST);
                    String name = objJson.getString(Constant.TAG_SONG_NAME);
                    String url = objJson.getString(Constant.TAG_MP3_URL);
                    String desc = objJson.getString(Constant.TAG_DESC);
                    String duration = objJson.getString(Constant.TAG_DURATION);
                    String image = objJson.getString(Constant.TAG_THUMB_B).replace(" ","%20");
                    String image_small = objJson.getString(Constant.TAG_THUMB_S).replace(" ","%20");

                    ItemSong objItem = new ItemSong(id,cid,cname,artist,url,image,image_small,name,duration,desc);
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
            if(s.equals("1")) {
                progressHUD.dismissWithSuccess(getResources().getString(R.string.success));
//                setLatestVariables(0);

                if(Constant.isAppFirst) {
                    if(arrayList.size()>0) {
                        Constant.isAppFirst = false;
                        Constant.arrayList_play.addAll(arrayList);
                        ((MainActivity)getActivity()).changeText(arrayList.get(0).getMp3Name(),arrayList.get(0).getCategoryName(),1,arrayList.size(),arrayList.get(0).getDuration(),arrayList.get(0).getImageBig(),"home");
                        Constant.context = getActivity();
                    }
                }

                viewpager.setAdapter(adapter);

                loadRecent();
//                adapterPagerTrending = new AdapterPagerTrending(getActivity(),Constant.arrayList_trending);
//                viewPager_trending.setAdapter(adapterPagerTrending);

//                adapterTopStories = new AdapterTopStories(getActivity(),Constant.arrayList_topstories);
//                listView_topstories.setAdapter(adapterTopStories);

//                setListViewHeightBasedOnChildren(listView_topstories);



            } else {
                progressHUD.dismissWithFailure(getResources().getString(R.string.error));
                Toast.makeText(getActivity(), getResources().getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    private void loadRecent() {
        arrayList_recent = dbHelper.loadDataRecent();
        adapterRecent = new AdapterRecent(getActivity(),arrayList_recent);
        recyclerView.setAdapter(adapterRecent);

        if(arrayList_recent.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            textView_empty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textView_empty.setVisibility(View.GONE);
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        public ImagePagerAdapter() {
            // TODO Auto-generated constructor stub

            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View imageLayout = inflater.inflate(R.layout.viewpager_home, container, false);
            assert imageLayout != null;
            ImageView imageView = imageLayout.findViewById(R.id.imageView_pager_home);
            final ProgressBar spinner = imageLayout.findViewById(R.id.loading_home);
            TextView title = imageLayout.findViewById(R.id.textView_pager_home_title);
            TextView cat = imageLayout.findViewById(R.id.textView_pager_home_cat);
            RelativeLayout rl = imageLayout.findViewById(R.id.rl_homepager);

            title.setText(arrayList.get(position).getMp3Name());
            cat.setText(arrayList.get(position).getCategoryName());

            Picasso.get()
                .load(arrayList.get(position).getImageBig())
                .placeholder(R.mipmap.app_icon)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        spinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        spinner.setVisibility(View.GONE);
                    }
                });

            rl.setOnClickListener(view -> {
                if(JsonUtils.isNetworkAvailable(getActivity())) {
                    showInter();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
                }
            });

            container.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private void showInter() {
        Constant.adCount = Constant.adCount + 1;
            playIntent();
    }

    private void playIntent() {
        Constant.isOnline = true;
        int pos = viewpager.getCurrentItem();
        Constant.arrayList_play.clear();
        Constant.arrayList_play.addAll(arrayList);
        Constant.playPos = pos;
        ((MainActivity)getActivity()).changeText(arrayList.get(pos).getMp3Name(),arrayList.get(pos).getCategoryName(),pos+1,arrayList.size(),arrayList.get(pos).getDuration(),arrayList.get(pos).getImageBig(),"home");

        Constant.context = getActivity();
        if(pos == 0) {
            Intent intent = new Intent(getActivity(), PlayerService.class);
            intent.setAction(PlayerService.ACTION_FIRST_PLAY);
            getActivity().startService(intent);
        }
    }
}