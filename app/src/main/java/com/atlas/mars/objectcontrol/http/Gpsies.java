package com.atlas.mars.objectcontrol.http;

import android.os.AsyncTask;
import android.util.Log;

import com.atlas.mars.objectcontrol.gps.MapsActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by mars on 6/26/15.
 */
public class Gpsies {
    MapsActivity mapsActivity;
    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';
    private static final String LOGGER_TAG="myLog";
    Auth au;
    URLConnection urlConnection;

    public Gpsies(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
        urlConnection = null;
        au = new Auth();
        au.execute("http://www.gpsies.com/createTrack.do");
    }

    class  Auth extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {


            Log.d(LOGGER_TAG, "+++start");
            InputStream in=null;
            URL url = null;
            try {
                url = new URL(params[0]);
                urlConnection = url.openConnection();

                Map<String, List<String>> headerFields = urlConnection.getHeaderFields();

                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (MalformedURLException e) {
                Log.e(LOGGER_TAG, "+++MalformedURLException");
                e.printStackTrace();
            }catch (IOException e) {
                Log.e(LOGGER_TAG, "+++IOException");
                e.printStackTrace();
            }



            return getResponseText(in);
        }
        @Override
        protected void onPostExecute(String result) {

                Log.d(LOGGER_TAG, "+++ result " + result);
        }

        private String getResponseText(InputStream is) {

            Log.d(LOGGER_TAG, "+++InputStream");
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return sb.toString();
        }
    }


}
