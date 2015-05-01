package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.Calendar;

import edu.sjsu.cmpe295.parket.http.RestClient;
import edu.sjsu.cmpe295.parket.model.request.ParkingSpaceAvailabilityRequest;
import edu.sjsu.cmpe295.parket.model.response.ParkingSpaceAvailabilityResponse;
import edu.sjsu.cmpe295.parket.util.AuthUtil;
import edu.sjsu.cmpe295.parket.util.DateUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class EnableParkingSpace extends Activity {
    // Tags
    private static final String TAG_DATE = "dateButton";
    private static final String TAG_STARTTIME = "startTimeButton";
    private static final String TAG_ENDTIME = "endTimeButton";
    private static final String TAG = "EnableParkingSpace";
    ParkingSpaceAvailabilityRequest parkingSpaceAvailabilityRequest;
    AuthUtil authUtil;
    DateUtil dateUtil;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_parking_space);
        //set up toolbar
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar_enableParkingSpace);
        mainToolbar.setTitle(getResources().getString(R.string.title_activity_enable_parking_space));
        mainToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mainToolbar.setNavigationIcon(R.drawable.ic_back);
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return back to RentParkingSpace
                Intent i = new Intent(getApplicationContext(), RentParkingSpace.class);
                startActivity(i);
            }
        });
        // Set up button OnClick listeners
        final Button dateButton = (Button) findViewById(R.id.enableParkingSpaceDateButton);
        final Button startTimeButton = (Button) findViewById(R.id.enableParkingSpaceStartTimeButton);
        final Button endTimeButton = (Button) findViewById(R.id.enableParkingSpaceEndTimeButton);
        Button okButton = (Button) findViewById(R.id.enableParkingSpaceOkButton);
        final EditText rate = (EditText) findViewById(R.id.enableParkingRate);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), TAG_DATE);
            }
        });
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new StartTimePickerFragment();
                newFragment.show(getFragmentManager(), TAG_STARTTIME);
            }
        });
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new EndTimePickerFragment();
                newFragment.show(getFragmentManager(), TAG_ENDTIME);
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateButtonText = dateButton.getText().toString().trim();
                String startTimeButtonText = startTimeButton.getText().toString().trim();
                String endTimeButtonText = endTimeButton.getText().toString().trim();
                String rateText = rate.getText().toString().trim();
                String startTimeHour = startTimeButtonText.substring(0,2);
                String endTimeHour = endTimeButtonText.substring(0,2);
                String startTimeMinute = startTimeButtonText.substring(3);
                String endTimeMinute = endTimeButtonText.substring(3);

                // Check whether all values have been set
                if ((rate.getText().toString().trim().length()==0)
                        || dateButtonText.equals(getResources()
                        .getString(R.string.enableParkingSpaceDateButtonText))
                        || startTimeButtonText
                        .equals(getResources()
                                .getString(R.string.enableParkingSpaceStartTimeButtonText))
                        || endTimeButtonText
                        .equals(getResources()
                                .getString(R.string.enableParkingSpaceEndTimeButtonText))) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter all search parameters", Toast.LENGTH_LONG).show();
                }
                else if (Integer.parseInt(startTimeHour) > Integer.parseInt(endTimeHour))
                {
                    Toast.makeText(getApplicationContext(),
                            "Start time must be before End time", Toast.LENGTH_LONG).show();
                }
                // Check that end time does not overflow into the next day
                else if (Integer.parseInt(startTimeButtonText.subSequence(0,2).toString()) >
                        Integer.parseInt(endTimeButtonText.subSequence(0,2).toString())) {
                    Toast.makeText(getApplicationContext(),
                            "Start and End time must be on same date", Toast.LENGTH_LONG).show();
                }
                // Check address and then pass the formatted values back to ParkingSpacesAroundMe activity
                else {
                    authUtil = new AuthUtil(getApplicationContext());
                    dateUtil = new DateUtil();
                    Bundle bundle = getIntent().getExtras();
                    String parkingSpaceId = bundle.getString("parkingSpaceId");
                    String[] splitDate = dateButtonText.split("-");
                    String startDateTime = dateButtonText +
                            "T" +
                            dateUtil.pickerTimeToMachineString(Integer.parseInt(startTimeHour),
                                    Integer.parseInt(startTimeMinute)) +
                            ":00" +
                            dateUtil.getCurrentTimezoneOffset();
                    String endDateTime = dateButtonText +
                            "T" +
                            dateUtil.pickerTimeToMachineString(Integer.parseInt(endTimeHour),
                                    Integer.parseInt(endTimeMinute)) +
                            ":00" +
                            dateUtil.getCurrentTimezoneOffset();
                    parkingSpaceAvailabilityRequest = new ParkingSpaceAvailabilityRequest(authUtil.getIdToken(),
                            "parkingSpaceEnable",true,startDateTime,endDateTime,Double.parseDouble(rateText));
                    RestClient.getInstance().enableDisableParkingSpace(parkingSpaceId, parkingSpaceAvailabilityRequest, new Callback<ParkingSpaceAvailabilityResponse>() {
                        @Override
                        public void success(ParkingSpaceAvailabilityResponse parkingSpaceAvailabilityResponse, Response response) {
                            Toast.makeText(getApplicationContext(),
                                    "Parking Space has been made available", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getApplicationContext(),
                                    "Could not connect to web service", Toast.LENGTH_LONG).show();

                        }
                    });
                    Intent i = new Intent(getApplicationContext(), RentParkingSpace.class);
                    startActivity(i);

                }
            }
        });
    }

    // Date and Time pickers

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Use the date selected and set it as the appropriate buttons' text
            DateUtil dateUtil = new DateUtil();
            ((Button)getActivity().findViewById(R.id.enableParkingSpaceDateButton))
                    .setText(dateUtil.pickerDateToMachineString(year, month, day));
        }
    }

    public static class StartTimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker v, int hourOfDay, int minute) {
            DateUtil dateUtil = new DateUtil();
            String time = dateUtil.pickerTimeToMachineString(hourOfDay, minute);
            // Use the time selected and set it as the appropriate buttons' text
            ((Button)getActivity().findViewById(R.id.enableParkingSpaceStartTimeButton))
                    .setText(time);
        }
    }

    public static class EndTimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker v, int hourOfDay, int minute) {
            DateUtil dateUtil = new DateUtil();
            String time = dateUtil.pickerTimeToMachineString(hourOfDay, minute);
            // Use the time selected and set it as the appropriate buttons' text
            ((Button)getActivity().findViewById(R.id.enableParkingSpaceEndTimeButton))
                    .setText(time);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enable_parking_space, menu);
        return true;
    }


}
