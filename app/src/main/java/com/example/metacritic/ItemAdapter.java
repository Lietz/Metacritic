package com.example.metacritic;

import android.content.Context;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CJ on 2017/6/12.
 */

public class ItemAdapter extends ArrayAdapter<Listitem> {

    private int resourceId;

    public ItemAdapter(Context context, int textViewResourceId, List<Listitem> objects) {
        super(context, textViewResourceId, objects);
        resourceId=textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Listitem item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        ImageView itemImage = (ImageView) view.findViewById(R.id.item_image);
        TextView itemTitle = (TextView) view.findViewById(R.id.item_title);
        TextView score=(TextView)view.findViewById(R.id.item_score);
        TextView releaseDate = (TextView) view.findViewById(R.id.item_releasedate);
    //    itemImage.setImageResource(R.drawable.titlelogo);
        Glide
                .with(getContext())
                .load(item.getImgUrl())
                .into(itemImage);
        itemTitle.setText(item.getTitle());
        releaseDate.setText("Release Date: "+item.getReleaseDate());
        if (item.getReleaseDate().length() == 0||item.getStats().length()!=0) {
            releaseDate.setText(item.getStats());
        }
        score.setText(item.getMetascore());
        if (item.getMetascore().isEmpty()) {
            score.setBackgroundColor(Color.WHITE);
        }else if (item.getMetascore().contains("tbd")){   //==对字符串有待商榷~
            Log.d("tbd",item.getMetascore());
            score.setBackgroundColor(Color.GRAY);
        } else if (new Integer(item.getMetascore()) >60){
            score.setBackgroundColor(Color.GREEN);
        }else if (new Integer(item.getMetascore()) <=40){
            score.setBackgroundColor(Color.RED);
        }else {
            score.setBackgroundColor(Color.parseColor("#fff700"));
        }
        return view;
    }
}
