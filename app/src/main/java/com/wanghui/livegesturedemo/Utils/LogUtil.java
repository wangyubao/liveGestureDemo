package com.wanghui.livegesturedemo.Utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by sh on 2016/3/2 10:06.
 */

/**
 * Log工具
 * 使用changeLogSwitch()方法开启和关闭Log
 * 可输出当前线程,类名,方法名等附加信息
 * 使用changeAppendSwitch()方法开启和关闭显示附加信息
 */
public class LogUtil {
    //是否开启Log
    public static boolean isOpen = true;
    //是否开启附加信息
    public static boolean isAppend = true;
    public static final String SEPARATOR = ",";
    public static final String DEFAULT = "6ROOMSS";

    /**
     * info级Log
     *
     * @param TAG
     * @param content
     */
    public static void i(String TAG, String content) {
        if (isOpen) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String info = getLogInfo(stackTraceElement);
            if (TextUtils.isEmpty(TAG)) {
                Log.i(DEFAULT, info + "\n" + content);
            } else {
                Log.i(TAG, info + "\n" + content);
            }
        }
    }

    /**
     * 使用默认TAG输出info级Log
     *
     * @param content
     */
    public static void i(String content) {
        if (isOpen) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String info = getLogInfo(stackTraceElement);
            Log.i(DEFAULT, info + "\n" + content);
        }
    }

    /**
     * debug级Log
     *
     * @param TAG
     * @param content
     */
    public static void d(String TAG, String content) {
        if (isOpen) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String info = getLogInfo(stackTraceElement);
            if (TextUtils.isEmpty(TAG)) {
                Log.d(DEFAULT, info + "\n" + content);
            } else {
                Log.d(TAG, info + "\n" + content);
            }
        }
    }

    /**
     * 使用默认TAG输出debug级Log
     *
     * @param content
     */
    public static void d(String content) {
        if (isOpen) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String info = getLogInfo(stackTraceElement);
            Log.d(DEFAULT, info + "\n" + content);
        }
    }


    /**
     * error级Log
     *
     * @param TAG
     * @param content
     */
    public static void e(String TAG, String content) {
        if (isOpen) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String info = getLogInfo(stackTraceElement);
            if (TextUtils.isEmpty(TAG)) {
                Log.e(DEFAULT, info + "\n" + content);
            } else {
                Log.e(TAG, info + "\n" + content);
            }
        }
    }

    /**
     * 使用默认TAG输出error级Log
     *
     * @param content
     */
    public static void e(String content) {
        if (isOpen) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String info = getLogInfo(stackTraceElement);
            Log.e(DEFAULT, info + "\n" + content);
        }
    }

    /**
     * warn级Log
     *
     * @param TAG
     * @param content
     */
    public static void w(String TAG, String content) {
        if (isOpen) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String info = getLogInfo(stackTraceElement);
            if (TextUtils.isEmpty(TAG)) {
                Log.w(DEFAULT, info + "\n" + content);
            } else {
                Log.w(TAG, info + "\n" + content);
            }
        }
    }

    /**
     * 使用默认TAG输出warn级Log
     *
     * @param content
     */
    public static void w(String content) {
        if (isOpen) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String info = getLogInfo(stackTraceElement);
            Log.w(DEFAULT, info + "\n" + content);
        }
    }

    /**
     * Log的开关
     *
     * @param isOpen
     */
    public static void changeLogSwitch(boolean isOpen) {
        LogUtil.isOpen = isOpen;
    }

    /**
     * 附加信息是否显示的开关
     *
     * @param isAppend
     */
    public static void changeAppendSwitch(boolean isAppend) {
        LogUtil.isAppend = isAppend;
    }


    /**
     * 输出日志所包含的信息
     */
    public static String getLogInfo(StackTraceElement stackTraceElement) {
        if (isAppend) {

            StringBuilder logInfoStringBuilder = new StringBuilder();
            // 获取线程ID
            long threadID = Thread.currentThread().getId();
            // 获取类名.即包名+类名
            String className = stackTraceElement.getClassName();
            // 获取方法名称
            String methodName = stackTraceElement.getMethodName();
            // 获取生成输出行数
            int lineNumber = stackTraceElement.getLineNumber();

            logInfoStringBuilder.append("{ ");
            logInfoStringBuilder.append("线程ID=" + threadID).append(SEPARATOR);
            logInfoStringBuilder.append("类名=" + className).append(SEPARATOR);
            logInfoStringBuilder.append("方法名=" + methodName).append(SEPARATOR);
            logInfoStringBuilder.append("行数=" + lineNumber);
            logInfoStringBuilder.append(" } ");
            return logInfoStringBuilder.toString();
        } else {
            return "";
        }
    }

}
