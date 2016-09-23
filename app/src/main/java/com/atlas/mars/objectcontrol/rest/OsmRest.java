package com.atlas.mars.objectcontrol.rest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.ViewDebug;

import com.atlas.mars.objectcontrol.DataBaseHelper;
//import com.squareup.okhttp.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

//import retrofit.Call;
//import retrofit.Retrofit;
//import retrofit.http.GET;
//import retrofit.http.Path;

/**
 * Created by mars on 5/16/16.
 */
public class OsmRest {
    private final static String BASE_URL = "http://a.tile.openstreetmap.org";
    private final static String MAP_TYPE = "OSM";
    private final static String TAG = "OsmRestLog";

    Call<ResponseBody> call1, call2, call3, call4;
    Bitmap bitmap1, bitmap2, bitmap3, bitmap4;
    int zoom, x, y;
    private static HashMap<String, String> hashSetting = DataBaseHelper.hashSetting;
    String storagePathTiles, storagePathTilesFull;
    DataBaseHelper db;
    OsmInterfaceService service;
    Retrofit client;

   int[][] listPath;


    public OsmRest(int zoom, int x, int y, Context context) {

        db = new DataBaseHelper(context);
        listPath = new int[4][3];
        int i = 0;
        for (i = 0;i<4; i++){
            switch (i){
                case 0:
                    listPath[i] = new int[]{zoom + 1, x * 2, y * 2};
                    break;
                case 1:
                    listPath[i] = (new int[]{zoom + 1, (x * 2) + 1, y * 2});
                    break;
                case 2:
                    listPath[i]=(new int[]{zoom + 1, (x * 2), (y * 2) + 1});
                    break;
                case 3:
                    listPath[i] = (new int[]{zoom + 1, (x * 2) + 1, (y * 2) + 1});
                    break;
            }

        }

        this.zoom = zoom;
        this.x = x;
        this.y = y;
        storagePathTiles = hashSetting.get(DataBaseHelper.STORAGE_PATH_TILES);
        storagePathTilesFull = storagePathTiles + "/" + MAP_TYPE + "/" + zoom + "/" + x + "/" + y + ".png";


    }


    public byte[] getResultByteArray() {
        byte[] byteArray = null;
        Bitmap bmp = null;


        if(storagePathTiles == null){
            retrofitCreate();
            call1 = service.getMapTile(zoom + 1, x * 2, y * 2);
            call2 = service.getMapTile(zoom + 1, (x * 2) + 1, y * 2);
            call3 = service.getMapTile(zoom + 1, (x * 2), (y * 2) + 1);
            call4 = service.getMapTile(zoom + 1, (x * 2) + 1, (y * 2) + 1);

            bitmap1 = getBitmapSync(call1, getPath(zoom + 1, x * 2, y * 2));
            bitmap2 = getBitmapSync(call2, getPath(zoom + 1, (x * 2) + 1, y * 2));
            bitmap3 = getBitmapSync(call3, getPath(zoom + 1, (x * 2), (y * 2) + 1));
            bitmap4 = getBitmapSync(call4, getPath(zoom + 1, (x * 2) + 1, (y * 2) + 1));
            //bmp = merge();
        }else{
            /** Все ли файлы имеются на крточке */
            if(isNededretrofitCreate(zoom, x, y)){
                retrofitCreate();
            }

            if(isFileExist(listPath[0])){
                bitmap1 = getStorageBitmap(listPath[0]);

            }else{
                call1 = getCall(listPath[0]);
                bitmap1 = getBitmapSync(call1, getPath(listPath[0]));

                if(storagePathTiles!= null &&  bitmap1!=null){
                    saveFile(bitmap1,listPath[0]);
                   /* AsyncSave asyncSave = new  AsyncSave();
                    asyncSave.execute(getHashMap(bitmap1, getPath(listPath[0])));*/
                }
            }



            if(isFileExist(listPath[1])){
                bitmap2 = getStorageBitmap(listPath[1]);
            }else{
                call2 = getCall(listPath[1]);
                bitmap2 = getBitmapSync(call2, getPath(listPath[1]));
                if(storagePathTiles!= null &&  bitmap3!=null){
                    saveFile(bitmap2,listPath[1]);
                }
            }

            if(isFileExist(listPath[2])){
                bitmap3 = getStorageBitmap(listPath[2]);
            }else{
                call3 = getCall(listPath[2]);
                bitmap3 = getBitmapSync(call3, getPath(listPath[2]));
                if(storagePathTiles!= null &&  bitmap3!=null){
                    saveFile(bitmap3,listPath[2]);

                }
            }

            if(isFileExist(listPath[3])){
                bitmap4 = getStorageBitmap(listPath[3]);
            }else{
                call4 = getCall(listPath[3]);
                bitmap4 = getBitmapSync(call4, getPath(listPath[3]));
                if(storagePathTiles!= null &&  bitmap3!=null){
                    saveFile(bitmap4,listPath[3]);
                }
            }
        }
        bmp = merge();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
        return byteArray;
    }

