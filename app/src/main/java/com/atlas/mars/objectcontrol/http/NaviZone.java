package com.atlas.mars.objectcontrol.http;

import android.os.AsyncTask;
import android.util.Log;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.atlas.mars.objectcontrol.gps.MapsActivity;
import com.fasterxml.jackson.core.io.UTF8Writer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mars on 7/3/15.
 */
public class NaviZone {
    HttpURLConnection urlConnection;
    MapsActivity mapsActivity;
    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';
    private final String TAG = "naviZone";
    List<String> cookiesHeader;
    HashMap<String, String> mapSetting;
    String cookies;
    List<HashMap<String, String>> listCoocies;
    List<String> idDevs;
    boolean recursy = false;
    MyTcp2 myTcp2;
    DataBaseHelper db;


    public NaviZone(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
        db = new DataBaseHelper(mapsActivity);
        mapSetting = DataBaseHelper.hashSetting;
        idDevs = new ArrayList<>();
    }

    public void init() {
        new MyTcp().execute();
    }

    public void onResume(){
        new MyTcp().execute();
    }

    public  void  onPause(){

    }



    private void getDevices() {
      /*  synchronized (myTcp2) {

        }*/
        myTcp2 = new MyTcp2();
        myTcp2.execute();

    }

  /*  public void tryAuth() {
        Auth au = new Auth();
        au.execute(mapSetting.get(DataBaseHelper.MAP_LOGIN), mapSetting.get(DataBaseHelper.MAP_PASS), mapSetting.get(DataBaseHelper.MAP_SERVER_URL));
    }*/

    class MyTcp extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String login = mapSetting.get(db.MAP_LOGIN);
            String pass = mapSetting.get(db.MAP_PASS);
            Map <String, String> mapReqParam = new HashMap<>();
            mapReqParam.put("email", login);
            mapReqParam.put("password", pass);
            mapReqParam.put("submit", "");
            String stringReqParam = createQueryStringForParameters(mapReqParam);


            String resline = null;
            try {
                String sentence;
              //  String modifiedSentence;
              //  BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                Socket clientSocket = new Socket("navi.zone", 80); //http://178.62.44.54/
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());


                //sentence = "POST /data/login?login=max&password=demo HTTP/1.0\n" +"host: 192.168.126.73\n\n\n";
                //sentence = "\r\nPOST /user/login?email=lmaxim%40mail.ru&password=gliderman&submit= HTTP/1.0\n" +
                sentence = "" +
                        "POST /user/login HTTP/1.1\n" +
                        "Host: navi.zone\n" +
                        "Connection: keep-alive\n" +
                        "Content-Length: 49\n" +
                        "Pragma: no-cache\n" +
                        "Cache-Control: no-cache\n" +
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n" +
                        "Origin: http://navi.zone\n" +
                        "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.94 Safari/537.36\n" +
                        "Content-Type: application/x-www-form-urlencoded\n" +
                        "Referer: http://navi.zone/\n" +
                        "Accept-Encoding: gzip,deflate\n" +
                        "Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,uk;q=0.2,sr;q=0.2\n" +
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n" +
                        "Connection: keep-alive\n" +
                        "Host: navi.zone\r\n\r\n";

                //sentence += "email=lmaxim%40mail.ru&password=gliderman&submit=";
                sentence += stringReqParam;

                outToServer.writeBytes(sentence);

