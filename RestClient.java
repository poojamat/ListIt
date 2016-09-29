package com.mathur.android.listit;
//import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by us48114 on 3/21/2016.
 */
public class RestClient {

    public String getClient(URL url, String username, String password) {
        //URL url;
        HttpURLConnection urlConnection = null;
        InputStream in;
        StringBuilder result = new StringBuilder();
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            String userpass = username + ":" + password;
            String header = "Basic " + new String(android.util.Base64.encode(userpass.getBytes(), android.util.Base64.NO_WRAP));
            urlConnection.addRequestProperty("Authorization", header);
            in = new BufferedInputStream(urlConnection.getInputStream());
            int HttpResult = urlConnection.getResponseCode();
            //readStream(in);
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }

        } catch (MalformedURLException ex) {
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return result.toString();
    }

    //public String login(URL url, Context context,String idToken) {
    public String login(URL url) {
        //URL url;
        HttpURLConnection urlConnection = null;
        InputStream in;
        StringBuilder result = new StringBuilder();
        String resultToken = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            int HttpResult = urlConnection.getResponseCode();
            Log.d("login http result", String.valueOf((HttpResult)));
            //readStream(in);
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                Log.d("login result", String.valueOf((result)));
            }
            //urlConnection.disconnect();
            //url = new URL("http://52.38.93.60/gconnect?state="+result);
            //urlConnection = (HttpURLConnection) url.openConnection();
            //resultToken=gConnect(idToken,context,urlConnection);
        } catch (MalformedURLException ex) {
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }  //finally {
        //  urlConnection.disconnect();
        //}
        return result.toString();
    }

    public String gConnect(String idToken, Context context, URL url) throws JSONException, IOException {
        JSONObject cred = new JSONObject();
        HttpURLConnection con;
        cred.put("idToken", idToken);

        con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");

        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(cred.toString());
        wr.flush();

        StringBuilder sb = new StringBuilder();
        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK || HttpResult == HttpURLConnection.HTTP_CREATED) {

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            String resultString = sb.toString();
            ((Global) context.getApplicationContext()).set_Token(resultString);
            SetUserInfo(context, resultString, idToken, "");

            return resultString;
        } else System.out.println(con.getResponseMessage());

        return sb.toString();

    }
    public String postUser(URL url, String username, String password, Context context) throws IOException, JSONException {

        JSONObject cred = new JSONObject();
        cred.put("username", username);
        cred.put("password", password);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");

        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(cred.toString());
        wr.flush();

        StringBuilder sb = new StringBuilder();
        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK || HttpResult == HttpURLConnection.HTTP_CREATED) {

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            getToken(username, password, context);
            String resultString = sb.toString();
            SetUserInfo(context, resultString, username, password);

            return resultString;
        } else System.out.println(con.getResponseMessage());

        return sb.toString();
    }

    public String getToken(String username, String password, Context context) throws MalformedURLException, JSONException {
        URL url;
        url = new URL("http://52.38.93.60/token");
        String resultToken = getClient(url, username, password);

        String token = ParseJasonFromHttpResponse(resultToken, "token");
        ((Global) context.getApplicationContext()).set_Token(token);
        return resultToken;
    }

    private void SetUserInfo(Context context, String resultUserString, String userName, String password) throws JSONException {
        int id = Integer.parseInt(ParseJasonFromHttpResponse(resultUserString, "id"));
        ((Global) context.getApplicationContext()).set_userId(id);

        MySQLiteHelper db = new MySQLiteHelper(context);
        User user = new User(id, userName, null, password);
        db.addUser(user);
    }

    private String ParseJasonFromHttpResponse(String resultString, String keyToBeParsed) throws JSONException {
        JSONObject resultJson = new JSONObject(resultString);

        return resultJson.getString(keyToBeParsed);
    }

    public String postRequest(URL url, String username, String password, Date lastUpdated) throws IOException, JSONException {

        //String encodedQuery = URLEncoder.encode(query, "UTF-8");

        //String postData = "e=" + encodedQuery;

        JSONObject cred = new JSONObject();
        cred.put("username", username);
        cred.put("password", password);
        cred.put("lastSyncDate", lastUpdated);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        //con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        //con.setRequestProperty("Content-Length",  String.valueOf(postData.length()));
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");
        String userpass = username + ":" + password;
        String header = "Basic " + new String(android.util.Base64.encode(userpass.getBytes(), android.util.Base64.NO_WRAP));
        con.addRequestProperty("Authorization", header);
        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(cred.toString());
        //wr.write(String.valueOf(postData.getBytes()));
        wr.flush();

//display what returns the POST request

        StringBuilder sb = new StringBuilder();
        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            System.out.println("" + sb.toString());
        } else System.out.println(con.getResponseMessage());

        return sb.toString();
    }

    public String deleteClient(URL url, String username, String password) {
        //URL url;
        HttpURLConnection urlConnection = null;
        InputStream in;
        StringBuilder result = new StringBuilder();
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            String userpass = username + ":" + password;
            String header = "Basic " + new String(android.util.Base64.encode(userpass.getBytes(), android.util.Base64.NO_WRAP));
            urlConnection.addRequestProperty("Authorization", header);
            urlConnection.setRequestMethod("DELETE");
            in = new BufferedInputStream(urlConnection.getInputStream());
            int HttpResult = urlConnection.getResponseCode();
            //readStream(in);
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }

        } catch (MalformedURLException ex) {
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return result.toString();
    }

}
