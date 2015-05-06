package com.atlas.mars.objectcontrol.http;

import android.os.AsyncTask;
import android.util.Log;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.atlas.mars.objectcontrol.gps.MapsActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Администратор on 4/26/15.
 */
public class MyHttp {
    MapsActivity mapsActivity;
    private final String TAG = "myLog";

    MyTask mt;
    DataBaseHelper db;
    HashMap<String,String> mapSetting;
    HttpClient httpClient;



    public MyHttp(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
        db = new DataBaseHelper(mapsActivity);
        mapSetting = new HashMap<>();
        db.getSetting(mapSetting);
        httpClient = new DefaultHttpClient();

    }

    public void postData() {
        //"http://gps-tracker.com.ua/login.php"

        mt = new MyTask();
        if(mapSetting.get(db.MAP_LOGIN)!=null && mapSetting.get(db.MAP_PASS)!=null && mapSetting.get(db.MAP_SERVER_URL)!=null){
            mt.execute(mapSetting.get(db.MAP_LOGIN), mapSetting.get(db.MAP_PASS), mapSetting.get(db.MAP_SERVER_URL));
        }
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }




    class MyTask extends AsyncTask<String , Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // tvInfo.setText("Begin");
        }

        @Override
        protected String doInBackground(String ... _params) {
            String resText = null;
            String login = _params[0];
            String pass = _params[1];
            String urlAuth = _params[2]+"/login.php";
            String urlStateObj = _params[2]+"/loadevents.php?param=icars";

            Log.d(TAG, "+++ doInBackground");
           // httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urlAuth);
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("login", login));
            nameValuePair.add(new BasicNameValuePair("password", pass));
            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
                Log.e(TAG, "UnsupportedEncodingException+++"+ e.toString());
            }

            try {
                HttpResponse response = httpClient.execute(httpPost);
                // write response to log
              //  Log.d( TAG,  "Http Post Response1: +++ " +response.toString() );
               // resText = response.toString();
                InputStreamReader is = new InputStreamReader(response.getEntity().getContent());

                BufferedReader r = new BufferedReader(is);
                StringBuilder total = new StringBuilder();

                String line = null;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                Log.d( TAG,  "Http Post Response2: +++ " +total.toString() );
                resText = total.toString();

                is.close();

            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.e(TAG, "ClientProtocolException++ " + e.toString());

            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
                Log.e(TAG, "IOException ++ " + e.toString());
            }

            HttpGet httpGet = new HttpGet(urlStateObj);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                InputStreamReader is = new InputStreamReader(response.getEntity().getContent());
                BufferedReader r = new BufferedReader(is);
                StringBuilder total = new StringBuilder();
                String line = null;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                Log.d( TAG,  "Http Post Response3: +++ " +total.toString() );

            }catch (IOException e){
                Log.e(TAG, "IOException ++ " + e.toString());
            }


            return resText;
        }

        @Override
        protected void onPostExecute(String result) {
            mapsActivity.toastShow(result);
           // tvInfo.setText("End");
        }
    }

}
