package com.atlas.mars.objectcontrol.http;

import android.os.AsyncTask;
import android.util.Log;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.atlas.mars.objectcontrol.geoMath.GeoMath;
import com.atlas.mars.objectcontrol.gps.MapsActivity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;


/**
 * Created by Администратор on 4/26/15.
 */
public class M2Http {
    MapsActivity mapsActivity;
    static ObjectMapper mapper = new ObjectMapper();
    private static final String TAG = "myLog";
    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';

    getPointsAsync mt;
    _Auth au;
    DataBaseHelper db;
    HashMap<String, String> mapSetting;
    HttpClient httpClient;
    private Timer mTimer;
    private boolean doIt = false;
    private boolean goRecursion = true;
    HttpURLConnection urlConnection;
    HttpURLConnection urlConnection2;
    List<String> cookiesHeader;


    public  void stopTimer(){
        if (mTimer != null) {
            mTimer.cancel();
        }
        goRecursion = false;


    }

    public void onResume(){
        goRecursion = true;
        postData();
    }
    public void onPause(){
        goRecursion = false;
    }

    public M2Http(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
        db = new DataBaseHelper(mapsActivity);
        mapSetting = db.hashSetting;
        CookieManager msCookieManager = new CookieManager();
        CookieHandler.setDefault(msCookieManager);
    }

    public void postData() {
        if(urlConnection==null){
            getAuth();
        }else{
            getPoints();
        }
    }

    public void getAuth(){
        String LOGIN, PASS, SERVERURL;
        LOGIN = mapSetting.get(db.MAP_LOGIN);
        PASS = mapSetting.get(db.MAP_PASS);
        SERVERURL = mapSetting.get(db.MAP_SERVER_URL);
        if (LOGIN != null && PASS!= null && SERVERURL != null && !SERVERURL.isEmpty()) {
            au = new _Auth();
            au.execute(mapSetting.get(db.MAP_LOGIN), mapSetting.get(db.MAP_PASS), mapSetting.get(db.MAP_SERVER_URL));
        }
    }

    private void getPoints(){
        Log.d(TAG, "Http Post Response2: +++ ");
        GetPoints mt = new GetPoints(mapsActivity);
        mt.execute(mapSetting.get(db.MAP_SERVER_URL) + "/loadevents.php?param=icars");
    }