    private boolean saveFile(Bitmap bitmap, int path[]){
        AsyncSave asyncSave = new  AsyncSave();
        asyncSave.execute(getHashMap(bitmap, path));
        return true;
    }


    private  Call<ResponseBody> getCall(int arg[]){
        return  service.getMapTile(arg[0], arg[1], arg[2]);
    }

    /**
     *
     * @param zoom
     * @param x
     * @param y
     * @return
     */
    private String getPath(int zoom, int x, int y){


      return   storagePathTiles + "/" + MAP_TYPE + "/" + zoom + "/" + x + "/" + y + ".png";
    }
    private String getPath(int[] arg){
        return   storagePathTiles + "/" + MAP_TYPE + "/" + arg[0] + "/" + arg[1] + "/" + arg[2] + ".png";
    }

    private boolean isNededretrofitCreate(int zoom, int x, int y){
        if(!isFileExist(zoom + 1, x * 2, y * 2)){
            return true;
        }
        else if(!isFileExist(zoom + 1, (x * 2) + 1, y * 2)){
            return true;
        }
        else if(!isFileExist(zoom + 1, (x * 2), (y * 2) + 1)){
            return true;
        }
        else if(!isFileExist(zoom + 1, (x * 2) + 1, (y * 2) + 1)){
            return true;
        }
        return false;
    }

    private void retrofitCreate(){
        if(client == null){
            try {
                client = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .build();
            }catch (Exception e){
                Log.e("OsmRestLog", e.toString());
            }

        }
        if (service==null){
            service = client.create(OsmInterfaceService.class);
        }
    }

    private boolean isFileExist(int zoom, int x, int y){
        String path = storagePathTiles + "/" + MAP_TYPE + "/" + zoom + "/" + x + "/" + y + ".png";
        File file = new File(path);
        if (file.exists()) {
            return true;
        }else {
            return false;
        }
    }

    private boolean isFileExist(int arg[]){
        String path = storagePathTiles + "/" + MAP_TYPE + "/" + arg[0] + "/" + arg[1] + "/" + arg[2] + ".png";
        File file = new File(path);
        if (file.exists()) {
            return true;
        }else {
            return false;
        }
    }


    private Bitmap getStorageBitmap(int zoom, int x, int y) {

        Log.d(TAG, "StorageBitmap " + getPath(zoom , x, y ) );
        BitmapFactory.Options options = null;
        try {
            options  = new BitmapFactory.Options();
        }catch (RuntimeException e){
            Log.d(TAG, e.toString());
        }
       return  BitmapFactory.decodeFile(storagePathTiles + "/" + MAP_TYPE + "/" + Integer.toString(zoom) + "/" + Integer.toString(x) + "/" + Integer.toString(y) + ".png", options);
    }

    private Bitmap getStorageBitmap(int arg[]) {
        int zoom, x, y;
        zoom = arg[0];
        x = arg[1];
        y = arg[2];

        Log.d(TAG, "StorageBitmap " + getPath(zoom , x, y ) );
        BitmapFactory.Options options = null;
        try {
            options  = new BitmapFactory.Options();
        }catch (RuntimeException e){
            Log.d(TAG, e.toString());
        }
        return  BitmapFactory.decodeFile(storagePathTiles + "/" + MAP_TYPE + "/" + Integer.toString(zoom) + "/" + Integer.toString(x) + "/" + Integer.toString(y) + ".png", options);
    }

