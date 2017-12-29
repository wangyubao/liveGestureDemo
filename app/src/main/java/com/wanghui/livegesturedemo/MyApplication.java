package com.wanghui.livegesturedemo;

import android.app.Application;
import android.net.http.HttpResponseCache;

import java.io.File;
import java.io.IOException;

/**
 * Created by wangyubao123 on 2017/12/28.
 */

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
//        val cacheDir = File(context.applicationContext.cacheDir, "http");
//        HttpResponseCache.install(cacheDir, 1024 * 1024 * 128);
        File cacheFile = new File(this.getCacheDir(), "http");
        try {
            HttpResponseCache.install(cacheFile, 1024 * 1024 * 128);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
