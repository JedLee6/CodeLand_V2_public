
package com.redant.codeland.scratchgame;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.redant.codeland.R;
import com.redant.codeland.scratchgame.fragment.AnimationModelFragment;
import com.redant.codeland.scratchgame.fragment.GameModelFragment;
import com.redant.codeland.scratchgame.fragment.LevelFragment;
import com.redant.codeland.scratchgame.fragment.ModelFragment;
import com.redant.codeland.scratchgame.fragment.NotebookFragment;
import com.redant.codeland.scratchgame.fragment.TestFragment;
import com.redant.codeland.scratchgame.fragment.TestTwoFragment;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

public class HubActivity extends FragmentActivity implements View.OnClickListener {

    private FrameLayout layout_book;
    private FrameLayout layout_try;
    private Button button_part1;
    private Button button_part2;
    private Button button_part3;
    private Button button_back;
    private NotebookFragment fragment;

    private int part;
    private boolean haveBook=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);
        Intent intent=getIntent();
        part=intent.getIntExtra("part",1);

        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());


        bondButton();
        choosePart(part);
        setPartColor(part);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        haveBook=false;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.hub_try:
                intent=new Intent(HubActivity.this,ScratchJrActivity.class);
                startActivity(intent);
                break;
            case R.id.hub_book:
                if( !haveBook ){
                    fragment=new NotebookFragment();
                    replaceLeftFragment(fragment);
                    haveBook=true;
                }else{
                    onBackPressed();
                    haveBook=false;
                }
                break;
            case R.id.hub_part1:
                if(part!=1) {
                    replaceLeftBottomFragment(new AnimationModelFragment());
                    part=1;
                    setPartColor(part);
                }
                break;
            case R.id.hub_part2:
                if(part!=2) {
                    replaceLeftBottomFragment(new GameModelFragment());
                    part=2;
                    setPartColor(part);
                }
                break;
            case R.id.hub_part3:
                if(part!=3){
                    LevelFragment levelFragment=new LevelFragment();
                    Bundle bundle=new Bundle();
                    bundle.putString("model","scratchGuideBlock");
                    levelFragment.setArguments(bundle);
                    replaceLeftBottomFragment(levelFragment);
                    part=3;
                    setPartColor(part);
                }
                break;
            case R.id.hub_back_button:
                onBackPressed();
                break;
        }
    }

    public void replaceLeftFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.hub_left,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void replaceLeftBottomFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.hub_left_bottom,fragment);
        transaction.commit();
    }

    public void choosePart(int part){
        if (part ==1){
            replaceLeftBottomFragment(new AnimationModelFragment());
        }
        else if(part == 2){
            replaceLeftBottomFragment(new GameModelFragment());
        }
        else if(part == 3){
//            replaceLeftBottomFragment(new GuideModelFragment());
        }
    }
    public void setPartColor(int part){
        if (part ==2){
            button_part2.setBackgroundResource(R.color.hubPartTwoOn);
            button_part1.setBackgroundResource(R.color.hubPartOneOff);
            button_part3.setBackgroundResource(R.color.hubPartThreeOff);
            button_part3.setTextColor(Color.WHITE);
            button_part2.setTextColor(Color.BLACK);
            button_part1.setTextColor(Color.WHITE);
        }
        else if(part == 1){
            button_part2.setBackgroundResource(R.color.hubPartTwoOff);
            button_part1.setBackgroundResource(R.color.hubPartOneOn);
            button_part3.setBackgroundResource(R.color.hubPartThreeOff);
            button_part3.setTextColor(Color.WHITE);
            button_part1.setTextColor(Color.BLACK);
            button_part2.setTextColor(Color.WHITE);
        }else if(part==3 ){
            button_part2.setBackgroundResource(R.color.hubPartTwoOff);
            button_part1.setBackgroundResource(R.color.hubPartOneOff);
            button_part3.setBackgroundResource(R.color.hubPartThreeOn);
            button_part1.setTextColor(Color.WHITE);
            button_part2.setTextColor(Color.WHITE);
            button_part3.setTextColor(Color.BLACK);
        }
    }

    public void bondButton(){
        button_part1=(Button)findViewById(R.id.hub_part1);
        button_part2=(Button)findViewById(R.id.hub_part2);
        button_part3=(Button)findViewById(R.id.hub_part3);
        button_back=(Button)findViewById(R.id.hub_back_button);
        layout_book=(FrameLayout) findViewById(R.id.hub_book);
        layout_try=(FrameLayout)findViewById(R.id.hub_try);

        button_part1.setOnClickListener(this);
        button_part2.setOnClickListener(this);
        button_part3.setOnClickListener(this);
        button_back.setOnClickListener(this);
        layout_book.setOnClickListener(this);
        layout_try.setOnClickListener(this);
    }

}
