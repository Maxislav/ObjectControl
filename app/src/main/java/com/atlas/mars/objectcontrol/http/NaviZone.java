package com.atlas.mars.objectcontrol.http;

import android.os.AsyncTask;
import android.util.Log;

import com.atlas.mars.objectcontrol.DataBaseHelper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
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
    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';
    private final String TAG = "naviZone";
    List<String> cookiesHeader;
    HashMap<String, String> mapSetting;
    String cookies;
    List<HashMap<String, String>> listCoocies;


    public NaviZone() {
        mapSetting = DataBaseHelper.hashSetting;
    }

    public void init() {
        //PhpSess au = new PhpSess();
        //au.execute(mapSetting.get(DataBaseHelper.MAP_LOGIN), mapSetting.get(DataBaseHelper.MAP_PASS), mapSetting.get(DataBaseHelper.MAP_SERVER_URL));
        //tryAuth();

        new MyTcp().execute();
    }

    public void tryAuth() {
        Auth au = new Auth();
        au.execute(mapSetting.get(DataBaseHelper.MAP_LOGIN), mapSetting.get(DataBaseHelper.MAP_PASS), mapSetting.get(DataBaseHelper.MAP_SERVER_URL));
    }

    class MyTcp extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String sentence;
                String modifiedSentence;
                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                Socket clientSocket = new Socket("navi.zone", 80); //http://178.62.44.54/
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

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
                sentence += "email=lmaxim%40mail.ru&password=gliderman&submit=";

                outToServer.writeBytes(sentence);

                StringBuilder stringBuilder = new StringBuilder();


                String line = "";
                while ((line = inFromServer.readLine()) != null) {
                    //str.match(/[^\=]+$/)
                    stringBuilder.append(line + "\n");
                    Pattern pattern = Pattern.compile("^(Set-Cookie:).+$");
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        String match = matcher.group();
                        cookies = match.replaceAll("^Set-Cookie:\\s", "");
                        logTrace("+++ COOKIES: " + cookies);
                        listCoocies = setCoocies(cookies);

                    }
                }

                // modifiedSentence = inFromServer.readLine();
                //System.out.println("FROM SERVER: " + modifiedSentence);
                clientSocket.close();
                logTrace("\r\nAUTH: \r\n" + stringBuilder.toString());

            } catch (Exception e) {
                Log.e(TAG, "Auth +++ " + e.toString(), e);
                // Log.e(TAG, "Auth +++ "+   Log.getStackTraceString(e));
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            new MyTcp2().execute();
        }
    }

    class MyTcp2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String resp = "";

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e(TAG, "InterruptedException +++ " + e.toString(), e);
            }
            try {
                String sentence;
                Socket clientSocket = new Socket("navi.zone", 80); //http://178.62.44.54/
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                sentence = "\r\nPOST /map/devices HTTP/1.0\n" +
                        "Cookie: " + "PHPSESSID=" + getCookie("PHPSESSID", listCoocies) + "\n" +
                        //  "Cookie: "+ "PHPSESSID=ui1l1u96336a6hlbluisa9gvv7\n"+
                        "host: navi.zone\n\n\n";
                sentence += "\n";
                logTrace("PHPSESSID=" + getCookie("PHPSESSID", listCoocies));
                outToServer.writeBytes(sentence);

                StringBuilder stringBuilder = new StringBuilder();


                String line = "";
                while ((line = inFromServer.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }


                clientSocket.close();
                resp = stringBuilder.toString();
                //logTrace("\r\nFROM SERVER: \r\n" + stringBuilder.toString());

            } catch (Exception e) {
                Log.e(TAG, "Auth +++ " + e.toString(), e);
                // Log.e(TAG, "Auth +++ "+   Log.getStackTraceString(e));
            }


            return resp;
        }

        protected void onPostExecute(String result) {
            logTrace("\r\n FROM Server: \r\n " + result);
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

                /*System.out.println("Key: " + entry.getKey() + " Value: "
                        + entry.getValue());*/
            }
        }
        return null;
    }


    class Auth extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            URL url;
            String response = "";
            try {
                url = new URL("http://navi.zone/user/login");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                logTrace("+++ doInBackground");
                /*String cookie = "";
                int count = cookiesHeader.size() - 1;
                for (int i = 0; i < cookiesHeader.size(); i++) {
                    cookie += cookiesHeader.get(i);
                    if (i != count) {
                        cookie += ";";
                    }
                }
                urlConnection.addRequestProperty("Cookie", cookie);*/
                // urlConnection.setUseCaches(true);


                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                urlConnection.setRequestProperty("Accept-Encoding", "identity");
                urlConnection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,uk;q=0.2,sr;q=0.2");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setRequestProperty("Connection", "keep-alive");
                urlConnection.setRequestProperty("Host", "navi.zone");
                urlConnection.setRequestProperty("Origin", "http://navi.zone");
                urlConnection.setRequestProperty("Referer", "http://navi.zone/");

                Map<String, String> mapParams = new HashMap<>();
                mapParams.put("email", "lmaxim@mail.ru");
                mapParams.put("password", "gliderman");
                mapParams.put("submit", "");
                String reqParm = "email=lmaxim%40mail.ru&password=gliderman&submit=";

                logTrace("Lenght =  " + String.valueOf(reqParm.getBytes().length));

                //
                String postParameters = createQueryStringForParameters(mapParams);
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postParameters.getBytes().length));
                urlConnection.setRequestProperty("Origin", "http://navi.zone");
                urlConnection.setRequestProperty("Pragma", "no-cache");
                urlConnection.setRequestProperty("Referer", "http://navi.zone/");
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.94 Safari/537.36");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Connection", "close");
                /*if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                    urlConnection.setRequestProperty("Connection", "close");
                }*/
                logTrace(postParameters);

                // urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);

                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();

                Map<String, List<String>> headers = urlConnection.getHeaderFields();
                cookiesHeader = headers.get("Set-Cookie");

                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {

                    // throw some exception
                }

                logTrace("" + statusCode);

               /* Map<String, List<String>> headers = urlConnection.getHeaderFields();
                if (headers != null) {
                    cookiesHeader = headers.get("Set-Cookie");
                }*/


               /* Scanner inStream = new Scanner(urlConnection.getInputStream());
                while (inStream.hasNextLine()) {
                    response += (inStream.nextLine());
                }
*/

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Auth +++ " + e.toString(), e);
                Log.e(TAG, "Auth +++ " + Log.getStackTraceString(e));

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            Log.d(TAG, "response +++ " + response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }


    class PhpSess extends AsyncTask<String, Void, Boolean> {
        CookieManager msCookieManager = new CookieManager();

        @Override
        protected Boolean doInBackground(String... params) {
            //List<NameValuePair> _params = new LinkedList<NameValuePair>();
            // params.add(new BasicNameValuePair("login", "demo"));

            URL url = null;
            InputStream in = null;
            String response = "";
            try {
                // url = new URL("http://navi.zone/user/login");
                url = new URL("http://navi.zone/user/login");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                //  urlConnection.setUseCaches(true);
                // urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
               /* Map<String, String> mapParams = new HashMap<>();
                mapParams.put("email","lmaxim@mail.ru");
                mapParams.put("password","gliderman");
                String postParameters = createQueryStringForParameters(mapParams);

                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();
*/


                Map<String, List<String>> headers = urlConnection.getHeaderFields();
                cookiesHeader = headers.get("Set-Cookie");
                for (String c : cookiesHeader) {
                    logTrace(c);
                }

             /*   Scanner inStream = new Scanner(urlConnection.getInputStream());
                while(inStream.hasNextLine()){
                    response+=(inStream.nextLine());
                }*/
                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    return false;
                    // throw some exception
                }


            } catch (IOException e) {
                e.printStackTrace();
                logTrace(e.toString(), 1);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }


            return true;//getResponseText(in);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                tryAuth();
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

    private void logTrace(String mess, Integer code) {
        Log.d(TAG, (code == null ? "0" : code) + ": " + mess);
    }

    private void logTrace(String mess) {
        Log.d(TAG, "0: " + mess);
    }
}
