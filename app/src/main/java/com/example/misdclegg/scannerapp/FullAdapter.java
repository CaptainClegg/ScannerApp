package com.example.misdclegg.scannerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FullAdapter extends ArrayAdapter<FullClass>{

        public FullAdapter(Context context, ArrayList<FullClass> locations){
            super(context, 0, locations);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            if(convertView ==null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_full_list, parent, false);
            }

            FullClass wcRecord = getItem(position);

            TextView tvOne = (TextView) convertView.findViewById(R.id.work_order_column);
            TextView tvTwo = (TextView) convertView.findViewById(R.id.location_column);
            TextView tvThree = (TextView) convertView.findViewById(R.id.quantity_column);

            tvOne.setText(wcRecord.getSerial());
            tvTwo.setText(wcRecord.getShelf());
            tvThree.setText(Integer.toString(wcRecord.getQuantity()));

            return convertView;
        }
}



