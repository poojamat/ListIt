package com.mathur.android.listit;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by neerajpooja on 5/31/2016.
 */

public class GetTokenTask extends AsyncTask<Void, Void, Boolean> {

    private final String mEmail;
    private final String mPassword;
    private Context mContext;

    GetTokenTask(String email, String password, Context context) {
        mEmail = email;
        mPassword = password;
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        String result;
        try {

            RestClient restClient = new RestClient();
            result = restClient.getToken(mEmail, mPassword, mContext);
            JSONObject resultJson = new JSONObject(result);


            //int id = Integer.parseInt(resultJson.getString("id"));
            //String token = resultJson.getString("token");
            //((Global) getApplicationContext()).set_userId(id);

            //Thread.sleep(2000);
            //} catch (InterruptedException e) {
            //return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mEmail)) {
//                    // Account exists, return true if the password matches.
//                    return pieces[1].equals(mPassword);
//                }
//            }


        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

    }

    @Override
    protected void onCancelled() {

    }
}

