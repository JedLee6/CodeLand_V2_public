package com.redant.codeland.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import androidx.core.view.GravityCompat;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.android.control.BlocklyController;
import com.google.blockly.android.ui.TrashCanView;
import com.google.blockly.model.DefaultBlocks;
import com.google.blockly.utils.BlockLoadingException;
import com.redant.codeland.util.AppLanguageUtils;
import com.redant.codeland.MyContextWrapper;
import com.redant.codeland.R;
import com.redant.codeland.app.MyApplication;
import com.redant.codeland.entity.LevelInfo;
import com.redant.codeland.util.JavascriptUtil;
import com.redant.codeland.util.Temp;
import com.redant.codeland.util.Util;


import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
//小车模块
public class TurtleActivity extends AbstractBlocklyActivity implements View.OnClickListener{

    public byte[] message = new byte[1];
    private Vibrator vibrator;

    private int ENABLE_BLUETOOTH=2;
    private BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket = null;
    OutputStream outputStream = null;
    private static final UUID MY_UUID_SECURE=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String blueAddress="AB:8C:2E:57:34:02";//蓝牙模块的MAC地址
    private LinearLayout linearLayout1;
    private Button button_back;

    private Button btnShow;
    private Button btnContinue;
    private Button btnAgain;
    private Button btnExit;
    private Button btnMusic;
    private Button btnHelp;
    private DrawerLayout drawerLayout;

    //展示正确运行效果的按钮
    private Button ptnRun;
    //运行代码的按钮
    private Button btnRun;
    //新手引导帮助按钮
    private Button buttonHelp;
    //垃圾桶
    private TrashCanView trashCan;
    //toolbox的用于假装高亮的LinearLayout
    LinearLayout linearLayoutFakeToolbox;

    private int maxLevels;
    private static final String TAG = "TurtleActivity";

    //private static final String SAVE_FILENAME = "turtle_workspace.xml";
   // private static final String AUTOSAVE_FILENAME = "turtle_workspace_temp.xml";

    //当前所在的关卡，默认为0，即第一关
    private int clickedLevel = 0;
    private int rating=0;
    private int flag;
    private String finalResult;
    private WebViewClient webViewClient;
    private String mCode;


    static final List<String> TURTLE_BLOCK_GENERATORS = Arrays.asList(
            "turtle/generators.js"
    );
    private static final int MAX_LEVELS = 10;
    //测试一个关卡，后面用数组对每一关的代码进行记录
    private static final ArrayList<Temp> CodePreviewList = new ArrayList<>();
    //根据不同的关卡，加载不同的工具栏

