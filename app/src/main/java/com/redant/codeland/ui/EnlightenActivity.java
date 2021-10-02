package com.redant.codeland.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.redant.codeland.R;

import com.redant.codeland.app.MyApplication;
import com.redant.codeland.entity.Baike;
import com.redant.codeland.ui.fragment.BaikeFragment;
import com.redant.codeland.adapter.BaikeListAdapter;
import com.redant.codeland.util.CustomExpandListview;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-02-09.
 */

public class EnlightenActivity extends AppCompatActivity {
    private CustomExpandListview mListview;
    private BaikeListAdapter mAdapter;
    private List<List<Baike>> mDataList;
    private List<String> mGroupTitleList;
    private BaikeFragment mFragment;//界面右侧的Fragment，用于使用webview展示百科知识
    private View v=null;
    private View formalProgramItem=null;
    int expandFlag=-1;//用于控制列表的展开
    private Button button_back_enlighten_list;

    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlighten);

        //适配屏幕引入语句
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());

        initData();
        initView();

        button_back_enlighten_list=findViewById(R.id.button_back_enlighten_list);
        button_back_enlighten_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.playClickVoice(EnlightenActivity.this,"button");
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化百科知识的各个数据源。
     */
    private void initData() {
        mDataList = new ArrayList<>();
        mGroupTitleList = new ArrayList<>();
        //从资源文件中加载所有的百科分类的名称
        String[] titles = getResources().getStringArray(R.array.baike_group_title);
        for (String t : titles) {
            mGroupTitleList.add(t);
        }
        //测试的假数据
        List<Baike> datas = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Baike b = new Baike();
            b.setTitle("知识" + i);
            b.setUrl("haha");
            datas.add(b);
        }
//        List<Baike> poetryList = loadMaterialFromFile("poetry/poetry_title_url.txt");
//        List<Baike> animalList = loadMaterialFromFile("animal/animal_title_url.txt");
//        List<Baike> sanzijingList = loadMaterialFromFile("sanzijing/sanzijing_title_url.txt");
//        List<Baike> celebrityList = loadMaterialFromFile("celebrity/celebrity_title_url.txt");
        List<Baike> mazeList = loadMaterialFromFile("maze/maze_title_url.txt");
//        List<Baike> englishList = loadMaterialFromFile("english/english_knowledge_url.txt");
        List<Baike> scratchList = loadMaterialFromFile("scratch/scratch_title_url.txt");

        //加上最后一个挑战item

        //加载英语数据源
//        mDataList.add(englishList);
        mDataList.add(scratchList);
        //加载动物数据源
//        mDataList.add(animalList);
        //加载诗歌数据源
//        mDataList.add(poetryList);
        //加载三字经数据源
//        mDataList.add(sanzijingList);
        //加载名人事迹的数据
