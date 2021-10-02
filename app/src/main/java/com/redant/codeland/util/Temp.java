package com.redant.codeland.util;

import java.util.ArrayList;

/**
 * Created by pjh on 2018/7/14.
 */

public class Temp {
    ArrayList<String> tt=new ArrayList<>();
    public void addtt(String string){
        tt.add(string);
    }
    public boolean equal(String string){
        if(tt.indexOf(string)==-1){
            return false;
        }
        else{
            return true;
        }
    }
    public String getsth(){
        return tt.get(0);
    }
}
