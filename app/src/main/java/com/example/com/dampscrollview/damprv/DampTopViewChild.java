package com.example.com.dampscrollview.damprv;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.com.dampscrollview.R;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-6
 */
public class DampTopViewChild extends FrameLayout {

    private Context mContext;

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
    }
}
