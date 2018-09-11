package com.jzycc.layout.dampscrollview.damprv;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-11
 */
public interface DampBottomViewListener {
    void startLoadMore();

    void stopLoadMore();

    void cannotLoadMore();

    void getScrollChanged(int dy,int topViewPosition);
}
