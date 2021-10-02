package com.redant.codeland.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.redant.codeland.R;
import com.redant.codeland.app.MyApplication;
import com.redant.codeland.entity.Baike;
import com.redant.codeland.util.CustomExpandListview;

import java.util.List;

/**
 * Created by Administrator on 2018-03-08.
 */

public class BaikeListAdapter extends BaseExpandableListAdapter implements CustomExpandListview.HeaderAdapter {
    private List<List<Baike>> mDataList;
    private Context mContext;
    private List<String> mGroupTitleList;
    private CustomExpandListview listview;
    //CHILD_TYPE_NORMAL用于标记 list的item是否为最后一个，这里我们用不到
    private static final int CHILD_TYPE_NORMAL = 0;
    private static final int CHILD_TYPE_CHALLENGE = 1;

    public BaikeListAdapter(Context context, List<List<Baike>> dataList, List<String> groupTitleList, CustomExpandListview listview) {
        mContext = context;
        mDataList = dataList;
        mGroupTitleList = groupTitleList;
        this.listview = listview;
    }

    //2018-4-10 俊德添加,用于设置ExpandableListView一级子项（目前共6个）的颜色，与EnlightenActivity中的6个模块颜色相对应
    private int[] colorForExpandableListViewArray={R.color.shallowGreen,R.color.shallowGreen,R.color.shallowGreen,
                                                    R.color.shallowGreen,R.color.shallowGreen,R.color.shallowGreen};
    //2018-4-10 俊德添加,记录上述int数组的下标
    private int colorOrderCounter=0;

    @Override
    public int getGroupCount() {
        return mDataList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mDataList.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return mDataList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return mDataList.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        GroupViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_baike_group, viewGroup,false);
            //convertView.setBackgroundColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(MyApplication.getContext(), R.color.shallowGreen))));
            convertView.setBackgroundColor(ContextCompat.getColor(MyApplication.getContext(), colorForExpandableListViewArray[colorOrderCounter]));
            colorOrderCounter++;
            if(colorOrderCounter==5){colorOrderCounter=0;}
            holder = new GroupViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder= (GroupViewHolder) convertView.getTag();
        String s = mGroupTitleList.get(i);
        holder.tvTitle.setText(s);
        switch (s.trim()){
            case "英语":
                convertView.setBackgroundColor(ContextCompat.getColor(MyApplication.getContext(),colorForExpandableListViewArray[0]));
                break;
            case "动物":
                convertView.setBackgroundColor(ContextCompat.getColor(MyApplication.getContext(),colorForExpandableListViewArray[1]));
                break;
            case "古诗":
                convertView.setBackgroundColor(ContextCompat.getColor(MyApplication.getContext(),colorForExpandableListViewArray[2]));
                break;
            case "三字经":
                convertView.setBackgroundColor(ContextCompat.getColor(MyApplication.getContext(),colorForExpandableListViewArray[3]));
                break;
            case "名人事迹":
                convertView.setBackgroundColor(ContextCompat.getColor(MyApplication.getContext(),colorForExpandableListViewArray[4]));
                break;
        }
        if(isExpanded){
            holder.ivArrow.setImageResource(R.mipmap.arrow_down);
        }else{
            holder.ivArrow.setImageResource(R.mipmap.arrow_right);
        }
        return convertView;
    }

    @Override
    public int getChildTypeCount() {
        return 2;
    }

    /**
     * 判断item的类型，每个group的最后一个为挑战item，其余为正常的百科知识item
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public int getChildType(int groupPosition, int childPosition) {
        if(childPosition == mDataList.get(groupPosition).size() -1){
            return CHILD_TYPE_CHALLENGE;
        }
        else{
            return CHILD_TYPE_NORMAL;
        }
    }

    @Override
    public View getChildView(final int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            //判断item的类型，根据不同的类型加载不同item布局
            //if(getChildType(i,i1) == CHILD_TYPE_NORMAL){
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.layout_baike_item, viewGroup,false);
            //}
//            else{
//                convertView = LayoutInflater.from(mContext)
//                        .inflate(R.layout.layout_baike_item_challege, viewGroup,false);
//            }
        }
        Baike baike = mDataList.get(i).get(i1);
        //if用于 判断是否为 最后一个 挑战按钮 还是普通的item，我们不要挑战按钮，去掉if即可
        //if(getChildType(i,i1) == CHILD_TYPE_NORMAL){
            TextView tv = convertView.findViewById(R.id.baike_list_item_title);
            tv.setText(baike.getTitle());
        //}
        //else后面是把最后一个按钮设置成为 挑战 按钮，去掉即可
//        else
//            {
//            Button btn = convertView.findViewById(R.id.baike_list_item_challenge);
//            btn.setText(baike.getTitle());
//            //为挑战按钮制定事件监听器
//            btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    switch (i){
//                        //定制的listview中有7个大列，因此按照0-6顺序进行排列
//                        //现在更新为6个大列，去掉“植物”，分别为 英语 动物 古诗 三字经 对联 名人事迹
//                        case 0:
//                            //古诗挑战
//                            //mContext.startActivity(new Intent(mContext, PoetryBaseActivity.class));
//                            break;
//                        case 4:
//                            //三字经挑战
//                            mContext.startActivity(new Intent(mContext, SanzijingBlocklyActivity.class));
//                            break;
//                        case 6:
//                            mContext.startActivity(new Intent(mContext, EnglishBlocklyActivity.class));
//                            break;
//                    }
//
//                }
//            });
//        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    class GroupViewHolder{
        public TextView tvTitle;
        public ImageView ivArrow;
        public GroupViewHolder(View itemView){
            tvTitle = itemView.findViewById(R.id.baike_list_group_title);
            ivArrow = itemView.findViewById(R.id.baike_list_group_arrow);
        }
    }
    public int getHeaderState(int groupPosition, int childPosition){
        final int childCount = getChildrenCount(groupPosition);
        if(childPosition == childCount - 1){
            return PINNED_HEADER_PUSHED_UP;
        }else if(childPosition == -1 && !listview.isGroupExpanded(groupPosition)){
            return PINNED_HEADER_GONE;
        }else {
            return PINNED_HEADER_VISIBLE;
        }
    }
    public void configureHeader(View header, int groupPosition, int childPosition, int alpha){
        if(groupPosition > -1){
            ((TextView) header.findViewById(R.id.tv_indictor))
                    .setText(mGroupTitleList.get(groupPosition));
        }
    }
}
