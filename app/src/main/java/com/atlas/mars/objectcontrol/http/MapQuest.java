package com.atlas.mars.objectcontrol.http;

import android.os.AsyncTask;
import android.util.Log;

import com.atlas.mars.objectcontrol.gps.LoaderBar;
import com.atlas.mars.objectcontrol.gps.MapsActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
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
public class MapQuest {
    MapsActivity mapsActivity;
    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';
    private static final String LOGGER_TAG = "routing";
    private static final String TAG = "routing";
    FromMapQuest au;
    URLConnection urlConnection;
    List<FromMapQuest> listFromMapQuest;
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

    public void onCallBack(String result) {

    }
    public void onCancelled(){
        for(FromMapQuest au : listFromMapQuest){
            if (au!=null && au.getStatus() == AsyncTask.Status.RUNNING){
                au.onCancelled();
            }
        }
    }
    class _FromMapQuest extends AsyncTask<String, Void, String> {
        boolean onCancel=false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.show();
        }

        protected String doInBackground(String... params) {
            String resp="";
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
            String urlPath = "http://www.mapquestapi.com/directions/v2/route?key=" + apiKey + "&from=" + from + "&to=" + to + "&routeType=" + routeType + "&unit=k&fullShape=true";
            String reqParam = "key=" + apiKey + "&from=" + from + "&to=" + to + "&routeType=" + routeType + "&unit=k&fullShape=true";
            try {
                String sentence="";
                String modifiedSentence;
                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                Socket clientSocket = new Socket("www.mapquestapi.com", 80); //http://178.62.44.54/
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
///directions/v2/route?key=geCwAnTQVkpj2ixbLJyHsLpnuZtG742A&from=50.3891,30.49373&to=50.4

                Map<String, String> mapReq = new HashMap<>();
                mapReq.put("key",apiKey);
                mapReq.put("from",from);
                mapReq.put("to",to);
                mapReq.put("routeType",routeType);
                mapReq.put("fullShape","true");
                mapReq.put("unit","k");
                String reqStr = createQueryStringForParameters(mapReq);


                sentence = "" +
                     //   "GET /directions/v2/route?"+reqStr+" HTTP/1.1\n" +
                        "GET /directions/v2/route?to=50.40994635147456%2C30.52977371960878&unit=k&routeType=fastest&from=50.3886357%2C30.4935851&fullShape=true&key=geCwAnTQVkpj2ixbLJyHsLpnuZtG742A HTTP/1.1\n" +
                        "Host: www.mapquestapi.com\n" +
                        "Pragma: no-cache\n"+
                        "Connection: keep-alive\n" +
                        "Cache-Control: no-cache\n" +
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n" +
                        "User-Agent: Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36\n" +
                        "Accept-Encoding: gzip, deflate, sdch\n" +
                        "Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,uk;q=0.2,sr;q=0.2\n" +
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n" +
                        "Connection: keep-alive"+
                        "\r\n\r\n" ;

                //sentence+=reqStr;


                outToServer.writeBytes(sentence);
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = inFromServer.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                clientSocket.close();
                resp = stringBuilder.toString();

            }catch (Exception e){
                Log.e(TAG, "Auth +++ " + e.toString(), e);
            }


            return resp;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            loader.hide();
           // if(!onCancel) onCallBack(result);
        }

        @Override
        protected void onCancelled(){
            super.onCancelled();
            onCancel = true;
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

    class FromMapQuest extends AsyncTask<String, Void, String> {
        boolean onCancel=false;
        @Override
        protected String doInBackground(String... params) {
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

            //http://www.mapquestapi.com/directions/v2/route?key=geCwAnTQVkpj2ixbLJyHsLpnuZtG742A&from=50.3891,30.49373&to=50.446,30.44852&routeType=bicycle&unit=k&fullShape=true
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
            if(onCancel){
                return null;
            }
            String resParams = getResponseText(in);
            return resParams;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            loader.hide();
            if(!onCancel) onCallBack(result);
        }
        @Override
        protected void onCancelled(){
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
