package com.activity;

import com.http.HttpUtil;
import com.staticdata.StaticBoolean;
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

public class RegisterUser extends Activity {

	private String Phone;
	private String Pwda;
	private String Pwdb;
	private EditText EditPhone;
	private EditText EditPwda;
	private EditText EditPwdb;
	private String httpjson;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_user);
		intiView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_user, menu);
		return true;
	}

	public void intiView() {
		EditPhone = (EditText) findViewById(R.id.register1_userid);
		EditPwda = (EditText) findViewById(R.id.register1_pwd);
		EditPwdb = (EditText) findViewById(R.id.register1_pwd_again);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(RegisterUser.this, LoginActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
		}
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return false;
		}
		return false;

	}

	public void btOnclick(View view) {
		int viewId = view.getId();
		
		if(viewId==R.id.register111_back){
			Intent intent = new Intent(RegisterUser.this, LoginActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}
		
		if (viewId == R.id.register1_upload) {

			if (StaticBoolean.NetLink == false) {
				Toast.makeText(getApplicationContext(), "�������Ӳ�����",Toast.LENGTH_SHORT).show();
				return;
			}

			Phone = EditPhone.getText().toString().trim();
			Pwda = EditPwda.getText().toString().trim();
			Pwdb = EditPwdb.getText().toString().trim();

			if (Phone.length() == 0 || Pwda.length() == 0 || Pwdb.length() == 0) {
				Toast.makeText(getApplicationContext(), "����ȫ��Ϣ",
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(utils.isMobileNO(Phone)==false){
				Toast.makeText(getApplicationContext(), "�ֻ������ʽ����ȷ",Toast.LENGTH_SHORT).show();
				return;
			}
			
			
			if(utils.isPwdNO(Pwda)==false){
				Toast.makeText(getApplicationContext(), "������Ӣ����ĸ|����|�»������", Toast.LENGTH_SHORT).show();  
				return ;
			}
			
			
			if (!Pwda.equals(Pwdb)) {
				Toast.makeText(getApplicationContext(), "�����������벻һ��",Toast.LENGTH_SHORT).show();
				return;
			}

			
			if(Pwda.length()>12||Pwda.length()<6){
				Toast.makeText(getApplicationContext(), "���볤��Ϊ6��12λ",Toast.LENGTH_SHORT).show();
				return;
			}
			
			progressDialog = ProgressDialog.show(RegisterUser.this, "","������֤�ֻ���,���Ժ�");
			new Thread() {
				public void run() {
					httpjson = HttpUtil.sendGet(StaticString.server+"phoneregister", "phone="+Phone);
					Message msg_listData = new Message();

					if (httpjson.equals("error")) {
						msg_listData.what = 0;
					} else {
						if(httpjson.equals("OK")){
							msg_listData.what = 1;
						}else{
							msg_listData.what = 2;
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
				progressDialog.dismiss(); // �رս�����
				Toast.makeText(getApplicationContext(), "�������ӻ������������",Toast.LENGTH_SHORT).show();
				break;
			case 1:
				progressDialog.dismiss(); // �رս�����
				Toast.makeText(getApplicationContext(), "�ֻ��������ע��",Toast.LENGTH_SHORT).show();
				pagejump();
				break;
			case 2:
				progressDialog.dismiss(); // �رս�����
				Toast.makeText(getApplicationContext(), "�ֻ������Ѿ�ע�����",Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	public void pagejump(){
		Intent intent = new Intent(RegisterUser.this, RegisterInfo.class);
		intent.putExtra("From","02");
		intent.putExtra("Phone", Phone);
		intent.putExtra("Pwd", Pwda);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
	}
}
