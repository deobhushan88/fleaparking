package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import edu.sjsu.cmpe295.parket.model.ParkingSpace;
import edu.sjsu.cmpe295.parket.util.DBHandler;
import edu.sjsu.cmpe295.parket.util.DateUtil;


public class ParkingSpaceDetails extends Activity {

    private final static String[] TITLES = new String[]{
            "Address",
            "Availability",
            "Rate",
            "Disabled Parking?",
            "Description"};
    private final static int[] ICONS = new int[]{
            R.drawable.ic_map_pin_primary_dark,
            R.drawable.ic_time,
            R.drawable.ic_drawer_rent_out_parking,
            R.drawable.ic_accessibility,
            R.drawable.ic_info};

    private String[] values;
    private DBHandler dbHandler;
    private DateUtil dateUtil;
    private static final String TAG = "ParkingSpaceDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_space_details);

        dbHandler = new DBHandler(this);
        dateUtil = new DateUtil();

        // Set up toolbar
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar_with_only_back_navigation);
        mainToolbar.setTitle(getResources().getString(R.string.title_activity_parking_space_details));
        mainToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mainToolbar.setNavigationIcon(R.drawable.ic_back);
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Decide whether to do this, or return result for startActivityWithResult
                onBackPressed();
            }
        });

        // Get the selected parkingSpaceId
        Bundle bundle = getIntent().getExtras();
        String parkingSpaceId = bundle.getString("parkingSpaceId");

        // Retrieve the parking space details from the database
        ParkingSpace parkingSpace = dbHandler.getParkingSpaceFromSearchResponse(parkingSpaceId);
        // Populate values
        Log.d(TAG, parkingSpace.getStartDateTime() + "    " +
                parkingSpace.getEndDateTime());
        values = new String[]{
                parkingSpace.getParkingSpaceAddress(),
                dateUtil.getDateString(parkingSpace.getStartDateTime()) + ", "
                        + dateUtil.getRangeString(parkingSpace.getStartDateTime(),
                            parkingSpace.getEndDateTime()),
                "$" + String.valueOf(parkingSpace.getParkingSpaceRate()) + " per hour",
                (parkingSpace.isDisabledParkingFlag()) ? "Yes" : "No",
                parkingSpace.getParkingSpaceDescription()
        };

        // Populate List View
        ListView lv = (ListView) findViewById(R.id.parkingSpaceDetailsListView);
        lv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return TITLES.length;
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
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = getLayoutInflater().inflate(R.layout.listitem_parking_space_details, null);
                ImageView icon = (ImageView) convertView
                        .findViewById(R.id.list_item_parking_space_details_icon);
                TextView title = (TextView) convertView
                        .findViewById(R.id.list_item_parking_space_details_icon_title);
                TextView value = (TextView) convertView
                        .findViewById(R.id.list_item_parking_space_details_icon_value);

                switch (position) {
                    case 0:
                        icon.setImageResource(ICONS[0]);
                        title.setText(TITLES[0]);
                        value.setText(values[0]);
                        break;
                    case 1:
                        icon.setImageResource(ICONS[1]);
                        icon.setColorFilter(Color.argb(255, 255, 160, 0));
                        title.setText(TITLES[1]);
                        value.setText(values[1]);
                        break;
                    case 2:
                        icon.setImageResource(ICONS[2]);
                        icon.setColorFilter(Color.argb(255, 255, 160, 0));
                        title.setText(TITLES[2]);
                        value.setText(values[2]);
                        break;
                    case 3:
                        icon.setImageResource(ICONS[3]);
                        icon.setColorFilter(Color.argb(255, 255, 160, 0));
                        title.setText(TITLES[3]);
                        value.setText(values[3]);
                        break;
                    case 4:
                        icon.setImageResource(ICONS[4]);
                        icon.setColorFilter(Color.argb(255, 255, 160, 0));
                        title.setText(TITLES[4]);
                        value.setText(values[4]);
                        break;
                }
                return convertView;
            }
        });




    }

}
