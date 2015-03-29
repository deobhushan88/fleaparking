package edu.sjsu.cmpe295.parket;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AdvancedSearchMap extends ActionBarActivity implements OnMapReadyCallback {

    String addresses;
    List<JSONObject> vacantParkingAddress;
    JSONObject jsonObject = null, arrObject;
    JSONArray jsonArray;
    String d;
    ArrayList<Double> latitude = new ArrayList<Double>();
    ArrayList<Double> longitude = new ArrayList<Double>();
    ArrayList<String> address = new ArrayList<String>();
    ArrayList<String> rate = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search_map);


        ActionBar actionBar = getSupportActionBar();

        Bundle bundle = getIntent().getExtras();
        Geocoder coder = new Geocoder(this);


        try {

            addresses = bundle.getString("addresses");

            Log.d("add:::::::",addresses);

            if (addresses == null) {
                Log.v("Address empty", "Address is Empty");
            }



        } catch(Exception e) {

        }


        try {
            jsonObject = new JSONObject(addresses);

            jsonArray = jsonObject.getJSONArray("parkingSpaces");

            for(int i=0;i<jsonArray.length();i++)
            {
                arrObject = jsonArray.getJSONObject(i);
                latitude.add(Double.parseDouble(arrObject.getString("parkingSpaceLat")));
                longitude.add(Double.parseDouble(arrObject.getString("parkingSpaceLong")));
                address.add(arrObject.getString("parkingSpaceAddress"));
                rate.add(arrObject.getString("parkingSpaceRate"));
                Log.d("JSON DTAA",arrObject.getString("parkingSpaceLat")+"-->"+arrObject.getString("parkingSpaceLong"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        coder = new Geocoder(this);



        MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);



    }



    @Override
    public void onMapReady(GoogleMap map) {

        for(int j=0;j<latitude.size();j++) {

            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(new LatLng(latitude.get(j), longitude.get(j)));

            CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);

            map.moveCamera(center);
            map.animateCamera(zoom);

            map.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude.get(j), longitude.get(j)))
                    .title(address.get(j)).snippet(rate.get(j) + "$ / hour").flat(false).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));



        }




        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                /*if(marker.getTitle().equals("10601 Larry Way, Cupertino, CA 95014")) // if marker source is clicked
                    Toast.makeText(AdvancedSearchMap.this, marker.getTitle(), Toast.LENGTH_SHORT).show();*/
                String addressParkingSpace = marker.getTitle();



            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_advanced_search_map, menu);
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
}
