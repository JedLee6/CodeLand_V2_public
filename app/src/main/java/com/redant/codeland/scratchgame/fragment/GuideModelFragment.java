package com.redant.codeland.scratchgame.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redant.codeland.R;
import com.redant.codeland.app.MyApplication;

public class GuideModelFragment extends Fragment implements View.OnClickListener {
    private LinearLayout linearLayout1;
    private boolean isEmpty=true;
    private int[] imageid={R.mipmap.robot_block,
            R.mipmap.maze,
            R.mipmap.cat_mouse};
    private int[] moduleBackground={R.drawable.module_fillet_green,
            R.drawable.module_fillet_pink,
            R.drawable.module_fillet_blue,
            R.drawable.module_fillet_red,
            R.drawable.module_fillet_yellow,
            R.drawable.module_fillet_purple};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.model_game_choose,container,false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //这种getString(R.string.coding_output_text)方法必须放在方法里面，不能放在外面的
        String[] moduleName={getString(R.string.scratch_block_guide),
                getString(R.string.scratch_game_guide_maze),
                getString(R.string.scratch_game_cat)};
        linearLayout1=(LinearLayout)getActivity().findViewById(R.id.linearlayout_horizonalscrollview_game_base);
        if(isEmpty){
            for(int counter=0;counter<moduleName.length;counter++) {
                View inflateView = LayoutInflater.from(getActivity()).inflate(R.layout.hub_linear_item, null);
                //适配屏幕引入语句
//            ScreenAdapterTools.getInstance().loadView((ViewGroup) inflateView);
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
            }
            isEmpty=false;
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        LevelFragment levelFragment=new LevelFragment();
        Bundle bundle=new Bundle();
        switch (v.getId())
        {
            case 0:
                MyApplication.playClickVoice(getActivity(),"button");
                bundle.putString("model","scratchGameGuideBlock");
                break;
            case 1:
                MyApplication.playClickVoice(getActivity(),"button");
                bundle.putString("model","scratchGameMaze");
                break;
            case 2:
                MyApplication.playClickVoice(getActivity(),"button");
                bundle.putString("model","scratchGameCat");
                break;
        }
        levelFragment.setArguments(bundle);
        showFragment(this,levelFragment);
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
