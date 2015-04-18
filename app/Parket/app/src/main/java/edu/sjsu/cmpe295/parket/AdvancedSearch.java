package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toolbar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.sjsu.cmpe295.parket.http.RestClient;
import edu.sjsu.cmpe295.parket.model.ParkingSpace;
import edu.sjsu.cmpe295.parket.model.request.SearchRequest;
import edu.sjsu.cmpe295.parket.model.response.SearchResponse;
import edu.sjsu.cmpe295.parket.util.AuthUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class AdvancedSearch extends Activity implements View.OnClickListener {
    MenuInflater inflater;

    private EditText date, startTime, endTime, maxRate, range;
    private Calendar calendar;
    private int day, month, year, hour, minute, starttime_seconds, endtime_seconds;
    private EditText address;
    private EditText ste;
    String address_POST, date_POST, startTime_POST, endTime_POST, maxRate_POST;
    double range_POST;
    final int DATE_DIALOG_ID = 990;
    final int STARTTIME_DIALOG_ID = 991;
    final int ENDTIME_DIALOG_ID = 992;
    String serverResponse;
    String UTC_startTime, UTC_endTime;
    AuthUtil authUtil;
    Address location;
    double lat, lon;
    List<ParkingSpace> ServerResponse;
    int count;

    public AdvancedSearch() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        authUtil = new AuthUtil(this);



        // Set up main toolbar
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar_parkingSpacesAroundMe);
        mainToolbar.setTitle(getResources().getString(R.string.title_activity_advanced_search));
        mainToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mainToolbar.setNavigationIcon(R.drawable.ic_back);
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set up date and time toolbars
        Toolbar iconToolbar = (Toolbar) findViewById(R.id.toolbar_parkingSpacesAroundMe_icon);
        iconToolbar.setNavigationIcon(R.drawable.ic_time);

        Toolbar dateToolbar = (Toolbar) findViewById(R.id.toolbar_parkingSpacesAroundMe_date);
        dateToolbar.setTitle("Date");
        dateToolbar.setTitleTextAppearance(this, R.style.ToolbarSmallTextStyle);
//        dateToolbar.setNavigationIcon(R.drawable.ic_date);

        Toolbar startTimeToolbar = (Toolbar) findViewById(R.id.toolbar_parkingSpacesAroundMe_startTime);
        startTimeToolbar.setTitle("Start Time");
        startTimeToolbar.setTitleTextAppearance(this, R.style.ToolbarSmallTextStyle);
//        startTimeToolbar.setNavigationIcon(R.drawable.ic_time);

        Toolbar endTimeToolbar = (Toolbar) findViewById(R.id.toolbar_parkingSpacesAroundMe_endTime);
        endTimeToolbar.setTitle("End Time");
        endTimeToolbar.setTitleTextAppearance(this, R.style.ToolbarSmallTextStyle);
