package com.wanghui.livegesturedemo.Utils;

import android.content.Context;
import android.util.Log;

import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangyubao123 on 2018/1/12.
 */

public class RawFileUtils {
    public static String getCacheRawFilePath(Context context, int resId) {
        InputStream is = null;
        FileOutputStream os = null;
        try {
            is = context.getResources().openRawResource(resId);
            File cascadeDir = context.getDir("pngcache", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, resId + ".png");
            if (cascadeFile.exists()) {
                Log.i("hahaaa", cascadeFile.length() + "");
                return cascadeFile.getAbsolutePath();
            }
            os = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            return cascadeFile.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
                if (null != os) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
