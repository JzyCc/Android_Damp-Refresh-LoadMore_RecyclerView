package com.jzycc.layout.dampscrollview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.jzycc.layout.damplayoutlibrary.bottomview.DampBottomViewChild;
import com.jzycc.layout.damplayoutlibrary.layout.DampRecyclerViewChild;
import com.jzycc.layout.damplayoutlibrary.layout.DampRefreshAndLoadMoreLayout;
import com.jzycc.layout.damplayoutlibrary.topview.DampTopViewChild;
import com.jzycc.layout.dampscrollview.vo.Movie;
import com.jzycc.layout.dampscrollview.vo.ZhiHuVo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvContent;
    private DampRefreshAndLoadMoreLayout dampRefreshAndLoadMoreLayout;
    private List<ZhiHuVo> mList = new ArrayList<>();
    private List<ZhiHuVo> mPageList = new ArrayList<>();
    private MainAdapter mAdapter;
    private int count = 0;
    private int pageSize = 5;
    private boolean loadOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setZhiHuVo();
        rvContent = (RecyclerView)findViewById(R.id.rv_content);

        dampRefreshAndLoadMoreLayout =  (DampRefreshAndLoadMoreLayout)findViewById(R.id.dv_content);
        DampTopViewChild dampTopViewChild = new DampTopViewChild(this);
        DampBottomViewChild dampBottomViewChild = new DampBottomViewChild(this);
        dampBottomViewChild.setImageColorResource(getResources().getColor(R.color.colorAccent));
        dampTopViewChild.setImageColorResource(getResources().getColor(R.color.colorAccent));
        dampTopViewChild.setTextColorResource(getResources().getColor(R.color.colorAccent));
        dampRefreshAndLoadMoreLayout.setTopView(dampTopViewChild,DampTopViewChild.DAMPTOPVIEW_HEIGHT);
        dampRefreshAndLoadMoreLayout.setBottomView(dampBottomViewChild,DampBottomViewChild.DAMPBOTTOMVIEW_HEIGHT);
        loadZhiHuVo();

        LinearLayoutManager layoutmanager = new LinearLayoutManager(MainActivity.this);
        rvContent.setLayoutManager(layoutmanager);
        mAdapter = new MainAdapter(this,mPageList);
        rvContent.setAdapter(mAdapter);

        dampRefreshAndLoadMoreLayout.addOnDampRefreshListener(new DampRefreshAndLoadMoreLayout.DampRefreshListener() {
            @Override
            public void getScrollChanged(int i, int i1) {

            }

            @Override
            public void startRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refresh();
                                }
                            });
                        }catch (InterruptedException e){
                            Log.e("jzyTest", "run: ",e );
                        }
                    }
                }).start();
            }
        });

        dampRefreshAndLoadMoreLayout.addOnDampLoadMoreListener(new DampRefreshAndLoadMoreLayout.DampLoadMoreListener() {
            @Override
            public void getScrollChanged(int i, int i1) {

            }

            @Override
            public void startLoadMore() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!loadOver){
                                        loadZhiHuVo();
                                        dampRefreshAndLoadMoreLayout.stopLoadMoreAnimation();
                                    }else {
                                        dampRefreshAndLoadMoreLayout.loadOver();
                                    }
                                }
                            });
                        }catch (InterruptedException e){
                            Log.e("jzyTest", "run: ",e );
                        }
                    }
                }).start();
            }
        });

    }

    private void setZhiHuVo(){
        String[] images = Content.images.split(",");
        String[] titles = Content.titles.split("&&");
        for(int i = 0 ; i < images.length ; i++){
            mList.add(new ZhiHuVo(titles[i],images[i]));
        }
    }
    private void refresh(){
        loadOver = false;
        count = 0;
        mPageList.clear();
        loadZhiHuVo();
        dampRefreshAndLoadMoreLayout.stopRefreshAnimation();
    }
    private void loadZhiHuVo(){
        if(count+1 != mList.size()/pageSize){
            for (int i = count*pageSize; i<count*pageSize+pageSize;i++){
                mPageList.add(mList.get(i));
            }
            count++;
        } else{
            loadOver = true;
        }
    }
}
