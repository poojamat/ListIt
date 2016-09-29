package com.mathur.android.listit;

/**
 * Created by neerajpooja on 7/13/2016.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class GoogleLoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String mToken;
    private Context mContext;

    GoogleLoginTask(String token, Context context) {
        this.mToken = token;

        mContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        String result;
        try {
            // Simulate network access.
            URL url = new URL("http://52.38.93.60/login");
            RestClient restClient = new RestClient();
            result = restClient.login(url);

            Log.d("gconnect user token", result.toString());
            url = new URL("http://52.38.93.60/gconnect?state=" + result);
            String resultToken = restClient.gConnect(mToken, mContext, url);
            Log.d("gconnect resultToken", resultToken.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {


        if (success) {

        } else {

        }
    }

    @Override
    protected void onCancelled() {

    }
}