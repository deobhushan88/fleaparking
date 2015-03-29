package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import javax.net.ssl.HttpsURLConnection;

import static edu.sjsu.cmpe295.parket.R.layout.activity_advanced_search;


public class AdvancedSearch extends ActionBarActivity implements View.OnClickListener {
    MenuInflater inflater;

    private EditText date, startTime, endTime, maxRate, range;
    private Calendar calendar;
    private int day, month, year, hour, minute;
    private EditText address;
        private EditText ste;
    String address_POST,date_POST,startTime_POST,endTime_POST,maxRate_POST,range_POST;

    final int DATE_DIALOG_ID = 990;
    final int STARTTIME_DIALOG_ID = 991;
    final int ENDTIME_DIALOG_ID = 992;
    String serverResponse;

    public AdvancedSearch() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.
        ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        setContentView(R.layout.activity_advanced_search);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


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



        // just for setting the app up

        Button searchButton = (Button) findViewById(R.id.searchButton);

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
                range_POST = range.getText().toString();

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("https://parketb.com/search");


                try {
                    // Add your data
                    List<NameValuePair> postData = new ArrayList<NameValuePair>(6);
                    postData.add(new BasicNameValuePair("action", "searchAroundAddress"));
                    postData.add(new BasicNameValuePair("address", address_POST));
                    postData.add(new BasicNameValuePair("queryStartDateTime", startTime_POST));
                    postData.add(new BasicNameValuePair("queryStopDateTime", endTime_POST));
                    postData.add(new BasicNameValuePair("maxRate", maxRate_POST));
                    postData.add(new BasicNameValuePair("range", range_POST));
                    httppost.setEntity(new UrlEncodedFormEntity(postData));


                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);

                    HttpEntity entity = response.getEntity();
                    serverResponse = EntityUtils.toString(entity);


                    Log.v("Http Post Response:", serverResponse);

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }

                Bundle bundle = new Bundle();
                bundle.putString("addresses",serverResponse);



                Intent i = new Intent(getBaseContext(), AdvancedSearchMap.class);

                i.putExtras(bundle);

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
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(address.getWindowToken(), 0);

            String clickedComponentID = v.getResources().getResourceName(v.getId());
            Log.v(clickedComponentID,"error head going");



            if(clickedComponentID.equals("edu.sjsu.cmpe295.parket:id/date"))
                showDialog(DATE_DIALOG_ID);
            else if(clickedComponentID.equals("edu.sjsu.cmpe295.parket:id/startTime"))
                showDialog(STARTTIME_DIALOG_ID);
            else if(clickedComponentID.equals("edu.sjsu.cmpe295.parket:id/endTime"))
                showDialog(ENDTIME_DIALOG_ID);

    }


    @Deprecated
    protected Dialog onCreateDialog(int id)
    {
        if(id==990)
            return new DatePickerDialog(this, datePickerListener, year, month, day);
        else if(id==991)
            return new TimePickerDialog(this, startTimePickerListener, hour, minute, false);
        else if(id==992)
            return new TimePickerDialog(this, endTimePickerListener, hour, minute, false);


        return null;

    }


    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            date.setText(selectedDay + " / " + (selectedMonth + 1) + " / "
                    + selectedYear);
        }
    };

    private TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
           int hour;
           String am_pm;

            if (hourOfDay >  12) {
                hour = hourOfDay - 12;
                am_pm = "PM";
            } else if(hourOfDay <  12) {
                hour = hourOfDay;
                am_pm = "AM";
            } else {
                hour = hourOfDay;
                am_pm = "PM";
            }


            int length = (int)(Math.log10(minute)+1);

            if(length==1)
                startTime.setText(hour + " : " + "0"+minute + " " + am_pm);
            else
                startTime.setText(hour + " : " + minute + " " + am_pm);


        }
    };


    private TimePickerDialog.OnTimeSetListener endTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            int hour;
            String am_pm;

            if (hourOfDay >  12) {
                hour = hourOfDay - 12;
                am_pm = "PM";
            } else if(hourOfDay <  12) {
                hour = hourOfDay;
                am_pm = "AM";
            } else {
                hour = hourOfDay;
                am_pm = "PM";
            }


            int length = (int)(Math.log10(minute)+1);

            if(length==1)
                endTime.setText(hour + " : " + "0"+minute + " " + am_pm);
            else
                endTime.setText(hour + " : " + minute + " " + am_pm);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_advanced_search, menu);

        return super.onCreateOptionsMenu(menu);
    }


   @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

}
