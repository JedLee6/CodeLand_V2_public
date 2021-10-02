package com.redant.codeland;

import android.content.Context;
import android.util.AttributeSet;

import com.redant.codeland.app.MyApplication;

import androidx.appcompat.widget.AppCompatButton;

public class MyButton extends AppCompatButton {
    //private final MediaPlayer mediaPlayer=MediaPlayer.create(getContext(), R.raw.loginmusic);
    /*if(!MyApplication.){
        mediaPlayer.setVolume(0f,0f);
    }
    else{
        mediaPlayer.setVolume(MyApplication.voiceCurrent/MyApplication.voiceMax,MyApplication.voiceCurrent/MyApplication.voiceMax);
    }*/
    public MyButton(Context context) {
        super(context);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean performClick(){
        MyApplication.playClickVoice(getContext());
        return super.performClick();
    }
}
