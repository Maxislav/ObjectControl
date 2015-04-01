package com.atlas.mars.objectcontrol;

import android.app.DialogFragment;
import android.content.Context;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements PageFragment.OnSelectedButtonListener {

    static final String TAG = "myLogs";
    static final int PAGE_COUNT = 3;
    static ArrayList<View> viewArrayList;
    static ArrayList<View> fragmentView;
    static LinearLayout action_bar_title;
    MyJQuery myJQuery;
    LinearLayout lv;
    ViewPager pager;
    PagerAdapter pagerAdapter;
   static Button selectObjButton;
    private static final int NOTIFY_ID = 101;
    final int DIALOG_EXIT = 1;
    DialogFragment dlg1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dlg1 = new Dialog1();
        dlg1.onAttach(this);
        _init();
    }


    private void _init(){
        myJQuery = new MyJQuery();
        fragmentView = new ArrayList<View>();
        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        action_bar_title = (LinearLayout)findViewById(R.id.action_bar_title);
        ViewGroup vgr = (ViewGroup)action_bar_title;
        viewArrayList = myJQuery.getViewsByTag(vgr, LinearLayout.class);

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

        for(int i = 0; i<viewArrayList.size(); i++){
            setTitleClickListener(viewArrayList.get(i), i);
        }

    }

    private void setTitleClickListener(View view, int _i){
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

       // viewArrayList.get(i).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.title_active, null));

       // pager.setCurrentItem(1);
        super.onStart();
    }

    private void setActiveNavBar(int k){

        if(k==0 && selectObjButton == null){
          //  View view  = pager.getChildAt(k);
          //  selectObjButton = (Button)view.findViewById(R.id.selectButton);
        }



        for (int i = 0 ; i<viewArrayList.size(); i++){
            if(k==i){
                viewArrayList.get(i).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.title_active, null));
            }else{
                viewArrayList.get(i).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.title_bar, null));
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

   /* public static Context getAppContext() {
        MainActivity.this.getApplicationContext();
        return MainActivity.context;
    }*/




    public  void showPopupMenu(View v) {

        PopupMenu popupMenu = new PopupMenu(getContext(),v);
        popupMenu.inflate(R.menu.select_object); // Для Android 4.0
        MenuItem item;
        MenuInflater mi = new MenuInflater(getContext());
        //item = mi.inflate(R.menu.one_row,popupMenu.getMenu());
     //   item = mi
       // popupMenu.getMenu().add()
       // getMenuInflater
        Log.d(TAG, "showPopupMenu +++ ");

       // PopupMenu popupMenuRow = new PopupMenu(getContext(),popupMenu)

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Toast.makeText(PopupMenuDemoActivity.this,
                        // item.toString(), Toast.LENGTH_LONG).show();
                        // return true;
                        switch (item.getItemId()) {

                            case R.id.menu4:
                                Toast.makeText(getApplicationContext(),
                                        "Вы выбрали PopupMenu 1",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu5:
                                Toast.makeText(getApplicationContext(),
                                        "Вы выбрали PopupMenu 2",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu6:
                                Toast.makeText(getApplicationContext(),
                                        "Вы выбрали PopupMenu 3",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
        popupMenu.show();

    }
    PopupWindow pw;
    public void showPopupWindow(View view){
        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.row_select_obj, null);




       pw = new PopupWindow( v, FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        pw.setOutsideTouchable(false);
        pw.showAtLocation(view, Gravity.CENTER, 0, 0);
        FrameLayout b = (FrameLayout)v.findViewById(R.id.btn);
        b.setOnClickListener(new View.OnClickListener() {
            //final PopupWindow _pw = pw;
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        //pw.showAsDropDown(view, 0, 0);
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG , "getItem " + position );

            return  PageFragment.newInstance(position);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
