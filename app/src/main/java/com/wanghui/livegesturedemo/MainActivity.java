package com.wanghui.livegesturedemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.wanghui.livegesturedemo.databinding.ActivityMainBinding;
import com.wanghui.livegesturedemo.opencv.ObjectDetectingActivity;
import com.wanghui.livegesturedemo.opencv.OpenCVActivity;


/**
 * Created by wangyubao123 on 2018/1/5.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_PERMISSION_CAMERA_CODE = 1;
    private ActivityMainBinding mainBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.tvLive.setOnClickListener(this);
        mainBinding.tvFaceDetection.setOnClickListener(this);
        mainBinding.tvObjectDetection.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_live:
                startActivity(new Intent(getApplicationContext(), LiveActivity.class));
                break;

            case R.id.tv_face_detection:

                //判断当前系统是否高于或等于6.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //当前系统大于等于6.0
                    if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        //具有拍照权限，直接调用相机
                        //具体调用代码
                        startActivity(new Intent(getApplicationContext(), ObjectDetectingActivity.class));
                    } else {
                        //不具有拍照权限，需要进行权限申请
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA_CODE);
                    }
                } else {
                    //当前系统小于6.0，直接调用拍照

                    startActivity(new Intent(getApplicationContext(), ObjectDetectingActivity.class));
                }

                break;
            case R.id.tv_object_detection:
                startActivity(new Intent(getApplicationContext(), OpenCVActivity.class));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA_CODE) {
            if (grantResults.length >= 1) {
                int cameraResult = grantResults[0];//相机权限
                boolean cameraGranted = cameraResult == PackageManager.PERMISSION_GRANTED;//拍照权限
                if (cameraGranted) {
                    //具有拍照权限，调用相机
                    startActivity(new Intent(getApplicationContext(), ObjectDetectingActivity.class));
                } else {
                    //不具有相关权限，给予用户提醒，比如Toast或者对话框，让用户去系统设置-应用管理里把相关权限开启
                    Toast.makeText(getApplicationContext(), "请先开启摄像头权限", Toast.LENGTH_SHORT);
                }
            }
        }
    }

}
