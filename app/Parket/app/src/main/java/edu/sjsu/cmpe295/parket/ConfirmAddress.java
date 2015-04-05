package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.location.Geocoder;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.List;


public class ConfirmAddress extends Activity implements OnMapReadyCallback{


   String address;
    List<Address> parkingAddress;
    Address location;
    double lat,lon;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_address);

       /* ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);*/

      Bundle bundle = getIntent().getExtras();
        Geocoder coder = new Geocoder(this);

        try {

            address = bundle.getString("address");

            if (address == null) {
                Log.v("Address empty","Address is Empty");
            }

           parkingAddress = coder.getFromLocationName(address,1);
           location = parkingAddress.get(0);
           lat = location.getLatitude();
           lon = location.getLongitude();

           Log.v("Lat Long",lat+"  "+lon);

        } catch(Exception e) {

        }


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }



   @Override
    public void onMapReady(GoogleMap map) {

        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(lat, lon));

        CameraUpdate zoom=CameraUpdateFactory.zoomTo(18);

        map.moveCamera(center);
        map.animateCamera(zoom);

        map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title("Marker"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_confirm_address, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
