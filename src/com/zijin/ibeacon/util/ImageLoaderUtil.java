package com.zijin.ibeacon.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.zjjin.utils.Consts;

public class ImageLoaderUtil {
	
	private static final String TAG = ImageLoaderUtil.class.getSimpleName();
	private static ImageLoaderUtil instance;// = new ImageLoaderUtil();
	private Context mContext;
	private ImageLoaderCallBack mCallback;

	// private PhotoViewAttacher mAttacher;
	/**
	 * 构造方法
	 * @param context
	 */
	public ImageLoaderUtil(Context context) {
		File cachePath = new File(Consts.IMG_PATH);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context.getApplicationContext())
		// .defaultDisplayImageOptions(defaultOptions)
				.discCacheSize(50 * 1024 * 1024)//
				.discCacheFileCount(100)// 缓存一百张图片
				.writeDebugLogs().discCache(new UnlimitedDiscCache(cachePath))// 自定义缓存路径
				.imageDownloader(new BaseImageDownloader(context, 15 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间  
				.build();
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 获取ImageLoaderUtil实例
	 * @param context
	 * @return
	 */
	public static ImageLoaderUtil getInstance(Context context) {
		if (instance == null) {
			synchronized (context) {
				if (instance == null)
					instance = new ImageLoaderUtil(context);
			}
		}
		return instance;
	}

	/**
	 * 获取图片：本地或网络
	 * @param imageres
	 * @param imagePath
	 */
	
	public void getImage(ImageView imageres, String imagePath) {//http://61.51.110.209:8080/ok/ARTICLE_IMG/IMG_20150819_121647.JPEG
		// mAttacher = new PhotoViewAttacher(imageres);
		String mImagePath = imagePath;
		if(checkImage(imagePath)){//加载本地图片
			File file =new File(imagePath);//http:/61.51.110.209:8080/ok/ARTICLE_IMG/IMG_20150819_121647.JPEG
			String fileName = file.getName();//IMG_20150819_121647.JPEG
			mImagePath = "file://"+Consts.IMG_PATH+fileName;//file:///storage/emulated/0/ARTICLE_IMG/IMG_20150819_121647.JPEG
		}
		ImageLoader.getInstance().displayImage(mImagePath, imageres, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				// progressBar.setVisibility(View.VISIBLE);
			}
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = null;
				switch (failReason.getType()) {
				case IO_ERROR:
					message = "下载错误";
					break;
				case DECODING_ERROR:
					message = "图片无法显示";
					break;
				case NETWORK_DENIED:
					message = "网络有问题，无法下载";
					break;
				case OUT_OF_MEMORY:
					message = "图片太大无法显示";
					break;
				case UNKNOWN:
					message = "未知的错误";
					break;
				}
				Log.e(TAG, "下载失败，图片地址："+imageUri+"，失败原因："+message);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				Log.i(TAG, "下载完成，图片地址："+imageUri);
				saveBMP2SD(imageUri, loadedImage);
				mCallback.refreshAdapter();
			}
		});
	}

	/**
	 * 检测图片文件是否存在
	 */
	private boolean checkImage(String imagePath) {//http://61.51.110.209:8080/ok/ARTICLE_IMG/IMG_20150819_121647.JPEG
		boolean isexsit =true;
		File file =new File(imagePath);//http:/61.51.110.209:8080/ok/ARTICLE_IMG/IMG_20150819_121647.JPEG
		String fileName = file.getName();//IMG_20150819_121647.JPEG
		File f = new File(Consts.IMG_PATH+fileName);// /storage/emulated/0/ARTICLE_IMG/IMG_20150819_121647.JPEG
		isexsit = f.exists();//true
		return isexsit;
	}

	/**
	 * 将网络下载来的图片存入SD卡
	 */
	protected void saveBMP2SD(String imagePath, Bitmap loadedImage) {
		if(!imagePath.contains("http"))return;
		File file =new File(imagePath);
		String fileName = file.getName();
		File f = new File(Consts.IMG_PATH+fileName);
		FileOutputStream fOut = null;
		if(!f.getParentFile().exists()){
			f.getParentFile().mkdirs();
		}
		if(!f.exists())
			try {
			fOut = new FileOutputStream(f);
			loadedImage.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if(fOut!=null)
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void setCallBack(ImageLoaderCallBack callback){
		mCallback=callback;
	}
	
	public interface ImageLoaderCallBack{
		public void refreshAdapter();
	}

}
