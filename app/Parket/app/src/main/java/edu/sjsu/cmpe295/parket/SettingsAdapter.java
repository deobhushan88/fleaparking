package edu.sjsu.cmpe295.parket;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by amodrege on 4/12/15.
 */
public class SettingsAdapter  extends BaseAdapter
{

    String[] settings1;
    Context context;
    String[] settings2;
    private static LayoutInflater inflater = null;

    public SettingsAdapter(Settings mainActivity, String[] values1, String[] values2) {
        // TODO Auto-generated constructor stub
        settings1 = values1;
        settings2 = values2;
        context = mainActivity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return settings1.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub

        switch (position) {

            case 0:
                convertView = inflater.inflate(R.layout.settings_activity_3, null);
                ((TextView) convertView.findViewById(R.id.tva3)).setText(settings1[position]);
                break;

            case 1:
                convertView = inflater.inflate(R.layout.settings_activity_1, null);
                ((TextView) convertView.findViewById(R.id.tv1a1)).setText(settings1[position]);
                ((TextView) convertView.findViewById(R.id.tv2a1)).setText(settings2[position-1]);
                break;


            case 2:
                convertView = inflater.inflate(R.layout.settings_activity_1, null);
                ((TextView) convertView.findViewById(R.id.tv1a1)).setText(settings1[position]);
                ((TextView) convertView.findViewById(R.id.tv2a1)).setText(settings2[position-1]);
                convertView.findViewById(R.id.tv2a1);
                convertView.setClickable(true);
                break;

            case 3:
                convertView = inflater.inflate(R.layout.settings_activity_3, null);
                ((TextView) convertView.findViewById(R.id.tva3)).setText(settings1[position]);
                break;

            case 4:
                convertView = inflater.inflate(R.layout.settings_activity_1, null);
                ((TextView) convertView.findViewById(R.id.tv1a1)).setText(settings1[position]);
                ((TextView) convertView.findViewById(R.id.tv2a1)).setText(settings2[position-2]);
                break;

            case 5:
                convertView = inflater.inflate(R.layout.settings_activity_3, null);
                ((TextView) convertView.findViewById(R.id.tva3)).setText(settings1[position]);
                break;

            case 6:
                convertView = inflater.inflate(R.layout.settings_activity_2, null);
                ((TextView) convertView.findViewById(R.id.tv1a2)).setText(settings1[position]);
                ((Switch) convertView.findViewById(R.id.switchBtn)).setChecked(true);
                break;

            case 7:
                convertView = inflater.inflate(R.layout.settings_activity_2, null);
                ((TextView) convertView.findViewById(R.id.tv1a2)).setText(settings1[position]);
                ((Switch) convertView.findViewById(R.id.switchBtn)).setChecked(false);
                break;



        }

        return convertView;

    }



}
