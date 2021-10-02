package com.redant.codeland.entity;

import org.litepal.crud.DataSupport;

/**
 * 诗歌的实体类
 * Created by Administrator on 2018-03-17.
 */

public class EnglishWord extends DataSupport {
    private int id;
    private String allBlockWords;
    private String allPictureWords;
    //保存关卡值，一个关卡值对应一系列的“英语单词”和“英语图片”
    private int level;

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAllPictureWords() {
        return allPictureWords;
    }

    public void setAllPictureWords(String allPictureWords) {
        this.allPictureWords = allPictureWords;
    }

    public String getAllBlockWords() {
        return allBlockWords;
    }

    public void setAllBlockWords(String allBlockWords) {
        this.allBlockWords = allBlockWords;
    }

    public int getId() {
        return id;
    }

}
