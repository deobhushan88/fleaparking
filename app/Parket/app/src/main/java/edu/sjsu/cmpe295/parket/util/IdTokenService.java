package edu.sjsu.cmpe295.parket.util;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;

import java.io.IOException;

/**
 * Created by bdeo on 4/3/15.
 *
 * JobService to update Google ID Token before it expires
 */
public class IdTokenService extends JobService {

    private final String TAG = "IdTokenService";

    @Override
    public boolean onStartJob(JobParameters params) {
        AuthUtil authUtil = new AuthUtil(getApplicationContext());
        String account = authUtil.getUserEmail();
        String scope = authUtil.getScope();
        GetIdTokenTask git = new GetIdTokenTask(this, params, getApplicationContext(), account, scope);
        git.execute(); // This will store the idToken in onPostExecute(), and call jobFinished()
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private class GetIdTokenTask extends AsyncTask<Void, Void, String> {
        IdTokenService service;
        JobParameters params;
        Context context;
        String account;
        String scope;

        GetIdTokenTask(IdTokenService service, JobParameters params,
                       Context context, String account, String scope) {
            this.service = service;
            this.params = params;
            this.context = context;
            this.account = account;
            this.scope = scope;
        }

        @Override
        protected String doInBackground(Void... params) {
            String idToken = null;
            try {
                idToken = GoogleAuthUtil.getToken(context, account, scope);
            } catch (IOException e) {
                Log.e(TAG, "IOException while getting a idToken", e);
            } catch (GoogleAuthException e) {
                Log.e(TAG, "GoogleAuthException while getting a idToken", e);
            }
            Log.d(TAG, "Received idToken : " + idToken);
            return idToken;
        }

        @Override
        protected void onPostExecute(String result) {
            AuthUtil authUtil = new AuthUtil(getApplicationContext());
            authUtil.setIdToken(result);
            Log.d(TAG, "New idToken retrieved and set");
            service.jobFinished(params, false);
        }
    }
}
