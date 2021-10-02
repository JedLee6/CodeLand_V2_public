package com.redant.codeland.entity.Bmob;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;


public class Topic extends BmobObject {
    private String title;
    private String userName;
    private String sectionName;
    private Integer topPriority;
    private String code;
    private String content;

    public Topic(){

    }

    public Topic(String title, String userName,String sectionName){
        this.title=title;
        this.userName=userName;
        this.sectionName=sectionName;
    }
    public Topic(String title, String userName, String sectionName, BmobFile bmobFile){
        this.title=title;
        this.userName=userName;
        this.sectionName=sectionName;
        this.bmobFile=bmobFile;
    }
    public Topic(String title, String userName, String sectionName, BmobFile bmobFile,String code,String content){
        this.title=title;
        this.userName=userName;
        this.sectionName=sectionName;
        this.bmobFile=bmobFile;
        this.code=code;
        this.content=content;
    }
    //最新添加，可上传图片topic
    private BmobFile bmobFile;

    public BmobFile getBmobFile(){
        return bmobFile;
    }
    public void setBmobFile(BmobFile bmobFile){
        this.bmobFile=bmobFile;
    }


    public Integer getTopPriority() {
        return topPriority;
    }

    public void setTopPriority(Integer topPriority) {
        this.topPriority = topPriority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getCode(){return code;}

    public void setCode(String code){this.code=code;}

    public String getContent(){return content;}

    public void setContent(String content){this.content=content;}

}
