package com.maxi.chatdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

public class ScreenUtil {

	private static int screenWidth = 0;

	private static int screenHeight = 0;
	private static int screenTotalHeight = 0;
	private static int statusBarHeight = 0;

	private static final int TITLE_HEIGHT = 0;

	/** 
     */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/** 
     */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getScreenWidth(Context context) {
//		if (screenWidth != 0) {
//			return screenWidth;
//		}
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		return screenWidth;
	}

	public static int getScreenHeight(Context context) {
//		if (screenHeight != 0) {
//			return screenHeight;
//		}
		int top = 0;
		if (context instanceof Activity) {
			top = ((Activity) context).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
			if (top == 0) {
				top = (int) (TITLE_HEIGHT * getScreenDensity(context));
			}
		}
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		screenHeight = dm.heightPixels - top;
		return screenHeight;
	}

	public static int getScreenTotalHeight(Context context) {
		if (screenTotalHeight != 0) {
			return screenTotalHeight;
		}
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		screenTotalHeight = displayMetrics.heightPixels;
		return screenTotalHeight;
	}
	public static float getScreenDensity(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);
		return metric.density;
	}

	public static float getScreenDensityDpi(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);
		return metric.densityDpi;
	}
	public static int getStatusBarHeight(Context context) {
		if (statusBarHeight != 0) {
			return statusBarHeight;
		}
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}
	public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}