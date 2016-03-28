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
						if (conn.getResponseCode() != 200)    //��Internet��ȡ��ҳ,��������,����ҳ��������ʽ������
						{
							throw new RuntimeException("����urlʧ��");
						}
					    conn.disconnect();
						/*URL url = null;
						url = new URL("https://www.baidu.com/");
						InputStream in = url.openStream();
						System.out.println("��������");
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
	 * ��鵱ǰGPRS�Ƿ����ӣ�������˼�����Ƿ����ӣ������ǲ���GPRS
	 * 
	 * @param context
	 * @return true��ʾ��ǰ���紦������״̬������GPRS�����򷵻�false
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