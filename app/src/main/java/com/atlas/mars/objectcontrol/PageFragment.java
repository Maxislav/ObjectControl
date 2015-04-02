package com.atlas.mars.objectcontrol;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

//import com.larvalabs.svgandroid.SVG;

public class PageFragment extends Fragment implements View.OnClickListener {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String TAG = "myLogs";
    static final String SAVE_PAGE_NUMBER = "save_page_number";

    int pageNumber;
    int backColor;
    static ArrayList<View> fragmetView;
    public static Button selectObjButton;
    static HashMap< Integer, View> fragmetMapView; //массив фрагментов
    RowCreator rowCreator;
    static PageFragment newInstance(int page) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);

        return pageFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmetView = new ArrayList<>();

        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

        Random rnd = new Random();
        backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        int savedPageNumber = -1;
        if (savedInstanceState != null) {
            savedPageNumber = savedInstanceState.getInt(SAVE_PAGE_NUMBER);
        }
        Log.d(TAG, "savedPageNumber = " + savedPageNumber);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        switch (pageNumber) {
            case 0:

                view = inflater.inflate(R.layout.fragment_0_home, null);
                selectObjButton = (Button) (view.findViewById(R.id.selectButton));
                selectObjButton.setOnClickListener(this);
                TextView tvSelectObject = (TextView)view.findViewById(R.id.tvSelectObject);
                Communicator communicator = (Communicator)getActivity();
                communicator.setTextSelectObject(tvSelectObject);
                break;
            case 1:


                view = inflater.inflate(R.layout.fragment_1_all_commands, null);
                rowCreator = new RowCreator(view, inflater);
                rowCreator.create();
                rowCreator.create();
                for(int k = 0; k<10; k++){
                    rowCreator.create();
                }
               // rowCreator.create();
                //rowCreator.create();


               /* final SurfaceView surface = (SurfaceView) view.findViewById(R.id.surface);
                surface.setZOrderOnTop(true);
                SurfaceHolder sfhTrackHolder = surface.getHolder();
                sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);

                surface.getHolder().addCallback(new Callback() {

                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        // Do some drawing when surface is ready
                        int width = surface.getWidth()-3;
                        int height = surface.getHeight()-10;
                        Log.d(TAG, "ГотовоЖ ++++"+ width);
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
                        float kx = new Float(width)/100;
                        float ky = new Float(height)/40;
                        Log.d(TAG, "width/100 ++++"+ kx);
                        Path path = new Path();

                        path.moveTo(10*kx, 0);
                        path.lineTo(90 * kx, 0);
                        path.lineTo(100 * kx, 10 * ky);
                        path.lineTo(100 * kx, 30 * ky);
                        path.lineTo(90 * kx, 40 * ky);
                        path.lineTo(10 * kx, 40 * ky);
                        path.lineTo(0, 30 * ky);
                        path.lineTo(0, 10*ky);

                        path.close();
                        matrix.reset();
                        matrix.preTranslate(0, 5);
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
                });*/



/*
                Paint paint = new Paint();
                paint.setColor(Color.parseColor("#CD5C5C"));
                Bitmap bg = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bg);
                canvas.drawRect(50, 50, 200, 200, paint);
                LinearLayout ll = (LinearLayout) view.findViewById(R.id.row_component);*/


                //  ll.addView(bg);


                //Drawable drawable = new BitmapDrawable(bg);
                //ll.setBackground(getResources(), bg);






/*
                ImageView imageView = (ImageView)view.findViewById(R.id.imgVector);
               // imageView.setBackgroundColor(Color.WHITE);
                imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.romb);
                Drawable drawable = svg.createPictureDrawable();
                imageView.setImageDrawable(drawable);*/

                /*String p = "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"400px\" height=\"480px\"> " + "<polyline stroke=\"#00FF00\" stroke-width=\"3\" points=\"10,10 200,200\" /> " + "</svg>";
                InputStream is = new ByteArrayInputStream(p.getBytes());
                SVG svg = SVGParser.getSVGFromInputStream(is);
                Picture pic = svg.getPicture();
                ImageView imageView = (ImageView)view.findViewById(R.id.imgVector);
                imageView.setBackgroundColor(Color.WHITE);
                imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                Drawable drawable = svg.createPictureDrawable();
                imageView.setImageDrawable(drawable);*/

               // canvas.drawPicture(pic);

                break;
            default:
                view = inflater.inflate(R.layout.fragment, null);
                TextView tvPage = (TextView) view.findViewById(R.id.tvPage);
                tvPage.setText("Page " + (pageNumber + 1));
                break;

        }

        return view;
    }



    @Override
    public void onClick(View v) {

        int buttonIndex = translateIdToIndex(v.getId());
        OnSelectedButtonListener listener = (OnSelectedButtonListener) getActivity();
        listener.onButtonSelected(buttonIndex, v);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: " + pageNumber);
    }
    public interface OnSelectedButtonListener {
        void onButtonSelected(int buttonIndex, View v);
    }

    int translateIdToIndex(int id) {
        int index = -1;
        switch (id) {
            case R.id.selectButton:
                index = 1;
                break;
          /*  case R.id.button2:
                index = 2;
                break;
            case R.id.button3:
                index = 3;
                break;*/
        }
        return index;
    }
}