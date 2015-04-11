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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.atlas.mars.objectcontrol.dialogs.SelectObjDialog00;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements PageFragment.OnSelectedButtonListener, Communicator {

    static final String TAG = "myLogs";
    static final int PAGE_COUNT = 3;
    static ArrayList<View> titleArrayList;
    static HashMap<Integer, View> fragmetMapView; //массив фрагментов
    static ArrayList<View> fragmentView;
    static LinearLayout action_bar_title;
    static final public int CHOOSE_THIEF = 0;
    MenuInflater menuInflater;
    MyJQuery myJQuery;
    LinearLayout lv;
    ViewPager pager;
    PagerAdapter pagerAdapter;
    static Button selectObjButton;
    private static final int NOTIFY_ID = 101;
    final int DIALOG_EXIT = 1;
    DialogFragment dlg1;
    SelectObjDialog00 selectObjDialog;
    DataBaseHelper db;
    static String selectObject;
    static HashMap<String, String> mapSelectObjects;
    boolean saveToHistory;
    final public int TO_ADD_OBJECT = 0;
    final public int TO_ADD_COMMAND = 1;
    static final String FROM = "FROM";
    TextView tvSelectObject;
    RowCreator rowCreator;
    View viewAllCommand;
    FragmentAllCommand fragmentAllCommand;
    FragmentHome fragmentHome;
    Menu menu;


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

        _init();
    }

    @Override
    protected void onStart() {
        int i = pager.getCurrentItem();
        setActiveNavBar(i);
        super.onStart();

    }

    @Override
    public void setTextSelectObject(TextView textView) {
        setTextSelectedObj(textView);
    }

    private void _init() {
        mapSelectObjects = new HashMap<>();


        db = new DataBaseHelper(this);

        ArrayList arrayList = db.getValueSelected();
        HashMap<String, String> map = new HashMap<>();
        for (int k = 0; k < arrayList.size(); k++) {
            map = (HashMap) arrayList.get(k);
            mapSelectObjects.put(map.get(db.UID), map.get(db.VALUE_NAME));
        }
        selectObjDialog = new SelectObjDialog00(this);
        myJQuery = new MyJQuery();
        fragmentView = new ArrayList<View>();
        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        action_bar_title = (LinearLayout) findViewById(R.id.action_bar_title);

        titleArrayList = myJQuery.getViewsByTag((ViewGroup) action_bar_title, LinearLayout.class);

        pager.setOffscreenPageLimit(3);

        pager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setActiveNavBar(position);
                changeMenuByFragment(position);
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


    private void setActiveNavBar(int k) {

        for (int i = 0; i < titleArrayList.size(); i++) {
            if (k == i) {
                titleArrayList.get(i).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.title_active, null));
            } else {
                titleArrayList.get(i).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.title_bar, null));
            }
        }


    }

    public void changeMenuByFragment(int k) {

      //  menu == null ? return : null;
        if(menu==null){
            return;
        }
        {
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        }

        switch (k) {
            case 0:
                menu.findItem(R.id.action_settings).setVisible(true);
                menu.findItem(R.id.action_add_objecte).setVisible(true);

                break;
            case 1:
                menu.findItem(R.id.action_settings).setVisible(true);
                menu.findItem(R.id.action_add_command).setVisible(true);
                menu.findItem(R.id.action_remove_command).setVisible(true);
                menu.findItem(R.id.action_add_favorite).setVisible(true);
                break;
            default:

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


    public void goToNewObjCreate() {
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

        this.menu = menu;
        this.menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        if(pagerAdapter!=null){
            changeMenuByFragment(pager.getCurrentItem());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent questionIntent;
        switch (item.getItemId()){
            case R.id.action_add_objecte:

                questionIntent = new Intent(MainActivity.this, AddObject.class);
                questionIntent.putExtra(FROM, TO_ADD_OBJECT);
                startActivityForResult(questionIntent, CHOOSE_THIEF);

                return true;
            case R.id.action_add_command:

                questionIntent = new Intent( this, MakeCommandActivity.class);
                questionIntent.putExtra(FROM, TO_ADD_COMMAND);
                startActivityForResult(questionIntent, CHOOSE_THIEF);

                return true;

            case R.id.action_remove_command:
                fragmentAllCommand.showMinus(View.VISIBLE);
                return true;
            case R.id.action_add_favorite:
                fragmentAllCommand.showFavorite(View.VISIBLE);
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, ActivitySetting.class);
                startActivity(intent);
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
                int k = data.getIntExtra(FROM, 0);
                switch (k) {
                    case 0:
                        //добавление нового Девайса
                        Log.d(TAG, "RESULT +++ " + k + "");
                        String name = data.getStringExtra(AddObject.NAME);
                        String phone = data.getStringExtra(AddObject.PHONE);
                        if (phone.isEmpty() || name.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Пустое значение поля",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //todo Раскоментировать
                        long n = db.addNewDevice(name, phone);
                        Toast.makeText(getApplicationContext(), "ID : " + n + "", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        //добавление новой команды
                        Log.d(TAG, "RESULT +++ " + k + "");
                        //   regenViewAllCommand();
                        fragmentAllCommand.onRedraw();
                        break;
                }

            } else {

            }
        }
    }
    @Override
    public void onBackPressed() {
        Log.d(TAG,"back bress");
       /* searchView.clearFocus();

        getListData();*/
        //relativeLayout.requestFocus();
        if(fragmentAllCommand!=null && fragmentAllCommand.searchView.isFocused()){
            Log.d(TAG, "searchView back btn "+ fragmentAllCommand.searchView.isFocused());
            fragmentAllCommand.searchView.onActionViewExpanded();
            fragmentAllCommand.mainLayout.requestFocus();
            fragmentAllCommand.searchView.onActionViewCollapsed();
        }else{
            super.onBackPressed();
        }

    }


    public void setActiveObject(String name, String id, boolean bo) {
        if (bo) {
            mapSelectObjects.put(id, name);
        } else {
            if (mapSelectObjects != null && 0 < mapSelectObjects.size()) {
                mapSelectObjects.remove(id);
            }
        }
        View view = pager.getChildAt(0);
        //TextView tvSelectObject = (TextView) view.findViewById(R.id.tvSelectObject);

        setTextSelectedObj(tvSelectObject);
    }

    private void setTextSelectedObj(TextView tvSelectObject) {
        selectObject = "";
        if (mapSelectObjects != null && 0 < mapSelectObjects.size()) {
            for (Map.Entry<String, String> entry : mapSelectObjects.entrySet()) {
                selectObject += entry.getValue() + " ";
                //System.out.println(entry.getKey() + "/" + entry.getValue());
            }
        } else {
            selectObject = "NONE";
        }
        tvSelectObject.setText(selectObject);
    }

    @Override
    public void initBtnSelectObj(View view) {
        tvSelectObject = (TextView) myJQuery.findViewByTagClass((ViewGroup) view, TextView.class).get(0);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectObjDialog.dialogSelectObj(v);
            }
        });
    }

    @Override
    public void initBtnAddObj(View view) {
        final PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_add_obj);
        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addObj:

                                Intent questionIntent = new Intent(MainActivity.this, AddObject.class);
                                questionIntent.putExtra(FROM, TO_ADD_OBJECT);
                                startActivityForResult(questionIntent, CHOOSE_THIEF);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });
    }

    @Override
    public void initViewAllCommand(View view, LayoutInflater inflater) {
        fragmentAllCommand = new FragmentAllCommand(this, view, inflater);
    }

    @Override
    public void initViewHome(View view, LayoutInflater inflater) {
        fragmentHome = new FragmentHome(this, view, inflater);
    }

    @Override
    public void connectionFragment() {
        if (fragmentHome != null) {
            fragmentHome.regenScrollView();
        }
    }
}
