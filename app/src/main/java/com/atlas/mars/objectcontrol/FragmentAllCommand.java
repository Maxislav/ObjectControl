package com.atlas.mars.objectcontrol;

import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Администратор on 4/7/15.
 */
public class FragmentAllCommand extends MyFragmentView {
    public SearchView  searchView;
    final static String TAG = "myLog";
    LinearLayout mainLayout;
    String searchText;
    public FragmentAllCommand(MainActivity mainActivity, View viewFragment, LayoutInflater inflater){
        super(mainActivity, viewFragment, inflater);
    }
    @Override
    public void onInit(){
        rowCreator = new RowCreator(viewFragment, inflater);
        searchText ="";
        searchView =(SearchView)viewFragment.findViewById(R.id.searchView);
        mainLayout = (LinearLayout)mainActivity.findViewById(R.id.mainLayout);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                searchView.clearFocus();
                //searchView.onActionViewCollapsed();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                Log.d(TAG, "onQueryTextChange: " + newText);

                //todo Запрос параметров
               // getListDataLike(newText);
                /*if(newText.isEmpty()){
                    searchView.onActionViewCollapsed();
                    searchView.setQuery("", false);
                }*/
                return false;
            }
        });

        searchView.setOnFocusChangeListener(new SearchView.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange: " + hasFocus);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d(TAG, " searchView onClose");

                regenParams();
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "searchView onClick "+ searchView.isFocused());
                searchView.onActionViewExpanded();
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if(!queryTextFocused && searchText.isEmpty()) {
                    searchView.onActionViewCollapsed();
                    searchView.setQuery("", false);
                }
            }
        });
        onDraw();

    }
    @Override
    public void regenParams() {
        onRedraw();
    }

    private void onDraw(){
        hashMapRow = new HashMap<>();
        ArrayList<HashMap> arrayList = db.getAllCommand();
        for (HashMap<String, String> map: arrayList){
            FrameLayout row = rowCreator.create(map);
            String id = map.get(DataBaseHelper.UID);
            hashMapRow.put(id, row);
            setListenerDelRow(row, id);
            ArrayList<View> viewArrayList = myJQuery.findViewByTagClass(row, ImageView.class);
            ImageView imgFavorite = (ImageView)viewArrayList.get(2);
            ImageView bacGroutdImg = (ImageView)viewArrayList.get(0);
            if(map.get(db.VALUE_FAVORITE).equals("1")){
               imgFavorite.setBackgroundResource(R.drawable.btn_favorite);
            }else{
                bacGroutdImg.setBackgroundResource(R.drawable.bitmap_button_unfaforite);
            }
            setListenerFavorite(imgFavorite,bacGroutdImg, map);
        }
    }

    private void setListenerFavorite(final ImageView imgFavorite, final ImageView bacGroutdImg, final HashMap<String, String> map){
        final String id = map.get(db.UID);

        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(map.get(db.VALUE_FAVORITE).equals("1")){
                    db.setValueFavorite(id, false);
                    map.put(db.VALUE_FAVORITE, "0");
                    imgFavorite.setBackgroundResource(R.drawable.btn_unfavorite);
                    bacGroutdImg.setBackgroundResource(R.drawable.bitmap_button_unfaforite);
                }else{
                    db.setValueFavorite(id, true);
                    map.put(db.VALUE_FAVORITE, "1");
                    imgFavorite.setBackgroundResource(R.drawable.btn_favorite);
                    bacGroutdImg.setBackgroundResource(R.drawable.bitmap_button);
                }

                mainActivity.connectionFragment();
            }


        });
    }

    private void setListenerDelRow(FrameLayout _row, final String _id){
        final FrameLayout row = _row;
        ImageView imgMinus = (ImageView)myJQuery.findViewByTagClass(row, ImageView.class).get(1);

        imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //db.delCommand(_id);
                if(db.delCommand(_id)){
                    hashMapRow.remove(_id);
                    ((LinearLayout)row.getParent()).removeView(row);
                }

                mainActivity.connectionFragment();
            }
        });
    }

    public void onRedraw(){
        ((LinearLayout)viewFragment.findViewById(R.id.mainLayout)).removeAllViews();
        onDraw();
    }


    public void showMinus(int visible){
        for (Map.Entry entry : hashMapRow.entrySet()) {
            ArrayList<View> viewArrayList = myJQuery.findViewByTagClass((FrameLayout) entry.getValue(), ImageView.class);
            ImageView img = (ImageView)viewArrayList.get(1);
            img.setVisibility(visible);
        }
        dialogEndDelShow();
    }

    public void showFavorite(int visible){
        for (Map.Entry entry : hashMapRow.entrySet()) {
            ArrayList<View> viewArrayList = myJQuery.findViewByTagClass((FrameLayout) entry.getValue(), ImageView.class);
            ImageView img = (ImageView)viewArrayList.get(2);
            img.setVisibility(visible);
        }
        dialogEndDelShow();
    }

    private void dialogEndDelShow(){
        if(dialog==null){
            dialog = inflater.inflate(R.layout.dialod_end_del, null);
            pw = new PopupWindow(dialog, FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
            FrameLayout btnEndDel = (FrameLayout)myJQuery.findViewByTagClass((ViewGroup) dialog, FrameLayout.class).get(0);
            btnEndDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMinus(View.INVISIBLE);
                    showFavorite(View.INVISIBLE);
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
                               // Intent questionIntent = new Intent( mainActivity, MakeCommandActivity.class);
                               // questionIntent.putExtra(mainActivity.FROM, mainActivity.TO_ADD_COMMAND);
                               // mainActivity.startActivityForResult(questionIntent, mainActivity.CHOOSE_THIEF);
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
