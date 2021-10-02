package com.redant.codeland.scratchgame;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.redant.codeland.R;
import com.redant.codeland.app.MyApplication;
import com.redant.codeland.ui.KnowledgeLearningActivity;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Main activity for Scratch Jr., consisting of a full-screen landscape WebView.
 *
 * This activity creates an embedded WebView, which runs the HTML5 app containing the majority of the source code.
 *
 * Special thanks to Benesse Corp. for providing access to their Android port, which helped inspire some of the source code here.
 *
 * @author markroth8
 */
public class ScratchJrActivity extends Activity
{
    /** Milliseconds to pan when showing the soft keyboard */
    private static final int SOFT_KEYBOARD_PAN_MS = 250;
    
    /** Log tag for Scratch Jr. app */
    private static final String LOG_TAG = "ScratchJr";
    
    /** Bundle key in which the current url is stored */
    private static final String BUNDLE_KEY_URL = "url";

    /** The url of the index page */
    //这里把index.html改写成了home.html我们只需要项目文件夹界面即可，所以直接跳转到这个界面
    private static final String INDEX_PAGE_URL = "file:///android_asset/HTML5/home.html";
    //这里把home.html改写成了home.html?singleUse=yes 传参给webView告诉他 要建立一个教程的项目
    private static final String INDEX_PAGE_URL_SINGLE_USE = "file:///android_asset/HTML5/home.html?singleUse=yes";

    /** Container containing the web view */
    private RelativeLayout _container;

    /** Web browser containing the Scratch Jr. HTML5 webapp */
    private WebView _webView;

    /** Maintains connection to database */
    private DatabaseManager _databaseManager;

    /** Performs file IO */
    private IOManager _ioManager;

    /** Manages sounds */
    private SoundManager _soundManager;

    /** Manages recording of new sounds */
    private SoundRecorderManager _soundRecorderManager;

    /** Set to true when the app is initialized. This is used for unit testing. */
    private boolean _appInitialized = false;

    /** Set to true when the editor is initialized. This is used for unit testing. */
    private boolean _editorInitialized = false;

    /** Set to true when the splash screen is done loading. This is used for unit testing. */
    private boolean _splashDone = false;
    
    /** Y starting and ending coordinate for soft keyboard scroll position */
    private int _softKeyboardScrollPosY0;
    private int _softKeyboardScrollPosY1;
    public int count;

    /** Handler for posting delayed updates */
    private final Handler _handler = new Handler();

    /** Run-time Permissions */
    private final int SCRATCHJR_CAMERA_MIC_PERMISSION = 1;
    public int cameraPermissionResult = PackageManager.PERMISSION_DENIED;
    public int micPermissionResult = PackageManager.PERMISSION_DENIED;

    private SharedPreferences sharedPreferences;
    /** Analytics tracker */
    //private Tracker _tracker;

    //返回按钮，新手开发教程中的任务按钮
    Button button_back,button_check,button_guide,button_task;
    private LinearLayout linearLayout;

    //从上一个活动中获取scratchGameSingleUse来判断他是否是 教程项目（保存，该项目只适用于新手教程）
    String singUseFlag;
    //从上一个项目获取当前模块
    String model;
    int clickedLevel;
    JavaScriptDirectInterface javaScriptDirectInterface = new JavaScriptDirectInterface(ScratchJrActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        _databaseManager = new DatabaseManager(this);
        _ioManager = new IOManager(this);
        _soundManager = new SoundManager(this);
        _soundRecorderManager = new SoundRecorderManager(this);
        setContentView(R.layout.activity_scratch_jr);
        sharedPreferences = getSharedPreferences("AllLevel",MODE_PRIVATE);
        clickedLevel = sharedPreferences.getInt("clickedLevel",1);

        //Android6.0多语言切换
        MyApplication.changeAppLanguage(getResources(),MyApplication.languageFlag);
        //适配屏幕引入语句
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());

        //从上一个活动中获取scratchGameSingleUse来判断他是否是 教程项目（该项目只适用于新手教程）
        Intent intent=getIntent();
        singUseFlag=intent.getStringExtra("scratchGameSingleUse");
        model=intent.getStringExtra("model");

