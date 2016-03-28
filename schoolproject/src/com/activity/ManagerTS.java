package com.activity;

import org.json.JSONArray;
import com.http.HttpUtil;
import com.staticdata.StaticBoolean;
import com.staticdata.StaticString;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class ManagerTS extends Activity {

	private String oganizationid;
	private String phone,pwd;
	private ProgressDialog progressDialog;
	private JSONArray array;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager_ts);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manager_t, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(ManagerTS.this,UserCenter.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
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
	
	public void  btonclick(View v){
		int vid=v.getId();
		if(vid==R.id.managerts_back){
			Intent intent=new Intent(ManagerTS.this,UserCenter.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}
		
		if(StaticBoolean.NetLink==false){
			Toast.makeText(getApplicationContext(), "请检查网络连接～～", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		if(vid==R.id.managerts_student){
			SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
			phone=set.getString("Phone", "d");
			pwd=set.getString("Pwd", "d");
			progressDialog = ProgressDialog.show(ManagerTS.this, "", "正在验证身份信息,请稍候！");
	
			new Thread(){
				public void run(){
					String httpjson=HttpUtil.sendGet(StaticString.server+"loginweb", "phone="+phone+"&pwd="+pwd);
					Message msg_listData = new Message();
					if(httpjson.equals("error")){
						msg_listData.what=0;
					}else{
						if(httpjson.equals("wrong")){
							msg_listData.what=1;
						}else{
							if(httpjson.startsWith("ok")){								
								try{
									array=new JSONArray(httpjson.substring(2));
									int len=array.length();
									if(len==0){
										msg_listData.what=1;
									}else{
										if(len==1){
											oganizationid=array.getJSONObject(0).getString("id");
											msg_listData.what=2;
										}else{
											msg_listData.what=3;
										}
									}
								}catch(Exception e){
									
								}
							}else{
								msg_listData.what=0;
							}
						}
					}
					handler.sendMessage(msg_listData);
				}
			}.start();
			return ;
		}
		if(vid==R.id.managerts_teacher){
			Intent intent=new Intent(ManagerTS.this,ManagerTeacher.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return ;
		}
	}
	
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
            switch (message.what) {
            case 0:
            	progressDialog.dismiss(); //关闭进度条
            	Toast.makeText(getApplicationContext(), "网络连接或其他意外错误", Toast.LENGTH_SHORT).show();  
            	break;
            case 1:
            	progressDialog.dismiss(); //关闭进度条
            	Toast.makeText(getApplicationContext(), "您没有管理活动的权限，请联系您的组织部门", Toast.LENGTH_SHORT).show();  
            	break;
            case 2:
            	progressDialog.dismiss(); //关闭进度条
            	Toast.makeText(getApplicationContext(), "验证成功，正在跳转请稍后", Toast.LENGTH_SHORT).show();  
            	Intent intent=new Intent(ManagerTS.this,ManagerActivity.class);
            	intent.putExtra("oganizationid", oganizationid);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            	break;
            case 3:
            	progressDialog.dismiss(); //关闭进度条
            	Toast.makeText(getApplicationContext(), "验证成功，正在跳转请稍后", Toast.LENGTH_SHORT).show();  
            	Intent intent1=new Intent(ManagerTS.this,ChooseOrganization.class);
            	intent1.putExtra("oganization",array.toString());
				startActivity(intent1);
				finish();
				overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            	break;
            }
		}
	};
}
