package com.activity;

import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;
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
import android.widget.EditText;
import android.widget.Toast;
import com.http.HttpUtil;
import com.service.CoreService;
import com.staticdata.StaticBoolean;
import com.staticdata.StaticInt;
import com.staticdata.StaticString;
import com.utilt.utils;

public class LoginActivity extends Activity {
	
	private EditText phoneEdit;
	private EditText pwdEdit;
	private String phone;
	private String pwd;
	private String httpjson;
	private ProgressDialog progressDialog; 
	private static Boolean isExit = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.activity_login);
		
		phoneEdit=(EditText)findViewById(R.id.login_userid);
		pwdEdit=(EditText)findViewById(R.id.login_pwd);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click();
		}
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			Toast.makeText(getApplicationContext(), "1111", Toast.LENGTH_SHORT).show();  
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Toast.makeText(getApplicationContext(), "2222222", Toast.LENGTH_SHORT).show();  
			return false;
		}
		return false;

	}

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "Baby，再按就出去了～", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			finish();
			System.exit(0);
		}
	}
	
	public void btonclick(View view){
		int viewId=view.getId();
		if(viewId==R.id.login_login){
			
			phone=phoneEdit.getText().toString().trim();
			pwd=pwdEdit.getText().toString().trim();
			
			if(phone.length()==0||pwd.length()==0){
				Toast.makeText(getApplicationContext(), "请填全信息", Toast.LENGTH_SHORT).show();  
				return ;
			}
			
			if(utils.isMobileNO(phone)==false){
				Toast.makeText(getApplicationContext(), "手机号码格式不对", Toast.LENGTH_SHORT).show();  
				return ;
			}
			
			
			if(utils.isPwdNO(pwd)==false){
				Toast.makeText(getApplicationContext(), "密码由英文字母|数字|下划线组成", Toast.LENGTH_SHORT).show();  
				return ;
			}
			
			SharedPreferences setting = getSharedPreferences("schooltime", MODE_PRIVATE);
			int LoginState=setting.getInt("LoginState", -1);
			if(LoginState==-1){
				if(StaticBoolean.NetLink==false){
					Toast.makeText(getApplicationContext(), "网络接连不可用", Toast.LENGTH_SHORT).show();
					return ;
				}
				
				progressDialog = ProgressDialog.show(LoginActivity.this, "", "正在登录,请稍候！");
				
				new Thread(){
					public void run(){
						httpjson=HttpUtil.sendGet(StaticString.server+"login", "phone="+phone+"&pwd="+pwd);
						Message msg_listData = new Message();
						if(httpjson.equals("error")){
							msg_listData.what =0;
						}else{
							if(httpjson.equals("LoginError")){
								msg_listData.what =1;
							}else{
								try {
									JSONObject jsonobj=new JSONObject(httpjson);
									String id=jsonobj.get("_id").toString();
									String name=jsonobj.get("Name").toString();
									int sex=(Integer)jsonobj.get("Sex");
									String shcoolid=jsonobj.get("SchoolId").toString();
									String schoolname=jsonobj.get("SchoolName").toString();
									int degree=(Integer)jsonobj.get("Degree");
									String studentid=jsonobj.get("IdCard").toString();
									String majorid=jsonobj.get("MajorId").toString();
									String majorname=jsonobj.get("MajorName").toString();
									int grade=(Integer)jsonobj.get("Grade");
									String email=jsonobj.get("Email").toString();
									String schoolimg=jsonobj.get("SchoolImg").toString();
									
									SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
									SharedPreferences.Editor editor = set.edit();
									editor.putString("Id", id);
									editor.putString("Phone", phone);
									editor.putString("Pwd", pwd);
									editor.putString("Name", name);
									editor.putInt("Sex", sex);
									editor.putString("ShoolId", shcoolid);
									editor.putString("SchoolName", schoolname);
									editor.putString("SchoolImg", schoolimg);
									editor.putInt("Degree", degree);
									editor.putString("StudentId", studentid);
									editor.putString("MajorId", majorid);
									editor.putString("MajorName", majorname);
									editor.putInt("Grade", grade);
									editor.putString("Email", email);	
									editor.putInt("RegisterFirst", 1);
									editor.putInt("LoginState", 1);
									editor.commit();					
									
									msg_listData.what =2;
								} catch (Exception e) {
									msg_listData.what =0;
								}
							}
						}
		                handler.sendMessage(msg_listData);
					}
				}.start();
			}
			else{
				if(setting.getString("Phone","ww").equals(phone)&&setting.getString("Pwd", "0").equals(pwd)){
					
					SharedPreferences.Editor edito = setting.edit();
					edito.putInt("LoginState", 1);
					edito.commit();
					
					Intent intent=new Intent(LoginActivity.this,LoaderData.class);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
				}
				else{
					Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();  
				}
			}
			return ;
		}
		if(viewId==R.id.login_register){
			
			SharedPreferences setting = getSharedPreferences("schooltime", MODE_PRIVATE);
			int LoginState=setting.getInt("LoginState", -1);
			if(LoginState==-1){
				Intent intent=new Intent(LoginActivity.this,RegisterUser.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			}
			else{
				Toast.makeText(getApplicationContext(), "您已经注册过了~", Toast.LENGTH_SHORT).show();  
			}
			
		}
		if(viewId==R.id.login_getpwdback){
			Intent intent=new Intent(LoginActivity.this,GetPwdBack.class);
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
            	Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();  
            	break;
            case 2:
            	progressDialog.dismiss(); //关闭进度条
            	
            	Intent intent0=new Intent(LoginActivity.this,CoreService.class);
            	intent0.putExtra("tag", StaticInt.getmyactivityid);
				startService(intent0);
           	
            	Intent intent=new Intent(LoginActivity.this,LoaderData.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            	break;
            }
		}
	};
}
