package com.redant.codeland.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.redant.codeland.R;
import com.redant.codeland.app.MyApplication;
import com.redant.codeland.entity.Animal;
import com.redant.codeland.entity.Celebrity;
import com.redant.codeland.entity.EnglishWord;
import com.redant.codeland.entity.Poetry;
import com.redant.codeland.entity.Sanzijing;

import org.litepal.crud.DataSupport;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import com.redant.kidprogramming.app.MyApplication;

/**
 * Created by Administrator on 2017-12-18.
 */

public class Util {
    /**
     * 针对整诗匹配场景来生成XML文件
     * 根据传入的level的值从数据库中随机选择响应level的诗歌来生成XML文件，写入fos流所在的文件中
     * @param fos
     * @param level
     */
    public static void generateWholePoetryXML(FileOutputStream fos, int level){
        List<Poetry> poetryList = DataSupport.where("level = ?",level+"").find(Poetry.class);
        Random random = new Random(System.currentTimeMillis());
        try {
            //获取XmlSerializer对象
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = factory.newSerializer();
            //设置输出流对象
            xmlSerializer.setOutput(fos, "utf-8");
            //文档开始
            xmlSerializer.startDocument("utf-8", true);
            //开始一个标签
            xmlSerializer.startTag(null,"toolbox");
            int flag1 = -1;
            for(int i = 0; i< 2;i++){
                int index = random.nextInt(poetryList.size()-1);
                while(index==flag1){
                    index = random.nextInt(poetryList.size()-1);
                }
                flag1=index;
                Poetry p = poetryList.get(index);
                xmlSerializer.startTag(null,"block");
                xmlSerializer.attribute(null, "type","poetry");
                xmlSerializer.startTag(null,"value");
                xmlSerializer.attribute(null, "name","TITLE");
                xmlSerializer.startTag(null,"block");
                xmlSerializer.attribute(null, "type","poetry_title");
                xmlSerializer.startTag(null,"field");
                xmlSerializer.attribute(null,"name","TITLE");
                xmlSerializer.text(p.getTitle());
                xmlSerializer.endTag(null,"field");
                xmlSerializer.endTag(null,"block");
                xmlSerializer.endTag(null,"value");
                xmlSerializer.endTag(null,"block");
                for(String c : p.getContents()){
                    xmlSerializer.startTag(null,"block");
                    xmlSerializer.attribute(null,"type","poetry_contents");
                    xmlSerializer.startTag(null,"field");
                    xmlSerializer.attribute(null,"name","CONTENT");
                    xmlSerializer.text(c);
                    xmlSerializer.endTag(null,"field");
                    xmlSerializer.endTag(null,"block");
                }
            }
            //每一个endTag对应的startTag
            xmlSerializer.endTag(null,"toolbox");
            xmlSerializer.endDocument();

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 针对上下句匹配的场景生成XML文件
     * 现在这能针对四句的诗，抽一三句
     * @param fos
     * @param level
     */
    public static void generateUpdownPoetryXML(FileOutputStream fos,int level){
        List<Poetry> poetryList = DataSupport.where("level = ?",level+"").find(Poetry.class);//找到对应level的古诗
        Random random = new Random(System.currentTimeMillis());//使用随机数实现随机分布
        try {
            //开始进行新的文件的建立
            //获取XmlSerializer对象
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = factory.newSerializer();
            //设置输出流对象
            xmlSerializer.setOutput(fos, "utf-8");
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null,"toolbox");
            for(int i = 0; i< 2;i++){
                int index = random.nextInt(poetryList.size());
                Poetry p = poetryList.get(index);
                int j=0;
                for(String c : p.getContents()){
                    if(j%2==0){
                        xmlSerializer.startTag(null,"block");
                        xmlSerializer.attribute(null,"type","poetry_first");
                        xmlSerializer.startTag(null,"field");
                        xmlSerializer.attribute(null,"name","CONTENT");
                        xmlSerializer.text(c);
                        xmlSerializer.endTag(null,"field");
                        xmlSerializer.endTag(null,"block");
                    }else{
                        xmlSerializer.startTag(null,"block");
                        xmlSerializer.attribute(null,"type","poetry_second");
                        xmlSerializer.startTag(null,"field");
                        xmlSerializer.attribute(null,"name","CONTENT");
                        xmlSerializer.text(c);
                        xmlSerializer.endTag(null,"field");
                        xmlSerializer.endTag(null,"block");
                        break;
                    }
                    j++;
                }
            }
            xmlSerializer.endTag(null,"toolbox");
            xmlSerializer.endDocument();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据三字经的场景生成XML文件
     */
    public static void generateWholeSanzijingXML(FileOutputStream fos, int level){
        List<Sanzijing> sanzijingList = DataSupport.where("level = ?",level+"").find(Sanzijing.class);
        Random random = new Random(System.currentTimeMillis());
        try {
            //获取XmlSerializer对象
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = factory.newSerializer();
            //设置输出流对象
            xmlSerializer.setOutput(fos, "utf-8");
            //文档开始
            xmlSerializer.startDocument("utf-8", true);
            //开始一个标签
            xmlSerializer.startTag(null,"toolbox");
            int flag2 = -1;
            for(int i = 0; i< 2;i++){
                int index = random.nextInt(sanzijingList.size()-1);
                while(index==flag2){
                    index = random.nextInt(sanzijingList.size()-1);
                }
                flag2=index;
                Sanzijing p = sanzijingList.get(index);
                xmlSerializer.startTag(null,"block");
                xmlSerializer.attribute(null, "type","sanzijing");
                xmlSerializer.startTag(null,"value");
                xmlSerializer.attribute(null, "name","TITLE");
                xmlSerializer.startTag(null,"block");
                xmlSerializer.attribute(null, "type","sanzijing_title");
                xmlSerializer.startTag(null,"field");
                xmlSerializer.attribute(null,"name","TITLE");
                xmlSerializer.text(p.getTitle());
                xmlSerializer.endTag(null,"field");
                xmlSerializer.endTag(null,"block");
                xmlSerializer.endTag(null,"value");
                xmlSerializer.endTag(null,"block");
                for(String c : p.getContents()){
                    xmlSerializer.startTag(null,"block");
                    xmlSerializer.attribute(null,"type","sanzijing_contents");
                    xmlSerializer.startTag(null,"field");
                    xmlSerializer.attribute(null,"name","CONTENT");
                    xmlSerializer.text(c);
                    xmlSerializer.endTag(null,"field");
                    xmlSerializer.endTag(null,"block");
                }
            }
            //每一个endTag对应的startTag
            xmlSerializer.endTag(null,"toolbox");
            xmlSerializer.endDocument();

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 针对上下句匹配的场景生成XML文件
     * 现在这能针对四句的SANZIJING，抽一三句
     * @param fos
     * @param level
     */
    public static void generateUpdownSanzijingXML(FileOutputStream fos,int level){
        List<Sanzijing> sanzijingList = DataSupport.where("level = ?",level+"").find(Sanzijing.class);//找到对应level的古诗
        Random random = new Random(System.currentTimeMillis());//使用随机数实现随机分布
        try {
            //开始进行新的文件的建立
            //获取XmlSerializer对象
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = factory.newSerializer();
            //设置输出流对象
            xmlSerializer.setOutput(fos, "utf-8");
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null,"toolbox");
            for(int i = 0; i< 2;i++){
                int index = random.nextInt(sanzijingList.size());
                Sanzijing p = sanzijingList.get(index);
                int j=0;
                for(String c : p.getContents()){
                    if(j%2==0){
                        xmlSerializer.startTag(null,"block");
                        xmlSerializer.attribute(null,"type","sanzijing_first");
                        xmlSerializer.startTag(null,"field");
                        xmlSerializer.attribute(null,"name","CONTENT");
                        xmlSerializer.text(c);
                        xmlSerializer.endTag(null,"field");
                        xmlSerializer.endTag(null,"block");
                    }else{
                        xmlSerializer.startTag(null,"block");
                        xmlSerializer.attribute(null,"type","sanzijing_second");
                        xmlSerializer.startTag(null,"field");
                        xmlSerializer.attribute(null,"name","CONTENT");
                        xmlSerializer.text(c);
                        xmlSerializer.endTag(null,"field");
                        xmlSerializer.endTag(null,"block");
                        break;
                    }
                    j++;
                }
            }
            xmlSerializer.endTag(null,"toolbox");
            xmlSerializer.endDocument();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //名人事迹
    public static void generateWholeCelebrityXML(FileOutputStream fos, int level){
        List<Celebrity> celebrityList = DataSupport.where("level = ?",level+"").find(Celebrity.class);
        Random random = new Random(System.currentTimeMillis());
        try {
            //获取XmlSerializer对象
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = factory.newSerializer();
            //设置输出流对象
            xmlSerializer.setOutput(fos, "utf-8");
            //文档开始
            xmlSerializer.startDocument("utf-8", true);
            //开始一个标签
            xmlSerializer.startTag(null,"toolbox");

            int flag3=-1;
            for(int i=0;i<2;i++){
                int index = random.nextInt(celebrityList.size()-1);
                while(index==flag3){
                    index = random.nextInt(celebrityList.size()-1);
                }
                flag3=index;


                Celebrity p = celebrityList.get(index);
                xmlSerializer.startTag(null,"block");
                xmlSerializer.attribute(null, "type","celebrity");
                xmlSerializer.startTag(null,"value");
                xmlSerializer.attribute(null, "name","TITLE");
                xmlSerializer.startTag(null,"block");
                xmlSerializer.attribute(null, "type","celebrity_title");
                xmlSerializer.startTag(null,"field");
                xmlSerializer.attribute(null,"name","TITLE");
                xmlSerializer.text(p.getTitle());
                xmlSerializer.endTag(null,"field");
                xmlSerializer.endTag(null,"block");
                xmlSerializer.endTag(null,"value");
                xmlSerializer.endTag(null,"block");
                for(String c : p.getContents()){
                    xmlSerializer.startTag(null,"block");
                    xmlSerializer.attribute(null,"type","celebrity_contents");
                    xmlSerializer.startTag(null,"field");
                    xmlSerializer.attribute(null,"name","CONTENT");
                    xmlSerializer.text(c);
                    xmlSerializer.endTag(null,"field");
                    xmlSerializer.endTag(null,"block");
                }
            }
            //每一个endTag对应的startTag
            xmlSerializer.endTag(null,"toolbox");
            xmlSerializer.endDocument();

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 针对上下句匹配的场景生成XML文件
     * 现在这能针对四句的诗，抽一三句
     * @param fos
     * @param level
     */
    public static void generateUpdownCelebrityXML(FileOutputStream fos,int level){
        List<Celebrity> celebrityList = DataSupport.where("level = ?",level+"").find(Celebrity.class);//找到对应level的古诗
        Random random = new Random(System.currentTimeMillis());//使用随机数实现随机分布
        try {
            //开始进行新的文件的建立
            //获取XmlSerializer对象
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = factory.newSerializer();
            //设置输出流对象
            xmlSerializer.setOutput(fos, "utf-8");
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null,"toolbox");
            int flag3=-1;
            for(int i=0;i<2;i++){
                int index = random.nextInt(celebrityList.size()-1);
                while(index==flag3){
                    index = random.nextInt(celebrityList.size()-1);
                }
                flag3=index;

                Celebrity p = celebrityList.get(index);
                int j=0;
                for(String c : p.getContents()){
                    if(j%2==0){
                        xmlSerializer.startTag(null,"block");
                        xmlSerializer.attribute(null,"type","celebrity_first");
                        xmlSerializer.startTag(null,"field");
                        xmlSerializer.attribute(null,"name","CONTENT");
                        xmlSerializer.text(c);
                        xmlSerializer.endTag(null,"field");
                        xmlSerializer.endTag(null,"block");
                    }else{
                        xmlSerializer.startTag(null,"block");
                        xmlSerializer.attribute(null,"type","celebrity_second");
                        xmlSerializer.startTag(null,"field");
                        xmlSerializer.attribute(null,"name","CONTENT");
                        xmlSerializer.text(c);
                        xmlSerializer.endTag(null,"field");
                        xmlSerializer.endTag(null,"block");
                        break;
                    }
                    j++;
                }
            }
            xmlSerializer.endTag(null,"toolbox");
            xmlSerializer.endDocument();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 针对英语单词来生成XML文件
     * 根据传入的level的值从数据库中随机选择响应level的诗歌来生成XML文件，写入fos流所在的文件中
     */
    //2018-3-31注意，这里重写了generateWholeEnglishWordXML()方法，加入新参数 clickedlevel,用于获取用户点击的关卡，以便于完成关卡的加载
    public static void generateWholeEnglishWordXML(FileOutputStream fos,int clickedLevel){

        //find(News.class) findAll()方法查询出的就是news表中的所有数据
        List<EnglishWord> englishWordsList = DataSupport.where("level = ?",""+clickedLevel).find(EnglishWord.class);//找到所有的英语单词表
        Random random = new Random(System.currentTimeMillis());//使用随机数实现随机分布

        try {
            //开始进行新的文件的建立
            //获取XmlSerializer对象
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = factory.newSerializer();
            //设置输出流对象
            xmlSerializer.setOutput(fos, "utf-8");
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null,"toolbox");

            //虽然index这里是随机的，但level是唯一的，所以只有0
            int index = random.nextInt(englishWordsList.size());
            EnglishWord s = englishWordsList.get(index);

            //获取 单词块 图片头的全部个数
            String[] gettheNumberofBlocks= s.getAllBlockWords().split(" ");
            String[] gettheNumberofPictures= s.getAllPictureWords().split(" ");


            //生成 图片头 的xml代码
            for(int counter=0;counter<gettheNumberofPictures.length;counter++){
                xmlSerializer.startTag(null,"block");
                xmlSerializer.attribute(null,"type",gettheNumberofPictures[counter]);
                xmlSerializer.endTag(null,"block");
            }

            //生成 单词块 的xml代码
            for(int counter=0;counter<gettheNumberofBlocks.length;counter++){
                xmlSerializer.startTag(null,"block");
                xmlSerializer.attribute(null,"type","body");
                xmlSerializer.startTag(null,"field");
                xmlSerializer.attribute(null,"name","content");


                xmlSerializer.text(gettheNumberofBlocks[counter]);

                xmlSerializer.endTag(null,"field");
                xmlSerializer.endTag(null,"block");
            }



            xmlSerializer.endTag(null,"toolbox");
            xmlSerializer.endDocument();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 针对动物科属匹配场景来生成XML文件
     * 根据传入的level的值从数据库中随机选择响应level的科属来生成XML文件，写入fos流所在的文件中
     *
     * @param fos
     * @param level
     */
    public static void generateAnimalKindXML(FileOutputStream fos,int level){
        List<Animal> animalLevelList = DataSupport.where("level = ?",level+"").find(Animal.class);
        Log.d("ll",level+"");
        Random random = new Random(System.currentTimeMillis());//使用随机数实现随机分布
        try {
            //获取XmlSerializer对象
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = factory.newSerializer();
            //设置输出流对象
            xmlSerializer.setOutput(fos, "utf-8");
            //文档开始
            xmlSerializer.startDocument("utf-8", true);
            //开始一个标签
            xmlSerializer.startTag(null,"toolbox");
            int size;
            List<String> kindList=new ArrayList<>();
            for(int i=0;i<animalLevelList.size();i++){
                String kind=animalLevelList.get(i).getKind();
                if(kindList.contains(kind)){
                    continue;
                }else {
                    kindList.add(kind);
                }
            }
            int flag3=-1;
            for(int i=0;i<2;i++){
                int index = random.nextInt(kindList.size());
                while(index==flag3){
                    index = random.nextInt(kindList.size());
                }
                flag3=index;
                List<Animal> animalList2=DataSupport.where("kind = ?",kindList.get(index)).find(Animal.class);
                xmlSerializer.startTag(null,"block");
                xmlSerializer.attribute(null,"type","animal_kind");
                xmlSerializer.startTag(null,"field");
                xmlSerializer.attribute(null,"name","KIND");
                xmlSerializer.text(kindList.get(index));
                xmlSerializer.endTag(null,"field");
                xmlSerializer.endTag(null,"block");
                int flag2=-1;
                for(int k=0;k<2;k++){
                    int index2= random.nextInt(animalList2.size()-1);
                    while (index2==flag2){
                        index2=random.nextInt(animalList2.size()-1);
                    }
                    flag2=index2;
                    xmlSerializer.startTag(null,"block");
                    xmlSerializer.attribute(null,"type","animal_instance");
                    xmlSerializer.startTag(null,"field");
                    xmlSerializer.attribute(null,"name","INSTANCE");
                    xmlSerializer.text(animalList2.get(index2).getName());
                    xmlSerializer.endTag(null,"field");
                    xmlSerializer.endTag(null,"block");
                }
            }
            xmlSerializer.endTag(null,"toolbox");
            xmlSerializer.endDocument();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 针对动物食性匹配场景来生成XML文件
     *
     * @param fos
     */
    public static void generateAnimalEatingXML(FileOutputStream fos,int level){
        List<Animal> animalList = DataSupport.where("level = ?",level+"").find(Animal.class);//找到所有的三字经
        Random random = new Random(System.currentTimeMillis());//使用随机数实现随机分布
        try {
            //开始进行新的文件的建立
            //获取XmlSerializer对象
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = factory.newSerializer();
            //设置输出流对象
            xmlSerializer.setOutput(fos, "utf-8");
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null,"toolbox");
            //
            int flag=-1;
            for(int i = 0; i< 2;i++){
                int index = random.nextInt(animalList.size()-1);
                while(index==flag){
                    index = random.nextInt(animalList.size()-1);
                }
                flag=index;
                xmlSerializer.startTag(null,"block");
                xmlSerializer.attribute(null, "type","animal");
                xmlSerializer.startTag(null,"value");
                xmlSerializer.attribute(null, "name","aName");
                xmlSerializer.startTag(null,"block");
                xmlSerializer.attribute(null,"type","animal_name");
                xmlSerializer.startTag(null,"field");
                xmlSerializer.attribute(null,"name","ANAME");
                xmlSerializer.text(animalList.get(index).getName());
                xmlSerializer.endTag(null,"field");
                xmlSerializer.endTag(null,"block");
                xmlSerializer.endTag(null,"value");
                xmlSerializer.endTag(null,"block");

                //配置食性块
                xmlSerializer.startTag(null,"block");
                xmlSerializer.attribute(null,"type","animal_eating");
                xmlSerializer.startTag(null,"field");
                xmlSerializer.attribute(null,"name","AEATING");
                xmlSerializer.text(animalList.get(index).getEating());
                xmlSerializer.endTag(null,"field");
                xmlSerializer.endTag(null,"block");

            }
//

            xmlSerializer.endTag(null,"toolbox");
            xmlSerializer.endDocument();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //这里使用到了JAVA的可变长参数 int... dialogModule ，也就是在方法定义中可以使用个数不确定的参数，对于同一方法可以使用不同个数的参数调用
    //我们用dialogModule的值判断是那个模块的对话框，以重新定义对话框的UI布局：目前是 不传参默认第一模块和第二模块；传参为3代表第三模块
    public static void showDialog(Context context, String text, int rating, final Handler handler,int... dialogModule){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);



        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_match_dialog, null);
        RatingBar ratingBar=  dialogView.findViewById(R.id.match_dialog_ratingbar);
        TextView tvMessage = dialogView.findViewById(R.id.match_dialog_text);
        TextView textView_console = dialogView.findViewById(R.id.textview_console);
        LinearLayout linearLayout_button=dialogView.findViewById(R.id.dialog_button_linearlayout);
        //让文本支持滑动
        tvMessage.setMovementMethod(ScrollingMovementMethod.getInstance());

        //首先一定要注意，如果数组为空，即便是写 if(dialogModule[0]==3)也会报错，所以先判断数组是否存在
        //如果是第三模块中的对话框，那么多出文字“控制台”
        if(dialogModule.length!=0){
            if(dialogModule[0]==3){
                textView_console.setVisibility(View.VISIBLE);
            }
            else if(dialogModule[0]==4){
                textView_console.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.GONE);
                linearLayout_button.setVisibility(View.GONE);
            }
            else{

            }
        }

        ratingBar.setRating(rating);
        tvMessage.setText(text);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        //而其他模块中，涉及下一关等按钮，除了对话框的按钮，其他都不可点击，否则逻辑很麻烦
        dialog.setCancelable(false);
        dialog.show();

        Button btnQuit = dialogView.findViewById(R.id.match_dialog_btn_quit);
        Button btnCheck = dialogView.findViewById(R.id.match_dialog_btn_check);
        Button btnNext = dialogView.findViewById(R.id.match_dialog_btn_next);

        //如果分数没有达到满分，依然有完善的块，使 下一关的块消失
        if(rating<2){
            MyApplication.playClickVoice(MyApplication.getContext(),"failure");
            btnNext.setVisibility(View.GONE);
        }
        //如果是满分3星，则运行以下代码
        else {
            //播放成功音效
            MyApplication.playClickVoice(MyApplication.getContext(),"success");

            btnNext.setVisibility(View.VISIBLE);
        }

//        if(flag == true){
//            btnNext.setVisibility(View.VISIBLE);
//        }else{
//            btnNext.setVisibility(View.GONE);
//        }

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //使用回调让Activity进行处理
                if(handler!=null)
                handler.sendEmptyMessage(1);
                dialog.dismiss();
            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //使用回调让Activity进行处理
                if(handler!=null)
                handler.sendEmptyMessage(2);
                dialog.dismiss();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //使用回调让Activity进行处理
                if(handler!=null)
                handler.sendEmptyMessage(3);
                dialog.dismiss();
            }
        });



    }

    public static void showDialog2(Context context, String text, int rating,boolean flag, final Handler handler,int... dialogModule){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);



        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_match_dialog, null);
        RatingBar ratingBar=  dialogView.findViewById(R.id.match_dialog_ratingbar);
        TextView tvMessage = dialogView.findViewById(R.id.match_dialog_text);
        TextView textView_console = dialogView.findViewById(R.id.textview_console);
        LinearLayout linearLayout_button=dialogView.findViewById(R.id.dialog_button_linearlayout);
        //让文本支持滑动
        tvMessage.setMovementMethod(ScrollingMovementMethod.getInstance());

        //首先一定要注意，如果数组为空，即便是写 if(dialogModule[0]==3)也会报错，所以先判断数组是否存在
        //如果是第三模块中的对话框，那么多出文字“控制台”
        if(dialogModule.length!=0){
            if(dialogModule[0]==3){
                textView_console.setVisibility(View.VISIBLE);
            }
            else if(dialogModule[0]==4){
                textView_console.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.GONE);
                linearLayout_button.setVisibility(View.GONE);
            }
            else{

            }
        }

        ratingBar.setRating(rating);
        tvMessage.setText(text);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        //而其他模块中，涉及下一关等按钮，除了对话框的按钮，其他都不可点击，否则逻辑很麻烦
        dialog.setCancelable(false);
        dialog.show();

        Button btnQuit = dialogView.findViewById(R.id.match_dialog_btn_quit);
        Button btnCheck = dialogView.findViewById(R.id.match_dialog_btn_check);
        Button btnNext = dialogView.findViewById(R.id.match_dialog_btn_next);

        //如果分数没有达到满分，依然有完善的块，使 下一关的块消失
        if(rating<2){
            MyApplication.playClickVoice(MyApplication.getContext(),"failure");
            btnNext.setVisibility(View.GONE);
        }
        //如果是满分3星，则运行以下代码
        else {
            //播放成功音效
            MyApplication.playClickVoice(MyApplication.getContext(),"success");

            btnNext.setVisibility(View.VISIBLE);
        }

        //如果是最后一关，隐藏“下一关”按钮
        if(flag == false){
            btnNext.setVisibility(View.GONE);
        }

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //使用回调让Activity进行处理
                if(handler!=null)
                    handler.sendEmptyMessage(1);
                dialog.dismiss();
            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //使用回调让Activity进行处理
                if(handler!=null)
                    handler.sendEmptyMessage(2);
                dialog.dismiss();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //使用回调让Activity进行处理
                if(handler!=null)
                    handler.sendEmptyMessage(3);
                dialog.dismiss();
            }
        });



    }

    //二维数组存储 每一章节每一关卡的 答案图片
    private static int[][]image_answer_array={
            {R.mipmap.module3_answer_1_1,R.mipmap.module3_answer_1_2,R.mipmap.module3_answer_1_3},
            {R.mipmap.module3_answer_2_1,R.mipmap.module3_answer_2_2,R.mipmap.module3_answer_2_3,R.mipmap.module3_answer_2_4,R.mipmap.module3_answer_2_5},
            {R.mipmap.module3_answer_3_1,R.mipmap.module3_answer_3_2,R.mipmap.module3_answer_3_3,R.mipmap.module3_answer_3_4,R.mipmap.module3_answer_3_5},
            {R.mipmap.module3_answer_4_1,R.mipmap.module3_answer_4_2,R.mipmap.module3_answer_4_3,R.mipmap.module3_answer_4_4,R.mipmap.module3_answer_4_5},
            {R.mipmap.module3_answer_5_1,R.mipmap.module3_answer_5_2,R.mipmap.module3_answer_5_3,R.mipmap.module3_answer_5_4,R.mipmap.module3_answer_5_5},
            {R.mipmap.module3_answer_6_1,R.mipmap.module3_answer_6_2,R.mipmap.module3_answer_6_3,R.mipmap.module3_answer_6_4,R.mipmap.module3_answer_6_5},
            {R.mipmap.module3_answer_7_1,R.mipmap.module3_answer_7_2,R.mipmap.module3_answer_7_3,R.mipmap.module3_answer_7_4,R.mipmap.module3_answer_7_5}
    };
    private static int[][]image_answer_array_en={
            {R.mipmap.en_1_1,R.mipmap.en_1_2,R.mipmap.en_1_3},
            {R.mipmap.en_2_1,R.mipmap.en_2_2,R.mipmap.en_2_3,R.mipmap.en_2_4,R.mipmap.en_2_5},
            {R.mipmap.en_3_1,R.mipmap.en_3_2,R.mipmap.en_3_3,R.mipmap.en_3_4,R.mipmap.en_3_5},
            {R.mipmap.en_4_1,R.mipmap.en_4_2,R.mipmap.en_4_3,R.mipmap.en_4_4,R.mipmap.en_4_5},
            {R.mipmap.en_5_1,R.mipmap.en_5_2,R.mipmap.en_5_3,R.mipmap.en_5_4,R.mipmap.en_5_5},
            {R.mipmap.en_6_1,R.mipmap.en_6_2,R.mipmap.en_6_3,R.mipmap.en_6_4,R.mipmap.en_6_5},
            {R.mipmap.en_6_1,R.mipmap.en_6_2,R.mipmap.en_6_3,R.mipmap.en_6_4,R.mipmap.en_6_5}
    };


    public static void showModule3TipDialog(Context context,int chapter,int level, final Handler handler){
        if(chapter==0)
            return;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);


        View dialogView = LayoutInflater.from(context).inflate(R.layout.module3_tip_dialog, null);

        Button button_close=dialogView.findViewById(R.id.button_modul3_tip_close);
        final Button button_look_answer=dialogView.findViewById(R.id.button_module3_look_answer);
        final TextView textView_tip=dialogView.findViewById(R.id.module3_tip);
        //让 任务说明文本过长时可支持滑动
        textView_tip.setMovementMethod(ScrollingMovementMethod.getInstance());
        final TextView textView_task=dialogView.findViewById(R.id.module3_task);

        final Button button_back=dialogView.findViewById(R.id.button_modul3_tip_back);
        final ImageView imageView_answer=dialogView.findViewById(R.id.image_module3_answer);

        if (MyApplication.languageFlag==1){
            imageView_answer.setImageResource(image_answer_array[chapter-1][level-1]);
        }
        else{
            imageView_answer.setImageResource(image_answer_array_en[chapter-1][level-1]);
        }
        //读取 关卡要求信息txt，获取每一章节的每一关卡的编程要求
        try{
            if(MyApplication.languageFlag==1) {
                InputStream is = context.getAssets().open("coding/json_chinese/module3_tip.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = "";
                while ((line = br.readLine()) != null) {
                    String[] arrays = null;//备用数组，用于获取每一行中的 chapter章节，level关卡，信息
                    arrays = line.split(">");
                    //Integer.parseInt(str)用于将字符串转化为int
                    if (chapter == Integer.parseInt(arrays[0]) && level == Integer.parseInt(arrays[1])) {
                        textView_tip.setText(arrays[2]);
                    }

                }
                br.close();
                is.close();
            }
            else{
                InputStream is = context.getAssets().open("coding/json_chinese/english_module3_tip.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = "";
                while ((line = br.readLine()) != null) {
                    String[] arrays = null;//备用数组，用于获取每一行中的 chapter章节，level关卡，信息
                    arrays = line.split(">");
                    //Integer.parseInt(str)用于将字符串转化为int
                    if (chapter == Integer.parseInt(arrays[0]) && level == Integer.parseInt(arrays[1])) {
                        textView_tip.setText(arrays[2]);
                    }

                }
                br.close();
                is.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        button_look_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView_task.setVisibility(View.GONE);
                textView_tip.setVisibility(View.GONE);
                button_look_answer.setVisibility(View.GONE);
                button_back.setVisibility(View.VISIBLE);
                imageView_answer.setVisibility(View.VISIBLE);
            }
        });
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView_task.setVisibility(View.VISIBLE);
                textView_tip.setVisibility(View.VISIBLE);
                button_look_answer.setVisibility(View.VISIBLE);
                button_back.setVisibility(View.GONE);
                imageView_answer.setVisibility(View.GONE);
            }
        });
        dialog.show();
    }

    public static void showContactUsDialog(Context context){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);


        View dialogView = LayoutInflater.from(context).inflate(R.layout.contact_us_dialog, null);

        final TextView textView_contact_us=dialogView.findViewById(R.id.textView_contact_us);
        //让 任务说明文本过长时可支持滑动
        textView_contact_us.setMovementMethod(ScrollingMovementMethod.getInstance());

        TextView textView_contact_us_information=dialogView.findViewById(R.id.textview_contact_us_information);
        textView_contact_us_information.setMovementMethod(ScrollingMovementMethod.getInstance());
        //根据中英文展现不同的语言的联系方式
        if(MyApplication.languageFlag==MyApplication.LANGUAGE_CHINESE) {
            textView_contact_us.setText(R.string.contact_us_chinese);
            textView_contact_us_information.setText(R.string.contact_us_information_chinese);
        }else {
            textView_contact_us.setText(R.string.contact_us_english);
            textView_contact_us_information.setText(R.string.contact_us_information_english);
        }

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        Button buttonClose=dialogView.findViewById(R.id.button_contact_us_close);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    /**
     *此方法的相关信息(All information about this function)
     *@fileName Util
     *@date on 2020/2/11 12:14
     *@author JedLee 李俊德
     *@email 386236308@qq.com
     *@purpose（此方法的用途）展示隐私政策
     */
    public static void showPrivacyPolicy(final Context context){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.contact_us_dialog, null);

        final TextView textView_contact_us=dialogView.findViewById(R.id.textView_contact_us);
        //让 任务说明文本过长时可支持滑动
        textView_contact_us.setMovementMethod(ScrollingMovementMethod.getInstance());

        TextView textView_contact_us_information=dialogView.findViewById(R.id.textview_contact_us_information);
        textView_contact_us_information.setMovementMethod(ScrollingMovementMethod.getInstance());
        //根据中英文展现不同的语言的联系方式
        if(MyApplication.languageFlag==MyApplication.LANGUAGE_CHINESE) {
            textView_contact_us.setText(R.string.privacy_chinese);
            textView_contact_us_information.setText(R.string.privacy_information_chinese);
        }else {
            textView_contact_us.setText(R.string.privacy_english);
            textView_contact_us_information.setText(R.string.privacy_information_english);
        }

        builder.setView(dialogView);
        //设置隐私政策的对话框不可通过点击对话框外部区域导致对话框关闭
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();

        //从SharedPreferences中读取用户是否同意隐私政策，0代表未同意
        final SharedPreferences sharedPreferences= context.getSharedPreferences("data",Context.MODE_PRIVATE);
        final int privacyFlag=sharedPreferences.getInt("privacyFlag",0);

        Button buttonClose=dialogView.findViewById(R.id.button_contact_us_close);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //若用户未同意隐私政策，那么强制弹出隐私政策对话框，若用户还点击关闭按钮，则直接退出应用
                if(privacyFlag==0){
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                dialog.dismiss();
            }
        });

        Button buttonConfirm=dialogView.findViewById(R.id.button_confirm_privacy);
        buttonConfirm.setText(R.string.Agree);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(privacyFlag==0){
                    SharedPreferences.Editor editor=context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
                    editor.putInt("privacyFlag",1);
                    editor.apply();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * 显示引导的蒙版（最为一般的：一张图片+一段文字）
     */
    public static void showGuide(final Context context, final int textId, final int imgId) {
        NewbieGuide.with((AppCompatActivity) context)
                .setLabel("page")
                .alwaysShow(true)
                .addGuidePage(
                        GuidePage.newInstance()
                                .setLayoutRes(R.layout.layout_guide)
//                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
//                                    @Override
//                                    public void onLayoutInflated(View view) {
//                                        TextView tvTip = view.findViewById(R.id.guide_tv_tip);
//                                        ImageView ivExample = view.findViewById(R.id.guide_iv_example);
//                                        tvTip.setText(context.getText(textId));
//                                        ivExample.setImageResource(imgId);
//                                    }
//                                })
                ).show();

    }

}
