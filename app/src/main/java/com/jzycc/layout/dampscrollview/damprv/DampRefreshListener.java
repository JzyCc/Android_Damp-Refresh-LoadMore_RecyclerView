package com.jzycc.layout.dampscrollview.damprv;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-6
 */
public interface DampRefreshListener {
    void getScrollChanged(int dy,int topViewPosition);

    void startRefresh();
}
