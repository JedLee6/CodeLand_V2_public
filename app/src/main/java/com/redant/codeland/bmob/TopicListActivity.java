package com.redant.codeland.bmob;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.redant.codeland.R;
import com.redant.codeland.entity.Bmob.Topic;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class TopicListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private String sectionName;

    //帖子列表的标题
    private TextView textViewTopicListTitle;

    //存取 帖子的标题
    private ArrayList<String> topicTitleArray;
    //存取 发送此帖子的作者姓名
    private ArrayList<String> topicUserNameArray;
    //zhl 更新 存取 贴子对应的图片
    private ArrayList<BmobFile>topicPictureArray;
    //pjh 存取 帖子的内容
    private ArrayList<String> topicContentArray;


    //每次刷新GridView都要从 云端数据库 读取 现在已有的帖子的标题以及其对应的作者，这些已有的帖子的标题以及其对应的作者加载到dataList中
    private List<Map<String,Object>> dataList;

    //GridView要加载的数据全部存储在simpleAdapter里面
    private SimpleAdapter simpleAdapter;
    //加载帖子的帖子列表
    private GridView gridView;

    private Button buttonWriteTopic;

    private LinearLayout linearLayoutTop;
    private TextView textViewTopTopicUserName;
    private TextView textViewTopTopicTitle;


    //private MyImageView ImageTopicPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_list);

        gridView=findViewById(R.id.grid_view_topic_list);
        gridView.setOnItemClickListener(this);

        textViewTopicListTitle=findViewById(R.id.textview_topic_list);
        sectionName=getIntent().getStringExtra("Section");
        textViewTopicListTitle.setText(sectionName+"版块下的所有帖子");

        buttonWriteTopic=findViewById(R.id.write_topic);
        buttonWriteTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TopicListActivity.this,WriteTopicActivity.class);
                intent.putExtra("Section",sectionName);
                startActivity(intent);
            }
        });
        linearLayoutTop=findViewById(R.id.top_linearlayout);
        textViewTopTopicUserName=findViewById(R.id.top_topic_user_name);
        textViewTopTopicTitle=findViewById(R.id.top_topic_title);

        //置顶版块的点击事件
        linearLayoutTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopicListActivity.this, TopicAndReplyActivity.class);
                intent.putExtra("topicTitle",textViewTopTopicTitle.getText().toString());
                startActivity(intent);
                linearLayoutTop.setVisibility(View.GONE);
            }
        });

    }

    public void refreshGridView(){
        //new ArrayList<>()是清空dataList的数据,每次刷新GridView都要从 云端数据库 读取 现在已有的版块，这些已有的版块加载到dataList中
        dataList=new ArrayList<>();
        //清空老版本的数据读取新的数据
        topicTitleArray=new ArrayList<>();
        topicUserNameArray=new ArrayList<>();
        //zhl新增
        topicPictureArray=new ArrayList<>();
        topicContentArray=new ArrayList<>();

        BmobQuery<Topic> topicBmobQuery=new BmobQuery<>();
        //查询 topicName列 下不为空的所有数据
        topicBmobQuery.addWhereEqualTo("sectionName",sectionName);
        //查询数据最多只显示查询到的前50条数据,如果不加上这条语句，默认返回10条数据
        topicBmobQuery.setLimit(50);
        topicBmobQuery.findObjects(new FindListener<Topic>() {
            @Override
            public void done(List<Topic> list, BmobException e) {
                if(e==null){
                    for(final Topic topic:list){
                        topicTitleArray.add(topic.getTitle());
                        topicUserNameArray.add(topic.getUserName());
                        //zhl新增
                        topicPictureArray.add(topic.getBmobFile());
                        //pjh
                        topicContentArray.add(topic.getContent());
                        Map<String,Object> map=new HashMap<>();
                        map.put("topicTitle",topic.getTitle());
                        map.put("topicUserName",topic.getUserName());
                        map.put("topicContent",topic.getContent());
                        //zhl新增
                        if(topic.getBmobFile()!=null) {
                            final Bitmap[] bitmap = new Bitmap[1];
                            new Thread(new Runnable(){
                                @Override
                                public void run() {
                                    bitmap[0] = getBitMBitmap(topic.getBmobFile().getUrl());
                                }
                            }).start();
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            map.put("topicPictureUrl", bitmap[0]);
                        }
                        dataList.add(map);
                    }
//                    simpleAdapter = new SimpleAdapter(TopicListActivity.this, dataList, R.layout.gridview_item_topic_list, new String[]{"topicTitle", "topicUserName", "topicPictureUrl"}, new int[]{R.id.topic_title, R.id.topic_user_name, R.id.all_picture});
                    simpleAdapter = new SimpleAdapter(TopicListActivity.this, dataList, R.layout.gridview_item_topic_list, new String[]{"topicTitle", "topicUserName","topicContent"}, new int[]{R.id.topic_title, R.id.topic_user_name,R.id.topic_content});
                    //实现ViewBinder接口，对两种资源进行说明
                    simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder(){

                        @Override
                        public boolean setViewValue(View view, Object data, String textRepresentation) {
                            if((view instanceof ImageView) && (data instanceof Bitmap)) {
                                ImageView imageView = (ImageView) view;
                                Bitmap bmp = (Bitmap) data;
                                imageView.setImageBitmap(bmp);
                                return true;
                            }
                            return false;
                        }
                    });

                    gridView.setAdapter(simpleAdapter);

                }else {
                    Toast.makeText(TopicListActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //刷新被置顶的帖子
    public void refreshTopTopic(){
        BmobQuery<Topic> topicBmobQuery=new BmobQuery<>();
        topicBmobQuery.addWhereEqualTo("sectionName",sectionName);
        topicBmobQuery.addWhereEqualTo("topPriority",1);
        topicBmobQuery.findObjects(new FindListener<Topic>() {
            @Override
            public void done(List<Topic> list, BmobException e) {
                if(e==null){
                    //如果当前版块存在置顶帖子，那么不再隐藏置顶帖子的布局
                    if(list.size()!=0){
                        linearLayoutTop.setVisibility(View.VISIBLE);
                    }
                    textViewTopTopicUserName.setText(list.get(0).getUserName());
                    textViewTopTopicTitle.setText(list.get(0).getTitle());
                }else {
                    Toast.makeText(TopicListActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //GridView的子项点击事件 把被点击的话题的标题内容传输到下一个活动中
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        //获取GridView中被点击的的子item的view
        LinearLayout linearLayout=(LinearLayout) gridView.getAdapter().getView(i,view,null);
        //获取 gridview_item_topic_list.xml第二个子view
        TextView topicTitle=(TextView)linearLayout.getChildAt(1);
        //zhl新增
        TextView topicPictureUrl=(TextView) linearLayout.getChildAt(2);


        Intent intent = new Intent(TopicListActivity.this, TopicAndReplyActivity.class);
        intent.putExtra("topicTitle",topicTitle.getText().toString());
        //zhl新增
        intent.putExtra("topicPicture",topicPictureUrl.getText().toString());

        startActivity(intent);

        linearLayoutTop.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  refreshTopTopic();
        refreshGridView();
    }


    /**通过图片url生成Bitmap对象
     * @param urlpath
     * @return Bitmap
     * 根据图片url获取图片对象
     */
    public static Bitmap getBitMBitmap(String urlpath) {
        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


}

