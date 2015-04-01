package com.atlas.mars.objectcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME_DEVICES = "devices";
    public static final String UID = "_id";
    public static final String VALUE_NAME = "valueName";
    public static final String VALUE_PHONE = "valuePhone";
    public static final String VALUE_PARAM = "valueParam";

    private static final String SQL_CREATE_TABLE_DEVICES = "CREATE TABLE if not exists "
           + TABLE_NAME_DEVICES +" (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VALUE_NAME + " VARCHAR(255), " + VALUE_PHONE + " VARCHAR(255), " + VALUE_PARAM + " VARCHAR(255) "+");";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_DEVICES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long addNewDevice(String name, String phone){
        sdb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(VALUE_NAME, name );
        cv.put(VALUE_PHONE, phone );

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
        }
        return arrayList;
    };
}
