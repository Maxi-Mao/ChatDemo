package com.maxi.chatdemo.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;

public class ImageCheckoutUtil {
	/**
	 * 检测图片内存大小
	 * @param data
	 * @return
	 */
	@SuppressLint("NewApi")
	public static int getImageSize(Bitmap data) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
			return data.getRowBytes() * data.getHeight();
		} else {
			return data.getByteCount();
		}
	}

}
