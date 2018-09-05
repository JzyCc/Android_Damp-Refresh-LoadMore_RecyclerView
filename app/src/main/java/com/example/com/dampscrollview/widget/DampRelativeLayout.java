package com.example.com.dampscrollview.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-3
 */
public class DampRelativeLayout extends RelativeLayout{
    private Context mContext;
    private View topRefresh;
    private View rvView;
    private int mInitialDownY;
    private boolean isCanDamp = false;
    public DampRelativeLayout(Context context) {
        super(context);
        mContext = context;
    }

    public DampRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public DampRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(getChildCount()>0){
            topRefresh = getChildAt(1);
            rvView = (RecyclerView)getChildAt(0);
        }
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i("jzy", "onInterceptTouchEvent: "+"onIn");
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mInitialDownY = (int)ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int nowY = (int)ev.getY();
                int offsetY = mInitialDownY - nowY;
                mInitialDownY = nowY;
                if(rvView.canScrollVertically(1)){
                    if(offsetY<0)
                        isCanDamp = true;
                        topRefresh.setVisibility(View.VISIBLE);
                        return true;
                }else {
                    if(offsetY>0)
                        isCanDamp = true;
                        return true;
                }
            case MotionEvent.ACTION_UP:
                break;
        }
        isCanDamp = false;
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public boolean getCanDamp() {
        return isCanDamp;
    }

    public void setCanDamp(boolean isCanDamp){
        this.isCanDamp = isCanDamp;
    }
}
