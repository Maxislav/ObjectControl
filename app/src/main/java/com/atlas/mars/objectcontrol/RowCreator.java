package com.atlas.mars.objectcontrol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mars on 4/2/15.
 */
public class RowCreator {
    View view;
    LayoutInflater inflater;
    MyJQuery myJQuery;
    LinearLayout mainLayout;
    static int count = 0;
    final static String TAG = "myLog";

    RowCreator(View view, LayoutInflater inflater) {
        this.view = view;
        this.inflater = inflater;
        myJQuery = new MyJQuery();
        mainLayout = (LinearLayout)view.findViewById(R.id.mainLayout);
    }

    public FrameLayout create() {
        FrameLayout row = (FrameLayout) inflater.inflate(R.layout.row_command, null);
        ViewGroup vgRow = (ViewGroup) row;
        ArrayList<View> arrayList = new  ArrayList<>();
        arrayList = myJQuery.getViewsByTagWithReset(vgRow, SurfaceView.class);
        SurfaceView surface = (SurfaceView) arrayList.get(0);
        surfaseCreate(surface);

        TextView tvCmd = (TextView)myJQuery.getViewsByTagWithReset(row, TextView.class).get(0);
        tvCmd.setText("Команда: "+ (count+1)+"");

        mainLayout.addView(row);

        count++;
        return row;
    }
    private void surfaseCreate(SurfaceView _surface){
        final SurfaceView surface  = _surface;
        surface.setZOrderOnTop(true);
        SurfaceHolder sfhTrackHolder = surface.getHolder();
        sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);

        surface.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // Do some drawing when surface is ready

                int width = surface.getWidth() - 5;
                int height = surface.getHeight() - 10;
                Log.d(TAG, "ГотовоЖ ++++" + width);
                Paint paint = new Paint();
                paint.setColor(Color.parseColor("#FF3E3E")); //цвет кисти красный
                //paint.setShadowLayer(12, 0, 0, Color.BLACK);
                Matrix matrix = new Matrix();
                paint.setStrokeWidth(5);
                paint.setStyle(Paint.Style.STROKE);
                paint.setAntiAlias(true);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeJoin(Paint.Join.ROUND);
                Canvas canvas = holder.lockCanvas();
                float kx = new Float(width) / 100;
                float ky = new Float(height) / 40;
                Log.d(TAG, "width/100 ++++" + kx);
                Path path = new Path();

                path.moveTo(10 * kx, 0);
                path.lineTo(90 * kx, 0);
                path.lineTo(100 * kx, 10 * ky);
                path.lineTo(100 * kx, 30 * ky);
                path.lineTo(90 * kx, 40 * ky);
                path.lineTo(10 * kx, 40 * ky);
                path.lineTo(0, 30 * ky);
                path.lineTo(0, 10 * ky);

                path.close();
                matrix.reset();
                matrix.preTranslate(2, 5);
                path.transform(matrix);
                canvas.drawPath(path, paint);

                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });


    }
}