        //返回按钮
        button_back=(Button)findViewById(R.id.button_back_scratch_game);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = _webView.getUrl();
                if (url != null) {
                    Log.i(LOG_TAG, url);
                    //李俊德：在这里做了修改：用户在home.html界面点击 手机上Home键旁边的的“Back返回键”时，不再跳回index.html界面，直接finish这个活动
                    if (url.contains("home.html")) {
                        finish();
                    }else if (_webView.canGoBack()) {

                        _webView.goBack();
                    }
                }
            }
        });
        //教程按钮
        button_guide=(Button)findViewById(R.id.button_task_scratch_game_guide);
        button_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ScratchJrActivity.this,KnowledgeLearningActivity.class);
                intent.putExtra("model",model);
                startActivity(intent);
            }
        });


        //任务按钮
        button_task=(Button)findViewById(R.id.button_task_scratch_game_task);
        button_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTask(clickedLevel);
            }
        });


        //运行按钮
        button_check=(Button)findViewById(R.id.button_task_scratch_game_check);
        button_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (clickedLevel){
                    case 1:
                        runJavaScript("reloadProject(1)");
                        break;
                    case 2:
                        runJavaScript("reloadProject(2)");
                        break;
                    case 3:
                        runJavaScript("reloadProject(3)");
                        break;
                    case 4:
                        runJavaScript("reloadProject(4)");
                        break;
                    case 5:
                        runJavaScript("reloadProject(5)");
                        break;
                    case 6:
                        runJavaScript("reloadProject(6)");
                        break;
                    case 7:
                        runJavaScript("reloadProject(7)");
                        break;
                    case 8:
                        runJavaScript("reloadProject(8)");
                        break;
                    case 9:
                        runJavaScript("reloadProject(9)");
                        break;
                    case 10:
                        runJavaScript("reloadProject(10)");
                        break;
                    case 11:
                        runJavaScript("reloadProject(11)");
                        break;
                    case 12:
                        runJavaScript("reloadProject(12)");
                        break;

                    default:
                        break;

                }
            }
        });


         setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        _container = (RelativeLayout) findViewById(R.id.container);
        _webView = (WebView) findViewById(R.id.webview);
        _webView.setBackgroundColor(0x00000000);
        _webView.clearCache(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Log.i(LOG_TAG, "Setting non-immersive full screen");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            setImmersiveMode();
        }
        configureWebView();
        registerSoftKeyboardPanner();
        /* URL to load once ready */
        String urlToLoad;
        if ((savedInstanceState != null) && savedInstanceState.containsKey(BUNDLE_KEY_URL)) {
            Log.i(LOG_TAG, "Restoring bundle state...");
            urlToLoad = savedInstanceState.getString(BUNDLE_KEY_URL);
            if (urlToLoad == null) {
                urlToLoad = INDEX_PAGE_URL;
            }
        } else {
                urlToLoad = INDEX_PAGE_URL;
        }
        //如果是教程中打开的项目，直接结束项目，会自动保存到我的项目里面，不再返回我的项目界面，同时更新用户最大关卡数据
        if(singUseFlag!=null){
            urlToLoad=INDEX_PAGE_URL_SINGLE_USE;
        }
        _webView.loadUrl(urlToLoad);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.setAcceptFileSchemeCookies(true);

        String PROJECT_MIMETYPE = getApplicationContext().getString(R.string.share_mimetype);
        Intent it = getIntent();
        if (it != null && it.getType() != null && it.getType().equals(PROJECT_MIMETYPE)) {
            receiveProject(it.getData());
        }

        // When System UI bar is displayed, wait one second and then re-assert immersive mode.
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                _handler.postDelayed(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                setImmersiveMode();
                            }
                        });
                    }
                }, 1000);
            }
        });

        requestPermissions();
    }
    //在教程中，因为不好判断用户的积木代码块是否符合任务要求，那么只要用户进入了该界面，就算用户完成了教学，更新 最大关卡数

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
            setImmersiveMode();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // Check the WebView to see if we're on the editor page.
                // If so, call the JavaScript to save the current project
                // and return to the lobby.
                final String url = _webView.getUrl();
                if (url != null) {
                    Log.i(LOG_TAG, url);
                    //李俊德：在这里做了修改：用户在home.html界面点击 手机上Home键旁边的的“Back返回键”时，不再跳回index.html界面，直接finish这个活动
                    if (url.contains("home.html")) {
                        finish();
                    }
                    //李俊德：在这里做了修改：用户在hoem,html界面点击 手机上Home键旁边的的“Back返回键”时，不再跳回index.html界面，直接finish这个活动
                    else if (url.contains("editor.html")) {
                        runJavaScript("goBackToHome()");
                    } else if (_webView.canGoBack()) {
                        _webView.goBack();
                    }
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _databaseManager.open();
        _soundManager.open();
        _soundRecorderManager.open();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _webView.onResume();
                CookieSyncManager.getInstance().startSync();
            }
        });
        runJavaScript("if (typeof(ScratchJr) !== 'undefined') ScratchJr.onResume();");
    }

    @Override
    protected void onPause() {
        super.onPause();
        runJavaScript("if (typeof(ScratchJr) !== 'undefined') ScratchJr.onPause();");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _webView.onPause();
                CookieSyncManager.getInstance().stopSync();
            }
        });
        _databaseManager.close();
        _soundManager.close();
        _soundRecorderManager.close();
    }
    
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_KEY_URL, _webView.getUrl());
    }

    @Override
    protected void onNewIntent(Intent it) {
        super.onNewIntent(it);
        String PROJECT_MIMETYPE = getApplicationContext().getString(R.string.share_mimetype);
        if (it != null && it.getType() != null && it.getType().equals(PROJECT_MIMETYPE)) {
            receiveProject(it.getData());
        }
    }

    private void receiveProject(Uri projectUri) {
        // Read the project one byte at a time into a buffer
        ByteArrayOutputStream projectData = new ByteArrayOutputStream();
        try {
            InputStream is = getContentResolver().openInputStream(projectUri);
            byte[] readByte = new byte[1];
            while ((is.read(readByte)) == 1) {
                projectData.write(readByte[0]);
            }
        } catch (FileNotFoundException e) {
            Log.i(LOG_TAG, "File not found in project load");
            return;
        } catch (IOException e) {
            Log.i(LOG_TAG, "IOException in project load");
            return;
        }
        // We send the project Base64-encoded to JavaScript where it's processed and unpacked
        String base64Project = Base64.encodeToString(projectData.toByteArray(), Base64.DEFAULT);
        runJavaScript("iOS.loadProjectFromSjr('" + base64Project + "');");
    }

    public RelativeLayout getContainer() {
        return _container;
    }

    public DatabaseManager getDatabaseManager() {
        return _databaseManager;
    }

    public IOManager getIOManager() {
        return _ioManager;
    }

    public SoundManager getSoundManager() {
        return _soundManager;
    }

    public SoundRecorderManager getSoundRecorderManager() {
        return _soundRecorderManager;
    }

    private void setImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.i(LOG_TAG, "Setting immersive mode");
            int immersiveStickyFlag = 0;
            try {
                immersiveStickyFlag = View.class.getField("SYSTEM_UI_FLAG_IMMERSIVE_STICKY").getInt(null);
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException e) {
                Log.e(LOG_TAG, "Reflection fail", e);
            }
            _webView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_FULLSCREEN
                  | immersiveStickyFlag);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebView() {
        WebSettings webSettings = _webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(false);
        webSettings.setUseWideViewPort(false);
        // Uncomment to enable remote Chrome debugging on a physical Android device
        //WebView.setWebContentsDebuggingEnabled(true);

        // Enable cookie persistence
        CookieManager.setAcceptFileSchemeCookies(true);
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(_webView, true);
        } else {
            cookieManager.setAcceptCookie(true);
        }
        CookieSyncManager.createInstance(this);

        /* Object exposed to the JavaScript that makes it easy to bridge JavaScript and Java */
        JavaScriptDirectInterface javaScriptDirectInterface = new JavaScriptDirectInterface(this/*,  getApplication()*/);
        _webView.addJavascriptInterface(javaScriptDirectInterface, "AndroidInterface");
        _webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(LOG_TAG, description);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                //在home.html的时候，显示 Android下面的可返回的button，否则就是在editor.html和editor的子页面，这时候隐藏这个返回按钮
                if (url.contains("home.html")) {
                    button_back.setVisibility(View.VISIBLE);
                    //如果是教程项目，直接结束项目，会自动保存到 我的项目里面，不再返回 我的项目界面
                    if(singUseFlag!=null){
                        finish();
                    }
                }else {
                    button_back.setVisibility(View.GONE);
                }

                // Filter out Internet links and open those with the Android browser
                if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                return false; // Allow WebView to load url
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Sync cookies
                CookieSyncManager.getInstance().sync();

                // Track page load
                String[] parts = url.split("/");
                //_tracker.setScreenName(parts[parts.length - 1]);
                //_tracker.send(new HitBuilders.ScreenViewBuilder().build());
            }
        });
        _webView.requestFocus(View.FOCUS_DOWN);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowContentAccess(true);

        // Configure the web chrome client to consume console.log
        // calls from JavaScript.
        Log.i(LOG_TAG, "Configurating webChromeClient");
        _webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(@NonNull ConsoleMessage cm) {
                Log.e(LOG_TAG, "JavaScript log, " + cm.sourceId() + ":" + cm.lineNumber() + ", " + cm.message());
                return true;
            }
        });
    }

    public void createNewProject() {
        runJavaScript("Home.createNewProject()");
    }

    /**
     * Returns true when all resources are loaded and the app is initialized.
     */
    public boolean isAppInitialized() {
        return _appInitialized;
    }

    public void setAppInitialized(boolean initialized) {
        _appInitialized = initialized;
    }

    public boolean isEditorInitialized() {
        return _editorInitialized;
    }

    public void setEditorInitialized(boolean initialized) {
        _editorInitialized = initialized;
        //因为Android的UI是单线程的，所以，必须有一个2s延时，再把button_task显示出来，再自动打开教程
        //同时要加入一个判断，因为refreshUIHandler里面就是有教程自动弹出，只有教程关卡的活动才需要这个功能，所以加一个if判断:只有是教程关卡进去的才调用refreshUIHandler里面的方法
        if(singUseFlag!=null){
            refreshUIHandler.sendEmptyMessageDelayed(0,0);
        }
    }

    public boolean isSplashDone() {
        return _splashDone;
    }

    public void setSplashDone(boolean done) {
        _splashDone = done;
    }

    /**
     * Click the "Home" button on the title screen (for unit testing).
     */
    public void goHome() {
        runJavaScript("gohome()");
    }

    /**
     * Run the given JavaScript in the web view.
     */
    public void runJavaScript(final String js) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _webView.loadUrl("javascript:" + js);
            }
        });
    }

    public void translateAndScaleRectToContainerCoords(RectF rect, float devicePixelRatio) {
        float wx = _webView.getX();
        float wy = _webView.getY();
        rect.set(wx + rect.left * devicePixelRatio, wy + rect.top * devicePixelRatio,
            wx + rect.right * devicePixelRatio, wy + rect.bottom * devicePixelRatio);
    }

    public void setSoftKeyboardScrollLocation(int topYPx, int bottomYPx) {
        _softKeyboardScrollPosY0 = topYPx;
        _softKeyboardScrollPosY1 = bottomYPx;
    }
    
    /**
     * Height of the status bar at the top of the screen
     */
    private int getStatusBarHeight() {
        Rect rectangle= new Rect();
        Window window= getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int result = rectangle.top;
        return result;
    }

    /**
     * Android does not properly pan to the text fields in full screen mode, so
     * here we introduce some custom logic to pan when the soft keyboard appears.
     *
     * The technique used here was inspired by http://stackoverflow.com/questions/7417123/
     * android-how-to-adjust-layout-in-full-screen-mode-when-softkeyboard-is-visible
     */
    private void registerSoftKeyboardPanner() {
        _container.getViewTreeObserver().addOnGlobalLayoutListener(
            new OnGlobalLayoutListener() {
                private int _priorVisibleHeight;
                private ObjectAnimator _currentAnimator;

                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    _container.getWindowVisibleDisplayFrame(r);
                    int currentVisibleHeight = r.bottom - r.top;

                    // Determine if visible height changed
                    if (currentVisibleHeight != _priorVisibleHeight) {
                        // Determine if keyboard visibility changed
                        int screenHeight = _container.getRootView().getHeight();
                        int coveredHeight = screenHeight - currentVisibleHeight;
                        if((currentVisibleHeight < _priorVisibleHeight) && (coveredHeight > (screenHeight / 4))) {
                            // Keyboard probably just became visible

                            // Get the current focus elements top & bottom using a ratio to convert the values
                            // to the native scale.
                            int elTop = _softKeyboardScrollPosY0;
                            int elBottom = _softKeyboardScrollPosY1;

                            // Determine the amount of the focus element covered by the keyboard
                            int elPixelsCovered = elBottom - currentVisibleHeight;

                            // If any amount is covered
                            if (elPixelsCovered > 0) {
                                // Pan by the amount of coverage
                                int panUpPixels = elPixelsCovered;

                                // Prevent panning so much the top of the element becomes hidden
                                panUpPixels = panUpPixels > elTop ? elTop : panUpPixels;

                                // Prevent panning more than the keyboard height (which produces an empty gap in the screen)
                                int statusBarHeight = getStatusBarHeight();
                                panUpPixels = panUpPixels > (coveredHeight - statusBarHeight) ?
                                    (coveredHeight - statusBarHeight) : panUpPixels;

                                // Pan up
                                cancelAnimator();
                                ObjectAnimator animator = ObjectAnimator.ofFloat(_container, "y", _container.getY(), -panUpPixels).
                                    setDuration(SOFT_KEYBOARD_PAN_MS);
                                animator.start();
                                _currentAnimator = animator;
                            }
                            else {
                                cancelAnimator();
                                ObjectAnimator animator = ObjectAnimator.ofFloat(_container, "y", _container.getY(),
                                    getStatusBarHeight()).setDuration(SOFT_KEYBOARD_PAN_MS);
                                animator.start();
                                _currentAnimator = animator;
                            }
                        } else if (currentVisibleHeight > _priorVisibleHeight) {
                            // Keyboard probably just became hidden

                            // Reset pan
                            cancelAnimator();
                            ObjectAnimator animator = ObjectAnimator.ofFloat(_container, "y", _container.getY(), 0).
                                setDuration(SOFT_KEYBOARD_PAN_MS);
                            animator.start();
                            _currentAnimator = animator;
                            setImmersiveMode();
                            runJavaScript("if (typeof(ScratchJr) !== 'undefined') ScratchJr.editDone();");
                        }

                        // Save usable height for the next comparison
                        _priorVisibleHeight = currentVisibleHeight;
                    }
                }

                private void cancelAnimator() {
                    ObjectAnimator animator = _currentAnimator;
                    if (animator != null) {
                        if (animator.isStarted()) {
                            animator.cancel();
                        }
                        _currentAnimator = null;
                    }
                }
            });
    }



    /**
     *此方法的相关信息(All information about this function)
     *@fileName ScratchJrActivity
     *@date on 2018/11/19 18:53
     *@author JedLee 李俊德
     *@email 386236308@qq.com
     *@purpose（此方法的用途）教程关卡里面的需求，显示教程小灯泡，按钮自动打开教程页面
     */
    //handler的作用就是 教程关卡里面的需求，显示教程小灯泡，按钮自动打开教程页面
    public Handler refreshUIHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //这里面的方法其实就是 教程关卡里面的需求，显示教程小灯泡，按钮自动打开教程页面
            if(singUseFlag.equals("yes2")) {
                //从“猫捉老鼠”“男孩回家”“飞机大战”“老鼠偷金”进入Scratch编程界面只需要显示一个“教程提示按钮即可”
                button_guide.setVisibility(View.VISIBLE);
            }else{
                button_guide.setVisibility(View.VISIBLE);
                button_check.setVisibility(View.VISIBLE);
                button_task.setVisibility(View.VISIBLE);
            }
//            Intent intent=new Intent(ScratchJrActivity.this,KnowledgeLearningActivity.class);
//            intent.putExtra("model",model);
//            startActivity(intent);
            showTask(clickedLevel);
        }
    };

    public void showTask(int level){
        int[] arr={
                R.string.scratch_guide_1,
                R.string.scratch_guide_2,
                R.string.scratch_guide_3,
                R.string.scratch_guide_4,
                R.string.scratch_guide_5,
                R.string.scratch_guide_6,
                R.string.scratch_guide_7,
                R.string.scratch_guide_8,
                R.string.scratch_guide_9,
                R.string.scratch_guide_10,
                R.string.scratch_guide_11,
                R.string.scratch_guide_12
        };
        String T=(String)this.getResources().getText(arr[clickedLevel-1]);
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setHorizontalGravity(1);
        Button button = new Button(this);
        button.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        button.setText("继续");
        linearLayout.addView(button);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(T)
                .setView(linearLayout);
        final AlertDialog ad=builder.create();

        if(singUseFlag.equals("yes"))
        ad.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });
    }
}
