package edu.sjsu.cmpe295.parket;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.sjsu.cmpe295.parket.util.AuthUtil;
import edu.sjsu.cmpe295.parket.util.IdTokenService;

public class LoginActivity extends Activity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ServerAuthCodeCallbacks {

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* A flag indicating that a PendingIntent is in progress and prevents
    * us from starting further intents.
    */
    private boolean mIntentInProgress;

    /* Track whether the sign-in button has been clicked so that we know to resolve
    * all issues preventing sign-in without waiting.
    */
    private boolean mSignInClicked;

    /* Store the connection result from onConnectionFailed callbacks so that we can
    * resolve them when the user clicks sign-in.
    */
    private ConnectionResult mConnectionResult;

    private SignInButton btnSignIn;
    private GoogleApiClient mGoogleApiClient;

    private final String TAG = "LoginActivity";

    private AuthUtil authUtil;
    private ComponentName serviceComponent;
    private static int jobId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authUtil = new AuthUtil(getApplicationContext());

        // Set up Google Sign in Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .requestServerAuthCode(authUtil.getServerClientId(), this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        btnSignIn = (SignInButton)findViewById(R.id.btn_googleSignIn);
        btnSignIn.setOnClickListener(this);

        serviceComponent = new ComponentName(this, IdTokenService.class);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_googleSignIn
                && !mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Starting the IdToken service.
        // This will run periodically from here on, updating the idToken
        JobInfo.Builder builder = new JobInfo.Builder(jobId++, serviceComponent)
                .setPeriodic(authUtil.getTokenExpiryPeriod())
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true);

        JobScheduler jobScheduler =
                (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());

        // Move on to next activity
        Intent i = new Intent(getApplicationContext(), AdvancedSearch.class);
        startActivity(i);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    public CheckResult onCheckServerAuthorization(String idToken, Set<Scope> scopeSet) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet;
        HttpResponse httpResponse;
        int responseCode;
        String responseBody;

    /*
     * Get a list of scopes required by the server and check whether the
     * server has a valid refresh token for the user.
     */
        HashSet<Scope> serverScopeSet = new HashSet<Scope>();
        Boolean serverHasToken = false;
        try {
            String id = URLEncoder.encode(idToken, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Unsupported encoding", e);
        }
        HttpPost httpPost = new HttpPost(getString(R.string.backend_auth));
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("idToken", idToken));
            nameValuePairs.add(new BasicNameValuePair("scopes", serverScopeSet.toString()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            httpResponse = httpClient.execute(httpPost);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            responseBody = EntityUtils.toString(httpResponse.getEntity());
            JSONObject json = new JSONObject(responseBody);
            if (responseCode == 200) {
                /* Not needed, as our server does not need additional scopes
                String[] scopeStrings = json.getString("scopes");
                for (String scope : scopeStrings) {
                    Log.i(TAG, "Server Scope: " + scope);
                    serverScopeSet.add(new Scope(scope));
                }
                */
                serverHasToken = json.getBoolean("has_refresh_token");
            } else {
                Log.e(TAG, "Error in getting refresh token status: " + responseCode);
            }
        } catch (ClientProtocolException e) {
            Log.e(TAG, "Error in getting refresh token status.", e);
        } catch (IOException e) {
            Log.e(TAG, "Error in getting refresh token status.", e);
        } catch (JSONException e) {
            Log.e(TAG, "Invalid JSON", e);
        }

    /*
     * If the server doesn't have a valid refresh token, tell the
     * GoogleApiClient to retrieve a new authorization code for the scopes.
     */
        if (!serverHasToken) {
            // Note: ignoring serverScopeSet (and using scopeSet) for now,
            // since our server is not going to send any additional scopes
            return CheckResult.newAuthRequiredResult(scopeSet);
        } else {
            // The server has a valid refresh token, so a new authorization code
            // is not necessary.
            return CheckResult.newAuthNotRequiredResult();
        }
    }

    @Override
    public boolean onUploadServerAuthCode(String idToken, String serverAuthCode) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(getString(R.string.backend_auth_callback));
        try {
            // Storing the initial idToken
            authUtil.setIdToken(idToken);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("idToken", idToken));
            nameValuePairs.add(new BasicNameValuePair("serverAuthCode", serverAuthCode));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            final String responseBody = EntityUtils.toString(response.getEntity());
            Log.i(TAG, "Code: " + statusCode);
            Log.i(TAG, "Resp: " + responseBody);

            if (statusCode == 200) {
                try {
                    // Save the idToken expiry
                    // NOTE: THIS IS NOT BEING USED ANYWHERE CURRENTLY, CONSIDER REMOVING
                    JSONObject jo = new JSONObject(responseBody);
                    authUtil.setIdTokenExpiry(jo.getString("expiry"));
                    return true;
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing auth callback JSON response", e);
                    return false;
                }
            }
            else {
                JSONObject jo = null;
                try {
                    jo = new JSONObject(responseBody);
                    Log.e(TAG, "Server returned status code + " + statusCode + " and status "
                            + jo.getString("status") +" - from auth callback");
                    return false;
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing auth callback JSON response", e);
                    return false;
                }
            }
        } catch (ClientProtocolException e) {
            Log.e(TAG, "Error in auth code exchange.", e);
            return false;
        } catch (IOException e) {
            Log.e(TAG, "Error in auth code exchange.", e);
            return false;
        }
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode == RESULT_OK) {
                if (intent != null) {
                    // Store the account that user selected
                    String userEmail = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    authUtil.setUserEmail(userEmail);
                }
            }

            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

}
