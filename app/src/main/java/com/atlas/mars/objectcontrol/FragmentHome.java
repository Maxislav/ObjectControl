package com.atlas.mars.objectcontrol;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.atlas.mars.objectcontrol.dialogs.SelectObjDialog00;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mars on 4/8/15.
 */
public class FragmentHome extends MyFragmentView {

    FrameLayout btnSelectObj; //Кнопка выбрать объект
    TextView tvSelectObject; //Текст этой кнопки
    FrameLayout btnAddObj; //Кнопка создать обект
    SelectObjDialog00 selectObjDialog;
    String selectObject;
    static HashMap<String, String> mapSelectObjects;


    FragmentHome(MainActivity mainActivity, View viewFragment, LayoutInflater inflater){
        super(mainActivity, viewFragment, inflater);
    }


    @Override
    public void onInit() {
        //rowCreator = new RowCreator(viewFragment, inflater);
        selectObjDialog = new SelectObjDialog00(mainActivity);
        mapSelectObjects = new HashMap<>();
        btnSelectObj =(FrameLayout)myJQuery.getViewsByTagWithReset((ViewGroup)viewFragment,FrameLayout.class).get(0);
        tvSelectObject = (TextView)myJQuery.getViewsByTagWithReset((ViewGroup)btnSelectObj, TextView.class ).get(0);

        btnAddObj = (FrameLayout)viewFragment.findViewById(R.id.addObj);
        initBtnAddObj(btnAddObj);
        getValueSelected();
        setTextSelectedObj();
        initBtnSelectObj(btnSelectObj);
    }

    private void getValueSelected(){
        ArrayList arrayList = db.getValueSelected();
        HashMap<String, String> map = new HashMap<>();
        for (int k = 0; k < arrayList.size(); k++) {
            map = (HashMap) arrayList.get(k);
            mapSelectObjects.put(map.get(db.UID), map.get(db.VALUE_NAME));
        }
    }


    private void initBtnAddObj(View view){
        final PopupMenu popupMenu = new PopupMenu(mainActivity, view);
        popupMenu.inflate(R.menu.menu_add_obj);
        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addObj:

                                Intent questionIntent = new Intent(mainActivity, AddObject.class);
                                questionIntent.putExtra(mainActivity.FROM, mainActivity.TO_ADD_OBJECT);
                                mainActivity.startActivityForResult(questionIntent, mainActivity.CHOOSE_THIEF);
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

    private void setTextSelectedObj() {
        selectObject = "";
        if (mapSelectObjects != null && 0 < mapSelectObjects.size()) {
            for (Map.Entry<String, String> entry : mapSelectObjects.entrySet()) {
                selectObject += entry.getValue() + " ";
            }
        } else {
            selectObject = "NONE";
        }
        tvSelectObject.setText(selectObject);
    }

    public void initBtnSelectObj(View view){
        tvSelectObject =(TextView) myJQuery.getViewsByTagWithReset((ViewGroup)view, TextView.class).get(0);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectObjDialog.dialogSelectObj(v);
            }
        });
    }

}
