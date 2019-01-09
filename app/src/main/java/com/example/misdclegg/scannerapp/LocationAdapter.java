package com.example.misdclegg.scannerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationAdapter extends ArrayAdapter<WrappingClass> {
    public LocationAdapter(Context context, ArrayList<WrappingClass> locations){
        super(context, 0, locations);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView ==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_1, parent, false);
        }

        WrappingClass wcRecord = getItem(position);

        TextView tvOne = (TextView) convertView.findViewById(R.id.shelf_column);
        TextView tvTwo = (TextView) convertView.findViewById(R.id.quantity_column);

        tvOne.setText(wcRecord.getShelf());
        tvTwo.setText(Integer.toString(wcRecord.getQuantity()));

        return convertView;
    }
}
