package com.redant.codeland.entity;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 诗歌的实体类
 * Created by Administrator on 2017-12-11.
 */

public class Poetry extends DataSupport {
    private int id;
    private String name;
    private String kind;
    private String title;
    private String writor;
    /**
     * 2018-03-06
     * 将诗歌的内容从first、second等等修改为string类型的列表，便于适配更多的诗歌类型。
     * 增加了诗歌作者这一属性
     * 增加了作者朝代这一属性
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

    public void setKind(String kind) {
        this.kind = kind;
    }
    public String getKind() {
        return kind;
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

    public String getName() {

        return name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for(String c:contents){
            sb.append(c);
        }
        return "["+level+"-"+title+"-"+writor+"("+dynasty+")"+sb.toString()+"]";
    }
}
