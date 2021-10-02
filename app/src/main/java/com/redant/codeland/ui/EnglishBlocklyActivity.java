package com.redant.codeland.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.model.Block;
import com.google.blockly.model.DefaultBlocks;
import com.google.blockly.utils.BlockLoadingException;
import com.google.blockly.utils.BlocklyXmlHelper;
import com.redant.codeland.R;
import com.redant.codeland.app.MyApplication;
import com.redant.codeland.entity.LevelInfo;
import com.redant.codeland.util.Util;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2018-03-19.
 */

public class EnglishBlocklyActivity extends BaseBlocklyActivity {
    //打印log的tag
    private static final String TAG = "EnglishBlocklyTest";
    //单词块y值上下之间的宽度
    private static int ENGLISH_BLOCK_DISTANCE = 80;
    //单词块 与 图片头 x值左右之间的长度
    private static int PICTURE_ENGLISH_BLOCK_DISTANCE = 150;
    //单词块 与 屏幕左边缘的距离
    private static int BLOCK_MARGIN_LEFT_DISTANCE=100;
    //单词块 与 屏幕上边缘的距离
    private static int BLOCK_MARGIN_TOP_DISTANCE=100;
    //toolbox的路径,复用了古诗中的空白toolbox
    private static final String TOOLBOX_PATH = "poetry/toolbox.xml";
    //添加js生成器函数路径
    private static final List<String> JAVASCRIPT_GENERATORS = Arrays.asList(
            "english/generator_english.js"
    );

    private Block blockDelete;

    private VideoView videoView;

    //回调接口,在这里写匹配逻辑
    public final CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback = new
            CodeGenerationRequest.CodeGeneratorCallback() {
                @Override
                public void onFinishCodeGeneration(String generatedCode) {

                    Log.i(TAG, "generatedCode: \n"+generatedCode);
                    StringBuffer finalToastWord = new StringBuffer();
                    int flagWrongOne=0;
                    int flagRightOne=0;
                    String pictureWord;
                    String [] codes = generatedCode.split("\n");
                    for(int i = 0; i< codes.length; i++){
                        if(codes[i].contains("=")){
                            String [] arrs = codes[i].split("=");
                            pictureWord=arrs[0];
                            //一定要注意“头图片后面是空的，刚才就是这里bug 'pig='后面空的，你再用arrs[1]数组越界”
                            if(arrs.length==2){
                                if(pictureWord.equals(arrs[1])){
                                    finalToastWord.append("<"+pictureWord+">单词拼写正确啦，你真棒！\n");
                                    flagRightOne++;
                                }else{
                                    finalToastWord.append("<"+pictureWord+">单词拼写错误咯，快看看原单词哪里错了吧！\n");
                                    flagWrongOne++;
                                }
                            }
                            else if(arrs.length==1){
                                finalToastWord.append("【"+pictureWord+"】这块图片头没有接入单词块喔\n");
                                flagWrongOne++;
                            }
                        }
                    }

                    if(flagRightOne==0){
                        rating=0;
                    }else if(flagWrongOne>0 && flagRightOne<=flagWrongOne){
                        rating=1;
                    }else{
                        if(flagWrongOne>0 && flagRightOne>flagWrongOne){
                            rating=2;
                        }else if(flagWrongOne==0){
                            rating=3;
                        }
                        SharedPreferences sharedPreferences=getSharedPreferences("AllLevel",MODE_PRIVATE);
                        int englishMaxLevel=sharedPreferences.getInt("englishMaxLevel",0);
                        maxLevel=englishMaxLevel;
                        int englishUnlockLevel=sharedPreferences.getInt("englishUnlockLevel",0);
                        //只有 全部拼写正确 且 最大关卡为当前关卡， 数据库中的“最大关卡”才会+1
                        if(englishMaxLevel>englishUnlockLevel && clickedLevel>=englishUnlockLevel){
                            SharedPreferences.Editor editor=getSharedPreferences("AllLevel",MODE_PRIVATE).edit();
                            editor.putInt("englishUnlockLevel",clickedLevel+1);
                            flag=true;
                            editor.commit();
                        }
                    }
                    List<LevelInfo> rates= DataSupport.where("name = ?","english "+clickedLevel).find(LevelInfo.class);
                    if(rates.isEmpty()){
                        LevelInfo rate=new LevelInfo();
                        rate.setName("english "+clickedLevel);
                        rate.setModel("english");
                        rate.setRating(rating);
                        rate.save();
                        Toast.makeText(EnglishBlocklyActivity.this,rate.getRating()+"",Toast.LENGTH_SHORT).show();
                        Log.d("animal",rate.getRating()+"");
                    }else{
                        int oldRating=rates.get(0).getRating();
                        if(rating>oldRating){
                            LevelInfo rate=new LevelInfo();
                            rate.setRating(rating);
                            rate.updateAll("name = ?","english "+clickedLevel);
                            Toast.makeText(EnglishBlocklyActivity.this,rate.getRating()+"",Toast.LENGTH_SHORT).show();
                            Log.d("animal",rate.getRating()+"");
                        }
                    }
                    //获取英语关卡值
                    Util.showDialog2(EnglishBlocklyActivity.this, finalToastWord.toString()+"\n单词图总数:"+" 拼对数:"+rating, rating,clickedLevel<maxLevel, handler);
//                    Util.showDialog(EnglishBlocklyActivity.this, finalToastWord.toString()+"\n单词图总数:"+" 拼对数:"+rating, rating, handler);
                    Log.i(TAG, "finalToastWord: \n"+finalToastWord);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        //适配屏幕引入语句
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());


        SharedPreferences sharedPreferences=getSharedPreferences("AllLevel",MODE_PRIVATE);
        clickedLevel=sharedPreferences.getInt("clickedLevel",1);
        //初始化工作空间，随机地从数据库中提取英语单词文本填充进块，然后随机散乱排列
        loadCase();
        //getController().unlinkViews(blockDelete);
        //getController().trashRootBlock(blockDelete);
        //getController().getWorkspace().addBlockToTrash(blockDelete);

        //是否第一次打开APP，是则运行新手引导，否则不在此运行
        if(MyApplication.isFirstRun("EnglishBlocklyActivityNewNewbieGuide"))
        {
            EnglishBlocklyActivityNewNewbieGuide();
        }

    }

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {
        return TOOLBOX_PATH;
    }

