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

/**
 * Created by amodrege on 4/6/15.
 */




public class ShowParkingSpacesAroundMeAdapter extends BaseAdapter {



    String[] text;
    Context context;
    int[] images;
    private static LayoutInflater inflater=null;

    public ShowParkingSpacesAroundMeAdapter(ShowParkingSpacesAroundMe mainActivity, String[] data_text, int[] data_images) {
        // TODO Auto-generated constructor stub
        text=data_text;
        context=mainActivity;
        images=data_images;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return text.length;
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
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.activity_parking_space_details, null);
        holder.tv1=(TextView) rowView.findViewById(R.id.header);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView);
        holder.tv1.setText(text[position]);
        holder.img.setImageResource(images[position]);
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
