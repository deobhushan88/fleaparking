package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.visa.checkout.VisaMcomLibrary;
import com.visa.checkout.VisaMerchantInfo;
import com.visa.checkout.VisaPaymentInfo;
import com.visa.checkout.utils.VisaEnvironmentConfig;
import com.visa.checkout.widget.VisaPaymentButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.sjsu.cmpe295.parket.http.RestClient;
import edu.sjsu.cmpe295.parket.model.ParkingSpace;
import edu.sjsu.cmpe295.parket.model.request.BookParkingSpaceRequest;
import edu.sjsu.cmpe295.parket.model.response.BookParkingSpaceResponse;
import edu.sjsu.cmpe295.parket.util.AuthUtil;
import edu.sjsu.cmpe295.parket.util.DBHandler;
import edu.sjsu.cmpe295.parket.util.DateUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class BookParkingSpace extends Activity {

    private final static String[] TITLES = new String[]{
            "Address",
            "Booking Date",
            "Set Booking Start Time",
            "Set Booking End Time",
            "Total"};
    private final static int[] ICONS = new int[]{
            R.drawable.ic_map_pin_primary_dark,
            R.drawable.ic_date,
            R.drawable.ic_time,
            R.drawable.ic_time,
            R.drawable.ic_infowindow_money};

    private String[] values;

    // Utilities
    DBHandler dbHandler;
    DateUtil dateUtil;
    AuthUtil authUtil;

    // The parkingSpaceId shown by this activity
    String parkingSpaceId;
    // The QR Code of that parking space
    String qrCode;
    double parkingSpaceLat;
    double parkingSpaceLong;
    // Start and end date times as Calendar member variables
    Calendar cStart;
    Calendar cEnd;

    private static ArrayList<String> startTimes;
    private static ArrayList<String> endTimes;

    private static final String TAG = "BookParkingSpace";
    private static final String TAG_STARTTIME = "startTimeListItem";
    private static final String TAG_ENDTIME = "endTimeListItem";
    private static final String TAG_TOTAL = "totalListItem";

    // Visa checkout
    private static final int VISA_CHECKOUT_REQUEST_CODE = 10102;
    private VisaPaymentButton checkoutWithVisaButton;
    private VisaMcomLibrary mVisaMcomLibrary;
    private double calculatedTotal = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_parking_space);

        dbHandler = new DBHandler(this);
        dateUtil = new DateUtil();
        authUtil = new AuthUtil(this);

        // Set up Visa Checkout
        checkoutWithVisaButton = (VisaPaymentButton) findViewById(R.id.visaCheckoutButton);
        VisaEnvironmentConfig visaEnvironmentConfig = VisaEnvironmentConfig.SANDBOX;
        visaEnvironmentConfig.setMerchantApiKey(authUtil.getVisaApiKey());
        mVisaMcomLibrary = VisaMcomLibrary.getLibrary(this, visaEnvironmentConfig);

        // Set up toolbar
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar_with_only_back_navigation);
        mainToolbar.setTitle(getResources().getString(R.string.title_activity_book_parking_space));
        mainToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mainToolbar.setNavigationIcon(R.drawable.ic_back);
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Decide whether to do this, or return result for startActivityWithResult
                onBackPressed();
            }
        });

        // Get the selected parkingSpaceId and associated qrCode
        parkingSpaceId = getIntent().getStringExtra("parkingSpaceId");
        qrCode = getIntent().getStringExtra("qrCode");
        parkingSpaceLat = getIntent().getDoubleExtra("parkingSpaceLat", 0.0);
        parkingSpaceLong = getIntent().getDoubleExtra("parkingSpaceLong", 0.0);

        // Retrieve the parking space details from the database
        final ParkingSpace parkingSpace = dbHandler.getParkingSpaceFromSearchResponse(parkingSpaceId);
        // Calculate the start and end times arrays
        startTimes = new ArrayList<String>();
        endTimes = new ArrayList<String>();
        cStart = dateUtil.decodeFromString(parkingSpace.getStartDateTime());
        cEnd = dateUtil.decodeFromString(parkingSpace.getEndDateTime());
        int startHour = cStart.get(Calendar.HOUR_OF_DAY);
        int startMinute = cStart.get(Calendar.MINUTE);
        int endHour = cEnd.get(Calendar.HOUR_OF_DAY);
        int endMinute = cEnd.get(Calendar.MINUTE);
        while (startHour < endHour) {
            startTimes.add(dateUtil.pickerTimeToMachineString(startHour, startMinute));
            startHour = startHour + 1;
            endTimes.add(dateUtil.pickerTimeToMachineString(startHour, startMinute));
        }

        // Populate values
        values = new String[] {
                parkingSpace.getParkingSpaceAddress(),
                dateUtil.getDateString(parkingSpace.getStartDateTime()),
                "Tap to set start time",
                "Tap to set end time",
                "Tap to get total"};

        // Populate list view
        final ListView lv = (ListView) findViewById(R.id.bookParkingSpaceListView);
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
                final TextView value = (TextView) convertView
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

                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogFragment newFragment = new StartTimePickerFragment();
                                newFragment.show(getFragmentManager(), TAG_STARTTIME);
                            }
                        });
                        value.setTag(TAG_STARTTIME);
                        break;
                    case 3:
                        icon.setImageResource(ICONS[3]);
                        icon.setColorFilter(Color.argb(255, 255, 160, 0));
                        title.setText(TITLES[3]);
                        value.setText(values[3]);

                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogFragment newFragment = new EndTimePickerFragment();
                                newFragment.show(getFragmentManager(), TAG_ENDTIME);
                            }
                        });
                        value.setTag(TAG_ENDTIME);
                        break;
                    case 4:
                        icon.setImageResource(ICONS[4]);
                        icon.setColorFilter(Color.argb(255, 255, 160, 0));
                        title.setText(TITLES[4]);
                        value.setText(values[4]);
                        value.setTag(TAG_TOTAL);

                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Activity host = (Activity) v.getContext();
                                ListView l = (ListView) host
                                        .findViewById(R.id.bookParkingSpaceListView);
                                // Determine the value
                                TextView startTextView = ((TextView)l.findViewWithTag(TAG_STARTTIME));
                                TextView endTextView = ((TextView)l.findViewWithTag(TAG_ENDTIME));
                                if (startTextView != null && endTextView != null) {
                                    String start = startTextView.getText().toString().trim();
                                    String end = endTextView.getText().toString().trim();
                                    Double hours = 0.0;
                                    Double rate = 0.0;

                                    // Some values have been set in both start and end time list items
                                    if (!(start.equals("Tap to set start time")
                                            || end.equals("Tap to set end time"))) {
                                        hours = Double.parseDouble(end.substring(0, 2))
                                                - Double.parseDouble(start.substring(0, 2));
                                        // Check that start and end times are not same
                                        if (hours > 0) {
                                            rate = parkingSpace.getParkingSpaceRate();
                                            calculatedTotal = hours * rate;
                                            value.setText("$" + String.valueOf(calculatedTotal));
                                            // Disable onClickListeners on the time list views

                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                    "End time should be later than the Start time", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    else Toast.makeText(getApplicationContext(),
                                            "Please set start and end time first", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        break;
                }
                return convertView;
            }
        });
    }

    public static class StartTimePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Set Start Time")
                    .setItems(startTimes.toArray(new CharSequence[startTimes.size()]),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Use the time selected and set it as the appropriate list items' text
                                    ListView l = (ListView)(getActivity()
                                            .findViewById(R.id.bookParkingSpaceListView));
                                    ((TextView)(l.findViewWithTag(TAG_STARTTIME)))
                                            .setText(startTimes.get(which));
                                }
                            });
            return builder.create();
        }
    }

    public static class EndTimePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Set End Time")
                    .setItems(endTimes.toArray(new CharSequence[endTimes.size()]),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Use the time selected and set it as the appropriate list items' text
                                    ListView l = (ListView)(getActivity()
                                            .findViewById(R.id.bookParkingSpaceListView));
                                    ((TextView)(l.findViewWithTag(TAG_ENDTIME)))
                                            .setText(endTimes.get(which));

                                }
                            });
            return builder.create();
        }
    }

    // Visa checkout methods
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*Log.d(TAG, "Result got back from Visa Checkout SDK");
        String result = "Failed";
        if (data != null) {
            if (data.getParcelableExtra(VisaLibrary.PAYMENT_SUMMARY) != null) {
                VisaPaymentSummary paymentSummary =
                        data.getParcelableExtra(VisaLibrary.PAYMENT_SUMMARY);
                if (paymentSummary != null) {

                    Log.d(TAG, "PURCHASE SUCCESS : \n countryCode : "
                                    + paymentSummary.getCountryCode()
                                    + "\n postalCode : " + paymentSummary.getPostalCode()
                                    + "\n lastFourDigits : " + paymentSummary.getLastFourDigits()
                                    + "\n cardBrand : " + paymentSummary.getCardBrand()
                                    + "\n cardType : " + paymentSummary.getCardType()
                                    + "\n encPaymentData : " + paymentSummary.getEncPaymentData()
                                    + "\n encKey : " + paymentSummary.getEncKey()
                                    + "\n callId : " + paymentSummary.getCallId()
                    );
                    result = "Success";
                    // Send payment summary to the parket server
                    // TODO
                }
            }
            else if (data.getStringExtra(VisaLibrary.VALIDATION_RESULTS) != null) {
                result = data.getStringExtra(VisaLibrary.VALIDATION_RESULTS);
            }
        }

        if (requestCode == VISA_CHECKOUT_REQUEST_CODE) {
            String msg = "purchase result " + result;
            Log.d(TAG, msg);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }

        if (resultCode == 505) {
            Toast.makeText(this, "SDK Version is not Supported, Result Code : " +
                    resultCode, Toast.LENGTH_LONG).show();
        }
        if (resultCode == 506) {
            Toast.makeText(this,
                    "SDK Version Check couldn't be completed, Result Code : "
                            + resultCode, Toast.LENGTH_LONG).show();
        }
        if (resultCode == 510) {
            Toast.makeText(this, "Device OS version is not supported : " +
                    resultCode, Toast.LENGTH_LONG).show();
        }*/
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(getApplicationContext(),
                "Payment Successful!", Toast.LENGTH_LONG).show();

        /**
         * If payment succeeded through Visa Checkout, then perform following steps:
         * 1. Make a web service call to booking endpoint, including the encrypted data from
         *    Visa Checkout as well.
         * 2. When the bookings endpoint returns a 200 OK, that means payment and booking has
         *    succeeded. The backend has confirmed the payment with Visa Checkout.
         * 3. Now show the check-in UI
         */
        // Make request object
        ListView l = (ListView) findViewById(R.id.bookParkingSpaceListView);
        cStart.set(Calendar.HOUR_OF_DAY,
                Integer.parseInt((((TextView)(l.findViewWithTag(TAG_STARTTIME)))
                        .getText().toString().trim()).substring(0,2)));
        cStart.set(Calendar.MINUTE,
                Integer.parseInt((((TextView)(l.findViewWithTag(TAG_STARTTIME)))
                        .getText().toString().trim()).substring(3,5)));
        cEnd.set(Calendar.HOUR_OF_DAY,
                Integer.parseInt((((TextView)(l.findViewWithTag(TAG_ENDTIME)))
                        .getText().toString().trim()).substring(0,2)));
        cEnd.set(Calendar.MINUTE,
                Integer.parseInt((((TextView)(l.findViewWithTag(TAG_ENDTIME)))
                        .getText().toString().trim()).substring(3,5)));
        BookParkingSpaceRequest bookParkingSpaceRequest = new BookParkingSpaceRequest(
                authUtil.getIdToken(),
                parkingSpaceId,
                dateUtil.getFormattedString(cStart),
                dateUtil.getFormattedString(cEnd));
        RestClient.getInstance().bookParkingSpace(bookParkingSpaceRequest,
                new Callback<BookParkingSpaceResponse>() {
                    @Override
                    public void success(BookParkingSpaceResponse bookParkingSpaceResponse, Response response) {
                        // Save to Database
                        dbHandler.setBookingParkingSpaceResponse(bookParkingSpaceResponse, qrCode,
                                parkingSpaceLat, parkingSpaceLong);
                        // Go back to ShowParkingSpacesAroundMe
                        Intent i = new Intent(getApplicationContext(), ShowParkingSpacesAroundMe.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.putExtra("type", "BookParkingSpace");
                        startActivity(i);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    public void checkoutWithVisa(View v) {
        if (formIsValid()) {
            mVisaMcomLibrary.checkoutWithPayment(getPaymentInfo(), VISA_CHECKOUT_REQUEST_CODE);
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "Please enter all information correctly above before proceeding", Toast.LENGTH_LONG).show();
        }
    }

    private boolean formIsValid() {
        ListView l = (ListView) findViewById(R.id.bookParkingSpaceListView);
        TextView totalText = (TextView) l.findViewWithTag(TAG_TOTAL);
        return !(totalText.getText().toString().trim().equals("Tap to get total"));
    }

    private VisaPaymentInfo getPaymentInfo() {
        VisaPaymentInfo paymentInfo = new VisaPaymentInfo();

        /** REQUIRED: If merchant needs to collect shipping address from the user */
        paymentInfo.setUsingShippingAddress(false);

        /** (REQUIRED if total amount is set): Standard ISO format currency code.
         * Example USD, CAD, AUD
         */
        paymentInfo.setCurrency("USD");

        /** (REQUIRED if currency is set): The total amount for current session in
         *  BigDecimal format with max 2 decimal places and >= to 0 */
        paymentInfo.setTotal(new BigDecimal(calculatedTotal));
        /** OPTIONAL (CONTINUE by default): Action merchant wants to take whether to
         * Pay or Continue. If amount is not set, it should be Continue.
         * Enum values: UserReviewAction.CONTINUE or UserReviewAction.PAY
         */
        paymentInfo.setUserReviewAction(VisaPaymentInfo.UserReviewAction.CONTINUE);
        /** REQUIRED: The subtotal amount for current session in BigDecimal format with
         * max 2 decimal places and greater than or equal to zero
         */
        paymentInfo.setSubtotal(new BigDecimal(calculatedTotal));
        /** OPTIONAL: The tax amount for current session in BigDecimal format with a
         * max 2 decimal places and greater than or equal to zero
         */
        /*paymentInfo.setTax(new BigDecimal("12.34"));*/
        /** OPTIONAL: The misc amount for current session in BigDecimal format with a
         * max 2 decimal places and greater than or equal to zero
         */
        /*paymentInfo.setMisc(new BigDecimal("12.34"));*/
        /** OPTIONAL: The discount amount for current session in BigDecimal format with
         * max 2 decimal places and greater than or equal to zero */
        /*paymentInfo.setDiscount(new BigDecimal("12.34"));*/
        /** OPTIONAL: The giftwrap amount for current session in BigDecimal format with
         * max 2 decimal places and greater than or equal to zero
         */
        /*paymentInfo.setGiftWrap(new BigDecimal("12.34"));*/
        /** OPTIONAL: Shipping+handling amount for current session in BigDecimal format
         * with a max 2 decimal places and greater than or equal to zero
         */
        /*paymentInfo.setShippingHandling(new BigDecimal("12.34"));*/
        /** OPTIONAL: Description for current order (String value) */
        paymentInfo.setDescription("Parking Space Booking");
        /** OPTIONAL: Order ID for current transaction (String value) */
        paymentInfo.setOrderId(parkingSpaceId + " - " + String.valueOf(System.currentTimeMillis()));

        paymentInfo.setVisaMerchantInfo(getMerchantInfo());

        return paymentInfo;
    }

    private VisaMerchantInfo getMerchantInfo() {
        VisaMerchantInfo visaMerchantInfo = new VisaMerchantInfo();
        /** REQUIRED: Merchant API key obtained during onboarding (String) */
        visaMerchantInfo.setMerchantApiKey(authUtil.getVisaApiKey());
        /** REQUIRED: Access level for current merchant for the
         * encrypted payment info
         * (ENUM: MerchantDataLevel.SUMMARY or MerchantDataLevel.FULL)
         */
        visaMerchantInfo.setDataLevel(VisaMerchantInfo.MerchantDataLevel.SUMMARY);
        /** OPTIONAL: Logo Resource to display during the review screen (int) */
        visaMerchantInfo.setLogoResourceId(R.drawable.parket_logo);
        /** OPTIONAL: Name to display on the review screen for the merchant (String) */
        visaMerchantInfo.setDisplayName("Parket Inc.");
        /** OPTIONAL: Name to display on the review screen for the merchant (String) */
        visaMerchantInfo.setExternalProfileId("Parket Inc.");
        /** OPTIONAL: Merchant ID obtained during onboarding (String) */
        /*visaMerchantInfo.setMerchantId("212");*/
        /** OPTIONAL (false by default): Whether the current merchant wants to accept
         * Canadian Visa Debit cards (boolean)
         */
        visaMerchantInfo.setAcceptCanadianVisaDebit(false);
        /** OPTIONAL - List of shipping regions merchant wants to accept for current
         * transaction. (List of AcceptedShippingRegions Enum)
         */
        visaMerchantInfo.setAcceptedShippingRegions(getAcceptedShippingRegions());
        /** OPTIONAL - List of card brands that merchant wants to accept for current
         * transaction. (List of AcceptedCardBrands Enum)
         */
        visaMerchantInfo.setAcceptedCardBrands(getAcceptedCardBrands());
        return visaMerchantInfo;
    }

    private List<VisaMerchantInfo.AcceptedCardBrands> getAcceptedCardBrands(){

        /** Current AcceptedCardBrands values - AcceptedCardBrands.VISA,
         * AcceptedCardBrands.AMEX, AcceptedCardBrands.DISCOVER,
         * AcceptedCardBrands.MASTERCARD
         */
        List<VisaMerchantInfo.AcceptedCardBrands> acb =
                new ArrayList<VisaMerchantInfo.AcceptedCardBrands>();
        acb.add(VisaMerchantInfo.AcceptedCardBrands.VISA);
        acb.add(VisaMerchantInfo.AcceptedCardBrands.AMEX);
        acb.add(VisaMerchantInfo.AcceptedCardBrands.DISCOVER);
        acb.add(VisaMerchantInfo.AcceptedCardBrands.MASTERCARD);
        return acb;
    }

    private List<VisaMerchantInfo.AcceptedShippingRegions>getAcceptedShippingRegions(){

        /** Current AcceptedShippingRegions values - AcceptedShippingRegions.US,
         * AcceptedShippingRegions.CA, AcceptedShippingRegions.AU
         */
        List<VisaMerchantInfo.AcceptedShippingRegions> acceptedShippingRegions =
                new ArrayList<VisaMerchantInfo.AcceptedShippingRegions>();
        acceptedShippingRegions.add(VisaMerchantInfo.AcceptedShippingRegions.US);
        return acceptedShippingRegions;
    }

}
