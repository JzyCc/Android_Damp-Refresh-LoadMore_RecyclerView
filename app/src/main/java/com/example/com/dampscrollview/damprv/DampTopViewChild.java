package com.example.com.dampscrollview.damprv;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.com.dampscrollview.R;

import org.w3c.dom.Text;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-6
 */
public class DampTopViewChild extends FrameLayout implements DampTopViewListener {

    private Context mContext;

    private TextView tvRefreshState;

    private ImageView ivRefreshState;

    private float mTopViewHeight;

    private float mMeasureHeight;

    private int isRefreshState;


    /**
     * 刷新相关操作前的状态
     */
    private static final int REFRESH_PRE = 0;

    /**
     * 下拉到了足够高度，松手即可刷新
     */
    private static final int REFRESH_READY = 1;

    /**
     * 下拉长度不足，松手回弹到原位
     */
    private static final int REFRESH_CANNOT = 2;

    /**
     * 刷新中
     */
    private static final int REFRESH_ING = 3;

    private ObjectAnimator animator;


    public DampTopViewChild(@NonNull Context context) {
        super(context);
        mContext = context;
        initThis();
    }

    public DampTopViewChild(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public DampTopViewChild(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    private void initThis(){
        View inflate = inflate(getContext(), R.layout.damp_top_view, this);

        tvRefreshState = (TextView)inflate.findViewById(R.id.tv_refresh_state);

        ivRefreshState = (ImageView)inflate.findViewById(R.id.iv_refresh_state);

        mTopViewHeight = (float) dp2px(mContext,60);

        mMeasureHeight = (float) dp2px(mContext,25);
    }

    @Override
    public void getScrollChanged(int dy, int topViewPosition) {
        if((mTopViewHeight+topViewPosition)>-mMeasureHeight&&topViewPosition<0&&dy<0&&isRefreshState!=REFRESH_ING) {
            ivRefreshState.setRotation(measureImageRotation((float)topViewPosition));
        }
        if(topViewPosition>=0){
            ivRefreshState.setRotation(180);
        }
        if(dy>0&&isRefreshState==REFRESH_READY&&topViewPosition<=0){
            tvRefreshState.setText("下拉刷新");
            ivRefreshState.setRotation(measureImageRotation((float)topViewPosition));
            if((mTopViewHeight-mMeasureHeight)<=-topViewPosition){
                ivRefreshState.setRotation(0);
            }
        }
    }

    @Override
    public void refreshComplete() {
        tvRefreshState.setText("刷新完成");
        if(animator!=null){
            animator.cancel();
        }
    }

    @Override
    public void refreshing() {
        isRefreshState = REFRESH_ING;
        tvRefreshState.setText("正在刷新");
        ivRefreshState.setBackgroundResource(R.drawable.refresh_ing);
        startImageRotation();
    }

    @Override
    public void refreshReady() {
        isRefreshState = REFRESH_READY;
        tvRefreshState.setText("松开刷新");
    }


    @Override
    public void shouldInitialize() {
        tvRefreshState.setText("下拉刷新");
        ivRefreshState.setRotation(0);
        ivRefreshState.setBackgroundResource(R.drawable.pull_down);
        isRefreshState = REFRESH_PRE;

    }

    private float measureImageRotation(float topViewPosition){
        float rotation = (mTopViewHeight-mMeasureHeight-topViewPosition)/(mTopViewHeight-mMeasureHeight)*180;
        return rotation;
    }

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

    private void startImageRotation(){
        animator = ObjectAnimator.ofFloat(ivRefreshState,"rotation",0f,360f);
        animator.setDuration(1000);
        animator.setRepeatCount(-1);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }
}
