package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class AddParkingSpace extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Spinner spinner, state;
    Button addPhotosButton;
    EditText address1, address2, city, zipcode, parkingSpaceLabel, description;
    String dataPacket;
    String address1_GET, address2_GET, city_GET, state_GET, parkingSpaceLabel_GET, description_GET, zipcode_GET;
    private String selectedImagePath;
    private ImageView img;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int PICK_FROM_GALLERY = 2;
    Bitmap thumbnail = null;
    String encodedImage = "";
    byte[] byteArrayImage;

    // Location Services
    GoogleApiClient mGoogleApiClient;
    Location mUserLocation;

    private final static String TAG = "AddParkingSpace";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking_space);

        //Adding a Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_addParkingSpace);
        toolbar.setTitle(getResources().getString(R.string.title_activity_add_parking_space));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RentParkingSpace.class);
                startActivity(i);

            }
        });
        toolbar.inflateMenu(R.menu.toolbar_add_parking_space);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // First Check if the Google API client is connected
                if (mGoogleApiClient.isConnected()) {
                    // Get user's current location
                    mUserLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mUserLocation != null) {
                        updateUIWithAddress();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "Cannot get your current location, Try again later", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        // Location Services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


        spinner = (Spinner) findViewById(R.id.state);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(this,
                R.array.states, R.layout.spinner_layout);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        addPhotosButton = (Button) findViewById(R.id.addPhotos);
        addPhotosButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, RESULT_LOAD_IMAGE);

            }
        });

        Button verifyAddress = (Button) findViewById(R.id.verifyAddress);
        verifyAddress.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                parkingSpaceLabel = (EditText) findViewById(R.id.parkingSpaceLabel);
                description = (EditText) findViewById(R.id.description);
                address1 = (EditText) findViewById(R.id.address1);
                address2 = (EditText) findViewById(R.id.address2);
                city = (EditText) findViewById(R.id.city);
                state = (Spinner) findViewById(R.id.state);
                zipcode = (EditText) findViewById(R.id.zipcode);
                parkingSpaceLabel_GET = parkingSpaceLabel.getText().toString();
                description_GET = description.getText().toString();
                address1_GET = address1.getText().toString();
                address2_GET = address2.getText().toString();
                city_GET = city.getText().toString();
                state_GET = state.getSelectedItem().toString();
                zipcode_GET = zipcode.getText().toString();
                if (zipcode_GET.equals("")) zipcode_GET = "00000";
                dataPacket = address1_GET + " " + address2_GET + " " + city_GET + ", " + state_GET +  " " + zipcode_GET;
                Bundle bundle = new Bundle();
                bundle.putString("address", dataPacket);
                bundle.putString("parkingSpaceLabel", parkingSpaceLabel_GET);
                bundle.putString("parkingSpaceDescription", description_GET);
                bundle.putString("addrLine1", address1_GET);
                bundle.putString("addrLine2", address2_GET);
                bundle.putString("city", city_GET);
                bundle.putString("state", state_GET);
                bundle.putString("country", "USA");
                bundle.putInt("zip", Integer.parseInt(zipcode_GET));
                // TODO: Add a UI switch to select this disabledParkingFlag and use its value
                bundle.putBoolean("disabledParkingFlag", false);
                bundle.putByteArray("parkingSpacePhoto", byteArrayImage);
                loadSavedPreferences();
                Intent i = new Intent(getBaseContext(), ConfirmAddress.class);
                i.putExtras(bundle);
                startActivity(i);
           }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            Button btn = (Button) findViewById(R.id.addPhotos);
            btn.setText("Change Photo");
            //storing image in thumbnail
            thumbnail = (BitmapFactory.decodeFile(picturePath));
            //converting image into Bitmap
            Bitmap bm = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byteArrayImage = baos.toByteArray();
            //converting image into Base64
            // encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        }
    }

    private void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        parkingSpaceLabel.setText(sharedPreferences.getString(parkingSpaceLabel_GET, parkingSpaceLabel_GET));
        description.setText(sharedPreferences.getString(description_GET, description_GET));
        address1.setText(sharedPreferences.getString(address1_GET, address1_GET));
        address2.setText(sharedPreferences.getString(address2_GET, address2_GET));
        city.setText(sharedPreferences.getString(city_GET, city_GET));
        zipcode.setText(sharedPreferences.getString(zipcode_GET, zipcode_GET));
        int spinnerValue = sharedPreferences.getInt("User selection", -1);
        if (spinnerValue != -1) {
            state.setSelection(spinnerValue);
        }
    }

    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void saveData() {
        try {
            savePreferences(parkingSpaceLabel_GET, parkingSpaceLabel.getText().toString());
            savePreferences(address1_GET, address1.getText().toString());
            savePreferences(address2_GET, address2.getText().toString());
            savePreferences(city_GET, city.getText().toString());
            savePreferences(zipcode_GET, zipcode.getText().toString());
        } catch(Exception e) {

        }
    }

    @Override
    public void onBackPressed() {
        saveData();
        super.onBackPressed();
    }

    // Location services callbacks
    @Override
    public void onConnected(Bundle bundle) {
        mUserLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    public void updateUIWithAddress() {
        // Update the UI
        Geocoder g = new Geocoder(this);
        try {
            Address a = (g.getFromLocation(mUserLocation.getLatitude()
                    , mUserLocation.getLongitude(), 1)).get(0);
            int i = a.getMaxAddressLineIndex();
            if (i == 0) {
                ((EditText) findViewById(R.id.address1)).setText(a.getAddressLine(0));
            }
            if (i > 0) {
                ((EditText) findViewById(R.id.address1)).setText(a.getAddressLine(0));
                ((EditText) findViewById(R.id.address2)).setText(a.getAddressLine(1));
            }
            ((EditText) findViewById(R.id.city)).setText(a.getLocality());
            String[] stateStrings = getResources().getStringArray(R.array.states);
            i = 0;
            for (String s : stateStrings) {
                if (s.equals(a.getAdminArea())) {
                    // found the state
                    ((Spinner) findViewById(R.id.state)).setSelection(i);
                    break;
                }
                i++;
            }
            // TODO: Temporary, improve later
            // i == 49 means search has gone beyond the index, i.e. state was not found
            if (i==49) ((Spinner) findViewById(R.id.state)).setSelection(4); // CA
            ((EditText) findViewById(R.id.zipcode)).setText(a.getPostalCode());
        } catch (IOException e) {
            Log.e(TAG, "Error getting Address from latitude and longitude", e);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