    @NonNull
    @Override
    protected List<String> getBlockDefinitionsJsonPaths() {
        List<String> assetPaths = new ArrayList<>(DefaultBlocks.getAllBlockDefinitions());
        Context context=EnglishBlocklyActivity.this;//首先，在Activity里获取context
        assetPaths.add("english/english.json");
        return assetPaths;
    }

    @NonNull
    @Override
    protected List<String> getGeneratorsJsPaths() {
        return JAVASCRIPT_GENERATORS;
    }

    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        return mCodeGeneratorCallback;
    }

    /**
     * 初始化工作区时将块散乱排列
     */
    public void load(){
        File directory = getFilesDir();
        File file = new File(directory,"englishWord_workspace.xml");
        if(file.exists()){
            file.delete();

        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //fos绑定file，使得file指向了generateEnglishWordXML生成的xml文件，file从数据库中获取了名字的块
            FileOutputStream fos = new FileOutputStream(file);
            Util.generateWholeEnglishWordXML(fos,clickedLevel);
            fos.close();
            //fis绑定file,使得BlocklyXmlHelper.loadFromXml可以加载我们刚生成的xml文件
            FileInputStream fis = new FileInputStream(file);
            //加载指定XML文件中的块，就是上面Util.generateEnglishWordXML(fos);生成的xml
            List<Block> blocks = BlocklyXmlHelper.loadFromXml(fis,
                    getController().getBlockFactory());

            //收集所有块（图片头+单词块）中 单词块的总个数
            int englishWordBlock=0;
            for (int i = 0; i < blocks.size(); i++) {
                Block copiedModel = blocks.get(i).deepCopy();
                if (copiedModel.getType().equals("body"))
                {englishWordBlock++;}
            }

            //生成一个元素为 0~块的总数-1 的数组，然后用randomizeBlock(randomNumber);打乱里面所有元素的顺序
            //打乱一个数组中的所有值，例如：有5个元素，0-4 打乱后可能为 4 2 1 0 3，这样在setPosition(100,0+i*60);
            //打乱后的4 2 1 0 3决定这个块的位置，就按这个随机顺序排列，块排列整齐，用户体验好，也实现了随机性
            int[] randomNumber=new int[englishWordBlock];
            for(int counter=0;counter<englishWordBlock;counter++){
                randomNumber[counter]=counter;
            }
            randomizeBlock(randomNumber);


            Block standardBlock = blocks.get(0).deepCopy();
            standardBlock.setPosition(0,0);
            blockDelete=standardBlock;
//            getController().addRootBlock(standardBlock);

            //将块打乱排列,i记录总块数（图片头+单词块）用于加载完全部的块后跳出循环，j值记录‘单词块’，用于y值得排序
            for (int i = blocks.size()-1,j=0; i >=0 ; i--) {
                Block copiedModel = blocks.get(i).deepCopy();
                if (copiedModel.getType().equals("body")) {
                    copiedModel.setPosition(BLOCK_MARGIN_LEFT_DISTANCE+PICTURE_ENGLISH_BLOCK_DISTANCE, BLOCK_MARGIN_TOP_DISTANCE + randomNumber[j] * ENGLISH_BLOCK_DISTANCE);
                    copiedModel.setEditable(false);
                    j++;
                }
                else {
                    copiedModel.setPosition(BLOCK_MARGIN_LEFT_DISTANCE, BLOCK_MARGIN_TOP_DISTANCE + i * ENGLISH_BLOCK_DISTANCE);
                    copiedModel.setEditable(false);
                }
                getController().addRootBlock(copiedModel);
            }

            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BlockLoadingException e) {
            e.printStackTrace();
        }
        //getController().getWorkspace().
    }

    //打乱一个数组中的所有值，这里用于如：有5个块，0-4 打乱后可能为 4 2 1 0 3，这样在setPosition(100,0+i*60);
    //打乱后的4 2 1 0 3决定这个块的位置，就按顺序排列，用户体验好，也实现了随机性
    public void randomizeBlock(int[] a) {
        int index, tmp, i;
        int n = a.length;
        Random random = new Random(System.currentTimeMillis());
        for (i = 0; i < n; i++) {
            //random.nextInt()括号中不加入数值会生成副负数+正数，刚才就这报错
            //random.nextInt(n) 才会生成 0~n-1的正整数
            index = random.nextInt(n-i) + i;//生成 i~n-1 的数值
            if (index != i) {
                tmp = a[i];
                a[i] = a[index];
                a[index] = tmp;
            }
        }
    }

    @Override
    protected void reload() {
        loadCase();
    }

    @Override
    protected void showHelp() {
        EnglishBlocklyActivityNewNewbieGuide();
        super.showHelp();
    }

    @Override
    public void loadModel() {
        load();
    }

    @Override
    public void loadCase() {
        super.loadCase();
    }

    public void EnglishBlocklyActivityNewNewbieGuide(){

        NewbieGuide.with(this).setLabel("page").alwaysShow(true)//true:程序员开发调试时使用，每次打开APP显示新手引导;false:只在用户第一次打开APP显示新手引导
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {}
                    //把调用父亲的新手引导放在儿子新手引导全部结束后再次弹出
                    @Override
                    public void onRemoved(Controller controller) {
                        EnglishBlocklyActivity.super.BaseBlocklyActivityNewNewbieGuide();
                    }
                })
                .addGuidePage(
                        GuidePage.newInstance()
//可以一个引导页多个高亮，多加几个.addHighLight()即可
                                .setLayoutRes(R.layout.english_blockly_activity_newbie_guide)//引导页布局，点击跳转下一页或者消失引导层的控件id
                                .setEverywhereCancelable(true)//是否点击任意地方跳转下一页或者消失引导层，默认true
//                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
//                                    @Override
//                                    //onLayoutInflated很重要，返回了 引导层的view，一定要利用好这个view，比如为引导层的view设定监听事件
//                                    //如果想要在引导页findViewByID一定要加上 view. 否则空指针报错
//                                    public void onLayoutInflated(View view) {
//                                        TextView textViewNewbieGuide = view.findViewById(R.id.textview_mainactvitiy_newbie_guide);
//
//                                    }
//                                })
                                //这几行代码可以让引导界面慢慢过渡，从有到无
                                //暂时不加入动画，否则内存泄漏，显示不出来
//                                .setEnterAnimation(enterAnimation)//进入动画
//                                .setExitAnimation(exitAnimation)//退出动画
                )
                .addGuidePage(
                        GuidePage.newInstance()
//可以一个引导页多个高亮，多加几个.addHighLight()即可
                                .setLayoutRes(R.layout.english_blockly_activity_newbie_guide2)//引导页布局，点击跳转下一页或者消失引导层的控件id
                                .setEverywhereCancelable(true)//是否点击任意地方跳转下一页或者消失引导层，默认true
//                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
//                                    @Override
//                                    //onLayoutInflated很重要，返回了 引导层的view，一定要利用好这个view，比如为引导层的view设定监听事件
//                                    public void onLayoutInflated(View view) {
//                                        TextView textViewNewbieGuide = view.findViewById(R.id.textview_mainactvitiy_newbie_guide);
//
//                                    }
//                                })
                                //这几行代码可以让引导界面慢慢过渡，从有到无
//                                .setEnterAnimation(enterAnimation)//进入动画
//                                .setExitAnimation(exitAnimation)//退出动画
                ).show();

    }
}
