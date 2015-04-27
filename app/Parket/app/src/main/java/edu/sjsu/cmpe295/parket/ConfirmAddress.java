package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class ConfirmAddress extends Activity implements OnMapReadyCallback {


    String address;
    List<Address> parkingAddress;
    Address location;
    double lat, lon;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_address);

        //Adding a toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_confirmAddress);
        toolbar.setTitle(getResources().getString(R.string.title_activity_confirm_address));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        Bundle bundle = getIntent().getExtras();
        Geocoder coder = new Geocoder(this);
        try {
            address = bundle.getString("address");

            if (address == null) {
                Log.v("Address empty", "Address is Empty");
            }
            parkingAddress = coder.getFromLocationName(address, 1);
            location = parkingAddress.get(0);
            lat = location.getLatitude();
            lon = location.getLongitude();

        } catch (Exception e) {

        }
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(lat, lon));

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(18);

        map.moveCamera(center);
        map.animateCamera(zoom);

        map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title("Marker"));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
