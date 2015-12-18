package com.example.akash.hackerflow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Akash on 11/21/2015.
 */
public class BeaconsListAdapter extends ArrayAdapter<BeaconDetails> {
    private ArrayList<BeaconDetails> beaconDetailList;
    Context c;

    BeaconsListAdapter(ArrayList<BeaconDetails> beaconList, Context c) {
        super(c, R.layout.beacon_item, beaconList);
        this.beaconDetailList = beaconList;
        this.c = c;
    }

    @Override
    public int getCount() {
        return beaconDetailList.size();
    }

    @Override
    public BeaconDetails getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (v == null) {
            LayoutInflater li = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.beacon_item, null);
        }
        TextView beaconName = (TextView) v.findViewById(R.id.beaconName);
        TextView beaconDistance = (TextView) v.findViewById(R.id.beaconDistance);

        BeaconDetails bd = beaconDetailList.get(i);
        beaconName.setText(bd.getBeaconName());
        beaconDistance.setText(bd.getBeaconDistance());
        return v;
    }
}