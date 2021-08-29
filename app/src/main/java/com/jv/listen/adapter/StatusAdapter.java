package com.jv.listen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.jv.listen.R;

import java.util.ArrayList;

public class StatusAdapter extends BaseAdapter {

    Context context;

    private ArrayList<String> tableName;
    private ArrayList<Long> difference;
    int lightColor;
    int lowColor;

    public StatusAdapter(Context context, ArrayList<String> tableName, ArrayList<Long> difference, int lightColor, int lowColor) {
        super();
        this.context = context;
        this.tableName = tableName;
        this.difference = difference;
        this.lightColor = lightColor;
        this.lowColor = lowColor;
    }

    @Override
    public int getCount() {
        return tableName.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.table_status_item,viewGroup,false);
        TextView tableitem = view.findViewById(R.id.tableitem);
        TextView tableInfo = view.findViewById(R.id.tableInfo);
        CardView statuslight = view.findViewById(R.id.statuslight);
        if(difference.get(i) > 60000)
            statuslight.setCardBackgroundColor(lightColor);
        else
            statuslight.setCardBackgroundColor(lowColor);
        tableInfo.setText("Table -> " + tableName.get(i) + "\tdifference -> " + difference.get(i).toString());
        // tableitem.setText(tableName.get(i));
        return view;
    }
}
