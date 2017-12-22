package com.wanghui.livegesturedemo.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wanghui.livegesturedemo.R;

import java.lang.reflect.Method;

/**
 * Created by sh on 2016/4/26 11:25.
 */
public class ScreenUtils {
	/**
	 * 获取状态栏高度
	 * @param context
	 * @return
     */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
				"android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * 获取导航栏高度
	 * @param context
	 * @return
     */
	public static int getNavigationBarHeight(Context context) {
		int totalHeight = getHasVirtualKeyHeight(context);

		int contentHeight = getScreenHeight(context);

		return totalHeight  - contentHeight;
	}

	/**
	 * 通过反射，获取包含虚拟键的整体屏幕高度（真实高度）
	 *
	 * @return
	 */
	public static int getHasVirtualKeyHeight(Context context) {
		int dpi = 0;
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		@SuppressWarnings("rawtypes")
        Class c;
		try {
			c = Class.forName("android.view.Display");
			@SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
			method.invoke(display, dm);
			dpi = dm.heightPixels;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dpi;
	}

	/**
	 * 获取屏幕高度（不包括系统ui）
	 * @param context
	 * @return
     */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	/**
	 * 获取屏幕宽度（不包括系统ui）
	 * @param context
	 * @return
     */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 通过反射，获取包含虚拟键的整体屏幕宽度（真实宽度）
	 *
	 * @return
	 */
	public static int getHasVirtualKeyWidth(Context context) {
		int dpi = 0;
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		@SuppressWarnings("rawtypes")
        Class c;
		try {
			c = Class.forName("android.view.Display");
			@SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
			method.invoke(display, dm);
			dpi = dm.widthPixels;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dpi;
	}


	/**
	 * 判断导航栏是否显示（用户是否滑出导航栏）
	 * @param context
	 * @return
     */
	public static boolean isNavigationBarShow(Context context){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = manager.getDefaultDisplay();
			Point size = new Point();
			Point realSize = new Point();
			display.getSize(size);
			display.getRealSize(realSize);
			return realSize.y!=size.y;
		}else {
			boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
			boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
			return !(menu || back);
		}
	}
//	public static void setColor(Activity activity, @ColorRes int color) {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			//Android5.0版本
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//				activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//				//设置导航栏颜色
////				activity.getWindow().setNavigationBarColor(color);
//
//				activity.getWindow().setStatusBarColor(activity.getResources().getColor(color));
//			} else {
//				//透明状态栏
//				activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//				//透明导航栏
//				//创建状态栏的管理实例
//				SystemBarTintManager tintManager = new SystemBarTintManager(activity);
////				//激活状态栏设置
////				tintManager.setStatusBarTintEnabled(true);
////				//设置状态栏颜色
////				tintManager.setTintResource(color);
//				//激活导航栏设置
////				tintManager.setNavigationBarTintEnabled(true);
//				//设置导航栏颜色
////				tintManager.setNavigationBarTintResource(color);
//			}
//		}
//	}

	public static void hideUI(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//Android5.0版本
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
						| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
				activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				);
				activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				//设置状态栏颜色
				activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.transparent));
				//设置导航栏颜色
				activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.transparent));
			} else {
				//透明状态栏
				activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				//透明导航栏
				activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
				//创建状态栏的管理实例
				SystemBarTintManager tintManager = new SystemBarTintManager(activity);
				//激活状态栏设置
				tintManager.setStatusBarTintEnabled(true);
				//设置状态栏颜色
				tintManager.setTintResource(R.color.transparent);
				//激活导航栏设置
				tintManager.setNavigationBarTintEnabled(true);
				//设置导航栏颜色
				tintManager.setNavigationBarTintResource(R.color.transparent);
			}
		}
	}

	public static boolean checkDeviceHasNavigationBar(Context context) {
		boolean hasNavigationBar = false;
		Resources rs = context.getResources();
		int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
		if (id > 0) {
			hasNavigationBar = rs.getBoolean(id);
		}
		try {
			Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
			Method m = systemPropertiesClass.getMethod("get", String.class);
			String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
			if ("1".equals(navBarOverride)) {
				hasNavigationBar = false;
			} else if ("0".equals(navBarOverride)) {
				hasNavigationBar = true;
			}
		} catch (Exception e) {
		}
		return hasNavigationBar;
	}

	/**
	 * 计算指定的 View 在屏幕中的坐标。
	 */
	public static RectF calcViewScreenLocation(View view) {
		int[] location = new int[2];
		// 获取控件在屏幕中的位置，返回的数组分别为控件左顶点的 x、y 的值
		view.getLocationOnScreen(location);
		return new RectF(location[0], location[1], location[0] + view.getWidth(),
				location[1] + view.getHeight());
	}

}
