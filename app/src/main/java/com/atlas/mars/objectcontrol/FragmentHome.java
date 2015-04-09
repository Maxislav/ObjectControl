package com.atlas.mars.objectcontrol;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.atlas.mars.objectcontrol.dialogs.DialogSelectObj;

import java.util.ArrayList;
import java.util.HashMap;

//import com.atlas.mars.objectcontrol.dialogs.SelectObjDialog00;

/**
 * Created by mars on 4/8/15.
 */
public class FragmentHome extends MyFragmentView {

    FrameLayout btnSelectObj; //Кнопка выбрать объект
    TextView tvSelectObject; //Текст этой кнопки
    FrameLayout btnAddObj; //Кнопка создать обект
    DialogSelectObj selectObjDialog;
    String selectObject;
    View dialogView;
    ArrayList<CheckBox> listCheckBox;
    static HashMap<String, String> mapSelectObjects;
    static ArrayList<HashMap> listDevices;
    LinearLayout mainLayout;


    FragmentHome(MainActivity mainActivity, View viewFragment, LayoutInflater inflater) {
        super(mainActivity, viewFragment, inflater);
    }


    @Override
    public void onInit() {
        //rowCreator = new RowCreator(viewFragment, inflater);

        selectObjDialog = new DialogSelectObj(mainActivity);
        mapSelectObjects = new HashMap<>();
        listCheckBox = new ArrayList<>();
        btnSelectObj = (FrameLayout) myJQuery.getViewsByTagWithReset((ViewGroup) viewFragment, FrameLayout.class).get(0);
        tvSelectObject = (TextView) myJQuery.getViewsByTagWithReset((ViewGroup) btnSelectObj, TextView.class).get(0);
        btnAddObj = (FrameLayout) viewFragment.findViewById(R.id.addObj);
        initBtnSelectObj(btnSelectObj);

        initBtnAddObj(btnAddObj);

        getListDevices();
        setTextSelectedObj();

        dialogInflate(dialogView);
        okCancelListener(dialogView);

        mainLayout = (LinearLayout)viewFragment.findViewById(R.id.mainLayout);
        getFavoriteCommand();

    }

    @Override
    public void regenParams() {
        listDevices = db.getListDevices();
        View content = dialogView.findViewById(R.id.contentDialog);
        ((LinearLayout)content).removeAllViews();
        dialogInflate(dialogView);
    }

    public void regenScrollView(){
        mainLayout.removeAllViews();
        getFavoriteCommand();
    }

    private void getFavoriteCommand(){
        ArrayList<HashMap> arrayList = db.getFavoriteCommand();
        Log.d("dd", "");
        for(HashMap<String,String> map : arrayList){
            createRow(map);
        }
    }

    private void createRow(HashMap<String,String> map){
        FrameLayout row = (FrameLayout)inflater.inflate(R.layout.row_command, null);
        ArrayList<View> arrayTextView = myJQuery.getViewsByTagWithReset(row, TextView.class);
        ((TextView)arrayTextView.get(0)).setText(map.get(db.VALUE_NAME));
        ((TextView)arrayTextView.get(1)).setText(map.get(db.VALUE_COMMAND));
        ((TextView)arrayTextView.get(2)).setText(map.get("valueDeviceName"));
        ArrayList<View> arrayImgView = myJQuery.getViewsByTagWithReset(row, ImageView.class);
        arrayImgView.get(2).setVisibility(View.INVISIBLE);
        mainLayout.addView(row);
    }


    private void getListDevices() {
        listDevices = db.getListDevices();
        Log.d("dd", "");
    }


    private void initBtnAddObj(View view) {
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
        //listDevices = db.getListDevices();
        selectObject = "";
        for (HashMap<String, String> map : listDevices) {
            if(map.get(db.VALUE_SELECTED)!= null){
                if (  map.get(db.VALUE_SELECTED).equals("1")) {
                    selectObject += map.get(db.VALUE_NAME) + " | ";
                }
            }
        }
        if (selectObject.isEmpty()) {
            selectObject = "NONE";
        }
        selectObject = selectObject.replaceAll("\\|\\s$", "");
        tvSelectObject.setText(selectObject);
    }

    public void initBtnSelectObj(View view) {
        tvSelectObject = (TextView) myJQuery.getViewsByTagWithReset((ViewGroup) view, TextView.class).get(0);
        dialogView = selectObjDialog.onCreate();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectObjDialog.vHide(v);
            }
        });
    }

    private void dialogInflate(View dialogView) {
        View content = dialogView.findViewById(R.id.contentDialog);
        for (HashMap<String, String> map : listDevices) {
            FrameLayout rowCheckBox = (FrameLayout) inflater.inflate(R.layout.row_object, null);
            ViewGroup vg = (ViewGroup) rowCheckBox;
            CheckBox checkBox = (CheckBox) vg.getChildAt(0);
            checkBox.setText(map.get(db.VALUE_NAME));
            if (map.get(db.VALUE_SELECTED).equals("1")) {
                checkBox.setChecked(true);
            }
            ((ViewGroup) content).addView(rowCheckBox);
            checkBoxEvents(checkBox, map);
        }
    }

    private void checkBoxEvents(final CheckBox checkBox, final HashMap map) {
        listCheckBox.add(checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String id = map.get(db.UID).toString();
                if (checkBox.isChecked()) {
                    db.setValueSelected(id, true);
                    map.put(db.VALUE_SELECTED, "1");
                } else {
                    db.setValueSelected(id, false);
                    map.put(db.VALUE_SELECTED, "0");
                }
                setTextSelectedObj();
                regenScrollView();
            }
        });
    }

    private void okCancelListener(View dialogView) {
        FrameLayout btnOk = (FrameLayout) dialogView.findViewById(R.id.btn_ok);
        FrameLayout btnCancel = (FrameLayout) dialogView.findViewById(R.id.btn_cancel);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectObjDialog.onDismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CheckBox checkBox : listCheckBox) {
                    checkBox.setChecked(false);
                }
                selectObjDialog.onDismiss();
            }
        });
    }
}
