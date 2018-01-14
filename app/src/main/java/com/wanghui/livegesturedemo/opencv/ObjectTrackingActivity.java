package com.wanghui.livegesturedemo.opencv;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


import com.wanghui.livegesturedemo.R;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import kongqw.ObjectTrackingView;
import kongqw.listener.OnCalcBackProjectListener;
import kongqw.listener.OnObjectTrackingListener;
import kongqw.listener.OnOpenCVLoadListener;

public class ObjectTrackingActivity extends BaseActivity {

    private static final String TAG = "RobotTrackingActivity";
    private ObjectTrackingView objectTrackingView;
    private ImageView imageView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_object_tracking);

        imageView = (ImageView) findViewById(R.id.image_view);

        objectTrackingView = (ObjectTrackingView) findViewById(R.id.tracking_view);

        objectTrackingView.setOnOpenCVLoadListener(new OnOpenCVLoadListener() {
            @Override
            public void onOpenCVLoadSuccess() {
                Toast.makeText(getApplicationContext(), "OpenCV 加载成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOpenCVLoadFail() {
                Toast.makeText(getApplicationContext(), "OpenCV 加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNotInstallOpenCVManager() {
                showInstallDialog();
            }
        });
        // 显示反投影图 调试用
        objectTrackingView.setOnCalcBackProjectListener(new OnCalcBackProjectListener() {
            @Override
            public void onCalcBackProject(final Mat backProject) {
                Log.i(TAG, "onCalcBackProject: " + backProject);
                ObjectTrackingActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null == bitmap) {
                            bitmap = Bitmap.createBitmap(backProject.width(), backProject.height(), Bitmap.Config.ARGB_8888);
                        }
                        Utils.matToBitmap(backProject, bitmap);
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        });
        // 目标检测回调
        objectTrackingView.setOnObjectTrackingListener(new OnObjectTrackingListener() {
            @Override
            public void onObjectLocation(Point center) {
                Log.i(TAG, "onObjectLocation: 目标位置 [" + center.x + ", " + center.y + "]");
            }

            @Override
            public void onObjectLost() {
                ObjectTrackingActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "目标丢失", Toast.LENGTH_SHORT).show();
                        imageView.setImageBitmap(null);
                    }
                });
            }
        });


    }

    /**
     * 切换摄像头
     *
     * @param view view
     */
    public void swapCamera(View view) {
        objectTrackingView.swapCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        objectTrackingView.loadOpenCV(getApplicationContext());
    }
}
