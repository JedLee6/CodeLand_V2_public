package com.redant.codeland.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.redant.codeland.R;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

public class KnowledgeLearningActivity extends AppCompatActivity {

    private WebView mWebView;

    protected int clickedLevel=1;//表示选择关卡难度，从sh中读取，键clickedLevel,用于加载相应教程pjh
    private String model;//表示当前是什么模块，从上个活动intent获得
    private Class nextClass;//表示下个要启动的活动类型
    private String urlModel;//用于从数据库中读的判断条件

    private Button button_back_knowledge,button_challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge_learning);

        //适配屏幕引入语句
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());

        //pjh用于根据模块加载教程
        Intent intent=getIntent();
        model=intent.getStringExtra("model");

        SharedPreferences sharedPreferences=getSharedPreferences("AllLevel",MODE_PRIVATE);
        clickedLevel=sharedPreferences.getInt("clickedLevel",1);
        refreshLevelandUrl(model);

        //Toast.makeText(KnowledgeLearningActivity.this,"lll",Toast.LENGTH_SHORT).show();

        //用户看完教程了，点击该按钮 finish掉这个页面，用户就可以直接代码块操作了
        button_challenge=(Button)findViewById(R.id.button_challenge);
        button_challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        button_back_knowledge=findViewById(R.id.button_back_knowledge);
        button_back_knowledge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void refreshLevelandUrl(String s){
        mWebView = findViewById(R.id.knowledge_learning_webview);
        WebSettings settings = mWebView.getSettings();
        //设置支持JS
        settings.setJavaScriptEnabled(true);
        //设置直接在webview中展示网页
        mWebView.setWebViewClient(new WebViewClient());
        if(s.compareTo("scratchGuideBlock")==0){
            //pjh 不同的模块加载不同的教程
            mWebView.loadUrl("file:///android_asset/scratchhtml/guide/html/"+clickedLevel+".html");
        }else if(s.compareTo("plane_fight")==0){
            mWebView.loadUrl("file:///android_asset/scratchhtml/game/html/1.html");
        }else if(s.compareTo("mouse_steal_gold")==0){
            mWebView.loadUrl("file:///android_asset/scratchhtml/game/html/2.html");
        }else if(s.compareTo("cat_and_mouse")==0){
            mWebView.loadUrl("file:///android_asset/scratchhtml/animation/html/1.html");
        }else if(s.compareTo("boy")==0){
            mWebView.loadUrl("file:///android_asset/scratchhtml/animation/html/2.html");
        }


    }

}
