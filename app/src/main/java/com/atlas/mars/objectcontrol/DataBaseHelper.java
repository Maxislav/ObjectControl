package com.atlas.mars.objectcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by mars on 4/1/15.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase sdb;


    private static final String TAG = "myLog";
    private static final String DATABASE_NAME = "obcon.db";
    private static final int DATABASE_VERSION = 16;

    private static final String TABLE_NAME_DEVICES = "devices";
    private static final String TABLE_NAME_COMMANDS = "commands";
    private static final String TABLE_NAME_HISTORY = "history";
    private static final String TABLE_NAME_SETTING = "setting";
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




    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_DEVICES);
        db.execSQL(SQL_CREATE_TABLE_COMMANDS);
        db.execSQL(SQL_CREATE_TABLE_HISTORY);
        db.execSQL(SQL_CREATE_TABLE_SETTING);
        fillSetting(CONFIRM_SEND, "1",db);
        fillSetting(MULTIPLE_SEND, "0", db);
        fillSetting(COUNT_MEMORY_HISTORY, "100", db);
        fillSetting(COUNT_DISPLAY_HISTORY, "10", db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
                        "VALUES ('"+map.get("name")+"','"+map.get("code")+"','"+map.get("idDev")+"','0');";
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

    public ArrayList<HashMap> getHistoryCommand(){
        ArrayList<HashMap> arrayList = new ArrayList<>();
        sdb = this.getWritableDatabase();
        /*String jquery = "SELECT * FROM "+TABLE_NAME_COMMANDS+" INNER JOIN " + TABLE_NAME_DEVICES +" ON " + TABLE_NAME_COMMANDS+"."+VALUE_ID_DEVICE+"="+TABLE_NAME_DEVICES+"."+UID
                +" WHERE "+ TABLE_NAME_COMMANDS+"."+VALUE_FAVORITE+"=1 AND "+TABLE_NAME_DEVICES+ "."+VALUE_SELECTED+"=1";*/

        String jquery = "SELECT * FROM "+TABLE_NAME_HISTORY +" ORDER BY "+VALUE_DATE+" DESC";
        Cursor cursor = sdb.rawQuery(jquery,null);
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
        sdb.close();
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
        sdb = getWritableDatabase();
       /* String jquery = "INSERT INTO "+ TABLE_NAME_HISTORY + " ("+ VALUE_DATE +", "+ VALUE_ID_COMMAND+" )"
                +"VALUES ('" +map.get(VALUE_DATE)+"' , '"+ map.get(VALUE_ID_COMMAND)+"');";*/
      //  sdb.execSQL(jquery);



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
            Log.e(TAG, "+++ERROR fillSetting");
        }
        return id;
    }

    public void setSetting(HashMap<String,String>map){
        String query;
        sdb = getWritableDatabase();
        for (Map.Entry entry : map.entrySet()) {

            query = "UPDATE " + TABLE_NAME_SETTING + " SET " + VALUE_PARAMETER_SETTING + "='"+ entry.getValue()+"'" + " WHERE " + VALUE_NAME_SETTING_CODE +"='"+entry.getKey()+"'";
            Log.d(TAG, query);
            sdb.execSQL(query);
           // System.out.println("Key: " + entry.getKey() + " Value: "+ entry.getValue());
        }
        sdb.close();

    }

    public void getSetting(HashMap<String,String> map){
        sdb = getWritableDatabase();
        //HashMap<String,String> map = new HashMap<>();
        String query =  "SELECT * FROM " + TABLE_NAME_SETTING;
        Cursor cursor = sdb.rawQuery(query,null);
        while (cursor.moveToNext()) {
            String key = cursor.getString(cursor.getColumnIndex(VALUE_NAME_SETTING_CODE));
            String value = cursor.getString(cursor.getColumnIndex(VALUE_PARAMETER_SETTING));
            map.put(key,value);
        }
        sdb.close();
    }
}
