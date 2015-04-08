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
    private static final int DATABASE_VERSION = 5;

    private static final String TABLE_NAME_DEVICES = "devices";
    private static final String TABLE_NAME_COMMANDS = "commands";
    public static final String UID = "_id";
    public static final String VALUE_NAME = "valueName";
    public static final String VALUE_NAME_DEVICE = "valueNameDevice";
    public static final String VALUE_PHONE = "valuePhone";
    public static final String VALUE_PARAM = "valueParam";
    public static final String VALUE_COMMAND = "valueCommand";
    public static final String VALUE_ID_DEVICE = "valueIdDevice";

    public static final String VALUE_SELECTED = "valueSelected";

    private static final String SQL_CREATE_TABLE_DEVICES = "CREATE TABLE if not exists "
           + TABLE_NAME_DEVICES +" (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VALUE_NAME + " VARCHAR(255), " + VALUE_PHONE + " VARCHAR(255), " + VALUE_PARAM + " VARCHAR(255) "+");";

    private static final String SQL_CREATE_TABLE_COMMANDS = "CREATE TABLE if not exists "
            +TABLE_NAME_COMMANDS+" (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VALUE_NAME + " VARCHAR(255), " + VALUE_COMMAND +  " VARCHAR(255), " + VALUE_ID_DEVICE +" INTEGER " + ");";


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

            HashMap<String,String> map = new HashMap<>();
            map.put(VALUE_NAME, name);
            map.put(UID, id);
            map.put(VALUE_COMMAND, command);
            map.put(VALUE_ID_DEVICE, idDev);
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
            e.printStackTrace();
            sdb.close();
            return false;
        }
        sdb.close();
        return true;
    }
}
