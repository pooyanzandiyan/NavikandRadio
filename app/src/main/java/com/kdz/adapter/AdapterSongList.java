package com.kdz.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.kdz.item.ItemSong;
import com.kdz.navikandradio.R;
import com.kdz.utils.Constant;

import java.util.ArrayList;

import es.claucookie.miniequalizerlibrary.EqualizerView;


public class AdapterSongList extends RecyclerView.Adapter<AdapterSongList.MyViewHolder> {

    private ArrayList<ItemSong> arrayList;
    private ArrayList<ItemSong> filteredArrayList;
    private NameFilter filter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_song, textView_duration, textView_catname;
        EqualizerView equalizer;

        public MyViewHolder(View view) {
            super(view);
            textView_song = view.findViewById(R.id.textView_songname);
            textView_duration = view.findViewById(R.id.textView_songduration);
            equalizer = view.findViewById(R.id.equalizer_view);
            textView_catname = view.findViewById(R.id.textView_catname);
        }
    }

    public AdapterSongList(ArrayList<ItemSong> arrayList) {
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_songlist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.textView_song.setText(arrayList.get(position).getMp3Name());
        holder.textView_duration.setText(arrayList.get(position).getDuration());

        if(Constant.isPlaying && Constant.arrayList_play.get(Constant.playPos).getId().equals(arrayList.get(position).getId())) {
            holder.equalizer.animateBars();
            holder.equalizer.setVisibility(View.VISIBLE);
        } else {
            holder.equalizer.stopBars();
            holder.equalizer.setVisibility(View.GONE);
        }

        if(arrayList.get(position).getCategoryName() != null) {
            holder.textView_catname.setText(arrayList.get(position).getCategoryName());
        } else {
            holder.textView_catname.setText(arrayList.get(position).getArtist());
        }
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public String getID(int pos) {
        return arrayList.get(pos).getId();
    }

    public Filter getFilter() {
        if (filter == null){
            filter  = new NameFilter();
        }
        return filter;
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<ItemSong> filteredItems = new ArrayList<ItemSong>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getMp3Name();
                    if (nameList.toLowerCase().contains(constraint))
                        filteredItems.add(filteredArrayList.get(i));
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = filteredArrayList;
                    result.count = filteredArrayList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arrayList = (ArrayList<ItemSong>) results.values;
            notifyDataSetChanged();
        }
    }
}