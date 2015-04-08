package com.atlas.mars.objectcontrol;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Администратор on 4/7/15.
 */
public class FragmentAllCommand extends FragventView{

    public FragmentAllCommand(MainActivity mainActivity, View viewFragment, LayoutInflater inflater){
        super(mainActivity, viewFragment, inflater);

       /* this.mainActivity = mainActivity;
        this.viewFragment = viewFragment;
        this.inflater = inflater;
        myJQuery = new MyJQuery();
        db = new DataBaseHelper(mainActivity);
       // rowCreator = new RowCreator(viewFragment, inflater);
        onInit();*/
    }
    @Override
    public void onInit(){
        rowCreator = new RowCreator(viewFragment, inflater);
        onDraw();
        editCommand();
    }

    private void onDraw(){
        hashMapRow = new HashMap<>();
        ArrayList<HashMap> arrayList = db.getAllCommand();
        for (HashMap<String, String> map: arrayList){
            FrameLayout row = rowCreator.create(map);
            String id = map.get(DataBaseHelper.UID);
            hashMapRow.put(id, row);
            setListenerDelRow(row, id);
        }
    }

    private void setListenerDelRow(FrameLayout _row, final String _id){
        final FrameLayout row = _row;
        ImageView imgMinus = (ImageView)myJQuery.getViewsByTagWithReset( row, ImageView.class).get(1);
        imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //db.delCommand(_id);
                if(db.delCommand(_id)){
                    hashMapRow.remove(_id);
                    ((LinearLayout)row.getParent()).removeView(row);
                }
            }
        });


    }

    public void onRedraw(){
        ((LinearLayout)viewFragment.findViewById(R.id.mainLayout)).removeAllViews();
        onDraw();
    }

    private void editCommand(){
        final FrameLayout btnEdit = (FrameLayout)viewFragment.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenuEditCommands(v);
            }
        });
    }

    private void showMinus(int visible){

        for (Map.Entry entry : hashMapRow.entrySet()) {
            ImageView img = (ImageView)myJQuery.getViewsByTagWithReset((FrameLayout)entry.getValue(), ImageView.class).get(1);
            img.setVisibility(visible);
           //System.out.println("Key: " + entry.getKey() + " Value: " + entry.getValue());
        }
        dialogEndDelShow();
    }

    private void dialogEndDelShow(){
        if(dialog==null){
            dialog = inflater.inflate(R.layout.dialod_end_del, null);
            pw = new PopupWindow(dialog, FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
            FrameLayout btnEndDel = (FrameLayout)myJQuery.getViewsByTagWithReset((ViewGroup)dialog, FrameLayout.class).get(0);
            btnEndDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMinus(View.INVISIBLE);
                    pw.dismiss();
                }
            });
        }
        pw.showAtLocation(viewFragment, Gravity.TOP, 0, 40);
    }

    private void showPopupMenuEditCommands(View v){
        PopupMenu popupMenu = new PopupMenu(mainActivity, v);
        popupMenu.inflate(R.menu.edit_commands);
        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.create:
                                Intent questionIntent = new Intent( mainActivity, MakeCommandActivity.class);
                                questionIntent.putExtra(mainActivity.FROM, mainActivity.TO_ADD_COMMAND);
                                mainActivity.startActivityForResult(questionIntent, mainActivity.CHOOSE_THIEF);
                                return true;
                            case R.id.del:
                                showMinus(View.VISIBLE);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
        popupMenu.show();
    }

}
