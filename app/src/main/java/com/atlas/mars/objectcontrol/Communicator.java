package com.atlas.mars.objectcontrol;

import android.content.BroadcastReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Администратор on 4/2/15.
 */
public interface Communicator {
    public void setTextSelectObject(TextView setTextSelectedObj);
    public void initBtnSelectObj(View view);
    public void initBtnAddObj(View view);
    public void initViewAllCommand(View view, LayoutInflater inflater);
    public void initViewHome(View view, LayoutInflater inflater);
    public void connectionFragment();
    public void initReceivers(BroadcastReceiver receiver, BroadcastReceiver receiverDeliver);


}
