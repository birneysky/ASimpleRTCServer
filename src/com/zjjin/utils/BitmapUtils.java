package com.zjjin.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class BitmapUtils {
	/**
	 * 将图片存入SD卡中指定位置
	 * @param photo 图片
	 * @param path 路径
	 * @param name 名字
	 */
	public static void photo2Sd(Bitmap photo, String path, String name) {
		File fImage = new File(path + name); // /storage/emulated/0/ARTICLE_IMG/1435203142289.jpg
		FileOutputStream iStream = null;
		try {
			boolean b = fImage.createNewFile();
			iStream = new FileOutputStream(fImage);
			boolean result = photo.compress(CompressFormat.PNG, 100, iStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (iStream != null) {
				try {
					iStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 按照指定尺寸加載圖片
	 * 
	 * @param data
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap loadBitmap(String path, int width, int height) {
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		int xScale = opts.outWidth / width;
		int yScale = opts.outHeight / height;
		opts.inSampleSize = xScale > yScale ? xScale : yScale;
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, opts);
	}
	
}
