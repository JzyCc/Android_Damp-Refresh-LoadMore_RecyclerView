package com.example.com.dampscrollview.damprv;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.example.com.dampscrollview.R;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-6
 */
public class DampBottomViewChild extends FrameLayout{
    private Context mContext;

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
    }
}
