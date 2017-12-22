package com.wanghui.livegesturedemo.widget;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.wanghui.livegesturedemo.R;


/**
 * Created by 王辉 on 2017/8/30.
 */

public class DragLayout extends RelativeLayout {
    private ViewDragHelper mDragHelper;
    private View mDragonView;

    public DragLayout(Context context) {
        super(context);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        /**
         * @params ViewGroup forParent 必须是一个ViewGroup
         * @params float sensitivity 灵敏度
         * @params Callback cb 回调
         */
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragCallback());
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {
        /**
         * 尝试捕获子view，一定要返回true
         * 这里可以决定哪个子view可以拖动
         */
        @Override
        public boolean tryCaptureView(View view, int pointerId) {
//          return mCanDragView == view;
            if (view.getId() == R.id.can_drag_layout) {
                mDragonView = view;
                return true;
            }
            return false;
        }

        /**
         * 处理水平方向上的拖动
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            System.out.println("left = " + left + ", dx = " + dx);

            // 两个if主要是为了让viewViewGroup里
            if(getPaddingLeft() > left) {
                return getPaddingLeft();
            }

            if(getWidth() - child.getWidth() < left) {
                return getWidth() - child.getWidth();
            }

            return left;
        }

        /**
         *  处理竖直方向上的拖动
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            // 两个if主要是为了让viewViewGroup里
            if(getPaddingTop() > top) {
                return getPaddingTop();
            }

            if(getHeight() - child.getHeight() < top) {
                return getHeight() - child.getHeight();
            }

            return top;
        }

        /**
         * 当拖拽到状态改变时回调
         * @params 新的状态
         */
        @Override
        public void onViewDragStateChanged(int state) {
            switch (state) {
                case ViewDragHelper.STATE_DRAGGING:  // 正在被拖动
                    Log.i("0000", "拖动中");
                    break;
                case ViewDragHelper.STATE_IDLE:  // view没有被拖拽或者 正在进行fling/snap
                    if (mDragonView != null) {
                        ViewGroup.LayoutParams layoutParams = mDragonView.getLayoutParams();
                        float x = mDragonView.getX();
                        float y = mDragonView.getY();
                        Log.i("DragLayout", x + " : " + y);
                    }
                    Log.i("0000", "拖动结束2");
                    break;
                case ViewDragHelper.STATE_SETTLING: // fling完毕后被放置到一个位置
                    Log.i("0000", "拖动结束");
                    break;
            }
            super.onViewDragStateChanged(state);
        }

        @Override
        public int getViewHorizontalDragRange(View child){
            return getMeasuredWidth()-child.getMeasuredWidth();
        }

        @Override
        public int getViewVerticalDragRange(View child){
            return getMeasuredHeight()-child.getMeasuredHeight();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_DOWN:
                mDragHelper.cancel(); // 相当于调用 processTouchEvent收到ACTION_CANCEL

                break;
        }

        /**
         * 检查是否可以拦截touch事件
         * 如果onInterceptTouchEvent可以return true 则这里return true
         */
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /**
         * 处理拦截到的事件
         * 这个方法会在返回前分发事件
         */
        mDragHelper.processTouchEvent(event);
        return true;
    }

}
