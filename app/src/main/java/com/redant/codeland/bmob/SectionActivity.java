package com.redant.codeland.bmob;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.redant.codeland.R;
import com.redant.codeland.entity.Bmob.Section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class SectionActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    //本地 gridview每一个子项 版块的名字 的 数组
    private ArrayList<String> sectionNameArray;

    private GridView gridView;
    //每次刷新GridView都要从 云端数据库 读取 现在已有的版块，这些已有的版块加载到dataList中
    private List<Map<String,Object>> dataList;
    //GridView要加载的数据全部存储在simpleAdapter里面
    private SimpleAdapter simpleAdapter;

    //添加版块的按钮
    private Button search_button;
    //删除版块的按钮
//    private Button buttonDeleteSection;
    //编辑 版块名 的EditText
    private EditText editTextSection;

    public int count=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);

        gridView=findViewById(R.id.grid_view_section);
        gridView.setOnItemClickListener(this);

        search_button=findViewById(R.id.search_button);
//        buttonDeleteSection=findViewById(R.id.delete_section);
        editTextSection=findViewById(R.id.edit_text_section);

        //添加版块按钮的点击事件
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //读取当前用户的权限，如果用户不是版主无法进行版块管理
                //int currentUserControlLevel= BmobUser.getCurrentUser(User.class).getControlLevel();
//                if(currentUserControlLevel!=1){
//                    Toast.makeText(SectionActivity.this,"您不是版主，没有管理板块的权限哦",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (TextUtils.isEmpty(editTextSection.getText().toString().trim())) {
//                    Toast.makeText(SectionActivity.this,"版块名不能为空，请重新输入", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                //板块名字不可以超过4个汉字
//                if(editTextSection.getText().toString().trim().length()>4){
//                    Toast.makeText(SectionActivity.this,"板块名字不可以超过4个汉字哦", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                for(int count=0;count<sectionNameArray.size();count++) {
                    //如果编辑框中的版块名 与 云端数据库中已经存在的板块名相同冲突了，那么给用户提示，并return不再执行 把这个版块名保存到数据库的操作
                    if (editTextSection.getText().toString().trim().equals(sectionNameArray.get(count))) {
                        Toast.makeText(SectionActivity.this, "该版块已经存在了，无法再次添加喔", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Section section=new Section();
                section.setSectionName(editTextSection.getText().toString().trim());
                section.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Toast.makeText(SectionActivity.this,"板块创建成功", Toast.LENGTH_SHORT).show();
                            refreshGridView();
                        }else {
                            Toast.makeText(SectionActivity.this,"错在这里"+e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("section",e.toString());
                        }
                    }
                });


            }
        });

//        //删除版块按钮的点击事件
//        buttonDeleteSection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (TextUtils.isEmpty(editTextSection.getText().toString().trim())) {
//                    Toast.makeText(SectionActivity.this,"版块名不能为空，请重新输入", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                //板块名字不可以超过4个汉字
//                if(editTextSection.getText().toString().trim().length()>4){
//                    Toast.makeText(SectionActivity.this,"板块名字不可以超过4个汉字哦", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                for(int count=0;count<sectionNameArray.size();count++) {
//                    //如果要删除的 编辑框中的版块名 已存在 云端数据，直接break跳出去
//                    if (editTextSection.getText().toString().trim().equals(sectionNameArray.get(count))) {
//                        break;
//                    }
//                    //如果已经查询到最后一个云端版块名还没有跳出，说明云端数据库没有要删除的该板块，return禁止删除操作并提示用户
//                    else if(count==sectionNameArray.size()-1){
//                        Toast.makeText(SectionActivity.this,"你要删除的版块不存在哦", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
//
//                BmobQuery<Section> sectionBmobQuery=new BmobQuery<>();
//                sectionBmobQuery.addWhereEqualTo("sectionName",editTextSection.getText().toString().trim());
//                sectionBmobQuery.findObjects(new FindListener<Section>() {
//                    @Override
//                    public void done(List<Section> list, BmobException e) {
//                        if(e==null){
//
//                            Section sectionDelete=new Section();
//                            sectionDelete.setObjectId(list.get(0).getObjectId());
//                            sectionDelete.delete(new UpdateListener() {
//                                @Override
//                                public void done(BmobException e) {
//                                    if(e==null){
//                                        Toast.makeText(SectionActivity.this,"版块删除成功", Toast.LENGTH_SHORT).show();
//                                        refreshGridView();
//                                    }else {
//                                        Toast.makeText(SectionActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        }
//                    }
//                });
//
//
//            }
//        });

        //刚打开界面 加载GridView
        refreshGridView();

    }

    //加载或者刷新GridView的页面
    public void refreshGridView(){
        //new ArrayList<>()是清空dataList的数据,每次刷新GridView都要从 云端数据库 读取 现在已有的版块，这些已有的版块加载到dataList中
        dataList=new ArrayList<>();
        //清空老版本的数据读取新的数据
        sectionNameArray=new ArrayList<>();

        BmobQuery<Section> sectionBmobQuery=new BmobQuery<>();
        //查询 sectionName列 下不为空的所有数据
        sectionBmobQuery.addWhereExists("sectionName");
        //查询数据最多只显示查询到的前50条数据,如果不加上这条语句，默认返回10条数据
        sectionBmobQuery.setLimit(50);
        sectionBmobQuery.findObjects(new FindListener<Section>() {
            @Override
            public void done(List<Section> list, BmobException e) {
                if(e==null){
                    for(Section section:list){
                        sectionNameArray.add(section.getSectionName());
                        Map<String,Object> map=new HashMap<>();
                        map.put("SectionName",section.getSectionName());
                        dataList.add(map);
                    }
                    simpleAdapter=new SimpleAdapter(SectionActivity.this,dataList,R.layout.gridview_item_section,new String[]{"SectionName"},new int[]{R.id.textview_gridview_item_section});
                    gridView.setAdapter(simpleAdapter);
                }else {
                    Toast.makeText(SectionActivity.this,"这里出错了："+e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //GridView中每一个子项（版块）的点击事件
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        FrameLayout frameLayout=(FrameLayout) gridView.getAdapter().getView(i,view,null);
        TextView textView=(TextView)frameLayout.getChildAt(0);

        Toast.makeText(SectionActivity.this,"你选中了 "+textView.getText().toString()+" 版块",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SectionActivity.this, TopicListActivity.class);
        intent.putExtra("Section",textView.getText().toString());
        startActivity(intent);
    }
}
