package com.redant.codeland;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;


/**
 * 实现View对象，响应陀螺仪改变事件
 * 达到类似IOS那种背景图片移动的效果
 * 效果见提分宝选择科目模块
 * <p/>
 * **使用方式，初始化：{ #ParallelViewHelper(Context, View, int)}
 * 调用：
 * 在onResume里使用{@link #start()}
 * 在onPause里使用{@link #stop()}
 * Created by 陈俊杰 on 2015/12/9.
 */
public class ParallelViewHelper implements GyroScopeSensorListener.ISensorListener {

    /**
     * 默认0.02f在宽度填满屏幕的图片上，移动起来看着很舒服
     */
    //TRANSFORM_FACTOR越大，移动的加速度就越快，晃得厉害，但是移动距离不变
    public static final float TRANSFORM_FACTOR = 0.02f;
    private float mTransformFactor = TRANSFORM_FACTOR;

    private View mParallelView;
    private float mCurrentShiftX;
    private float mCurrentShiftY;
    //gyroscope的监听器
    private GyroScopeSensorListener mSensorListener;

    private ViewGroup.LayoutParams mLayoutParams;
    private int mViewWidth;
    private int mViewHeight;

    //shiftDistancePX越大，摇晃时图片移动的距离就越大
    private int mShiftDistancePX;
    //俊德新加的，新建一个类字段context，以便获取当前context中手机屏幕旋转方向
    private Context context;
    //俊德新加的， 加载进来的布局 改变(x,y),width,height的风格，即你要不要改成这样
    //目前 0 默认 “ios视差背景”风格，(x,y)偏移 width,height放大  作用：确保“视差背景”完美显示
    //1 代表 “普通视差”风格 (x,y)在原来的位置 width,height保持不变 作用：可自定义一些button 漂移，但是不改变大小和x,y位置
    private int pictureChangeStyle;

    private float originalX;
    private float originalY;

    public ParallelViewHelper(Context context, final View targetView) {
        //如果只是传2个参数，那么调用this自己本身，但是自己自动多加入二个参数-摇晃时图片移动的距离（value中定义）,图片风格（默认0）
        //注意这里有 dp 转 int,转换公式为 int=dp*density(屏幕密度)
        this(context, targetView, 40,0);
        // 获取屏幕密度（方法2）
        }

    /**
     * 初始化一个
     *
     * @param context
     * @param targetView
     * @param shiftDistancePX
     */
    //shiftDistancePX越大，摇晃时图片移动的距离就越大
    //注意这里有 dp 转 int,转换公式为 int=dp*density(屏幕密度)
    public ParallelViewHelper(Context context, final View targetView, int shiftDistancePX, int pictureChangeStyle) {

        //俊德新加的，以便获取当前context中手机屏幕旋转方向
        this.context=context;
        this.pictureChangeStyle=pictureChangeStyle;

        if(pictureChangeStyle==0)
        {
            //上面的this已经做好了，我们不用乘以 density（屏幕密度）
            mShiftDistancePX = shiftDistancePX;
        }
            //自定义传入的值没有 乘以 density（屏幕密度）
            DisplayMetrics dm = new DisplayMetrics();
            dm = context.getResources().getDisplayMetrics();
            int density  = (int)dm.density;
            mShiftDistancePX = shiftDistancePX*density;

        mSensorListener = new GyroScopeSensorListener(context);
        mSensorListener.setSensorListener(this);
        mParallelView = targetView;

        //如果在输出x,y参数，是全为0，要等UI控件都加载完了才能获取到这些
        originalX=targetView.getLeft();
        originalY=mParallelView.getY();



        //“ios视差背景”风格 才这样
        if(pictureChangeStyle==0)
        {
            mParallelView.setX(-mShiftDistancePX);
            mParallelView.setY(-mShiftDistancePX);
        }

        mLayoutParams = mParallelView.getLayoutParams();
        mViewWidth = mParallelView.getWidth();
        mViewHeight = mParallelView.getHeight();

        if (mViewWidth > 0 && mViewHeight > 0) {
            bindView();
            return;
        }

        //等UI控件都加载完了才能获取控件XY
        ViewTreeObserver vto = targetView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                targetView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                originalX=targetView.getLeft();
                originalY=mParallelView.getY();

                mViewWidth = targetView.getWidth();
                mViewHeight = targetView.getHeight();
                bindView();
            }
        });
    }

    void bindView() {
        //让传入的图片资源的 宽高 变大
        //就是因为 + mShiftDistancePX * 2 让“飞机”图片变大了
        if(pictureChangeStyle==0){
            //“ios视差背景”风格
            mLayoutParams.width = mViewWidth + mShiftDistancePX * 2;
            mLayoutParams.height = mViewHeight + mShiftDistancePX * 2;
        }
        else{
            //“普通视差”风格
            mLayoutParams.width = mViewWidth ;
            mLayoutParams.height = mViewHeight ;
        }

        mParallelView.setLayoutParams(mLayoutParams);
    }


    /**
     * 注册监听陀螺仪事件
     */
    public void start() {
        mSensorListener.start();
    }

    /**
     * 监听陀螺仪事件耗电，因此在onPause里需要注销监听事件
     */
    public void stop() {
        mSensorListener.stop();
    }

    /**
     * 设置移动的补偿变量，越高移动越快，标准参考{@link #TRANSFORM_FACTOR}
     *
     * @param transformFactor
     */
    public void setTransformFactor(float transformFactor) {
        mTransformFactor = transformFactor;
    }

    //Geroscope陀螺仪改变的时候（监听动作中）
    @Override
    public void onGyroScopeChange(float horizontalShift, float verticalShift) {

        //通过context获取当前屏幕旋转的方向，（竖屏/横屏），以对应的方式修改x,y轴
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            //横屏时这么写
            mCurrentShiftX += mShiftDistancePX * horizontalShift * mTransformFactor;
            mCurrentShiftY += mShiftDistancePX * verticalShift * mTransformFactor;
        }
        else {
            //竖屏时这么写
            mCurrentShiftY += mShiftDistancePX * horizontalShift * mTransformFactor;
            mCurrentShiftX += mShiftDistancePX * verticalShift * mTransformFactor;
        }

        if (Math.abs(mCurrentShiftX) > mShiftDistancePX)
            mCurrentShiftX = mCurrentShiftX < 0 ? -mShiftDistancePX : mShiftDistancePX;

        if (Math.abs(mCurrentShiftY) > mShiftDistancePX)
            mCurrentShiftY = mCurrentShiftY < 0 ? -mShiftDistancePX : mShiftDistancePX;

        //默认就margin 负的边距尺寸，因此 margin的最大值是 负的边距尺寸*2 ~ 0
        //就是因为 - mShiftDistancePX 让“飞机”图片漂移了
        if(pictureChangeStyle==0)
        {
            //“ios视差背景”风格
            mParallelView.setX((int) mCurrentShiftX - mShiftDistancePX);
            mParallelView.setY((int) mCurrentShiftY - mShiftDistancePX);
        }else{
            //“普通视差”风格
            mParallelView.setX((int) mCurrentShiftX+originalX);
            mParallelView.setY((int) mCurrentShiftY+originalY);
        }

    }


}
