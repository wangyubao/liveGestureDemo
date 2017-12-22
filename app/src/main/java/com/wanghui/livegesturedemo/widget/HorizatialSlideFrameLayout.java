package com.wanghui.livegesturedemo.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.wanghui.livegesturedemo.Utils.ScreenUtils;

/**
 * Created by liqiang on 2016/1/16.
 */
public class HorizatialSlideFrameLayout extends FrameLayout {
    private Context mContext;
    private Scroller mScroller;
    private int mScreenWidth = 0;

    private int mScreenHeigh = 0;

    private int mLastDownY = 0;
    private int mLastDownX = 0;
    private int mCurryY;
    private int mCurryX;
    private int mDelY;
    private int mDelX;

    private boolean isMoving;
    private boolean isShow = true;
    private float mLastX;
    private float mLastY;
    private float mY;
    private float mX;

    private int part = 2;
    private VelocityTracker mVelocityTracker;
    private int slideTime = 380;
    /**
     * 认为是用户滑动的最小距离
     */
    private int mTouchSlop;
    private int mFling;

    public HorizatialSlideFrameLayout(Context context) {
        super(context);
        mContext = context;
        setupView();
    }

    public HorizatialSlideFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setupView();
    }

    public HorizatialSlideFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setupView();
    }

    private void setupView() {
//        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        mScroller = new Scroller(mContext);
        mScreenWidth = ScreenUtils.getHasVirtualKeyWidth(mContext);
        mScreenHeigh = ScreenUtils.getHasVirtualKeyHeight(mContext);
        // 这里你一定要设置成透明背景,不然会影响你看到底层布局
        this.setBackgroundColor(Color.argb(0, 0, 0, 0));
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mFling = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity() * 20;
    }

    // 推动门的动画
    public void startBounceAnim(int startX, int dx, int duration) {
        invalidate();
        mScroller.startScroll(startX, 0, dx, 0, duration);
//        LogUtil.i("slidehorizatialqqqq", startX + "______" + dx);
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    private void addVelocityTrackerEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);
    }

    // 获得横向的手速
    private int getTouchVelocityX() {
        if (mVelocityTracker == null)
            return 0;
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mVelocityTracker!=null){
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker=null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        addVelocityTrackerEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastDownX = (int) event.getX();
                mLastDownY = (int) event.getY();
                mLastX = event.getX();
                mLastY = event.getY();
                if (mOnKeyBoardListening != null) {
                    mOnKeyBoardListening.hideKeyBoardListening();
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                mCurryY = (int) event.getY();
                mCurryX = (int) event.getX();
                mDelY = mCurryY - mLastDownY;
                mDelX = mCurryX - mLastDownX;
                mY = event.getY() - mLastY;
                mX = event.getX() - mLastX;

                if (Math.abs(mY) < Math.abs(mX) && Math.abs(mX) > mTouchSlop) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    if (mDelX > 0) {
                        if (isShow) {
                            scrollTo(-mDelX, 0);
                            if (mOnSlideFinishListening != null)
                                mOnSlideFinishListening.onSlideScrolled(-mDelX);
//                            LogUtil.i("slidehorizatialqqqqisshow", -mDelX + "++++++");
                        }
                    } else {
                        if (!isShow) {
                            scrollTo(-mScreenWidth - mDelX, 0);
                            if (mOnSlideFinishListening != null)
                                mOnSlideFinishListening.onSlideScrolled(-mScreenWidth - mDelX);
//                            LogUtil.i("slidehorizatialqqqqnoshow", -mScreenWidth - mDelX + "++++++");
                        }
                    }
                    return true;
                } else {
                    return super.onTouchEvent(event);
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mCurryX = (int) event.getX();
                mDelX = mCurryX - mLastDownX;
//                LogUtil.i("slidehorizatialqqqqcancel", this.getScrollX() + "___" + mDelX + "____" + mCurryX);
                if (Math.abs(mDelX) > mScreenWidth / part || getTouchVelocityX() >= mFling) {

                    // 向上滑动超过半个屏幕高的时候 开启向上消失动画
                    if (mDelX > 0) {
                        if (Math.abs(this.getScrollX()) < mScreenWidth) {
                            startBounceAnim(this.getScrollX(), Math.abs(this.getScrollX()) - mScreenWidth, Math.abs(Math.abs(this.getScrollX()) - mScreenWidth) * slideTime / mScreenWidth);
                            isShow = false;
                            if (mOnSlideFinishListening != null) {
                                mOnSlideFinishListening.slidefinish(isShow);
                            }
                        }
                    } else {
                        if (!isShow) {
                            startBounceAnim(this.getScrollX(), -this.getScrollX(), Math.abs(this.getScrollX() * slideTime / mScreenWidth));
                            isShow = true;
                            if (mOnSlideFinishListening != null) {
                                mOnSlideFinishListening.slidefinish(isShow);
                            }
                        } else {
                            startBounceAnim(this.getScrollX(), -this.getScrollX(), Math.abs(this.getScrollX() * slideTime / mScreenWidth));
                            isShow = true;
                        }
                    }
                } else {
                    // 向上滑动未超过半个屏幕高的时候 开启向下弹动动画
                    if (mDelX > 0) {
                        if (isShow) {
                            startBounceAnim(this.getScrollX(), -this.getScrollX(), Math.abs(this.getScrollX() * slideTime / mScreenWidth));
                        } else {
                            scrollTo(-mScreenWidth - mDelX, 0);
                        }
                    } else if (mDelX == 0) {

                    } else {
                        if (!isShow) {
                            startBounceAnim(this.getScrollX(), this.getScrollX() - mScreenWidth, Math.abs(this.getScrollX() - mScreenWidth) * slideTime / mScreenWidth);
                        } else {
                            startBounceAnim(this.getScrollX(), -this.getScrollX(), Math.abs(this.getScrollX() * slideTime / mScreenWidth));
                        }
                    }
                }
                return super.onTouchEvent(event);
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
//            LogUtil.i("slidehorizatialqqqqcom", mScroller.getCurrX() + "__" + mScroller.getCurrY());
            if (mOnSlideFinishListening != null) {
                mOnSlideFinishListening.onSlideScrolled(mScroller.getCurrX());
            }
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // 更新界面
            postInvalidate();
            isMoving = true;
        } else {
            isMoving = false;
        }
        super.computeScroll();
    }

    public void setOrientation() {
        mScreenWidth = ScreenUtils.getHasVirtualKeyWidth(mContext);
        mScreenHeigh = ScreenUtils.getHasVirtualKeyHeight(mContext);
        if (mScreenHeigh < mScreenWidth) {
            slideTime = 550;
        } else {
            slideTime = 380;
        }
    }

    public interface OnSlideFinishListening {
        void slidefinish(boolean isShow);

        void onSlideScrolled(int offset);
    }

    OnSlideFinishListening mOnSlideFinishListening = null;

    public void setOnSlideFinishListening(OnSlideFinishListening e) {
        mOnSlideFinishListening = e;
    }

    public interface OnKeyBoardListening {
        void hideKeyBoardListening();
    }

    OnKeyBoardListening mOnKeyBoardListening = null;

    public void setOnKeyBoardListening(OnKeyBoardListening e) {
        mOnKeyBoardListening = e;
    }

    public void setPart(int part) {
        this.part = part;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastDownY = (int) event.getY();
                mLastDownX = (int) event.getX();
                mLastX = event.getX();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mY = event.getY() - mLastY;
                mX = event.getX() - mLastX;
                if (Math.abs(mY) < Math.abs(mX) && Math.abs(mX) > mTouchSlop - 2) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                }
                break;
        }
        return false;
    }

    public void reShow(boolean isShowing, int scrollX) {
        isShow = isShowing;
        if (isShowing) {
            startBounceAnim(scrollX, -scrollX, 0);
        } else {
            startBounceAnim(scrollX, -(scrollX + mScreenWidth), 0);
        }
    }
}
