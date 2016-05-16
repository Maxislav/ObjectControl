package com.atlas.mars.objectcontrol.rest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.squareup.okhttp.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit.Call;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by mars on 5/16/16.
 */
public class OsmRest {
    private final static String BASE_URL = "http://a.tile.openstreetmap.org";


    Call<ResponseBody> call1, call2, call3, call4;
    Bitmap bitmap1, bitmap2, bitmap3, bitmap4;

    public OsmRest(int zoom, int x, int y) {
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
        bitmap1 = getBitmapSync(call1);
        bitmap2 = getBitmapSync(call2);
        bitmap3 = getBitmapSync(call3);
        bitmap4 = getBitmapSync(call4);
        Bitmap bmp = merge();
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


    private interface OsmInterfaceService {
        @GET("/{zoom}/{x}/{y}.png")
        Call<ResponseBody> getMapTile(@Path("zoom") int zoom, @Path("x") int x, @Path("y") int y);
    }
}
