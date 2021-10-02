package com.redant.codeland.entity;

import org.litepal.crud.DataSupport;

import java.util.List;


public class Celebrity extends DataSupport {
    private int id;
    private String title;
    private String writor;
    /**
     * 2018-04-1
     * 将celebrity的内容从first、second等等修改为string类型的列表。
     *Created by quan on 2018-04-1.
     */
    private List<String> contents;
    private String dynasty;//作者朝代
    private int level; //根据诗歌的难易程度所划分的等级，分为1-6,1最简单，6最难。
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWritor() {
        return writor;
    }

    public void setWritor(String writor) {
        this.writor = writor;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public String getDynasty() {
        return dynasty;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        StringBuffer sb3 = new StringBuffer();
        for(String c:contents){
            sb3.append(c);
        }
        return "["+level+"-"+title+"-"+writor+"("+dynasty+")"+sb3.toString()+"]";
    }
}
