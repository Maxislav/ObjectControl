package com.atlas.mars.objectcontrol.http;

import android.os.AsyncTask;
import android.util.Log;

import com.atlas.mars.objectcontrol.gps.MapsActivity;
import com.atlas.mars.objectcontrol.gps.Track;

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
public class MapQuest{
    MapsActivity mapsActivity;
    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';
    private static final String LOGGER_TAG="routing";
    Track track;
    Auth au;
    URLConnection urlConnection;

    public MapQuest(MapsActivity mapsActivity, Track track) {
        this.track = track;
        this.mapsActivity = mapsActivity;
        urlConnection = null;

    }

    public void findRoute(String from, String to){
        au = new Auth();
        au.execute(from, to);
    }



    class Auth extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiKey = "geCwAnTQVkpj2ixbLJyHsLpnuZtG742A";
            //String from = "50.3891,30.49373";
            String from = params[0];
            String to = params[1];
            //http://www.mapquestapi.com/directions/v2/route?key=geCwAnTQVkpj2ixbLJyHsLpnuZtG742A&from=50.3891,30.49373&to=50.446,30.44852&routeType=bicycle&unit=k&fullShape=true
            String urlPath = "http://www.mapquestapi.com/directions/v2/route?key="+apiKey+"&from="+from+"&to="+to+"&routeType=shortest&unit=k&fullShape=true";

            Log.d(LOGGER_TAG, urlPath);
            InputStream in=null;
            URL url = null;
            try {
                url = new URL(urlPath);
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
            String resParams = getResponseText(in);
            return resParams;
        }
        @Override
        protected void onPostExecute(String result) {
            track.parseTrack(result);
        }

        private String getResponseText(InputStream is) {
            if(is == null){
                return null;
            }
            Log.d(LOGGER_TAG, "+++ InputStream");
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