                StringBuilder stringBuilder = new StringBuilder();

                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));




                String line = "";
                while ((line = inFromServer.readLine()) != null ) {
                    stringBuilder.append(line + "\n");
                    Pattern pattern = Pattern.compile("^(Set-Cookie:).+$");
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        String match = matcher.group();
                        cookies = match.replaceAll("^Set-Cookie:\\s", "");
                        listCoocies = setCoocies(cookies);
                    }
                    if(line.isEmpty()){
                        break;
                    }
                }
                resline = stringBuilder.toString();
                clientSocket.close();

            } catch (Exception e) {
                Log.e(TAG, "Auth Exception +++ " + e.toString(), e);
            }
            return resline;
        }

        @Override
        protected void onPostExecute(String result) {
            logTrace("\nAUTH RESPONSE  \n" + result);
            getDevices();
        }
    }

    class MyTcp2 extends AsyncTask<String, Void, String> {
        Socket clientSocket;

        MyTcp2() {
            super();


        }


        @Override
        protected String doInBackground(String... params) {
            String resp = "";





            try {
                String sentence;
                clientSocket = new Socket("navi.zone", 80); //http://178.62.44.54/
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // sentence = "\r\nPOST /map/devices HTTP/1.0\n" +
                sentence = "" +
                        "POST /map/devices HTTP/1.0\n"
                        + "Cookie: " + "PHPSESSID=" + getCookie("PHPSESSID", listCoocies) + "\n"
                        + "host: navi.zone\n";
                sentence += "\r\n\r\n";
                //  logTrace("PHPSESSID=" + getCookie("PHPSESSID", listCoocies));

               // outToServer.writeUTF(sentence); // отсылаем введенную строку текста серверу.
                //outToServer.flush();
                outToServer.writeBytes(sentence);
                outToServer.flush();


                StringBuilder stringBuilder = new StringBuilder();


                String line = "";
                boolean head = true;
                while ((line = inFromServer.readLine()) != null) {
                    if (line.isEmpty()) {
                        head = false;
                    }
                    if (!head) {
                        stringBuilder.append(line + "\n");
                    }
                }
                outToServer.close();
                clientSocket.close();
//                clientSocket.shutdownInput();
                //              clientSocket.shutdownOutput();
                //clientSocket = null;

                resp = stringBuilder.toString();
                if (resp != null) {
                    resp = resp.replaceAll("\\n", "");
                }
                //HTTP/1.1 200 OKServer: nginx/1.0.15Date: Fri, 10 Jul 2015 14:30:48 GMTContent-Type: text/htmlConnection: closeX-Powered-By: PHP/5.4.34Expires: Thu, 19 Nov 1981 08:52:00 GMTCache-Control: no-store, no-cache, must-revalidate, post-check=0, pre-check=0Pragma: no-cache{"devices":[{"d_id":"49","d_type":"2","d_imei":"353492049085856","d_title":"\u043c\u0430\u0448\u0438\u043d\u0430 1","d_status":"2","d_tracking":null,"d_icon":"0.png","t_id":null,"t_time_text":null,"t_time":null,"t_time_diff":null,"t_longitude":null,"t_latitude":null,"t_altitude":null,"t_satellite":0,"t_speed":null,"t_stop_time":null,"icons":{"is_online":false,"online_class":"icon-offline","move_class":"icon-stop","move_text":"\u0441\u0442\u043e\u0438\u0442 \u0432 \u0442\u0435\u0447\u0435\u043d\u0438\u0435 "},"sat_state":"red","power":"acum-empty"}],"success":true}
//
                //logTrace("\r\nFROM SERVER: \r\n" + stringBuilder.toString());

            } catch (Exception e) {
                Log.e(TAG, "Devices Exception +++ " + e.toString(), e);
                // Log.e(TAG, "Auth +++ "+   Log.getStackTraceString(e));
            }
            return resp;
        }

        protected void onPostExecute(String result) {
            logTrace("\r\n Devices response: \r\n " + result);
            //doInBackground();
             new MyTcp3().execute(result);
        }
    }

    class MyTcp3 extends AsyncTask<String, Void, String> {
        ObjectMapper mapper = new ObjectMapper();

        MyTcp3() {
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                if(recursy){
                    Thread.sleep(5000);
                }else {
                    Thread.sleep(500);
                    recursy = true;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e(TAG, "InterruptedException +++ " + e.toString(), e);
            }

            try {
                ObjectNode root = (ObjectNode) mapper.readTree(params[0]);
                ArrayNode devices = (ArrayNode) root.get("devices");
                for (JsonNode device : devices) {
                    String idDev = device.path("d_id").asText();
                    idDevs.add(idDev);
                }

            } catch (Exception e) {
                Log.e(TAG, "ObjectNode Exception +++ " + e.toString(), e);
            }

            Map<String, String> reqParmMap = new HashMap<>();
            try {
                reqParmMap.put(URLEncoder.encode("device[0][device]", "UTF-8"), idDevs.get(0));
                reqParmMap.put(URLEncoder.encode("device[0][lastId]", "UTF-8"), "");
                reqParmMap.put(URLEncoder.encode("device[0][loadTrack]",  "UTF-8"), "0");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String reqString = createQueryStringForParameters(reqParmMap);
            int contentLength = reqString.getBytes().length;


            String resp = "";
            try {
                String sentence;
                Socket clientSocket = new Socket("navi.zone", 80); //http://178.62.44.54/
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // sentence = "\r\nPOST /map/devices HTTP/1.0\n" +
                sentence = "" +
                        "POST /map/deviceTrack HTTP/1.1\n" +
                        "host: navi.zone\n" +
                        "Connection: keep-alive\n" +
                        "Content-Length: " + contentLength + "\n" +
                        "Pragma: no-cache\n" +
                        "Cache-Control: no-cache\n" +
                        "X-Requested-With: XMLHttpRequest\n"+
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n" +
                        "Origin: http://navi.zone\n" +
                        "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.94 Safari/537.36\n" +
                        "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\n" +
                        "Referer: http://navi.zone/map\n" +
                        "Accept-Encoding: gzip,deflate\n" +
                        "Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,uk;q=0.2,sr;q=0.2\n" +
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n" +
                        "Connection: keep-alive\n" +
                        "Cookie: " + "PHPSESSID=" + getCookie("PHPSESSID", listCoocies) +
                        //"Host: navi.zone" +
                        "\n\n";
                sentence += reqString;

                outToServer.writeBytes(sentence);
                outToServer.flush();
                StringBuilder stringBuilder = new StringBuilder();


                String line = "";
                boolean head = true;
                boolean found = false;
                Pattern p = Pattern.compile("\\{.+\\}");
                while ((line = inFromServer.readLine()) != null) {
                    if (line.isEmpty()) {
                        head = false;
                    }
                    if (!head) {

                        Matcher m = p.matcher(line);
                        if(m.matches()){
                            stringBuilder.append(line);
                            break;
                        }

                    }
                }
                outToServer.close();
                inFromServer.close();
//                clientSocket.shutdownInput();
          //      clientSocket.shutdownOutput();
                clientSocket.close();
                resp = stringBuilder.toString();

               /* if (resp != null) {
                    resp = resp.replaceAll("\\n", "");
                    resp = resp.replaceAll("^.+?\\{", "{");
                    resp = resp.replaceAll("\\}([^\\}])+$", "}");
                }*/
            } catch (Exception e) {
                Log.e(TAG, "DevicesTrack Exception +++ " + e.toString(), e);
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            logTrace("\r\n DevicesTrack response: \r\n " + result);
            new MyTcp3().execute(result);
            new Parser().execute(result);
        }
    }


    class Parser extends AsyncTask<String, Void, ArrayList<HashMap>> {



        @Override
        protected ArrayList<HashMap> doInBackground(String... params) {
            ArrayList<HashMap> arrayListObjects = new ArrayList<>();

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = null;
            //[{"d_id":"49","d_type":"2","d_imei":"353492049085856","d_title":"машина 1","d_status":"2","d_tracking":null,"d_icon":"0.png","t_id":null,"t_time_text":null,"t_time":null,"t_time_diff":null,"t_longitude":null,"t_latitude":null,"t_altitude":null,"t_satellite":null,"t_speed":null,"t_stop_time":null,"icons":{"is_online":false,"online_class":"icon-offline","move_class":"icon-stop","move_text":"стоит в течение "},"sat_state":"red","io":{"values":[]},"power":"acum-empty"}]
            try {
                root = (ObjectNode) mapper.readTree(params[0]);
                ArrayNode devices = (ArrayNode) root.get("devices");
                for (JsonNode device : devices) {
                    HashMap<String, String> map = new HashMap<>();
                    String idDev = device.path("d_id").asText();
                    String imei = device.path("d_imei").asText();

                    map.put("id", imei);
                    map.put("name", device.path("d_title").asText());
                    if(device.path("t_latitude").asText().equals("null")){
                        map.put("lat", "");
                    }else{
                        map.put("lat", device.path("t_latitude").asText());
                    }

                    if(device.path("t_longitude").asText().equals("null")){
                        map.put("lng", "");
                    }else{
                        map.put("lng", device.path("t_longitude").asText());
                    }

                    map.put("speed", device.path("t_speed").asText());
                    map.put("dateLong", device.path("t_time").asText());
                    map.put("date", device.path("t_time_text").asText());
                    map.put("time", device.path("t_time_text").asText());
                    map.put("gps_level", device.path("t_satellite").asText());
                    map.put("bat_level", device.path("power").asText());




                    arrayListObjects.add(map);
                }


            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "DevicesTrack Exception +++ " + e.toString(), e);
            }



            return arrayListObjects;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap> arrayListObjects) {
            mapsActivity.setObjectMarkers(arrayListObjects);
        }
    }


    private List<HashMap<String, String>> setCoocies(String cookies) {
        List<HashMap<String, String>> list = new ArrayList<>();

        String[] arrCooc = cookies.split(";");
        for (int i = 0; i < arrCooc.length; i++) {
            String key = arrCooc[i].split("=")[0];
            key = key.replaceAll("^\\s", "");
            String value = arrCooc[i].split("=")[1];
            HashMap<String, String> map = new HashMap<>();
            map.put(key, value);
            list.add(map);
        }
        return list;
    }

    private String getCookie(String key, List<HashMap<String, String>> list) {
        for (HashMap<String, String> map : list) {

            for (Map.Entry entry : map.entrySet()) {
                if (entry.getKey().equals(key)) {
                    return entry.getValue().toString();
                }
            }
        }
        return null;
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

    private void logTrace(String mess, Integer code) {
        Log.d(TAG, (code == null ? "0" : code) + ": " + mess);
    }

    private void logTrace(String mess) {
        Log.d(TAG, "0: " + mess);
    }
}
