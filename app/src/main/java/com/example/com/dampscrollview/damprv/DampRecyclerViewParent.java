package com.example.com.dampscrollview.damprv;

import android.animation.ValueAnimator;
import android.content.Context;
import android.print.PrinterId;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MonthDisplayHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-4
 */
public class DampRecyclerViewParent extends LinearLayout implements NestedScrollingParent {
    private Context mContext;

    /**
     *不实现Damp
     */
    private static final int DAMP_NONE = 0;

    /**
     * 顶部可以实现damp
     */
    private static final int DAMP_TOP = 1;

    /**
     * 底部可以实现damp
     */
    private static final int DAMP_BOTTOM = -1;

    /**
     * 当前拖动状态
     */
    private int isDampTopOrBottom = DAMP_NONE;

    /**
     * 下拉前
     */
    private static final int PULL_DOWN_PRE = 0;

    /**
     * 下拉中
     */
    private static final int PULL_DOWN_ING = 1;

    /**
     * 下拉完成
     */
    private static final int PULL_DOWN_COMPLETE = 2;

    /**
     * 当前下拉状态
     */
    private int isPullDownState = 0;


    /**
     * 顶层View
     */
    private View topView;

    /**
     * 中间层View
     */
    private DampRecyclerViewChild rvView;

    /**
     * 底层View
     */
    private View bottomView;


    /**
     * 最大阻尼时的MarginTop;
     */
    private int maxMarginTop = 500;

    /**
     * 保存上一次move时手指在Y轴的位置
     */
    private int mInitialDownY;

    /**
     * 保存topView的原始marginTop值
     */
    private int mInitialTopViewHeight;

    /**
     * 实时改变的topView的marginTop值
     */
    private int mChangedTopViewMargin = 0;

    /**
     * 保存rvView的初始MarginBottom值
     */
    private int mInitialRvViewMagin;


    /**
     * 实时改变的rvView的MarginBottom值
     */
    private int mChangedRvViewMargin = 0;

    /**
     * 保存bottomView的原始marginTop值
     */
    private int mInitialBottomViewMargin;

    /**
     * 实时改变的bottomView的marginTop值
     */
    private int mChangedBottomViewMargin = 0;

    /**
     * topView的MarginLayoutParams
     */
    private ViewGroup.MarginLayoutParams topViewMarginParams;

    /**
     * rvView的MarginLayoutParams
     */
    private ViewGroup.MarginLayoutParams rvViewMarginParams;

    /**
     * bottomView的MarginLayoutParams
     */
    private ViewGroup.MarginLayoutParams bottomViewMarginParams;

    /**
     * 保存最后一次MotionEvent
     */
    private MotionEvent mLastMoveMotionEvent;

    /**
     * 单独保存dispatchTouchEvent中上一次MOVE的位置
     */
    private int mDispatchDownY;

    public DampRecyclerViewParent(Context context) {
        super(context);
        mContext = context;
    }

    public DampRecyclerViewParent(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public DampRecyclerViewParent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(getChildCount()>2){
            topView = getChildAt(0);
            rvView = (DampRecyclerViewChild) getChildAt(1);
            bottomView = getChildAt(2);
            initThis();
        }
    }

