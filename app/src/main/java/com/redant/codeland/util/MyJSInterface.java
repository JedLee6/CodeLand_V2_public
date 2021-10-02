package com.redant.codeland.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;

//import android.content.SharedPreferences;

/**
 * 用于将此对象注入JS代码中，使得在JS中可以调用本地的一些方法
 * Created by Administrator on 2018-03-31.
 */

public class MyJSInterface {
    private Context mContext;
    private Handler handler;
    public MyJSInterface(Context context, Handler handler){
        this.mContext = context;
        this.handler=handler;
    }

    /**
     * 展示游戏运行结果
     * @param rating
     * @param msg
     */
    @JavascriptInterface
    //showGameResult并不是显示的说没有被调用，而是在在tadpole的js里面被调用了
    public void showGameResult(int rating, String msg){

//        Util.showDialog(mContext,msg, rating,handler);
//        SharedPreferences.Editor editor=mContext.getSharedPreferences("AllLevel",MODE_PRIVATE).edit();
//        editor.putInt("RatingMsg",rating);
//        editor.commit();
        if(rating==0){
            Message message=new Message();
            message.what=4;
            handler.sendMessage(message);
        }else if(rating==-2){
            Message message=new Message();
            message.what=5;
            handler.sendMessage(message);
        }else{
            Message message=new Message();
            if(rating==1){
                message.what=6;
            }else if(rating==2){
                message.what=7;
            }else if(rating==3){
                message.what=8;
            }
            handler.sendMessage(message);
        }

    }

    /**
     * 展示游戏运行结果
     * @param rating
     * @param msg
     */
    @JavascriptInterface
    //showGameResult并不是显示的说没有被调用，而是在在tadpole的js里面被调用了
    public void showGameBoxResult(int rating, String msg){

        if(rating==0){
            Message message=new Message();
            message.what=4;
            handler.sendMessage(message);
        }else{
            Message message=new Message();
            if(rating==1){
                message.what=6;
            }else if(rating==2){
                message.what=7;
            }else if(rating==3){
                message.what=8;
            }
            handler.sendMessage(message);
        }
    }

}

