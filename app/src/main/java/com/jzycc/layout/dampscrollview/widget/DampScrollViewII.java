package com.jzycc.layout.dampscrollview.widget;

import android.content.Context;
import android.graphics.Rect;
import android.print.PrinterId;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;
import android.widget.Scroller;

public class DampScrollViewII extends ScrollView {
    private RecyclerView childView;
    private int mInitialDownY;
    private Rect mRect = new Rect();
    private int maxTop = 500;
    private int dampValue = 100;
    private Context mContext;

    public DampScrollViewII(Context context) {
        super(context);
        this.mContext = context;
    }

    public DampScrollViewII(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public DampScrollViewII(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    private void initDampScrollView(){

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(getChildCount()>0){
            childView = (RecyclerView) getChildAt(0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mInitialDownY = (int)ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int nowY = (int)ev.getY();
                int offsetY = mInitialDownY - nowY;
                mInitialDownY = nowY;
                if(!childView.canScrollVertically(-1)){
                    if(offsetY<0)
                        return true;
                }else if(!childView.canScrollVertically(1)){
                    if(offsetY>0)
                        return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(childView!=null){
            handleTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    private void handleTouchEvent(MotionEvent ev){
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mInitialDownY = (int)ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int nowY = (int)ev.getY();
                int offsetY = mInitialDownY - nowY;
                mInitialDownY = nowY;
                if(isNeedMove()==true){
                    if(mRect.isEmpty()){
                        mRect.set(childView.getLeft(),childView.getTop(),childView.getRight(),childView.getBottom());
                    }
                    measureDampValue();
                    childView.layout(childView.getLeft(),childView.getTop()-offsetY/2,childView.getRight(),childView.getBottom()-offsetY/2);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(isNeedAnimation()){
                    startAnimation();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

    }

    private boolean isNeedMove(){
        int offset = childView.getMeasuredHeight()-getHeight();
        int scrollY = getScrollY();
        if(scrollY == 0 || scrollY == offset){
            return true;
        }
        return false;
    }

    private boolean isNeedAnimation(){
        return !mRect.isEmpty();
    }

    private void startAnimation(){
        TranslateAnimation anim = new TranslateAnimation(0,0,childView.getTop(),mRect.top);
        anim.setDuration(200);
        anim.setInterpolator(new OvershootInterpolator());
        childView.startAnimation(anim);
        childView.layout(mRect.left,mRect.top,mRect.right,mRect.bottom);
        mRect.setEmpty();
        dampValue = 100;
    }

    private void measureDampValue(){
        int top = Math.abs(childView.getTop());
        dampValue = (maxTop-top)/10;
        if(dampValue<20){
            dampValue = 20;
        }
    }

    public RecyclerView getChildView() {
        return childView;
    }
//
//    private void setRecyclerViewChild(){
//        childView = new RecyclerView(mContext);
//        this.addView(childView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
//        childView.setOverScrollMode(View.OVER_SCROLL_NEVER);
//    }
}
