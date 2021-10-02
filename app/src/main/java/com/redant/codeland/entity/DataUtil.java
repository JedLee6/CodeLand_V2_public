package com.redant.codeland.entity;

import android.content.Context;
import android.content.SharedPreferences;

import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-11.
 */

public class DataUtil {

    public static void initAnimalKindDataBase(Context context){
        //先清空数据库
        DataSupport.deleteAll(Animal.class);
        try{
            InputStream is = context.getAssets().open("animal/animal2.txt");
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String line="";
            while ((line=br.readLine())!=null) {
                String[] arrs=null;//备用数组
                Animal animal = new Animal();
                arrs=line.split(" ");
                animal.setLevel(Integer.parseInt(arrs[0]));
                animal.setKind(arrs[1]);
                animal.setName(arrs[2]);
                animal.setEating(arrs[3]);
                animal.save();
            }
            br.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void initCelebrityDataBase(Context context) {
        //先清空该数据库中所有的名人
        DataSupport.deleteAll(Celebrity.class);
        try{
            InputStream is = context.getAssets().open("celebrity/celebrity.txt");
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String line="";
            while ((line=br.readLine())!=null) {
                String[] arrs2=null;//备用数组
                Celebrity c=new Celebrity();
                arrs2=line.split(">");//用来分割TXT中文字的
                c.setLevel(Integer.parseInt(arrs2[0]));
                c.setTitle(arrs2[1]);
                //c.setWritor(arrs2[2]);//按排序来录入
                //c.setDynasty(arrs2[3]);
                List<String> contents = new ArrayList<>();
                for(int i = 2; i<arrs2.length;i++){
                    /*这里的contents.add(arrs2[i]);就是用来加入内容块的，i之所以从2开始，是arrs2[0]arrs2[1]用来放题目块了*/
                    contents.add(arrs2[i]);
                }
                c.setContents(contents);
                c.save();
            }
            br.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }



    public static void initSanzijingDataBase(Context context) {
        //先清空该数据库中所有的名人
        DataSupport.deleteAll(Sanzijing.class);
        try{
            InputStream is = context.getAssets().open("sanzijing/sanzijing.txt");
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String line="";
            while ((line=br.readLine())!=null) {
                String[] arrs3=null;//备用数组
                Sanzijing c=new Sanzijing();
                arrs3=line.split(">");//用来分割TXT中文字的
                c.setLevel(Integer.parseInt(arrs3[0]));
                c.setTitle(arrs3[1]);
                //c.setWritor(arrs2[2]);//按排序来录入
                //c.setDynasty(arrs2[3]);
                List<String> contents = new ArrayList<>();
                for(int i = 2; i<arrs3.length;i++){
                    /*这里的contents.add(arrs2[i]);就是用来加入内容块的，i之所以从2开始，是arrs2[0]arrs2[1]用来放题目块了*/
                    contents.add(arrs3[i]);
                }
                c.setContents(contents);
                c.save();
            }
            br.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }


    /**
     * 将原来手工录入数据库改为了将文件读入到数据库
     * 现在只需要修改poetry.txt即可
     */
    public static void initPoetryDataBase(Context context) {
        //先清空该数据库中所有的古诗，避免旧版本中数据库不匹配
        DataSupport.deleteAll(Poetry.class);
        try{
            InputStream is = context.getAssets().open("poetry/poetry.txt");
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String line="";
            while ((line=br.readLine())!=null) {
                String[] arrs=null;//备用数组
                Poetry p=new Poetry();
                arrs=line.split(">");
                p.setLevel(Integer.parseInt(arrs[0]));
                p.setTitle(arrs[1]);
                p.setWritor(arrs[2]);
                p.setDynasty(arrs[3]);
                List<String> contents = new ArrayList<>();
                for(int i = 4; i<arrs.length;i++){
                    contents.add(arrs[i]);
                }
                p.setContents(contents);
                p.save();
            }
            br.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void initEnglishWordDataBase(Context context) {
        //先清空该数据库中所有的英语单词以及块的资源，避免旧版本中数据库不匹配
        DataSupport.deleteAll(EnglishWord.class);
        try{
            InputStream is = context.getAssets().open("english/english_word_list.txt");
            InputStream ispicture = context.getAssets().open("english/english_word_list.txt");
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            BufferedReader brpicture=new BufferedReader(new InputStreamReader(ispicture));

            String line="";
            String linepicture="";

            //用于记录关卡值
            int counter=0;
            while ((line=br.readLine())!=null && (linepicture=brpicture.readLine())!=null) {
                counter++;
                EnglishWord s = new EnglishWord();
                //传入该行所有的单词块,图片块
                s.setAllBlockWords(line);
                s.setAllPictureWords(linepicture);
                s.setLevel(counter);
                s.save();
            }
            br.close();
            is.close();
            brpicture.close();
            ispicture.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void initAllLevel(Context context){
        //实例化SharedPreferences对象（第一步）
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "AllLevel", Context.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        //拿到，说明此englishLevel已经被创建了，直接得到最大关卡；拿不到，说明第一次创建该文件，返回初始的关卡0，代表第一次运行游戏
        int englishLevel = sharedPreferences.getInt("englishLevel", 0);
        int animalLevel =sharedPreferences.getInt("animalLevel",0);
        int poetryLevel =sharedPreferences.getInt("poetryLevel",0);
        int sanzijingLevel =sharedPreferences.getInt("sanzijingLevel",0);
        int celebrityLevel =sharedPreferences.getInt("celebrityLevel",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //如果关卡为0，则为唯一的可能，就是上面的默认值，第一次运行游戏，初始化关卡为1
        //如果关卡不为0，则说明之前已经运行过APP，不能再改动文件
        if (englishLevel==0) {
            //保存数据 （第三步）
            editor.putInt("englishLevel", 1);
            //提交当前数据 （第四步）
            editor.commit();
        }
        if (animalLevel==0) {
            //保存数据 （第三步）
            editor.putInt("aniamlLevel", 1);
            //提交当前数据 （第四步）
            editor.commit();
            if (poetryLevel==0) {
                //保存数据 （第三步）
                editor.putInt("poetryLevel", 1);
                //提交当前数据 （第四步）
                editor.commit();
            }
            if (sanzijingLevel==0) {
                //保存数据 （第三步）
                editor.putInt("sanzijingLevel", 1);
                //提交当前数据 （第四步）
                editor.commit();
            }
            if (celebrityLevel==0) {
                //保存数据 （第三步）
                editor.putInt("celebrityLevel", 1);
                //提交当前数据 （第四步）
                editor.commit();
            }
        }
    }

    //2018-4-11 重写，由于 知识笔记中要加入 知识标题>知识网站 因此 arrs=line.split(">"); 读取 > 后面的网站url
    //把所有 学习资料url写入数据库，方便读写
    public static void initKnowledgeUrl(Context context){
        //先清空数据库，避免多次写入数据库
//        DataSupport.deleteAll(LevelInfo.class);
        DataSupport.deleteAll(LevelInfo.class,"rating = ?","");
        try{
            //english url写入数据库
            //InputStream is = context.getAssets().open("english/english_knowledge_url.txt");
            InputStream is = context.getAssets().open("scratch/scratch_knowledge_url.txt");
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String line="";

            //counter用于记录关卡，并传入数据库
            int counter=0;
            while ((line=br.readLine())!=null) {

                String[] arrs=null;//备用数组
                arrs=line.split(">");

                counter++;
                LevelInfo knowledgeUrl=new LevelInfo();
                knowledgeUrl.setCategory("EnglishUrl");
                knowledgeUrl.setLevel(counter);
                knowledgeUrl.setUrl(arrs[1]);
                knowledgeUrl.save();
            }
            br.close();
            is.close();

            //animal url写入数据库
            is = context.getAssets().open("maze/maze_knowledge_url.txt");
            br=new BufferedReader(new InputStreamReader(is));
            line="";

            //counter用于记录关卡，并传入数据库
            counter=0;
            while ((line=br.readLine())!=null) {
                counter++;
                LevelInfo knowledgeUrl=new LevelInfo();
                knowledgeUrl.setCategory("AnimalUrl");
                knowledgeUrl.setLevel(counter);
                knowledgeUrl.setUrl(line);
                knowledgeUrl.save();
            }
            br.close();
            is.close();


            is = context.getAssets().open("cat/cat_knowledge_url.txt");
            br=new BufferedReader(new InputStreamReader(is));
            line="";
            //counter用于记录关卡，并传入数据库
            counter=0;
            while ((line=br.readLine())!=null) {
                counter++;
                LevelInfo knowledgeUrl=new LevelInfo();
                knowledgeUrl.setCategory("PoetryUrl");
                knowledgeUrl.setLevel(counter);
                knowledgeUrl.setUrl(line);
                knowledgeUrl.save();
            }
            br.close();
            is.close();


            is = context.getAssets().open("sanzijing/sanzijing_knowledge_url.txt");
            br=new BufferedReader(new InputStreamReader(is));
            line="";
            //counter用于记录关卡，并传入数据库
            counter=0;
            while ((line=br.readLine())!=null) {
                counter++;
                LevelInfo knowledgeUrl=new LevelInfo();
                knowledgeUrl.setCategory("SanzijingUrl");
                knowledgeUrl.setLevel(counter);
                knowledgeUrl.setUrl(line);
                knowledgeUrl.save();
            }
            br.close();
            is.close();


//            is = context.getAssets().open("celebrity/celebrity_knowledge_url.txt");
            is = context.getAssets().open("maze/maze_knowledge_url.txt");
            br=new BufferedReader(new InputStreamReader(is));
            line="";
            //counter用于记录关卡，并传入数据库
            counter=0;
            while ((line=br.readLine())!=null) {
                counter++;
                LevelInfo knowledgeUrl=new LevelInfo();
                knowledgeUrl.setCategory("CelebrityUrl");
                knowledgeUrl.setLevel(counter);
                knowledgeUrl.setUrl(line);
                knowledgeUrl.save();
            }
            br.close();
            is.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

}
