package com.activity;

import com.http.HttpUtil;
import com.staticdata.StaticString;
import com.utilt.utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class GetPwdBack extends Activity {

	private String httpjson,phoneTxt,emailTxt;
	private ProgressDialog progressDialog; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_pwd_back);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.get_pwd_back, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			startActivity(new Intent(GetPwdBack.this, LoginActivity.class));
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

	public void btonclick(View v){
		int vid=v.getId();
		
		if(vid==R.id.getpwdback_back){
			startActivity(new Intent(GetPwdBack.this, LoginActivity.class));
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}
		if(vid==R.id.getpwdback_change){
			EditText phone=(EditText)findViewById(R.id.getpwdback_oldpwd);
			EditText email=(EditText)findViewById(R.id.getpwdback_newpwd);
			phoneTxt=phone.getText().toString();
			emailTxt=email.getText().toString();
			
			if(phoneTxt.length()==0||emailTxt.length()==0){
				Toast.makeText(getApplicationContext(), "请填全信息",Toast.LENGTH_SHORT).show();
				return ;
			}
			if(utils.isEmailNO(emailTxt)==false){
				Toast.makeText(getApplicationContext(), "邮箱格式不正确",Toast.LENGTH_SHORT).show();
				return ;
			}
			if(utils.isMobileNO(phoneTxt)==false){
				Toast.makeText(getApplicationContext(), "电话号码格式不正确",Toast.LENGTH_SHORT).show();
				return ;
			}
			
			progressDialog = ProgressDialog.show(GetPwdBack.this, "", "正在找回密码,请稍候！");
			
			new Thread(){
				public void run(){
					httpjson=HttpUtil.sendGet(StaticString.server+"getpwd", "phone="+phoneTxt+"&email="+emailTxt);
					Message msg_listData = new Message();
					if(httpjson.equals("ok")){
						msg_listData.what =2;
					}else{
						
						if(httpjson.equals("wrong")){
							msg_listData.what =0;
						}
						else{
							msg_listData.what =1;
						}	
					}
					
					handler.sendMessage(msg_listData);
				}
			}.start();
			
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
            switch (message.what) {
            case 0:
            	progressDialog.dismiss(); //关闭进度条
            	Toast.makeText(getApplicationContext(), "邮箱或电话与注册时信息不一致", Toast.LENGTH_SHORT).show();  
            	break;
            case 1:
            	progressDialog.dismiss(); //关闭进度条
            	Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_SHORT).show();  
            	break;
            case 2:
            	progressDialog.dismiss(); //关闭进度条
            	Toast.makeText(getApplicationContext(), "密码已经发送到您的邮箱～请查收", Toast.LENGTH_SHORT).show();  
            	break;
            }
		}
	};
	
}
