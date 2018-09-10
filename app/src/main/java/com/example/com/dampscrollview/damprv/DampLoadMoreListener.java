package com.example.com.dampscrollview.damprv;

/**
 * author Jzy(Xiaohuntun)
 * date 18-9-10
 */
public interface DampLoadMoreListener {

    void startLoadMore();

    void stopLoadMore();

    void getScrollChanged(int dy,int topViewPosition);

}
