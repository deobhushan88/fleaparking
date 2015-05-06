package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
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
import android.widget.Toast;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import edu.sjsu.cmpe295.parket.http.RestClient;
import edu.sjsu.cmpe295.parket.model.ParkingSpace;
import edu.sjsu.cmpe295.parket.model.request.CheckInOutRequest;
import edu.sjsu.cmpe295.parket.model.request.SearchRequest;
import edu.sjsu.cmpe295.parket.model.response.BookParkingSpaceResponse;
import edu.sjsu.cmpe295.parket.model.response.CheckInOutResponse;
import edu.sjsu.cmpe295.parket.model.response.SearchResponse;
import edu.sjsu.cmpe295.parket.util.AuthUtil;
import edu.sjsu.cmpe295.parket.util.DBHandler;
import edu.sjsu.cmpe295.parket.util.DateUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ShowParkingSpacesAroundMe extends Activity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, CheckInFragment.CheckInListener, CheckOutFragment.CheckOutListener{

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
    private GoogleMap cachedMap;
    private static final int LOCATION_UPDATE_INTERVAL = 50000;

    // Utilities
    AuthUtil authUtil;
    DateUtil dateUtil;
    DBHandler dbHandler;
    private final String TAG = "ShowParkingAroundMe";
    private final String TAG_CHECKIN = "checkIn";
    private final String TAG_CHECKOUT = "checkOut";

    // Intent Request code
    private static final int REQ_ADVANCED_SEARCH = 1;
    private static final int REQ_ZXING_QR_CODE_SCANNER = 49374;

    /**
     * Flag to indicate whether this activity is currently displaying results from an
     * advanced search initiated by the user.
     */
    private boolean advancedSearchResultMode = false;

    /**
     * Flag to indicate whether this activity is currently displaying check in or check out
     * fragment
     */
    private boolean checkInCheckOutMode = false;

    // Object to store data given by AdvancedSearch to process
    private SearchRequest advancedSearchRequest = new SearchRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_parking_around_me);

        authUtil = new AuthUtil(this);
        dateUtil = new DateUtil();
        dbHandler = new DBHandler(this);

        // Set up Google Maps
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        // Set up periodic location request object
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_UPDATE_INTERVAL);
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
                        // Switch to find parking spaces around me mode
                        advancedSearchResultMode = false;
                        // Start location updates
                        if (mGoogleApiClient.isConnected()) {
                            LocationServices.FusedLocationApi
                                    .requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                                            (LocationListener) mMapFragment.getActivity());
                        }
                        // Clear Markers
                        cachedMap.clear();
                        // Update map
                        mMapFragment.getMapAsync((OnMapReadyCallback) mMapFragment.getActivity());
                        break;
                    }
                    case 1: {
                        Intent i = new Intent(getApplicationContext(), RentParkingSpace.class);
                        startActivity(i);
                        break;
                    }
                    case 2: {
                        Intent i = new Intent(getApplicationContext(), Settings.class);
                        startActivity(i);
                        break;
                    }
                    case 3: {
                        // TODO: Help Activity
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
                        startActivityForResult(i, REQ_ADVANCED_SEARCH);
                        return true;
                    }
                    case R.id.action_refresh:{
                        // Update the map, the mode is determined by the advancedSearchResultMode flag
                        searchAndDisplay(mMapFragment.getMap());
                        return true;
                    }
                }
                return true;
            }
        });

        // TODO: If in checkInCheckOutMode, then the fragment is overlapping with the sidebar. Need to Fix
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
        if((!advancedSearchResultMode) && (!checkInCheckOutMode)) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi
                        .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if it is QR Code Scanner result
        if (requestCode == REQ_ZXING_QR_CODE_SCANNER) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (scanResult != null) {
                    // Get the scanned code
                    String scannedQrCode = scanResult.getContents();
                    // Compare it with the correct code for this booking's associated parking space
                    final BookParkingSpaceResponse res = dbHandler.getBookingParkingSpaceResponse();
                    boolean isCorrectParkingSpace = scannedQrCode.equals(res.getQrCode());
                    Log.d(TAG, "scannedQrCode : " + scannedQrCode + ", booking qrCode: " + res.getQrCode());
                    // Check in if code matches
                    // TODO: Need to persist the check in and check out fragment, using some flag, or based on database entry
                    if (isCorrectParkingSpace) {
                        CheckInOutRequest req = new CheckInOutRequest(authUtil.getIdToken(), "checkIn");
                        RestClient.getInstance().checkInOutParkingSpace(res.getBookingId(), req,
                                new Callback<CheckInOutResponse>() {
                                    @Override
                                    public void success(CheckInOutResponse checkInOutResponse, Response response) {
                                        // Check in successful, now replace with check out fragment
                                        CheckOutFragment checkOutFragment = new CheckOutFragment();
                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.overlay_checkinout_container, checkOutFragment, TAG_CHECKOUT)
                                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                                .commit();
                                        Toast.makeText(getApplicationContext(),
                                                "Check In successful!", Toast.LENGTH_LONG).show();
                                        // Stop Periodic location updates and and focus on the booked parking space
                                        // so that user can navigate to it
                                        stopLocationUpdates();
                                        cachedMap.clear();
                                        CameraUpdate center = CameraUpdateFactory
                                                .newLatLng(new LatLng(res.getParkingSpaceLat(),
                                                        res.getParkingSpaceLong()));
                                        cachedMap.moveCamera(center);
                                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(18);
                                        cachedMap.animateCamera(zoom);
                                        MarkerOptions m = new MarkerOptions()
                                                .position(new LatLng(res.getParkingSpaceLat(),
                                                        res.getParkingSpaceLong()))
                                                .flat(false)
                                                .title("bookingMarker")
                                                .icon(BitmapDescriptorFactory
                                                        .fromResource(R.drawable.ic_map_pin_accent));
                                        cachedMap.addMarker(m);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {

                                    }
                                });
                    }
                    else {
                        // Wrong parking space
                        Toast.makeText(getApplicationContext(),
                                "You are trying to check in to the wrong parking space", Toast.LENGTH_LONG).show();
                    }
                }
            }
            else if (resultCode == RESULT_CANCELED) {
                // TODO
                // do nothing at the moment
            }
        }

        // Result from Advanced search
        else if (requestCode == REQ_ADVANCED_SEARCH) {
            if (resultCode == RESULT_CANCELED) {
                // do nothing, user returned back without searching from AdvancedSearch
                // TODO: Handle case when this returns from ParkingSpaceDetails ("Tap to Book")
            }
            if (resultCode == RESULT_OK) {
                // Get the data from the intent and update the UI
                advancedSearchResultMode = true;
                // Stop Location Updates
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                // Clear the Map of any markers
                cachedMap.clear();
                // Store the data given by AdvancedSearch
                advancedSearchRequest.setIdToken(authUtil.getIdToken());
                advancedSearchRequest.setAction("searchAroundAddress");
                advancedSearchRequest.setUserLat(data.getDoubleExtra("userLat", 0.0));
                advancedSearchRequest.setUserLong(data.getDoubleExtra("userLong", 0.0));
                advancedSearchRequest.setQueryStartDateTime(data.getStringExtra("queryStartDateTime"));
                advancedSearchRequest.setQueryStopDateTime(data.getStringExtra("queryStopDateTime"));
                advancedSearchRequest.setRange(data.getDoubleExtra("range", 2.0));
                // Kick in the update process
                searchAndDisplay(cachedMap);
            }
        }
    }

    // Map related callbacks
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.cachedMap = googleMap;

        // Clear map of all markers
        googleMap.clear();

        // Show user location layer
        googleMap.setMyLocationEnabled(true);

        // Continue process to show markers around it
        if (mUserLocation !=null) {
            searchAndDisplay(googleMap);
        }
    }

    // Stop Location Updates
    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    // Call to search endpoint, followed by updating UI
    private void searchAndDisplay(final GoogleMap googleMap) {
        SearchRequest sr;
        if (advancedSearchResultMode) {
            sr = advancedSearchRequest;
            // Zoom in to advanced search location
            CameraUpdate center = CameraUpdateFactory
                    .newLatLng(new LatLng(advancedSearchRequest.getUserLat(),
                            advancedSearchRequest.getUserLong()));
            googleMap.moveCamera(center);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
            googleMap.animateCamera(zoom);
        }
        else {
            // Sending the start time as the next hour, and end time as next hour + 1
            // Example: At 6:30 PM, user uses app, then send 7:00 PM - 8:00 PM
            // At 7:00 PM, user uses app, then send 8:00 PM - 9:00 PM (?)
            sr = new SearchRequest(authUtil.getIdToken(), "searchAroundMe",
                    mUserLocation.getLatitude(), mUserLocation.getLongitude(), dateUtil.nextHour(),
                    dateUtil.nextHourPlusOne(), 2.0);

            // Zoom in to user location
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(mUserLocation.getLatitude(),
                    mUserLocation.getLongitude()));
            googleMap.moveCamera(center);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
            googleMap.animateCamera(zoom);
        }

        // Fetch parking spaces from web service with the appropriate SearchRequest object
        RestClient.getInstance().search(sr, new Callback<SearchResponse>() {
            @Override
            public void success(final SearchResponse searchResponse, Response response) {
                // handle success -> update UI
                // Add the advanced search location marker if needed
                if (advancedSearchResultMode) {
                    googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(advancedSearchRequest.getUserLat(),
                                            advancedSearchRequest.getUserLong()))
                                    .flat(false)
                                    .title("advancedSearchLocation")
                                    .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.ic_advanced_search_marker)));
                }

                // add markers from the result
                int i = 0;
                for (ParkingSpace ps : searchResponse.getParkingSpaces()) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(ps.getParkingSpaceLat(),
                                    ps.getParkingSpaceLong()))
                            .flat(false)
                            .title(String.valueOf(i))
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_map_pin_primary_dark)));
                    i++;
                }

                // add infowindow on markers
                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        // When marker is clicked, stop the automatic location updates
                        stopLocationUpdates();
                        View v = getLayoutInflater().inflate(R.layout.google_map_marker, null);
                        TextView tvAddress;
                        TextView tvTime;
                        TextView tvMoney;
                        String dateTime;
                        for (int i = 0; i < searchResponse.getCount(); i++) {
                            // First check if its a booking marker
                            if (marker.getTitle().equals("bookingMarker")) {
                                return getLayoutInflater().inflate(R.layout.booking_map_marker, null);
                            }
                            // Check if its the advanced search location marker
                            if (marker.getTitle().equals("advancedSearchLocation")) {
                                return getLayoutInflater().inflate(R.layout.advanced_search_marker, null);
                            }
                            if (marker.getTitle().equals(String.valueOf(i))) {
                                ParkingSpace parkingSpace = searchResponse
                                        .getParkingSpaces()
                                        .get(i);
                                tvAddress = (TextView) v.findViewById(R.id.markerInfoTitle);
                                tvAddress.setText(parkingSpace.getParkingSpaceAddress());

                                tvTime= (TextView) v.findViewById(R.id.markerInfoTime);
                                dateTime = dateUtil.getDateString(parkingSpace.getStartDateTime())
                                        + ", " + dateUtil.getRangeString(parkingSpace.getStartDateTime(),
                                                    parkingSpace.getEndDateTime());
                                tvTime.setText(dateTime);

                                tvMoney = (TextView) v.findViewById(R.id.markerInfoRate);
                                tvMoney.setText(String.valueOf(parkingSpace.getParkingSpaceRate())
                                        + " per hour");
                            }
                        }
                        return v;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        return null;
                    }
                });

                // set infowindow click listener to open parking space detail activity
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        ParkingSpace parkingSpace = null;
                        // First check if its a booking marker
                        if (marker.getTitle().equals("bookingMarker")) {
                            // do nothing
                            return;
                        }
                        // Check if its the advanced search location marker
                        if (marker.getTitle().equals("advancedSearchLocation")) {
                            // do nothing
                            return;
                        }
                        // Check if the activity is in checkin/checkout mode
                        if (checkInCheckOutMode) {
                            // do nothing
                            return;
                        }
                        // Identify the marker clicked using its title
                        for (int i = 0; i < searchResponse.getCount(); i++) {
                            if (marker.getTitle().equals(String.valueOf(i))) {
                                // Get the corresponding ParkingSpace object
                                parkingSpace = searchResponse.getParkingSpaces().get(i);
                            }
                        }
                        // Send the parkingSpaceId and associated qrCode to the ParkingSpaceDetails activity in the bundle
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putString("parkingSpaceId", parkingSpace.getParkingSpaceId());
                            bundle.putString("qrCode", parkingSpace.getQrCode());
                            bundle.putDouble("parkingSpaceLat", parkingSpace.getParkingSpaceLat());
                            bundle.putDouble("parkingSpaceLong", parkingSpace.getParkingSpaceLong());
                        } catch (NullPointerException e) {
                            Log.e(TAG, "Could not find the corresponding ParkingSpace object", e);
                        }

                        // ParkingSpaceDetails can use the parkingSpaceId to fetch data to populate
                        // its list view
                        Intent i = new Intent(getApplicationContext(), ParkingSpaceDetails.class);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });

                // Save the results to DB
                dbHandler.setSearchResponse(searchResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                // TODO: handle failure
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Save user location
        mUserLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        // Start Location Updates
        if(!advancedSearchResultMode && !checkInCheckOutMode) {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

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
        // Update the map, whenever location is updated
        mMapFragment.getMapAsync(this);
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
            // Set up icon
            ((ImageView) convertView.findViewById(R.id.list_item_sidebar_icon)).setImageResource(images[position]);
            convertView.findViewById(R.id.list_item_sidebar_icon).setScaleX(0.50f);
            convertView.findViewById(R.id.list_item_sidebar_icon).setScaleY(0.50f);

            // Set up text view
            ((TextView) convertView.findViewById(R.id.list_item_sidebar_text)).setText(text[position]);
            return convertView;
        }
    }

    // Methods to handle Check in and Check out
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getStringExtra("type").equals("BookParkingSpace")) {
            // set checkInCheckOutMode
            checkInCheckOutMode = true;
            // Add the check in fragment, passing in the bookingId from the DB
            BookParkingSpaceResponse booking = dbHandler.getBookingParkingSpaceResponse();
            Fragment checkInFragment = new CheckInFragment();
            Bundle bundle = new Bundle();
            bundle.putString("bookingId", booking.getBookingId());
            checkInFragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.overlay_checkinout_container, checkInFragment, TAG_CHECKIN)
                    .commit();
        }
    }

    @Override
    public void onCheckIn() {
        // Scan QR code at parking location with the camera and verify its correct, if correct then proceed
        // Using https://github.com/journeyapps/zxing-android-embedded
        IntentIntegrator qrCodeScanner = new IntentIntegrator(this);
        qrCodeScanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        qrCodeScanner.setPrompt("Scan the QR Code at the Parking Space");
        qrCodeScanner.setResultDisplayDuration(0);
        qrCodeScanner.setCameraId(0);
        qrCodeScanner.initiateScan();
        // Result of scan is processed in onActivityResult, where we perform the subsequent actions
    }

    @Override
    public void onCheckOut() {
        // Fetch the bookingId from the DB
        BookParkingSpaceResponse res = dbHandler.getBookingParkingSpaceResponse();
        // Check out
        // TODO: Need to persist the check in and check out fragment, using some flag, or based on database entry -->
        // --> when onCreate is called when the app is restarted
        CheckInOutRequest req = new CheckInOutRequest(authUtil.getIdToken(), "checkOut");
        RestClient.getInstance().checkInOutParkingSpace(res.getBookingId(), req,
                new Callback<CheckInOutResponse>() {
            @Override
            public void success(CheckInOutResponse checkInOutResponse, Response response) {
                // Checkout successful, Clear the database table and remove the checkout fragment
                dbHandler.clearTable(DBHandler.TABLE_BOOKINGS);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .remove(getFragmentManager().findFragmentByTag(TAG_CHECKOUT))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .commit();
                Toast.makeText(getApplicationContext(),
                        "Check Out Successful", Toast.LENGTH_LONG).show();
                // Clear Map Markers
                cachedMap.clear();
                // Set checkInCheckOutMode to false
                checkInCheckOutMode = false;
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

}