    /**
     * 初始化方法
     * 1.初始化mInitialTopViewHeight和mChangedTopViewHeight
     * 2.初始化初始topview的margin值
     */
    private void initThis(){
        topViewMarginParams = (ViewGroup.MarginLayoutParams)topView.getLayoutParams();
        mInitialTopViewHeight = -getViewHeight(topView);
        mChangedTopViewMargin = mInitialTopViewHeight;
        setTopMarigin(topView,topViewMarginParams,mInitialTopViewHeight,mInitialTopViewHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //Log.i("jzy", "onInterceptTouchEvent: "+"Parent Intercept");
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i("jzy", "onInterceptTouchEvent: "+"down");
                mInitialDownY = (int)ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int nowY = (int)ev.getY();
                int offsetY = mInitialDownY - nowY;
                mInitialDownY = nowY;
                if(!rvView.canScrollVertically(-1)){
                    if(offsetY<0){//判断子view是否滑动到顶部并且当前是下滑
                        isDampTopOrBottom = DAMP_TOP;
                        return true;
                    }
                }else if(!rvView.canScrollVertically(1)){
                    if(offsetY>0){//判断子view是否滑动到顶部并且当前是上滑
                        isDampTopOrBottom = DAMP_BOTTOM;
                        return true;
                    }
                }
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                //Log.i("jzy", "onInterceptTouchEvent: "+"cancel");
                break;
        }
        //Log.i("jzy", "onInterceptTouchEvent: "+super.onInterceptTouchEvent(ev));
        isDampTopOrBottom = DAMP_NONE;
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mInitialDownY = (int)event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int nowY = (int)event.getY();
                int offsetY = mInitialDownY - nowY;
                mInitialDownY = nowY;
                if(isDampTopOrBottom == DAMP_TOP&&!rvView.canScrollVertically(-1)){
                    if(offsetY<0){//判断当前是否是顶部可拉动状态
                        isPullDownState = PULL_DOWN_PRE;//复原下拉状态
                    }
                    if(isPullDownState!=PULL_DOWN_COMPLETE){
                        isPullDownState = PULL_DOWN_ING;
                        float nowMarginTop = (mChangedTopViewMargin-offsetY*measureDampTopValue(mChangedTopViewMargin));
                        setTopMarigin(topView,topViewMarginParams,(int)nowMarginTop,mInitialTopViewHeight);
                        mChangedTopViewMargin = (int) nowMarginTop;
                        if(nowMarginTop < mInitialTopViewHeight){
                            //如果顶部view回到原位但是仍然在上滑时添加此标记
                            isPullDownState = PULL_DOWN_COMPLETE;
                        }
                    }
                }else if(isDampTopOrBottom == DAMP_BOTTOM){

                }
                break;
            case MotionEvent.ACTION_UP:
                if(isDampTopOrBottom == DAMP_TOP){
                    startDampTopAnimation();
                    isPullDownState = PULL_DOWN_PRE;
                }
                mChangedTopViewMargin = mInitialTopViewHeight;
                break;
            case MotionEvent.ACTION_CANCEL:
                isPullDownState = PULL_DOWN_PRE;
                Log.i("jzy", "dispatchTouchEvent: "+"cancel");
                break;
        }
        return super.onTouchEvent(event);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                //Log.i("jzy", "dispatchTouchEvent: "+"down");
                mDispatchDownY = (int)ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mLastMoveMotionEvent = ev;
                int nowY = (int)ev.getY();
                int offsetY = mDispatchDownY-nowY;
                mDispatchDownY = nowY;
                if(!canScrollVertically(-1)&&offsetY>0&&isPullDownState==PULL_DOWN_COMPLETE){
                    //sendCancelEvent(mLastMoveMotionEvent);
                    sendDownEvent(mLastMoveMotionEvent);
                    isPullDownState = PULL_DOWN_PRE;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i("jzy", "dispatchTouchEvent: "+"cancel");
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    private void sendCancelEvent(MotionEvent ev){
        MotionEvent e = MotionEvent.obtain(ev.getDownTime(),ev.getEventTime()+ ViewConfiguration.getLongPressTimeout(),MotionEvent.ACTION_CANCEL,ev.getX(),ev.getY(),ev.getMetaState());
        super.dispatchTouchEvent(e);
    }

    private void sendDownEvent(MotionEvent ev){
        MotionEvent e = MotionEvent.obtain(ev.getDownTime(),ev.getEventTime(),MotionEvent.ACTION_DOWN,ev.getX(),ev.getY(),ev.getMetaState());
        super.dispatchTouchEvent(e);
    }

    /**
     * @param view
     * @return view.height
     * 获取view的高度
     */
    private int getViewHeight(View view){

        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(0,h);
        int height = view.getMeasuredHeight();
        return height;
    }

    /**
     * @param targetView
     * @param targetMarginParams
     * @param mariginTopValue
     * @param initialValue
     * set margintop 的方法
     */
    private void setTopMarigin(View targetView,ViewGroup.MarginLayoutParams targetMarginParams,int mariginTopValue,int initialValue){
        if(mariginTopValue>=initialValue){
            targetMarginParams.setMargins(0,mariginTopValue,0,0);
            targetView.requestLayout();
        }else{
            targetMarginParams.setMargins(0,initialValue,0,0);
            targetView.requestLayout();
        }
    }

/*以下是顶部View相关函数*/

    /**
     * 顶部回弹时的动画
     */
    private void startDampTopAnimation(){
        ValueAnimator animator = ValueAnimator.ofInt(mChangedTopViewMargin,mInitialTopViewHeight);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setTopMarigin(topView,topViewMarginParams,(int)animation.getAnimatedValue(),mInitialTopViewHeight);
            }
        });
        animator.start();
    }

    /**
     * @param marginTopValue
     * @return dampvalue
     * 计算顶部下拉时的实时阻尼值
     */
    private float measureDampTopValue(int marginTopValue){
        float dampTopValue = 100;
        if(marginTopValue < 0){
            marginTopValue = 0;
        }
        int marginTop = Math.abs(marginTopValue);
        dampTopValue = (maxMarginTop-marginTop)/5;
        if(dampTopValue<10){
            dampTopValue = 10;
        }
        return dampTopValue/100;
    }
/*以上是顶部View相关函数*/

/*以下是底部View相关函数*/

    private void setBottomMargin(){

    }
/*以上是底部View相关函数*/
}
