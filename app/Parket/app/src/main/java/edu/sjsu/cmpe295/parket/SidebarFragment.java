package edu.sjsu.cmpe295.parket;

/**
 * Created by amodrege on 4/6/15.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SidebarFragment extends Fragment {
    TextView text;
    ImageView img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.sidebar_show_parking_around_me, container, false);
        String menu = getArguments().getString("Menu");
        text = (TextView) view.findViewById(R.id.detail);
        text.setText(menu);
        return view;
    }
}