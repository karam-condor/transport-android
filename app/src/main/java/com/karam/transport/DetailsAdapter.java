package com.karam.transport;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailsAdapter  extends BaseExpandableListAdapter {
    Context context;
    ArrayList<String> titles;
    HashMap<String, ArrayList<String>> items;

    public DetailsAdapter(Context context, ArrayList<String> titles, HashMap<String, ArrayList<String>> items) {
        this.titles = titles;
        this.items = items;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return titles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return items.get(titles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return titles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return items.get(titles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView =  inflater.inflate(R.layout.info_group, null);
        }
        ImageView detailsGroupImgvw = convertView.findViewById(R.id.details_group_imgvw);
        TextView detailsGrouptxtvw = convertView.findViewById(R.id.details_group_txtvw);
        if (groupPosition == 0) {
            detailsGroupImgvw.setImageResource(R.drawable.details_adress);
        } else if (groupPosition == 1) {
            detailsGroupImgvw.setImageResource(R.drawable.details_obs);
        }
        detailsGrouptxtvw.setText(titles.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.info_item, null);
        }
        TextView details_item_txtvw = convertView.findViewById(R.id.details_item_txtvw);
        details_item_txtvw.setText(String.valueOf(items.get(titles.get(groupPosition)).get(childPosition)));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
