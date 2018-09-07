package com.example.com.dampscrollview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.com.dampscrollview.damprv.DampRecyclerViewParent;
import com.example.com.dampscrollview.damprv.DampRefreshListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btButton = (Button)findViewById(R.id.bt_button);
        final DampRecyclerViewParent dampRecyclerViewParent = (DampRecyclerViewParent)findViewById(R.id.dv_content);
        dampRecyclerViewParent.setTopView();
        dampRecyclerViewParent.setBottomView();
        RecyclerView mRvContent = (RecyclerView)findViewById(R.id.rv_content);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        mRvContent.setLayoutManager(layoutmanager);
        MainAdapter mAdapter = new MainAdapter();
        mRvContent.setAdapter(mAdapter);


        btButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dampRecyclerViewParent.stopRefresh();
            }
        });
        dampRecyclerViewParent.addOnDampRefreshListen(new DampRefreshListener() {
            @Override
            public void getScrollChanged(int dy, int topViewPosition) {
            }

            @Override
            public void refreshComplete() {

            }

            @Override
            public void refreshing() {

            }

            @Override
            public void refreshReady() {

            }

            @Override
            public void shouldInitialize() {

            }
        });
    }
}
