package com.atlas.mars.objectcontrol;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
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
    private final   MyJQuery myJQuery = new MyJQuery();
    public static Button selectObjButton;
    static HashMap< Integer, View> fragmetMapView; //массив фрагментов
    RowCreator rowCreator;
    static DataBaseHelper db;
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
        db = new DataBaseHelper(getActivity());
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
        Communicator communicator;
        switch (pageNumber) {
            case 0:

                view = inflater.inflate(R.layout.fragment_0_home, null);
                FrameLayout btnSelectObj =(FrameLayout)myJQuery.getViewsByTagWithReset((ViewGroup)view,FrameLayout.class).get(0);
                communicator = (Communicator)getActivity();
                TextView tvSelectObject = (TextView)myJQuery.getViewsByTagWithReset((ViewGroup)btnSelectObj, TextView.class ).get(0);
                FrameLayout btnAddObj = (FrameLayout)view.findViewById(R.id.addObj);
                communicator.initBtnAddObj(btnAddObj);
                communicator.setTextSelectObject(tvSelectObject);
                communicator.initBtnSelectObj(btnSelectObj);

                communicator.iniViewHome(view,inflater);
               // TextView tvSelectObject = (TextView)view.findViewById(R.id.tvSelectObject);






                break;
            case 1:
                communicator = (Communicator)getActivity();
                view = inflater.inflate(R.layout.fragment_1_all_commands, null);
                communicator.iniViewAllCommand(view, inflater);


               /* rowCreator = new RowCreator(view, inflater);
                ArrayList <HashMap> arrayList = db.getAllCommand();
                for (HashMap<String, String> map: arrayList){
                    rowCreator.create(map);
                }*/

               // FrameLayout btnEdit = (FrameLayout)view.findViewById(R.id.btnEdit);
               // communicator.editCommand(btnEdit);


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
      //  int buttonIndex = translateIdToIndex(v.getId());
        OnSelectedButtonListener listener = (OnSelectedButtonListener) getActivity();
       // listener.onButtonSelected(buttonIndex, v);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: " + pageNumber);
    }
    public interface OnSelectedButtonListener {
        //void onButtonSelected(int buttonIndex, View v);

    }

    /*int translateIdToIndex(int id) {
        int index = -1;
        switch (id) {
            case R.id.selectButton:
                index = 1;
                break;
          *//*  case R.id.button2:
                index = 2;
                break;
            case R.id.button3:
                index = 3;
                break;*//*
        }
        return index;
    }*/
}