    public  void resData(String json) {
        ArrayList<HashMap> arrayListObjects = new ArrayList<>();
        GeoMath geoMath = new GeoMath();

        try {
            ObjectNode root = (ObjectNode) mapper.readTree(json);
            ArrayNode rows = (ArrayNode) root.get("rows");
            for (JsonNode jsonNode : rows) {
                HashMap<String, String> map = new HashMap<>();
               // Log.d(TAG, jsonNode.toString());
                map.put("name", jsonNode.path("CarName").asText());
                map.put("lat", jsonNode.path("X").asText());
                map.put("lng", jsonNode.path("Y").asText());
                map.put("id", jsonNode.path("CarId").asText());
                map.put("gps_level", jsonNode.path("gps_level").asText());
                map.put("bat_level", jsonNode.path("bat_level").asText());

                double lat1 = jsonNode.path("X").asDouble();
                double lng1 = jsonNode.path("Y").asDouble();
                double lat2 = jsonNode.path("pX").asDouble();
                double lng2 = jsonNode.path("pY").asDouble();

                double azimuth = geoMath.toAthimuth(lat2,lng2,lat1,lng1);
                if(!Double.isNaN(azimuth)){
                    //Log.d(TAG,"azimuz ++ "+azimuth);
                    map.put("azimuth", ""+(int) Math.round(azimuth));
                }

                JsonNode arrayDateCar =  new ObjectMapper().readTree(jsonNode.path("DateCar").asText());
                String string_date = null;
                if(arrayDateCar.isArray()){
                    string_date = arrayDateCar.get(0).path("DateCar").asText();
                }

                double speed = jsonNode.path("SpeedV").asDouble();
                speed = speed*1.61;
                map.put("speed", ""+(int)speed);

                SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                Date d = null;
                try {
                    d = f.parse(string_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat df2 = new SimpleDateFormat("dd.MM.yyyy");
                SimpleDateFormat df3 = new SimpleDateFormat("HH:mm:ss");
                String dateText = df2.format(d);
                String dateTime = df3.format(d);
                if(d!=null){
                    //string_date = ""+ d.getTime();
                    map.put("date", dateText);
                    map.put("time", dateTime);
                    map.put("dateLong", "" + d.getTime());
                }
               // Log.d(TAG, "" + map.get("name") + " " + map.get("lat") + ":" + map.get("lng"));
                arrayListObjects.add(map);
            }
            mapsActivity.setObjectMarkers(arrayListObjects);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getPoints();

    }

    class _Auth extends  AsyncTask<String, Void, String>{

        CookieManager msCookieManager = new CookieManager();

        @Override
        protected String doInBackground(String... params) {
            //List<NameValuePair> _params = new LinkedList<NameValuePair>();
           // params.add(new BasicNameValuePair("login", "demo"));

            URL url = null;
            InputStream in = null;
            String response = "";
            try {
                url = new URL("http://gps-tracker.com.ua/login.php");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
              //  urlConnection.setUseCaches(true);
               // urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                Map<String, String> mapParams = new HashMap<>();
                mapParams.put("login","demo");
                mapParams.put("password","accepted");
                String postParameters = createQueryStringForParameters(mapParams);

                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();

                Map<String, List<String>> headers = urlConnection.getHeaderFields();
                cookiesHeader = headers.get("Set-Cookie");

                Scanner inStream = new Scanner(urlConnection.getInputStream());
                while(inStream.hasNextLine()){
                    response+=(inStream.nextLine());
                }
                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    // throw some exception
                }


            } catch (IOException e) {
               // in = null;
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                  //  urlConnection.disconnect();
                }
            }


            return response;//getResponseText(in);
        }
        @Override
        protected void onPostExecute(String result) {
            ObjectNode root = null;
            if(result == null){
                Log.d(TAG, "result = null"+ result);
                mapsActivity.toastShow("Unable to connection");
                return;
            }
            Log.d(TAG, "++++++"+ result);
            try {
                root = (ObjectNode) mapper.readTree(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonNode successNode = root.path("success");
            boolean res = successNode.asBoolean();
            if(res){
                getPoints();
            }
        }

    }

    class GetPoints extends AsyncTask<String, Void, String> {
        MapsActivity mapsActivity;
        GetPoints(MapsActivity mapsActivity){
            super();
            this.mapsActivity = mapsActivity;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // tvInfo.setText("Begin");
        }

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            String response = "";

            try {
                url = new URL(params[0]);
              //  url.
              //  url.

                urlConnection = (HttpURLConnection) url.openConnection();
                String cookie = "";
                int count = cookiesHeader.size();
                int i = 0;
                for(String value: cookiesHeader){
                    cookie+=value;
                    if(i!=count){
                        cookie+=" ;";
                    }
                    i++;
                }
                urlConnection.addRequestProperty("Cookie", cookie);
                Scanner inStream = new Scanner(urlConnection.getInputStream());
                while(inStream.hasNextLine()){
                    response+=(inStream.nextLine());
                }



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            if(goRecursion){
                resData(result);
            }
        }
    }


    class getPointsAsync extends AsyncTask<String, Void, String> {
        MapsActivity mapsActivity;

        getPointsAsync(MapsActivity mapsActivity){
            super();
            this.mapsActivity = mapsActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // tvInfo.setText("Begin");
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                if(doIt){
                    Thread.sleep(5000);
                }else{
                    doIt = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Get points ++ ");
            String urlStateObj = params[0];// + "/loadevents.php?param=icars";
            String resText = null;
            HttpGet httpGet = new HttpGet(urlStateObj);
            httpGet.setHeader("charset", "windows-1251");
            try {

                HttpResponse response = httpClient.execute(httpGet);
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF8");
                String utf8String = new String(responseBody.getBytes("Cp1251"), "UTF-8");
                resText = utf8String;
            } catch (IOException e) {
                Log.e(TAG, "IOException ++ " + e.toString());
            }
            return resText;
        }

        @Override
        protected void onPostExecute(String result) {
            if(goRecursion){
                resData(result);
            }
        }
    }

    public static String createQueryStringForParameters(Map<String, String> parameters) {
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


    class  Auth extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            httpClient = new DefaultHttpClient();
            String resText = null;
            String login = params[0];
            String pass = params[1];
            String urlAuth = params[2] + "/login.php";
            httpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
            httpClient.getParams().setParameter("http.protocol.content-charset", "cp1251");
            Log.d(TAG, "+++ doInBackground");
            //  @SuppressWarnings("deprecation")
            HttpPost httpPost = new HttpPost(urlAuth);
            @SuppressWarnings("deprecation")
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("login", login));
            nameValuePair.add(new BasicNameValuePair("password", pass));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e(TAG, "UnsupportedEncodingException+++" + e.toString());
            }
            try {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity);
                resText = content.toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.e(TAG, "ClientProtocolException++ " + e.toString());

            } catch (IOException e) {
                e.printStackTrace();
                resText = null;
                Log.e(TAG, "IOException ++ " + e.toString());
            }

            return resText;
        }
        @Override
        protected void onPostExecute(String result) {
            // super.onPostExecute(result);
            ObjectNode root = null;
            if(result!=null){
                try {
                    root = (ObjectNode) mapper.readTree(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JsonNode successNode = root.path("success");
                boolean res = successNode.asBoolean();
                if(res){
                    getPoints();
                }
            }

        }
    }

}
