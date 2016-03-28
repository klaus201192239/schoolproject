package com.activity;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class SystemSettings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_settings);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.system_settings, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(SystemSettings.this, UserCenter.class));
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
		}
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			Toast.makeText(getApplicationContext(), "1111", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Toast.makeText(getApplicationContext(), "2222222",Toast.LENGTH_SHORT).show();
			return false;
		}
		return false;

	}
	
	public void onclick(View v){
		int vid=v.getId();
		if(vid==R.id.system_back){
			startActivity(new Intent(SystemSettings.this, UserCenter.class));
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}
		if(vid==R.id.system_lv_two){
			startActivity(new Intent(SystemSettings.this, ChangePwd.class));
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return ;
		}
		if(vid==R.id.system_lv_three){
			startActivity(new Intent(SystemSettings.this, UpdateSystems.class));
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return ;
		}
		if(vid==R.id.system_lv_four){

			exe();
			
			return ;
		}
	}
	
	public void exe(){
		Builder builder=new AlertDialog.Builder(SystemSettings.this);
		builder.setTitle("系统退出");
		builder.setMessage("确定退出系统？");
		builder.setPositiveButton("确定退出", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which){
				SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
				SharedPreferences.Editor editor = set.edit();
				editor.putInt("LoginState", 0);
				editor.commit();		
				Intent intent=new Intent(SystemSettings.this,MainActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			}
		});
		builder.setNegativeButton("暂不退出",null);
		builder.create().show();
	}
	
}
