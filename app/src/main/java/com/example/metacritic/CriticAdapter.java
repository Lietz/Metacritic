package com.example.metacritic;

import android.content.Context;
import android.graphics.Color;
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

import java.util.List;

/**
 * Created by CJ on 2017/6/14.
 */

public class CriticAdapter extends ArrayAdapter<Critic> {
    private int resourceId;



    public CriticAdapter(Context context, int textViewResourceId, List<Critic> objects) {
        super(context, textViewResourceId, objects);
        resourceId=textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Critic critic = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView criticsource = (TextView) view.findViewById(R.id.critic_source);
        TextView criticscore=(TextView)view.findViewById(R.id.critic_score);
        TextView criticdate = (TextView) view.findViewById(R.id.critic_date);
        TextView criticauthor = (TextView) view.findViewById(R.id.critic_author);
        TextView criticsummary = (TextView) view.findViewById(R.id.critic_summary);
        criticsource.setText(critic.getSource());
        criticdate.setText("Release Date: "+critic.getDate());
        criticscore.setText(critic.getScore());
        criticsummary.setText(critic.getSummary());

        if (critic.getAuthor().isEmpty()) {
            criticauthor.setVisibility(View.GONE);
        } else {
            criticauthor.setText(critic.getAuthor());
        }
        if (critic.getScore().isEmpty()) {
            criticscore.setVisibility(View.INVISIBLE);
        }else if (critic.getScore().contains("tbd")){   //==对字符串有待商榷~
            Log.d("tbd",critic.getScore());
            criticscore.setBackgroundColor(Color.GRAY);
        } else if (new Integer(critic.getScore()) >60){
            criticscore.setBackgroundColor(Color.GREEN);
        }else if (new Integer(critic.getScore()) <=40){
            criticscore.setBackgroundColor(Color.RED);
        }else {
            criticscore.setBackgroundColor(Color.parseColor("#fff700"));
        }
        return view;
    }
}
