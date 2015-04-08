package edu.sjsu.cmpe295.parket;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ParkingSpaceDetailsCustomAdapter extends BaseAdapter {



        ArrayList<String> data;
        Context context;
        String[] fixedData;
        private static LayoutInflater inflater=null;
        public ParkingSpaceDetailsCustomAdapter(ParkingSpaceDetails mainActivity, ArrayList list, String[] values) {
            // TODO Auto-generated constructor stub
            data=list;
            context=mainActivity;
            fixedData=values;
            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
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

        public class Holder
        {
            TextView tv1;
            TextView tv2;
            ImageView img;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder=new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.activity_parking_space_details, null);
            holder.tv1=(TextView) rowView.findViewById(R.id.header);
            holder.tv2=(TextView) rowView.findViewById(R.id.value);
            holder.img=(ImageView) rowView.findViewById(R.id.imageView);
            holder.tv1.setText(fixedData[position]);
            holder.tv2.setText(data.get(position));
            holder.img.setImageResource(R.drawable.parket_icon_actionbar);
           /* rowView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Toast.makeText(context, "You Clicked "+data[position], Toast.LENGTH_LONG).show();
                }
            });*/
            return rowView;
        }


}
