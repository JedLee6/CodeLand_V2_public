package com.redant.codeland.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Log;

import com.redant.codeland.R;
import com.redant.codeland.entity.DataUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import org.litepal.LitePal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import cn.bmob.v3.Bmob;

/**
 * Created by Administrator on 2017-12-11.
 */

public class MyApplication extends Application {

    public static boolean isPlaying=true;

    public static float voiceMax;
    public static float voiceCurrent;
    public static AssetFileDescriptor afd;

    public static final int LANGUAGE_ENGLISH=0;
    public static final int LANGUAGE_CHINESE=1;


    public static int languageFlag=LANGUAGE_CHINESE;


    private static Context mContext = null;
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();

        context=getApplicationContext();
        //Bmob初始化
        Bmob.initialize(context,"4651e0faa766afd14c770667cf077701");

        /**
         * 参数1:上下文，必须的参数，不能为空。
         * 参数2:【友盟+】 AppKey，非必须参数，如果Manifest文件中已配置AppKey，该参数可以传空，则使用Manifest中配置的AppKey，否则该参数必须传入。
         * 参数3:【友盟+】 Channel，非必须参数，如果Manifest文件中已配置Channel，该参数可以传空，则使用Manifest中配置的Channel，否则该参数必须传入，Channel命名请详见Channel渠道命名规范。
         * 参数4:设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机。
         * 参数5:Push推送业务的secret，需要集成Push功能时必须传入Push的secret，否则传空。
         */
        UMConfigure.init(this, null, null, UMConfigure.DEVICE_TYPE_PHONE, null);

        //场景类型设置接口,必须要有！之前 编程乐园就是没有这个借口导致一直无法登记数据！
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        // 使用集成测试模式请先在程序入口处调用如下代码，打开调试模式，打开统计SDK调试模式
        UMConfigure.setLogEnabled(true);

        //首先mContext获得一下 Context全局的值
        mContext = getApplicationContext();

        //如果是第一次运行，获得系统的语言，并记录在MyApplication.languageFlag中
        if(isFirstRun("Language")){
            if(getLanguage().equals("zh-CN")){
                languageFlag=LANGUAGE_CHINESE;
            }
            else{
                languageFlag=LANGUAGE_ENGLISH;
            }
            Log.e("开始时",""+languageFlag);
        }
        //如果不是第一次运行就读取SharedPreferences中的数值
        else{
            SharedPreferences pref=getSharedPreferences("languageList",MODE_PRIVATE);
            //读取SharedPreferences中的数值，没有时 默认1：中文
            languageFlag=pref.getInt("language",1);
            Log.e("第二次",""+languageFlag);
        }

        //初始化Litepal数据库，每次打开APP都要
        LitePal.initialize(getApplicationContext());

        //运行isFirstRun()后，isFirstRunBoolean都会获取当次打开APP是否为第一次，只需判断isFirstRunBoolean即可
        if (isFirstRun("App")) {
            // 如果是第一次启动程序就加载数据库；不是第一次启动就不加载数据库，提升速度
            //加载素材数据库,第一次启动APP需要，以后不需要
            //为了方便调试，每次重启APP都需要重新加载数据库，APP发布时，加入if (isFirstRun())
            DataUtil.initPoetryDataBase(getApplicationContext());
            DataUtil.initSanzijingDataBase(getApplicationContext());
            DataUtil.initCelebrityDataBase(getApplicationContext());
            DataUtil.initEnglishWordDataBase(getApplicationContext());
            DataUtil.initAllLevel(getApplicationContext());
            DataUtil.initKnowledgeUrl(getApplicationContext());
            DataUtil.initAnimalKindDataBase(getApplicationContext());
            Log.d("boot","第一次启动");
        }

        //适配屏幕时，需要在 MyApplication中声明的代码
        ScreenAdapterTools.init(this);

    }


    protected String getLanguage(){
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else locale = Locale.getDefault();
        String language = locale.getLanguage() + "-" + locale.getCountry();
        return language;
    }

    //Android6.0多语言切换
    public static void changeAppLanguage(Resources resources, int languageFlag){
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (languageFlag==0){
            configuration.locale = Locale.ENGLISH;
        }
        else {
            configuration.locale = Locale.CHINESE;
        }
        resources.updateConfiguration(configuration,displayMetrics);
        Log.e("执行","lll");
        // EventBus.getDefault().post(EVENT_REFRESH_LANGUAGE);
    }

    public static void disableAPIDialog(){
        if (Build.VERSION.SDK_INT < 28)return;
        try {
            Class clazz = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = clazz.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object activityThread = currentActivityThread.invoke(null);
            Field mHiddenApiWarningShown = clazz.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param firstRunType 第一次运行的类型，这样所有要判断是否是第一次运行的，传入这个第一次运行的类型就可以批量化的判断是否是第一次运行了，如
     *                     （1）"App" 代表是不是第一次启动APP
     *                     （2）"EnlightenActivityNewNewbieGuide"代表 第一模块EnlightenActivity的新手引导是否已经运行过一次了
     *                     （3）"CodingBaseActivityNewbieGuide"代表 第一模块CodingBaseActivity的新手引导是否已经运行过一次了
     * @return
     */
    public static boolean isFirstRun(String firstRunType) {
        //实例化SharedPreferences对象（第一步）
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(
                "share", MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun"+firstRunType, true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!isFirstRun) {
            return false;
        } else {
            //保存数据 （第三步）
            editor.putBoolean("isFirstRun"+firstRunType, false);
            //提交当前数据 （第四步）
            editor.commit();
            return true;
        }
    }


    public static MediaPlayer mp;  //音频播放
    public static void playClickVoice(Context context,String... soundType){
        if (true) {
            try {
                if (mp == null) {
                    mp = new MediaPlayer();
                }
                mp.reset();

                //首先一定要注意，如果数组为空会报错，所以先判断数组是否存在
                if(soundType.length!=0){
                    //如果字符串是 success 代表要播放 成功的音效
                    if(soundType[0].equals("success")){
                        afd = context.getResources().openRawResourceFd(R.raw.success_sound);
                    }
                    else if(soundType[0].equals("failure")){
                        afd = context.getResources().openRawResourceFd(R.raw.zero_to_onestar_fail_layout_match_dialog);
                    }
                    else if(soundType[0].equals("setVoice"))
                    {
                        afd = context.getResources().openRawResourceFd(R.raw.button_set_voice_activity_main);
                    }
                    else if(soundType[0].equals("button"))
                    {
                        afd = context.getResources().openRawResourceFd(R.raw.button_sound);
                    }
                }
                else {
                    afd = context.getResources().openRawResourceFd(R.raw.button_sound);
                }

                if (afd == null)  return;
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();

                //mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                if(!isPlaying)
                {
                    mp.setVolume(0f,0f);
                }
                else{
                    mp.setVolume(1,1);
                }
                mp.prepare();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.start();
        }
    }



    public static Context getContext(){
        return mContext;
    }
}
