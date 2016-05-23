package com.atlas.mars.objectcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


/**
 * Created by mars on 4/1/15.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase sdb;


    private static final String TAG = "myLog";
    private static final String DATABASE_NAME = "obcon.db";
    private static final int DATABASE_VERSION = 19;

    private static final String TABLE_NAME_DEVICES = "devices";
    private static final String TABLE_NAME_COMMANDS = "commands";
    private static final String TABLE_NAME_HISTORY = "history";
    private static final String TABLE_NAME_SETTING = "setting";
    private static final String TABLE_TRACK_COLLECTION = "trackCollection";
    private static final String TABLE_TRACKS = "traks";


    public static final String UID = "_id";
    public static final String VALUE_NAME = "valueName";
    public static final String VALUE_NAME_DEVICE = "valueNameDevice";
    public static final String VALUE_PHONE = "valuePhone";
    public static final String VALUE_PARAM = "valueParam";
    public static final String VALUE_COMMAND = "valueCommand";
    public static final String VALUE_ID_DEVICE = "valueIdDevice";
    public static final String VALUE_FAVORITE = "valueFavorite";
    public static final String VALUE_DATE = "valueDate";
    public static final String VALUE_ID_COMMAND = "valueIdCommand";
    public static final String VALUE_DELIVERED = "valueDelivered";
    public static final String VALUE_NAME_SETTING_CODE = "valueNameSetting"; //confirmSend || multipleSend ||  countMemoryHistory || countDisplayHistory
    public static final String VALUE_PARAMETER_SETTING = "valueParameterSetting";

    public static final String CONFIRM_SEND = "confirmSend";
    public static final String MULTIPLE_SEND = "multipleSend";
    public static final String COUNT_MEMORY_HISTORY = "countMemoryHistory";
    public static final String COUNT_DISPLAY_HISTORY = "countDisplayHistory";
    public static final String MAP_LOGIN = "mapLogin";
    public static final String MAP_PASS = "mapPass";
    public static final String MAP_SERVER_URL = "mapServerUrl";
    public static final String START_ON_MAP_ACTIVITY = "startOnMapActivity"; //старт на активности карты
    public static final String STORAGE_PATH_TILES = "storagePathTiles"; //путь хранения тайлов карт


    public static final String MAP_TYPE = "mapType";
    public static final String MAP_START_LAT = "startLat";
    public static final String MAP_START_LNG = "startLng";
    public static final String MAP_START_ZOOM = "startZoom";
    public static final String MAP_SHOW_LIST = "mapShowList";
    public static final String MAP_ROUTE_TYPE = "mapRouteType";
    public static  final String PROTOCOL_TYPE = "protocolType";
    public static  final String DISTANCE = "distance";

    public static final String MAP_CURRENT_ID_TRACK = "mapCurrentIdTrack"; //Некущий отображаемый трек

    public static HashMap<String, String> hashSetting;


    public static final String VALUE_SELECTED = "valueSelected";

    private static final String SQL_CREATE_TABLE_DEVICES = "CREATE TABLE if not exists "
           + TABLE_NAME_DEVICES +" (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VALUE_NAME + " VARCHAR(255), " + VALUE_PHONE + " VARCHAR(255), " + VALUE_PARAM + " VARCHAR(255), "+ VALUE_SELECTED +" VARCHAR(255) "+ ");";

    private static final String SQL_CREATE_TABLE_COMMANDS = "CREATE TABLE if not exists "
            +TABLE_NAME_COMMANDS+" (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VALUE_NAME + " VARCHAR(255), " + VALUE_COMMAND +  " VARCHAR(255), " + VALUE_ID_DEVICE +" INTEGER, " +VALUE_FAVORITE+ " INTEGER"+ ");";

    private static final String SQL_CREATE_TABLE_HISTORY = "CREATE TABLE if not exists "
            +TABLE_NAME_HISTORY+ " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VALUE_DATE + " TIMESTAMP, " + VALUE_ID_COMMAND + " INTEGER, " +VALUE_DELIVERED + " INTEGER"+");";

    private static final String SQL_CREATE_TABLE_SETTING = "CREATE TABLE if not exists "
            +TABLE_NAME_SETTING+ " ("+ UID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VALUE_NAME_SETTING_CODE + " VARCHAR(255), " + VALUE_PARAMETER_SETTING +  " VARCHAR(255) " + ");";

    private static final String SQL_CREATE_TABLE_TRACK_COLLECTION = "CREATE TABLE if not exists "
            +TABLE_TRACK_COLLECTION+ " ("+ UID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "name" + " VARCHAR(255), " + "date" +  " TIMESTAMP, " + DISTANCE +" VARCHAR(255) "+ ");";

    private static final String SQL_CREATE_TABLE_TRACKS = "CREATE TABLE if not exists "
            +TABLE_TRACKS+ " ("+ UID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "trackId" + " INTEGER, lat DOUBLE, lng DOUBLE, date TIMESTAMP);";

    private static final String UPDATE_TRACK_COLLECTION = "ALTER TABLE "+TABLE_TRACK_COLLECTION+" ADD COLUMN "+DISTANCE+" VARCHAR(255);";




    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if(hashSetting==null){
            hashSetting = new HashMap<>();
            getSetting(hashSetting);
        }
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_DEVICES);
        db.execSQL(SQL_CREATE_TABLE_COMMANDS);
        db.execSQL(SQL_CREATE_TABLE_HISTORY);
        db.execSQL(SQL_CREATE_TABLE_SETTING);
        db.execSQL(SQL_CREATE_TABLE_TRACK_COLLECTION);
        db.execSQL(SQL_CREATE_TABLE_TRACKS);
        fillSetting(CONFIRM_SEND, "1",db);
        fillSetting(MULTIPLE_SEND, "0", db);
        fillSetting(COUNT_MEMORY_HISTORY, "100", db);
        fillSetting(COUNT_DISPLAY_HISTORY, "10", db);
        fillDefaultParam(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_CREATE_TABLE_TRACK_COLLECTION);
        db.execSQL(SQL_CREATE_TABLE_TRACKS);
        db.execSQL(UPDATE_TRACK_COLLECTION);

        //String upgradeQuery = "ALTER TABLE mytable ADD COLUMN mycolumn TEXT;";

       /* String query = "ALTER TABLE "+TABLE_NAME_HISTORY+" ADD COLUMN "+VALUE_DELIVERED+" INTEGER";
        db.execSQL(query);
        query = "UPDATE " + TABLE_NAME_HISTORY+" SET "+VALUE_DELIVERED+"="+0;
        db.execSQL(query);*/

    }

    public long addNewDevice(String name, String phone){
        sdb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(VALUE_NAME, name );
        cv.put(VALUE_PHONE, phone );
        cv.put(VALUE_SELECTED, "0" );
        long id = sdb.insert(TABLE_NAME_DEVICES, null, cv);
        sdb.close();
        Log.d(TAG, "addNewDevice" + id + "");
        return id;
    }

    public ArrayList<HashMap> getListDevices(){
        ArrayList<HashMap> arrayList = new ArrayList<HashMap>();
        SQLiteDatabase sdb = this.getWritableDatabase();
        String jquery = "SELECT * FROM "+this.TABLE_NAME_DEVICES;
        Cursor cursor = sdb.rawQuery(jquery,null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(this.VALUE_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(this.VALUE_PHONE));
            String id = cursor.getString(cursor.getColumnIndex(UID));
            String selected = cursor.getString(cursor.getColumnIndex(VALUE_SELECTED));
            HashMap<String,String> map = new HashMap<>();
            map.put(VALUE_NAME, name);
            map.put(VALUE_PHONE, phone);
            map.put(UID, id);
            map.put(VALUE_SELECTED, selected);
            arrayList.add(map);
        }
        sdb.close();
        return arrayList;
    };

    public void setValueSelected(String id, boolean t){
        String jquery;
        sdb = getWritableDatabase();
        if(t){
            jquery = "UPDATE " + TABLE_NAME_DEVICES+" SET "+VALUE_SELECTED+"="+1+" WHERE "+UID+"="+id;
        }else{
            jquery = "UPDATE " + TABLE_NAME_DEVICES+" SET "+VALUE_SELECTED+"="+0+" WHERE "+UID+"="+id;
        }
        sdb.execSQL(jquery);
        sdb.close();
    }
    public void updateObject(HashMap<String, String> map){
        String jquery;
        sdb = getWritableDatabase();
        String id = map.get(UID);
        String name = map.get(VALUE_NAME);
        String phone = map.get(VALUE_PHONE);
        jquery = "UPDATE " + TABLE_NAME_DEVICES+" SET "+VALUE_NAME+"='"+name+"', "+VALUE_PHONE+"='"+phone+"' WHERE "+UID+"="+id;

        Log.d(TAG, "name: "+name+" phone: "+ phone+ " id: "+id);
        sdb.execSQL(jquery);
        sdb.close();
    }

    public ArrayList<HashMap>  getValueSelected() {
        ArrayList<HashMap> arrayList = new ArrayList<HashMap>();
        SQLiteDatabase sdb = this.getWritableDatabase();
        String jquery = "SELECT * FROM "+TABLE_NAME_DEVICES+ " WHERE "+VALUE_SELECTED+"=1";
        Cursor cursor = sdb.rawQuery(jquery,null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(this.VALUE_NAME));
            String id = cursor.getString(cursor.getColumnIndex(UID));
            HashMap<String,String> map = new HashMap<>();
            map.put(VALUE_NAME, name);
            map.put(UID, id);
            arrayList.add(map);
        }
        sdb.close();
        return arrayList;
    }

    public ArrayList<HashMap> getFavoriteCommand(){
        ArrayList<HashMap> arrayList = new ArrayList<HashMap>();
        sdb = this.getWritableDatabase();

        Cursor cursor;
        String jquery = "SELECT * FROM "+TABLE_NAME_COMMANDS+" INNER JOIN " + TABLE_NAME_DEVICES +" ON " + TABLE_NAME_COMMANDS+"."+VALUE_ID_DEVICE+"="+TABLE_NAME_DEVICES+"."+UID
               +" WHERE "+ TABLE_NAME_COMMANDS+"."+VALUE_FAVORITE+"=1 AND "+TABLE_NAME_DEVICES+ "."+VALUE_SELECTED+"=1";
        try {
            cursor =  sdb.rawQuery(jquery,null);
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String nameCommand = cursor.getString(1);
                String valueCommand = cursor.getString(2);
                String idDev = cursor.getString(cursor.getColumnIndex(VALUE_ID_DEVICE));
                String favorite = cursor.getString(cursor.getColumnIndex(VALUE_FAVORITE));
                String nameDevice = cursor.getString(6);;
                String phone = cursor.getString(cursor.getColumnIndex(VALUE_PHONE));;
                HashMap<String,String> map = new HashMap<>();
                map.put(VALUE_NAME, nameCommand);
                map.put(UID, id);
                map.put(VALUE_COMMAND, valueCommand);
                map.put(VALUE_ID_DEVICE, idDev);
                map.put(VALUE_FAVORITE, favorite);
                map.put(VALUE_NAME_DEVICE, nameDevice);
                map.put(VALUE_PHONE, phone);
                arrayList.add(map);
            }
        }catch (SQLException e){
            Log.e(TAG, e.toString());
        }


        sdb.close();
        return arrayList;
    }

    public void setValueFavorite(String id, boolean favorite){
        String jquery;
        sdb = getWritableDatabase();
        if(favorite){
            jquery = "UPDATE " + TABLE_NAME_COMMANDS+" SET "+VALUE_FAVORITE+"="+1+" WHERE "+UID+"="+id;
        }else{
            jquery = "UPDATE " + TABLE_NAME_COMMANDS+" SET "+VALUE_FAVORITE+"="+0+" WHERE "+UID+"="+id;
        }
        sdb.execSQL(jquery);
        sdb.close();
    }

    public boolean addCommand(ArrayList<HashMap> arrayList){
        sdb = this.getWritableDatabase();
        ArrayList<HashMap> _arrayList = arrayList;
        System.out.println("ok");
        ContentValues cv;
        try {
            for(HashMap<String,String> map : arrayList){
               /* cv = new ContentValues();
                cv.put(VALUE_NAME, map.get("name"));
                cv.put(VALUE_COMMAND, map.get("code"));
                cv.put(VALUE_ID_DEVICE, map.get("idDev"));
                cv.put(VALUE_FAVORITE, "0");
                long id = sdb.insert(TABLE_NAME_COMMANDS, null, cv);*/

                String jquery = "INSERT INTO "+TABLE_NAME_COMMANDS+" ("+VALUE_NAME+", "+VALUE_COMMAND+", "+VALUE_ID_DEVICE +", "+VALUE_FAVORITE+")"+
                        "VALUES ('"+map.get("name")+"','"+map.get("code")+"','"+map.get("idDev")+"','1');";
                sdb.execSQL(jquery);
            }
        }catch (SQLException e){
            Log.e(TAG, e.toString());
            sdb.close();
            return false;
        }
        sdb.close();
        return true;

    }



    public ArrayList<HashMap> getAllCommand(){
        sdb = this.getWritableDatabase();
        ArrayList<HashMap> arrayList = new ArrayList<>();

        String jquery = "SELECT * FROM "+TABLE_NAME_COMMANDS;
        Cursor cursor = sdb.rawQuery(jquery,null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(VALUE_NAME));
            String id = cursor.getString(cursor.getColumnIndex(UID));
            String command =  cursor.getString(cursor.getColumnIndex(VALUE_COMMAND));
            String idDev =  cursor.getString(cursor.getColumnIndex(VALUE_ID_DEVICE));
            String favorite =  cursor.getString(cursor.getColumnIndex(VALUE_FAVORITE));

            HashMap<String,String> map = new HashMap<>();
            map.put(VALUE_NAME, name);
            map.put(UID, id);
            map.put(VALUE_COMMAND, command);
            map.put(VALUE_ID_DEVICE, idDev);
            map.put(VALUE_FAVORITE, favorite);

            HashMap<String, String> mapDevice = getNameDevice(idDev, sdb);
            map.put(VALUE_NAME_DEVICE, mapDevice.get(VALUE_NAME));
            map.put(VALUE_PHONE, mapDevice.get(VALUE_PHONE));
            //map.put(VALUE_NAME_DEVICE, nameDevice);
            arrayList.add(map);
        }
        sdb.close();
        return  arrayList;
    }
    public ArrayList<HashMap> getAllCommand(String like){
        sdb = this.getWritableDatabase();
        ArrayList<HashMap> arrayList = new ArrayList<>();

        String jquery = "SELECT * FROM "+TABLE_NAME_COMMANDS +" WHERE "+ VALUE_NAME+" LIKE '%"+like+"%'";
        Cursor cursor = sdb.rawQuery(jquery,null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(VALUE_NAME));
            String id = cursor.getString(cursor.getColumnIndex(UID));
            String command =  cursor.getString(cursor.getColumnIndex(VALUE_COMMAND));
            String idDev =  cursor.getString(cursor.getColumnIndex(VALUE_ID_DEVICE));
            String favorite =  cursor.getString(cursor.getColumnIndex(VALUE_FAVORITE));

            HashMap<String,String> map = new HashMap<>();
            map.put(VALUE_NAME, name);
            map.put(UID, id);
            map.put(VALUE_COMMAND, command);
            map.put(VALUE_ID_DEVICE, idDev);
            map.put(VALUE_FAVORITE, favorite);

           // String nameDevice = getNameDevice(idDev, sdb);
            HashMap<String, String> mapDevice = getNameDevice(idDev, sdb);

            map.put(VALUE_NAME_DEVICE, mapDevice.get(VALUE_NAME));
            map.put(VALUE_PHONE, mapDevice.get(VALUE_PHONE));
            arrayList.add(map);
        }
        sdb.close();
        return  arrayList;
    }

    private void clearUpLimitHistory(){
        HashMap<String,String> map = new HashMap<>();
        getSetting(map);
        String limit = map.get(COUNT_MEMORY_HISTORY);
        sdb = getWritableDatabase();
        String query = "DELETE  FROM "+TABLE_NAME_HISTORY + " WHERE "+UID +" NOT IN "+"(SELECT "+UID+" FROM " +TABLE_NAME_HISTORY+" ORDER BY " + VALUE_DATE + " DESC LIMIT "+limit+")";
       // String query = "DELETE  FROM "+TABLE_NAME_HISTORY + " WHERE "+UID +" NOT IN "+"(SELECT "+UID+" FROM " +TABLE_NAME_HISTORY+" ORDER BY " + VALUE_DATE + " DESC LIMIT "+5+")";
        sdb.execSQL(query);
        sdb.close();
    }

    public ArrayList<HashMap> getHistoryCommand(String limit){
       // ArrayList<HashMap> arrayList = new ArrayList<>();
        sdb = this.getWritableDatabase();
        String jquery = "SELECT * FROM "+TABLE_NAME_HISTORY +" ORDER BY "+VALUE_DATE+" DESC LIMIT "+ limit;
        ArrayList<HashMap> arrayList = getHistoryCommand(jquery, sdb);
        sdb.close();
        return  arrayList;
    }
    public ArrayList<HashMap> getHistoryCommand(Calendar calFrom, Calendar calTo){
        Date dateFrom = calFrom.getTime();
        Calendar _calTo = Calendar.getInstance();
        _calTo.set(Calendar.YEAR, calTo.get(Calendar.YEAR));
        _calTo.set(Calendar.MONTH, calTo.get(Calendar.MONTH));
        _calTo.set(Calendar.DAY_OF_MONTH, calTo.get(Calendar.DAY_OF_MONTH));
        _calTo.set(Calendar.HOUR,24);
        _calTo.set(Calendar.MINUTE,0);
        _calTo.set(Calendar.SECOND,0);
        Date dateTo = _calTo.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String TO_DATE = formatter.format(dateTo);
        String FROM_DATE = formatter.format(dateFrom);

        sdb = this.getWritableDatabase();
        String jquery = "SELECT * FROM "+TABLE_NAME_HISTORY +  " WHERE "+ VALUE_DATE+ " <= '"+ TO_DATE+ "' AND " +VALUE_DATE +">='"+ FROM_DATE + "' ORDER BY "+VALUE_DATE+" DESC ";
        ArrayList<HashMap> arrayList = getHistoryCommand(jquery, sdb);
        sdb.close();
        return  arrayList;
    }

    private ArrayList<HashMap> getHistoryCommand(String query, SQLiteDatabase sdb){
        ArrayList<HashMap> arrayList = new ArrayList<>();
        Cursor cursor = sdb.rawQuery(query, null);
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            map.put(UID,cursor.getString(cursor.getColumnIndex(UID)));
            map.put(VALUE_DATE,cursor.getString(cursor.getColumnIndex(VALUE_DATE)));
            map.put(VALUE_ID_COMMAND,cursor.getString(cursor.getColumnIndex(VALUE_ID_COMMAND)));
            map.put(VALUE_DELIVERED,cursor.getString(cursor.getColumnIndex(VALUE_DELIVERED)));
            HashMap<String, String> mapCommand = getCommand(cursor.getString(cursor.getColumnIndex(VALUE_ID_COMMAND)), sdb);
            map.put(VALUE_NAME, mapCommand.get(VALUE_NAME));
            map.put(VALUE_COMMAND, mapCommand.get(VALUE_COMMAND));
            map.put(VALUE_ID_DEVICE, mapCommand.get(VALUE_ID_DEVICE));
            map.put(VALUE_NAME_DEVICE, mapCommand.get(VALUE_NAME_DEVICE));
            arrayList.add(map);
        }
        cursor.close();
        return  arrayList;
    }

    private HashMap<String,String> getCommand(String id, SQLiteDatabase sdb){
        String jquery = "SELECT * FROM "+TABLE_NAME_COMMANDS+ " WHERE " + UID +"=" +id ;
        HashMap<String,String> map = new HashMap<>();
        Cursor cursor = sdb.rawQuery(jquery,null);
        while (cursor.moveToNext()) {
            map.put(VALUE_NAME, cursor.getString(cursor.getColumnIndex(VALUE_NAME)));
            map.put(VALUE_COMMAND, cursor.getString(cursor.getColumnIndex(VALUE_COMMAND)));
            map.put(VALUE_ID_DEVICE, cursor.getString(cursor.getColumnIndex(VALUE_ID_DEVICE)));
            HashMap<String,String> mapDev;
            mapDev = getNameDevice(cursor.getString(cursor.getColumnIndex(VALUE_ID_DEVICE)), sdb);
            map.put(VALUE_NAME_DEVICE, mapDev.get(VALUE_NAME));
            map.put(VALUE_PHONE, mapDev.get(VALUE_PHONE));
        }
        cursor.close();
        return map;

    }


    private HashMap<String, String> getNameDevice(String id, SQLiteDatabase sdb){
        HashMap<String, String> map = new HashMap<>();
        String name = "";
        String phone = "";
        String jquery = "SELECT * FROM "+TABLE_NAME_DEVICES+ " WHERE "+UID+"=" + id;
        Cursor cursor = sdb.rawQuery(jquery,null);
        while (cursor.moveToNext()) {
            map.put(VALUE_NAME, cursor.getString(cursor.getColumnIndex(VALUE_NAME)));
            map.put(VALUE_PHONE, cursor.getString(cursor.getColumnIndex(VALUE_PHONE)));
          //  name = cursor.getString(cursor.getColumnIndex(this.VALUE_NAME));
           // phone = cursor.getString(cursor.getColumnIndex(this.VALUE_PHONE));
        }
        return map;
    }

    public boolean delCommand(String id){
        sdb = getWritableDatabase();
        String jquery = "DELETE FROM "+TABLE_NAME_COMMANDS+" WHERE _id="+id;
        try {
            sdb.execSQL(jquery);
        }catch (Exception e){
            Log.e(TAG, e.toString());
            e.printStackTrace();
            sdb.close();
            return false;
        }
        sdb.close();
        return true;
    }

    public void delObject(String id){
        sdb = getWritableDatabase();
        String jquery = "DELETE FROM "+TABLE_NAME_DEVICES+" WHERE _id="+id;
        try {
            sdb.execSQL(jquery);
        }catch (Exception e){
            Log.e(TAG, e.toString());
            e.printStackTrace();
            sdb.close();

        }
        jquery = "DELETE FROM "+TABLE_NAME_COMMANDS+" WHERE "+VALUE_ID_DEVICE+"="+id;
        try {
            sdb.execSQL(jquery);
        }catch (Exception e){
            Log.e(TAG, e.toString());
            e.printStackTrace();
            sdb.close();

        }
        sdb.close();
    }

    public synchronized long  insertHistory(HashMap<String, String> map){
        clearUpLimitHistory();
        sdb = getWritableDatabase();
        ContentValues cv =  new ContentValues();
        cv.put(VALUE_DATE,map.get(VALUE_DATE));
        cv.put(VALUE_ID_COMMAND,map.get(VALUE_ID_COMMAND));
        cv.put(VALUE_DELIVERED,0);
        long id = sdb.insert(TABLE_NAME_HISTORY, null, cv);

        if(id<0){
            Log.e(TAG, "Error INSERT to table:  "+TABLE_NAME_HISTORY);
        }

        sdb.close();
        return  id;
    }

    public void updateToDelivered (String id){
        sdb = getWritableDatabase();
        String query =  "UPDATE " + TABLE_NAME_HISTORY+" SET "+VALUE_DELIVERED+"="+1+" WHERE "+UID+"="+id;
        sdb.execSQL(query);
        sdb.close();
    }

    public long fillSetting(String nameSetting, String param, SQLiteDatabase db){

        ContentValues cv =  new ContentValues();
        cv.put(VALUE_NAME_SETTING_CODE, nameSetting);
        cv.put(VALUE_PARAMETER_SETTING, param);
        long id = db.insert(TABLE_NAME_SETTING, null, cv);
        if(id<0){
            Log.e(TAG, "+++ ERROR fillSetting");
        }
        return id;
    }
    private long fillDefaultParam(SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put(VALUE_NAME, "My Lambo car" );
        cv.put(VALUE_PHONE, "0000" );
        cv.put(VALUE_SELECTED, "1" );
        long idDev = db.insert(TABLE_NAME_DEVICES, null, cv);
        if(idDev<0){
            Log.e(TAG, "+++ ERROR fillDefaultParam");
        }
        String jquery = "INSERT INTO "+TABLE_NAME_COMMANDS+" ("+VALUE_NAME+", "+VALUE_COMMAND+", "+VALUE_ID_DEVICE +", "+VALUE_FAVORITE+")"+
                "VALUES ('Поставить на охрану','123401','"+idDev+"','1');";
        db.execSQL(jquery);
        jquery = "INSERT INTO "+TABLE_NAME_COMMANDS+" ("+VALUE_NAME+", "+VALUE_COMMAND+", "+VALUE_ID_DEVICE +", "+VALUE_FAVORITE+")"+
                "VALUES ('Снять с охраны','123400','"+idDev+"','1');";
        db.execSQL(jquery);

        cv = new ContentValues();
        cv.put(VALUE_NAME, "My home" );
        cv.put(VALUE_PHONE, "0000" );
        cv.put(VALUE_SELECTED, "0" );
        idDev = db.insert(TABLE_NAME_DEVICES, null, cv);
        if(idDev<0){
            Log.e(TAG, "+++ ERROR fillDefaultParam");
        }
        jquery = "INSERT INTO "+TABLE_NAME_COMMANDS+" ("+VALUE_NAME+", "+VALUE_COMMAND+", "+VALUE_ID_DEVICE +", "+VALUE_FAVORITE+")"+
                "VALUES ('Call me','123407','"+idDev+"','1');";
        db.execSQL(jquery);
        jquery = "INSERT INTO "+TABLE_NAME_COMMANDS+" ("+VALUE_NAME+", "+VALUE_COMMAND+", "+VALUE_ID_DEVICE +", "+VALUE_FAVORITE+")"+
                "VALUES ('Set APN','123463internet','"+idDev+"','0');";
        db.execSQL(jquery);
        return idDev;
    }

    public void setSetting(HashMap<String,String>map){
        String query;
        sdb = getWritableDatabase();
        for (Map.Entry entry : map.entrySet()) {

            String oldKey=null, oldValue=null;

            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            query = "SELECT * FROM "+TABLE_NAME_SETTING+ " WHERE "+VALUE_NAME_SETTING_CODE+"=" +"'"+key+"'";

            try{
                Cursor cursor = sdb.rawQuery(query,null);
                while (cursor.moveToNext()) {
                oldValue = cursor.getString(cursor.getColumnIndex(VALUE_PARAMETER_SETTING));
                }
            }catch (Exception e){
                Log.e(TAG,e.toString());
            }
            if(oldValue!=null && !oldValue.equals(value)){
                query = "UPDATE " + TABLE_NAME_SETTING + " SET " + VALUE_PARAMETER_SETTING + "='"+ entry.getValue()+"'" + " WHERE " + VALUE_NAME_SETTING_CODE +"='"+entry.getKey()+"'";
                sdb.execSQL(query);
            }else if(oldValue==null){
                fillSetting(key, value, sdb );
            }
        }
        sdb.close();
    }

    public boolean clearSettingValue(String key){
        sdb = getWritableDatabase();
        boolean del = sdb.delete(TABLE_NAME_SETTING, VALUE_NAME_SETTING_CODE+ "="+key, null )>0;
        sdb.close();
        return  del;
    }

    private void getSetting(HashMap<String,String> map){
        sdb = getWritableDatabase();
        //HashMap<String,String> map = new HashMap<>();
        String query =  "SELECT * FROM " + TABLE_NAME_SETTING;
        Cursor cursor = sdb.rawQuery(query,null);
        while (cursor.moveToNext()) {
            String key = cursor.getString(cursor.getColumnIndex(VALUE_NAME_SETTING_CODE));
            String value = cursor.getString(cursor.getColumnIndex(VALUE_PARAMETER_SETTING));
            map.put(key,value);
        }
        cursor.close();
        sdb.close();
    }
    public long createRowNameTrack(){
        ContentValues cv = new ContentValues();
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStamp = formatter.format(now);
        cv.put("date", timeStamp);
        sdb = getWritableDatabase();
        long id = sdb.insert(TABLE_TRACK_COLLECTION, null, cv);
        sdb.close();
        return id;
    }
   /* public boolean fillRowNameTrack(long idTrack, String timeStamp, String name ,  List<Polyline> listPolylyneTrack){
        boolean  a;
        sdb = getWritableDatabase();
        String query = "UPDATE " + TABLE_TRACK_COLLECTION+" SET "+"name = '"+name+"', date = '"+timeStamp+"' WHERE "+UID+"="+idTrack;
        try {
            sdb.execSQL(query);
            Log.d(TAG, "+++ name:  " + name);
            a = true;
        }catch (SQLException e){
            Log.e(TAG, "+++SQLException " + e.toString());
            a = false;
        }
        sdb.close();
        if(a){
           a = fillTracks(idTrack,  listPolylyneTrack);
        }

        return a;
    }*/

    public boolean fillRowNameTrack(long idTrack, String timeStamp, String name ,   List<HashMap<String, Double>> listControlPointsTrack, Double distance){
        boolean  a;
        sdb = getWritableDatabase();
        String query = "UPDATE " + TABLE_TRACK_COLLECTION+" SET "+"name = '"+name+"', date = '"+timeStamp+"', "+DISTANCE+"="+Double.toString(distance)+" WHERE "+UID+"="+idTrack;
        try {
            sdb.execSQL(query);
            Log.d(TAG, "+++ name:  " + name);
            a = true;
        }catch (SQLException e){
            Log.e(TAG, "+++SQLException " + e.toString());
            a = false;
        }
        sdb.close();
        if(a){
            a = fillTracks(idTrack,  listControlPointsTrack);
        }

        return a;
    }

    /*private synchronized boolean fillTracks(long idTrack, List<Polyline> listPolylyneTrack){
        boolean  a = true;
        ContentValues cv = new ContentValues();
        sdb = getWritableDatabase();
        cv.put("trackId", idTrack);
        for(Polyline line : listPolylyneTrack){
            List<LatLng> latLngList =  line.getPoints();
            for(LatLng latLng : latLngList){
                Double lat = latLng.latitude;
                Double lng = latLng.longitude;
                cv.put("lat", lat);
                cv.put("lng", lng);
                try {
                    sdb.insert(TABLE_TRACKS, null, cv);

                }catch (SQLException e){
                    Log.e(TAG, "+++SQLException " + e.toString());
                    a = false;

                }

            }
        }
        sdb.close();
        return a;
    }
*/
    private synchronized boolean fillTracks(long idTrack,   List<HashMap<String, Double>> listControlPointsTrack ){
        boolean  a = true;
        ContentValues cv = new ContentValues();
        sdb = getWritableDatabase();
        cv.put("trackId", idTrack);
        for(HashMap<String, Double> map : listControlPointsTrack){

                Double lat = map.get("lat");
                Double lng = map.get("lng");
                cv.put("lat", lat);
                cv.put("lng", lng);
                try {
                    sdb.insert(TABLE_TRACKS, null, cv);

                }catch (SQLException e){
                    Log.e(TAG, "+++SQLException " + e.toString());
                    a = false;

                }


        }
        sdb.close();
        return a;
    }


    public boolean deleteRowNameTrack(long id){
        boolean  a;
        sdb = getWritableDatabase();
        String query = "DELETE FROM "+TABLE_TRACK_COLLECTION+" WHERE _id="+id;
        try {
            sdb.execSQL(query);
            a = true;
        }catch (SQLException e){
            Log.e(TAG, "+++SQLException " + e.toString());
            a = false;
        }
        sdb.close();
        return a;
    }
    public List<HashMap<String, String>> getTraksNameRows(){
        sdb = getWritableDatabase();
        List<HashMap<String, String>>  list = new ArrayList<>();
        String query =  "SELECT * FROM " + TABLE_TRACK_COLLECTION;
        Cursor cursor = sdb.rawQuery(query,null);
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String distance = cursor.getString(cursor.getColumnIndex(DISTANCE));
            String id = cursor.getString(cursor.getColumnIndex(UID));
            map.put("name", name);
            map.put("date", date);
            map.put("id", id);
            map.put(DISTANCE, distance);
            list.add(map);
        }
        cursor.close();
        sdb.close();
        return  list;
    }
    public LatLng[] getTrack(String id){
        LatLng [] latLngs = new LatLng[0];
        String query =  "SELECT * FROM " + TABLE_TRACKS + " WHERE trackId = " + id+ " ORDER BY "+ UID;
        sdb = getWritableDatabase();
        Cursor cursor = sdb.rawQuery(query,null);
        latLngs = new LatLng[cursor.getCount()];
        int i = 0;
        while (cursor.moveToNext()) {
            latLngs[i] = new LatLng(cursor.getDouble(cursor.getColumnIndex("lat")), cursor.getDouble(cursor.getColumnIndex("lng")));
            i++;
        }
        cursor.close();
        sdb.close();
        return  latLngs;
    }

    public boolean delTrack(String id){
        boolean b = true;
        String query =  "DELETE  FROM " + TABLE_TRACKS + " WHERE trackId = " + id;
        sdb = getWritableDatabase();
        try {
            sdb.execSQL(query);
        }catch (SQLException e){
            Log.e(TAG, "+++SQLException " + e.toString());
            b = false;
        }
        query =  "DELETE FROM " + TABLE_TRACK_COLLECTION + " WHERE "+UID+" = " + id;
        try {
            sdb.execSQL(query);
        }catch (SQLException e){
            Log.e(TAG, "+++SQLException " + e.toString());
            b = false;
        }

        sdb.close();
        return b;
    }

}
