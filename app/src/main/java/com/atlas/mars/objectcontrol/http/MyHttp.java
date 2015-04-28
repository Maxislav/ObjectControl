package com.atlas.mars.objectcontrol.http;

import android.os.AsyncTask;
import android.util.Log;

import com.atlas.mars.objectcontrol.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Администратор on 4/26/15.
 */
public class MyHttp {
    MainActivity mainActivity;
    private final String TAG = "myLog";
    String url;
    MyTask mt;

    public MyHttp(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void postData(String url) {
        this.url= url;
        mt = new MyTask();
        mt.execute();
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

    class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // tvInfo.setText("Begin");
        }

        @Override
        protected Void doInBackground(Void... _params) {
            Log.d(TAG, "+++ doInBackground");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("login", "Mars"));
            nameValuePair.add(new BasicNameValuePair("password", "..."));
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
                Log.d( TAG,  "Http Post Response: +++ " +response.toString() );

                InputStreamReader is = new InputStreamReader(response.getEntity().getContent());

                BufferedReader r = new BufferedReader(is);
                StringBuilder total = new StringBuilder();

                String line = null;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                Log.d( TAG,  "Http Post Response: +++ " +total.toString() );


                is.close();


            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
                Log.e(TAG, "ClientProtocolException++ " + e.toString());

            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
                Log.e(TAG, "IOException ++ " + e.toString());
            }


                try{


                }catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }

               // TimeUnit.SECONDS.sleep(2);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
           // tvInfo.setText("End");
        }
    }

}
