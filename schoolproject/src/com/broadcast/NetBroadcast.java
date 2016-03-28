package com.broadcast;

import java.net.HttpURLConnection;
import java.net.URL;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.staticdata.StaticBoolean;

public class NetBroadcast extends BroadcastReceiver {
	private boolean WifiState;

	@Override
	public void onReceive(Context context, Intent intent) {

		if (isNetworkConnected(context)) {
			System.out.println("isNetworkConnected");
			StaticBoolean.NetLink = true;
			if (isGprsConnected(context)) {
				System.out.println("isGprsConnected");
				StaticBoolean.GPRSLink = true;
			} else {
				StaticBoolean.GPRSLink = false;
				if (isWifiConnected(context)) {
					System.out.println("isWifiConnected");
					StaticBoolean.WifiLink = true;
				} else {
					StaticBoolean.WifiLink = false;
					StaticBoolean.NetLink = false;
				}
			}
		} else {
			System.out.println("NotisNetworkConnected");
			StaticBoolean.NetLink = false;
		}
	}

	private boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	private boolean isWifiConnected(Context context) {
		WifiState = false;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()
				&& ConnectivityManager.TYPE_WIFI == info.getType()) {
			Thread ss = new Thread() {
				public void run() {
					try {
						URL url = new URL("https://www.baidu.com/");
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setConnectTimeout(5*1000);
						if (conn.getResponseCode() != 200)    //从Internet获取网页,发送请求,将网页以流的形式读回来
						{
							throw new RuntimeException("请求url失败");
						}
					    conn.disconnect();
						/*URL url = null;
						url = new URL("https://www.baidu.com/");
						InputStream in = url.openStream();
						System.out.println("连接正常");
						in.close();*/
						WifiState = true;
					} catch (Exception e) {
						WifiState = false;
					}
				}
			};
			ss.start();
			try {
				ss.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return WifiState;
	}

	/**
	 * 检查当前GPRS是否连接，两层意思——是否连接，连接是不是GPRS
	 * 
	 * @param context
	 * @return true表示当前网络处于连接状态，且是GPRS，否则返回false
	 */
	private boolean isGprsConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()
				&& ConnectivityManager.TYPE_MOBILE == info.getType()) {
			return true;
		}
		return false;
	}
}