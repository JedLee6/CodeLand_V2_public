package com.redant.codeland.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.android.ui.TrashCanView;
import com.redant.codeland.util.AppLanguageUtils;
import com.redant.codeland.R;
import com.redant.codeland.app.MyApplication;
import com.redant.codeland.entity.LevelInfo;
import com.redant.codeland.util.JavascriptUtil;
import com.redant.codeland.util.MyJSInterface;
import com.redant.codeland.util.Util;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class CodingLearningActivity extends AbstractBlocklyActivity implements View.OnClickListener{private WebView mWebview;
    private Button btnRun;
    private Button btnShow;
    private Button btnContinue;
    private Button btnExit;
    private Button btnClear;
    private Button btnHelp;
    private Button buttonTaskandTip;
    //捕捉的用户的Blockly代码块中alert输出的内容
    private String alertMessage;
    //get running code
    private String runningCode;
    //获取用户点击的章节
    private int clickedChapter;
    //获取用户点击的关卡值
    private int clickedLevel;
    //get max level
    private int maxLevel;
    //用户点击的章节 的字符串名字
    private String chapterString;
    //星星个数
    private int rating;
    //    private String newName=null;
//    private SavingRecord savingRecord;
//    private String willLoadSpace;

    private DrawerLayout drawerLayout;


    //一个轻量的纸屑粒子效果
    private KonfettiView konfettiView;
    //新手引导帮助按钮
    private Button buttonHelp;
    //垃圾桶
    private TrashCanView trashCan;
    //toolbox的用于假装高亮的LinearLayout
    LinearLayout linearLayoutFakeToolbox;

    private String LOG_TAG="WebViewTest";
    private TextView mReusultText;

    private static final String TAG = "NCBlocklyActivity";
    //要存储用户点击的 章节Chapter 和 关卡Level，用到二维数组
//    private String [][] toolboxPaths = {
//            {"coding/chapter_toolbox/toolbox1_1.xml","coding/chapter_toolbox/toolbox1_2.xml"},
//            {"coding/chapter_toolbox/toolbox2_1.xml","coding/chapter_toolbox/toolbox2_2.xml"},
//            {"coding/chapter_toolbox/toolbox3_1.xml"}
//    };


    /**
     * Handler在Dialog点击确定按钮时触发，重新加载英语单词
     */
    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //一定要处理好 完成 再玩一次 下一关 三个消息，否则陷入死循环
            //点击“完成”退出该活动
            if(msg.what == 1){
                //隐藏掉“礼花”效果
                konfettiView.setVisibility(View.GONE);
                finish();

            }
            //点击“再玩一次”
            else if(msg.what==2){
                //第三模块的这里，不需要任何操作
                //隐藏掉“礼花”效果
                konfettiView.setVisibility(View.GONE);
            }
            //只有星星个数为3个，才会出现 下一关 选项
            else if(msg.what==3)
            {
                //隐藏掉“礼花”效果
                konfettiView.setVisibility(View.GONE);

                if(clickedLevel<maxLevel){
                    //每次点击运行按钮后，清空文本
                    mReusultText.setText("");

                    SharedPreferences.Editor editor=getSharedPreferences("AllLevel",MODE_PRIVATE).edit();
                    clickedLevel++;
                    editor.putInt("clickedLevel",clickedLevel);
                    editor.commit();
                    //清空工作区
                    getController().resetWorkspace();
                    reloadToolbox();
                }else{
                    finish();
                }

            }
            else {
                //根据不同的 章节Chapter+关卡Level，设计不同的用户判定算法
                //第一层if,else为 章节Chapter判断
                alertMessage=mReusultText.getText().toString();
                check(alertMessage);

                //根据以上算法判断后，确定的星星数值，3个星星代表算法判断成功，否则 弹出对话框，算法判定失败
                if(rating==3){
                    //成功通关的，那么播放 礼花效果的动画
                    konfettiView.setVisibility(View.VISIBLE);
                    konfettiView.performClick();

                    Util.showDialog2(CodingLearningActivity.this, getString(R.string.success), 3,clickedLevel<maxLevel, this);

                }
                else {
                    Util.showDialog2(CodingLearningActivity.this, getString(R.string.fail), 0,clickedLevel<maxLevel, this);
                }

                SharedPreferences sharedPreferences=getSharedPreferences("AllLevel",MODE_PRIVATE);
                int chapterUnlockLevel=sharedPreferences.getInt(chapterString+"UnlockLevel",0);

                //只有  全部拼写正确 并且 点击的关卡数>=已经解锁的最大关卡数， 数据库中的“最大关卡”才会+1
                if(clickedLevel>=chapterUnlockLevel&&rating==3){
                    SharedPreferences.Editor editor=getSharedPreferences("AllLevel",MODE_PRIVATE).edit();
                    editor.putInt(chapterString+"UnlockLevel",clickedLevel+1);
                    editor.commit();
                }

                //更新数据库中的星星值
                List<LevelInfo> rates= DataSupport.where("name = ?",chapterString+" "+clickedLevel).find(LevelInfo.class);
                if(rates.isEmpty()){
                    LevelInfo rate=new LevelInfo();
                    rate.setName(chapterString+" "+clickedLevel);
                    rate.setModel(chapterString);
                    rate.setRating(rating);
                    rate.save();
                }
                else{
                    int oldRating=rates.get(0).getRating();
                    if(rating>oldRating){
                        LevelInfo rate=new LevelInfo();
                        rate.setRating(rating);
                        rate.updateAll("name = ?",chapterString+" "+clickedLevel);
                    }
                }
                
            }

            //已经弹出对话框了，那么设置为按钮可再次点击
            btnRun.setClickable(true);

            //清空rating为0
            rating=0;
        }
    };
    //每个存在语言切换的Activity需要写
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase));
    }

    public void check(String m){
        //中英双语版本中的字符串不一样要分开判断
        if(clickedChapter == 1 && MyApplication.languageFlag==MyApplication.LANGUAGE_CHINESE){
            switch(clickedLevel){
                case 1:
                    if(alertMessage.contains("你好") && alertMessage.contains("世界") ){
                        rating=3;
                    }
                    break;
                case 2:
                    if(alertMessage.contains("你好吗") && alertMessage.contains("是的") ){
                        rating=3;
                    }
                    break;
                case 3:
                    if( alertMessage.contains("大家好") && alertMessage.contains("我是编程乐园") && alertMessage.contains("小小程序员") ){
                        rating=3;
                    }
                    break;
            }
        }
        else if(clickedChapter == 1 && MyApplication.languageFlag==MyApplication.LANGUAGE_ENGLISH){
            switch(clickedLevel){
                case 1:
                    //判断大小写的要求应该是不区分大小写的，但是不区分大小写代码太麻烦了，就直接判断小写部分即可
                    if(alertMessage.contains("ello") && alertMessage.contains("orld") ){
                        rating=3;
                    }
                    break;
                case 2:
                    if(alertMessage.contains("ow") && alertMessage.contains("are") && alertMessage.contains("you") && alertMessage.contains("ine") ){
                        rating=3;
                    }
                    break;
                case 3:
                    if( alertMessage.contains("everyone") && alertMessage.contains("I") && alertMessage.contains("ode") && alertMessage.contains("and") && alertMessage.contains("ittle") && alertMessage.contains("programmer") ){
                        rating=3;
                    }
                    break;
            }
        }
        else if(clickedChapter == 2){
            switch(clickedLevel){
                case 1:
                    if(m.contains("666") && !m.contains("6666")){
                        rating=3;
                    }
                    break;
                case 2:
                    if(m.contains("6912") && runningCode.contains("1234") && runningCode.contains("5678")){
                        rating=3;
                    }
                    break;
                case 3:
                    if(m.contains("4") && runningCode.contains("Math.sqrt(16)") ){
                        rating=3;
                    }
                    break;
                case 4:
                    if(m.contains("0.50") && runningCode.contains("Math.cos(60 / 180 * Math.PI)")){
                        rating=3;
                    }
                    break;
                case 5:
                    if(m.contains("3") && runningCode.contains("31 % 4")){
                        rating=3;
                    }
                    break;
            }
        }
        else if(clickedChapter == 3){
            switch(clickedLevel){
                case 1:
                    if(m.contains("666")){
                        rating=3;
                    }
                    break;
                case 2:
                    if(m.contains("1841")){
                        rating=3;
                    }
                    break;
                case 3:
                    if(m.contains("56088")){
                        rating=3;
                    }
                    break;
                case 4:
                    if(m.contains("988") && m.contains("70980")){
                        rating=3;
                    }
                    break;
                case 5:
                    if(m.contains("22848")){
                        rating=3;
                    }
                    break;
            }
        }
        else if(clickedChapter == 4){
            switch(clickedLevel){
                case 1:
                    if((m.contains("uccess")||m.contains("成功"))){
                        rating=3;
                    }
                    break;
                case 2:
                    if(m.contains("uccess")){
                        rating=3;
                    }
                    break;
                case 3:
                    if((m.contains("ail")) && runningCode.contains("if (3 <= 2)") ){
                        rating=3;
                    }
                    break;
                case 4:
                    if(m.contains("uccess") && runningCode.contains("if (2 < 4 && 3 > 1)")){
                        rating=3;
                    }
                    break;
                case 5:
                    if(m.contains("uccess") && runningCode.contains("if (3 <= 2 || 5 > 4)")){
                        rating=3;
                    }
                    break;
            }
        }else if(clickedChapter==5){
            switch(clickedLevel){
                case 1:
                    if(m.contains("hello") && runningCode.contains("for (var count = 0; count < 2; count++)")){
                        rating=3;
                    }
                    break;
                case 2:
                    if(m.contains("2") && m.contains("3") && m.contains("4") && m.contains("5") && !m.contains("6") && !m.contains("1")){
                        rating=3;
                    }
                    break;
                case 3:
                    if(m.contains("2") && m.contains("3") && m.contains("5") && m.contains("7") && runningCode.contains("mathIsPrime")){
                        rating=3;
                    }
                    break;
                case 4:
                    if(m.contains("1") && m.contains("13") && !m.contains("14")){
                        rating=3;
                    }
                    break;
                case 5:
                    if(m.contains("2") && m.contains("4") && m.contains("6") && runningCode.contains("3") && runningCode.contains("% 2 == 0")){
                        rating=3;
                    }
                    break;
            }
        }
        else if(clickedChapter == 6){
            switch(clickedLevel){
                case 1:
                    if(m.contains("20,20,20,20,20")){
                        rating=3;
                    }
                    break;
                case 2:
                    if(m.contains("12,34,56")){
                        rating=3;
                    }
                    break;
                case 3:
                    if(m.contains("12,34,56") ){
                        rating=3;
                    }
                    break;
                case 4:
                    if(m.contains("1")){
                        rating=3;
                    }
                    break;
                case 5:
                    if(m.contains("5,6,7")){
                        rating=3;
                    }
                    break;
            }
        }
    }

    private CodeGenerationRequest.CodeGeneratorCallback mCallback = new CodeGenerationRequest.CodeGeneratorCallback() {
        @Override
        public void onFinishCodeGeneration(final String generatedCode) {

            runningCode=generatedCode;
            Log.i(TAG, "onFinishCodeGeneration: "+generatedCode);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(CodingLearningActivity.this,"run里面的："+alertMessage,Toast.LENGTH_SHORT).show();
                    String encoded = "tp.execute("
                            + JavascriptUtil.makeJsString(generatedCode) + ")";
                    mWebview.loadUrl("javascript:"+encoded);
                }
            });

