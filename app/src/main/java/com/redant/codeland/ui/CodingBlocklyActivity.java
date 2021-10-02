package com.redant.codeland.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.android.ui.TrashCanView;
import com.redant.codeland.util.AppLanguageUtils;
import com.redant.codeland.R;
import com.redant.codeland.adapter.RecordAdapter;

import com.redant.codeland.app.MyApplication;
import com.redant.codeland.entity.SavingRecord;
import com.redant.codeland.util.InputDialog;
import com.redant.codeland.util.JavascriptUtil;
import com.redant.codeland.util.MyJSInterface;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CodingBlocklyActivity extends AbstractBlocklyActivity implements View.OnClickListener{private WebView mWebview;
    private Button btnRun;
    private Button btnShow;
    private Button btnContinue;
    private Button btnExit;
    private Button btnLoad;
    private Button btnSave;
    private Button btnClear;
    private Button btnHelp;

    //调整了 保存 加载 按钮的位置
    private Button buttonLoadReal;
    private Button buttonSaveReal;

    private LinearLayout recordLayout;
    private DrawerLayout drawerLayout;
    private List<SavingRecord> savingRecordList=new ArrayList<>();
    private Button loadButton;
    private Button clearButton;
    private Button cancelButton;
    private Button renameButton;
    private TextView recordText;
    private RecordAdapter adapter;
    private ListView listView;
    private static int spaceNum=1;
    //    private String newName=null;
//    private SavingRecord savingRecord;
//    private String willLoadSpace;
    private String workspaceSavePath;
    private String workspaceLoadPath;

    private String LOG_TAG="WebViewTest";
    private TextView mReusultText;

    //新手引导帮助按钮
    private Button buttonHelp;
    //垃圾桶
    private TrashCanView trashCan;
    //toolbox的用于假装高亮的LinearLayout
    LinearLayout linearLayoutFakeToolbox;

    private static final String TAG = "NCBlocklyActivity";

    /**
     * Handler在Dialog点击确定按钮时触发，重新加载英语单词
     */
    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private CodeGenerationRequest.CodeGeneratorCallback mCallback = new CodeGenerationRequest.CodeGeneratorCallback() {
        @Override
        public void onFinishCodeGeneration(final String generatedCode) {
            Log.i(TAG, "onFinishCodeGeneration: "+generatedCode);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String encoded = "tp.execute("
                            + JavascriptUtil.makeJsString(generatedCode) + ")";
                    mWebview.loadUrl("javascript:"+encoded);
                }
            });
        }
    };

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {
        List<String> toolboxPaths = new ArrayList<>();
        if(MyApplication.languageFlag==1){
            toolboxPaths.add("coding/json_chinese/toolbox.xml");
        }
        else{
            toolboxPaths.add("coding/json_english/toolbox.xml");
        }

        return toolboxPaths.get(0);
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
        View root = getLayoutInflater().inflate(R.layout.activity_coding_blockly, null);
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
                new AlertDialog.Builder(CodingBlocklyActivity.this).
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

        recordText=root.findViewById(R.id.record_text);
        recordLayout=root.findViewById(R.id.record_layout);
        btnRun = root.findViewById(R.id.coding_button_run);
        btnContinue=root.findViewById(R.id.blockly_fbtn_continue);
        btnExit=root.findViewById(R.id.blockly_fbtn_exit);
        btnLoad=root.findViewById(R.id.blockly_fbtn_load);
        btnSave=root.findViewById(R.id.blockly_fbtn_save);
        btnClear=root.findViewById(R.id.blockly_fbtn_clear);
        btnHelp=root.findViewById(R.id.blockly_fbtn_help);
        btnShow=root.findViewById(R.id.blockly_fbtn_show);
        drawerLayout=root.findViewById(R.id.drawer_layout);
        loadButton=root.findViewById(R.id.button_load);
        cancelButton=root.findViewById(R.id.button_cancel);
        clearButton=root.findViewById(R.id.button_clear);
        renameButton=root.findViewById(R.id.button_rename);
        buttonLoadReal=root.findViewById(R.id.blockly_fbtn_load_real);
        buttonSaveReal=root.findViewById(R.id.blockly_fbtn_save_real);
        buttonHelp=root.findViewById(R.id.coding_blockly_help);
        trashCan=root.findViewById(R.id.blockly_trash_icon);
        linearLayoutFakeToolbox=root.findViewById(R.id.coding_blockly_fake_toolbox);
        buttonHelp.setOnClickListener(this);
        renameButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        loadButton.setOnClickListener(this);
        btnRun.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnLoad.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        buttonSaveReal.setOnClickListener(this);
        buttonLoadReal.setOnClickListener(this);

        mReusultText= root.findViewById(R.id.mReusultText);
        //让文本支持滑动
        mReusultText.setMovementMethod(ScrollingMovementMethod.getInstance());
        mReusultText.setOnClickListener(this);


        return root;
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.coding_button_run:
                //运行代码
                if (getController().getWorkspace().hasBlocks()) {
                    onRunCode();
//                    btnRun.setClickable(false);
                    //每次点击运行按钮后，清空文本
                    mReusultText.setText("");
                    //Toast.makeText(CodingBlocklyActivity.this,getSavingDate(),Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "工作区中没有块");
                    Toast.makeText(CodingBlocklyActivity.this,getString(R.string.no_block_in_workspace),Toast.LENGTH_SHORT).show();
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
            case R.id.blockly_fbtn_load:
                loadNewRecord();
                drawerLayout.closeDrawers();
                workspaceLoadPath="";
                break;
            case R.id.blockly_fbtn_save:
                saveNewRecord();
//                onSaveWorkspace();
                drawerLayout.closeDrawers();
                break;
            case R.id.blockly_fbtn_clear:
                onClearWorkspace();
                drawerLayout.closeDrawers();
                break;
            case R.id.blockly_fbtn_help:
                codingBlocklyActivityNewbieGuide();
                drawerLayout.closeDrawers();
                break;
            case R.id.button_load:
                //load
                doLoad();
                break;
            case R.id.button_cancel:

                initListView();
                break;
            case R.id.button_clear:
                clearAllRecord();
                break;
            case R.id.button_rename:
                renameRecord();
                break;
            case R.id.mReusultText:
//                if(mReusultText.getMaxLines()==3){
//                    mReusultText.setLines(1);
//                }
//                else {
//                    mReusultText.setLines(3);
//                }
                break;
            case R.id.blockly_fbtn_load_real:
                loadNewRecord();
                workspaceLoadPath="";
                break;
            case R.id.blockly_fbtn_save_real:
                saveNewRecord();
                break;
            case R.id.coding_blockly_help:
                codingBlocklyActivityNewbieGuide();
                break;
            }

    }

    @NonNull
    @Override
    protected String getWorkspaceSavePath() {
        return workspaceSavePath+".xml";
    }

    protected String getWorkspaceLoadPath() {
        return workspaceLoadPath+".xml";
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

        initRecord();
        adapter=new RecordAdapter(CodingBlocklyActivity.this , R.layout.record_item , savingRecordList);
        listView=(ListView)findViewById(R.id.record_list_1);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                SavingRecord savingRecord=savingRecordList.get(position);
                //Toast.makeText(CodingBlocklyActivity.this,savingRecord.getSavingName()+savingRecord.getSavingWorkspace()+" 22",Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder=new AlertDialog.Builder(CodingBlocklyActivity.this);
                builder.setTitle("delete"+savingRecord.getSavingName());
                builder.setMessage("sure?");
                builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete something
                        DataSupport.deleteAll(SavingRecord.class,"savingName = ? and savingWorkspace = ? and savingDate = ?",
                                savingRecordList.get(position).getSavingName(),savingRecordList.get(position).getSavingWorkspace(),savingRecordList.get(position).getSavingDate());
                        int lastPos=savingRecordList.size()-1;
                        savingRecordList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("cancel",null);
                builder.create().show();

                return false;
            }
        });

        //是否第一次打开 编程乐园，是的话显示 新手引导
        if(MyApplication.isFirstRun("CodingBlocklyActivityNewbieGuide"))
        {
            codingBlocklyActivityNewbieGuide();
        }

    }

    public String  getSavingDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        return (simpleDateFormat.format(date));
    }

    @Override
    public void onSaveWorkspace() {
        mBlocklyActivityHelper.saveWorkspaceToAppDirSafely(getWorkspaceSavePath());
    }

    @Override
    public void onLoadWorkspace() {
        mBlocklyActivityHelper.loadWorkspaceFromAppDirSafely(getWorkspaceLoadPath());
    }

    private void initRecord(){
        List<SavingRecord> savingRecords= DataSupport.findAll(SavingRecord.class);
        savingRecordList.clear();
        if(savingRecords.size()>0){
            for(SavingRecord record:savingRecords){
                savingRecordList.add(record);
            }
        }
    }

    private void saveNewRecord(){

        InputDialog dialog=new InputDialog(CodingBlocklyActivity.this,new InputDialog.OnEditInputFinishedListener(){
            @Override
            public void editInputFinished(String name) {
                String newName=name;
                SavingRecord savingRecord;
                if( !(newName==null || newName.equals("")) ){
                    savingRecord=new SavingRecord(newName,getSavingDate());
                    List<SavingRecord> savingRecords= DataSupport.findAll(SavingRecord.class);
                    String recordSpace="workspace"+spaceNum;
                    if(savingRecords.size()>0){
                        for(int i=0;i<savingRecords.size();i++){
                            SavingRecord record=savingRecords.get(i);
                            if(record.getSavingWorkspace().equals(recordSpace)){
                                spaceNum++;
                                recordSpace="workspace"+spaceNum;
                                i=-1;
                            }
                        }
                    }
//                    Log.d("savepath",workspaceSavePath);
//                    Toast.makeText(CodingBlocklyActivity.this,workspaceSavePath,Toast.LENGTH_SHORT).show();
                    workspaceSavePath=recordSpace;
                    savingRecord.setSavingWorkspace(recordSpace);
                    savingRecord.save();
                    initRecord();
                    onSaveWorkspace();
                }
            }
        });
        dialog.setView(new EditText(CodingBlocklyActivity.this));
        dialog.show();
    }

    private void loadNewRecord(){
        adapter.notifyDataSetChanged();
        recordLayout.setVisibility(View.VISIBLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SavingRecord savingRecord=savingRecordList.get(position);
                recordText.setText(savingRecord.getSavingName());
                workspaceLoadPath=savingRecord.getSavingWorkspace();
                Toast.makeText(CodingBlocklyActivity.this,workspaceLoadPath,Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                SavingRecord savingRecord=savingRecordList.get(position);
//                Toast.makeText(CodingBlocklyActivity.this,savingRecord.getSavingName()+savingRecord.getSavingWorkspace()+" 22",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(CodingBlocklyActivity.this);
                builder.setTitle(getString(R.string.delete)+savingRecord.getSavingName());
                builder.setMessage(R.string.confirm_delete);
                builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete something
                        DataSupport.deleteAll(SavingRecord.class,"savingName = ? and savingWorkspace = ? and savingDate = ?",
                                savingRecordList.get(position).getSavingName(),
                                savingRecordList.get(position).getSavingWorkspace(),
                                savingRecordList.get(position).getSavingDate());
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

    private void clearAllRecord(){

        AlertDialog.Builder builder=new AlertDialog.Builder(CodingBlocklyActivity.this);
        builder.setTitle(R.string.clear);
        builder.setMessage(R.string.clear_all_project);
        builder.setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataSupport.deleteAll(SavingRecord.class);
                adapter.notifyDataSetChanged();
                initRecord();
                initListView();
            }
        });
        builder.setNegativeButton(R.string.cancel,null);
        builder.create().show();
    }

    private void doLoad(){
        String name=recordText.getText().toString();
        if(name.equals("") || name==null){
            Toast.makeText(CodingBlocklyActivity.this, R.string.no_project_selected,Toast.LENGTH_SHORT).show();
        }else{
            onLoadWorkspace();
            initListView();
        }
    }

    private void renameRecord(){
        if(workspaceLoadPath.equals("") ){
            Toast.makeText(CodingBlocklyActivity.this,R.string.no_project_selected,Toast.LENGTH_SHORT).show();
            return;
        }
        InputDialog dialog=new InputDialog(CodingBlocklyActivity.this,new InputDialog.OnEditInputFinishedListener(){
            @Override
            public void editInputFinished(String name) {
                String newName=name;
                SavingRecord savingRecord;
                if( !(newName==null || newName.equals("")) ){
                    savingRecord=new SavingRecord();
                    savingRecord.setSavingName(newName);
                   // Toast.makeText(CodingBlocklyActivity.this,"in"+workspaceLoadPath,Toast.LENGTH_SHORT).show();
                    savingRecord.updateAll("savingWorkspace = ?",workspaceLoadPath);
                    recordText.setText(newName);
                    initRecord();
                    adapter.notifyDataSetChanged();
                    workspaceLoadPath="";
                }
            }
        });
        dialog.setView(new EditText(CodingBlocklyActivity.this));
        dialog.show();
    }

    private void initListView(){
        recordLayout.setVisibility(View.GONE);
        recordText.setText("");
    }

    //新手引导
    public void codingBlocklyActivityNewbieGuide(){

        NewbieGuide.with(this).setLabel("page").alwaysShow(true)//true:程序员开发调试时使用，每次打开APP显示新手引导;false:只在用户第一次打开APP显示新手引导
                .addGuidePage(
                        GuidePage.newInstance()
//可以一个引导页多个高亮，多加几个.addHighLight()即可
                                .addHighLight(mReusultText)
                                .addHighLight(buttonLoadReal)
                                .addHighLight(buttonSaveReal)
                                .addHighLight(btnRun)
                                .addHighLight(trashCan)
                                .addHighLight(linearLayoutFakeToolbox)
                                .setLayoutRes(R.layout.coding_blockly_activity_newbie_guide)//引导页布局，点击跳转下一页或者消失引导层的控件id
                                .setEverywhereCancelable(true)//是否点击任意地方跳转下一页或者消失引导层，默认true
                ).show();
    }

}
