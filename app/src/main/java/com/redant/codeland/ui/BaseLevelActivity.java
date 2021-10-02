package com.redant.codeland.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.redant.codeland.MyButton;
import com.redant.codeland.ParallelViewHelper;
import com.redant.codeland.R;
import com.redant.codeland.entity.LevelInfo;
import com.redant.codeland.scratchgame.ScratchJrActivity;
import com.redant.codeland.util.AppLanguageUtils;
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


public class BaseLevelActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

//    private int englishChapterNumber=0;
    //动态int型数组,List<int>用不了
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

    private ParallelViewHelper parallelViewHelper1;
    private ParallelViewHelper parallelViewHelper2;
    private ParallelViewHelper parallelViewHelper3;
    private ParallelViewHelper parallelViewHelper4;
    private ParallelViewHelper parallelViewHelper5;

    private MyButton button_back_level;
    private ImageView imageView_background;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_level);

        //适配屏幕引入语句
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());


        Intent intent=getIntent();
        model=intent.getStringExtra("model");

        //视差动画 实例化
        parallelViewHelper1 = new ParallelViewHelper(this, findViewById(R.id.background_chapter),50,0 );
        parallelViewHelper2 = new ParallelViewHelper(this, findViewById(R.id.leaves_chapter),20,1 );
        parallelViewHelper3 = new ParallelViewHelper(this, findViewById(R.id.butterfly1_chapter),120,1 );
        parallelViewHelper4 = new ParallelViewHelper(this, findViewById(R.id.butterfly2_chapter),200,1 );
        parallelViewHelper5 = new ParallelViewHelper(this, findViewById(R.id.butterfly3_chapter),280,1 );

        button_back_level=findViewById(R.id.button_back_level);
        button_back_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageView_background=findViewById(R.id.background_chapter);

    }

    //每个存在语言切换的Activity需要写
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshEnglishChapterActivity();
    }

    private List<Map<String,Object>> getData(){
        for(int counter=0;counter<pictureIdArray.size();counter++){
            Map<String,Object> map=new HashMap<>();
            map.put("Image",pictureIdArray.get(counter));
            map.put("Text",levelNameArray.get(counter));
            if(ratingNumArray.get(counter)==0){
                map.put("FirstRate",R.mipmap.star_black);
                map.put("SecondRate",R.mipmap.star_black);
                map.put("ThirdRate",R.mipmap.star_black);
            }else if(ratingNumArray.get(counter)==1){
                map.put("FirstRate",R.mipmap.star_light);
                map.put("SecondRate",R.mipmap.star_black);
                map.put("ThirdRate",R.mipmap.star_black);
            }else if(ratingNumArray.get(counter)==2){
                map.put("FirstRate",R.mipmap.star_light);
                map.put("SecondRate",R.mipmap.star_light);
                map.put("ThirdRate",R.mipmap.star_black);
            }else if(ratingNumArray.get(counter)==3){
                map.put("FirstRate",R.mipmap.star_light);
                map.put("SecondRate",R.mipmap.star_light);
                map.put("ThirdRate",R.mipmap.star_light);
            }else if(ratingNumArray.get(counter)==-1){
                map.put("FirstRate",R.mipmap.star_nothing);
                map.put("SecondRate",R.mipmap.star_nothing);
                map.put("ThirdRate",R.mipmap.star_nothing);
            }
            dataList.add(map);
        }
        return dataList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //获取用户点击的关卡值
        clickedLevel=position+1;

        if(clickedLevel<=unlockLevel)
        {
            SharedPreferences.Editor editor=getSharedPreferences("AllLevel",MODE_PRIVATE).edit();
            editor.putInt("clickedLevel",clickedLevel);
            editor.putInt(modelUnlockLevel,unlockLevel);
            editor.commit();
//            Intent intent=new Intent(BaseLevelActivity.this,KnowledgeLearningActivity.class);
//            intent.putExtra("chapterModel",model);
            Intent intent=new Intent(BaseLevelActivity.this,nextClass);
            //在 Scratch游戏 中的 开发教程 中创建的项目都是一次性的（即不会保存到项目管理区，退出，项目就会消失）
            intent.putExtra("scratchGameSingleUse","yes");
            //彭佳汉添加，用于加载相应模块的教程
            intent.putExtra("model",model);
            startActivity(intent);
        }
        else {
            //release发行版本应该使用的代码-开头
            Toast.makeText(this,getString(R.string.over_level_tip)+unlockLevel+getString(R.string.over_level_tip_end),Toast.LENGTH_SHORT).show();
        }

    }

    //2018-4-14 最大关卡数 应该与 关卡内容文件的行数对应，之前与网站个数对应太过牵强
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
        else if(model.equals("turtle")){
            imageView_background.setBackgroundResource(R.mipmap.background_module1);
            modelUrl= "level_txt/coding_car.txt";
            modelUnlockLevel="gameTurtleUnlockLevel";
            modelMaxLevel="gameTurtleMaxLevel";
            modelRating="turtle";
            nextClass=TurtleActivity.class;
        }
        SharedPreferences sharedPreferences=getSharedPreferences("AllLevel",MODE_PRIVATE);
        unlockLevel=sharedPreferences.getInt(modelUnlockLevel,1);
    }

    private void refreshEnglishChapterActivity(){
        initModel(model);

        //通过 english_knowledge_url.txt 的行数动态获取 用户所选中章节 的最大关卡数
        try{
            InputStream is = this.getAssets().open(modelUrl);
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String line="";
            if(maxLevel==0){
                while ((line=br.readLine())!=null) {
                    maxLevel++;
                }
                SharedPreferences.Editor editor=getSharedPreferences("AllLevel",MODE_PRIVATE).edit();
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


        gridView = (GridView) findViewById(R.id.gridView_bae_level);
        dataList = new ArrayList<>();
        String[] key=new String[]{"Image","Text","FirstRate","SecondRate","ThirdRate"};
        int[] value=new int[]{R.id.button_gridview_item,R.id.textview_gridview_item,R.id.item_first_image,R.id.item_second_image,R.id.item_third_image};
        //适配屏幕引入语句，重写了getView
        simpleAdapter = new SimpleAdapter(this, getData(),R.layout.gridview_item,key,value)
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

    @Override
    protected void onPause() {
        super.onPause();
        parallelViewHelper1.stop();
        parallelViewHelper2.stop();
        parallelViewHelper3.stop();
        parallelViewHelper4.stop();
        parallelViewHelper5.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        parallelViewHelper1.start();
        parallelViewHelper2.start();
        parallelViewHelper3.start();
        parallelViewHelper4.start();
        parallelViewHelper5.start();
    }
}
