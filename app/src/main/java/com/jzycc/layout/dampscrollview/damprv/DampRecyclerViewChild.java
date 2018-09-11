package com.jzycc.layout.dampscrollview.damprv;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-4
 */
public class DampRecyclerViewChild extends RecyclerView  {
    private int mInitialDownY;

    public DampRecyclerViewChild(Context context) {
        super(context);
    }

    public DampRecyclerViewChild(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DampRecyclerViewChild(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return super.onTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mInitialDownY = (int)ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int nowY = (int)ev.getY();
                int offsetY = mInitialDownY-nowY;
                mInitialDownY = nowY;
                if((!canScrollVertically(-1)&&offsetY<0)||(!canScrollVertically(1)&&offsetY>0)){
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setTrue(){
        getParent().requestDisallowInterceptTouchEvent(true);
    }
}
