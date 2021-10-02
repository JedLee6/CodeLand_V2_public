package com.redant.codeland.scratchgame.scratchgameui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redant.codeland.R;
import com.redant.codeland.app.MyApplication;
import com.redant.codeland.scratchgame.ScratchJrActivity;
import com.redant.codeland.ui.BaseLevelActivity;
import com.redant.codeland.ui.GameNoteBookActivity;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

public class ScratchGameBaseActivity extends AppCompatActivity implements View.OnClickListener {


    private LinearLayout linearLayout1;
    private Button button_back;

    private int[] imageid={R.mipmap.robot_block,R.mipmap.gamepad};
    private int[] moduleBackground={R.drawable.module_fillet_blue,R.drawable.module_fillet_red,R.drawable.module_fillet_yellow,R.drawable.module_fillet_purple,R.drawable.module_fillet_green,R.drawable.module_fillet_pink};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_game);

        //Android6.0多语言切换
        MyApplication.changeAppLanguage(getResources(),MyApplication.languageFlag);


        //适配屏幕引入语句
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());

        //这种getString(R.string.coding_output_text)方法必须放在方法里面，不能放在外面的
        String[] moduleName={getString(R.string.scratch_game_guide_block),getString(R.string.scratch_game_develop_factory)};

        linearLayout1=(LinearLayout)findViewById(R.id.linearlayout_horizonalscrollview_game_base);
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
            TextView textView = (TextView) inflateView.findViewById(R.id.textview_horizonalscrollview);

            textView.setText(moduleName[counter]);
            imageView.setImageResource(imageid[counter]);
            imageView.setBackground(getResources().getDrawable(moduleBackground[counter]));
            linearLayout1.addView(inflateView);

            button_back = findViewById(R.id.button_back_game_base);
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
                MyApplication.playClickVoice(ScratchGameBaseActivity.this,"button");
                intent=new Intent(ScratchGameBaseActivity.this,ScratchGameGuideActivity.class);
                startActivity(intent);
                //以下是打开 积木使用教程的关卡选择界面
//                MyApplication.playClickVoice(ScratchGameBaseActivity.this,"button");
//                intent=new Intent(ScratchGameBaseActivity.this,BaseLevelActivity.class);
//                intent.putExtra("model","scratchGameGuideBlock");
//                startActivity(intent);
                break;
            case 1:
                MyApplication.playClickVoice(ScratchGameBaseActivity.this,"button");
                intent=new Intent(ScratchGameBaseActivity.this,ScratchJrActivity.class);
                startActivity(intent);
                break;
            case R.id.image_game_notebook:
                startActivity(new Intent(ScratchGameBaseActivity.this,GameNoteBookActivity.class));
                break;
            case R.id.button_back_game_base:
                MyApplication.playClickVoice(ScratchGameBaseActivity.this,"button");
                onBackPressed();
            default:
                break;
        }
    }

}
