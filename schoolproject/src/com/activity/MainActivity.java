package com.activity;

import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import com.broadcast.NetBroadcast;
import com.staticdata.StaticBoolean;
import com.staticdata.StaticString;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Date date=new Date();
		StaticString.loginTime=date.toString();
		
		SharedPreferences setting = getSharedPreferences("schooltime", MODE_PRIVATE); 
		int RegisterFirst=setting.getInt("RegisterFirst", -1);
	
		
		if(RegisterFirst==-1){
			Intent intent=new Intent(MainActivity.this,GuideActivity.class);
			startActivity(intent);
			finish();
		}else{
			
			int LoginState=setting.getInt("LoginState", 0);
			if(LoginState==0){
				Intent intent=new Intent(MainActivity.this,LoginActivity.class);
				startActivity(intent);
				finish();
			}
			else{
				StaticBoolean.NetLink=jugeRegNet();
				//StaticBoolean.NetLink=false;
				Intent intent=new Intent(MainActivity.this,LoaderData.class);
				startActivity(intent);
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public Boolean jugeRegNet(){
		
		ConnectivityManager cm=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info=cm.getActiveNetworkInfo();
		if(info!=null){
			return true;
		}
		if(info==null){
			return false;
		}
		@SuppressWarnings("unused")
		Intent intent=new Intent();
		NetBroadcast net=new NetBroadcast();
		net.onReceive(this, intent);
	    return true;
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
            switch (message.what) {
            }
		}
	};
}