    //延迟一定时间再执行代码检查工作
    public  Handler handlerCheckCode=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            onCheckCode();
            //点击后防止用户多次点击设为不可点击，现在对话框已经弹出，所以要恢复运行按钮为可点击
            btnRun=findViewById(R.id.button_turtle_run);
            btnRun.setClickable(true);
        }
    };

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                flag=0;
                finish();
            }
            else if(msg.what==2){
                mTurtleWebview.loadUrl("javascript:"+"tl.execute()" );
                flag=0;
            }
            else if(msg.what==3)
            {
                flag=0;
                if(rating!=0){
                    SharedPreferences.Editor editor=getSharedPreferences("AllLevel",MODE_PRIVATE).edit();
                    clickedLevel++;
                    editor.putInt("clickedLevel",clickedLevel);
                    editor.commit();
                    mTurtleWebview.loadUrl("javascript:"+"tl.execute()" );
                    reloadToolbox();
                }
            }

        }

    };
    private WebView mTurtleWebview;
    private final CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback =
            new CodeGenerationRequest.CodeGeneratorCallback() {
                @Override
                public void onFinishCodeGeneration(final String generatedCode) {
                    // Sample callback.

                    handler.post(new Runnable() {
                        @Override
                        public void run(){
                            String encoded = "tl.execute("
                                    + JavascriptUtil.makeJsString(generatedCode) + ")";
                            Log.i(TAG, "js:generatedCode:\n" + JavascriptUtil.makeJsString(generatedCode));
                            Log.i(TAG, "encoded:\n" + encoded);

                            mTurtleWebview.loadUrl("javascript:" + encoded);
                        }//改写成函数形式加载到turtle.js文件中执行对应动作
                    });
                    //检查是否和对应关卡图形代码相同
                    if(CodePreviewList.get(clickedLevel).equal(generatedCode)){
//                        finalResult = getString(R.string.congratulation);
                        rating=3;
                        flag=1;
                        saveRating();
//                        Util.showDialog(TurtleActivity.this,finalResult,3,handler);

                    }else
                    {
                        Log.e("error:",generatedCode);
                        //finalResult=getString(R.string.fail_painting);
                        rating=0;
                        flag=1;
//                        Util.showDialog(TurtleActivity.this,finalResult,0,handler);
                    }
                    Log.i(TAG, "generatedCode:\n" + generatedCode);
                    String[] type={"moveForward(100)","moveBackward(100)","turnRight(90)","turnLeft(90)","penDown()","penUp()"};
                    List<String> stringList=returnBlockType(generatedCode,type);
                    Log.e("generatedCodeXXX",stringList.toString());
                    for(String temp:stringList)
                    {
                        //前进
                        if(temp.equals(type[0])) {                            message[0]= (byte) 0x41;//设置要发送的数值
                            bluesend(message);//发送数值
                            Log.e("generatedCodeXXX","1");}
                        //后退
                        else if(temp.equals(type[1])){
                            message[0]= (byte) 0x47  ;//设置要发送的数值
                            bluesend(message);//发送数值
                            Log.e("generatedCodeXXX","2");}
                        //右转90度
                        else if(temp.equals(type[2])){
                            message[0]= (byte) 0x46;//设置要发送的数值
                            bluesend(message);//发送数值
                            Log.e("generatedCodeXXX","3");}
                        //左转90度
                        else if(temp.equals(type[3])){
                            message[0]= (byte) 0x42;//设置要发送的数值
                            bluesend(message);//发送数值
                            Log.e("generatedCodeXXX","4");}
                        //自动寻线
                        else if(temp.equals(type[4])){
                            message[0]= (byte) 0x45;//设置要发送的数值
                            bluesend(message);//发送数值
                            Log.e("generatedCodeXXX","5");}
                        //无人驾驶
                        //自动避障
                        else if(temp.equals(type[5])){
                            message[0]= (byte) 0x48;//设置要发送的数值
                            bluesend(message);//发送数值
                            Log.e("generatedCodeXXX","5");
                            Toast.makeText(TurtleActivity.this,"避障",Toast.LENGTH_LONG).show();}

//                        try {
//                            Thread.currentThread().sleep(1000);//阻断1秒
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        message[0]= (byte) 0x40;//设置要发送的数值
//                        bluesend(message);//发送数值
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable()
//                        { @Override
//                        public void run() {
//                            message[0]= (byte) 0x40;//设置要发送的数值
//                            bluesend(message);//发送数值
//                        }
//                        }, 1000);
                    }

                    Log.i(TAG, "CodePreview:\n" + CodePreviewList.get(clickedLevel));
                    Log.i(TAG, "Count:\n" + clickedLevel);
                    Log.i(TAG, "size:\n" + CodePreviewList.size());
                }
            };


    public void onCheckCode(){
        if(rating==3 && flag==1){
            finalResult = getString(R.string.congratulation);
            Util.showDialog2(TurtleActivity.this,finalResult,3,clickedLevel<maxLevels-1,handler);
        }else if(flag==1){
            finalResult = getString(R.string.fail_painting);
            Util.showDialog2(TurtleActivity.this,finalResult,2,clickedLevel<maxLevels-1,handler);
            //Util.showDialog2(TurtleActivity.this,finalResult,3,clickedLevel<maxLevels-1,handler);
        }
        else{
            //Toast.makeText(TurtleActivity.this,R.string.no_block_in_workspace,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return onDemoItemSelected(item, this) || super.onOptionsItemSelected(item);
    }

    static boolean onDemoItemSelected(MenuItem item, AbstractBlocklyActivity activity) {//界面右上角工具栏选项中的三个demo，根据点击的选型，将js代码块进行加载
        BlocklyController controller = activity.getController();
        int id = item.getItemId();
        boolean loadWorkspace = false;
        String filename = "";
        if (id == R.id.action_demo_android) {//判断用户选择/
            loadWorkspace = true;
            filename = "android.xml";
        } else if (id == R.id.action_demo_lacey_curves) {
            loadWorkspace = true;
            filename = "lacey_curves.xml";
        } else if (id == R.id.action_demo_paint_strokes) {
            loadWorkspace = true;
            filename = "paint_strokes.xml";
        }

        if (loadWorkspace) {
            String assetFilename = "turtle/demo_workspaces/" + filename;//
            try {
                controller.loadWorkspaceContents(activity.getAssets().open(assetFilename));//加载到工作空间中
            } catch (IOException | BlockLoadingException e) {
                throw new IllegalStateException(
                        "Couldn't load demo workspace from assets: " + assetFilename, e);
            }
            addDefaultVariables(controller);
            return true;
        }

        return false;
    }

    @NonNull
    @Override
    protected List<String> getBlockDefinitionsJsonPaths() {//返回加载所有的json块定义
        // Use the same blocks for all the levels. This lets the user's block code carry over from
        // level to level. The set of blocks shown in the toolbox for each level is defined by the
        // toolbox path below.
        String JsonPath;
        if(MyApplication.languageFlag==1){
            JsonPath="turtle/turtle_blocks.json";
        }
        else {
            JsonPath="turtle/english_turtle_blocks.json";
        }
        final List<String> TURTLE_BLOCK_DEFINITIONS = Arrays.asList(
                DefaultBlocks.COLOR_BLOCKS_PATH,
                DefaultBlocks.LOGIC_BLOCKS_PATH,
                DefaultBlocks.LOOP_BLOCKS_PATH,
                DefaultBlocks.MATH_BLOCKS_PATH,
                DefaultBlocks.TEXT_BLOCKS_PATH,
                DefaultBlocks.VARIABLE_BLOCKS_PATH,
                JsonPath
        );
        return TURTLE_BLOCK_DEFINITIONS;
    }

    @Override
    protected int getActionBarMenuResId() { //返回界面右上方的动作选择栏
        return R.menu.turtle_actionbar;
    }

    @NonNull
    @Override
    protected List<String> getGeneratorsJsPaths() {//返回所有新的json块定义的聚集函数
        return TURTLE_BLOCK_GENERATORS;
    }

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {//得到对应等级的toobox路径
        // Expose a different set of blocks to the user at each level.
        //String[] ;
        List<String> toolboxPaths = new ArrayList<>();
        if(MyApplication.languageFlag==1) {
            toolboxPaths.add("turtle/level1_toolbox.xml");
            toolboxPaths.add("turtle/level2_toolbox.xml");
            toolboxPaths.add("turtle/level3_toolbox.xml");
            toolboxPaths.add("turtle/level4_toolbox.xml");
            toolboxPaths.add("turtle/level5_toolbox.xml");
            toolboxPaths.add("turtle/level6_toolbox.xml");
            toolboxPaths.add("turtle/level7_toolbox.xml");
            toolboxPaths.add("turtle/level8_toolbox.xml");
            toolboxPaths.add("turtle/level9_toolbox.xml");
            toolboxPaths.add("turtle/level10_toolbox.xml");
            toolboxPaths.add("turtle/level11_toolbox.xml");
            toolboxPaths.add("turtle/level12_toolbox.xml");
            toolboxPaths.add("turtle/level13_toolbox.xml");
            toolboxPaths.add("turtle/level14_toolbox.xml");
            toolboxPaths.add("turtle/level15_toolbox.xml");
        }
        else {
            toolboxPaths.add("turtle/english_toolbox/english_level1_toolbox.xml");
            toolboxPaths.add("turtle/english_toolbox/english_level2_toolbox.xml");
            toolboxPaths.add("turtle/english_toolbox/english_level3_toolbox.xml");
            toolboxPaths.add("turtle/english_toolbox/english_level4_toolbox.xml");
            toolboxPaths.add("turtle/english_toolbox/english_level5_toolbox.xml");
            toolboxPaths.add("turtle/english_toolbox/english_level6_toolbox.xml");
            toolboxPaths.add("turtle/english_toolbox/english_level7_toolbox.xml");
            toolboxPaths.add("turtle/english_toolbox/english_level8_toolbox.xml");
            toolboxPaths.add("turtle/english_toolbox/english_level9_toolbox.xml");
            toolboxPaths.add("turtle/english_toolbox/english_level10_toolbox.xml");
            toolboxPaths.add("turtle/english_toolbox/english_level11_toolbox.xml");
            toolboxPaths.add("turtle/english_toolbox/english_level12_toolbox.xml");
            toolboxPaths.add("turtle/english_toolbox/english_level13_toolbox.xml");
            toolboxPaths.add("turtle/english_toolbox/english_level14_toolbox.xml");
            toolboxPaths.add("turtle/english_toolbox/english_level15_toolbox.xml");
        }
        return toolboxPaths.get(clickedLevel);
    }

    @Override
    protected void onInitBlankWorkspace() {
        addDefaultVariables(getController());
    }

    @Override
    protected View onCreateContentView(int parentId) {

        initArrayList();
//        clickedLevel = getIntent().getIntExtra("index",0);
        SharedPreferences sharedPreferences=getSharedPreferences("AllLevel",MODE_PRIVATE);
        clickedLevel=sharedPreferences.getInt("clickedLevel",1);
        clickedLevel--;     //from 0


        View root = getLayoutInflater().inflate(R.layout.turtle_content, null);
        mTurtleWebview = (WebView) root.findViewById(R.id.turtle_runtime);//找到webview控件
        mTurtleWebview.getSettings().setJavaScriptEnabled(true);//设置为可见状态
        mTurtleWebview.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mTurtleWebview.loadUrl("file:///android_asset/turtle/turtle.html");
        btnRun = root.findViewById(R.id.button_turtle_run);
        Button btnCheck =root.findViewById(R.id.button_turtle_check);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getController().getWorkspace().hasBlocks()) {
                    onCheckCode();
                } else {
                    //Toast.makeText(TurtleActivity.this,R.string.no_block_in_workspace,Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "工作区中没有块");
                }
            }
        });
        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //运行代码
                if (getController().getWorkspace().hasBlocks()) {
                    //在html上动画运行代码
                    onRunCode();
                    //在android上弹出提示框，告诉用户是否成功
                    //由于不清楚javascript中如何正确调用android中的handler方法，只能做一个假的延时效果
                    handlerCheckCode.sendEmptyMessageDelayed(0,2000);
                    //点击一次后，由于延时，用户可能多次点击：这样会有多个结果反馈对话框，为了防止出现多个对话框，设置为不可点击，在对话框弹出后恢复为可点击
                    btnRun.setClickable(false);
                } else {
                    //Toast.makeText(TurtleActivity.this,R.string.no_block_in_workspace,Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "工作区中没有块");
                }
            }
        });
        ptnRun = root.findViewById(R.id.button_turtle_answer);     //延时
        ptnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //运行代码
                String encoded = "tl.execute("
                        + JavascriptUtil.makeJsString(CodePreviewList.get(clickedLevel).getsth()) + ")";//运行对应关卡应该显示的图形
                Log.e("code:",CodePreviewList.get(clickedLevel).getsth());
                mTurtleWebview.loadUrl("javascript:" + encoded);
            }
        });
        btnContinue=root.findViewById(R.id.blockly_fbtn_continue);
        btnAgain=root.findViewById(R.id.blockly_fbtn_again);
        btnExit=root.findViewById(R.id.blockly_fbtn_exit);
        btnHelp=root.findViewById(R.id.blockly_fbtn_help);
        btnMusic=root.findViewById(R.id.blockly_fbtn_music);
        btnShow=root.findViewById(R.id.blockly_fbtn_show);
        drawerLayout=root.findViewById(R.id.drawer_layout);
        buttonHelp=root.findViewById(R.id.turtle_help);
        trashCan=root.findViewById(R.id.blockly_trash_icon);
        linearLayoutFakeToolbox=root.findViewById(R.id.turtle_fake_toolbox);
        buttonHelp.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        btnAgain.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        btnMusic.setOnClickListener(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter==null){
            Toast.makeText(this,"不支持蓝牙",Toast.LENGTH_LONG).show();
            Log.e("MainActivity","不支持蓝牙");
            finish();
        }
        else if(!bluetoothAdapter.isEnabled())
        {
            Log.d("MainActivity","开始连接");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,ENABLE_BLUETOOTH);
        }
        return root;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.blockly_fbtn_run:
                if (getController().getWorkspace().hasBlocks()) {
                    onRunCode();
                } else {
                    //Toast.makeText(TurtleActivity.this,R.string.no_block_in_workspace,Toast.LENGTH_SHORT).show();
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
                drawerLayout.closeDrawers();
                break;
            case R.id.blockly_fbtn_exit:
                finish();
                break;
            case R.id.blockly_fbtn_help:
                turtleActivityNewbieGuide();
                drawerLayout.closeDrawers();
                break;
            case R.id.turtle_help:
                turtleActivityNewbieGuide();
                break;
        }
    }

    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        return mCodeGeneratorCallback;
    }

    static void addDefaultVariables(BlocklyController controller) {//?????????????????????????????
        // TODO: (#22) Remove this override when variables are supported properly
        controller.addVariable("item");
        controller.addVariable("count");
        controller.addVariable("marshmallow");
        controller.addVariable("lollipop");
        controller.addVariable("kitkat");
        controller.addVariable("android");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //适配屏幕引入语句
        com.yatoooon.screenadaptation.ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());

        //隐藏Actionbar
        getSupportActionBar().hide();
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //清空工作区
        getController().resetWorkspace();
       // Util.showGuide(this, R.string.guide_draw_graph1, R.mipmap.guide_draw_graphs_3);
        Locale newLocale;
        if (MyApplication.languageFlag==0){
            newLocale= Locale.ENGLISH;
        }
        else {
            newLocale = Locale.CHINESE;
        }
        MyContextWrapper.wrap(TurtleActivity.this, newLocale);


        //是否第一次打开 画图，是则运行新手引导，否则不在此运行
        if(MyApplication.isFirstRun("TurtleActivityNewbieGuide"))
        {
            turtleActivityNewbieGuide();
        }
    }

    //新手引导
    public void turtleActivityNewbieGuide(){

        NewbieGuide.with(this).setLabel("page").alwaysShow(true)//true:程序员开发调试时使用，每次打开APP显示新手引导;false:只在用户第一次打开APP显示新手引导
                .addGuidePage(
                        GuidePage.newInstance()
//可以一个引导页多个高亮，多加几个.addHighLight()即可
                                .addHighLight(buttonHelp)
                                .addHighLight(btnRun)
                                .addHighLight(trashCan)
                                .addHighLight(ptnRun)
                                .addHighLight(linearLayoutFakeToolbox)
                                .setLayoutRes(R.layout.turtle_activity_newbie_guide)//引导页布局，点击跳转下一页或者消失引导层的控件id
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase));
    }

    /**
     * 初始化ArrayList（内容是每个关卡的标准答案）
     */
    public void initArrayList() {
        //初始化将每一关卡代码用ArrayList存储  ！！！图形题目还没有编写好！！！todo！！！
        String CodePreview1 = "moveForward(100);\n" +
                "moveBackward(100);";

        String CodePreview2 = "moveForward(100);\n" +
                "turnRight(90);\n" +
                "moveForward(100);";

        String CodePreview3 = "for (var count = 0; count < 4; count++) {\n" +      //2.循环结构画正方形
                "  moveForward(100);\n" +
                "  turnRight(90);\n" +
                "}";

        String CodePreview4 = "turnRight(30);\n" +                                //3.等边三角形
                "moveForward(100);\n" +
                "turnRight(120);\n" +
                "moveForward(100);\n" +
                "turnRight(120);\n" +
                "moveForward(100);";

        String CodePreview5 = "for (var count = 0; count < 5; count++) {\n" +
                "  moveForward(100);\n" +
                "  turnRight(72);\n" +
                "}";

        String CodePreview6 = "moveForward(50);\n" +                              //5.特定要求长方形
                "turnRight(90);\n" +
                "penColour('#ff0000');\n" +
                "moveForward(100);\n" +
                "turnRight(90);\n" +
                "penColour('#000000');\n" +
                "moveForward(50);\n" +
                "turnRight(90);\n" +
                "penColour('#ff0000');\n" +
                "moveForward(100);";
        String CodePreview6_second = "moveForward(50);\n" +                              //5.特定要求长方形
                "penColour('#ff0000');\n" +
                "turnRight(90);\n" +
                "moveForward(100);\n" +
                "turnRight(90);\n" +
                "penColour('#000000');\n" +
                "moveForward(50);\n" +
                "turnRight(90);\n" +
                "penColour('#ff0000');\n" +
                "moveForward(100);";

        String CodePreview7 = "penColour('#ff0000');\n" +
                "penWidth(60);\n" +
                "moveForward(0);\n" +
                "hideTurtle();";
        String CodePreview7_second = "penWidth(60);\n" +
                "penColour('#ff0000');\n" +
                "moveForward(0);\n" +
                "hideTurtle();";

        String CodePreview8 = "penColour('#ff0000');\n" +             //6.等腰梯形 腰：50 上底：50 下底：100
                "turnRight(30);\n" +
                "moveForward(50);\n" +
                "penColour('#000000');\n" +
                "turnRight(60);\n" +
                "moveForward(50);\n" +
                "penColour('#ff0000');\n" +
                "turnRight(60);\n" +
                "moveForward(50);\n" +
                "penColour('#000000');\n" +
                "turnRight(120);\n" +
                "moveForward(100);";

        String CodePreview9 = "moveForward(100);\n" +
                "turnRight(90);\n" +
                "penUp();\n" +
                "moveForward(100);\n" +
                "turnRight(90);\n" +
                "penDown();\n" +
                "moveForward(100);";

        String CodePreview10 = "turnRight(90);\n" +                 //打印文字 早教Blockly
                "drawPrint('早教');\n" +
                "turnLeft(90);\n" +
                "penUp();\n" +
                "moveForward(50);\n" +
                "turnRight(90);\n" +
                "drawPrint('Blockly');\n" +
                "hideTurtle();";

        String CodePreview11 = "for (var count = 0; count < 4; count++) {\n" +
                "  moveForward(50);\n" +
                "  turnRight(90);\n" +
                "}\n" +
                "turnLeft(90);\n" +
                "penUp();\n" +
                "moveForward(50);\n" +
                "penDown();\n" +
                "for (var count2 = 0; count2 < 4; count2++) {\n" +
                "  moveForward(100);\n" +
                "  turnRight(90);\n" +
                "}";

        String CodePreview12 = "for (var count = 0; count < 2; count++) {\n" +
                "  moveForward(100);\n" +
                "  turnRight(144);\n" +
                "}\n" +
                "penColour('#ff0000');\n" +
                "moveForward(100);\n" +
                "turnRight(144);\n"+
                "penColour('#000000');\n"+
                "for (var count2 = 0; count2 < 2; count2++) {\n"+
                "  moveForward(100);\n"+
                "  turnRight(144);\n"+
                "}";

        String CodePreview13="penColour('#ff0000');\n"+
                "for (var count = 0; count < 5; count++) {\n" +
                "  moveForward(100);\n" +
                "  turnRight(144);\n" +
                "}\n"+
                "turnRight(144);\n"+
                "penUp();\n"+
                "moveForward(50);\n" +
                "penDown();\n"+
                "penColour('#000000');\n"+
                "for (var count2 = 0; count2 < 3; count2++) {\n" +
                "  moveForward(100);\n" +
                "  turnRight(144);\n" +
                "}\n" +
                "penColour('#ff0000');\n" +
                "moveForward(100);\n" +
                "turnRight(144);\n"+
                "penColour('#000000');\n"+
                "moveForward(100);";

        String CodePreview14="for (var count = 0; count < 4; count++) {\n" +     //8.画一个大正方形里面套圆，圆圈涂成红色
                "  moveForward(100);\n" +
                "  turnRight(90);\n" +
                "}\n" +
                "moveForward(50);\n" +
                "turnRight(90);\n" +
                "moveForward(50);\n" +
                "penColour('#ff0000');\n" +
                "penWidth(100);\n" +
                "moveForward(0);\n" +
                "hideTurtle();";

        String CodePreview15="penUp();\n" +
                "moveForward(100);\n" +
                "penDown();\n" +
                "penWidth(60);\n" +
                "penColour('#ff0000');\n" +
                "moveForward(0);\n" +
                "penWidth(4);\n" +
                "turnRight(180);\n" +
                "moveForward(50);\n" +
                "turnRight(30);\n" +
                "moveForward(50);\n" +
                "turnRight(180);\n" +
                "moveForward(50);\n" +
                "turnRight(120);\n" +
                "moveForward(50);\n" +
                "turnRight(180);\n" +
                "moveForward(50);\n" +
                "turnLeft(150);\n" +
                "moveForward(100);\n" +
                "turnLeft(30);\n" +
                "moveForward(50);\n" +
                "turnRight(180);\n" +
                "moveForward(50);\n" +
                "turnLeft(120);\n" +
                "moveForward(50);";

        Temp temp1=new Temp();
        temp1.addtt(CodePreview1);
        CodePreviewList.add(temp1);
        Temp temp2=new Temp();
        temp2.addtt(CodePreview2);
        CodePreviewList.add(temp2);
        Temp temp3=new Temp();
        temp3.addtt(CodePreview3);
        CodePreviewList.add(temp3);
        Temp temp4=new Temp();
        temp4.addtt(CodePreview4);
        CodePreviewList.add(temp4);
        Temp temp5=new Temp();
        temp5.addtt(CodePreview5);
        CodePreviewList.add(temp5);
        Temp temp6=new Temp();
        temp6.addtt(CodePreview6);
        temp6.addtt(CodePreview6_second);
        CodePreviewList.add(temp6);
        Temp temp7=new Temp();
        temp7.addtt(CodePreview7);
        temp7.addtt(CodePreview7_second);
        CodePreviewList.add(temp7);
        Temp temp8=new Temp();
        temp8.addtt(CodePreview8);
        CodePreviewList.add(temp8);
        Temp temp9=new Temp();
        temp9.addtt(CodePreview9);
        CodePreviewList.add(temp9);
        Temp temp10=new Temp();
        temp10.addtt(CodePreview10);
        CodePreviewList.add(temp10);
        Temp temp11=new Temp();
        temp11.addtt(CodePreview11);
        CodePreviewList.add(temp11);
        Temp temp12=new Temp();
        temp12.addtt(CodePreview12);
        CodePreviewList.add(temp12);
        Temp temp13=new Temp();
        temp13.addtt(CodePreview13);
        CodePreviewList.add(temp13);
        Temp temp14=new Temp();
        temp14.addtt(CodePreview14);
        CodePreviewList.add(temp14);
        Temp temp15=new Temp();
        temp15.addtt(CodePreview15);
        CodePreviewList.add(temp15);
    }

    private void saveRating(){
        SharedPreferences sharedPreferences=getSharedPreferences("AllLevel",MODE_PRIVATE);
        int gameMaxLevel=sharedPreferences.getInt("gameTurtleMaxLevel",0);
        maxLevels=gameMaxLevel;
        int gameUnlockLevel=sharedPreferences.getInt("gameTurtleUnlockLevel",0);
        //只有 全部拼写正确 且 最大关卡为当前关卡， 数据库中的“最大关卡”才会+1
        if(gameMaxLevel>gameUnlockLevel){
            SharedPreferences.Editor editor=getSharedPreferences("AllLevel",MODE_PRIVATE).edit();
            editor.putInt("gameTurtleUnlockLevel",clickedLevel+2);
            editor.commit();
        }
        List<LevelInfo> rates= DataSupport.where("name = ?","turtle "+clickedLevel).find(LevelInfo.class);
        if(rates.isEmpty()){
            LevelInfo rate=new LevelInfo();
            rate.setName("turtle "+clickedLevel);
            rate.setModel("turtle");
            rate.setRating(rating);
            rate.save();
        }else{
            int oldRating=rates.get(0).getRating();
            if(rating>oldRating){
                LevelInfo rate=new LevelInfo();
                rate.setRating(rating);
                rate.updateAll("name = ?","turtle "+clickedLevel);
            }
        }
    }
    //蓝牙发送数据
    public void bluesend(byte[] message){
        try{
            outputStream = bluetoothSocket.getOutputStream();
            Log.d("send", Arrays.toString(message));
            outputStream.write(message);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(blueAddress);
//        try{
//            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
//            Log.d("true","开始连接");
//            bluetoothSocket.connect();
//            Log.d("true","完成连接");
//        }catch (IOException e){
//            e.printStackTrace();
//        }
        final Handler mHandler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //do something
                //每隔1s循环执行run方法
                try{
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
            Log.d("true","开始连接");
            bluetoothSocket.connect();
            Log.d("true","完成连接");
        }catch (IOException e){
            e.printStackTrace();
        }
            }
        };
        mHandler.postDelayed(r, 1000);//延时100毫秒
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        try{
            bluetoothSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public List<String> returnBlockType(String value, String[] type)
    {
        List<String> stringList=new ArrayList<>();
        if(type.length<=0||value.length()<=0)
            return null;
        else
        {
            while(StrMatching(value,type)!=-1)
            {
                int temp=StrMatching(value,type);
                stringList.add(type[temp]);
                int min=value.indexOf(type[temp]);
                value=value.substring(min+1);
            }
        }
        return stringList;
    }
    public int StrMatching(String value,String[] type)//指定从value字符串的第position个位置开始查找
    {
        int index=-1;
        int min=value.length()+1;
        for(int i=0;i<type.length;i++)
        {
            if(value.indexOf(type[i])!=-1&&value.indexOf(type[i])<min)
            {
                min=value.indexOf(type[i]);
                index=i;
            }
        }
        return index;
    }


}
