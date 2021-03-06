package com.jzycc.layout.damplayoutlibrary.topview;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-11
 */
public interface DampTopViewListener {
    void getScrollChanged(int dy, int topViewPosition);

    void refreshComplete();

    void refreshing();

    void refreshReady();

    void shouldInitialize();
}
