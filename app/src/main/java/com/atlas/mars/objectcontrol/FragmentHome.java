package com.atlas.mars.objectcontrol;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by mars on 4/8/15.
 */
public class FragmentHome extends MyFragmentView {

    FragmentHome(MainActivity mainActivity, View viewFragment, LayoutInflater inflater){
        super(mainActivity, viewFragment, inflater);
    }

    @Override
    public void onInit() {
        //rowCreator = new RowCreator(viewFragment, inflater);
    }


}
