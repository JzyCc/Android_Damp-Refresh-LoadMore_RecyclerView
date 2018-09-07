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

import java.util.ArrayList;
import java.util.List;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-4
 */
public class DampRefreshAndLoadMoreLayout extends LinearLayout implements NestedScrollingParent {
    private Context mContext;

    /**
     * 刷新相关操作前的状态
     */
    public static final int REFRESH_PRE = 0;

    /**
     * 下拉到了足够高度，松手即可刷新
     */
    public static final int REFRESH_READY = 1;

    /**
     * 下拉长度不足，松手回弹到原位
     */
    public static final int REFRESH_CANNOT = 2;

    /**
     * 刷新中
     */
    public static final int REFRESH_ING = 3;

    /**
     * 刷新完成
     */
    public static final int REFRESH_COMPLETE = 4;

    /**
     * 记录当前刷新状态
     */
    private int isRefreshState = 0;

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
     * 中间层View
     */
    private View middleView;

    /**
     * 底层View
     */
    private View bottomView;

    /**
     * 顶部下拉时阻尼值最大时的距离
     */
    private final static int maxMarginTop = 300;

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
     * topView的MarginLayoutParams
     */
    private ViewGroup.MarginLayoutParams topViewMarginParams;

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
    private int mTopViewHeight = 60;

    /**
     * BottomView的高度
     * 单位：px
     */
    private int mInitialBottomViewHeight;

    private DampRefreshListener mDampRefreshListenerInChild;

    //private DampRefreshListener mDampRefreshListener;

    private List<DampRefreshListener> mDampRefreshListeners = new ArrayList<>();



    public DampRefreshAndLoadMoreLayout(Context context) {
        super(context);
        mContext = context;
    }

    public DampRefreshAndLoadMoreLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

    }

    public DampRefreshAndLoadMoreLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(getChildCount()>0){
            middleView = getChildAt(0);
        }
    }
    /**
     * 初始化方法
     * 1.初始化mInitialTopViewHeight和mChangedTopViewHeight
     * 2.初始化初始topview的margin值
     */

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
                if(isRefreshState==REFRESH_PRE){
                    //刷新状态为初始状态时，发送需要初始化topview的消息
                    if(mDampRefreshListeners!=null){
                        for(DampRefreshListener dampRefreshListener : mDampRefreshListeners){
                            dampRefreshListener.shouldInitialize();
                        }
                    }
                    if(mDampRefreshListenerInChild!=null){
                        mDampRefreshListenerInChild.shouldInitialize();
                    }
                }
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
                    if(offsetY>=0){
                        if(isRefreshState == REFRESH_ING&&mChangedTopViewMarginTop>=mInitialTopViewMarginTop){
                            //刷新时若topview在初始位置下面，则拦截事件
                            isDampTopOrBottom = DAMP_TOP;
                            return true;
                        }
                    }
                }else if(!middleView.canScrollVertically(1)){
                    if(offsetY>0){
                        //判断子view是否滑动到顶部并且当前是上滑
                        isDampTopOrBottom = DAMP_BOTTOM;
                        return true;
                    }
                }
            case MotionEvent.ACTION_UP:
                //重置必须要重置的状态
                resetState();
                break;
            case MotionEvent.ACTION_CANCEL:
                //重置必须要重置的状态
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
                    if(topView!=null){
                        if(offsetY<0||isRefreshState==REFRESH_ING){//判断当前是否是顶部可拉动状态
                            isPullDownState = PULL_DOWN_ING;//复原下拉状态
                        }
                        if(isPullDownState == PULL_DOWN_ING){
                            float nowMarginTop = (mChangedTopViewMarginTop-offsetY*measureDampTopValue(mChangedTopViewMarginTop));
                            setTopMarigin(topView,topViewMarginParams,(int)nowMarginTop,mInitialTopViewMarginTop);
                            mChangedTopViewMarginTop = (int) nowMarginTop;
                            if(mDampRefreshListenerInChild!=null){
                                mDampRefreshListenerInChild.getScrollChanged((int)(offsetY*measureDampTopValue(mChangedTopViewMarginTop)),mChangedTopViewMarginTop);
                            }
                            if(mDampRefreshListeners!=null){
                                for(DampRefreshListener dampRefreshListener : mDampRefreshListeners){
                                    dampRefreshListener.getScrollChanged((int)(offsetY*measureDampTopValue(mChangedTopViewMarginTop)),mChangedTopViewMarginTop);
                                }
                            }
                            if(mChangedTopViewMarginTop > 0){
                                //当前下拉距离足够刷新
                                if(isRefreshState != REFRESH_ING){
                                    isRefreshState = REFRESH_READY;
                                    if(mDampRefreshListenerInChild!=null){
                                        mDampRefreshListenerInChild.refreshReady();
                                    }
                                    if(mDampRefreshListeners!=null){
                                        for(DampRefreshListener dampRefreshListener : mDampRefreshListeners){
                                            dampRefreshListener.refreshReady();
                                        }
                                    }
                                }
                            }else {
                                //当前下拉距离不够刷新
                                if(isRefreshState != REFRESH_ING)
                                    isRefreshState = REFRESH_CANNOT;
                            }
                            if(nowMarginTop < mInitialTopViewMarginTop){
                                //如果顶部view回到原位但是仍然在上滑时添加此标记
                                isPullDownState = PULL_DOWN_COMPLETE;
                            }
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
                   if(topView!=null){
                       if(isRefreshState == REFRESH_CANNOT){
                           //当下拉距离不够刷新时,执行该情景下动画，并初始化Refresh状态
                           startDampTopToHomeAnimation();
                           isRefreshState = REFRESH_PRE;
                           resetTopViewState();
                       }else if(mChangedTopViewMarginTop<0){
                           //为解决刷新完成状态全交由外部决定的需求，此处可以实现在刷新状态时，topView尚未完全显示的时候可以带有回弹效果
                           startDampTopToHomeAnimation();
                           resetTopViewState();
                       }else if(isRefreshState == REFRESH_READY){
                           //当状态为即将触发刷新时，执行该情景下动画，并且矫正topView结果位置
                           startDampTopToRefreshAnimation();
                           mChangedTopViewMarginTop = 0;
                           //刷新必需步骤执行后将状态置为正在刷新
                           isRefreshState = REFRESH_ING;
                           if(mDampRefreshListenerInChild!=null){
                               mDampRefreshListenerInChild.refreshing();
                           }
                           if(mDampRefreshListeners!=null){
                               for(DampRefreshListener dampRefreshListener : mDampRefreshListeners){
                                   dampRefreshListener.refreshing();
                               }
                           }
                       }else if(isRefreshState == REFRESH_ING&&mChangedTopViewMarginTop>=mTopViewHeight){
                           //为解决刷新完成状态全交由外部决定的需求，此处实现正在刷新时，topView完全显示的时候可以带有回弹效果
                           startDampTopToRefreshAnimation();
                           mChangedTopViewMarginTop = 0;
                       }
                   }
                    //重置拖动状态
                    resetState();
                }else if(isDampTopOrBottom == DAMP_BOTTOM){
                    startDampMiddleAndBottomAnimation();
                    isUpglide = UPGLIDE_PRE;
                    mChangedMiddleHeight = 0;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i("jzy", "onTouchEvent: "+"cancel");
                if(isDampTopOrBottom == DAMP_BOTTOM){
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
                    //判断上述前置条件，模拟down事件激活拦截方法，将事件交由子View
                    sendDownEvent(mLastMoveMotionEvent);
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
     * 顶部完全回弹时的动画
     */
    private void startDampTopToHomeAnimation(){
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
     * 回弹到刷新位置的动画
     */
    private void startDampTopToRefreshAnimation(){
        ValueAnimator animator = ValueAnimator.ofInt(mChangedTopViewMarginTop,0);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setTopMarigin(topView,topViewMarginParams,(int)animation.getAnimatedValue(),0);
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
        if(((getBottom()-getTop())-(targetView.getBottom()+changedValue))>=0){
            targetView.layout(targetView.getLeft(),top+changedValue,targetView.getRight(),bottom+changedValue);
        }else {
            targetView.layout(targetView.getLeft(),0,targetView.getRight(),getBottom()-getTop());
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
        if(((getBottom()-getTop())-(targetView.getBottom()+changedValue))>=(-initialValue)){
            targetView.layout(targetView.getLeft(),top+changedValue,targetView.getRight(),bottom+changedValue);
        }else {
            targetView.layout(targetView.getLeft(),getBottom()-getTop(),targetView.getRight(),getBottom()-getTop()+initialValue);
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

    /**
     * 设置默认topView
     */
    public void setTopView(){
        if(topView == null){
            topView = new DampTopViewChild(mContext);
            try {
                mDampRefreshListenerInChild = (DampRefreshListener) topView;
            }catch (Exception e){
                Log.e("DampRecyclerViewParent", "setTopView: ",e);
            }
            this.addView(topView,0,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(mContext,mTopViewHeight)));
            //初始化topView相关
            topViewMarginParams = (ViewGroup.MarginLayoutParams)topView.getLayoutParams();
            mInitialTopViewMarginTop = -dp2px(mContext,mTopViewHeight);
            mChangedTopViewMarginTop = mInitialTopViewMarginTop;
            setTopMarigin(topView,topViewMarginParams,mInitialTopViewMarginTop,mInitialTopViewMarginTop);
        }
    }

    /**
     * 设置默认bottomView
     */
    public void setBottomView(){
        if(bottomView == null){
            bottomView = new DampBottomViewChild(mContext);
            this.addView(bottomView,2,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(mContext,mBottomViewHeight)));
            mInitialBottomViewHeight = dp2px(mContext,mBottomViewHeight);
        }
    }

    /**
     * @param dampRefreshListener
     * 添加refresh相关监听
     */
    public void addOnDampRefreshListen(DampRefreshListener dampRefreshListener){
        if(dampRefreshListener!=null&&mDampRefreshListeners!=null){
            mDampRefreshListeners.add(dampRefreshListener);
        }
    }

    /**
     * 停止刷新
     */
    public void stopRefresh(){
        if(isRefreshState == REFRESH_ING&&topView!=null){

            ValueAnimator animator = ValueAnimator.ofInt(mChangedTopViewMarginTop,mInitialTopViewMarginTop);
            animator.setDuration(200);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setTopMarigin(topView,topViewMarginParams,(int)animation.getAnimatedValue(),mInitialTopViewMarginTop);
                }
            });
            animator.start();
            isRefreshState = REFRESH_PRE;
            resetTopViewState();
            if(mDampRefreshListenerInChild!=null){
                mDampRefreshListenerInChild.refreshComplete();
            }

            if(mDampRefreshListeners!=null){
                for(DampRefreshListener dampRefreshListener : mDampRefreshListeners){
                    dampRefreshListener.refreshComplete();
                }
            }

        }
    }

    public int getIsRefreshState() {
        return isRefreshState;
    }
}
