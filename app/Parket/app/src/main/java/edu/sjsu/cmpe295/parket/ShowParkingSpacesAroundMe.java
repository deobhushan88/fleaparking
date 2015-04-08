package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toolbar;

import java.util.ArrayList;


public class ShowParkingSpacesAroundMe extends Activity {


    String[] menu;
    int[] img;
    DrawerLayout dLayout;
    ListView dList;
    ArrayAdapter<String> adapter;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_parking_around_me);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        toolbar.setTitle("Parket");

        toolbar.setLogo(R.drawable.ic_drawer);

        toolbar.inflateMenu(R.menu.menu_show_parking_space_around_me);

        menu = new String[]{"Find Parking","Rent out your parking space","Settings","Sign in / Sign out","Help"};


        img = new int[]{R.drawable.parket_icon_actionbar,R.drawable.parket_icon_actionbar,R.drawable.parket_icon_actionbar,R.drawable.parket_icon_actionbar,R.drawable.parket_icon_actionbar};

        context = this;

        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dList = (ListView) findViewById(R.id.left_drawer);

        dList.setAdapter(new ShowParkingSpacesAroundMeAdapter(this,menu,img));
        dList.setSelector(android.R.color.holo_orange_light);
        dList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                dLayout.closeDrawers();
                Bundle args = new Bundle();

                args.putString("Menu", menu[position]);
                Fragment detail = new SidebarFragment();
                detail.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, detail).commit();
            }
        });





    }






  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.menu_show_parking_space_around_me, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
