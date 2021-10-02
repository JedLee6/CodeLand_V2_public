package com.redant.codeland.bmob;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.redant.codeland.R;
import com.redant.codeland.adapter.CodingAdapter;
import com.redant.codeland.entity.Bmob.Topic;
import com.redant.codeland.entity.Coding;
import com.redant.codeland.ui.CodingBaseActivity_test;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class  WriteTopicActivity extends AppCompatActivity {

    private TextView textViewTitleHint;
    private String sectionName;

    private Button buttonPostTopic;

    private EditText editTextTopic;
    private EditText editTextContent;

    //zhl新增
    //添加图片
    private  Button addbutton;

    //添加代码
    private  Button codeButton;


    private ImageView picture;


    public static final int CHOOSE_PHOTO=2;
    //对肮脏、辱骂的词汇进行屏蔽
    private ArrayList<String> dirtyWords;

    //new一个全局的对象，以便在不同功能函数中添加
    private Topic t=new Topic();

    private CodingAdapter adapter;
    private List<Coding> savingRecordList=new ArrayList<>();
    private LinearLayout recordLayout;
    private ListView listView;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_topic);

        textViewTitleHint=findViewById(R.id.textview_write_topic_title);

        sectionName=getIntent().getStringExtra("Section");
        textViewTitleHint.setText(sectionName+"版块下发表帖子");

        editTextTopic=findViewById(R.id.edit_text_write_topic_title);
        editTextContent=findViewById(R.id.edit_text_write_topic_content);
        addbutton=findViewById(R.id.add_button);
        codeButton=findViewById(R.id.add_code);
        picture = (ImageView) findViewById(R.id.add_pic);
        recordLayout=findViewById(R.id.record_layout);
        listView=findViewById(R.id.record_list_1);
        initRecord();
        adapter=new CodingAdapter(WriteTopicActivity.this, R.layout.record_item , savingRecordList);
        listView.setAdapter(adapter);
        //添加图片
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(WriteTopicActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(WriteTopicActivity.this,new String []{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                }else {
                    openAlbum();
                }
            }
        });
        //添加代码
        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRecord();
            }
        });

        //发表帖子的点击事件
        buttonPostTopic=findViewById(R.id.post_topic);
        buttonPostTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(editTextTopic.getText().toString().trim())){
                    Toast.makeText(WriteTopicActivity.this,"你的话题内容为空哦", Toast.LENGTH_SHORT).show();
                    return;
                }

                //敏感词汇进行屏蔽，含有敏感词汇无法发表话题
                dirtyWords=new ArrayList<>();
                dirtyWords.add("艹");
                dirtyWords.add("你妈");
                for (int i = 0; i < dirtyWords.size(); i++) {
                    if((editTextTopic.getText().toString().trim()).contains(dirtyWords.get(i))){
                        Toast.makeText(WriteTopicActivity.this,"帖子的标题或内容包括敏感词汇哦，请重新输入",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                t.setUserName( BmobUser.getCurrentUser().getUsername());
                t.setSectionName(sectionName);
                t.setTitle(editTextTopic.getText().toString().trim());
                t.setContent(editTextContent.getText().toString().trim());
                t.setCode(code);
                //t.setBmobFile(bmobFile);
                t.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Toast.makeText(WriteTopicActivity.this,"话题发表成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }   else {
                            Toast.makeText(WriteTopicActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //判断本次要发表的 话题内容 之前是否已经有人发表过了
//                final BmobQuery<Topic> topicBmobQuery=new BmobQuery<>();
//                topicBmobQuery.addWhereExists("title");
//                topicBmobQuery.findObjects(new FindListener<Topic>() {
//                    @Override
//                    public void done(List<Topic> list, BmobException e) {
//                        if(e==null){
//                            for(Topic topic:list){
//                                //本次要发表的 话题内容 之前已经有人发表过了
//                                if(topic.getTitle().equals(editTextTopic.getText().toString().trim())){
//                                    Toast.makeText(WriteTopicActivity.this,"该话题已经有人发表过了哦，换一个话题吧", Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
//                            }
//                            //Topic topic=new Topic();
//                            t.setUserName( BmobUser.getCurrentUser().getUsername());
//                            t.setSectionName(sectionName);
//                            t.setTitle(editTextTopic.getText().toString().trim());
//                            //t.setBmobFile(bmobFile);
//                            t.save(new SaveListener<String>() {
//                                @Override
//                                public void done(String s, BmobException e) {
//                                    if(e==null){
//                                        Toast.makeText(WriteTopicActivity.this,"话题发表成功", Toast.LENGTH_SHORT).show();
//                                        finish();
//                                    }   else {
//                                        Toast.makeText(WriteTopicActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//
//                        }else {
//                            Toast.makeText(WriteTopicActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
            }
        });
    }
    private void loadRecord(){
        adapter.notifyDataSetChanged();
        recordLayout.setVisibility(View.VISIBLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Coding savingRecord=savingRecordList.get(position);
                //recordText.setText(savingRecord.getName());
                code=savingRecord.getCode();
                //Toast.makeText(CodingBaseActivity_test.this,workspaceLoadPath,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initRecord(){
        List<Coding> savingRecords= DataSupport.findAll(Coding.class);
        savingRecordList.clear();
        if(savingRecords.size()>0){
            for(Coding record:savingRecords){
                savingRecordList.add(record);
            }
        }
    }
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
            upload(imagePath);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }


    // 图片上传
    private void upload(String imgpath) {
        final BmobFile bmobFile = new BmobFile(new File(imgpath));
        t.setBmobFile(bmobFile);
        t.getBmobFile().uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    Toast.makeText(WriteTopicActivity.this ,"上传文件成功:" + bmobFile.getFileUrl(),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(WriteTopicActivity.this,"上传文件失败：" + e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });
    }

}
