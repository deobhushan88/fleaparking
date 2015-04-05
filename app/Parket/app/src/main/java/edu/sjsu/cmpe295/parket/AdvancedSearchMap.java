package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
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
import java.util.Date;
import java.util.List;


public class AdvancedSearchMap extends Activity implements OnMapReadyCallback {

    String addresses;
    List<JSONObject> vacantParkingAddress;
    JSONObject jsonObject = null, arrObject;
    JSONArray jsonArray;
    String d;
    ArrayList<Double> latitude = new ArrayList<Double>();
    ArrayList<Double> longitude = new ArrayList<Double>();
    ArrayList<String> address = new ArrayList<String>();
    ArrayList<String> rate = new ArrayList<String>();
    ArrayList<String> startTime = new ArrayList<String>();
    ArrayList<String> endTime = new ArrayList<String>();
    String start_time, end_time;
    Bundle bundle;
    Geocoder coder;
    int i,j;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search_map);

    }

    @Override
    protected void onStart() {
        super.onStart();

        bundle = getIntent().getExtras();
        coder = new Geocoder(this);


        try {

            addresses = bundle.getString("addresses");



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
                startTime.add(arrObject.getString("startDateTime"));
                endTime.add(arrObject.getString("endDateTime"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        coder = new Geocoder(this);



        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


    }


    /* @Override
    public void onResume() {
        super.onResume();

        try {

            addresses = bundle.getString("addresses");



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
                startTime.add(arrObject.getString("startDateTime"));
                endTime.add(arrObject.getString("endDateTime"));

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
    public void onPause()
    {
        super.onPause();
    }*/


    @Override
    public void onMapReady(final GoogleMap map) {

        for(j=0;j<latitude.size();j++) {

            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(new LatLng(latitude.get(j), longitude.get(j)));

            CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);

            map.moveCamera(center);
            map.animateCamera(zoom);

            map.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude.get(j), longitude.get(j))).title(address.get(j)).flat(false).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));



            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    View v = getLayoutInflater().inflate(R.layout.google_map_marker, null);


                    TextView titleMarker = (TextView) v.findViewById(R.id.markerInfoTitle);


                    TextView timeMarker = (TextView) v.findViewById(R.id.markerInfoTime);

                    TextView rateMarker = (TextView) v.findViewById(R.id.markerInfoRate);


                    String addressMarker = marker.getTitle().toString();

                    for(i=0;i<address.size();i++)
                    {
                        if(addressMarker.equals(address.get(i)))
                            break;
                    }

                    String ISO_start_time = startTime.get(i).substring(11,19);
                    String start_time_array[] = ISO_start_time.split(":");



                    if (Integer.parseInt(start_time_array[0]) >  12) {
                        int time = Integer.parseInt(start_time_array[0]) - 12;

                        start_time = Integer.toString(time)+" pm";

                    } else if(Integer.parseInt(start_time_array[0]) <  12) {
                        int time = Integer.parseInt(start_time_array[0]);

                        start_time = Integer.toString(time)+" am";

                    } else {
                        int time = Integer.parseInt(start_time_array[0]);

                        start_time = Integer.toString(time)+" pm";

                    }


                    String ISO_end_time = endTime.get(i).substring(11,19);
                    String end_time_array[] = ISO_end_time.split(":");

                    //Log.d("end time  ",ISO_end_time);

                    if (Integer.parseInt(end_time_array[0]) >  12) {
                        int time = Integer.parseInt(end_time_array[0]) - 12;

                        end_time = Integer.toString(time)+" pm";

                    } else if(Integer.parseInt(end_time_array[0]) <  12) {
                        int time = Integer.parseInt(end_time_array[0]);

                        end_time = Integer.toString(time)+" am";

                    } else {
                        int time = Integer.parseInt(end_time_array[0]);

                        end_time = Integer.toString(time)+" pm";

                    }


                    titleMarker.setText(address.get(i));


                    timeMarker.setText(start_time+" - "+end_time);

                    rateMarker.setText("$"+rate.get(i)+" / hour");


                    return v;

                }
            });


        }


        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                /*if(marker.getTitle().equals("10601 Larry Way, Cupertino, CA 95014")) // if marker source is clicked
                    Toast.makeText(AdvancedSearchMap.this, marker.getTitle(), Toast.LENGTH_SHORT).show();*/
                String markerTitle = marker.getTitle();

                for(i=0;i<address.size();i++)
                {
                    if(markerTitle.equals(address.get(i)))
                        break;
                }


                Intent newIntent = new Intent(getBaseContext(), ParkingSpaceDetails.class).putExtra("address",address.get(i))
                        .putExtra("time",start_time+" - "+end_time).putExtra("rate",rate.get(i));



                startActivity(newIntent);


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
