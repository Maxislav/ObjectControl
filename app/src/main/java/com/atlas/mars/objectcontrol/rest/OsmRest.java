package com.atlas.mars.objectcontrol.rest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.ViewDebug;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.squareup.okhttp.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;

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


    public OsmRest(int zoom, int x, int y, Context context) {

        db = new DataBaseHelper(context);

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

            bitmap1 = getBitmapSync(call1);
            bitmap2 = getBitmapSync(call2);
            bitmap3 = getBitmapSync(call3);
            bitmap4 = getBitmapSync(call4);
            //bmp = merge();
        }else{
            /** Все ли файлы имеются на крточке */
            if(isNededretrofitCreate(zoom, x, y)){
                retrofitCreate();
            }

            if(isFileExist(zoom + 1, x * 2, y * 2)){
                bitmap1 = getStorageBitmap(zoom + 1, x * 2, y * 2 );
                bitmap1 = getStorageBitmap(zoom + 1, x * 2, y * 2 );
                bitmap1 = getStorageBitmap(zoom + 1, x * 2, y * 2 );
            }else{
                call1 = service.getMapTile(zoom + 1, x * 2, y * 2);
                bitmap1 = getBitmapSync(call1);

                if(storagePathTiles!= null &&  bitmap1!=null){
                    AsyncSave asyncSave = new  AsyncSave();
                    asyncSave.execute(getHashMap(bitmap1, getPath(zoom + 1, x * 2, y * 2)));
                }
            }

            if(isFileExist(zoom + 1, (x * 2) + 1, y * 2)){
                bitmap2 = getStorageBitmap(zoom + 1, (x * 2) + 1, y * 2);
            }else{
                call2 = service.getMapTile(zoom + 1, (x * 2) + 1, y * 2);
                bitmap2 = getBitmapSync(call2);
                if(storagePathTiles!= null &&  bitmap3!=null){
                    AsyncSave asyncSave = new  AsyncSave();
                    asyncSave.execute(getHashMap(bitmap2, getPath(zoom + 1, (x * 2) + 1, y * 2)));
                   // saveBitmap(bitmap2, getPath(zoom + 1, (x * 2) + 1, y * 2));
                }
            }

            if(isFileExist(zoom + 1, (x * 2), (y * 2) + 1)){
                bitmap3 = getStorageBitmap(zoom + 1, (x * 2), (y * 2) + 1);
            }else{
                call3 = service.getMapTile(zoom + 1, (x * 2), (y * 2) + 1);
                bitmap3 = getBitmapSync(call3);
                if(storagePathTiles!= null &&  bitmap3!=null){
                    AsyncSave asyncSave = new  AsyncSave();
                    asyncSave.execute(getHashMap(bitmap3, getPath(zoom + 1, (x * 2), (y * 2) + 1)));

                    //saveBitmap(bitmap3, getPath(zoom + 1, (x * 2), (y * 2) + 1));

                }
            }

            if(isFileExist(zoom + 1, (x * 2) + 1, (y * 2) + 1)){
                bitmap4 = getStorageBitmap(zoom + 1, (x * 2) + 1, (y * 2) + 1);
            }else{
                call4 = service.getMapTile(zoom + 1, (x * 2) + 1, (y * 2) + 1);
                bitmap4 = getBitmapSync(call4);
                if(storagePathTiles!= null &&  bitmap3!=null){
                    AsyncSave asyncSave = new  AsyncSave();
                    asyncSave.execute(getHashMap(bitmap4, getPath(zoom + 1, (x * 2) + 1, (y * 2) + 1)));
                   // saveBitmap(bitmap4, getPath(zoom + 1, (x * 2) + 1, (y * 2) + 1));
                }
            }
        }
        bmp = merge();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
        return byteArray;
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
            client = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .build();
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


    private Bitmap getStorageBitmap(int zoom, int  x, int  y) {
        BitmapFactory.Options options = null;
        try {
            options  = new BitmapFactory.Options();
        }catch (RuntimeException e){
            Log.d(TAG, e.toString());
        }


        //Log.d(TAG, options.toString());
        return BitmapFactory.decodeFile(storagePathTiles + "/" + MAP_TYPE + "/" + Integer.toString(zoom) + "/" + Integer.toString(x) + "/" + Integer.toString(y) + ".png", options);

        //return null;
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


    private Bitmap getBitmapSync(Call<ResponseBody> call) {
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

    private synchronized boolean createPathFolderIfNeeded(int zoom, int x, int y) {
        boolean ret = true;
        String path = storagePathTiles + "/" + MAP_TYPE + "/" + Integer.toString(zoom) + "/" + Integer.toString(x) + "/" + Integer.toString(y) + ".png";
        ArrayList<String> listPath = new ArrayList<>();
        listPath.add(MAP_TYPE);
        listPath.add(Integer.toString(zoom));
        listPath.add(Integer.toString(x));

        File file = new File(path);;



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

    private Map<String, Bitmap> getHashMap(Bitmap b, String path){
        Map<String, Bitmap> map = new HashMap<>();
        map.put(path, b);

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

    class AsyncSave extends AsyncTask<Map<String, Bitmap>, Void, String >{

        @Override
        protected String doInBackground(Map... maps) {
            Map<String, Bitmap> map = maps[0];
            String path = null;
            Bitmap bitmap = null;

            for (Map.Entry entry: map.entrySet()) {
                path = (String) entry.getKey();
                bitmap = (Bitmap) entry.getValue();
            }

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
