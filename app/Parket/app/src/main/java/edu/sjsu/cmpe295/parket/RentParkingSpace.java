package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import edu.sjsu.cmpe295.parket.http.RestClient;
import edu.sjsu.cmpe295.parket.model.UserParkingSpace;
import edu.sjsu.cmpe295.parket.model.request.IdTokenParameterRequest;
import edu.sjsu.cmpe295.parket.model.request.ParkingSpaceAvailabilityRequest;
import edu.sjsu.cmpe295.parket.model.response.ParkingSpaceAvailabilityResponse;
import edu.sjsu.cmpe295.parket.model.response.QueryParkingSpacesResponse;
import edu.sjsu.cmpe295.parket.util.AuthUtil;
import edu.sjsu.cmpe295.parket.util.DBHandler;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RentParkingSpace extends Activity {

    AuthUtil authUtil;
    DBHandler dbHandler;


    ListView displayList;
    RentParkingSpaceAdapter adapter;
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> value = new ArrayList<>();
    ArrayList<String> parkingSpaceId = new ArrayList<>();
    ArrayList availabilityStatus = new ArrayList();

    IdTokenParameterRequest idTokenParameterRequest;
    ParkingSpaceAvailabilityRequest parkingSpaceAvailabilityRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rent_parking_space_listview);

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_rentParkingSpace);
        toolbar.setTitle(getResources().getString(R.string.title_activity_rent_parking_space));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.inflateMenu(R.menu.toolbar_rent_parking_space);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ShowParkingSpacesAroundMe.class);
                startActivity(i);

            }

        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(getApplicationContext(), AddParkingSpace.class);
                startActivity(i);
                return true;
            }
        });

        authUtil = new AuthUtil(this);
        dbHandler = new DBHandler(this);

        // Set list view adapter
        adapter = new RentParkingSpaceAdapter(getApplicationContext(),title,value,parkingSpaceId, availabilityStatus);
        displayList = (ListView) findViewById(R.id.listViewRentParkingSpace);
        displayList.setAdapter(adapter);

        // Get parking spaces from web service
        idTokenParameterRequest = new IdTokenParameterRequest(authUtil.getIdToken());
        RestClient.getInstance().queryParkingSpaces(idTokenParameterRequest, new Callback<QueryParkingSpacesResponse>() {

            @Override
            public void success(QueryParkingSpacesResponse queryParkingSpacesResponse, Response response) {
                // Save parking spaces to DB
                dbHandler.setParkingSpaceResponse(queryParkingSpacesResponse);

                // Populate list view arrays and update the adapter
                int count = queryParkingSpacesResponse.getCount();
                List<UserParkingSpace> userPSList = queryParkingSpacesResponse.getParkingSpaces();
                title.clear();
                value.clear();
                parkingSpaceId.clear();
                availabilityStatus.clear();
                for (int i = 0; i < count; i++) {
                    title.add(userPSList.get(i).getParkingSpaceLabel());
                    value.add(userPSList.get(i).getParkingSpaceAddress());
                    parkingSpaceId.add(userPSList.get(i).getParkingSpaceId());
                    availabilityStatus.add(userPSList.get(i).isParkingSpaceAvailabilityFlag());
                }
                // Update the list view
                adapter.updateList(title, value, parkingSpaceId, availabilityStatus);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

       /* final Switch switchButton = (Switch) findViewById(R.id.switchBtn);
        switchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (switchButton.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Button Checked",
                            Toast.LENGTH_LONG).show();
                    //Button is ON
                    // Do Something
                } else {
                    //Button is OFF
                    // Do Something
                }
            }
        });*/
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("RentParkingSpace", "onNewIntent");
        RestClient.getInstance().queryParkingSpaces(idTokenParameterRequest, new Callback<QueryParkingSpacesResponse>() {

            @Override
            public void success(QueryParkingSpacesResponse queryParkingSpacesResponse, Response response) {
                // Save parking spaces to DB
                dbHandler.setParkingSpaceResponse(queryParkingSpacesResponse);

                // Populate list view arrays and update the adapter
                int count = queryParkingSpacesResponse.getCount();
                List<UserParkingSpace> userPSList = queryParkingSpacesResponse.getParkingSpaces();
                title.clear();
                value.clear();
                parkingSpaceId.clear();
                availabilityStatus.clear();
                for (int i = 0; i < count; i++) {
                    title.add(userPSList.get(i).getParkingSpaceLabel());
                    value.add(userPSList.get(i).getParkingSpaceAddress());
                    parkingSpaceId.add(userPSList.get(i).getParkingSpaceId());
                    availabilityStatus.add(userPSList.get(i).isParkingSpaceAvailabilityFlag());
                }
                // Update the list view
                adapter.updateList(title, value, parkingSpaceId, availabilityStatus);

               /* dbHandler = new DBHandler(getApplicationContext());
                dbHandler.setParkingSpaceResponse(queryParkingSpacesResponse);
                displayList = (ListView) findViewById(R.id.listViewRentParkingSpace);
                QueryParkingSpacesResponse parkingSpacesResponseDB = dbHandler.getParkingSpaceResponse();
                int count = parkingSpacesResponseDB.getCount();
                List<UserParkingSpace> userPSListDB = parkingSpacesResponseDB.getParkingSpaces();
                for(int i=0;i<count;i++)
                {
                    title.add(userPSListDB.get(i).getParkingSpaceLabel());
                    value.add(userPSListDB.get(i).getParkingSpaceAddress());
                }
                displayList.setAdapter(new RentParkingSpaceAdapter(getApplicationContext(),title,value,availabilityStatus));
           */
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });


    }



    private class RentParkingSpaceAdapter extends BaseAdapter {
        Context context;
        ArrayList<String> title;
        ArrayList<String> value;
        ArrayList<String> parkingSpaceId;
        ArrayList availabilityStatus;
        private LayoutInflater inflater = null;
        boolean noResults = false;

        public RentParkingSpaceAdapter(Context context,
                                       ArrayList<String> data_title, ArrayList<String> data_value, ArrayList<String> data_parkingSpaceId, ArrayList data_availabilityStatus) {
            this.context = context;
            this.title = data_title;
            this.value = data_value;
            this.parkingSpaceId = data_parkingSpaceId;
            this.availabilityStatus = data_availabilityStatus;
            this.inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {

            if(title.size()==0) {
                noResults = true;
                return 1;
            }
            else {
                noResults = false;
                return title.size();
            }

        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(noResults)
            {
                convertView = inflater.inflate(R.layout.listitem_rent_parking_space_empty, null);
                ((TextView) convertView.findViewById(R.id.list_item_rent_parking_space_empty_placeholder)).setText("You have no parking spaces added");
            }
            else {
                convertView = inflater.inflate(R.layout.listitem_rent_parking_space, null);
                // Set up text view
                ((TextView) convertView.findViewById(R.id.list_item_rent_parking_space_title)).setText(title.get(position));
                ((TextView) convertView.findViewById(R.id.list_item_rent_parking_space_value)).setText(value.get(position));
                Switch s = (Switch) convertView.findViewById(R.id.switchBtn);
                s.setTag(parkingSpaceId.get(position));
                s.setChecked((boolean) availabilityStatus.get(position));

                s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked)
                        {

                            Intent i = new Intent(getApplicationContext(), EnableParkingSpace.class);
                            i.putExtra("parkingSpaceId",buttonView.getTag().toString());
                            startActivity(i);
                        }
                        else
                        {
                            String parkingId = buttonView.getTag().toString();
                            authUtil = new AuthUtil(getApplicationContext());
                            parkingSpaceAvailabilityRequest = new ParkingSpaceAvailabilityRequest(authUtil.getIdToken(), "parkingSpaceDisable", false, "","",0.0);
                            RestClient.getInstance().enableDisableParkingSpace(parkingId, parkingSpaceAvailabilityRequest, new Callback<ParkingSpaceAvailabilityResponse>() {
                                @Override
                                public void success(ParkingSpaceAvailabilityResponse parkingSpaceAvailabilityResponse, Response response) {
                                    Toast.makeText(getApplicationContext(),
                                            "Parking Space has been disabled", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Toast.makeText(getApplicationContext(),
                                            "Could not connect to web service", Toast.LENGTH_LONG).show();

                                }
                            });

                        }
                    }
                });
            }
            return convertView;
        }

        // To refresh the list view
        public void updateList(ArrayList<String> title, ArrayList<String> value, ArrayList<String> parkingSpaceId,ArrayList availabilityStatus) {
            this.title = title;
            this.value = value;
            this.parkingSpaceId = parkingSpaceId;
            this.availabilityStatus = availabilityStatus;
            notifyDataSetChanged();
        }
    }
}
