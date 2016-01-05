package com.zjjin.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
	public static boolean checkNetwork(final Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
		if (activeNetwork == null) {
			// 没网
			return false;
			/*AlertDialog.Builder dialog = new Builder(context);
			dialog.setTitle("无网");
			dialog.setMessage("亲，没网不能上网啊");
			dialog.setPositiveButton("打开网络", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {

						Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
						context.startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			dialog.setNegativeButton("取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog.show();*/

		}
		return true;
	}
}