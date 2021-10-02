package com.redant.codeland.scratchgame.scratchanimation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.yatoooon.screenadaptation.ScreenAdapterTools;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: wjx
 * Date: 2018-11-14
 * Time: 15:11
 */
public class ScratchAnimationGuideActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout linearLayout1;
    private Button button_back;
    private ImageView imageView_animation_book;

    private int[] imageid={
            R.mipmap.robot_block,
            R.mipmap.makedraw};
    private int[] moduleBackground={R.drawable.module_fillet_green,
            R.drawable.module_fillet_pink,
            R.drawable.module_fillet_blue,
            R.drawable.module_fillet_red,
            R.drawable.module_fillet_yellow,
            R.drawable.module_fillet_purple};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_animation_guide);
        MyApplication.changeAppLanguage(getResources(),MyApplication.languageFlag);
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());
        String[] moduleName={
                getString(R.string.scratch_block_guide),
                getString(R.string.scratch_animation_guide_maze)
        };
        linearLayout1=(LinearLayout)findViewById(R.id.linearlayout_horizonalscrollview_animation_base);
        for(int counter=0;counter<moduleName.length;counter++) {
            View inflateView = LayoutInflater.from(this).inflate(R.layout.linear_item, null);
            ScreenAdapterTools.getInstance().loadView((ViewGroup) inflateView);
            inflateView.setId(counter);
            inflateView.setOnClickListener(this);
            ImageView imageView = (ImageView) inflateView.findViewById(R.id.image_horizonalscrollview);
            TextView textView = (TextView) inflateView.findViewById(R.id.textview_horizonalscrollview);

            textView.setText(moduleName[counter]);
            imageView.setImageResource(imageid[counter]);
            imageView.setBackground(getResources().getDrawable(moduleBackground[counter]));
            linearLayout1.addView(inflateView);

            button_back = findViewById(R.id.button_back_animation_base);
            button_back.setOnClickListener(this);

            imageView_animation_book=findViewById(R.id.image_animation_notebook);
            imageView_animation_book.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        // 获取item的id
        Intent intent;
        switch (v.getId())
        {
            case 0:
                MyApplication.playClickVoice(ScratchAnimationGuideActivity.this,"button");
                intent=new Intent(ScratchAnimationGuideActivity.this,BaseLevelActivity.class);
                intent.putExtra("model","scratchAnimationGuideBlock");
                startActivity(intent);
                break;
            case 1:
                MyApplication.playClickVoice(ScratchAnimationGuideActivity.this,"button");
                intent=new Intent(ScratchAnimationGuideActivity.this,ScratchJrActivity.class);
                startActivity(intent);
                break;
//            case R.id.image_animation_notebook:
//                startActivity(new Intent(ScratchAnimationGuideActivity.this,AnimationNoteBookActivity.class));
//                break;
            case R.id.button_back_animation_base:
                MyApplication.playClickVoice(ScratchAnimationGuideActivity.this,"button");
                onBackPressed();
            default:
                break;
        }
    }
}
