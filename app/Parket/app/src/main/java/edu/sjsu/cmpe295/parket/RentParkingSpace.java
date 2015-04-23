package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.sjsu.cmpe295.parket.http.RestClient;
import edu.sjsu.cmpe295.parket.model.response.QueryParkingSpacesResponse;
import edu.sjsu.cmpe295.parket.model.response.SearchResponse;
import edu.sjsu.cmpe295.parket.util.AuthUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RentParkingSpace extends Activity {

    AuthUtil authUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rent_parking_space_listview);
        authUtil = new AuthUtil(this);

    }
}
