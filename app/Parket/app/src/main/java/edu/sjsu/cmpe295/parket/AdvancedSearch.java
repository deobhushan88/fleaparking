package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import edu.sjsu.cmpe295.parket.util.AuthUtil;
import edu.sjsu.cmpe295.parket.util.DateUtil;


public class AdvancedSearch extends Activity {
    // Tags
    private static final String TAG_DATE = "dateButton";
    private static final String TAG_STARTTIME = "startTimeButton";
    private static final String TAG_ENDTIME = "endTimeButton";
    private static final String TAG = "AdvancedSearch";

    AuthUtil authUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        authUtil = new AuthUtil(this);

        // Set up main toolbar
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar_with_only_back_navigation);
        mainToolbar.setTitle(getResources().getString(R.string.title_activity_advanced_search));
        mainToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mainToolbar.setNavigationIcon(R.drawable.ic_back);
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return back to ParkingSpacesAroundMe
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // Get ref to the EditText views
        final EditText addressText = (EditText) findViewById(R.id.advancedSearchAddress);
        final EditText rangeText = (EditText) findViewById(R.id.advancedSearchRange);

        // Set up button OnClick listeners
        final Button dateButton = (Button) findViewById(R.id.advancedSearchDateButton);
        final Button startTimeButton = (Button) findViewById(R.id.advancedSearchStartTimeButton);
        final Button endTimeButton = (Button) findViewById(R.id.advancedSearchEndTimeButton);
        Button searchButton = (Button) findViewById(R.id.advancedSearchSearchButton);

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
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateButtonText = dateButton.getText().toString().trim();
                String startTimeButtonText = startTimeButton.getText().toString().trim();
                String endTimeButtonText = endTimeButton.getText().toString().trim();

                // Check whether all values have been set
                if ((addressText.getText().toString().trim().length() == 0)
                        || (rangeText.getText().toString().trim().length() == 0)
                        || dateButtonText.equals(getResources()
                                .getString(R.string.advancedSearchDateButtonText))
                        || startTimeButtonText
                        .equals(getResources()
                                .getString(R.string.advancedSearchStartTimeButtonText))
                        || endTimeButtonText
                        .equals(getResources()
                                .getString(R.string.advancedSearchEndTimeButtonText))) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter all search parameters", Toast.LENGTH_LONG).show();
                }
                // Check that end time does not overflow into the next day
                else if (Integer.parseInt(startTimeButtonText.subSequence(0,2).toString()) >
                        Integer.parseInt(endTimeButtonText.subSequence(0,2).toString())) {
                    Toast.makeText(getApplicationContext(),
                            "Start and End time must be on same date", Toast.LENGTH_LONG).show();
                }
                // Check address and then pass the formatted values back to ParkingSpacesAroundMe activity
                else {
                    // Create the intent to pass back data
                    Intent data = new Intent();

                    // First geocode the address
                    // TODO: geocoding is a blocking network operation, do in AsyncTask
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    ArrayList<Address> geocoded = new ArrayList<Address>(1);
                    try {
                        geocoded = (ArrayList<Address>) geocoder
                                .getFromLocationName(addressText.getText().toString(), 1);
                    } catch (IOException e) {
                        Log.e(TAG, "Exception while geocoding user entered address", e);
                    }

                    if (geocoded.get(0) == null) {
                        Toast.makeText(getApplicationContext(),
                                "Please enter a valid address", Toast.LENGTH_LONG).show();
                    }
                    else {
                        data.putExtra("userLat", geocoded.get(0).getLatitude());
                        data.putExtra("userLong", geocoded.get(0).getLongitude());

                        // Add the range to intent
                        data.putExtra("range", Double.valueOf(rangeText.getText().toString().trim()));

                        // Format the dates and add to intent
                        DateUtil dateUtil = new DateUtil();
                        String offset = dateUtil.getCurrentTimezoneOffset();
                        StringBuilder queryStartDateTime = new StringBuilder("");
                        queryStartDateTime.append(dateButtonText)
                                .append("T")
                                .append(startTimeButtonText)
                                .append(":00")
                                .append(offset);
                        data.putExtra("queryStartDateTime", queryStartDateTime.toString());

                        StringBuilder queryStopDateTime = new StringBuilder("");
                        queryStopDateTime.append(dateButtonText)
                                .append("T")
                                .append(endTimeButtonText)
                                .append(":00")
                                .append(offset);
                        data.putExtra("queryStopDateTime", queryStopDateTime.toString());

                        // Everything is set, return back to ParkingSpacesAroundMe
                        setResult(RESULT_OK, data);
                        finish();
                    }
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
            ((Button)getActivity().findViewById(R.id.advancedSearchDateButton))
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
            ((Button)getActivity().findViewById(R.id.advancedSearchStartTimeButton))
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
            ((Button)getActivity().findViewById(R.id.advancedSearchEndTimeButton))
                    .setText(time);
        }
    }

}