//            String encoded = "tp.execute("+ JavascriptUtil.makeJsString(generatedCode) + ")";
//            mWebview.loadUrl("javascript:"+encoded);
        }
    };

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath()
    {
        //因为所有的toolbox文件都有 x_y的命名方式，因此只要修改x,y值即可
        if(MyApplication.languageFlag==1){
        return "coding/chapter_toolbox/toolbox"+clickedChapter+"_"+clickedLevel+".xml";}
        else{
            return "coding/english_chapter_toolbox/toolbox"+clickedChapter+"_"+clickedLevel+".xml";
        }
    }

    @NonNull
    @Override
    protected List<String> getBlockDefinitionsJsonPaths() {
        List<String> jsonPaths = new ArrayList<>();
        if(MyApplication.languageFlag==1){
            jsonPaths.add("coding/json_chinese/colour_blocks.json");
            jsonPaths.add("coding/json_chinese/list_blocks.json");
            jsonPaths.add("coding/json_chinese/logic_blocks.json");
            jsonPaths.add("coding/json_chinese/loop_blocks.json");
            jsonPaths.add("coding/json_chinese/math_blocks.json");
            jsonPaths.add("coding/json_chinese/procedures.json");
            //jsonPaths.add("coding/test_blocks打乱.json");
            jsonPaths.add("coding/json_chinese/text_blocks.json");
            jsonPaths.add("coding/json_chinese/variable_blocks.json");
        }
        else {
            jsonPaths.add("coding/json_english/colour_blocks.json");
            jsonPaths.add("coding/json_english/list_blocks.json");
            jsonPaths.add("coding/json_english/logic_blocks.json");
            jsonPaths.add("coding/json_english/loop_blocks.json");
            jsonPaths.add("coding/json_english/math_blocks.json");
            jsonPaths.add("coding/json_english/procedures.json");
            //jsonPaths.add("coding/json_english/test_blocks打乱.json");
            jsonPaths.add("coding/json_english/text_blocks.json");
            jsonPaths.add("coding/json_english/variable_blocks.json");
        }
        return jsonPaths;
    }

    @NonNull
    @Override
    protected List<String> getGeneratorsJsPaths() {
        List<String> jsPaths= new ArrayList<>();
        jsPaths.add("coding/generator.js");
        return jsPaths;
    }

    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        return mCallback;
    }

    @Override
    protected View onCreateContentView(int containerId) {

        //关卡的读取一定要写在onCreateContentView，否则关卡加载不正确

        //获取用户点击的章节
        SharedPreferences sharedPreferences=getSharedPreferences("Module3",MODE_PRIVATE);
        clickedChapter=sharedPreferences.getInt("clickedChapter",1);
        initChapterString(clickedChapter);
        //获取用户点击的关卡值
        sharedPreferences=getSharedPreferences("AllLevel",MODE_PRIVATE);
        clickedLevel=sharedPreferences.getInt("clickedLevel",1);
        maxLevel=sharedPreferences.getInt(chapterString+"MaxLevel",0);

        //提示去掉，否则影响用户体验
        //Toast.makeText(CodingLearningActivity.this,"clickChapter:"+clickedChapter+" clickedLevel:"+clickedLevel+" Max "+maxLevel,Toast.LENGTH_SHORT).show();


        View root = getLayoutInflater().inflate(R.layout.activity_coding_learning, null);
        mWebview = root.findViewById(R.id.coding_webview);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.setWebChromeClient(new WebChromeClient(){//允许有alert弹出框
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //webTitle = title;
            }


            /**
             * 处理alert弹出框
             */
            @Override
            public boolean onJsAlert(WebView view,String url,
                                     String message,JsResult result) {
                Log.d(LOG_TAG,"onJsAlert:"+message);
                //为多个“输出”块的“输出文本”追加到textview中
                  mReusultText.append(message+"\n");
                alertMessage=mReusultText.getText().toString();
               // Toast.makeText(CodingLearningActivity.this,"当场的 "+alertMessage,Toast.LENGTH_SHORT).show();
//                //对alert的简单封装,Android端弹出的内容
//                new AlertDialog.Builder(CodingBlocklyActivity .this).
//                    setTitle("Alert").setMessage(message).setPositiveButton("OK",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface arg0, int arg1) {
//                           //TODO
//                       }
//                }).create().show();

                //传入showDialog的可变长参数，表明这是第三模块“编程开发”，让待会弹出的对话框是适应的
//                int[] dialogModule={4};
//                Util.showDialog(CodingBlocklyActivity.this,message,3,handler,dialogModule);
                result.confirm();
                return true;
                /*
                * result.confirm();
                * return true;
                * 放在一起一块用，才会屏蔽掉网页原来的alert弹窗
                * result.confirm();
                * return super.onJsPrompt(view, url, message, message, result);
                * 放在一起用，是让网页原来的弹窗显示
                 * */
            }

            /**
             * 处理confirm弹出框，我们的编程乐园项目中2018-06-15目前用不到confirm弹出框
             */
            @Override
            public boolean onJsConfirm(WebView view, String url, String message,
                                       JsResult result) {
                Log.d(LOG_TAG, "onJsConfirm:"+message);
                mReusultText.setText("Confirm:"+message); //捕获弹框消息给原生赋值，相当于传值了
                //对confirm的简单封装
                new AlertDialog.Builder(CodingLearningActivity.this).
                        setTitle("Confirm").setMessage(message).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //TODO
                            }
                        }).create().show();

                result.confirm();
                return true;
                //如果采用下面的代码会另外再弹出个消息框，目前不知道原理
                //return super.onJsConfirm(view, url, message, result);
            }

            /**
             * 处理prompt弹出框，我们的编程乐园项目中2018-06-15目前用得到confirm弹出框，但是比较困难弄清楚原理，后期完善
             */
            @Override
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, JsPromptResult result) {
                Log.d(LOG_TAG,"onJsPrompt:"+message);
                mReusultText.setText("Prompt input is :"+message);
                result.confirm();
                return super.onJsPrompt(view, url, message, message, result);
            }

        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mWebview.addJavascriptInterface(new MyJSInterface(this,handler), "android");
        mWebview.loadUrl("file:///android_asset/coding/html/level0.html");


        btnRun = root.findViewById(R.id.coding_button_run);
        btnContinue=root.findViewById(R.id.blockly_fbtn_continue);
        btnExit=root.findViewById(R.id.blockly_fbtn_exit);

        btnClear=root.findViewById(R.id.blockly_fbtn_clear);
        btnHelp=root.findViewById(R.id.blockly_fbtn_help);
        btnShow=root.findViewById(R.id.blockly_fbtn_show);
        drawerLayout=root.findViewById(R.id.drawer_layout);
        buttonTaskandTip=root.findViewById(R.id.coding_button_task_tip);
        buttonHelp=root.findViewById(R.id.coding_learning_help);
        trashCan=root.findViewById(R.id.blockly_trash_icon);
        linearLayoutFakeToolbox=root.findViewById(R.id.coding_learning_fake_toolbox);

        konfettiView=root.findViewById(R.id.viewKonfetti);
        konfettiView.setOnClickListener(this);

        buttonHelp.setOnClickListener(this);
        btnRun.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        buttonTaskandTip.setOnClickListener(this);

        mReusultText= root.findViewById(R.id.mReusultText);
        //让文本支持滑动
        mReusultText.setMovementMethod(ScrollingMovementMethod.getInstance());
        mReusultText.setOnClickListener(this);


        return root;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.coding_button_run:
                //运行代码
                if (getController().getWorkspace().hasBlocks()) {
                    onRunCode();
                    //防止用户一次性点击多下,下次开启点击是在 对话框弹出时
                    btnRun.setClickable(false);
                    //每次点击运行按钮后，清空文本
                    mReusultText.setText("");
                   // Toast.makeText(CodingLearningActivity.this,getSavingDate(),Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "工作区中没有块");
                    Toast.makeText(CodingLearningActivity.this, R.string.no_block_in_workspace,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.blockly_fbtn_show:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.blockly_fbtn_continue:
                drawerLayout.closeDrawers();
                break;
            case R.id.blockly_fbtn_exit:
                finish();
                break;
            case R.id.blockly_fbtn_clear:
                onClearWorkspace();
                drawerLayout.closeDrawers();
                break;
            case R.id.blockly_fbtn_help:
                codingLearningActivityNewbieGuide();
                drawerLayout.closeDrawers();
                break;
            case R.id.mReusultText:
                //基于用户体验原则，不再设置点击后切换1或者3行，默认为3行
//                if(mReusultText.getMaxLines()==3){
//                    mReusultText.setLines(1);
//                }
//                else {
//                    mReusultText.setLines(3);
//                }
                break;
            case R.id.coding_button_task_tip:
                Util.showModule3TipDialog(CodingLearningActivity.this,clickedChapter,clickedLevel,handler);
                break;
            case R.id.coding_learning_help:
                codingLearningActivityNewbieGuide();
                break;
            case R.id.viewKonfetti:
                konfettiView.build()
                        .addColors(Color.YELLOW,Color.RED,Color.BLUE, Color.GREEN,Color.CYAN,Color.MAGENTA)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(2000L)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(new Size(12, 5))
                        .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                        .streamFor(300, 5000L);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //适配屏幕引入语句
        com.yatoooon.screenadaptation.ScreenAdapterTools.getInstance().loadView((android.view.ViewGroup) getWindow().getDecorView());

        //隐藏Actionbar
        getSupportActionBar().hide();
        getSupportActionBar();
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //清空工作区
        getController().resetWorkspace();

        //本关卡刚开始时，自动打开对话框以展示题目，让用户知道当前关卡需要做些什么任务
        Util.showModule3TipDialog(CodingLearningActivity.this,clickedChapter,clickedLevel,handler);

        //是否第一次打开 编程乐园，是的话显示 新手引导
        if(MyApplication.isFirstRun("CodingLearningActivityNewbieGuide"))
        {
            codingLearningActivityNewbieGuide();
        }
    }

    public String  getSavingDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        return (simpleDateFormat.format(date));
    }

    //新手引导
    public void codingLearningActivityNewbieGuide(){

        NewbieGuide.with(this).setLabel("page").alwaysShow(true)//true:程序员开发调试时使用，每次打开APP显示新手引导;false:只在用户第一次打开APP显示新手引导
                .addGuidePage(
                        GuidePage.newInstance()
//可以一个引导页多个高亮，多加几个.addHighLight()即可
                                .addHighLight(mReusultText)
                                .addHighLight(buttonTaskandTip)
                                .addHighLight(btnRun)
                                .addHighLight(trashCan)
                                .addHighLight(linearLayoutFakeToolbox)
                                .setLayoutRes(R.layout.coding_learning_activity_newbie_guide)//引导页布局，点击跳转下一页或者消失引导层的控件id
                                .setEverywhereCancelable(true)//是否点击任意地方跳转下一页或者消失引导层，默认true
                ).show();
    }

    public void initChapterString(int clickedChapter){
        if(clickedChapter==1){
            //定义 chapterString为printf，以方便接下来向数据库中存储信息
            chapterString="printf";
        }
        else if(clickedChapter==2){
            //定义 chapterString为math，以方便接下来向数据库中存储信息
            chapterString="math";
        }
        else if(clickedChapter==3){
            //定义 chapterString为variable，以方便接下来向数据库中存储信息
            chapterString="variable";
        }
        else if(clickedChapter==4){
            //定义 chapterString为logic，以方便接下来向数据库中存储信息
            chapterString="logic";
        }
        else if(clickedChapter==5){
            //定义 chapterString为loop，以方便接下来向数据库中存储信息
            chapterString="loop";
        }
        else if(clickedChapter==6){
            //定义 chapterString为array，以方便接下来向数据库中存储信息
            chapterString="array";
        }
    }

}
