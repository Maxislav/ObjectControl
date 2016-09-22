package com.atlas.mars.objectcontrol.rest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.squareup.okhttp.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by mars on 5/14/16.
 */
public class RestTest {
    private static String baseUrl = "http://a.tile.openstreetmap.org";
    Call<ResponseBody> call;
    Bitmap bitmap;


    public RestTest(int zoom, int x, int y) {

        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .build();
        TestInterfaceService service = client.create(TestInterfaceService.class);
        call = service.getMapTile(zoom, x, y);
        //getImage();


    }


    public Bitmap getBitmapSync() {
        InputStream is = null;
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

    public byte[] getImgByteSync() throws IOException {
        InputStream is = null;
        ResponseBody responseBody = null;

        responseBody = call.execute().body();
        is = responseBody.byteStream();
        int len;
        int size = 1024;
        byte[] buf;

        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1)
                bos.write(buf, 0, len);
            buf = bos.toByteArray();
        }
        return buf;
    }


    public void requestTile() {


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response) {
                InputStream is = null;

                try {
                    is = response.body().byteStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (is != null) {
                    bitmap = BitmapFactory.decodeStream(is);
                }
                ResponseBody responseBody = response.body();

                Log.d("RestTest", "Ok");


            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }

    public void onSuccess() {

    }

    public Bitmap getBitmap() {
        return bitmap;
    }


    public interface TestInterfaceService {

        @GET("/{zoom}/{x}/{y}.png")
        Call<ResponseBody> getMapTile(@Path("zoom") int zoom, @Path("x") int x, @Path("y") int y);


    }


}
