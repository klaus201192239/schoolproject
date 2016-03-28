package com.activity;

import com.http.HttpUtil;
import com.service.CoreService;
import com.staticdata.StaticBoolean;
import com.staticdata.StaticInt;
import com.staticdata.StaticString;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePwd extends Activity {
	
	private String userid;
	private EditText ETold,ETnew,ETnew1;
	private String oldpwd,newpwd,newpwd1;
	private String httpjson;
	private ProgressDialog progressDialog; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_pwd);
		
		if(StaticBoolean.NetLink==false){
			Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_SHORT).show();  
		}
		
		ETold=(EditText)findViewById(R.id.changepwd_oldpwd);
		ETnew=(EditText)findViewById(R.id.changepwd_newpwd);
		ETnew1=(EditText)findViewById(R.id.changepwd_pwdagain);
		SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
		userid=set.getString("Id","");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_pwd, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(ChangePwd.this,SystemSettings.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
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
	
	public void btonclick(View v){
		int vid=v.getId();
		
		if(vid==R.id.changepwd_getback){
			SharedPreferences sett = getSharedPreferences("schooltime", MODE_PRIVATE);
			Builder builder=new AlertDialog.Builder(ChangePwd.this);
			builder.setTitle("发送附件请求");
			builder.setMessage("系统将会发送该附件至您的"+sett.getString("Email", "")+"邮箱中～");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){
					Intent intent2 = new Intent(ChangePwd.this, CoreService.class);
					intent2.putExtra("tag", StaticInt.getbackpwd);
					startService(intent2);
				}
			});
			builder.setNegativeButton("NO",null);
			builder.create().show();
			return ;
		}
		
		if(vid==R.id.changepwd_back){
			Intent intent=new Intent(ChangePwd.this,SystemSettings.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}else{
			if(StaticBoolean.NetLink==false){
				Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_SHORT).show();  
				return ;
			}
			oldpwd=ETold.getText().toString().trim();
			newpwd=ETnew.getText().toString().trim();
			newpwd1=ETnew1.getText().toString().trim();
			if(oldpwd==null||oldpwd.length()==0){
				Toast.makeText(getApplicationContext(), "请填写原密码", Toast.LENGTH_SHORT).show();  
				return ;
			}
			if(newpwd==null||newpwd.length()==0){
				Toast.makeText(getApplicationContext(), "请填写新密码", Toast.LENGTH_SHORT).show();  
				return ;
			}
			if(newpwd==null||newpwd.length()==0){
				Toast.makeText(getApplicationContext(), "请再次填写新密码", Toast.LENGTH_SHORT).show();  
				return ;
			}
			if(!newpwd.equals(newpwd1)){
				Toast.makeText(getApplicationContext(), "两次输入的新密码不一样", Toast.LENGTH_SHORT).show();  
				return ;
			}
			SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
			String password=set.getString("Pwd", "");
			if(!oldpwd.equals(password)){
				Toast.makeText(getApplicationContext(), "原密码输入有误", Toast.LENGTH_SHORT).show();
				return ;
			}
			
			progressDialog = ProgressDialog.show(ChangePwd.this, "", "正在修改密码,请稍候！");
			
			new Thread(){
				public void run(){
					Message msg_listData = new Message();
					httpjson=HttpUtil.sendGet(StaticString.server+"changepwd", "userid="+userid+"&pwd="+newpwd);
					if(httpjson.equals("ok")){
						msg_listData.what=1;
						SharedPreferences sett = getSharedPreferences("schooltime", MODE_PRIVATE);
						SharedPreferences.Editor editor = sett.edit();
						editor.putString("Pwd", newpwd);
						editor.commit();
					}else{
						msg_listData.what=0;
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
            	Toast.makeText(getApplicationContext(), "网络连接或其他意外错误", Toast.LENGTH_SHORT).show();  
            	break;
            case 1:
            	progressDialog.dismiss(); //关闭进度条
            	Toast.makeText(getApplicationContext(), "密码修改成功,请重新登录", Toast.LENGTH_SHORT).show();
            	
            	SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
				SharedPreferences.Editor editor = set.edit();
				editor.putInt("LoginState", 0);
				editor.commit();		
				Intent intent=new Intent(ChangePwd.this,MainActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            	break;
            }
		}
	};
}