//        endTimeToolbar.setNavigationIcon(R.drawable.ic_time);

        date = (EditText) findViewById(R.id.date);
        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        date.setOnClickListener(this);
        startTime = (EditText) findViewById(R.id.startTime);
        endTime = (EditText) findViewById(R.id.endTime);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        Button searchButton = (Button) findViewById(R.id.searchButton);

        final Geocoder coder = new Geocoder(this);

        final DBHandler db = new DBHandler(this);

        searchButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                address = (EditText) findViewById(R.id.address);
                address_POST = address.getText().toString();

                date = (EditText) findViewById(R.id.date);
                date_POST = date.getText().toString();

                startTime = (EditText) findViewById(R.id.startTime);
                startTime_POST = startTime.getText().toString();

                endTime = (EditText) findViewById(R.id.endTime);
                endTime_POST = endTime.getText().toString();

                maxRate = (EditText) findViewById(R.id.maxRate);
                maxRate_POST = maxRate.getText().toString();

                range = (EditText) findViewById(R.id.range);
                range_POST = Double.parseDouble(range.getText().toString());

                if(range_POST==' ') {
                    range_POST = 0.0;

                }




                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("https://parketb.com/search");


               /* try {
                    // Add your data
                    *//*List<NameValuePair> postData = new ArrayList<NameValuePair>(6);
                    postData.add(new BasicNameValuePair("action", "searchAroundAddress"));
                    postData.add(new BasicNameValuePair("address", address_POST));
                    postData.add(new BasicNameValuePair("queryStartDateTime", startTime_POST));
                    postData.add(new BasicNameValuePair("queryStopDateTime", endTime_POST));
                    postData.add(new BasicNameValuePair("maxRate", maxRate_POST));
                    postData.add(new BasicNameValuePair("range", range_POST));
                    postData.add(new BasicNameValuePair("idToken",authUtil.getIdToken()));
                    httppost.setEntity(new UrlEncodedFormEntity(postData));


                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);

                    HttpEntity entity = response.getEntity();
                    serverResponse = EntityUtils.toString(entity);*//*


                    Log.v("Http Post Response:", serverResponse);

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }*/
                try {
                    List<Address> address = coder.getFromLocationName(address_POST, 1);
                    location = address.get(0);
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                }
                catch(Exception e) {

                }
               final Bundle bundle = new Bundle();
               SearchRequest sr = new SearchRequest(authUtil.getIdToken(),"searchAroundAddress",lat, lon,UTC_startTime,UTC_endTime, range_POST);
               RestClient.getInstance().search(sr, new Callback<SearchResponse>() {
                   @Override
                   public void success(SearchResponse searchResponse, Response response) {
                       //ServerResponse = searchResponse.getParkingSpaces();

                       db.insertSearchResponse(searchResponse);

                       count = searchResponse.getCount();

                   }

                   @Override
                   public void failure(RetrofitError error) {

                   }
               });

              /*  for(int i=0;i<count;i++)
                {
                    db.insertSearchResponse(ServerResponse.get(i));
                }*/

                Intent i = new Intent(getBaseContext(), AdvancedSearchMap.class);
                //i.putExtras(bundle);
                startActivity(i);
           }

        });
    }

    private void readStream(InputStream in) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onClick(View v) {
        address = (EditText) findViewById(R.id.address);
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(address.getWindowToken(), 0);
        String clickedComponentID = v.getResources().getResourceName(v.getId());
        Log.v(clickedComponentID, "error head going");
        if (clickedComponentID.equals("edu.sjsu.cmpe295.parket:id/date"))
            showDialog(DATE_DIALOG_ID);
        else if (clickedComponentID.equals("edu.sjsu.cmpe295.parket:id/startTime"))
            showDialog(STARTTIME_DIALOG_ID);
        else if (clickedComponentID.equals("edu.sjsu.cmpe295.parket:id/endTime"))
            showDialog(ENDTIME_DIALOG_ID);

    }

    @Deprecated
    protected Dialog onCreateDialog(int id) {
        if (id == 990)
            return new DatePickerDialog(this, datePickerListener, year, month, day);
        else if (id == 991)
            return new TimePickerDialog(this, startTimePickerListener, hour, minute, false);
        else if (id == 992)
            return new TimePickerDialog(this, endTimePickerListener, hour, minute, false);
        return null;

    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            date.setText((selectedMonth + 1) + " / " + selectedDay + " / "
                    + selectedYear);
            //Log.d("date",date.toString());   2015-03-23T18:00:00Z-08:00
            UTC_startTime = Integer.toString(selectedYear)+"-"+Integer.toString(selectedMonth)+"-"
                    +Integer.toString(selectedDay)+"T";

            UTC_endTime = Integer.toString(selectedYear)+"-"+Integer.toString(selectedMonth)+"-"
                    +Integer.toString(selectedDay)+"T";
        }
    };

    private TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            starttime_seconds = Calendar.getInstance().get(Calendar.SECOND);
            int hour;
            String am_pm;
            if (hourOfDay > 12) {
                hour = hourOfDay - 12;
                am_pm = "PM";
            } else if (hourOfDay < 12) {
                hour = hourOfDay;
                am_pm = "AM";
            } else {
                hour = hourOfDay;
                am_pm = "PM";
            }
            int length = (int) (Math.log10(minute) + 1);
            if (length == 1)
                startTime.setText(hour + " : " + "0" + minute + am_pm);
            else
                startTime.setText(hour + " : " + minute + am_pm);
            //2015-03-23T18:00:00Z-08:00
            UTC_startTime = UTC_startTime + Integer.toString(hourOfDay)+":"+Integer.toString(minute)+":"+
                    Integer.toString(starttime_seconds)+"Z-08:00";
    }

    };

    private TimePickerDialog.OnTimeSetListener endTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            int hour;
            String am_pm;
            if (hourOfDay > 12) {
                hour = hourOfDay - 12;
                am_pm = "PM";
            } else if (hourOfDay < 12) {
                hour = hourOfDay;
                am_pm = "AM";
            } else {
                hour = hourOfDay;
                am_pm = "PM";
            }
            endtime_seconds = Calendar.getInstance().get(Calendar.SECOND);
            int length = (int) (Math.log10(minute) + 1);
            if (length == 1)
                endTime.setText(hour + " : " + "0" + minute + " " + am_pm);
            else
                endTime.setText(hour + " : " + minute + " " + am_pm);
            //2015-03-23T18:00:00Z-08:00
            UTC_endTime = UTC_endTime + Integer.toString(hourOfDay)+":"+Integer.toString(minute)+":"+
                    Integer.toString(endtime_seconds)+"Z-08:00";
        }
    };
}
