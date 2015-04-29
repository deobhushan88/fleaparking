package edu.sjsu.cmpe295.parket.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by bdeo on 4/2/15.
 * <p/>
 * Auth utilities
 */
public class AuthUtil {

    private static Context context;

    private static final String ID_TOKEN_FILE_NAME = "idTokenStore";
    private static final String ID_TOKEN_EXPIRY_FILE_NAME = "idTokenExpiryStore";
    private static final String USER_EMAIL_FILE_NAME = "userEmailStore";

    // Token expires every 60 minutes (source/experiment?)
    // So setting the period to 55 minutes (in milliseconds)
    private static final long TOKEN_EXPIRY_PERIOD = 3300000;


    /* Server Client ID for OAuth */
    private static String SERVER_CLIENT_ID;
    private static String TOKEN_REQUEST_SCOPE;
    private static String VISA_API_KEY;

    private final String TAG = "LoginUtil";

    public AuthUtil(Context context) {
        this.context = context;
        this.SERVER_CLIENT_ID = CredentialStore.getInstance(context).getServerClientId();
        this.TOKEN_REQUEST_SCOPE = "audience:server:client_id:" +
                CredentialStore.getInstance(context).getServerClientId();
        this.VISA_API_KEY = CredentialStore.getInstance(context).getVisaApiKey();
    }

    /**
     * Store the email address selected by user to log in
     */
    public void setUserEmail(String userEmail) {
        try (FileOutputStream fos = context.openFileOutput(USER_EMAIL_FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(userEmail.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Error writing to userEmailStore", e);
        }
        Log.d(TAG, "setting userEmail: " + userEmail);
    }

    /**
     * Get the email address selected by user to log in
     */
    public String getUserEmail() {
        StringBuffer sb = new StringBuffer("");
        try {
            FileInputStream fis = context.openFileInput(USER_EMAIL_FILE_NAME);
            InputStreamReader iis = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(iis);
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            fis.close();
            iis.close();
            br.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading from userEmailStore", e);
        }
        Log.d(TAG, "getting userEmail : " + sb.toString());
        return sb.toString();
    }

    /**
     * Store the Google ID Token
     */
    public void setIdToken(String idToken) {
        try (FileOutputStream fos = context.openFileOutput(ID_TOKEN_FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(idToken.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Error writing to idTokenStore", e);
        }
        Log.d(TAG, "setting idToken: " + idToken);
    }

    /**
     * Get the Google ID Token
     */
    public String getIdToken() {
        StringBuffer sb = new StringBuffer("");
        try {
            FileInputStream fis = context.openFileInput(ID_TOKEN_FILE_NAME);
            InputStreamReader iis = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(iis);
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            fis.close();
            iis.close();
            br.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading from idTokenStore", e);
        }
        Log.d(TAG, "getting idToken : " + sb.toString());
        return sb.toString();
    }

    /**
     * Store the idToken expiry
     */
    public void setIdTokenExpiry(String expiry) {
        try (FileOutputStream fos = context.openFileOutput(ID_TOKEN_EXPIRY_FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(expiry.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Error writing to idTokenExpiryStore", e);
        }
        Log.d(TAG, "setting idTokenExpiry: " + expiry);
    }

    /**
     * Get the idToken expiry
     */
    public String getIdTokenExpiry() {
        StringBuffer sb = new StringBuffer("");
        try {
            FileInputStream fis = context.openFileInput(ID_TOKEN_EXPIRY_FILE_NAME);
            InputStreamReader iis = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(iis);
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            fis.close();
            iis.close();
            br.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading from idTokenExpiryStore", e);
        }
        Log.d(TAG, "getting idTokenExpiry : " + sb.toString());
        return sb.toString();
    }


    public String getServerClientId() {
        return SERVER_CLIENT_ID;
    }

    public long getTokenExpiryPeriod() {
        return TOKEN_EXPIRY_PERIOD;
    }

    public String getScope() {
        return TOKEN_REQUEST_SCOPE;
    }

    public String getVisaApiKey() { return VISA_API_KEY; }

}
