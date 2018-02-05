package com.example.emyeraky.nearlymap;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AdapterReview extends BaseAdapter {
    Context context;
    Review[] reviews;

    public AdapterReview(Context context,Review[] reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        return reviews.length;
    }

    @Override
    public Object getItem(int i) {
        return reviews[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.review_list, null);
        }
        ImageView profile = (ImageView) view.findViewById(R.id.profile_image);
        TextView name = (TextView) view.findViewById(R.id.auther);
        TextView descripe = (TextView) view.findViewById(R.id.descripe);
        TextView time = (TextView) view.findViewById(R.id.times);
        Picasso.with(context).load(reviews[i].getProfile()).into(profile);
        name.setText(reviews[i].getAuther());
        descripe.setText(reviews[i].getText());
        time.setText(reviews[i].getTime());
        return view;
    }
}

