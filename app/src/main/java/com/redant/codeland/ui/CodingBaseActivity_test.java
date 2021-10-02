package com.redant.codeland.ui;

//by pjh 2019.3.25 第三模块
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.redant.codeland.R;
import com.redant.codeland.adapter.CodingAdapter;
import com.redant.codeland.adapter.RecordAdapter;
import com.redant.codeland.entity.Coding;
import com.redant.codeland.entity.LevelInfo;
import com.redant.codeland.entity.SavingRecord;
import com.redant.codeland.scratchgame.scratchgameui.ProgramActivity;
import com.redant.codeland.util.InputDialog;
import com.redant.codeland.util.Util;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CodingBaseActivity_test extends AppCompatActivity {
    private WebView webView;
    private Button button;
    private Button button1;
    private Button load;
    private Button clear;
    private Button reName;
    private Button answer;
    private TextView resultText;
    private TextView recordText;
    private String workspaceLoadPath;
    //zhl新增
    private Button back;

    private  Button recordback;

    private int clickedChapter;//检查模块，例如文字、变量

    private int clickedLevel;       //点击的关卡
    private int maxLevel;           //最大关卡值
    private int rating=0;//星星个数
    private static int a=0;
    private static int b=0;
    private static int spaceNum=1;
    private ListView listView;
    private LinearLayout recordLayout;
    private CodingAdapter adapter;
    private List<Coding> savingRecordList=new ArrayList<>();
    //从上一个活动中获取scratchGameSingleUse来判断他是否是 教程项目（保存，该项目只适用于新手教程）
    String singUseFlag;
    //从上一个项目获取当前模块
    String model;


    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //更新数据库中的星星值

            if(msg.what == 1){
                //隐藏掉“礼花”效果
                //konfettiView.setVisibility(View.GONE);
              //  Toast.makeText(CodingBaseActivity_test.this,"代码块还有问题哦，再检查下吧",Toast.LENGTH_LONG).show();
                finish();


                Log.e("CodingBaseActivity_test","what1");

            }
//            //点击“再玩一次”
            else if(msg.what==2){
                //第三模块的这里，不需要任何操作
                //隐藏掉“礼花”效果
                //konfettiView.setVisibility(View.GONE);

            }
            //只有星星个数为3个，才会出现 下一关 选项
            else if(msg.what==3)
            {
                //隐藏掉“礼花”效果
                //konfettiView.setVisibility(View.GONE);
                if(clickedLevel<maxLevel){
                    //每次点击运行按钮后，清空文本
                    SharedPreferences.Editor editor=getSharedPreferences("AllLevel",MODE_PRIVATE).edit();
                    clickedLevel++;
                    editor.putInt("clickedLevel",clickedLevel);
                    editor.commit();
                }
                else
                {
                    finish();
                }


            }



            SharedPreferences sharedPreferences=getSharedPreferences("AllLevel",MODE_PRIVATE);
            int chapterUnlockLevel=sharedPreferences.getInt(model+"_UnlockLevel",0);


            List<LevelInfo> rates= DataSupport.where("name = ?",model+" "+clickedLevel).find(LevelInfo.class);
            if(rates.isEmpty()){
                LevelInfo rate=new LevelInfo();
                rate.setName(model+" "+clickedLevel);
                rate.setModel(model);
                rate.setRating(rating);
                rate.save();
            }
            else
            {
                int oldRating=rates.get(0).getRating();
                if(rating>oldRating){
                    LevelInfo rate=new LevelInfo();
                    rate.setRating(rating);
                    rate.updateAll("name = ?",model+" "+clickedLevel);
                }
            }

            //只有  全部拼写正确 并且 点击的关卡数>=已经解锁的最大关卡数， 数据库中的“最大关卡”才会+1
            if(clickedLevel>=chapterUnlockLevel&&rating==3){
                SharedPreferences.Editor editor=getSharedPreferences("AllLevel",MODE_PRIVATE).edit();
                editor.putInt(model+"_UnlockLevel",clickedLevel+1);
                editor.commit();
            }




            rating=0;
            //清空rating为0

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coding_base_test);
        //pjh 获取当前的模块 格式 coding_+
        final Intent intent=getIntent();
        singUseFlag=intent.getStringExtra("scratchGameSingleUse");
        model=intent.getStringExtra("model");

        initClickedChapter();

        webView=findViewById(R.id.code_test);


        Log.e("CodingBaseActivity_test",model);

        //保存代码块
        button=findViewById(R.id.coding_test1);
        recordText=findViewById(R.id.record_text);
        load=findViewById(R.id.button_load);
        clear=findViewById(R.id.button_clear);
        reName=findViewById(R.id.button_rename);
        //加载代码块
        button1=findViewById(R.id.coding_test_ss);
        resultText=findViewById(R.id.mResultText);
        recordLayout=findViewById(R.id.record_layout1);
        listView=findViewById(R.id.record_list_1);
        answer=findViewById(R.id.button_help_coding);
        back=findViewById(R.id.button_back_coding);
        resultText.setMovementMethod(ScrollingMovementMethod.getInstance());

        recordback=findViewById(R.id.button_cancel);

        //如果用户是从“输出文本”“数学”“变量”“逻辑”“循环”“数组”“函数”进入的该程序编程界面，那么设置任务提示按钮为可见；否则用户就是从“代码学习”和自定义的“试一试”进入的该程序编程界面，那么设置任务提示按钮为隐藏
        if(model.equals("coding_printf")||model.equals("coding_math")||model.equals("coding_variable")||model.equals("coding_logic")||model.equals("coding_loop")||model.equals("coding_array")||model.equals("coding_function")) {
            answer.setVisibility(View.VISIBLE);
        }


        WebSettings webSettings = webView.getSettings();
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //Log.e("coding_model",model);
        webView.loadUrl("file:///android_asset/blockly-master/demos/code/index.html?model="+model);
        initRecord();
        adapter=new CodingAdapter(CodingBaseActivity_test.this, R.layout.record_item , savingRecordList);
        listView.setAdapter(adapter);
        //实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences= getSharedPreferences("test",
                Activity.MODE_PRIVATE);
