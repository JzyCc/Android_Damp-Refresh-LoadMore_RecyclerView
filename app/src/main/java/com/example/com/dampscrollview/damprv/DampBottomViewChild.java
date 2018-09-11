package com.example.com.dampscrollview.damprv;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.com.dampscrollview.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-6
 */
public class DampBottomViewChild extends FrameLayout implements DampBottomViewListener{
    private Activity mContext;
    private ImageView ivLoad;
    private ObjectAnimator animator;

    public DampBottomViewChild(@NonNull Context context) {
        super(context);
        mContext = (Activity) context;
        initThis();
    }

    public DampBottomViewChild(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity) context;
    }

    public DampBottomViewChild(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = (Activity) context;
    }


    private void initThis(){
        View inflate = inflate(getContext(), R.layout.damp_bottom_view, this);
        ivLoad = (ImageView)inflate.findViewById(R.id.iv_load);

    }

    @Override
    public void startLoadMore() {
        ivLoad.setVisibility(View.VISIBLE);
        animator = ObjectAnimator.ofFloat(ivLoad,"rotation",0f,-360f);
        animator.setDuration(1500);
        animator.setRepeatCount(-1);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    @Override
    public void stopLoadMore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(animator!=null){
                                animator.cancel();
                            }
                        }
                    });
                }catch (InterruptedException e){
                    Log.e("DampBottomViewChild", "run: ",e );
                }
            }
        });
    }

    @Override
    public void cannotLoadMore() {
        if(animator!=null){
            animator.cancel();
        }
        ivLoad.setVisibility(View.GONE);
    }

    @Override
    public void getScrollChanged(int dy, int changedBottomViewPosition) {

    }
}
