package com.atlas.mars.objectcontrol;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class PageFragment extends Fragment implements View.OnClickListener  {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    int pageNumber;
    int backColor;
    static ArrayList<View> fragmetView;
    public static Button selectObjButton;

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

        fragmetView = new ArrayList<>();

        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

        Random rnd = new Random();
        backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        switch (pageNumber) {
            case 0:

                view = inflater.inflate(R.layout.fragment_0_home, null);
                selectObjButton = (Button) (view.findViewById(R.id.selectButton));

                selectObjButton.setOnClickListener(this);

              //  MainActivity.setSelectObjButton(selectObjButton);




                break;
            default:
                view = inflater.inflate(R.layout.fragment, null);
                TextView tvPage = (TextView) view.findViewById(R.id.tvPage);
                tvPage.setText("Page " + (pageNumber + 1));
                tvPage.setBackgroundColor(backColor);
                break;

        }

        return view;
    }



    @Override
    public void onClick(View v) {

        int buttonIndex = translateIdToIndex(v.getId());


        OnSelectedButtonListener listener = (OnSelectedButtonListener) getActivity();
        listener.onButtonSelected(buttonIndex);

       /* Toast.makeText(getActivity(), String.valueOf(buttonIndex),
                Toast.LENGTH_SHORT).show();*/

    }
    public interface OnSelectedButtonListener {
        void onButtonSelected(int buttonIndex);
    }

    int translateIdToIndex(int id) {
        int index = -1;
        switch (id) {
            case R.id.selectButton:
                index = 1;
                break;
          /*  case R.id.button2:
                index = 2;
                break;
            case R.id.button3:
                index = 3;
                break;*/
        }
        return index;
    }
}