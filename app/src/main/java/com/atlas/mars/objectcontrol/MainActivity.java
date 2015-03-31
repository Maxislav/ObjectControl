package com.atlas.mars.objectcontrol;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
                showPopupMenu(v);
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




    public static void showPopupMenu(View v) {

        PopupMenu popupMenu = new PopupMenu(getContext(),v);
        popupMenu.inflate(R.menu.select_object); // Для Android 4.0
        Log.d(TAG, "showPopupMenu +++ ");

      /*  Context context = getContext();
        //FragmentManager fm = getSupportFragmentManager();
        EditNameDialog editNameDialog = new EditNameDialog();
        editNameDialog.show(getApplicationContext(), "fragment_edit_name");*/
       // editNameDialog.gg(context, "fragment_edit_name");
       /* popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menu4:
                        Toast.makeText(getContext(),
                                "Вы выбрали PopupMenu 1",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu5:
                        Toast.makeText(getContext(),
                                "Вы выбрали PopupMenu 2",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu6:
                        Toast.makeText(getContext(),
                                "Вы выбрали PopupMenu 3",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }

            }

        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(getContext(), "onDismiss",
                        Toast.LENGTH_SHORT).show();
            }


        });
        popupMenu.show();*/
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
    public void onButtonSelected(int buttonIndex) {

      //  FragmentManager fragmentManager = getSupportFragmentManager();

        dlg1 = new Dialog1();
        dlg1.show(getFragmentManager(), "dlg1");




        FragmentTransaction ft = getFragmentManager().beginTransaction();

        Toast.makeText(getApplicationContext(), buttonIndex + "",
                Toast.LENGTH_SHORT).show();
    }
}
