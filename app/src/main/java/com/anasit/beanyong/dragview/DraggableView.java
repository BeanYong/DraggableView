package com.anasit.beanyong.dragview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by BeanYong on 2015/12/9.
 */
public class DraggableView extends ViewGroup implements View.OnTouchListener, View.OnLongClickListener {

    /**
     * 要拖动的View
     */
    private View mTarget = null;
    /**
     * 记录可拖动View的位置范围
     */
    private int[] mTargetPoi = null;
    /**
     * 拖动开关
     */
    private boolean isDraggable;

    public DraggableView(Context context) {
        this(context, null);
    }

    public DraggableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DraggableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener(this);
        setOnLongClickListener(this);
        if (mTarget != null) {
            mTarget.setFocusable(false);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            mTarget = getChildAt(0);
        }
        measureChild(mTarget, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mTarget.layout(0, 0, mTarget.getMeasuredWidth(), mTarget.getMeasuredHeight());
        if (mTargetPoi == null) {
            mTargetPoi = new int[]{(int) mTarget.getX(), (int) mTarget.getY(), (int) mTarget.getX() +
                    mTarget.getMeasuredWidth(), (int) mTarget.getY() + mTarget.getMeasuredHeight()};
        } else {
            //设置mTarget的位置信息
            setTargetPosition((int) mTarget.getX(), (int) mTarget.getY(), (int) mTarget.getX() + mTarget.getMeasuredWidth(), (int) mTarget.getY() + mTarget.getMeasuredHeight());
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();//触点的x坐标
        int y = (int) event.getY();//触点的y坐标
        //判断触点是否在需要拖动的View范围内，并且设置为可拖动
        if (x >= mTargetPoi[0] - 50 && x <= mTargetPoi[2] + 50 && y >= mTargetPoi[1] - 50 && y <= mTargetPoi[3] + 50 && isDraggable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    moveView(mTarget, event);
                    break;
                case MotionEvent.ACTION_UP:
                    isDraggable = false;//设置不可拖动
                    break;
            }
        }

        return false;
    }

    /**
     * 根据event中的数据移动target
     *
     * @param target
     * @param event
     */
    private void moveView(View target, MotionEvent event) {
        int targetWidth = target.getMeasuredWidth();
        int targetHeight = target.getMeasuredHeight();
        int l = (int) event.getX();
        int t = (int) event.getY();
        int r = l + targetWidth;
        int b = t + targetHeight;

        //边界检查
        boolean isOut = checkBorder(l, t, r, b);
        if (isOut) {
            l = targetWidth / 2;
            t = targetHeight / 2;
            r = (int) (targetWidth * 1.5);
            b = (int) (targetHeight * 1.5);
        }

        //更新范围
        setTargetPosition(l - targetWidth / 2, t - targetHeight / 2, r - targetWidth / 2, b - targetHeight / 2);
        target.layout(l - targetWidth / 2, t - targetHeight / 2, r - targetWidth / 2, b - targetHeight / 2);
    }

    /**
     * 边界检查
     *
     * @param l
     * @param t
     * @param r
     * @param b
     * @return true表示出界
     */
    private boolean checkBorder(int l, int t, int r, int b) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return t < 0 || r > dm.widthPixels;
    }

    @Override
    public boolean onLongClick(View v) {
        isDraggable = true;//设置可拖动
        return false;
    }

    /**
     * 设置mTarget的位置信息
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void setTargetPosition(int left, int top, int right, int bottom) {
        mTargetPoi[0] = left;
        mTargetPoi[1] = top;
        mTargetPoi[2] = right;
        mTargetPoi[3] = bottom;
    }
}
