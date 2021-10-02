package com.redant.codeland.scratchgame.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redant.codeland.R;
import com.redant.codeland.app.MyApplication;
import com.redant.codeland.scratchgame.scratchgameui.ProgramActivity;

import static android.content.Context.MODE_PRIVATE;

public class ProgramCodeFragment extends Fragment implements View.OnClickListener {


    ProgramActivity programActivity;

    private LinearLayout linearLayout;
    private boolean isEmpty=true;
    private int[] imageId={R.mipmap.coding_module_js, R.mipmap.coding_module_python, R.mipmap.coding_module_php
            , R.mipmap.coding_module_lua, R.mipmap.coding_module_dart};
    private int[] moduleBackground={ R.drawable.module_fillet_purple, R.drawable.module_fillet_yellow,R.drawable.module_fillet_pink,R.drawable.module_fillet_green,R.drawable.module_fillet_red,R.drawable.module_fillet_blue};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_program_coding,container,false);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        String[] moduleName={getString(R.string.code_javascript),getString(R.string.code_python),getString(R.string.code_php),getString(R.string.code_lua),getString(R.string.code_dart)};
        linearLayout=(LinearLayout)getActivity().findViewById(R.id.linearlayout_horizonalscrollview_animation_base);
        if(isEmpty)
        {
            for(int i=0;i<moduleName.length;i++)
            {
                View inflateView = LayoutInflater.from(getActivity()).inflate(R.layout.hub_linear_item, null);
                inflateView.setId(i);
                inflateView.setOnClickListener(this);
                ImageView imageView = (ImageView) inflateView.findViewById(R.id.image_horizonalscrollview);
                TextView textView = (TextView) inflateView.findViewById(R.id.textview_horizonalscrollview);

                textView.setText(moduleName[i]);
                if(MyApplication.languageFlag== MyApplication.LANGUAGE_ENGLISH) {
                    imageId[0] = R.mipmap.coding_module_printf_usa;
                }

                imageView.setImageResource(imageId[i]);
                imageView.setBackground(getResources().getDrawable(moduleBackground[i]));
                linearLayout.addView(inflateView);
            }
        }
    }

    @Override
    public void onClick(View view) {

        SharedPreferences.Editor editor=getActivity().getSharedPreferences("Module3",MODE_PRIVATE).edit();



        switch (view.getId())
        {
            case 0:
                MyApplication.playClickVoice(getActivity(),"button");
                editor.putInt("clickedChapter",1);
                editor.commit();
                changeParentFragment(new BaseLevelFragment(),"coding_javascript");
                break;
            case 1:
                MyApplication.playClickVoice(getActivity(),"button");
                editor.putInt("clickedChapter",2);
                editor.commit();
                changeParentFragment(new BaseLevelFragment(),"coding_python");
                break;
            case 2:
                MyApplication.playClickVoice(getActivity(),"button");
                editor.putInt("clickedChapter",3);
                editor.commit();
                changeParentFragment(new BaseLevelFragment(),"coding_php");
                break;
            case 3:
                MyApplication.playClickVoice(getActivity(),"button");
                editor.putInt("clickedChapter",4);
                editor.commit();
                changeParentFragment(new BaseLevelFragment(),"coding_lua");
                break;
            case 4:
                MyApplication.playClickVoice(getActivity(),"button");
                editor.putInt("clickedChapter",5);
                editor.commit();
                changeParentFragment(new BaseLevelFragment(),"coding_dart");
                break;
                //不涉及xml学习
//            case 5:
//                MyApplication.playClickVoice(getActivity(),"button");
//                editor.putInt("clickedChapter",6);
//                editor.commit();
//                changeParentFragment(new BaseLevelFragment(),"xml");
//                break;
        }
    }
    public void showFragment(Fragment f1,Fragment f2){
        FragmentTransaction transaction=getFragmentManager().beginTransaction();
        if(!f2.isAdded()){
            transaction.add(R.id.hub_left_bottom,f2)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }else {
            transaction.hide(f1)
                    .show(f2)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }

    private void changeParentFragment(Fragment fragment,String type)
    {
        Bundle bundle=new Bundle();
        bundle.putString("model",type);
        fragment.setArguments(bundle);
        FragmentManager manager=getFragmentManager();
        FragmentTransaction transaction;
        transaction=manager.beginTransaction();
        transaction.replace(R.id.program_left_bottom,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
