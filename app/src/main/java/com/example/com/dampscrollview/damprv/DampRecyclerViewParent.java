package com.example.com.dampscrollview.damprv;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
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
     * 上滑前
     */
    private static final int UPGLIDE_PRE = 0;

    /**
     * 上滑中
     */
    private static final int UPGLIDE_ING = 1;

    /**
     * 上滑完成
     */
    private static final int UPGLIDE_COMPLETE = 2;

    /**
     * 当前上滑状态
     */
    private int isUpglide = 0;

    /**
     * 顶层View
     */
    private View topView;

    /**
     * RecyclerView
     */
    //private DampRecyclerViewChild rvView;

    private View middleView;

    /**
     * 底层View
     */
    private View bottomView;

    /**
     * 顶部下拉时阻尼值最大时的距离
     */
    private final static int maxMarginTop = 200;

    /**
     * 底部上滑时阻尼值最大时的距离
     */
    private final static int maxMarginBottom = 300;

    /**
     * 保存上一次move时手指在Y轴的位置
     */
    private int mInitialDownY;

    /**
     * 保存topView的原始marginTop值
     */
    private int mInitialTopViewMarginTop;

    /**
     * 实时改变的topView的marginTop值
     */
    private int mChangedTopViewMarginTop = 0;


    /**
     * 实时改变的bottomView的marginTop值
     */
    private int mChangedBottomViewMarginTop = 0;

    /**
     * topView的MarginLayoutParams
     */
    private ViewGroup.MarginLayoutParams topViewMarginParams;

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

    /**
     * 记录MiddleView移动的总值
     */
    private int mChangedMiddleHeight = 0;

    /**
     * BottomView的高度
     * 单位：dp
     */
    private int mBottomViewHeight = 100;

    /**
     * TopView的高度
     * 单位：dp
     */
    private int mTopViewHeight = 100;

    /**
     * BottomView的高度
     * 单位：px
     */
    private int mInitialBottomViewHeight;


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
        Log.i("jzy", "onFinishInflate: "+getChildCount());
        super.onFinishInflate();
        if(getChildCount()>0){
            //topView = getChildAt(0);
            middleView = getChildAt(0);
            if(bottomView!=null){
                this.addView(bottomView,1,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(mContext,mBottomViewHeight)));
            }
            initThis();
        }
    }
    /**
     * 初始化方法
     * 1.初始化mInitialTopViewHeight和mChangedTopViewHeight
     * 2.初始化初始topview的margin值
     */
    private void initThis(){

        //初始化bottomView相关
        mInitialBottomViewHeight = dp2px(mContext,mBottomViewHeight);

    }

    private void resetState(){
        isDampTopOrBottom = DAMP_NONE;
        isPullDownState = PULL_DOWN_PRE;
        isUpglide = UPGLIDE_PRE;
    }

    private void resetTopViewState(){
        mChangedTopViewMarginTop = mInitialTopViewMarginTop;
    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mInitialDownY = (int)ev.getY();
                resetState();
                break;
            case MotionEvent.ACTION_MOVE:
                int nowY = (int)ev.getY();
                int offsetY = mInitialDownY - nowY;
                mInitialDownY = nowY;
                if(!middleView.canScrollVertically(-1)){
                    if(offsetY<0){//判断子view是否滑动到顶部并且当前是下滑
                        isDampTopOrBottom = DAMP_TOP;
                        return true;
                    }
                }else if(!middleView.canScrollVertically(1)){
                    if(offsetY>0){//判断子view是否滑动到顶部并且当前是上滑
                        isDampTopOrBottom = DAMP_BOTTOM;
                        return true;
                    }
                }
            case MotionEvent.ACTION_UP:
                resetState();
                break;
            case MotionEvent.ACTION_CANCEL:
                resetState();
                break;
        }
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
                if(isDampTopOrBottom == DAMP_TOP&&!middleView.canScrollVertically(-1)){
                    if(offsetY<0){//判断当前是否是顶部可拉动状态
                        isPullDownState = PULL_DOWN_ING;//复原下拉状态
                    }
                    if(isPullDownState == PULL_DOWN_ING){
                        float nowMarginTop = (mChangedTopViewMarginTop-offsetY*measureDampTopValue(mChangedTopViewMarginTop));
                        setTopMarigin(topView,topViewMarginParams,(int)nowMarginTop,mInitialTopViewMarginTop);
                        mChangedTopViewMarginTop = (int) nowMarginTop;
                        if(nowMarginTop < mInitialTopViewMarginTop){
                            //如果顶部view回到原位但是仍然在上滑时添加此标记
                            isPullDownState = PULL_DOWN_COMPLETE;
                        }
                    }
                }else if(isDampTopOrBottom == DAMP_BOTTOM&&!canScrollVertically(1)){
                    if(offsetY>0){//判断当前是否是底部可上滑状态
                        isUpglide = UPGLIDE_ING;
                    }
                    if(isUpglide == UPGLIDE_ING){
                        float nowOffsetY = offsetY*measureDampMiddleValue(mChangedMiddleHeight);
                        setMiddleViewLayout(middleView,middleView.getTop(),middleView.getBottom(),-(int)nowOffsetY);
                        setBottomViewLayout(bottomView,bottomView.getTop(),bottomView.getBottom(),-(int)nowOffsetY,mInitialBottomViewHeight);
                        mChangedMiddleHeight += (int)nowOffsetY;
                        if(mChangedMiddleHeight<0){
                            //如果MidlleView回到原位但是仍在下拉时添加此标记
                            isUpglide = UPGLIDE_COMPLETE;
                            mChangedMiddleHeight = 0;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(isDampTopOrBottom == DAMP_TOP){
                    startDampTopAnimation();
                    isPullDownState = PULL_DOWN_PRE;
                    resetTopViewState();
                }else if(isDampTopOrBottom == DAMP_BOTTOM){
                    startDampMiddleAndBottomAnimation();
                    isUpglide = UPGLIDE_PRE;
                    mChangedMiddleHeight = 0;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if(isDampTopOrBottom == DAMP_BOTTOM){
                    isUpglide = UPGLIDE_PRE;
                    mChangedMiddleHeight = 0;
                }
                resetState();
                break;
        }
        return super.onTouchEvent(event);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDispatchDownY = (int)ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mLastMoveMotionEvent = ev;
                int nowY = (int)ev.getY();
                int offsetY = mDispatchDownY-nowY;
                mDispatchDownY = nowY;
                if((!canScrollVertically(-1)&&offsetY>0&&isPullDownState == PULL_DOWN_COMPLETE)
                        ||(!canScrollVertically(1)&&offsetY<0&&isUpglide == UPGLIDE_COMPLETE)){
                    //sendCancelEvent(mLastMoveMotionEvent);
                    sendDownEvent(mLastMoveMotionEvent);//重新发送down 来激活拦截事件方法
                    resetState();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * @param ev
     * 模拟cancel事件
     */
    private void sendCancelEvent(MotionEvent ev){
        MotionEvent e = MotionEvent.obtain(ev.getDownTime(),ev.getEventTime()+ ViewConfiguration.getLongPressTimeout(),MotionEvent.ACTION_CANCEL,ev.getX(),ev.getY(),ev.getMetaState());
        super.dispatchTouchEvent(e);
    }

    /**
     * @param ev
     * 模拟down事件
     */
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
/*以下是顶部View相关函数*/

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

    /**
     * 顶部回弹时的动画
     */
    private void startDampTopAnimation(){
        final ValueAnimator animator = ValueAnimator.ofInt(mChangedTopViewMarginTop,mInitialTopViewMarginTop);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setTopMarigin(topView,topViewMarginParams,(int)animation.getAnimatedValue(),mInitialTopViewMarginTop);
            }
        });
        animator.start();
    }

    /**
     * @param valueAnimator
     * 取消Value动画
     */
    private void cancelAnimation(ValueAnimator valueAnimator){
        valueAnimator.cancel();
    }
    /**
     * @param marginValue
     * @return dampvalue
     * 计算顶部下拉时的实时阻尼值
     */
    private float measureDampTopValue(int marginValue){
        float dampTopValue = 100;
        if(marginValue < 0){
            marginValue = 0;
        }
        int marginTop = Math.abs(marginValue);
        dampTopValue = (maxMarginTop-marginTop)/(maxMarginTop/100);
        if(dampTopValue<10){
            dampTopValue = 10;
        }
        Log.i("jzy", "measureDampTopValue: "+dampTopValue);
        return dampTopValue/100;
    }

/*以上是顶部View相关函数*/

/*以下是中间View和底部View相关函数*/

    /**
     * middleView的回弹动画
     */
    private void startDampMiddleAndBottomAnimation(){
        final int topMiddle = middleView.getTop();
        final int bottomMiddle = middleView.getBottom();
        final int topBottom = bottomView.getTop();
        final int bottomBottom = bottomView.getBottom();
        final ValueAnimator animator = ValueAnimator.ofInt(0,mChangedMiddleHeight);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setMiddleViewLayout(middleView,topMiddle,bottomMiddle,(int)animation.getAnimatedValue());
                setBottomViewLayout(bottomView,topBottom,bottomBottom,(int)animation.getAnimatedValue(),mInitialBottomViewHeight);
            }
        });
        animator.start();
    }

    /**
     * @param targetView
     * @param top
     * @param bottom
     * @param changedValue
     * middleView设置布局位置的方法
     */
    private void setMiddleViewLayout(View targetView,int top,int bottom,int changedValue){
        if((getBottom()-(targetView.getBottom()+changedValue))>=0){
            targetView.layout(targetView.getLeft(),top+changedValue,targetView.getRight(),bottom+changedValue);
        }else {
            targetView.layout(targetView.getLeft(),getTop(),targetView.getRight(),getBottom());
        }
    }

    /**
     * @param changedValue
     * @return changedValue
     * 测量middleView的实时阻尼值
     */
    private float measureDampMiddleValue(int changedValue){
        float dampValue;
        if(changedValue < 0){
            changedValue = 0;
        }
        dampValue = (maxMarginBottom-changedValue)/(maxMarginBottom/100);
        if(dampValue<10){
            dampValue = 10;
        }
        return dampValue/100;
    }

    /**
     * @param targetView
     * @param top
     * @param bottom
     * @param changedValue
     * @param initialValue
     * bottomView设置布局位置的方法
     */
    private void setBottomViewLayout(View targetView,int top,int bottom,int changedValue,int initialValue){
        if((getBottom()-(targetView.getBottom()+changedValue))>=(-initialValue)){
            targetView.layout(targetView.getLeft(),top+changedValue,targetView.getRight(),bottom+changedValue);
        }else {
            targetView.layout(targetView.getLeft(),getBottom(),targetView.getRight(),getBottom()+initialValue);
        }
    }

/*以上是中间View和底部View相关函数*/

    /**
     * @param context
     * @param dpValue
     * @return px
     * 将dp转化为px
     */
    private int dp2px(Context context,float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }

    public void setTopView(){
        if(topView == null){
            topView = new DampTopViewChild(mContext);
            this.addView(topView,0,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(mContext,mTopViewHeight)));
            //初始化topView相关
            topViewMarginParams = (ViewGroup.MarginLayoutParams)topView.getLayoutParams();
            mInitialTopViewMarginTop = -dp2px(mContext,mTopViewHeight);
            mChangedTopViewMarginTop = mInitialTopViewMarginTop;
            setTopMarigin(topView,topViewMarginParams,mInitialTopViewMarginTop,mInitialTopViewMarginTop);
        }
    }

    public void setBottomView(){
        if(bottomView == null){
            bottomView = new DampBottomViewChild(mContext);
            //this.addView(bottomView,2,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(mContext,mBottomViewHeight)));
        }
    }
}
