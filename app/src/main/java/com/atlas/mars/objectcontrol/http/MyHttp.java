package com.atlas.mars.objectcontrol.http;

import android.os.AsyncTask;
import android.util.Log;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.atlas.mars.objectcontrol.MainActivity;
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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Администратор on 4/26/15.
 */
public class MyHttp {
    MapsActivity mapsActivity;
    static ObjectMapper mapper = new ObjectMapper();
    private static final String TAG = "myLog";

    MyTask mt;
    DataBaseHelper db;
    HashMap<String, String> mapSetting;
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

        mt = new MyTask(mapsActivity);
        if (mapSetting.get(db.MAP_LOGIN) != null && mapSetting.get(db.MAP_PASS) != null && mapSetting.get(db.MAP_SERVER_URL) != null) {
            mt.execute(mapSetting.get(db.MAP_LOGIN), mapSetting.get(db.MAP_PASS), mapSetting.get(db.MAP_SERVER_URL));
        }
    }

    public  void resData(String json) {
        ArrayList<HashMap> arrayListObjects = new ArrayList<>();
        try {
            ObjectNode root = (ObjectNode) mapper.readTree(json);
            ArrayNode rows = (ArrayNode) root.get("rows");
            for (JsonNode jsonNode : rows) {
                HashMap<String, String> map = new HashMap<>();
                Log.d(TAG, jsonNode.path("CarName").asText());
                map.put("name", jsonNode.path("CarName").asText());
                map.put("lat", jsonNode.path("X").asText());
                map.put("lng", jsonNode.path("Y").asText());
                map.put("id", jsonNode.path("CarId").asText());

                JsonNode arrayDateCar =  new ObjectMapper().readTree(jsonNode.path("DateCar").asText());
                if(arrayDateCar.isArray())
                map.put("date", arrayDateCar.get(0).path("DateCar").asText());

                Log.d(TAG, "" + map.get("name") + " " + map.get("lat") + ":" + map.get("lng"));
                arrayListObjects.add(map);
            }
            mapsActivity.setObjectMarkers(arrayListObjects);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class MyTask extends AsyncTask<String, Void, String> {
        MapsActivity mapsActivity;

        MyTask(MapsActivity mapsActivity){
            super();
            this.mapsActivity = mapsActivity;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // tvInfo.setText("Begin");
        }

        @Override
        protected String doInBackground(String... _params) {

            String resText = null;
            String login = _params[0];
            String pass = _params[1];
            String urlAuth = _params[2] + "/login.php";
            String urlStateObj = _params[2] + "/loadevents.php?param=icars";

            httpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
            httpClient.getParams().setParameter("http.protocol.content-charset", "cp1251");


            Log.d(TAG, "+++ doInBackground");
            // httpClient = new DefaultHttpClient();
            @SuppressWarnings("deprecation")
            HttpPost httpPost = new HttpPost(urlAuth);
            @SuppressWarnings("deprecation")
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);

            nameValuePair.add(new BasicNameValuePair("login", login));
            nameValuePair.add(new BasicNameValuePair("password", pass));
            //Encoding POST data
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
                Log.d(TAG, "Http Post Response2: +++ " + content.toString());

                ObjectNode root = (ObjectNode) mapper.readTree(content);

                JsonNode successNode = root.path("success");
                boolean res = successNode.asBoolean();

                Log.d(TAG, "nameNode +++" + res);


            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.e(TAG, "ClientProtocolException++ " + e.toString());

            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
                Log.e(TAG, "IOException ++ " + e.toString());
            }

            HttpGet httpGet = new HttpGet(urlStateObj);
            httpGet.setHeader("charset", "windows-1251");
            try {
                //  httpClient.bodyString("Important stuff", ContentType.create("text/plain", Consts.UTF_8))

                HttpResponse response = httpClient.execute(httpGet);

                // HttpEntity entity = response.getEntity();

                String responseBody = EntityUtils.toString(response.getEntity(), "UTF8");


                // String utf8String= new String(res.getBytes("UTF-8"), "windows-1251");;
                String utf8String = new String(responseBody.getBytes("Cp1251"), "UTF-8");
                //String utf8String= new String(res.getBytes("UTF-8"), "windows-1251");;
                resText = utf8String;
                // String content = EntityUtils.toString(entity, "ISO-8859-1");
/*
               // Log.d(TAG, "Http Post Response4: +++ " + content.toString());
               // resText =  content.toString();
                resText =  responseBody;
                ObjectNode root = (ObjectNode) mapper.readTree(content);
                ArrayNode rows = (ArrayNode) root.get("rows");
                rows.size();

                Log.d(TAG, "+++ " + rows.size());*/


            } catch (IOException e) {
                Log.e(TAG, "IOException ++ " + e.toString());
            }
            return resText;
        }

        @Override
        protected void onPostExecute(String result) {
            // super.onPostExecute(result);
            resData(result);
            //  mapsActivity.toastShow(result);
            // tvInfo.setText("End");
        }
    }

}
