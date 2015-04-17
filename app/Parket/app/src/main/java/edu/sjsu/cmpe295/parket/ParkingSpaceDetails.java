package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;


public class ParkingSpaceDetails extends Activity {

    Context context;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_space_listview);

        Bundle bundle = getIntent().getExtras();

        String title = bundle.getString("address");
        String rate = bundle.getString("rate");
        String time = bundle.getString("time");
        String description = bundle.getString("description");
        String disabledAvailablity = bundle.getString("status");


        final ArrayList<String> list = new ArrayList<String>();


        list.add(title);
        list.add(time);
        list.add(rate + " per hour");
        list.add(description);
        list.add(disabledAvailablity);
        list.add("dummy");
        list.add("dummy");


        String[] values = new String[]{"Address", "Availability", "Rate", "Description", "Disabled Parking?", "dummy", "dummy"};


        context = this;

        lv = (ListView) findViewById(R.id.listView);

        lv.setAdapter(new ParkingSpaceDetailsCustomAdapter(this, list, values));


    }

}
