package com.example.com.dampscrollview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRvContent = (RecyclerView)findViewById(R.id.rv_content);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        mRvContent.setLayoutManager(layoutmanager);

        MainAdapter mAdapter = new MainAdapter();
        mRvContent.setAdapter(mAdapter);
//
//        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams)mRvContent.getLayoutParams();
//        p.setMargins(0,100,0,0);
    }
}
