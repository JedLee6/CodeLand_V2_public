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
import com.yatoooon.screenadaptation.ScreenAdapterTools;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: wjx
 * Date: 2018-11-14
 * Time: 13:10
 */

public class ScratchAnimationBaseActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout linearLayout1;
    private Button button_back;
    private int[] imageid = {
            R.mipmap.animation,
            R.mipmap.drawpad
    };
    private int[] moduleBackground = {R.drawable.module_fillet_blue, R.drawable.module_fillet_red,
            R.drawable.module_fillet_yellow, R.drawable.module_fillet_purple,
            R.drawable.module_fillet_green, R.drawable.module_fillet_pink};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_animation);
        MyApplication.changeAppLanguage(getResources(), MyApplication.languageFlag);//支持多语言切换

        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());//适配屏幕引入语句
        String[] moduleName = {
                getString(R.string.scratch_animation_guide_block),
                getString(R.string.scratch_animation_develop_factory),
        };

        linearLayout1 = (LinearLayout) findViewById(R.id.linearlayout_horizonalscrollview_animation_base);
        for (int counter = 0; counter < moduleName.length; counter++) {
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
            button_back = findViewById(R.id.button_back_scratch_animation);
            button_back.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case 0:
                MyApplication.playClickVoice(ScratchAnimationBaseActivity.this, "button");
                intent = new Intent(ScratchAnimationBaseActivity.this, ScratchAnimationGuideActivity.class);
                startActivity(intent);
                break;

            case 1:
                MyApplication.playClickVoice(ScratchAnimationBaseActivity.this, "button");
                intent = new Intent(ScratchAnimationBaseActivity.this, ScratchJrActivity.class);
                startActivity(intent);
                break;
            case R.id.image_animation_notebook:
                startActivity(new Intent(ScratchAnimationBaseActivity.this, ScratchAnimationGuideActivity.class));
                break;
            case R.id.button_back_scratch_animation:
                MyApplication.playClickVoice(ScratchAnimationBaseActivity.this, "button");
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
