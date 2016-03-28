package com.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TestNet extends Activity {
	private TextView ttt;
	//private String ttstring;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_net);
			
		ttt=(TextView)findViewById(R.id.ttt);
		
		ttt.setOnClickListener(new OnClickListener() {
			

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Intent  intent = new Intent(TestNet.this,MainActivity.class);  
				PendingIntent pendingIntent = PendingIntent.getActivity(TestNet.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);  
				
				Notification.Builder builder = new Notification.Builder(TestNet.this)  .setAutoCancel(true)  
	            .setContentTitle("title")  
	            .setContentText("describe")  
	            .setContentIntent(pendingIntent)  
	            .setSmallIcon(R.drawable.ic_launcher)  
	            .setWhen(System.currentTimeMillis())  
	            .setOngoing(true);  
				@SuppressWarnings("unused")
				Notification notification=builder.getNotification();  
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_net, menu);
		return true;
	}
	

}
