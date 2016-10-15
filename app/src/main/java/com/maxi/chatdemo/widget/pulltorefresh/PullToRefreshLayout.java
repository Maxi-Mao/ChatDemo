package com.maxi.chatdemo.widget.pulltorefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maxi.chatdemo.utils.ScreenUtil;

/**
 * Created by Mao Jiqing on 2016/9/27.
 */
public class PullToRefreshLayout extends LinearLayout {
    private ViewDragHelper VDH;
    private View myList;
    private TextView pullText;
    private pulltorefreshNotifier pullNotifier;
    private boolean isPull = true;

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
//        init();
        VDH = ViewDragHelper.create(this, 10.0f, new DragHelperCallback());
    }

    public void setSlideView(View view) {
        init(view);
    }

    private void init(View view) {
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        myList = view;
        myList.setBackgroundColor(Color.parseColor("#FFFFFF"));
        myList.setLayoutParams(lp1);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(getContext(), 100));
        pullText = new TextView(getContext());
        pullText.setText("下拉加载更多");
        pullText.setBackgroundColor(Color.parseColor("#FFFFFF"));
        pullText.setGravity(Gravity.CENTER);
        pullText.setLayoutParams(lp2);
        setOrientation(LinearLayout.VERTICAL);
        addView(pullText);
        addView(myList);
    }

    public View returnMylist() {
        return myList;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(
                resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (pullText.getTop() == 0) {
            viewHeight = pullText.getMeasuredHeight();
            pullText.layout(l, 0, r, viewHeight);
            myList.layout(l, 0, r, b);
            pullText.offsetTopAndBottom(-viewHeight);
        } else {
            pullText.layout(l, pullText.getTop(), r, pullText.getBottom());
            myList.layout(l, myList.getTop(), r, myList.getBottom());
        }
    }

    /**
     * 这是View的方法，该方法不支持android低版本（2.2、2.3）的操作系统，所以手动复制过来以免强制退出
     */
    public static int resolveSizeAndState(int size, int measureSpec,
                                          int childMeasuredState) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                if (specSize < size) {
                    result = specSize | MEASURED_STATE_TOO_SMALL;
                } else {
                    result = size;
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result | (childMeasuredState & MEASURED_STATE_MASK);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean shouldIntercept = VDH.shouldInterceptTouchEvent(event) && isPull;
        return shouldIntercept;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        VDH.processTouchEvent(event);
        return true;
    }

    /**
     * 这是拖拽效果的主要逻辑
     */
    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            int childIndex = 1;
            if (changedView == myList) {
                childIndex = 2;
            }
            onViewPosChanged(childIndex, top);
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return 1;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            refreshOrNot(releasedChild, yvel);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int finalTop = top;
            if (child == pullText) {
                if (top > 0) {
                    finalTop = 0;
                }
            } else if (child == myList) {
                if (top < 0) {
                    finalTop = 0;
                }
                if (top >= viewHeight) {
                    pullText.setText("松开加载");
                } else {
                    pullText.setText("下拉加载更多");
                }
            }
            return child.getTop() + (finalTop - child.getTop()) / 2;
        }
    }

    /**
     * 滑动时view位置改变协调处理
     *
     * @param viewIndex
     * 滑动view的index(1或2)
     * @param posTop
     * 滑动View的top位置
     */
    private static int viewHeight;

    private void onViewPosChanged(int viewIndex, int posTop) {
        if (viewIndex == 1) {
            int offsetTopBottom = viewHeight + pullText.getTop()
                    - myList.getTop();
            myList.offsetTopAndBottom(offsetTopBottom);
        } else if (viewIndex == 2) {
            int offsetTopBottom = myList.getTop() - viewHeight
                    - pullText.getTop();
            pullText.offsetTopAndBottom(offsetTopBottom);
        }
        invalidate();
    }

    private void refreshOrNot(View releasedChild, float yvel) {
        int finalTop = 0;
        if (releasedChild == pullText) {
            // 拖动第一个view松手
            if (yvel < -50) {
                finalTop = 0;
            } else {
                finalTop = viewHeight;
            }
        } else {
            // 拖动第二个view松手
            if (yvel > viewHeight - 5 || releasedChild.getTop() >= viewHeight) {
                finalTop = viewHeight;
                if (null != pullNotifier) {
                    pullNotifier.onPull();
                }
                pullText.setText("正在加载");
            }
        }

        if (VDH.smoothSlideViewTo(myList, 0, finalTop)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void refreshComplete() {
        if (VDH.smoothSlideViewTo(myList, 0, 0)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void computeScroll() {
        if (VDH.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setpulltorefreshNotifier(pulltorefreshNotifier pullNotifier) {
        this.pullNotifier = pullNotifier;
    }

    public interface pulltorefreshNotifier {
        public void onPull();
    }

    /**
     * 禁止下拉
     */
    public void setPullGone() {
        isPull = false;
    }
}
