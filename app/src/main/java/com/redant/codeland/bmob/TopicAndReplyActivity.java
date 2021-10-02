package com.redant.codeland.bmob;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.redant.codeland.R;
import com.redant.codeland.entity.Bmob.Reply;
import com.redant.codeland.entity.Bmob.Topic;
import com.redant.codeland.entity.Bmob.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class TopicAndReplyActivity extends AppCompatActivity {

    private TextView textViewTopicUserName;
    private TextView textViewTopicTitle;

    private MyImageView textViewTopicPicture;
    Bitmap bmImg;

    private EditText editTextReply;
    //回复  按钮
    private Button buttonReply;

    //从上一个活动传递下来的 话题内容
    private String topicTitleString;
    //从上一个活动传递下来的 话题 的 版块
    private String topicSection;

    private String topicObjectId;

    //zhl新增
    private String topicPictureUrl;


    //存取 回复的内容
    private ArrayList<String> replyContentArray;
    //存取 回复此帖子的用户名
    private ArrayList<String> replyUserNameArray;





    //每次刷新GridView都要从 云端数据库 读取 现在已有的回复的内容以及其对应的作者，并加载到dataList中
    private List<Map<String,Object>> dataList;

    //GridView要加载的数据全部存储在simpleAdapter里面
    private SimpleAdapter simpleAdapter;
    //加载帖子的帖子列表
    private GridView gridView;

    private Button buttonSetTopicTop;
    private Button buttonDeleteTopic;

    private static String ImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_and_reply);

        gridView=findViewById(R.id.grid_view_topic_in);

        textViewTopicUserName=findViewById(R.id.topic_user_name_in);
        textViewTopicTitle=findViewById(R.id.topic_title_in);
        //zhl新增
        textViewTopicPicture=findViewById(R.id.topic_picture);

        editTextReply=findViewById(R.id.edit_text_reply);

        buttonReply=findViewById(R.id.button_reply);
        buttonReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(editTextReply.getText().toString().trim())) {
                    Toast.makeText(TopicAndReplyActivity.this,"回复内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                Reply reply=new Reply();
                reply.setTopicTitle(topicTitleString);
                //添加图片url
                reply.setTopicPictureUrl(topicPictureUrl);
                reply.setReplyUserName(BmobUser.getCurrentUser().getUsername());
                reply.setContent(editTextReply.getText().toString().trim());
                reply.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Toast.makeText(TopicAndReplyActivity.this,"回复成功", Toast.LENGTH_SHORT).show();
                            refreshGridView();
                        }else {
                            Toast.makeText(TopicAndReplyActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        //置顶帖子 按钮 点击事件
        buttonSetTopicTop=findViewById(R.id.set_top);
        buttonSetTopicTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //读取当前用户的权限
                int currentUserControlLevel= BmobUser.getCurrentUser(User.class).getControlLevel();
                if(currentUserControlLevel!=1){
                    Toast.makeText(TopicAndReplyActivity.this,"您不是版主，不可置顶帖子",Toast.LENGTH_SHORT).show();
                    return;
                }

                //首先找到所有的当前版块的帖子中被设置为置顶帖子的帖子，topPriority置为 不是置顶帖的标识位0
                BmobQuery<Topic> topicBmobQuery=new BmobQuery<>();
                topicBmobQuery.addWhereEqualTo("sectionName",topicSection);
                topicBmobQuery.findObjects(new FindListener<Topic>() {
                    @Override
                    public void done(List<Topic> list, BmobException e) {
                        if(e==null){
                            for(final Topic topic:list){
                                //把之前是置顶帖的帖子修改为不是置顶帖子
                                Topic topicUpdate=new Topic();
                                topicUpdate.setTopPriority(0);
                                topicUpdate.update(topic.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){

                                            //把当前的的帖子修改为置顶帖子
                                            Topic topicUpdate=new Topic();
                                            topicUpdate.setTopPriority(1);
                                            topicUpdate.update(topicObjectId, new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e==null){
                                                        Toast.makeText(TopicAndReplyActivity.this,"当前帖子置顶成功",Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        Toast.makeText(TopicAndReplyActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }else {
                                            Toast.makeText(TopicAndReplyActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }else {
                            Toast.makeText(TopicAndReplyActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //删除帖子按钮 点击事件
        buttonDeleteTopic=findViewById(R.id.delete_topic);
        buttonDeleteTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //读取当前用户的权限
                int currentUserControlLevel= BmobUser.getCurrentUser(User.class).getControlLevel();
                if(currentUserControlLevel!=1){
                    Toast.makeText(TopicAndReplyActivity.this,"您不是版主，不可删除帖子",Toast.LENGTH_SHORT).show();
                    return;
                }

                Topic topic=new Topic();
                topic.setObjectId(topicObjectId);
                topic.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            Toast.makeText(TopicAndReplyActivity.this,"当前帖子删除成功",Toast.LENGTH_SHORT).show();
                            //帖子被删除，当前帖子的页面应该销毁掉
                            finish();
                        }else {
                            Toast.makeText(TopicAndReplyActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //初始化最顶部的 话题 的用户名和话题内容
        initialTopic();
        //根据最新的云端数据库 加载或者刷新 显示回复的GrdiView
        refreshGridView();

    }

    //初始化最顶部的 话题 的用户名和话题内容
    public void initialTopic(){

        topicTitleString=getIntent().getStringExtra("topicTitle");

        topicPictureUrl= getIntent().getStringExtra("topicPicture");

        BmobQuery<Topic> topicBmobQuery=new BmobQuery<>();
        topicBmobQuery.addWhereEqualTo("title",topicTitleString);

        //在这里会根据标题进行查询，也就是说其他信息可以不用传进来
        topicBmobQuery.findObjects(new FindListener<Topic>() {
            @Override
            public void done(List<Topic> list, BmobException e) {
                if(e==null) {
                    //存储此话题的版块，以便于置顶
                    topicSection = list.get(0).getSectionName();
                    topicObjectId = list.get(0).getObjectId();
                    //把该话题的 话题内容和用户名填入到UI表示层中
                    textViewTopicTitle.setText(list.get(0).getTitle());
                    textViewTopicUserName.setText(list.get(0).getUserName());
                    textViewTopicPicture.setImageURL(list.get(0).getBmobFile().getUrl());
                }
                else {
                    Toast.makeText(TopicAndReplyActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //根据最新的云端数据库 加载或者刷新 显示回复的GrdiView
    public void refreshGridView(){
        //new ArrayList<>()是清空dataList的数据,每次刷新GridView都要从 云端数据库 读取 现在已有的版块，这些已有的版块加载到dataList中
        dataList=new ArrayList<>();
        //清空老版本的数据读取新的数据
        replyUserNameArray=new ArrayList<>();
        replyContentArray=new ArrayList<>();

        BmobQuery<Reply> replyBmobQuery=new BmobQuery<>();
        replyBmobQuery.addWhereEqualTo("topicTitle",topicTitleString);
        //查询数据最多只显示查询到的前50条数据,如果不加上这条语句，默认返回10条数据
        replyBmobQuery.setLimit(50);
        replyBmobQuery.findObjects(new FindListener<Reply>() {
            @Override
            public void done(List<Reply> list, BmobException e) {
                if(e==null){
                    for(Reply reply:list){
                        replyUserNameArray.add(reply.getReplyUserName());
                        replyContentArray.add(reply.getContent());

                        Map<String,Object> map=new HashMap<>();
                        map.put("replyUserName",reply.getReplyUserName());
                        map.put("replyContent",reply.getContent());
                        dataList.add(map);
                        simpleAdapter=new SimpleAdapter(TopicAndReplyActivity.this,dataList,R.layout.gridview_item_topic_in,new String[]{"replyUserName","replyContent"},new int[]{R.id.reply_user_name,R.id.reply_title});
                        gridView.setAdapter(simpleAdapter);
                    }
                }else {
                    Toast.makeText(TopicAndReplyActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
