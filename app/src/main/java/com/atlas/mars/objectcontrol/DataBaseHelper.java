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


/**
 * Created by mars on 4/1/15.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase sdb;

    private static final String TAG = "myLog";
    private static final String DATABASE_NAME = "obcon.db";
    private static final int DATABASE_VERSION = 6;

    private static final String TABLE_NAME_DEVICES = "devices";
    private static final String TABLE_NAME_COMMANDS = "commands";
    public static final String UID = "_id";
    public static final String VALUE_NAME = "valueName";
    public static final String VALUE_NAME_DEVICE = "valueNameDevice";
    public static final String VALUE_PHONE = "valuePhone";
    public static final String VALUE_PARAM = "valueParam";
    public static final String VALUE_COMMAND = "valueCommand";
    public static final String VALUE_ID_DEVICE = "valueIdDevice";
    public static final String VALUE_FAVORITE = "valueFavorite";

    public static final String VALUE_SELECTED = "valueSelected";

    private static final String SQL_CREATE_TABLE_DEVICES = "CREATE TABLE if not exists "
           + TABLE_NAME_DEVICES +" (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VALUE_NAME + " VARCHAR(255), " + VALUE_PHONE + " VARCHAR(255), " + VALUE_PARAM + " VARCHAR(255) "+");";

    private static final String SQL_CREATE_TABLE_COMMANDS = "CREATE TABLE if not exists "
            +TABLE_NAME_COMMANDS+" (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VALUE_NAME + " VARCHAR(255), " + VALUE_COMMAND +  " VARCHAR(255), " + VALUE_ID_DEVICE +" INTEGER " +VALUE_FAVORITE+ " INTEGER"+ ");";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_DEVICES);
        db.execSQL(SQL_CREATE_TABLE_COMMANDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      //  db.execSQL(SQL_CREATE_TABLE_COMMANDS);
       // String jquery  = "UPDATE " + TABLE_NAME_DEVICES+" SET "+VALUE_SELECTED+"="+0+" WHERE "+VALUE_SELECTED+" IS NULL";
        //db.execSQL(jquery);
       /* String jquery = "ALTER TABLE "+TABLE_NAME_COMMANDS+" ADD COLUMN "+VALUE_FAVORITE+" "+"INTEGER";
        db.execSQL(jquery);
        jquery = "UPDATE " + TABLE_NAME_COMMANDS+" SET "+VALUE_FAVORITE+"="+0+" WHERE "+VALUE_FAVORITE+" IS NULL";
        db.execSQL(jquery);*/
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
        //String jquery;


        //String jquery = "SELECT * FROM "+TABLE_NAME_COMMANDS+ " WHERE "+VALUE_FAVORITE+"=1"+" AND "+VALUE_ID_DEVICE+"= (SELECT " +UID+ " FROM "+TABLE_NAME_DEVICES+" WHERE "+VALUE_SELECTED+"="+1+")";


        String jquery = "SELECT * FROM "+TABLE_NAME_COMMANDS+" INNER JOIN " + TABLE_NAME_DEVICES +" ON " + TABLE_NAME_COMMANDS+"."+VALUE_ID_DEVICE+"="+TABLE_NAME_DEVICES+"."+UID
               +" WHERE "+ TABLE_NAME_COMMANDS+"."+VALUE_FAVORITE+"=1 AND "+TABLE_NAME_DEVICES+ "."+VALUE_SELECTED+"=1";


         //jquery = "SELECT * FROM commands INNER JOIN devices ON commands.valueIdDevice=devices._id WHERE commands.valueFavorite=1 AND devices.valueSelected=1";

        try {
            cursor =  sdb.rawQuery(jquery,null);
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String nameCommand = cursor.getString(1);
                String valueCommand = cursor.getString(2);
                String idDev = cursor.getString(cursor.getColumnIndex(VALUE_ID_DEVICE));
                String favorite = cursor.getString(cursor.getColumnIndex(VALUE_FAVORITE));
                String nameDevice = cursor.getString(6);;
                HashMap<String,String> map = new HashMap<>();
                map.put(VALUE_NAME, nameCommand);
                map.put(UID, id);
                map.put(VALUE_COMMAND, valueCommand);
                map.put(VALUE_ID_DEVICE, idDev);
                map.put(VALUE_FAVORITE, favorite);
                map.put("valueDeviceName", nameDevice);
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
                cv = new ContentValues();
                cv.put(VALUE_NAME, map.get("name"));
                cv.put(VALUE_COMMAND, map.get("code"));
                cv.put(VALUE_ID_DEVICE, map.get("idDev"));
                cv.put(VALUE_FAVORITE, "0");
                long id = sdb.insert(TABLE_NAME_COMMANDS, null, cv);
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
            String nameDevice = getNameDevice(idDev, sdb);
            map.put(VALUE_NAME_DEVICE, nameDevice);
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
            String nameDevice = getNameDevice(idDev, sdb);
            map.put(VALUE_NAME_DEVICE, nameDevice);
            arrayList.add(map);
        }
        sdb.close();
        return  arrayList;
    }


    private String getNameDevice(String id, SQLiteDatabase sdb){
        String name = "";
        String jquery = "SELECT * FROM "+TABLE_NAME_DEVICES+ " WHERE "+UID+"=" + id;
        Cursor cursor = sdb.rawQuery(jquery,null);
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(this.VALUE_NAME));
        }
        return name;
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
}
