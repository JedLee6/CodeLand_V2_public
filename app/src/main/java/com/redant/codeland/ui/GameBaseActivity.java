package com.redant.codeland.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redant.codeland.util.AppLanguageUtils;
import com.redant.codeland.ParallelViewHelper;
import com.redant.codeland.R;
import com.redant.codeland.app.MyApplication;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

public class GameBaseActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout linearLayout1;
    private Button button_back;

//    private int[] imageid={R.mipmap.tadpole,R.mipmap.draw,R.mipmap.pac_man,R.mipmap.box,R.mipmap.treasure,R.mipmap.math_module};
    private int[] imageid={R.mipmap.car};
//    private int[] moduleBackground={R.drawable.module_fillet_blue,R.drawable.module_fillet_red,R.drawable.module_fillet_yellow,R.drawable.module_fillet_purple,R.drawable.module_fillet_green,R.drawable.module_fillet_pink};
private int[] moduleBackground={R.drawable.module_fillet_yellow};


    private ParallelViewHelper parallelViewHelper1,parallelViewHelper2,parallelViewHelper3,parallelViewHelper4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_base);

        //Android6.0多语言切换
        MyApplication.changeAppLanguage(getResources(),MyApplication.languageFlag);


        //适配屏幕引入语句
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());

        //这种getString(R.string.coding_output_text)方法必须放在方法里面，不能放在外面的
//        String[] moduleName={getString(R.string.gamebase_tadpole),getString(R.string.gamebase_turtle),getString(R.string.gamebase_pacman),getString(R.string.gamebase_box),getString(R.string.dig_treasure),getString(R.string.gamebase_more_games)};
        String[] moduleName={getString(R.string.gamebase_turtle)};

        linearLayout1=(LinearLayout)findViewById(R.id.linearlayout_horizonalscrollview_game_base_1);
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
            imageView.setBackground(getResources().getDrawable(moduleBackground[counter]));
            linearLayout1.addView(inflateView);

            button_back=findViewById(R.id.button_back_game_base);
            button_back.setOnClickListener(this);

            //视差动画 实例化
            parallelViewHelper1 = new ParallelViewHelper(this, findViewById(R.id.background_module2),50,0 );
            parallelViewHelper2 = new ParallelViewHelper(this, findViewById(R.id.octopus_module2),60,1 );
            parallelViewHelper3 = new ParallelViewHelper(this, findViewById(R.id.jellyfish_module2),120,1 );
            parallelViewHelper4 = new ParallelViewHelper(this, findViewById(R.id.dolphin_module2),180,1 );

        }

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase));
    }

    @Override
    public void onClick(View v) {
        // 获取item的id
        Intent intent;
        switch (v.getId())
        {
//            case 0:
//                MyApplication.playClickVoice(GameBaseActivity.this,"button");
//                intent=new Intent(GameBaseActivity.this,BaseLevelActivity.class);
//                intent.putExtra("model","tadpole");
//                startActivity(intent);
//                break;
//            case 1:
//                MyApplication.playClickVoice(GameBaseActivity.this,"button");
//                intent=new Intent(GameBaseActivity.this,BaseLevelActivity.class);
//                intent.putExtra("model","turtle");
//                startActivity(intent);
//                break;
            //小车是从这里进去的
            case 0:
                MyApplication.playClickVoice(GameBaseActivity.this,"button");
                intent=new Intent(GameBaseActivity.this,BaseLevelActivity.class);
                intent.putExtra("model","turtle");
                startActivity(intent);
                break;
            case 2:
                intent=new Intent(GameBaseActivity.this,BaseLevelActivity.class);
                intent.putExtra("model","pacman");
                startActivity(intent);
                break;
                //Box游戏中有bug，正式发布时屏蔽此游戏
            case 3:
                intent=new Intent(GameBaseActivity.this,BaseLevelActivity.class);
                intent.putExtra("model","box");
                startActivity(intent);
                break;
            case 4:
                intent=new Intent(GameBaseActivity.this,BaseLevelActivity.class);
                intent.putExtra("model","treasure");
                startActivity(intent);
                break;
            case 5:
                intent=new Intent(GameBaseActivity.this,BlocklyGameActivity.class);
                intent.putExtra("model","moreGame");
                startActivity(intent);
                break;

            case R.id.button_back_game_base:
                MyApplication.playClickVoice(GameBaseActivity.this,"button");
                onBackPressed();
            default:
                break;
        }
    }

    @Override
    protected void onPause() {



        parallelViewHelper1.stop();
        parallelViewHelper2.stop();
        parallelViewHelper3.stop();
        parallelViewHelper4.stop();

        super.onPause();
    }

    //首次创建该活动+onPause该活动后再次onRestart该活动，都开始播放音乐
    @Override
    protected void onStart() {
        
//        mediaPlayer=MediaPlayer.create(this, R.raw.bgm_module2);
//        mediaPlayer.start();
//        mediaPlayer.setLooping(true);
//
//        mediaPlayer2=mediaPlayer2.create(this, R.raw.bgm_module2_sea_wave);
//        mediaPlayer2.start();
//        mediaPlayer2.setLooping(true);

        parallelViewHelper1.start();
        parallelViewHelper2.start();
        parallelViewHelper3.start();
        parallelViewHelper4.start();

        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
