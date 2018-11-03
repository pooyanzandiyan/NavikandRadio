package com.kdz.navikandradio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kdz.adapter.AdapterSongList;
import com.kdz.item.ItemSong;
import com.kdz.utils.BlurTransform;
import com.kdz.utils.Constant;
import com.kdz.utils.JsonUtils;
import com.kdz.utils.RecyclerItemClickListener;
import com.kdz.utils.ZProgressHUD;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentSongByArtist extends Fragment {


    RecyclerView recyclerView;
    ArrayList<ItemSong> arrayList;
    public static AdapterSongList adapterSongList;
    ZProgressHUD progressHUD;
    LinearLayoutManager linearLayoutManager;
    SearchView searchView;
    String artist_name = "", image = "";
    TextView textView_empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View rootView = inflater.inflate(R.layout.activity_song_by_cat, container, false);



        textView_empty = rootView.findViewById(R.id.textView_empty_artist);
        ImageView imageView = rootView.findViewById(R.id.imageView_back);
        imageView.setVisibility(View.VISIBLE);

        progressHUD = ZProgressHUD.getInstance(getActivity());
        progressHUD.setMessage(getResources().getString(R.string.loading));
        progressHUD.setSpinnerType(ZProgressHUD.FADED_ROUND_SPINNER);

        artist_name = getArguments().getString("artist");
        image = getArguments().getString("image");
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(artist_name);
        Picasso.get()
                .load(image)
                .transform(new BlurTransform(getActivity()))
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        rl.setBackground(new BitmapDrawable(bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }



                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        arrayList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView_songbycat);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new LoadSongs().execute(Constant.URL_SONG_BY_ARTIST + artist_name.replace(" ","%20"));
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
        }

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(JsonUtils.isNetworkAvailable(getActivity())) {
                    showInter(position);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
                }
            }
        }));

        setHasOptionsMenu(true);
        return rootView;
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
                recyclerView.setAdapter(adapterSongList);
                adapterSongList.notifyDataSetChanged();
            } else {
                adapterSongList.getFilter().filter(s);
                adapterSongList.notifyDataSetChanged();
            }
            return true;
        }
    };

    private class LoadSongs extends AsyncTask<String, String, String> {

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
                    String thumb = objJson.getString(Constant.TAG_THUMB_B).replace(" ","%20");
                    String thumb_small = objJson.getString(Constant.TAG_THUMB_S).replace(" ","%20");

                    ItemSong objItem = new ItemSong(id,cid,cname,artist,url,thumb,thumb_small,name,duration,desc);
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

                adapterSongList = new AdapterSongList(arrayList);
                recyclerView.setAdapter(adapterSongList);
            } else {
                progressHUD.dismissWithFailure(getResources().getString(R.string.error));
                Toast.makeText(getActivity(), getResources().getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
            }

            if(arrayList.size() == 0) {
                textView_empty.setVisibility(View.VISIBLE);
            } else {
                textView_empty.setVisibility(View.GONE);
            }
            super.onPostExecute(s);
        }
    }

    private int getPosition(String id) {
        int count=0;
        for(int i=0;i<arrayList.size();i++)
        {
            if(id.equals(arrayList.get(i).getId()))
            {
                count = i;
                break;
            }
        }
        return count;
    }

    private void showInter(final int pos) {
        Constant.adCount = Constant.adCount + 1;
            playIntent(pos);

    }

    private void playIntent(int position) {
        Constant.isOnline = true;
        Constant.frag = "art";
        Constant.arrayList_play.clear();
        Constant.arrayList_play.addAll(arrayList);
        Constant.playPos = getPosition(adapterSongList.getID(position));
        MainActivity.touchDesc();
        ((MainActivity)getActivity()).changeText(arrayList.get(position).getMp3Name(),arrayList.get(position).getCategoryName(),position+1,arrayList.size(),arrayList.get(position).getDuration(),arrayList.get(position).getImageBig(),"artist");

        Constant.context = getActivity();
        Intent intent = new Intent(getActivity(), PlayerService.class);
        intent.setAction(PlayerService.ACTION_FIRST_PLAY);
        getActivity().startService(intent);
    }
}