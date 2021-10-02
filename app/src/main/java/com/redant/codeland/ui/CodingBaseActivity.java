package com.redant.codeland.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.redant.codeland.util.AppLanguageUtils;
import com.redant.codeland.MyButton;
import com.redant.codeland.ParallelViewHelper;
import com.redant.codeland.R;
import com.redant.codeland.app.MyApplication;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

public class CodingBaseActivity extends AppCompatActivity implements View.OnClickListener {

    private int[] imageid={R.mipmap.coding_module_printf_chinese, R.mipmap.coding_module_math, R.mipmap.coding_module_variable
                          , R.mipmap.coding_module_logic, R.mipmap.coding_module_loop, R.mipmap.coding_module_array};
    private int[] moduleBackground={R.drawable.module_fillet_blue, R.drawable.module_fillet_red, R.drawable.module_fillet_green,
                                    R.drawable.module_fillet_pink, R.drawable.module_fillet_yellow, R.drawable.module_fillet_purple};
    private ImageView image_coding_ide;
    private MyButton button_back;

    private LinearLayout linearLayout1,linearlayout_horizonalscrollview_coding_base;

    private ParallelViewHelper parallelViewHelper1;
    private ParallelViewHelper parallelViewHelper2;
    private ParallelViewHelper parallelViewHelper3;
    private ParallelViewHelper parallelViewHelper4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coding_base);

        //Android6.0及以下多语言切换
        MyApplication.changeAppLanguage(getResources(),MyApplication.languageFlag);

        //这种getString(R.string.coding_output_text)方法必须放在方法里面，不能放在外面的
        String[] moduleName={getString(R.string.coding_output_text),getString(R.string.coding_math),getString(R.string.coding_variable),getString(R.string.coding_logic),getString(R.string.coding_loop),getString(R.string.coding_array)};

        //适配屏幕引入语句
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());


        linearLayout1=(LinearLayout)findViewById(R.id.linearlayout_horizonalscrollview_coding_base);
        for(int counter=0;counter<moduleName.length;counter++) {
            View inflateView = LayoutInflater.from(this).inflate(R.layout.linear_item, null);
            //适配屏幕引入语句
            ScreenAdapterTools.getInstance().loadView((ViewGroup) inflateView);
            inflateView.setId(counter);

            //识别不了，是因为没有implements OnClickListener，加入此接口
            inflateView.setOnClickListener(this);
            //如果不在前面加上inflateView. 那么findViewById就从当前的View(R.layout.activity_main)
            //找R.layout.linear_item，就找不到  而加了inflateView. 就从R.layout.linear_item里面找，就找到了
            ImageView imageView = (ImageView) inflateView.findViewById(R.id.image_horizonalscrollview);
            TextView textView=(TextView)inflateView.findViewById(R.id.textview_horizonalscrollview);

            textView.setText(moduleName[counter]);
            //因为第三模块的 教学 第一章节“输出文本”的配图有2张 你好，世界！ Hello, world! 所以要根据语言进行图片切换
            if(MyApplication.languageFlag==MyApplication.LANGUAGE_CHINESE){
                imageid[0]=R.mipmap.coding_module_printf_chinese;
            }else {
                imageid[0]=R.mipmap.coding_module_printf_usa;
            }
            imageView.setImageResource(imageid[counter]);
            imageView.setBackground(getResources().getDrawable(moduleBackground[counter]));
            linearLayout1.addView(inflateView);
        }

        image_coding_ide=(ImageView) findViewById(R.id.image_coding_ide);
        image_coding_ide.setOnClickListener(this);
        button_back=findViewById(R.id.button_back_coding);
        button_back.setOnClickListener(this);

        linearlayout_horizonalscrollview_coding_base=findViewById(R.id.linearlayout_horizonalscrollview_coding_base);

        //视差动画 实例化
        parallelViewHelper1 = new ParallelViewHelper(this, findViewById(R.id.background_module3),50,0 );
        parallelViewHelper2 = new ParallelViewHelper(this, findViewById(R.id.satellite_module),60,1 );
        parallelViewHelper3 = new ParallelViewHelper(this, findViewById(R.id.rocket_module),120,1 );
        parallelViewHelper4 = new ParallelViewHelper(this, findViewById(R.id.plane_module3),180,1 );

        //是否第一次打开APP，是则运行，否则不在此运行
        if(MyApplication.isFirstRun("CodingBaseActivityNewbieGuide"))
        {
            CodingBaseActivityNewbieGuide();
        }
    }

    //每个存在语言切换的Activity需要写
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase));
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        SharedPreferences.Editor editor=getSharedPreferences("Module3",MODE_PRIVATE).edit();

        switch (v.getId())
        {
            case 0:
                MyApplication.playClickVoice(CodingBaseActivity.this,"button");
                intent=new Intent(CodingBaseActivity.this,BaseLevelActivity.class);
                editor.putInt("clickedChapter",1);
                editor.commit();
                intent.putExtra("model","printf");
                startActivity(intent);
                break;
            case 1:
                MyApplication.playClickVoice(CodingBaseActivity.this,"button");
                intent=new Intent(CodingBaseActivity.this,BaseLevelActivity.class);
                editor.putInt("clickedChapter",2);
                editor.commit();
                intent.putExtra("model","math");
                startActivity(intent);
                break;
            case 2:
                MyApplication.playClickVoice(CodingBaseActivity.this,"button");
                intent=new Intent(CodingBaseActivity.this,BaseLevelActivity.class);
                editor.putInt("clickedChapter",3);
                editor.commit();
                intent.putExtra("model","variable");
                startActivity(intent);
                break;
            case 3:
                MyApplication.playClickVoice(CodingBaseActivity.this,"button");
                intent=new Intent(CodingBaseActivity.this,BaseLevelActivity.class);
                editor.putInt("clickedChapter",4);
                editor.commit();
                intent.putExtra("model","logic");
                startActivity(intent);
                break;
            case 4:
                MyApplication.playClickVoice(CodingBaseActivity.this,"button");
                intent=new Intent(CodingBaseActivity.this,BaseLevelActivity.class);
                editor.putInt("clickedChapter",5);
                editor.commit();
                intent.putExtra("model","loop");
                startActivity(intent);
                break;
            case 5:
                MyApplication.playClickVoice(CodingBaseActivity.this,"button");
                intent=new Intent(CodingBaseActivity.this,BaseLevelActivity.class);
                editor.putInt("clickedChapter",6);
                editor.commit();
                intent.putExtra("model","array");
                startActivity(intent);
                break;
            case R.id.image_coding_ide:
                MyApplication.playClickVoice(CodingBaseActivity.this,"button");
                intent=new Intent(CodingBaseActivity.this,CodingBlocklyActivity.class);
                startActivity(intent);
                break;
            case R.id.button_back_coding:
                onBackPressed();
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        parallelViewHelper1.stop();
        parallelViewHelper2.stop();
        parallelViewHelper3.stop();
        parallelViewHelper4.stop();


    }

    @Override
    protected void onStart() {
        super.onStart();
        parallelViewHelper1.start();
        parallelViewHelper2.start();
        parallelViewHelper3.start();
        parallelViewHelper4.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void CodingBaseActivityNewbieGuide(){

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
                                .addHighLight(image_coding_ide)
                                .setLayoutRes(R.layout.enlighten_activity_new_newbie_guide)//引导页布局，点击跳转下一页或者消失引导层的控件id
                                .setEverywhereCancelable(true)//是否点击任意地方跳转下一页或者消失引导层，默认true
//                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
//                                    @Override
//                                    //onLayoutInflated很重要，返回了 引导层的view，一定要利用好这个view，比如为引导层的view设定监听事件
//                                    public void onLayoutInflated(View view) {
//                                        TextView textViewNewbieGuide = view.findViewById(R.id.textview_mainactvitiy_newbie_guide);
//                                        textViewNewbieGuide.setText(getString(R.string.code_base_guide));
//
//                                        TranslateAnimation translateAnimation = new TranslateAnimation(
//                                                Animation.RELATIVE_TO_SELF,0,
//                                                Animation.RELATIVE_TO_SELF,0.5f,
//                                                Animation.RELATIVE_TO_SELF,0,
//                                                Animation.RELATIVE_TO_SELF,0
//                                        );
//                                        //imageView.setAnimation(translateAnimation);
////                                        ImageView imageView=(ImageView) findViewById(R.id.finger_touch);
////                                        imageView.startAnimation(translateAnimation);
//                                    }
//                                })
                                //这几行代码可以让引导界面慢慢过渡，从有到无
                                .setEnterAnimation(enterAnimation)//进入动画
                                .setExitAnimation(exitAnimation)//退出动画
                )
                .addGuidePage(
                        GuidePage.newInstance()
//可以一个引导页多个高亮，多加几个.addHighLight()即可
                                .addHighLight(linearlayout_horizonalscrollview_coding_base)
                                .setLayoutRes(R.layout.enlighten_activity_new_newbie_guide2)//引导页布局，点击跳转下一页或者消失引导层的控件id
                                .setEverywhereCancelable(true)//是否点击任意地方跳转下一页或者消失引导层，默认true
//                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
//                                    @Override
//                                    //onLayoutInflated很重要，返回了 引导层的view，一定要利用好这个view，比如为引导层的view设定监听事件
//                                    public void onLayoutInflated(View view) {
//                                        TextView textViewNewbieGuide = view.findViewById(R.id.textview_mainactvitiy_newbie_guide);
//
//                                    }
//                                })
                                //这几行代码可以让引导界面慢慢过渡，从有到无
                                .setEnterAnimation(enterAnimation)//进入动画
                                .setExitAnimation(exitAnimation)//退出动画
                ).show();
    }

}