//实例化SharedPreferences.Editor对象（第二步）
        final SharedPreferences.Editor editor = mySharedPreferences.edit();
        SharedPreferences levelSharedPreferences=getSharedPreferences("AllLevel",MODE_PRIVATE);
        clickedLevel=levelSharedPreferences.getInt("clickedLevel",1);
        maxLevel=levelSharedPreferences.getInt(model+"_MaxLevel",1);
        Util.showModule3TipDialog(CodingBaseActivity_test.this,clickedChapter,clickedLevel,handler);
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.showModule3TipDialog(CodingBaseActivity_test.this,clickedChapter,clickedLevel,handler);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url,final String message, final JsResult result) {

                //因为onJsAlert()方法会先于回调的方法执行,所以必须延时打印
                Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                { @Override
                public void run() {
                    resultText.append(message + "\n");
                }
                }, 100);//0.1秒后执行Runnable中的run方法
                //Toast.makeText(CodingBaseActivity_test.this, message, Toast.LENGTH_SHORT).show();
                result.confirm();
                //Log.e("message:",message);
                //confirm()用于确认，只有webview在获得用户的确认后，才可以操作
                return true;
                //return super.onJsAlert(view,url,message,result);
            }
//            @Override
//            public boolean onJsPrompt(WebView view, String url, String message,
//                                      String defaultValue, JsPromptResult result) {
//                Log.d("CodingBaseActivity_test","onJsPrompt:"+message);
//                resultText.setText("Prompt input is :"+message);
//                result.confirm();
//                return super.onJsPrompt(view, url, message, message, result);
//            }
        });
