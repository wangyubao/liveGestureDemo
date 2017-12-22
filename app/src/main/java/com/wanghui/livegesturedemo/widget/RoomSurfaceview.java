package com.wanghui.livegesturedemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.wanghui.livegesturedemo.Utils.Constants;


/**
 * Created by dell on 2016/10/25.
 */

public class RoomSurfaceview extends SurfaceView {
    private int videoWidth = Constants.VIDEO_WIDTH_WEAK;
    private int videoHeight = Constants.VIDEO_HEIGHT_WEAK;
    private String mUrl;
    private boolean isCreate = true;

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setCreate(boolean create) {
        isCreate = create;
    }

    public String getUrl() {
        return mUrl;
    }

    public boolean isCreate() {
        return isCreate;
    }

    public RoomSurfaceview(Context context) {
        super(context);
    }

    public RoomSurfaceview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoomSurfaceview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(videoWidth, widthMeasureSpec);
        int height = getDefaultSize(videoHeight, heightMeasureSpec);
        if (videoWidth > 0 && videoHeight > 0) {
            if (videoWidth * height > width * videoHeight) {
                height = width * videoHeight / videoWidth;
            } else if (videoWidth * height < width * videoHeight) {
                width = height * videoWidth / videoHeight;
            }
        }
        setMeasuredDimension(width, height);
    }

    public void setOrientation(int orientation) {
        if (orientation == 0) {//0:竖屏：1：横屏
            videoWidth = Constants.VIDEO_WIDTH_WEAK;
            videoHeight = Constants.VIDEO_HEIGHT_WEAK;
        } else {
            videoWidth = Constants.VIDEO_HEIGHT_WEAK;
            videoHeight = Constants.VIDEO_WIDTH_WEAK;
        }
    }
}
