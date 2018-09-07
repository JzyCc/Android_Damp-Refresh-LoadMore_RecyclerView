package com.example.com.dampscrollview.damprv;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-6
 */
public interface DampRefreshListener {
    void getScrollChanged(int dy,int topViewPosition);

    void refreshComplete();

    void refreshing();

    void refreshReady();

    void shouldInitialize();
}
