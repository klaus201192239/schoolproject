package com.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class NoticeDetail extends Activity {

	private String title,content,publisher,time;
	private TextView Ttitle,Tcontent,Tpublisher,Ttime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice_detail);
		
		intiData();
		intiView();
		intiViewData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notice_detail, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			Intent inte=getIntent();
			int x=inte.getIntExtra("ServiceFrom",-1);
			if(x==1){
				finish();
			}else{
				startActivity(new Intent(NoticeDetail.this,Notice.class));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			}
			
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			Toast.makeText(getApplicationContext(), "1111", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Toast.makeText(getApplicationContext(), "2222222",Toast.LENGTH_SHORT).show();
			return false;
		}
		return false;
	}
	
	public void intiData(){
		Intent intent=getIntent();
		title=intent.getStringExtra("title");
		content=intent.getStringExtra("content");
		publisher=intent.getStringExtra("publisher");
		time=intent.getStringExtra("time");
	}
	
	public void intiView(){
		Ttitle=(TextView)findViewById(R.id.noticedetail_title);
		Tcontent=(TextView)findViewById(R.id.noticedetail_content);
		Tpublisher=(TextView)findViewById(R.id.noticedetail_publisher);
		Ttime=(TextView)findViewById(R.id.noticedetail_time);
	}
	
	public void intiViewData(){
		Ttitle.setText(title);
		Tcontent.setText(Html.fromHtml(content));
		Tpublisher.setText(publisher);
		Ttime.setText(time);
	}
	
	public void btonclick(View v){
		startActivity(new Intent(NoticeDetail.this,Notice.class));
		finish();
		overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
	}
}