    private Bitmap merge() {
        Bitmap comboBitmap = null;
        comboBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
        Canvas comboCanvas = new Canvas(comboBitmap);
        comboCanvas.drawBitmap(bitmap1, 0.0f, 0.0f, null);
        comboCanvas.drawBitmap(bitmap2, 256.0f, 0.0f, null);
        comboCanvas.drawBitmap(bitmap3, 0.0f, 256.0f, null);
        comboCanvas.drawBitmap(bitmap4, 256.0f, 256.0f, null);

       /* if (storagePathTiles != null) {
            if (!createPathFolderIfNeeded(zoom, x,y)) {
                saveBitmap(comboBitmap, storagePathTiles + "/" + MAP_TYPE + "/" + zoom + "/" + x + "/" + y + ".png");
            }
        }*/

        return comboBitmap;
    }


    private Bitmap getBitmapSync(Call<ResponseBody> call, String path) {
        Log.d(TAG, "Bitmap sync " + path);
        InputStream is = null;
        Bitmap bitmap;
        try {
            ResponseBody responseBody = call.execute().body();
            is = responseBody.byteStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }


        return bitmap;
    }

    private synchronized boolean createPathFolderIfNeeded(int... arg) {

        int zoom=arg[0],  x=arg[1],  y=arg[2];

        boolean ret = true;
        String path = storagePathTiles + "/" + MAP_TYPE + "/" + Integer.toString(zoom) + "/" + Integer.toString(x) + "/" + Integer.toString(y) + ".png";
        ArrayList<String> listPath = new ArrayList<>();
        listPath.add(MAP_TYPE);
        listPath.add(Integer.toString(zoom));
        listPath.add(Integer.toString(x));

        File file = new File(path);



        if (file.exists()) {
            return true;
        } else {
            try{
                createPath(listPath, storagePathTiles);
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }

            return false;
        }
    }

    private void createPath (ArrayList<String> listPath, String path)  throws Exception{
        String _path = path;
        if (0 < listPath.size()) {
            _path = _path + "/" + listPath.remove(0);
        }
        Environment.getExternalStorageDirectory();
        File file = new File(_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (0 < listPath.size()) {
            createPath(listPath, _path);
        }
    }

    private Map<String, Object> getHashMap(Bitmap b, int arg[]){
        Map<String, Object> map = new HashMap<>();


        map.put("path", arg);
        map.put("bitmap", b);


//действия с ключом и значением


        return  map;
    }


    private void saveBitmap(Bitmap bitmap, String path) {
        File file = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            long size = db.getTilesSize();
            if (500000000 < size) {
                String remPath = db.removeTile();
                new File(remPath).delete();
            }
            db.addTile(storagePathTilesFull, byteSizeOf(bitmap));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private interface OsmInterfaceService {
        @GET("/{zoom}/{x}/{y}.png")
        Call<ResponseBody> getMapTile(@Path("zoom") int zoom, @Path("x") int x, @Path("y") int y);
    }

    private static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    class AsyncSave extends AsyncTask<Map<String, Object>, Void, String >{

        @Override
        protected String doInBackground(Map... maps) {

            Map<String, Object> map = maps[0];
            int arg[] = (int[]) map.get("path");


            Bitmap bitmap = (Bitmap)map.get("bitmap");

            String path = getPath(arg);
            createPathFolderIfNeeded(arg);

            File file = new File(path);

            try {
                FileOutputStream out = new FileOutputStream(file);
                if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)){
                    out.flush();
                    out.close();
                };
                long size = db.getTilesSize();
                if (500000000 < size) {
                    String remPath = db.removeTile();
                    new File(remPath).delete();
                }
                db.addTile(storagePathTilesFull, byteSizeOf(bitmap));

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


        }
    }

}
