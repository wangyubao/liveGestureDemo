package com.wanghui.livegesturedemo;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wanghui.livegesturedemo.adapter.VerticalViewPagerAdapter;
import com.wanghui.livegesturedemo.databinding.ActivityMainBinding;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private VerticalViewPagerAdapter mAdapter;
    private List<String> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initData();
        initPagerAdapter();
    }

    private void initData() {
        mList.add("rtmp://live.hkstv.hk.lxdns.com/live/hks");
        mList.add("http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8");
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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
