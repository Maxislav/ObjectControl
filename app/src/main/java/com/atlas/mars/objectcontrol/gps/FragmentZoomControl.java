package com.atlas.mars.objectcontrol.gps;



import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.atlas.mars.objectcontrol.R;

/**
 * Created by Администратор on 7/4/15.
 */
public class FragmentZoomControl extends Fragment implements View.OnClickListener {
    Activity activity;
    View view;
    private OnClickListener mListener;
    float density, width, height;
    public FragmentZoomControl(){
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_zoom, container, false);
         view.findViewById(R.id.zoomIn).setOnClickListener(this);
         view.findViewById(R.id.zoomOut).setOnClickListener(this);



        density = activity.getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (40*density), (int) (80*density));
        view.setLayoutParams(layoutParams);
       // view.findViewById(R.id.zoomOut).setOnClickListener(this);

        return view;
    }
    @Override
    public void onClick(View v) {
        Log.d("myLog", "+++ zoom in out");
        mListener = (OnClickListener)activity;
        mListener.onItemSelected(v);
    }
    public interface OnClickListener {
        public void onItemSelected(View v);
    };
}
