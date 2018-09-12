package com.jzycc.layout.dampscrollview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jzycc.layout.damplayoutlibrary.layout.DampRefreshAndLoadMoreLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btButton = (Button)findViewById(R.id.bt_button);
        Button btStopLoad = (Button)findViewById(R.id.bt_stop_load);
        final DampRefreshAndLoadMoreLayout dampRefreshAndLoadMoreLayout = (DampRefreshAndLoadMoreLayout)findViewById(R.id.dv_content);
        dampRefreshAndLoadMoreLayout.setTopView();
        dampRefreshAndLoadMoreLayout.setBottomView();

        RecyclerView mRvContent = (RecyclerView)findViewById(R.id.rv_content);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        mRvContent.setLayoutManager(layoutmanager);
        MainAdapter mAdapter = new MainAdapter();
        mRvContent.setAdapter(mAdapter);


        btButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dampRefreshAndLoadMoreLayout.stopRefreshAnimation();
            }
        });

        btStopLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dampRefreshAndLoadMoreLayout.stopLoadMoreAnimation();
                //dampRefreshAndLoadMoreLayout.loadOver();
            }
        });

        dampRefreshAndLoadMoreLayout.addOnDampRefreshListener(new DampRefreshAndLoadMoreLayout.DampRefreshListener() {
            @Override
            public void getScrollChanged(int dy, int topViewPosition) {

            }

            @Override
            public void startRefresh() {
            }
        });

        dampRefreshAndLoadMoreLayout.addOnDampLoadMoreListener(new DampRefreshAndLoadMoreLayout.DampLoadMoreListener() {
            @Override
            public void getScrollChanged(int dy, int bottomViewPosition) {

            }

            @Override
            public void startLoadMore() {
            }
        });

    }
}
