package com.example.com.dampscrollview.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-3
 */
public class DampChildRecyclerView extends RecyclerView {
    private Context mContext;

    public DampChildRecyclerView(Context context) {
        super(context);
    }

    public DampChildRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DampChildRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
