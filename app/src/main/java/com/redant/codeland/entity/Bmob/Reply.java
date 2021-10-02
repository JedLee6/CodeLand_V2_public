package com.redant.codeland.entity.Bmob;

import cn.bmob.v3.BmobObject;

public class Reply extends BmobObject {
    private String content;

    private String topicTitle;
    //zhl新增

    private String topicPictureUrl;

    public String getTopicPictureUrl(){
        return topicPictureUrl;
    }

    public  void setTopicPictureUrl(String topicPictureUrl){
        this.topicPictureUrl=topicPictureUrl;
    }

    //发起回复的用户的用户名
    private String replyUserName;

    public String getReplyUserName() {
        return replyUserName;
    }

    public void setReplyUserName(String replyUserName) {
        this.replyUserName = replyUserName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }


}
