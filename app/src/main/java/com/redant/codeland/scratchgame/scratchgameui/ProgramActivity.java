package com.redant.codeland.scratchgame.scratchgameui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.redant.codeland.R;
import com.redant.codeland.scratchgame.HubActivity;
import com.redant.codeland.scratchgame.ScratchJrActivity;
import com.redant.codeland.scratchgame.fragment.CodeNotebookFragment;
import com.redant.codeland.scratchgame.fragment.NotebookFragment;
import com.redant.codeland.scratchgame.fragment.ProgramCodeFragment;
import com.redant.codeland.scratchgame.fragment.ProgramCodingFragment;
import com.redant.codeland.ui.CodingBaseActivity_test;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

public class ProgramActivity extends FragmentActivity implements View.OnClickListener{

    private Button button_part1;
    private Button button_part2;
    private Button button_back;
    private NotebookFragment fragment;
    private boolean havecodeBook=false;

    //zhl新增
    private FrameLayout layout_book;
    private FrameLayout layout_try;

    private int part;
    private CodeNotebookFragment codeNotebookFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);
        Intent intent=getIntent();
        part=1;
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());
        bondButton();
        choosePart(part);
        setPartColor(part);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.hub_book:
                if(!havecodeBook) {
                    codeNotebookFragment = new CodeNotebookFragment();
                    replaceLeftFragment(codeNotebookFragment);
                    havecodeBook=true;
                }else {
                    onBackPressed();
                    havecodeBook=false;
                }
                break;
            case R.id.hub_try:
                intent=new Intent(ProgramActivity.this,CodingBaseActivity_test.class);
                intent.putExtra("model","try");
                startActivity(intent);

                break;
            //pjh 代码块学习
            case R.id.program_part1:
                if(part!=1) {
                    replaceLeftBottomFragment(new ProgramCodingFragment());
                    part=1;
                    setPartColor(part);
                }
                break;
                //pjh 语言学习
            case R.id.program_part2:
                if(part!=2) {
                    replaceLeftBottomFragment(new ProgramCodeFragment());
                    part=2;
                    setPartColor(part);
                }
                break;
            case R.id.program_back_button:
                onBackPressed();
                break;
        }
    }


    public void replaceLeftFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.program_left,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void replaceLeftBottomFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.program_left_bottom,fragment);
        transaction.commit();
    }

    public void choosePart(int part){
        if (part ==1){
            //replaceLeftBottomFragment(new AnimationModelFragment());
            replaceLeftBottomFragment(new ProgramCodingFragment());
        }
        else if(part == 2){
            //replaceLeftBottomFragment(new GameModelFragment());
            replaceLeftBottomFragment(new ProgramCodeFragment());
        }
    }
    public void setPartColor(int part){
        if (part ==2){
            button_part2.setBackgroundResource(R.color.hubPartTwoOn);
            button_part1.setBackgroundResource(R.color.hubPartOneOff);
            button_part2.setTextColor(Color.BLACK);
            button_part1.setTextColor(Color.WHITE);
        }
        else if(part == 1){
            button_part2.setBackgroundResource(R.color.hubPartTwoOff);
            button_part1.setBackgroundResource(R.color.hubPartOneOn);
            button_part1.setTextColor(Color.BLACK);
            button_part2.setTextColor(Color.WHITE);
        }
    }

    public void bondButton(){
        button_part1=(Button)findViewById(R.id.program_part1);
        button_part2=(Button)findViewById(R.id.program_part2);
        button_back=(Button)findViewById(R.id.program_back_button);


        button_part1.setOnClickListener(this);
        button_part2.setOnClickListener(this);
        button_back.setOnClickListener(this);

        layout_book=(FrameLayout)findViewById(R.id.hub_book);
        layout_book.setOnClickListener(this);
        layout_try=(FrameLayout)findViewById(R.id.hub_try);
        layout_try.setOnClickListener(this);
    }
}
