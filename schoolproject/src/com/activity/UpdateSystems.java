package com.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateSystems extends Activity {

	private TextView upload_sys_now,upload_sys_new,upload_sys_goods;
	private String newVersion,goods,nowversion;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_systems);
		
		intiView();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_systems, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(UpdateSystems.this, SystemSettings.class));
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
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
	
	public void intiView(){
		upload_sys_now=(TextView)findViewById(R.id.upload_sys_now);
		upload_sys_new=(TextView)findViewById(R.id.upload_sys_new);
		upload_sys_goods=(TextView)findViewById(R.id.upload_sys_goods);
		
		SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
		nowversion=getVersion();
		newVersion=set.getString("Version", nowversion);
		goods=set.getString("VersionGood", "goog is goood !!");
		
		upload_sys_now.setText(nowversion);
		upload_sys_new.setText(newVersion);
		upload_sys_goods.setText(goods);
	}
	
	public void btonclick(View v){
		int vid=v.getId();
		if(vid==R.id.updatesys_back){
			startActivity(new Intent(UpdateSystems.this, SystemSettings.class));
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}
		if(vid==R.id.updatesys_change){
		
			if(nowversion.equals(newVersion)){
				Toast.makeText(getApplicationContext(), "亲，当前已经是最新版本了～", Toast.LENGTH_SHORT).show();
				return ;
			}

			Builder builder=new AlertDialog.Builder(UpdateSystems.this);
			builder.setTitle("系统升级");
			builder.setMessage("欢迎您升级到最新版本～");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){
					Intent intent2 = new Intent(UpdateSystems.this, DownloadApk.class);
					startActivity(intent2);
					finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
				}
			});
			builder.setNegativeButton("取消",null);
			builder.create().show();

		}
	}
	
	public String getVersion() {
		String ver="error";
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			ver = info.versionName;
		} catch (Exception e) {
			
		}
		return ver;
	}
}
