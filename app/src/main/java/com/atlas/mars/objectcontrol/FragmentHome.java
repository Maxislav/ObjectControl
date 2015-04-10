package com.atlas.mars.objectcontrol;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atlas.mars.objectcontrol.dialogs.DialogSelectObj;
import com.atlas.mars.objectcontrol.dialogs.DialogSend;

import java.util.ArrayList;
import java.util.HashMap;

//import com.atlas.mars.objectcontrol.dialogs.SelectObjDialog00;

/**
 * Created by mars on 4/8/15.
 */
public class FragmentHome extends MyFragmentView {

    FrameLayout btnSelectObj; //Кнопка выбрать объект
    FrameLayout btnSend;
    TextView tvSelectObject; //Текст этой кнопки
    DialogSelectObj dialogSelectObj;
    DialogSend dialogSend;
    String selectObject;
    View viewDialogSelectObj; //View диалог выбора объекта
    View viewDialogSend; //View диалог отправки
    public final String SELECT_FOR_SEND = "selectForSend";
    ArrayList<CheckBox> listCheckBox;
    static HashMap<String, String> mapSelectObjects;
    public static ArrayList<HashMap> listDevices;
    public static ArrayList<HashMap> favoriteCommand;
    LinearLayout mainLayout;


    FragmentHome(MainActivity mainActivity, View viewFragment, LayoutInflater inflater) {
        super(mainActivity, viewFragment, inflater);
    }


    @Override
    public void onInit() {
        //rowCreator = new RowCreator(viewFragment, inflater);

        dialogSelectObj = new DialogSelectObj(mainActivity);
        dialogSend = new DialogSend(mainActivity);
        viewDialogSend = dialogSend.onCreate();
        dialogSend.initFragment(this);


        mapSelectObjects = new HashMap<>();
        listCheckBox = new ArrayList<>();
        btnSelectObj = (FrameLayout) myJQuery.findViewByTagClass((ViewGroup) viewFragment, FrameLayout.class).get(0);
        tvSelectObject = (TextView) myJQuery.findViewByTagClass((ViewGroup) btnSelectObj, TextView.class).get(0);
        btnSend = (FrameLayout)viewFragment.findViewById(R.id.send);
        initBtnSend();
        //initOkCancelSend(viewDialogSend);
       // getViewDialogSend();
       // viewDialogSend = getViewDialogSend();
        initBtnSelectObj(btnSelectObj);
        getListDevices();
        setTextSelectedObj();
        dialogInflate(viewDialogSelectObj);
        okCancelListener(viewDialogSelectObj);
        mainLayout = (LinearLayout)viewFragment.findViewById(R.id.mainLayout);
        getFavoriteCommand();

    }

    @Override
    public void regenParams() {
        listDevices = db.getListDevices();
        View content = viewDialogSelectObj.findViewById(R.id.contentDialog);
        ((LinearLayout)content).removeAllViews();
        dialogInflate(viewDialogSelectObj);
    }



    private void initBtnSend(){
       btnSend.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dialogSend.vHide(v);
           }
       });
    }



    public void regenScrollView(){
        mainLayout.removeAllViews();
        getFavoriteCommand();
    }

    private void getFavoriteCommand(){
        favoriteCommand = db.getFavoriteCommand();
        Log.d("dd", "");
        for(HashMap<String,String> map : favoriteCommand){
            map.put(SELECT_FOR_SEND, "0");
            createRow(map);
        }
    }

    private void createRow(HashMap<String,String> map){
        FrameLayout row = (FrameLayout)inflater.inflate(R.layout.row_command, null);
        ArrayList<View> arrayTextView = myJQuery.findViewByTagClass(row, TextView.class);
        ArrayList<View> arrayImgs = myJQuery.findViewByTagClass(row, ImageView.class);
        ImageView imageBackground = (ImageView)arrayImgs.get(0);
        ImageView imageSms = (ImageView)arrayImgs.get(3);

        ((TextView)arrayTextView.get(0)).setText(map.get(db.VALUE_NAME));
        ((TextView)arrayTextView.get(1)).setText(map.get(db.VALUE_COMMAND));
        ((TextView)arrayTextView.get(2)).setText(map.get("valueDeviceName"));
        ArrayList<View> arrayImgView = myJQuery.findViewByTagClass(row, ImageView.class);
        arrayImgView.get(2).setVisibility(View.INVISIBLE);
        mainLayout.addView(row);
        setClickListenerRow(row, imageBackground, imageSms ,map);
    }

    private void setClickListenerRow(final FrameLayout row, final ImageView imageBackground, final  ImageView imageSms, final HashMap<String, String> map){
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(map.get(SELECT_FOR_SEND).equals("0")){
                    imageBackground.setBackgroundResource(R.drawable.bitmap_button_to_send);
                    imageSms.setVisibility(View.VISIBLE);
                    map.put(SELECT_FOR_SEND, "1");
                }else{
                    imageBackground.setBackgroundResource(R.drawable.bitmap_button);
                    imageSms.setVisibility(View.INVISIBLE);
                    map.put(SELECT_FOR_SEND, "0");
                }
            }
        });
        //нажатие смс картинки
        imageSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(mainActivity, map.get(db.UID), Toast.LENGTH_LONG);
                toast.show();
                imageBackground.setBackgroundResource(R.drawable.bitmap_button);
                imageSms.setVisibility(View.INVISIBLE);
                map.put(SELECT_FOR_SEND, "0");
            }
        });

    }




    private void getListDevices() {
        listDevices = db.getListDevices();
        Log.d("dd", "");
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
        tvSelectObject = (TextView) myJQuery.findViewByTagClass((ViewGroup) view, TextView.class).get(0);
        viewDialogSelectObj = dialogSelectObj.onCreate();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectObj.vHide(v);
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
                dialogSelectObj.onDismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CheckBox checkBox : listCheckBox) {
                    checkBox.setChecked(false);
                }
                dialogSelectObj.onDismiss();
            }
        });
    }
}
