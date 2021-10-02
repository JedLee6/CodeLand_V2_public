package com.redant.codeland;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.listener.OnLayoutInflatedListener;
import com.app.hubert.guide.model.GuidePage;
import com.redant.codeland.app.MyApplication;
import com.redant.codeland.bmob.RegisterAndLoginActivity;
import com.redant.codeland.scratchgame.HubActivity;
import com.redant.codeland.scratchgame.scratchgameui.ProgramActivity;
import com.redant.codeland.ui.GameBaseActivity;
import com.redant.codeland.util.AppLanguageUtils;
import com.redant.codeland.util.Util;
import com.umeng.analytics.MobclickAgent;
import com.yatoooon.screenadaptation.ScreenAdapterTools;


/**
 * Created by Administrator on 2017-12-18.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MyButton ivEnghten;
    private MyButton  ivGame, ivCoding,buttonSet,buttonMusic,buttonHelp,ivCar;

    //联系我们 的 按钮
    private MyButton buttonContactUs;

    //隐私政策 的 按钮
    private Button buttonPrivacy;

    private MediaPlayer mediaPlayer;
    private ParallelViewHelper parallelViewHelper;
    private AudioManager audioManager;
    private ImageView imageView_main_logo;

    private boolean isVisible = true;
    public boolean music = true;

    private Thread thread;

    private ImageView imageView_main_background;

    /** Run-time Permissions 申请权限用到的一些常量 */
    private final int SCRATCHJR_CAMERA_MIC_PERMISSION = 1;
    public int cameraPermissionResult = PackageManager.PERMISSION_DENIED;
    public int micPermissionResult = PackageManager.PERMISSION_DENIED;

    //从ScratchJrActivity里面借过来的申请权限的方法，用来提前申请权限，避免进入home.html再申请权限时会导致没有权限，第一次加载失败
    public void requestPermissions() {
        cameraPermissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        micPermissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        String[] desiredPermissions;
        if (cameraPermissionResult != PackageManager.PERMISSION_GRANTED
                && micPermissionResult != PackageManager.PERMISSION_GRANTED) {
            desiredPermissions = new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
            };
        } else if (cameraPermissionResult != PackageManager.PERMISSION_GRANTED) {
            desiredPermissions = new String[]{Manifest.permission.CAMERA};
        } else if (micPermissionResult != PackageManager.PERMISSION_GRANTED) {
            desiredPermissions = new String[]{Manifest.permission.RECORD_AUDIO};
        } else {
            return;
        }

        ActivityCompat.requestPermissions(this,
                desiredPermissions,
                SCRATCHJR_CAMERA_MIC_PERMISSION);
    }

    //从ScratchJrActivity里面借过来的申请权限的方法，用来提前申请权限，避免进入home.html再申请权限时会导致没有权限，第一次加载失败
    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        if (requestCode == SCRATCHJR_CAMERA_MIC_PERMISSION) {
            int permissionId = 0;
            for (String permission : permissions) {
                if (permission.equals(Manifest.permission.CAMERA)) {
                    cameraPermissionResult = grantResults[permissionId];
                }
                if (permission.equals(Manifest.permission.RECORD_AUDIO)) {
                    micPermissionResult = grantResults[permissionId];
                }
                permissionId++;
            }
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //changeAppLanguage(getResources(),MyApplication.languageFlag);

        setContentView(R.layout.activity_main);

        MyApplication.disableAPIDialog();

        //适配屏幕引入语句
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());

        //申请 相机+录音权限
        requestPermissions();

        ivEnghten = findViewById(R.id.main_iv_enlighten);
        ivGame = findViewById(R.id.main_iv_game);
        ivCoding = findViewById(R.id.main_iv_coding);
//        ivGit=findViewById(R.id.main_iv_git);
        buttonContactUs=findViewById(R.id.button_contact_us);
        buttonPrivacy=findViewById(R.id.button_privacy);
        ivCar=findViewById(R.id.main_iv_car);
        buttonSet=findViewById(R.id.button_set);
        buttonMusic=findViewById(R.id.button_music);
        buttonHelp=findViewById(R.id.button_help);
        imageView_main_logo=findViewById(R.id.image_main_logo);
        imageView_main_background=findViewById(R.id.main_background);
        ivEnghten.setOnClickListener(this);
        ivGame.setOnClickListener(this);
        ivCoding.setOnClickListener(this);
