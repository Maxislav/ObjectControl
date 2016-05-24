package com.atlas.mars.objectcontrol.rest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.squareup.okhttp.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

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

    Call<ResponseBody> call1, call2, call3, call4;
    Bitmap bitmap1, bitmap2, bitmap3, bitmap4;
    int zoom, x, y;
    private static HashMap<String, String> hashSetting = DataBaseHelper.hashSetting;
    String storagePathTiles, storagePathTilesFull;
    DataBaseHelper db;


    public OsmRest(int zoom, int x, int y, Context context) {

        db = new DataBaseHelper(context);

        this.zoom = zoom;
        this.x = x;
        this.y = y;
        storagePathTiles = hashSetting.get(DataBaseHelper.STORAGE_PATH_TILES);
        storagePathTilesFull = storagePathTiles + "/" + MAP_TYPE + "/" + zoom + "/" + x + "/" + y + ".png";


        Retrofit client = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
        OsmInterfaceService service = client.create(OsmInterfaceService.class);
        call1 = service.getMapTile(zoom + 1, x * 2, y * 2);
        call2 = service.getMapTile(zoom + 1, (x * 2) + 1, y * 2);
        call3 = service.getMapTile(zoom + 1, (x * 2), (y * 2) + 1);
        call4 = service.getMapTile(zoom + 1, (x * 2) + 1, (y * 2) + 1);
    }


    public byte[] getResultByteArray() {
        byte[] byteArray = null;
        Bitmap bmp = null;

        if(storagePathTiles == null || !createPathFolderIfNeeded(Integer.toString(zoom), Integer.toString(x), Integer.toString(y))){
            bitmap1 = getBitmapSync(call1);
            bitmap2 = getBitmapSync(call2);
            bitmap3 = getBitmapSync(call3);
            bitmap4 = getBitmapSync(call4);
            bmp = merge();
        }

        if( storagePathTiles!=null && createPathFolderIfNeeded(Integer.toString(zoom), Integer.toString(x), Integer.toString(y))){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmp = BitmapFactory.decodeFile(storagePathTilesFull, options);
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
        return byteArray;
    }

    private Bitmap merge() {
        Bitmap comboBitmap = null;
        comboBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
        Canvas comboCanvas = new Canvas(comboBitmap);
        comboCanvas.drawBitmap(bitmap1, 0.0f, 0.0f, null);
        comboCanvas.drawBitmap(bitmap2, 256.0f, 0.0f, null);
        comboCanvas.drawBitmap(bitmap3, 0.0f, 256.0f, null);
        comboCanvas.drawBitmap(bitmap4, 256.0f, 256.0f, null);

        if (storagePathTiles != null) {
           if(!createPathFolderIfNeeded(Integer.toString(zoom), Integer.toString(x), Integer.toString(y))){
               saveBitmap(comboBitmap, storagePathTiles + "/" + MAP_TYPE + "/" + zoom + "/" + x + "/" + y + ".png");
           }
        }

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

    private synchronized boolean createPathFolderIfNeeded(String zoom, String x, String y) {
        boolean ret = true;
        String path = storagePathTiles + "/" + MAP_TYPE + "/" + zoom + "/" + x + "/" + y + ".png";
        ArrayList<String> listPath = new ArrayList<>();
        listPath.add(MAP_TYPE);
        listPath.add(zoom);
        listPath.add(x);

        File file = new File(path);

        if (file.exists()) {
            return true;
        } else {
            createPath(listPath, storagePathTiles);
            return false;
        }
    }

    private void createPath(ArrayList<String> listPath, String path) {
        String _path = path;
        if (0 < listPath.size()) {
            _path = _path + "/" + listPath.remove(0);
        }
        File file = new File(_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (0 < listPath.size()) {
            createPath(listPath, _path);
        }
    }

    private void saveBitmap(Bitmap bitmap, String path){
        File file = new File (path);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            long size = db.getTilesSize();
            if(500000000<size){
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

}