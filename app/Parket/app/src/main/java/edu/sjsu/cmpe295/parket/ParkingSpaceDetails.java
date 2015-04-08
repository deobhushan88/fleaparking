package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toolbar;

import java.util.ArrayList;


public class ParkingSpaceDetails extends Activity {

    Context context;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_space_listview);


        getActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle bundle = getIntent().getExtras();

        String title = bundle.getString("address");

        String rate = bundle.getString("rate");

        String time = bundle.getString("time");


        final ArrayList<String> list = new ArrayList<String>();


        list.add(title);
        list.add(time);
        list.add(rate+" per hour");
        list.add("dummy");
        list.add("dummy");
        list.add("dummy");
        list.add("dummy");


        String[] values = new String[] { "Address", "Availability", "Rate","dummy","dummy","dummy","dummy"};


        context = this;

        lv = (ListView) findViewById(R.id.listView);

        lv.setAdapter(new ParkingSpaceDetailsCustomAdapter(this,list,values));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parking_space_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