//        ivGit.setOnClickListener(this);
        ivCar.setOnClickListener(this);
        buttonSet.setOnClickListener(this);
        buttonMusic.setOnClickListener(this);
        buttonHelp.setOnClickListener(this);
        buttonContactUs.setOnClickListener(this);
        buttonPrivacy.setOnClickListener(this);

        //视差动画 实例化
        parallelViewHelper = new ParallelViewHelper(this, findViewById(R.id.main_background),20,0 );

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //每次刚打开APP，正确初始化 语言按钮对应的图片+文字
        if(MyApplication.languageFlag==1){
            ivEnghten.setText("动画");
            ivGame.setText("游戏");
            ivCoding.setText("程序");
//            ivGit.setText("论坛");
            ivCar.setText("小车");
            imageView_main_logo.setImageResource(R.mipmap.activity_main_logo2_chinese);
            ivEnghten.setVisibility(View.VISIBLE);
        }
        else{
            ivEnghten.setText("Initiation");
            ivGame.setText("Games");
            ivCoding.setText("Programming");
//            ivGit.setText("Git");
            ivCar.setText("Car");
            imageView_main_logo.setImageResource(R.mipmap.activity_main_logo2_english);
            ivEnghten.setVisibility(View.GONE);
        }



        //是否第一次打开APP，是则运行，否则不在此运行
        if(MyApplication.isFirstRun("MainActivityNewbieGuide"))
        {
            MainActivityNewbieGuide();
        }

        //从SharedPreferences中读取用户是否同意隐私政策，0代表未同意
        SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
        int privacyFlag=sharedPreferences.getInt("privacyFlag",0);
        //若用户未同意隐私政策，那么强制弹出隐私政策对话框
        if(privacyFlag==0){
            buttonPrivacy.performClick();
        }
    }

    //每个存在语言切换的Activity需要写
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase));
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.main_iv_enlighten:
                intent=new Intent(this, HubActivity.class);
                intent.putExtra("part",1);
                startActivity(intent);
//                startActivity(new Intent(this, ScratchAnimationBaseActivity.class));
                break;
            case R.id.main_iv_game:
//                startActivity(new Intent(this, ScratchGameBaseActivity.class));
                intent=new Intent(this, HubActivity.class);
                intent.putExtra("part",2);
                startActivity(intent);
                break;
            case R.id.main_iv_coding:
                //startActivity(new Intent(this, CodingBaseActivity.class));
                //2019.3.17 测试版
                intent=new Intent(this,ProgramActivity.class);
                //intent=new Intent(this,CodingBaseActivity.class);
                intent.putExtra("part",3);
                startActivity(intent);
                break;
