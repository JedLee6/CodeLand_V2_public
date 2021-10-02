package com.redant.codeland.ui;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.redant.codeland.MyButton;
import com.redant.codeland.ParallelViewHelper;
import com.redant.codeland.R;
import com.redant.codeland.app.MyApplication;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

public class EnlightenActivityNew extends AppCompatActivity implements View.OnClickListener {


    private ImageView imageView_note_book;
    private LinearLayout linearlayout_horizonalscrollview;

    private ParallelViewHelper parallelViewHelper1;
    private ParallelViewHelper parallelViewHelper2;
    private ParallelViewHelper parallelViewHelper3;
    private ParallelViewHelper parallelViewHelper4;
    private ParallelViewHelper parallelViewHelper5;

    private int[] imageid={R.mipmap.english_module,R.mipmap.animal_module,R.mipmap.poetry_module,
                            R.mipmap.sanzijing_module,R.mipmap.celebrity_module};
    private int[] moduleName={R.string.enlighten_module_english,R.string.enlighten_module_animal,R.string.enlighten_module_poetry, R.string.enlighten_module_sanzijing,R.string.enlighten_module_celebrity};
    private LinearLayout linearLayout1;
    private int[] moduleBackground={R.drawable.module_fillet_blue,R.drawable.module_fillet_green,R.drawable.module_fillet_pink,
                                    R.drawable.module_fillet_purple,R.drawable.module_fillet_red};

    private MyButton button_back;

    private ImageView imageView;

    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlighten_new);

        //Android6.0及以下多语言切换
        MyApplication.changeAppLanguage(getResources(),MyApplication.languageFlag);


        //适配屏幕引入语句
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());
        Log.e("EN:","22");

        initialView();


        //视差动画 实例化
        parallelViewHelper1 = new ParallelViewHelper(this, findViewById(R.id.background_module),50,0 );
        parallelViewHelper2 = new ParallelViewHelper(this, findViewById(R.id.leaves_module),20,1 );
        parallelViewHelper3 = new ParallelViewHelper(this, findViewById(R.id.butterfly1_module),220,1 );
        parallelViewHelper4 = new ParallelViewHelper(this, findViewById(R.id.butterfly2_module),300,1 );
        parallelViewHelper5 = new ParallelViewHelper(this, findViewById(R.id.butterfly3_module),180,1 );


        //是否第一次打开APP，是则运行，否则不在此运行
        if(MyApplication.isFirstRun("EnlightenActivityNewNewbieGuide"))
        {
            EnlightenActivityNewNewbieGuide();
        }

    }

    private void initialView(){
        linearlayout_horizonalscrollview=findViewById(R.id.linearlayout_horizonalscrollview);
        linearLayout1=(LinearLayout)findViewById(R.id.linearlayout_horizonalscrollview);
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
            imageView.setImageResource(imageid[counter]);
            //imageView.setOnClickListener(this);
            imageView.setBackground(getResources().getDrawable(moduleBackground[counter]));
            linearLayout1.addView(inflateView);

            imageView_note_book=findViewById(R.id.image_knowledge_notebook);
            imageView_note_book.setOnClickListener(this);

            button_back=findViewById(R.id.button_back_enlighten);
            button_back.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        // 获取item的id
        Intent intent;
        switch (v.getId())
        {
            case 0:
                MyApplication.playClickVoice(EnlightenActivityNew.this,"button");
                intent=new Intent(EnlightenActivityNew.this,BaseLevelActivity.class);
                intent.putExtra("model","english");
                startActivity(intent);
                break;
            case 1:
                MyApplication.playClickVoice(EnlightenActivityNew.this,"button");
                intent=new Intent(EnlightenActivityNew.this,BaseLevelActivity.class);
                intent.putExtra("model","animal");
                startActivity(intent);
                break;
            case 2:
                MyApplication.playClickVoice(EnlightenActivityNew.this,"button");
                intent=new Intent(EnlightenActivityNew.this,BaseLevelActivity.class);
                intent.putExtra("model","poetry");
                startActivity(intent);
                break;
            case 3:
                MyApplication.playClickVoice(EnlightenActivityNew.this,"button");
                intent=new Intent(EnlightenActivityNew.this,BaseLevelActivity.class);
                intent.putExtra("model","sanzijing");
                startActivity(intent);
                break;
            case 4:
                MyApplication.playClickVoice(EnlightenActivityNew.this,"button");
                intent=new Intent(EnlightenActivityNew.this,BaseLevelActivity.class);
                intent.putExtra("model","celebrity");
                startActivity(intent);
                break;
            case R.id.image_knowledge_notebook:
                startActivity(new Intent(EnlightenActivityNew.this,EnlightenActivity.class));
                break;
            case R.id.button_back_enlighten:
                onBackPressed();
                break;
            /*case R.id.image_horizonalscrollview:
                MyApplication.playClickVoice(EnlightenActivityNew.this,"setVoice");
                break;*/
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        parallelViewHelper1.stop();
        parallelViewHelper2.stop();
        parallelViewHelper3.stop();
        parallelViewHelper4.stop();
        parallelViewHelper5.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("EN:","11");
        parallelViewHelper1.start();
        parallelViewHelper2.start();
        parallelViewHelper3.start();
        parallelViewHelper4.start();
        parallelViewHelper5.start();
    }



    public void EnlightenActivityNewNewbieGuide(){

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
                                .addHighLight(imageView_note_book)
                                .setLayoutRes(R.layout.enlighten_activity_new_newbie_guide)//引导页布局，点击跳转下一页或者消失引导层的控件id
                                .setEverywhereCancelable(true)//是否点击任意地方跳转下一页或者消失引导层，默认true
//                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
//                                    @Override
//                                    //onLayoutInflated很重要，返回了 引导层的view，一定要利用好这个view，比如为引导层的view设定监听事件
//                                    public void onLayoutInflated(View view) {
//                                        TextView textViewNewbieGuide = view.findViewById(R.id.textview_mainactvitiy_newbie_guide);
//
//
//                                        TranslateAnimation translateAnimation = new TranslateAnimation(
//                                                Animation.RELATIVE_TO_SELF,0,
//                                                Animation.RELATIVE_TO_SELF,0.5f,
//                                                Animation.RELATIVE_TO_SELF,0,
//                                                Animation.RELATIVE_TO_SELF,0
//                                        );
//                                         //imageView.setAnimation(translateAnimation);
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
                                .addHighLight(linearlayout_horizonalscrollview)
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
