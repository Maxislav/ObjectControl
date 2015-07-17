package com.atlas.mars.objectcontrol.http;

import android.os.AsyncTask;
import android.util.Log;

import com.atlas.mars.objectcontrol.gps.LoaderBar;
import com.atlas.mars.objectcontrol.gps.MapsActivity;
import com.atlas.mars.objectcontrol.gps.TrackParser;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mars on 6/26/15.
 */
public abstract class MapQuest {
    MapsActivity mapsActivity;
    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';
    private static final String LOGGER_TAG = "routing";
    private static final String TAG = "routing";
    FromMapQuest au;
    URLConnection urlConnection;
    static List<FromMapQuest> listFromMapQuest;
    LoaderBar loader;


    public MapQuest(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
        listFromMapQuest = new ArrayList<>();
        urlConnection = null;
        loader = new LoaderBar(mapsActivity);

    }

    public void findRoute(String from, String to, String routeType) {
        au = new FromMapQuest();
        listFromMapQuest.add(au);
        au.execute(from, to, routeType);
    }

    public abstract void onCallBack( HashMap<String, Object> result);

    public void onCancelled() {
        for (FromMapQuest au : listFromMapQuest) {
            if (au != null && au.getStatus() == AsyncTask.Status.RUNNING) {
                au.onCancelled();
            }
        }
    }

    public String createQueryStringForParameters(Map<String, String> parameters) {
        StringBuilder parametersAsQueryString = new StringBuilder();
        if (parameters != null) {
            boolean firstParameter = true;

            for (String parameterName : parameters.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append(PARAMETER_DELIMITER);
                }

                parametersAsQueryString.append(parameterName)
                        .append(PARAMETER_EQUALS_CHAR)
                        .append(URLEncoder.encode(
                                parameters.get(parameterName)));

                firstParameter = false;
            }
        }
        return parametersAsQueryString.toString();
    }

    class FromMapQuest extends AsyncTask<String, Void,  HashMap<String, Object> > {
        boolean onCancel = false;
        TrackParser trackParser;

        @Override
        protected  HashMap<String, Object>  doInBackground(String... params) {
            HashMap<String, Object> res = null;

            String result = null;

            String apiKey = "geCwAnTQVkpj2ixbLJyHsLpnuZtG742A";
            String from = params[0];
            String to = params[1];
            String routeType = params[2];
            switch (routeType) {
                case "car":
                    routeType = "fastest";
                    break;
                case "moto":
                    routeType = "shortest";
                    break;
                case "velo":
                    routeType = "bicycle";
                    break;
                default:
                    routeType = "fastest";

            }
            /**
             *  Example http://www.mapquestapi.com/directions/v2/route?key=geCwAnTQVkpj2ixbLJyHsLpnuZtG742A&from=50.3891,30.49373&to=50.446,30.44852&routeType=bicycle&unit=k&fullShape=true *
             */

            String urlPath = "http://www.mapquestapi.com/directions/v2/route?key=" + apiKey + "&from=" + from + "&to=" + to + "&routeType=" + routeType + "&unit=k&fullShape=true";

            Log.d(LOGGER_TAG, urlPath);
            InputStream in = null;
            URL url = null;
            try {
                url = new URL(urlPath);
                urlConnection = url.openConnection();

                Map<String, List<String>> headerFields = urlConnection.getHeaderFields();

                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (MalformedURLException e) {
                Log.e(LOGGER_TAG, "+++MalformedURLException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOGGER_TAG, "+++IOException");
                e.printStackTrace();
            }
            if (onCancel) {
                return null;
            }
            result = getResponseText(in);

            if(result!=null){
                res = new HashMap<>();
                trackParser = new TrackParser(result);
                LatLng[] latLngs = trackParser.getLatLngs();
                Double distance = trackParser.getDistance();
                res.put(TrackParser.LATLNGS, latLngs);
                res.put(TrackParser.DIST, distance);
            }
            return res;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.show();
        }

        @Override
        protected void onPostExecute( HashMap<String, Object> result) {
            super.onPostExecute(result);
            loader.hide();
            if (!onCancel) onCallBack(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            onCancel = true;
        }


        private String getResponseText(InputStream is) {
            if (is == null) {
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