//            case R.id.main_iv_git:
//                intent=new Intent(this, RegisterAndLoginActivity.class);
//                intent.putExtra("part",4);
//                startActivity(intent);
//                break;
            case R.id.main_iv_car:
                intent=new Intent(this, GameBaseActivity.class);
                intent.putExtra("part",5);
                startActivity(intent);
                break;
            case R.id.button_set:
                if(buttonMusic.getVisibility()==View.INVISIBLE)
                {
                    buttonMusic.setVisibility(View.VISIBLE);
                    buttonHelp.setVisibility(View.VISIBLE);
                }
                else {
                    buttonMusic.setVisibility(View.INVISIBLE);
                    buttonHelp.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.button_music:
                if(MyApplication.isPlaying){
                    buttonMusic.setBackgroundResource(R.mipmap.music_off);
                    mediaPlayer.setVolume(0f,0f);
                    MyApplication.playClickVoice(MyApplication.getContext(),"setVoice");
                    MyApplication.isPlaying=false;
                }else{
                    buttonMusic.setBackgroundResource(R.mipmap.music_on);
                    mediaPlayer.setVolume(1,1);
                    mediaPlayer.start();
                    MyApplication.isPlaying=true;
                }
                break;
            case R.id.button_contact_us:
                Util.showContactUsDialog(MainActivity.this);
                break;
            case R.id.button_privacy:
                Util.showPrivacyPolicy(MainActivity.this);
                break;
            case R.id.button_help:
                MainActivityNewbieGuide();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                //buttonMusic.setBackgroundResource(R.mipmap.music_off);
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                //buttonMusic.setBackgroundResource(R.mipmap.music_on);
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

    @Override
    protected void onDestroy() {
        //videoView.suspend();
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer=null;
    }

    /**
     * 友盟Session启动、App使用时长等基础数据统计接口API
     * 在App中每个Activity的
     * onResume方法中调用 MobclickAgent.onResume(context)
     * onPause方法中调用 MobclickAgent.onPause(context)
     *
     * 确保在所有的Activity中都调用 MobclickAgent.onResume() 和onPause()方法
     * 这两个调用不会阻塞应用程序的主线程，也不会影响应用程序的性能。
     *
     * 注意：如果您的Activity之间有继承或者控制关系，请不要同时在父和子Activity中
     * 重复添加onPause和onResume方法，否则会造成重复统计，导致启动次数异常增高。
     * (例如：使用TabHost、TabActivity、ActivityGroup时)。
     */

    @Override
    protected void onResume() {
        super.onResume();
        //友盟 Session启动、App使用时长等基础数据统计接口API
        MobclickAgent.onResume(this);//统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();

        //友盟 Session启动、App使用时长等基础数据统计接口API
        MobclickAgent.onPause(this);//统计时长

//        mediaPlayer.pause();
        mediaPlayer.release();
        parallelViewHelper.stop();
    }

    //首次创建该活动+onPause该活动后再次onRestart该活动，都开始播放音乐
    @Override
    protected void onStart() {

        playBGSound();
        parallelViewHelper.start();

        thread=new Thread(new Runnable(){
            @Override
            public void run() {
                playBGSound();//播放背景音乐
            }
        });
        thread.start();//开启线程
        super.onStart();
    }

    private void playBGSound() {
        if (mediaPlayer!= null) {
            mediaPlayer.release();//释放资源
        }
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.login_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.e("MediaPlayer","Error");
                return false;
            }
        });
        if(!MyApplication.isPlaying)
        {
            mediaPlayer.setVolume(0f,0f);
        }
        else{
            mediaPlayer.setVolume(1,1 );
        }
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            Log.e("mediaplayer:","start");
        }
    }

    @Override
    protected void onRestart() {

        super.onRestart();
    }

    public void MainActivityNewbieGuide(){

        //从透明到不透明，不透明到透明的动画定义
        Animation enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        Animation exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(600);
        exitAnimation.setFillAfter(true);


        NewbieGuide.with(this).setLabel("page").alwaysShow(true)//true:程序员开发调试时使用，每次打开APP显示新手引导;false:只在用户第一次打开APP显示新手引导
                .addGuidePage(
                        GuidePage.newInstance()
//可以一个引导页多个高亮，多加几个.addHighLight()即可
                                .addHighLight(ivEnghten)
                                .addHighLight(ivGame)
                                .addHighLight(ivCoding)
                                .addHighLight(ivCar)
                                .setLayoutRes(R.layout.main_activity_newbie_guide)//引导页布局，点击跳转下一页或者消失引导层的控件id
//                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
//                                    @Override
//                                    //onLayoutInflated很重要，返回了 引导层的view，一定要利用好这个view，比如为引导层的view设定监听事件
//                                    public void onLayoutInflated(View view) {
//                                        TextView textViewNewbieGuide = view.findViewById(R.id.textview_mainactvitiy_newbie_guide);
//                                        if(MyApplication.languageFlag==MyApplication.LANGUAGE_CHINESE){
//                                            textViewNewbieGuide.setText("点击下方的按钮\n选择你喜欢的学习模块吧");
//                                        }else {
//                                            textViewNewbieGuide.setText("Click the button below \nand choose your favorite learning module");
//                                        }
//                                    }
//                                })
                                //这几行代码可以让引导界面慢慢过渡，从有到无
                                .setEnterAnimation(enterAnimation)//进入动画
                                .setExitAnimation(exitAnimation)//退出动画
                ).show();
    }

}
