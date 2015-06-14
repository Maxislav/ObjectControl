package com.atlas.mars.objectcontrol.gps;

import android.util.Log;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LoadFile {
    String path;
    public LoadFile(String path){
        this.path = path;
    }
    public String getText(){
        String txt = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            StringBuilder textBuilder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                textBuilder.append(line);
            }
            txt = textBuilder.toString();
            Log.d(MapsActivity.TAG,textBuilder.toString());
        } catch (FileNotFoundException e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.e(MapsActivity.TAG, e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(MapsActivity.TAG, e.toString());

        } finally{
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return txt;
    }
}
