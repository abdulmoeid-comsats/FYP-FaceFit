package com.example.facefit;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter<String> {
    private Activity context;
    private ArrayList<String> images = new ArrayList<String>();

    public ImageAdapter(Activity context, ArrayList<String> images){
        super(context, R.layout.exp_layout, images);
        this.context = context;
        this.images = images;
        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.exp_layout, null, true);
        ImageView image = (ImageView) listViewItem.findViewById(R.id.userImg);
        Picasso.get().load(images.get(position)).into(image);


        return listViewItem;
    }
}
