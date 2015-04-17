package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import edu.sjsu.cmpe295.parket.http.RestClient;
import edu.sjsu.cmpe295.parket.model.request.SearchRequest;
import edu.sjsu.cmpe295.parket.model.response.SearchResponse;
import edu.sjsu.cmpe295.parket.util.AuthUtil;
import edu.sjsu.cmpe295.parket.util.DateUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ShowParkingSpacesAroundMe extends Activity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    // Sidebar Related
    String[] menu;
    int[] img;
    DrawerLayout dLayout;
    ListView dList;
    boolean toolbarNavOpenFlag = false;

    // Maps Related
    GoogleApiClient mGoogleApiClient;
    Location mUserLocation;
    LocationRequest mLocationRequest;
    boolean mRequestingLocationUpdates = true;
    MapFragment mMapFragment;

    AuthUtil authUtil;

    private final String TAG = "ShowParkingAroundMe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_parking_around_me);

        authUtil = new AuthUtil(this);

        // Set up Google Maps
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        // Set up periodic location request object
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set up navigation drawer
        menu = new String[]{getResources().getString(R.string.text_drawer_find_parking),
                getResources().getString(R.string.text_drawer_rent_out),
                getResources().getString(R.string.text_drawer_settings),
                getResources().getString(R.string.text_drawer_help)};
        img = new int[]{R.drawable.ic_drawer_search,
                R.drawable.ic_drawer_rent_out_parking,
                R.drawable.ic_drawer_settings,
                R.drawable.ic_drawer_help};

        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dList = (ListView) findViewById(R.id.left_drawer);

        dList.setAdapter(new ShowParkingSpacesAroundMeAdapter(this, menu, img));
        dList.setSelector(android.R.color.holo_orange_light);
        dList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                dLayout.closeDrawers();
                toolbarNavOpenFlag = false;

                switch(position) {
                    case 0: {
                        dLayout.closeDrawers();
                        break;
                    }
                    case 1: {
                        dLayout.closeDrawers();
                        Intent i = new Intent(getApplicationContext(), AddParkingSpace.class);
                        startActivity(i);
                        break;
                    }
                    case 2: {
                        dLayout.closeDrawers();
                        Intent i = new Intent(getApplicationContext(), AddParkingSpace.class);
                        startActivity(i);
                        break;
                    }
                    case 3: {
                        dLayout.closeDrawers();
                        Intent i = new Intent(getApplicationContext(), ParkingSpaceDetails.class);
                        startActivity(i);
                        break;
                    }
                }
            }
        });

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_parkingSpacesAroundMe);
        toolbar.setTitle(getResources().getString(R.string.title_activity_show_parking_space_around_me));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        toolbar.inflateMenu(R.menu.toolbar_show_parking_space_around_me);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:{
                        Intent i = new Intent(getApplicationContext(), AdvancedSearch.class);
                        startActivity(i);
                        return true;
                    }
                    case R.id.action_refresh:{
                        // TODO
                        return true;
                    }
                }
                return true;
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!toolbarNavOpenFlag) {
                    dLayout.openDrawer(Gravity.LEFT);
                    toolbarNavOpenFlag = true;
                }
                else {
                    dLayout.closeDrawers();
                    toolbarNavOpenFlag = false;
                }
            }
        });

        // Show the fragment (for the map in this case. If we wanted to replace
        // fragments, we could pass the position and switch-case to replace with
        // appropriate fragment)
        displayFragment();

        // Set up the fragment (only map in this case)
        mMapFragment.getMapAsync(this);
    }

    private void displayFragment() {
        mMapFragment = MapFragment.newInstance();
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, mMapFragment)
                .commit();
        dLayout.closeDrawers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop Location Updates
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start Location Updates
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    // Map related callbacks
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Show user location layer
        googleMap.setMyLocationEnabled(true);

        // Continue process to show markers around it
        if (mUserLocation !=null) {
            // Zoom in to user location
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(mUserLocation.getLatitude(),
                    mUserLocation.getLongitude()));
            googleMap.moveCamera(center);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
            googleMap.animateCamera(zoom);

            // Fetch parking spaces from web service
            DateUtil dt = new DateUtil();
            SearchRequest sr = new SearchRequest(authUtil.getIdToken(), "searchAroundMe",
                    mUserLocation.getLatitude(), mUserLocation.getLongitude(), dt.now(),
                    dt.thirtyMinutesFromNow(), 2.0);
            RestClient.getInstance().search(sr, new Callback<SearchResponse>() {
                @Override
                public void success(SearchResponse searchResponse, Response response) {
                    // handle success -> update UI
                }

                @Override
                public void failure(RetrofitError error) {
                    // handle failure
                }
            });
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Save user location
        mUserLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        // Start Location Updates
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        // Kick in process to display markers around current location
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mUserLocation = location;
    }

    // Sidebar list view adapter
    private class ShowParkingSpacesAroundMeAdapter extends BaseAdapter {
        String[] text;
        Context context;
        int[] images;
        private LayoutInflater inflater = null;

        public ShowParkingSpacesAroundMeAdapter(Context context,
                                                String[] data_text, int[] data_images) {
            this.text = data_text;
            this.context = context;
            this.images = data_images;
            this.inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return text.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.list_item_sidebar, null);
            ((ImageView) convertView.findViewById(R.id.list_item_sidebar_icon)).setImageResource(images[position]);
            ((TextView) convertView.findViewById(R.id.list_item_sidebar_text)).setText(text[position]);
            return convertView;
        }
    }

}
