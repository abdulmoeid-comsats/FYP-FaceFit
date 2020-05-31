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

public class CustomList extends ArrayAdapter<String> {
    private Activity context;
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> status = new ArrayList<String>();
    private ArrayList<String> price = new ArrayList<String>();
    private ArrayList<String> date = new ArrayList<String>();
    private ArrayList<String> count = new ArrayList<String>();




    public CustomList (Activity context, ArrayList<String> names, ArrayList<String> price, ArrayList<String> status, ArrayList<String> date, ArrayList<String> count ){
        super(context, R.layout.bookings_layout, names);
        this.context = context;
        this.names = names;
        this.price = price;
        this.status = status;
        this.date = date;
        this.count = count;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.bookings_layout, null, true);
        TextView txtName = (TextView) listViewItem.findViewById(R.id.orderName);
        TextView txtStatus = (TextView) listViewItem.findViewById(R.id.orderStatus);
        TextView txtPrice = (TextView) listViewItem.findViewById(R.id.orderAmount);
        TextView txtCount = (TextView) listViewItem.findViewById(R.id.orderCount);
        TextView tctDate = (TextView) listViewItem.findViewById(R.id.orderDate);


        txtName.setText(names.get(position));
        txtPrice.setText("Rs. "+price.get(position));
        txtStatus.setText(status.get(position));
        tctDate.setText(date.get(position));
        txtCount.setText(count.get(position));

        return listViewItem;
    }


}