// 复写WebViewClient类的shouldOverrideUrlLoading方法
        //用于web调用android的方法
        webView.setWebViewClient(new WebViewClient() {
                                     @Override
                                     public boolean shouldOverrideUrlLoading(WebView view, String url) {

                                         // 步骤2：根据协议的参数，判断是否是所需要的url
                                         // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
                                         //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）

                                         Uri uri = Uri.parse(url);
                                         // 如果url的协议 = 预先约定的 js 协议
                                         // 就解析往下解析参数
                                         if ( uri.getScheme().equals("js")) {

                                             // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                                             // 所以拦截url,下面JS开始调用Android需要的方法
                                             if (uri.getAuthority().equals("webview")) {

                                                 //  步骤3：
                                                 // 执行JS所需要调用的逻辑
                                                 //加载会先于清理执行执行的错误
                                                 resultText.setText("");
                                                 a=1;
                                                 JudgeBlock();
                                                 // 可以在协议上带有参数并传递到Android上
                                                 //参数暂时用不上
                                                 HashMap<String, String> params = new HashMap<>();
                                                 Set<String> collection = uri.getQueryParameterNames();

                                             }

                                             return true;
                                         }
                                         return super.shouldOverrideUrlLoading(view, url);
                                     }
                                 }
        );
        //保存按钮;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(CodingBaseActivity_test.this,"l",Toast.LENGTH_SHORT).show();
                webView.evaluateJavascript("index:Code.changeLanguage()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
//                        editor.putString("JS",value);
//                        editor.commit();
                        saveNewRecord(value);
                    }
                });
            }
        });
        //此处需改为仅加载一次
        //加载按钮
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferences sharedPreferences= getSharedPreferences("test",Activity.MODE_PRIVATE);
//                String runJS= sharedPreferences.getString("JS", "");
//                Log.e("sharedJS",runJS);
//                Toast.makeText(CodingBaseActivity_test.this,"2",Toast.LENGTH_SHORT).show();
//                webView.evaluateJavascript("index:Code.loadBlock("+runJS+")", new ValueCallback<String>() {
//                    @Override
//                    public void onReceiveValue(String value) {
//                        //此处为 js 返回的结果
//                    }
//                });
               // Toast.makeText(CodingBaseActivity_test.this,"ss",Toast.LENGTH_LONG).show();
                workspaceLoadPath="";
                loadRecord();
            }
        });
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=recordText.getText().toString();
                if(name.equals("") || name==null){
                    Toast.makeText(CodingBaseActivity_test.this, R.string.no_project_selected,Toast.LENGTH_SHORT).show();
                }else{
                    webView.evaluateJavascript("index:Code.loadBlock("+workspaceLoadPath+")", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            //此处为 js 返回的结果
                        }
                    });
                    initListView();
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllRecord();
            }
        });
        reName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameRecord();
            }
        });
        //zhl新增
        recordback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordLayout.setVisibility(View.GONE);
            }
        });

    }
    private void initListView(){
        recordLayout.setVisibility(View.GONE);
        recordText.setText("");
    }
    private void saveNewRecord(final String code){
        InputDialog dialog=new InputDialog(CodingBaseActivity_test.this,new InputDialog.OnEditInputFinishedListener(){
            @Override
            public void editInputFinished(String name) {
                Coding coding=new Coding();
                coding.setCode(code);
                coding.setName(name);
                coding.setDate(getSavingDate());
                coding.save();
                initRecord();
            }
        });
        dialog.setView(new EditText(CodingBaseActivity_test.this));
        dialog.show();
    }
    private void loadRecord(){
        adapter.notifyDataSetChanged();
        recordLayout.setVisibility(View.VISIBLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Coding savingRecord=savingRecordList.get(position);
                recordText.setText(savingRecord.getName());
                workspaceLoadPath=savingRecord.getCode();
                //Toast.makeText(CodingBaseActivity_test.this,workspaceLoadPath,Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Coding savingRecord=savingRecordList.get(position);
//                Toast.makeText(CodingBlocklyActivity.this,savingRecord.getSavingName()+savingRecord.getSavingWorkspace()+" 22",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(CodingBaseActivity_test.this);
                builder.setTitle(getString(R.string.delete)+savingRecord.getName());
                builder.setMessage(R.string.confirm_delete);
                builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete something
                        DataSupport.deleteAll(Coding.class,"name = ? and code = ? and date = ?",
                                savingRecordList.get(position).getName(),
                                savingRecordList.get(position).getCode(),
                                savingRecordList.get(position).getDate());
//
                        savingRecordList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel),null);
                builder.create().show();

                return false;
            }
        });
    }
    private void initRecord(){
        List<Coding> savingRecords= DataSupport.findAll(Coding.class);
        savingRecordList.clear();
        if(savingRecords.size()>0){
            for(Coding record:savingRecords){
                savingRecordList.add(record);
            }
        }
    }
    public String  getSavingDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        return (simpleDateFormat.format(date));
    }
    private void clearAllRecord(){

        AlertDialog.Builder builder=new AlertDialog.Builder(CodingBaseActivity_test.this);
        builder.setTitle(R.string.clear);
        builder.setMessage(R.string.clear_all_project);
        builder.setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataSupport.deleteAll(Coding.class);
                adapter.notifyDataSetChanged();
                initRecord();
                initListView();
            }
        });
        builder.setNegativeButton(R.string.cancel,null);
        builder.create().show();
    }
    private void renameRecord(){
        if(workspaceLoadPath.equals("") ){
            Toast.makeText(CodingBaseActivity_test.this,R.string.no_project_selected,Toast.LENGTH_SHORT).show();
            return;
        }
        InputDialog dialog=new InputDialog(CodingBaseActivity_test.this,new InputDialog.OnEditInputFinishedListener(){
            @Override
            public void editInputFinished(String name) {
                String newName=name;
                Coding savingRecord;
                if( !(newName==null || newName.equals("")) ){
                    savingRecord=new Coding();
                    savingRecord.setName(newName);
                    // Toast.makeText(CodingBlocklyActivity.this,"in"+workspaceLoadPath,Toast.LENGTH_SHORT).show();
                    savingRecord.updateAll("code = ?",workspaceLoadPath);
                    recordText.setText(newName);
                    initRecord();
                    adapter.notifyDataSetChanged();
                    workspaceLoadPath="";
                }
            }
        });
        dialog.setView(new EditText(CodingBaseActivity_test.this));
        dialog.show();
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

    private void JudgeBlock()
    {

        webView.evaluateJavascript("index:Code.changeLanguage()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                //此处为 js 返回的结果
                List<String> stringList;


                Log.e("CodingBaseActivity_test",value);

                if(model.equals("coding_printf"))
                {

                    String[] type_printf={"\\\"text_print\\\"","\\\"text\\\""};
                    stringList=returnBlockType(value,type_printf);
                    switch (clickedLevel)
                    {
                        case 1:
                            String[] strings1={"\\\"text_print\\\"","\\\"text\\\""};
                            if(newStringEquals(strings1,stringList))
                            {
                                Log.e("CodingBaseActivity_test","SongSHaoKai");
                                rating=3;
                            }
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 2:
                            String[] strings2={"\\\"text_print\\\"","\\\"text\\\"","\\\"text\\\"",
                                    "\\\"text_print\\\"","\\\"text\\\"","\\\"text\\\""};
                            if(newStringEquals(strings2,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 3:
                            String[] strings3={"\\\"text_print\\\"","\\\"text\\\"","\\\"text\\\"",
                                    "\\\"text_print\\\"","\\\"text\\\"","\\\"text\\\"",
                                    "\\\"text_print\\\"","\\\"text\\\"","\\\"text\\\""};
                            if(newStringEquals(strings3,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        default:
                            rating=2;
                            break;

                    }
                }
                else if(model.equals("coding_math"))
                {
                    String[] type_math={"\\\"text_print\\\"","\\\"text\\\"","\\\"math_number\\\"","\\\"math_arithmetic\\\""
                            ,"\\\"math_single\\\"","\\\"math_trig\\\"","\\\"math_modulo\\\""};
                    stringList=returnBlockType(value,type_math);

                    switch (clickedLevel)
                    {
                        case 1:
                            String[] strings1={"\\\"text_print\\\"","\\\"text\\\"","\\\"math_number\\\""};

                            if(newStringEquals(strings1,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }

                            break;
                        case 2:
                            String[] strings2={"\\\"text_print\\\"","\\\"text\\\"","\\\"math_arithmetic\\\"",
                                    "\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\""};
                            if(newStringEquals(strings2,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 3:
                            String[] strings3={"\\\"text_print\\\"","\\\"text\\\"","\\\"math_single\\\"",
                                    "\\\"math_number\\\"","\\\"math_number\\\""};
                            if(newStringEquals(strings3,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 4:
                            String[] strings4={"\\\"text_print\\\"","\\\"text\\\"","\\\"math_trig\\\"",
                                    "\\\"math_number\\\"","\\\"math_number\\\""};
                            if(newStringEquals(strings4,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 5:
                            String[] strings5={"\\\"text_print\\\"","\\\"text\\\"","\\\"math_arithmetic\\\"",
                                    "\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\""};
                            if(newStringEquals(strings5,stringList)) {
                                rating = 3;
                            }
                            else
                            {
                                rating=2;
                            }
                            break;
                        default:
                            rating=2;
                            break;
                    }
                }
                else if(model.equals("coding_variable"))
                {
                    String[] type_variable={"\\\"text_print\\\"","\\\"text\\\"","\\\"variables_set\\\"","\\\"math_number\\\"",
                            "\\\"variables_get\\\"","\\\"math_change\\\"","\\\"math_arithmetic\\\""};
                    stringList=returnBlockType(value,type_variable);
                    switch (clickedLevel)
                    {
                        case 1:
                            String[] strings1={"\\\"variables_set\\\"","\\\"math_number\\\"","\\\"text_print\\\"","\\\"text\\\"","\\\"variables_get\\\""};
                            if(newStringEquals(strings1,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 2:
                            String[] strings2={"\\\"variables_set\\\"","\\\"math_number\\\"","\\\"math_change\\\"","\\\"math_number\\\"","\\\"text_print\\\"","\\\"text\\\"","\\\"variables_get\\\""};
                            if(newStringEquals(strings2,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 3:
                            String[] strings3={"\\\"variables_set\\\"","\\\"math_number\\\"","\\\"variables_set\\\"","\\\"math_number\\\"",
                                    "\\\"text_print\\\"","\\\"text\\\"","\\\"math_arithmetic\\\"","\\\"math_number\\\""
                                    ,"\\\"variables_get\\\"","\\\"math_number\\\"","\\\"variables_get\\\""};
                            if(newStringEquals(strings3,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 4:
                            String[] strings4={"\\\"variables_set\\\"","\\\"math_number\\\"","\\\"variables_set\\\"","\\\"math_number\\\"",
                                    "\\\"text_print\\\"","\\\"text\\\"","\\\"math_arithmetic\\\"","\\\"math_number\\\""
                                    ,"\\\"variables_get\\\"","\\\"math_number\\\"","\\\"variables_get\\\"","\\\"text_print\\\"","\\\"text\\\"","\\\"math_arithmetic\\\"","\\\"math_number\\\""
                                    ,"\\\"variables_get\\\"","\\\"math_number\\\"","\\\"variables_get\\\""};
                            if(newStringEquals(strings4,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 5:
                            String[] strings5={"\\\"variables_set\\\"","\\\"math_number\\\"","\\\"variables_set\\\"","\\\"math_number\\\"",
                                    "\\\"text_print\\\"","\\\"text\\\"","\\\"math_arithmetic\\\"","\\\"math_number\\\""
                                    ,"\\\"variables_get\\\"","\\\"math_arithmetic\\\"","\\\"math_number\\\""
                                    ,"\\\"variables_get\\\"","\\\"math_number\\\"","\\\"variables_get\\\""};
                            if(newStringEquals(strings5,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        default:
                            rating=2;
                            break;
                    }
                }
                else if(model.equals("coding_logic"))
                {
                    String[] type_logic={"\\\"text_print\\\"","\\\"text\\\"","\\\"controls_if\\\"","\\\"logic_boolean\\\""
                            ,"\\\"logic_ternary\\\"","\\\"logic_compare\\\"","\\\"math_number\\\"","\\\"logic_operation\\\""};
                    stringList=returnBlockType(value,type_logic);
                    Log.e("CodingBaseActivity_test",stringList.toString());
                    switch (clickedLevel) {
                        case 1:
                            String[] strings1 = {"\\\"controls_if\\\"", "\\\"logic_boolean\\\"", "\\\"text_print\\\"",
                                    "\\\"text\\\"", "\\\"text\\\""};
                            if (newStringEquals(strings1, stringList))
                                rating = 3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 2:
                            String[] strings2 = {"\\\"text_print\\\"", "\\\"text\\\"", "\\\"logic_ternary\\\"", "\\\"logic_boolean\\\"",
                                    "\\\"text\\\"", "\\\"text\\\""};
                            if (newStringEquals(strings2, stringList))
                                rating = 3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 3:
                            String[] strings3 = {"\\\"text_print\\\"", "\\\"text\\\"", "\\\"logic_ternary\\\"", "\\\"logic_compare\\\"",
                                    "\\\"math_number\\\"", "\\\"math_number\\\"", "\\\"text\\\"", "\\\"text\\\""};
                            if (newStringEquals(strings3, stringList))
                                rating = 3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 4:
                            String[] strings4 = {"\\\"text_print\\\"", "\\\"text\\\"", "\\\"logic_ternary\\\"", "\\\"logic_operation\\\"",
                                    "\\\"logic_compare\\\"", "\\\"math_number\\\"", "\\\"math_number\\\"", "\\\"logic_compare\\\"",
                                    "\\\"math_number\\\"", "\\\"math_number\\\"", "\\\"text\\\"", "\\\"text\\\""};
                            if (newStringEquals(strings4, stringList))
                                rating = 3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 5:
                            String[] strings5 = {"\\\"text_print\\\"", "\\\"text\\\"", "\\\"logic_ternary\\\"", "\\\"logic_operation\\\"",
                                    "\\\"logic_compare\\\"", "\\\"math_number\\\"", "\\\"math_number\\\"", "\\\"logic_compare\\\"",
                                    "\\\"math_number\\\"", "\\\"math_number\\\"", "\\\"text\\\"", "\\\"text\\\""};
                            if (newStringEquals(strings5, stringList))
                                rating = 3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        default:
                            rating=2;
                            break;
                    }
                }
                else if(model.equals("coding_loop"))
                {
                    String[] type_loop={"\\\"text_print\\\"","\\\"text\\\"","\\\"math_number\\\"","\\\"controls_repeat_ext\\\"",
                            "\\\"controls_for\\\"","\\\"variables_get\\\"","\\\"controls_if\\\"","\\\"math_number_property\\\"",
                            "\\\"logic_compare\\\"","\\\"math_arithmetic\\\"","\\\"controls_flow_statements\\\""};
                    stringList=returnBlockType(value,type_loop);
                    switch(clickedLevel)
                    {
                        case 1:
                            String[] strings1={"\\\"controls_repeat_ext\\\"","\\\"math_number\\\"","\\\"math_number\\\"",
                                    "\\\"text_print\\\"","\\\"text\\\"","\\\"text\\\""};
                            Log.e("CodingBaseActivity_test",stringList.toString());
                            if(newStringEquals(strings1,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 2:
                            String[] strings2={"\\\"controls_for\\\"","\\\"math_number\\\"","\\\"math_number\\\"",
                                    "\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\"",
                                    "\\\"text_print\\\"","\\\"text\\\"","\\\"variables_get\\\""};
                            if(newStringEquals(strings2,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 3:
                            String[] strings3={"\\\"controls_for\\\"","\\\"math_number\\\"","\\\"math_number\\\"",
                                    "\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"controls_if\\\"",
                                    "\\\"math_number_property\\\"","\\\"math_number\\\"","\\\"variables_get\\\"","\\\"text_print\\\"",
                                    "\\\"text\\\"","\\\"variables_get\\\""};
                            if(newStringEquals(strings3,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 4:
                            String[] strings4={"\\\"controls_for\\\"","\\\"math_number\\\"","\\\"math_number\\\"",
                                    "\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"text_print\\\"",
                                    "\\\"text\\\"","\\\"variables_get\\\"","\\\"controls_if\\\"","\\\"logic_compare\\\"","\\\"math_number\\\"",
                                    "\\\"math_arithmetic\\\"","\\\"math_number\\\"","\\\"variables_get\\\"","\\\"math_number\\\"","\\\"text_print\\\"",
                                    "\\\"text\\\"","\\\"variables_get\\\""};
                            if(newStringEquals(strings4,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 5:
                            String[] strings5={"\\\"controls_for\\\"","\\\"math_number\\\"","\\\"math_number\\\"",
                                    "\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"controls_for\\\"","\\\"math_number\\\"","\\\"math_number\\\"",
                                    "\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"controls_if\\\"",
                                    "\\\"math_number_property\\\"","\\\"math_number\\\"","\\\"variables_get\\\"","\\\"text_print\\\"",
                                    "\\\"text\\\"","\\\"variables_get\\\""};
                            if(newStringEquals(strings5,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        default:
                            rating=2;
                            break;

                    }
                }
                else if(model.equals("coding_array"))
                {
                    String [] type_array={"\\\"variables_get\\\"","\\\"variables_set\\\"","\\\"text_print\\\"","\\\"text\\\"","\\\"math_number\\\"","\\\"lists_create_empty\\\"",
                            "\\\"lists_create_with\\\"","\\\"lists_length\\\"","\\\"lists_repeat\\\"","\\\"lists_indexOf\\\"","\\\"lists_getIndex\\\"",
                            "\\\"lists_getSublist\\\"","\\\"lists_setIndex\\\"","\\\"lists_sort\\\""};
                    stringList=returnBlockType(value,type_array);
                    Log.e("CodingBaseActivity_test",stringList.toString());
                    switch (clickedLevel){
                        case 1:
                            String[] strings1={"\\\"variables_set\\\"","\\\"lists_create_with\\\"","\\\"lists_repeat\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"text_print\\\"","\\\"text\\\"","\\\"variables_get\\\""};
                            if(newStringEquals(strings1,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 2:
                            String[] strings2={"\\\"variables_set\\\"","\\\"lists_create_with\\\"","\\\"lists_repeat\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"lists_setIndex\\\"","\\\"variables_get\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"lists_setIndex\\\"","\\\"variables_get\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"lists_setIndex\\\"","\\\"variables_get\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"text_print\\\"","\\\"text\\\"","\\\"variables_get\\\""};
                            if(newStringEquals(strings2,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 3:
                            String[] strings3={"\\\"variables_set\\\"","\\\"lists_create_with\\\"","\\\"lists_repeat\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"lists_setIndex\\\"","\\\"variables_get\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"lists_setIndex\\\"","\\\"variables_get\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"lists_setIndex\\\"","\\\"variables_get\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"text_print\\\"","\\\"text\\\"","\\\"lists_indexOf\\\"","\\\"variables_get\\\"","\\\"math_number\\\""};
                            if(newStringEquals(strings3,stringList))
                            {
                                rating=3;
                            }
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 4:
                            String[] strings4={"\\\"variables_set\\\"","\\\"lists_create_with\\\"","\\\"lists_repeat\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"lists_setIndex\\\"","\\\"variables_get\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"lists_setIndex\\\"","\\\"variables_get\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"lists_setIndex\\\"","\\\"variables_get\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"text_print\\\"","\\\"text\\\"","\\\"lists_sort\\\"","\\\"variables_get\\\""};
                            if(newStringEquals(strings4,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 5:
                            String[] strings5={"\\\"variables_set\\\"","\\\"lists_create_with\\\"","\\\"lists_repeat\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"lists_setIndex\\\"","\\\"variables_get\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"lists_setIndex\\\"","\\\"variables_get\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"lists_setIndex\\\"","\\\"variables_get\\\"","\\\"math_number\\\"","\\\"math_number\\\"",
                                    "\\\"lists_setIndex\\\"","\\\"variables_get\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"text_print\\\"","\\\"text\\\"","\\\"lists_getSublist\\\"","\\\"variables_get\\\"","\\\"math_number\\\"","\\\"math_number\\\""};
                            if(newStringEquals(strings5,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        default:
                            rating=2;
                            break;
                    }
                }
                else if(model.equals("coding_function"))
                {
                    String[] type_function={"\\\"procedures_callreturn\\\"","\\\"procedures_ifreturn\\\"","\\\"logic_boolean\\\"","\\\"procedures_defreturn\\\"","\\\"math_change\\\"","\\\"controls_for\\\"","\\\"variables_set\\\"","\\\"math_number\\\"","\\\"variables_get\\\"","\\\"text_print\\\"","\\\"text\\\"","\\\"procedures_defnoreturn\\\"","\\\"procedures_callnoreturn\\\""};
                    stringList=returnBlockType(value,type_function);
                    Log.e("CodingBaseActivity_test",stringList.toString());
                    switch (clickedLevel)
                    {
                        case 1:
                            String[] strings1={"\\\"procedures_defnoreturn\\\"","\\\"text_print\\\"","\\\"text\\\"","\\\"text\\\"","\\\"procedures_callnoreturn\\\""};
                            if(newStringEquals(strings1,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 2:
                            String[] strings2={"\\\"procedures_defnoreturn\\\"","\\\"text_print\\\"","\\\"text\\\"","\\\"variables_get\\\"","\\\"procedures_callnoreturn\\\"","\\\"text\\\""};
                            if(newStringEquals(strings2,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                            break;
                        case 3:
                            String[] strings3={"\\\"procedures_defnoreturn\\\"","\\\"variables_set\\\"","\\\"math_number\\\"","\\\"controls_for\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_number\\\"","\\\"math_change\\\"","\\\"math_number\\\"","\\\"variables_get\\\"","\\\"text_print\\\"","\\\"text\\\"","\\\"variables_get\\\"","\\\"procedures_callnoreturn\\\""};
                            if(newStringEquals(strings3,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                        case 4:
                            String[] strings4={"\\\"procedures_defreturn\\\"","\\\"variables_set\\\"","\\\"math_number\\\"","\\\"variables_get\\\"","\\\"text_print\\\"","\\\"text\\\"","\\\"procedures_callreturn\\\""};
                            if(newStringEquals(strings4,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                        case 5:
                            String[] strings5={"\\\"procedures_defreturn\\\"","\\\"procedures_ifreturn\\\"","\\\"variables_get\\\"","\\\"text\\\"","\\\"text\\\"","\\\"text_print\\\"","\\\"text\\\"","\\\"procedures_callreturn\\\"","\\\"variables_get\\\""};
                            if(newStringEquals(strings5,stringList))
                                rating=3;
                            else
                            {
                                rating=2;
                            }
                        default:
                            rating=2;
                            break;
                    }
                }
                Log.e("Activity",rating+"d");
                if(rating==3)
                {
                    Util.showDialog2(CodingBaseActivity_test.this, getString(R.string.success), 3,clickedLevel<maxLevel, handler);
                }
                else if(rating==2)
                {
                    Util.showDialog2(CodingBaseActivity_test.this, getString(R.string.fail), 0,clickedLevel<maxLevel, handler);
                }
                else
                {
                    //代码关失败不给提示
                }

            }
        });

    }

    private boolean newStringEquals(String[] stringArray,List<String> stringList)
    {
        if(stringArray.length!=stringList.size())
            return false;
        for(int i=0;i<stringArray.length;i++)
        {
            if(!stringArray[i].equals(stringList.get(i)))
                return false;
        }
        return true;
    }

    private void initClickedChapter()
    {
        switch (model)
        {
            case "coding_printf":
                clickedChapter=1;
                break;
            case "coding_math":
                clickedChapter=2;
                break;
            case "coding_variable":
                clickedChapter=3;
                break;
            case "coding_logic":
                clickedChapter=4;
                break;
            case "coding_loop":
                clickedChapter=5;
                break;
            case "coding_array":
                clickedChapter=6;
                break;
            case "coding_function":
                clickedChapter=7;
                break;
            default:
                break;
        }
    }


//    private void initRecord(){
//        List<SavingRecord> savingRecords= DataSupport.findAll(SavingRecord.class);
//        savingRecordList.clear();
//        if(savingRecords.size()>0){
//            for(SavingRecord record:savingRecords){
//                savingRecordList.add(record);
//            }
//        }
//    }
}
