package edu.sjsu.cmpe295.parket.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.sjsu.cmpe295.parket.R;

/**
 * Created by bdeo on 4/5/15.
 */
public class CredentialStore {
    private static String TAG = "CredentialStore";
    private static CredentialStore instance;
    private static Context context;

    // Credentials
    private static String serverClientId;

    public synchronized static CredentialStore getInstance(Context context) {
        if(instance == null) {
            instance = new CredentialStore(context);
        }
        return instance;
    }

    private CredentialStore(Context context) {
        this.context = context;
        try {
            InputStream fis = context.getResources().openRawResource(R.raw.keys);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer("");
            String s = null;

            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            br.close();
            isr.close();
            fis.close();

            // Save the credentials read from the file
            try {
                JSONObject jsonObject = new JSONObject(sb.toString());
                this.serverClientId = jsonObject.getString("serverClientId");

            } catch (JSONException e) {
                Log.e(TAG, "Error decoding JSON while getting credentials", e);
            }

        } catch (IOException e) {
            Log.e(TAG, "IOException while getting credentials", e);
        }
    }

    public String getServerClientId() {
        return serverClientId;
    }
}
