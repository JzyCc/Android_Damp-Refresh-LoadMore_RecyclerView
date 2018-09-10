package com.example.com.dampscrollview.damprv;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.com.dampscrollview.R;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-6
 */
public class DampBottomViewChild extends FrameLayout implements DampLoadMoreListener{
    private Context mContext;
    private ImageView ivLoad;
    private ObjectAnimator animator;

    public DampBottomViewChild(@NonNull Context context) {
        super(context);
        mContext = context;
        initThis();
    }

    public DampBottomViewChild(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public DampBottomViewChild(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }


    private void initThis(){
        View inflate = inflate(getContext(), R.layout.damp_bottom_view, this);
        ivLoad = (ImageView)inflate.findViewById(R.id.iv_load);
    }

    @Override
    public void startLoadMore() {
        animator = ObjectAnimator.ofFloat(ivLoad,"rotation",0f,-360f);
        animator.setDuration(1500);
        animator.setRepeatCount(-1);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    @Override
    public void stopLoadMore() {
        if(animator!=null){
            animator.cancel();
        }
    }

    @Override
    public void getScrollChanged(int dy, int topViewPosition) {

    }
}
