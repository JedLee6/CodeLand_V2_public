package com.redant.codeland.scratchgame.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redant.codeland.R;
import com.redant.codeland.app.MyApplication;
import com.redant.codeland.scratchgame.ScratchJrActivity;

public class AnimationModelFragment extends Fragment implements View.OnClickListener {

    private LinearLayout linearLayout1;
    private boolean isEmpty=true;
    private int[] imageid={
            R.mipmap.cat_and_mouse,
            R.mipmap.boy
            };
    private int[] moduleBackground = {
            R.drawable.module_fillet_blue,
            R.drawable.module_fillet_red,
            R.drawable.module_fillet_yellow,
            R.drawable.module_fillet_purple,
            R.drawable.module_fillet_green,
            R.drawable.module_fillet_pink};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.model_animation_choose,container,false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        String[] moduleName={
                getString(R.string.scratch_animation_catAndMouse),
                getString(R.string.scratch_animation_boy)
        };
        linearLayout1=(LinearLayout)getActivity().findViewById(R.id.linearlayout_horizonalscrollview_animation_base);
        if(isEmpty){
            for(int counter=0;counter<moduleName.length;counter++) {
                View inflateView = LayoutInflater.from(getActivity()).inflate(R.layout.hub_linear_item, null);
                inflateView.setId(counter);
                inflateView.setOnClickListener(this);
                ImageView imageView = (ImageView) inflateView.findViewById(R.id.image_horizonalscrollview);
                TextView textView = (TextView) inflateView.findViewById(R.id.textview_horizonalscrollview);
                textView.setText(moduleName[counter]);
                imageView.setImageResource(imageid[counter]);
                imageView.setBackground(getResources().getDrawable(moduleBackground[counter]));
                linearLayout1.addView(inflateView);
            }
            isEmpty=false;
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(getActivity(),ScratchJrActivity.class);
        intent.putExtra("scratchGameSingleUse","yes2");
        switch (v.getId()) {
            case 0:
                MyApplication.playClickVoice(getActivity(), "button");
                intent.putExtra("model","cat_and_mouse");
                break;
            case 1:
                MyApplication.playClickVoice(getActivity(), "button");
                intent.putExtra("model","boy");
                break;
        }
        startActivity(intent);
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
}
