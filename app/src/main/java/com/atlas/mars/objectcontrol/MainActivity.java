package com.atlas.mars.objectcontrol;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements PageFragment.OnSelectedButtonListener, Communicator  {

    static final String TAG = "myLogs";
    static final int PAGE_COUNT = 3;
    static ArrayList<View> titleArrayList;
    static HashMap< Integer, View> fragmetMapView; //массив фрагментов
    static ArrayList<View> fragmentView;
    static LinearLayout action_bar_title;
    static final private int CHOOSE_THIEF = 0;
    MyJQuery myJQuery;
    LinearLayout lv;
    ViewPager pager;
    PagerAdapter pagerAdapter;
    static Button selectObjButton;
    private static final int NOTIFY_ID = 101;
    final int DIALOG_EXIT = 1;
    DialogFragment dlg1;
    MyDialog myDialog;
    DataBaseHelper db;
    static String selectObject;
    static HashMap<String,String> mapSelectObjects;

    @Override
    public void setTextSelectObject(TextView setTextSelectedObj){
        setTextSelectedObj(setTextSelectedObj);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("counter", 23);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectObject = "";
        dlg1 = new Dialog1();
        dlg1.onAttach(this);
        _init();
    }


    private void _init() {
        mapSelectObjects = new HashMap<>();


        db = new DataBaseHelper(this);

        ArrayList arrayList = db.getValueSelected();
        HashMap<String,String> map = new HashMap<>();
        for (int k = 0; k<arrayList.size(); k++) {
            map= (HashMap)arrayList.get(k);
            mapSelectObjects.put(map.get(db.UID),map.get(db.VALUE_NAME));
        }
      //  setTextSelectedObj();

        myDialog = new MyDialog(this);
        myJQuery = new MyJQuery();
        fragmentView = new ArrayList<View>();
        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        action_bar_title = (LinearLayout) findViewById(R.id.action_bar_title);

        titleArrayList = myJQuery.getViewsByTag( (ViewGroup)action_bar_title, LinearLayout.class);

        pager.setOffscreenPageLimit(3);
        pager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setActiveNavBar(position);
                Log.d(TAG, "onPageSelected, position = " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        for (int i = 0; i < titleArrayList.size(); i++) {
            setTitleClickListener(titleArrayList.get(i), i);
        }
        //setTextSelectedObj();

    }


    private void setTitleClickListener(View view, int _i) {
        final int i = _i;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(i);
            }
        });

    }


    @Override
    protected void onStart() {
        int i = pager.getCurrentItem();
        setActiveNavBar(i);
        super.onStart();

    }

    private void setActiveNavBar(int k) {

        if (k == 0 && selectObjButton == null) {
            //  View view  = pager.getChildAt(k);
            //  selectObjButton = (Button)view.findViewById(R.id.selectButton);
        }

        //View v =fragmetMapView.get(4);
        View v = pager.getChildAt(k);
       // fragmetMapView.put(k, pager.getChildAt(k));
        for (int i = 0; i < titleArrayList.size(); i++) {
            if (k == i) {
                titleArrayList.get(i).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.title_active, null));
            } else {
                titleArrayList.get(i).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.title_bar, null));
            }
        }

    }

    public static void setSelectObjButton(Button v) {

        selectObjButton = v;
        Log.d(TAG, "setSelectObjButton");
        selectObjButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "setSelectObjButton +++ ");
                // showPopupMenu(v);
            }
        });
    }

    public static Context getContext() {
        try {
            return (Context) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication").invoke(null, (Object[]) null);
        } catch (final Exception e1) {
            try {
                return (Context) Class.forName("android.app.AppGlobals")
                        .getMethod("getInitialApplication").invoke(null, (Object[]) null);
            } catch (final Exception e2) {
                throw new RuntimeException("Failed to get application instance");
            }
        }
    }




    public  void goToNewObjCreate(){
            Log.d(TAG, "goToNewObjCreate   ");
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem " + position);

            return PageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if(id == R.id.action_add_objecte){
            Intent questionIntent = new Intent(MainActivity.this, AddObject.class);
                startActivityForResult(questionIntent, CHOOSE_THIEF);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_THIEF) {
            if (resultCode == RESULT_OK) {
                String name =  data.getStringExtra(AddObject.NAME);
                String phone =  data.getStringExtra(AddObject.PHONE);
                Log.d(TAG, "Result " + name +" : "+ phone);

                long n  = db.addNewDevice(name,phone);

                Toast.makeText(getApplicationContext(), "ID : " + n + "",
                        Toast.LENGTH_SHORT).show();
            }else {

            }
        }
    }

    public void setActiveObject(String name, String id, boolean bo){
        if(bo){
            mapSelectObjects.put(id, name);
        }else{
            if( mapSelectObjects!=null && 0<mapSelectObjects.size() ){
                mapSelectObjects.remove(id);
            }
        }
        View view = pager.getChildAt(0);
        TextView tvSelectObject = (TextView)view.findViewById(R.id.tvSelectObject);
        setTextSelectedObj(tvSelectObject);
    }

    private void setTextSelectedObj(TextView tvSelectObject ){
        selectObject = "";

        if( mapSelectObjects!=null && 0<mapSelectObjects.size()){
            for (Map.Entry<String, String> entry : mapSelectObjects.entrySet())
            {
                selectObject+=entry.getValue()+" ";
                //System.out.println(entry.getKey() + "/" + entry.getValue());
            }
        }else{
            selectObject = "NONE";
        }


       // tvSelectObject = (TextView)view.findViewById(R.id.tvSelectObject);
        tvSelectObject.setText(selectObject);
    }

    public void showPopupWindow(View view) {
        myDialog.dialogSelectObj(view);
    }
    @Override
    public void onButtonSelected(int buttonIndex, View v) {
        //showPopupMenu(v);
        showPopupWindow(v);
        //  FragmentManager fragmentManager = getSupportFragmentManager();


        //dlg1.show(getFragmentManager(), "dlg1");


        //FragmentTransaction ft = getFragmentManager().beginTransaction();

      /*  Toast.makeText(getApplicationContext(), buttonIndex + "",
                Toast.LENGTH_SHORT).show();*/
    }
}
