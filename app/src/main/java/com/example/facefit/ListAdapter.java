package com.example.facefit;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ListAdapter extends  ArrayAdapter<String>  {

    private Activity context;
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> brands = new ArrayList<String>();
    private ArrayList<String> price = new ArrayList<String>();
    private ArrayList<String> url = new ArrayList<String>();



    public ListAdapter(Activity context, ArrayList<String> names, ArrayList<String> price, ArrayList<String> brand, ArrayList<String> url ){
        super(context, R.layout.frames_layout, names);
        this.context = context;
        this.names = names;
        this.price = price;
        this.brands = brand;
        this.url = url;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.frames_layout, null, true);
            TextView txtName = (TextView) listViewItem.findViewById(R.id.nameFrame);
            TextView txtBrand = (TextView) listViewItem.findViewById(R.id.brandFrame);
            TextView txtPrice = (TextView) listViewItem.findViewById(R.id.priceFrame);
            ImageView image = (ImageView) listViewItem.findViewById(R.id.imgFrame);


        txtName.setText(names.get(position));
        txtPrice.setText(price.get(position));
        txtBrand.setText(brands.get(position));
        Picasso.get().load(url.get(position)).into(image);


        return listViewItem;
    }

}