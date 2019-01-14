package com.example.misdclegg.scannerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ScheduleAdapter extends ArrayAdapter<ScheduleClass> {
    public ScheduleAdapter(Context context, ArrayList<ScheduleClass> locations){
        super(context, 0, locations);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView ==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_2, parent, false);
        }

        ScheduleClass wcRecord = getItem(position);

        TextView tvOne = (TextView) convertView.findViewById(R.id.sequence_column);
        TextView tvTwo = (TextView) convertView.findViewById(R.id.work_order_column);
        TextView tvThree = (TextView) convertView.findViewById(R.id.quantity_column);
        TextView tvFour = (TextView) convertView.findViewById(R.id.stock_column);

        String str = Float.toString(wcRecord.getQuantity());

        tvOne.setText(wcRecord.getSequence());
        tvTwo.setText(wcRecord.getWorkOrder());
        tvThree.setText(str.substring(0, str.length() - 2));
        tvFour.setText(Integer.toString(wcRecord.getInStock()));

        return convertView;
    }
}
