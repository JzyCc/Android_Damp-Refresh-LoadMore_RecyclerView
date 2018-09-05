package com.example.com.dampscrollview.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-3
 */
public class DampNestedScrollChild extends RecyclerView implements NestedScrollingChild{
    public DampNestedScrollChild(@NonNull Context context) {
        super(context);
    }

    public DampNestedScrollChild(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DampNestedScrollChild(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


}
