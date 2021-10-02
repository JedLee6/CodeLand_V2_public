package com.redant.codeland.scratchgame.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.redant.codeland.R;
import com.redant.codeland.entity.LevelInfo;
import com.redant.codeland.scratchgame.ScratchJrActivity;
import com.redant.codeland.ui.CodingBaseActivity_test;
import com.redant.codeland.ui.CodingLearningActivity;
import com.redant.codeland.ui.EnglishBlocklyActivity;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class BaseLevelFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayList<Integer> pictureIdArray;
    private ArrayList<String> levelNameArray;
    private ArrayList<Integer> ratingNumArray;

    private GridView gridView;
    private List<Map<String,Object>> dataList;
    private SimpleAdapter simpleAdapter;

    private
    int clickedLevel;//表示当前模块选中关卡，通过点击赋值
    private int unlockLevel;//表示当前模块解锁数值，从sh中的获得，用于保存
    private int maxLevel=0;//表示当前模块最大数值，文件读取累加获得，用于保存
    private String model;//表示当前是什么模块，从上个活动intent获得，还传到下个活动
    private String modelUnlockLevel;//用于数据库键，表示当前模块解锁的最大关卡（第一模块0-1星不算解锁的最大关卡，2-3星算；第二模块0星不算，1-3星算）
    private String modelMaxLevel;//用于数据库键，表示当前模块总共关卡数
    private String modelUrl;//当前模块对应网站
    private String modelRating;//database
    private Class nextClass;

    private ImageView imageView_background;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_base_level,container,false);
       // ScreenAdapterTools.getInstance().loadView((ViewGroup) getActivity().getWindow().getDecorView());
       //在Fragment中调用会导致适配出现问题
        Bundle bundle=getArguments();
        model=bundle.getString("model");
        Log.e("BaseLevelFragment",model);


        imageView_background=view.findViewById(R.id.background_chapter);


        Log.e("BaseLevelFragment","X");
        return view;
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        clickedLevel=position+1;

        if(clickedLevel<=unlockLevel)
        {
            SharedPreferences.Editor editor=getActivity().getSharedPreferences("AllLevel",MODE_PRIVATE).edit();
            editor.putInt("clickedLevel",clickedLevel);
            editor.putInt(modelUnlockLevel,unlockLevel);
            editor.commit();
//            Intent intent=new Intent(BaseLevelActivity.this,KnowledgeLearningActivity.class);
//            intent.putExtra("chapterModel",model);
            Intent intent=new Intent(getActivity(),nextClass);
            //在 Scratch游戏 中的 开发教程 中创建的项目都是一次性的（即不会保存到项目管理区，退出，项目就会消失）
            intent.putExtra("scratchGameSingleUse","yes");
            //彭佳汉添加，用于加载相应模块的教程
            intent.putExtra("model",model);
            startActivity(intent);
        }
        else {
            //release发行版本应该使用的代码-开头
            Toast.makeText(getActivity(),getString(R.string.over_level_tip)+unlockLevel+getString(R.string.over_level_tip_end),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshEnglishChapterActivity();
    }

    private List<Map<String,Object>> getData(){
        for(int counter=0;counter<pictureIdArray.size();counter++){
            Map<String,Object> map=new HashMap<>();
            map.put("Image",pictureIdArray.get(counter));
            map.put("Text",levelNameArray.get(counter));
            if(ratingNumArray.get(counter)==0){
                map.put("FirstRate", R.mipmap.star_black);
                map.put("SecondRate", R.mipmap.star_black);
                map.put("ThirdRate", R.mipmap.star_black);
            }else if(ratingNumArray.get(counter)==1){
                map.put("FirstRate", R.mipmap.star_light);
                map.put("SecondRate", R.mipmap.star_black);
                map.put("ThirdRate", R.mipmap.star_black);
            }else if(ratingNumArray.get(counter)==2){
                map.put("FirstRate", R.mipmap.star_light);
                map.put("SecondRate", R.mipmap.star_light);
                map.put("ThirdRate", R.mipmap.star_black);
            }else if(ratingNumArray.get(counter)==3){
                map.put("FirstRate", R.mipmap.star_light);
                map.put("SecondRate", R.mipmap.star_light);
                map.put("ThirdRate", R.mipmap.star_light);
            }else if(ratingNumArray.get(counter)==-1){
                map.put("FirstRate", R.mipmap.star_nothing);
                map.put("SecondRate", R.mipmap.star_nothing);
                map.put("ThirdRate", R.mipmap.star_nothing);
            }
            dataList.add(map);
        }
        return dataList;
    }

    private void initModel(String model){
        if(model.equals("english")){
            modelUrl= "english/english_word_list.txt";
            modelUnlockLevel="englishUnlockLevel";
            modelMaxLevel="englishMaxLevel";
            modelRating="english";
            nextClass  = EnglishBlocklyActivity.class;
        }
        else if(model.equals("scratchGameGuideBlock")){
            //用户点击的是第二模块“Scratch游戏”的“积木块教程”，因此关卡背景应该切换为 第二模块的背景
            imageView_background.setBackgroundResource(R.mipmap.background_module2);
            modelUrl= "level_txt/scratch_game_guide_block.txt";
            modelUnlockLevel="scratchGameGuideBlockUnlockLevel";
            modelMaxLevel="scratchGameGuideBlockMaxLevel";
            modelRating="scratchGameGuideBlock";
            nextClass=ScratchJrActivity.class;
        }
        else if(model.equals("scratchGameGuideBlock")){
            //用户点击的是第二模块“Scratch游戏”的“积木块教程”，因此关卡背景应该切换为 第二模块的背景
            imageView_background.setBackgroundResource(R.mipmap.background_module2);
            modelUrl= "level_txt/scratch_game_guide_block.txt";
            modelUnlockLevel="scratchGameGuideBlockUnlockLevel";
            modelMaxLevel="scratchGameGuideBlockMaxLevel";
            modelRating="scratchGameGuideBlock";
            nextClass=ScratchJrActivity.class;
        }
        else if(model.equals("scratchGameMaze")){
            //用户点击的是第二模块“Scratch游戏”的“积木块教程”，因此关卡背景应该切换为 第二模块的背景
            imageView_background.setBackgroundResource(R.mipmap.background_module2);
            modelUrl= "level_txt/scratch_game_maze.txt";
            modelUnlockLevel="scratchGameMazeUnlockLevel";
            modelMaxLevel="scratchGameMazeMaxLevel";
            modelRating="scratchGameMaze";
            nextClass=ScratchJrActivity.class;
        }
        else if(model.equals("scratchGameCat")){
            //用户点击的是第二模块“Scratch游戏”的“积木块教程”，因此关卡背景应该切换为 第二模块的背景
            imageView_background.setBackgroundResource(R.mipmap.background_module2);
            modelUrl= "level_txt/scratch_game_cat.txt";
            modelUnlockLevel="scratchGameCatUnlockLevel";
            modelMaxLevel="scratchGameCatMaxLevel";
            modelRating="scratchGameCat";
            nextClass=ScratchJrActivity.class;
        }
        else if(model.equals("scratchAnimationGuideBlock")){
            //用户点击的是第一模块“Scratch绘画”的“积木块教程”，因此关卡背景应该切换为 第一模块的背景
            imageView_background.setBackgroundResource(R.mipmap.background_module1);
            modelUrl= "level_txt/scratch_animation_guide_block.txt";
            modelUnlockLevel="scratchAnimationGuideBlockUnlockLevel";
            modelMaxLevel="scratchAnimationGuideBlockMaxLevel";
            modelRating="scratchAnimationGuideBlock";
            nextClass=ScratchJrActivity.class;
        }
        //传入模块为第三模块的代码输出
        else if(model.equals("coding_printf")){
            imageView_background.setBackgroundResource(R.mipmap.background_module1);
            modelUrl= "level_txt/coding_printf.txt";
            modelUnlockLevel="coding_printf_UnlockLevel";
            modelMaxLevel="coding_printf_MaxLevel";
            modelRating="coding_printf";
            nextClass= CodingBaseActivity_test.class;
        }
        else if(model.equals("coding_math")){
            imageView_background.setBackgroundResource(R.mipmap.background_module1);
            modelUrl= "level_txt/coding_math.txt";
            modelUnlockLevel="coding_math_UnlockLevel";
            modelMaxLevel="coding_math_MaxLevel";
            modelRating="coding_math";
            nextClass= CodingBaseActivity_test.class;
        }
        else if(model.equals("coding_variable")){
            imageView_background.setBackgroundResource(R.mipmap.background_module1);
            modelUrl= "level_txt/coding_variable.txt";
            modelUnlockLevel="coding_variable_UnlockLevel";
            modelMaxLevel="coding_variable_MaxLevel";
            modelRating="coding_variable";
            nextClass= CodingBaseActivity_test.class;
        }
        else if(model.equals("coding_logic")){
            imageView_background.setBackgroundResource(R.mipmap.background_module1);
            modelUrl= "level_txt/coding_logic.txt";
            modelUnlockLevel="coding_logic_UnlockLevel";
            modelMaxLevel="coding_logic_MaxLevel";
            modelRating="coding_logic";
            nextClass= CodingBaseActivity_test.class;
        }
        else if(model.equals("coding_loop")){
            imageView_background.setBackgroundResource(R.mipmap.background_module1);
            modelUrl= "level_txt/coding_loop.txt";
            modelUnlockLevel="coding_loop_UnlockLevel";
            modelMaxLevel="coding_loop_MaxLevel";
            modelRating="coding_loop";
            nextClass= CodingBaseActivity_test.class;
        }
        else if(model.equals("coding_array")){
            imageView_background.setBackgroundResource(R.mipmap.background_module1);
            modelUrl= "level_txt/coding_array.txt";
            modelUnlockLevel="coding_array_UnlockLevel";
            modelMaxLevel="coding_array_MaxLevel";
            modelRating="coding_array";
            nextClass= CodingBaseActivity_test.class;
        }
        else if(model.equals("coding_function")){
            imageView_background.setBackgroundResource(R.mipmap.background_module1);
            modelUrl= "level_txt/coding_function.txt";
            modelUnlockLevel="coding_function_UnlockLevel";
            modelMaxLevel="coding_function_MaxLevel";
            modelRating="coding_function";
            nextClass= CodingBaseActivity_test.class;
        }
        //传入模块为第三模块的代码输出
        else if(model.equals("coding_javascript")){
            imageView_background.setBackgroundResource(R.mipmap.background_module1);
            modelUrl= "level_txt/coding_javascript.txt";
            modelUnlockLevel="coding_javascript_UnlockLevel";
            modelMaxLevel="coding_javascript_MaxLevel";
            modelRating="coding_javascript";
            nextClass=CodingBaseActivity_test.class;
        }
        else if(model.equals("coding_python")){
            imageView_background.setBackgroundResource(R.mipmap.background_module1);
            modelUrl= "level_txt/coding_python.txt";
            modelUnlockLevel="coding_python_UnlockLevel";
            modelMaxLevel="coding_python_MaxLevel";
            modelRating="coding_python";
            nextClass=CodingBaseActivity_test.class;
        }
        else if(model.equals("coding_php")){
            imageView_background.setBackgroundResource(R.mipmap.background_module1);
            modelUrl= "level_txt/coding_php.txt";
            modelUnlockLevel="coding_php_UnlockLevel";
            modelMaxLevel="coding_php_MaxLevel";
            modelRating="coding_php";
            nextClass=CodingBaseActivity_test.class;
        }
        else if(model.equals("coding_lua")){
            imageView_background.setBackgroundResource(R.mipmap.background_module1);
            modelUrl= "level_txt/coding_lua.txt";
            modelUnlockLevel="coding_lua_UnlockLevel";
            modelMaxLevel="coding_lua_MaxLevel";
            modelRating="coding_lua";
            nextClass=CodingBaseActivity_test.class;
        }
        else if(model.equals("coding_dart")){
            imageView_background.setBackgroundResource(R.mipmap.background_module1);
            modelUrl= "level_txt/coding_dart.txt";
            modelUnlockLevel="coding_dart_UnlockLevel";
            modelMaxLevel="coding_dart_MaxLevel";
            modelRating="coding_dart";
            nextClass=CodingBaseActivity_test.class;
        }
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("AllLevel",MODE_PRIVATE);
        unlockLevel=sharedPreferences.getInt(modelUnlockLevel,1);
    }

    private void refreshEnglishChapterActivity(){
        initModel(model);

        //通过 english_knowledge_url.txt 的行数动态获取 用户所选中章节 的最大关卡数
        try{
            InputStream is = getActivity().getAssets().open(modelUrl);
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String line="";
            if(maxLevel==0){
                while ((line=br.readLine())!=null) {
                    maxLevel++;
                }
                SharedPreferences.Editor editor=getActivity().getSharedPreferences("AllLevel",MODE_PRIVATE).edit();
                editor.putInt(modelMaxLevel,maxLevel);
                editor.commit();
            }
            br.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }

        pictureIdArray=new ArrayList<Integer>();
        levelNameArray=new ArrayList<String>();
        ratingNumArray=new ArrayList<Integer>();
        List<LevelInfo> ratings= DataSupport.where("model = ?",modelRating).find(LevelInfo.class);

        //给动态数组赋值
        for(int i=0;i<maxLevel;i++){
            if(i<unlockLevel)
            {
                pictureIdArray.add(R.mipmap.unlock_button_pic);
                levelNameArray.add((i+1)+"");
                if(!ratings.isEmpty() && ratings.size()>i){
                    ratingNumArray.add(ratings.get(i).getRating());
                }else{
                    ratingNumArray.add(0);
                }
            }
            else{
                pictureIdArray.add(R.mipmap.lock_button_pic);
                levelNameArray.add("");
                ratingNumArray.add(-1);
            }
        }


        gridView = (GridView) getActivity().findViewById(R.id.gridView_bae_level);
        dataList = new ArrayList<>();
        String[] key=new String[]{"Image","Text","FirstRate","SecondRate","ThirdRate"};
        int[] value=new int[]{R.id.button_gridview_item, R.id.textview_gridview_item, R.id.item_first_image, R.id.item_second_image, R.id.item_third_image};
        //适配屏幕引入语句，重写了getView
        simpleAdapter = new SimpleAdapter(getActivity(), getData(), R.layout.gridview_item,key,value)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if(convertView==null){
                    View view=super.getView(position, convertView, parent);
                    //适配屏幕引入语句，重写了getView
                    ScreenAdapterTools.getInstance().loadView((ViewGroup) view);
                    return view;
                }
                return super.getView(position, convertView, parent);
            }
        }
        ;

        gridView.setAdapter(simpleAdapter);
        gridView.setOnItemClickListener(this);

    }
}
