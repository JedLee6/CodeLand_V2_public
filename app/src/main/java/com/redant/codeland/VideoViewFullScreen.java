package com.redant.codeland;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by jedlee on 2018/3/19.
 */

public class VideoViewFullScreen extends VideoView {

    public VideoViewFullScreen(Context context) {
        super(context);
    }

    public VideoViewFullScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoViewFullScreen(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}