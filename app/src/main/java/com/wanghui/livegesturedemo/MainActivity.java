package com.wanghui.livegesturedemo;

import android.databinding.DataBindingUtil;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wanghui.livegesturedemo.adapter.VerticalViewPagerAdapter;
import com.wanghui.livegesturedemo.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import master.flame.danmaku.danmaku.model.Danmaku;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding mainBinding;
    private VerticalViewPagerAdapter mAdapter;
    private List<String> mList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initData();
        initPagerAdapter();
        mainBinding.tvStart.setOnClickListener(this);

    }

    private void initData() {
        mList.add("rtmp://live.hkstv.hk.lxdns.com/live/hks");//香港卫视直播地址
        mList.add("http://ivi.bupt.edu.cn/hls/cctv5phd.m3u8");//CCTV5直播地址
    }

    private void initPagerAdapter() {

        mAdapter = new VerticalViewPagerAdapter(this, mList);
        mainBinding.vpLiveroomSwitch.setAdapter(mAdapter);
        mainBinding.vpLiveroomSwitch.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mAdapter.play(position);
                mAdapter.danmuList.get(position).initdanmu();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        mainBinding.rlStart.setVisibility(View.GONE);
        mAdapter.play(0);
        mAdapter.danmuList.get(0).initdanmu();
    }
}
