package com.redant.codeland.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.listener.OnLayoutInflatedListener;
import com.app.hubert.guide.model.GuidePage;
import com.google.blockly.android.AbstractBlocklyActivity;
import com.redant.codeland.R;
import com.redant.codeland.app.MyApplication;
import com.redant.codeland.util.AppLanguageUtils;

/**
 * 自定义的一个基础BlocklyActivity，默认全屏，隐藏原有的ActionBar,引入自定义布局，使用自定义运行按钮
 * Created by Administrator on 2017-12-07.
 */

public abstract  class  BaseBlocklyActivity extends AbstractBlocklyActivity implements View.OnClickListener{
    private Button btnRun;
    private Button btnShow;
    private Button btnContinue;
    private Button btnAgain;
    private Button btnExit;
    private Button btnMusic;
    private Button btnHelp;
    private AudioManager audioManager;
    private DrawerLayout drawerLayout;

    //挑战页面中的 电灯泡按钮 点击跳转至 知识笔记 用于查看知识内容
    private Button button_look_notebook;
    //帮助按钮
    private Button button_enlighten_help;

    protected int rating = 0;//匹配的评分，用于Dialog的RatingBar中
    //获取用户点击的关卡值
    protected int clickedLevel=1;//表示选择关卡难度，从sh中读取，键clickedLevel
    protected boolean flag=false;
    protected int maxLevel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏Actionbar
        getSupportActionBar().hide();
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(!MyApplication.isPlaying){
            btnMusic.setBackgroundResource(R.mipmap.music_off);
        }else{
            btnMusic.setBackgroundResource(R.mipmap.music_on);
        }


    }

    //每个存在语言切换的Activity需要写
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase));
    }

    @Override
    protected View onCreateContentView(int containerId) {
        View root = getLayoutInflater().inflate(R.layout.layout_blockly_run,null);
        //自定义运行按钮
        btnRun = root.findViewById(R.id.blockly_fbtn_run);
        btnContinue=root.findViewById(R.id.blockly_fbtn_continue);
        btnAgain=root.findViewById(R.id.blockly_fbtn_again);
        btnExit=root.findViewById(R.id.blockly_fbtn_exit);
        btnHelp=root.findViewById(R.id.blockly_fbtn_help);
        btnMusic=root.findViewById(R.id.blockly_fbtn_music);
        btnShow=root.findViewById(R.id.blockly_fbtn_show);
        drawerLayout=root.findViewById(R.id.drawer_layout);
        button_look_notebook=root.findViewById(R.id.enlighten_look_notebook);
        button_enlighten_help=root.findViewById(R.id.enlighten_help);
        btnRun.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        btnAgain.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        btnMusic.setOnClickListener(this);
        button_look_notebook.setOnClickListener(this);
        button_enlighten_help.setOnClickListener(this);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        return root;
    }

    //父亲活动的新手引导界面，因为有6个继承父亲，界面基本一样，所以super.调用父亲的新手引导界面就很方便了
    public void BaseBlocklyActivityNewNewbieGuide(){

        NewbieGuide.with(this).setLabel("page").alwaysShow(true)//true:程序员开发调试时使用，每次打开APP显示新手引导;false:只在用户第一次打开APP显示新手引导
                .addGuidePage(
                        GuidePage.newInstance()
//可以一个引导页多个高亮，多加几个.addHighLight()即可
                                .addHighLight(button_look_notebook)
                                .addHighLight(btnRun)
                                .addHighLight(button_enlighten_help)
                                .setLayoutRes(R.layout.base_blockly_activity_newbie_guide)//引导页布局，点击跳转下一页或者消失引导层的控件id
                                .setEverywhereCancelable(true)//是否点击任意地方跳转下一页或者消失引导层，默认true
//                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
//                                    @Override
//                                    //onLayoutInflated很重要，返回了 引导层的view，一定要利用好这个view，比如为引导层的view设定监听事件
//                                    //如果想要在引导页findViewByID一定要加上 view. 否则空指针报错
//                                    public void onLayoutInflated(View view) {
//                                        TextView textViewNewbieGuide = view.findViewById(R.id.textview_mainactvitiy_newbie_guide);
//
//                                    }
//                                })
                ).show();
    }

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.blockly_fbtn_run:
                if (getController().getWorkspace().hasBlocks()) {
                    onRunCode();
                } else {
                    Log.i("TAG", "工作区中没有块");
                }
                break;
            case R.id.blockly_fbtn_show:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.blockly_fbtn_continue:
                drawerLayout.closeDrawers();
                break;
            case R.id.blockly_fbtn_again:
                reload();
                drawerLayout.closeDrawers();
                break;
            case R.id.blockly_fbtn_exit:
                finish();
                break;
            case R.id.blockly_fbtn_music:
                if(MyApplication.isPlaying){
                    //audioManager.setStreamMute(AudioManager.STREAM_MUSIC,true);
                    btnMusic.setBackgroundResource(R.mipmap.music_off);
                    //mediaPlayer.setVolume(0f,0f);
                    MyApplication.isPlaying=false;
                    //Toast.makeText(MainActivity.this,"isPlaying"+MyApplication.isPlaying,Toast.LENGTH_SHORT).show();
                }else{
                    //audioManager.setStreamMute(AudioManager.STREAM_MUSIC,false);
                    //audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,10,20);
                    btnMusic.setBackgroundResource(R.mipmap.music_on);
                    MyApplication.voiceMax= audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);// 1
                    MyApplication.voiceCurrent= audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
                    //mediaPlayer.setVolume(MyApplication.voiceCurrent/MyApplication.voiceMax,MyApplication.voiceCurrent/MyApplication.voiceMax);
                    MyApplication.isPlaying=true;
                    //Toast.makeText(MainActivity.this,"isPlaying"+MyApplication.isPlaying,Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.blockly_fbtn_help:
                showHelp();
                drawerLayout.closeDrawers();
                break;
            case R.id.enlighten_look_notebook:
                Intent intent=new Intent(BaseBlocklyActivity.this,EnlightenActivity.class);
                startActivity(intent);
                break;
            case R.id.enlighten_help:
                showHelp();
                break;
        }
    }
    protected void reload(){
        //
    }
    protected void showHelp(){
        //这里不写，留着给子类重写
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                //btnMusic.setBackgroundResource(R.mipmap.music_off);
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                MyApplication.voiceMax= audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
                MyApplication.voiceCurrent= audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                MyApplication.voiceMax= audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
                MyApplication.voiceCurrent= audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Handler在Dialog点击确定按钮时触发，重新加载英语单词
     */
    protected Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                finish();
            }
            else if(msg.what==2){
                if(rating==3)//全部拼写正确，全部重新生成
                {loadCase();}
                //没有完全拼写正确，不打乱方块，继续拼接
            }
            else if(msg.what==3)
            {
                if(rating>=2 )//全部拼写正确，可以跳到下一关卡
                {
                    SharedPreferences.Editor editor=getSharedPreferences("AllLevel",MODE_PRIVATE).edit();
                    if(flag){
                        clickedLevel++;
                        editor.putInt("clickedLevel",clickedLevel);
                    }
                    editor.commit();
                    loadCase();
                }
                else{
                    Toast.makeText(BaseBlocklyActivity.this,"你还没有全部拼对喔，让我们继续完善吧",Toast.LENGTH_SHORT).show();
                }
            }
            //重置比分，评判结束后，比例值清零
            rating=0;
        }
    };

    public void loadModel(){}
    public void loadCase(){
        //重置工作空间
        getController().resetWorkspace();

        loadModel();

    }
}
