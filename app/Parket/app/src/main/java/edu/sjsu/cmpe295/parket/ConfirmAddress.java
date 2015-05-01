package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import edu.sjsu.cmpe295.parket.http.RestClient;
import edu.sjsu.cmpe295.parket.model.request.AddParkingSpaceRequest;
import edu.sjsu.cmpe295.parket.model.response.AddParkingSpaceResponse;
import edu.sjsu.cmpe295.parket.util.AuthUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ConfirmAddress extends Activity implements OnMapReadyCallback, VerifyAddressButton.VerifyListener {


    String address;
    List<Address> parkingAddress;
    Address location;
    double lat, lon;
    AuthUtil authUtil;
    Bundle bundle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_address);

        authUtil = new AuthUtil(this);

        //Adding a toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_confirmAddress);
        toolbar.setTitle(getResources().getString(R.string.title_activity_confirm_address));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        bundle = getIntent().getExtras();
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

    // Verify Listener method from VerifyAddressButton fragment
    @Override
    public void onVerified() {

        AddParkingSpaceRequest req = new AddParkingSpaceRequest(authUtil.getIdToken(),
                bundle.getString("parkingSpaceLabel"),
                bundle.getString("parkingSpaceDescription"),
                bundle.getString("addrLine1"),
                bundle.getString("addrLine2"),
                bundle.getString("city"),
                bundle.getString("state"),
                bundle.getString("country"),
                bundle.getInt("zip", 0),
                bundle.getBoolean("disabledParkingFlag", false),
                Base64.encodeToString(bundle.getByteArray("parkingSpacePhoto"), Base64.DEFAULT),
                lat, lon);
        RestClient.getInstance().addParkingSpace(req, new Callback<AddParkingSpaceResponse>() {
            @Override
            public void success(AddParkingSpaceResponse addParkingSpaceResponse, Response response) {
                Intent i = new Intent(getApplicationContext(), RentParkingSpace.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
