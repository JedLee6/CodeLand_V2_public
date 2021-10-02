package com.redant.codeland.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.redant.codeland.R;
import com.redant.codeland.app.MyApplication;

public class BlocklyGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blockly_game);
        WebView webView=(WebView)findViewById(R.id.blockly_game_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        //根据不同的语言，加载不同语言的 更多游戏
        if(MyApplication.languageFlag==MyApplication.LANGUAGE_CHINESE){
            webView.loadUrl("file:///android_asset/blockly_games/index.html");
        }else {
            webView.loadUrl("file:///android_asset/blockly_games_en/index.html");
        }

    }
}