//        mDataList.add(celebrityList);
        mDataList.add(mazeList);

    }


    /**
     * 根据资源文件路径加载各类百科数据源
     * @return
     */
    private List<Baike> loadMaterialFromFile(String fileName) {
        List<Baike> poetryList = new ArrayList<>();
        try {
            //从文件中读取标题以及对应的网址。
            InputStream is = getAssets().open(fileName);
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = br.readLine()) != null) {
                String [] arrs = line.split(">");
                Baike b = new Baike();
                b.setTitle(arrs[0]);
                b.setUrl(arrs[1]);
                poetryList.add(b);
            }
            br.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return poetryList;
    }

    private void initView() {
        mListview = (CustomExpandListview)findViewById(R.id.enlighten_listview);
        mListview.setGroupIndicator(null);
        mAdapter = new BaikeListAdapter(this, mDataList, mGroupTitleList,mListview);
        mListview.setAdapter(mAdapter);
        mListview.setHeaderView(getLayoutInflater().inflate(
                R.layout.indictor_layout,mListview,false));
        //加载碎片
        mFragment = new BaikeFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.enlighten_container, mFragment);
        transaction.commit();
        //只展开一个group
        mListview.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for(int i = 0; i < mAdapter.getGroupCount(); i++){
                    if(groupPosition != i){
                        mListview.collapseGroup(i);
                    }
                }
               //Toast.makeText(EnlightenActivity.this,mAdapter.getGroupCount()+"kk ",Toast.LENGTH_LONG).show();
            }
        });

        mListview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if(expandFlag == -1){
                    //这会导致重复出现问题，故用collapseGroup
                    //mListview.expandGroup(groupPosition);
                    mListview.setSelectedGroup(groupPosition);
                    expandFlag = groupPosition;
                    mListview.collapseGroup(expandFlag);
                    //mListview.setCurrentTitle(mGroupTitleList.get(groupPosition));
                }else if(expandFlag == groupPosition){
                    mListview.collapseGroup(expandFlag);
                    expandFlag = -1;
                }else {
                    mListview.collapseGroup(groupPosition);
                   // mListview.expandGroup(groupPosition);
                    mListview.setSelectedGroup(groupPosition);
                    expandFlag = groupPosition;
                }
                return false;
            }
        });

        mListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            View v;
            @Override
            public boolean onChildClick(ExpandableListView expandableListView,View view, int i, int i1, long l) {

                Baike b = mDataList.get(i).get(i1);
//                if(!(v==null)){
//                    v.setBackgroundColor(Color.parseColor("#fafafa"));
//                }
//                RelativeLayout tt=(RelativeLayout)view;
//                if (Build.VERSION.SDK_INT >= 23) {
//                    Log.d("aalike","he");
//                    if (isViewCovered(tt)) {
//                        tt.setBackgroundColor(Color.parseColor("#aaaaaa"));
//                        Log.d("alike",i+" "+i1+" "+l+" "+view);
//                        v=view;
//                    } else {
//                        //
//                    }
//                }
//                if(tt.getVisibility()==View.VISIBLE){
//                    tt.setBackgroundColor(Color.parseColor("#aaaaaa"));
//                    Log.d("alike",i+" "+i1+" "+l+" "+view);
//                    v=view;
//
//                }
                mListview.setItemChecked(i1,true);
                mFragment.refresh(b.getUrl());
                return false;
            }
        });
    }


    public boolean isViewCovered(final View view) {
        View currentView = view;

        Rect currentViewRect = new Rect();
        boolean partVisible = currentView.getGlobalVisibleRect(currentViewRect);
        boolean totalHeightVisible = (currentViewRect.bottom - currentViewRect.top) >= view.getMeasuredHeight();
        boolean totalWidthVisible = (currentViewRect.right - currentViewRect.left) >= view.getMeasuredWidth();
        boolean totalViewVisible = partVisible && totalHeightVisible && totalWidthVisible;
        if (!totalViewVisible)//if any part of the view is clipped by any of its parents,return true
            return true;

        while (currentView.getParent() instanceof ViewGroup) {
            ViewGroup currentParent = (ViewGroup) currentView.getParent();
            if (currentParent.getVisibility() != View.VISIBLE)//if the parent of view is not visible,return true
                return true;

            int start = indexOfViewInParent(currentView, currentParent);
            for (int i = start + 1; i < currentParent.getChildCount(); i++) {
                Rect viewRect = new Rect();
                view.getGlobalVisibleRect(viewRect);
                View otherView = currentParent.getChildAt(i);
                Rect otherViewRect = new Rect();
                otherView.getGlobalVisibleRect(otherViewRect);
                if (Rect.intersects(viewRect, otherViewRect))//if view intersects its older brother(covered),return true
                    return true;
            }
            currentView = currentParent;
        }
        return false;
    }


    private int indexOfViewInParent(View view, ViewGroup parent) {
        int index;
        for (index = 0; index < parent.getChildCount(); index++) {
            if (parent.getChildAt(index) == view)
                break;
        }
        return index;
    }
}


