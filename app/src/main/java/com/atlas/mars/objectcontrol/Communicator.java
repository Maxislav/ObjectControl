package com.atlas.mars.objectcontrol;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Администратор on 4/2/15.
 */
public interface Communicator {
    public void setTextSelectObject(TextView setTextSelectedObj);
    public void initBtnSelectObj(View view);
    public void initBtnAddObj(View view);
    public void iniViewAllCommand(View view, LayoutInflater inflater);


}
