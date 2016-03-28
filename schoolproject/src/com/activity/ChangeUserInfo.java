package com.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bean.SerializableMap;
import com.dbutil.DBHelper;
import com.http.HttpUtil;
import com.staticdata.StaticString;
import com.utilt.utils;

public class ChangeUserInfo extends Activity {

	private String httpjson;
	private ArrayAdapter<String> adapter1;
	private ProgressDialog progressDialog;
	private EditText changeinfo_phone;
	private EditText changeinfo_name;
	private RadioGroup radioGroup;
	private RadioButton radioMale;
	private RadioButton radioFemale;
	private TextView changeinfo_schoolname;
	private Spinner changeinfo_education;
	private EditText changeinfo_stuid;
	private Spinner changeinfo_major;
	private Spinner changeinfo_year;
	private EditText changeinfo_mail;
	private String obj[] ;
	private Map<String, String> majorMap;
	private int sex = -1, degree = -1, grade = -1;
	private String phone, majorname, name, majorid, schoolid  , schoolname, education, email,
			studentid,schoolimg;
	private boolean schooltag=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_user_info);
		intiView();
		intiViewData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_user_info, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(ChangeUserInfo.this, UserCenter.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return false;
		}
		return false;

	}

	public void intiView() {
		changeinfo_phone = (EditText) findViewById(R.id.changeinfo_phone);
		changeinfo_name = (EditText) findViewById(R.id.changeinfo_name);
		radioGroup = (RadioGroup) findViewById(R.id.changeinfo_radioGroup);
		radioMale = (RadioButton) findViewById(R.id.changeinfo_radioMale);
		radioFemale = (RadioButton) findViewById(R.id.changeinfo_radioFemale);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup arg0, int arg1) {
						// TODO Auto-generated method stub
						if (arg0.getCheckedRadioButtonId() == R.id.changeinfo_radioFemale) {
							sex = 1;
							radioMale.setChecked(false);
						} else {
							sex = 0;
							radioFemale.setChecked(false);
						}
					}
				});

		changeinfo_schoolname = (TextView) findViewById(R.id.changeinfo_schoolname);
		changeinfo_education = (Spinner) findViewById(R.id.changeinfo_education);
		changeinfo_education.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						education = ChangeUserInfo.this.getResources().getStringArray(R.array.educationArray)[arg2];
						if (education.equals("专科")) {
							degree = 0;
						}
						if (education.equals("本科")) {
							degree = 1;
						}
						if (education.equals("研究生")) {
							degree = 2;
						}
						if (education.equals("博士生")) {
							degree = 3;
						}
					}

					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

		changeinfo_stuid = (EditText) findViewById(R.id.changeinfo_stuid);
		changeinfo_major = (Spinner) findViewById(R.id.changeinfo_major);
		changeinfo_major.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						
						majorname = arg0.getItemAtPosition(arg2).toString();
						majorid = majorMap.get(majorname);

					}

					public void onNothingSelected(AdapterView<?> arg0) {
						
					}
				});

		changeinfo_year = (Spinner) findViewById(R.id.changeinfo_year);
		changeinfo_year.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						
						if(arg2==0){
							
						}else{
							String year = ChangeUserInfo.this.getResources().getStringArray(R.array.timeYearArray)[arg2];
							
							if(!year.equals("请  选  择")){
								grade = Integer.parseInt(year.substring(0, 4));
							}
						}
					}
					public void onNothingSelected(AdapterView<?> arg0) {
						
					}
				});
		changeinfo_mail = (EditText) findViewById(R.id.changeinfo_mail);
	}

	public void intiViewData() {
		Intent intent = getIntent();
		String from = intent.getStringExtra("from");
		if (from.equals("016")) {

			SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
			phone=set.getString("Phone", "");
			name=set.getString("Name","");
			sex=set.getInt("Sex", -1);
			schoolname=set.getString("SchoolName", "");
			schoolid=set.getString("ShoolId","");
			degree=set.getInt("Degree", -1);
			studentid=set.getString("StudentId", "");
			majorname=set.getString("MajorName", "");
			grade=set.getInt("Grade", -1);
			email=set.getString("Email", "");
						
			changeinfo_phone.setText(phone);
			changeinfo_name.setText(name);
			if(sex==0){
				radioMale.setChecked(true);
				radioFemale.setChecked(false);
			}else{
				radioFemale.setChecked(true);
				radioMale.setChecked(false);
			}
			changeinfo_schoolname.setText(schoolname);
			if (degree == 0) {
				changeinfo_education.setSelection(1, true);
			}
			if (degree == 1) {
				changeinfo_education.setSelection(2, true);
			}
			if (degree == 2) {
				changeinfo_education.setSelection(3, true);
			}
			if (degree == 3) {
				changeinfo_education.setSelection(4, true);
			}
			changeinfo_stuid.setText(studentid);
			
			majorMap = new HashMap<String,String>();		
			
			//这里获得该学校的所有专业信息
			progressDialog = ProgressDialog.show(ChangeUserInfo.this, "","正在获得专业信息,请稍候！");
			new Thread(){
				public void run(){
					
					SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
					String sid=set.getString("ShoolId","");
					String mymajor=set.getString("MajorName","");
					String json = HttpUtil.sendGet(StaticString.server + "getmajor", "schoolid="+sid);
					System.out.println(json);
					Message msg_listData = new Message();
					if (json.equals("error")) {
						msg_listData.what = 0;
					} else{
						try{
							JSONArray array=new JSONArray(json);
							int len=array.length();
							for(int i=0;i<len;i++){
								JSONObject obj=array.getJSONObject(i);
								majorMap.put(obj.getString("Name"), obj.getString("_id"));
							}
							int i = 0;
							obj=null;
							obj = new String[majorMap.size()];
							for (String key : majorMap.keySet()) {
								if(i==0){
									obj[0]=mymajor;
									System.out.println(obj[0]);
									i++;
								}
								if(!key.equals(mymajor)){
									
									obj[i] = key;
									System.out.println(obj[i]);
									i++;
								}
							}							
							msg_listData.what=1;
						}catch(Exception e){
							msg_listData.what = 0;
						}
					}
					handler.sendMessage(msg_listData);
				}
			}.start();
			
			changeinfo_year.setSelection(grade - 2005, true);
			changeinfo_mail.setText(email);
			
		} else {
		

		  if (intent.getStringExtra("phone") != null) {
				phone = intent.getStringExtra("phone");
				changeinfo_phone.setText(phone);
			}
			
			if (intent.getStringExtra("name") != null) {
				name = intent.getStringExtra("name");
				changeinfo_name.setText(name);
			}
			
			if (intent.getIntExtra("sex", -1) != -1) {
				sex = intent.getIntExtra("sex", -1);
				if (sex == 0) {
					radioFemale.setChecked(false);
					radioMale.setChecked(true);
				} else {
					radioFemale.setChecked(true);
					radioMale.setChecked(false);
				}
			}
			
			if (intent.getStringExtra("schoolid") != null&&intent.getStringExtra("schoolid").length()!=0) {
				schoolid = intent.getStringExtra("schoolid");
				schoolname = intent.getStringExtra("schoolname");
				schoolimg=intent.getStringExtra("SchoolImg");
				changeinfo_schoolname.setText(schoolname);
			}

			if (intent.getIntExtra("degree", -1) != -1) {
				degree = intent.getIntExtra("degree", -1);
				if (degree == 0) {
					changeinfo_education.setSelection(1, true);
				}
				if (degree == 1) {
					changeinfo_education.setSelection(2, true);
				}
				if (degree == 2) {
					changeinfo_education.setSelection(3, true);
				}
				if (degree == 3) {
					changeinfo_education.setSelection(4, true);
				}
			}

			if (intent.getStringExtra("studentid") != null) {
				studentid = intent.getStringExtra("studentid");
				changeinfo_stuid.setText(studentid);
			}
			
			{
				Bundle bundle = intent.getExtras();
				SerializableMap serializableMap = (SerializableMap) bundle.get("majormap");
				majorMap = serializableMap.getMap();
				int i = 0;
				obj = new String[majorMap.size()];
				for (String key : majorMap.keySet()) {
					obj[i] = key;
					i++;
				}
				adapter1 = new ArrayAdapter<String>(ChangeUserInfo.this,android.R.layout.simple_spinner_item, obj);
				adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);				
				changeinfo_major.setAdapter(adapter1);
				
				//adapter1 = new ArrayAdapter<String>(this,R.layout.register_school_item, obj);
				//changeinfo_major.setAdapter(adapter1);
			}
			
			if (intent.getIntExtra("grade", -1) != -1) {
				grade = intent.getIntExtra("grade", -1);
				changeinfo_year.setSelection(grade - 2005, true);
			}

			if (intent.getStringExtra("email") != null) {
				email = intent.getStringExtra("email");
				changeinfo_mail.setText(email);
			}
			
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case 0:
				progressDialog.dismiss(); // 关闭进度条
				Toast.makeText(getApplicationContext(), "网络连接或其他意外错误",Toast.LENGTH_SHORT).show();
				break;
			case 1:
				progressDialog.dismiss(); // 关闭进度条
				
			/*	adapter1 = new ArrayAdapter<String>(ChangeUserInfo.this,R.layout.register_school_item, obj);
				changeinfo_major.setAdapter(adapter1);
				changeinfo_major.setSelection(0, true);*/
				
				
				adapter1 = new ArrayAdapter<String>(ChangeUserInfo.this,android.R.layout.simple_spinner_item, obj);
				adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);				
				changeinfo_major.setAdapter(adapter1);
				changeinfo_major.setSelection(0, true);				
				
				break;
			case 2:
				progressDialog.dismiss(); // 关闭进度条				
				
				if(schooltag==true){
					Toast.makeText(getApplicationContext(), "修改成功哦,请重新登录",Toast.LENGTH_SHORT).show();
					
					DBHelper dbhelper=new DBHelper(ChangeUserInfo.this);
					dbhelper.CreatOrOpen("schooltime");
					dbhelper.excuteInfo("delete from attendactivity;");
					dbhelper.excuteInfo("delete from myactivity;");
					dbhelper.closeDB();
					
					SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
					SharedPreferences.Editor editor = set.edit();
					editor.putInt("LoginState", 0);
					editor.commit();		
					Intent intent=new Intent(ChangeUserInfo.this,MainActivity.class);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				}
				else{
					Toast.makeText(getApplicationContext(), "修改成功哦",Toast.LENGTH_SHORT).show();
					Intent intent=new Intent(ChangeUserInfo.this,UserCenter.class);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
					
				}
				
				break;
			case 3:
				progressDialog.dismiss(); // 关闭进度条
				Toast.makeText(getApplicationContext(), "修改失败，您输入的电话已被他人注册",Toast.LENGTH_SHORT).show();
				break;
			case 4:
				progressDialog.dismiss(); // 关闭进度条
				Toast.makeText(getApplicationContext(), "修改失败，您输入的邮箱已被他人使用",Toast.LENGTH_SHORT).show();
				break;
			case 5:
				progressDialog.dismiss(); // 关闭进度条
				Toast.makeText(getApplicationContext(), "修改失败，您输入的学号已被他人使用",Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	public void btonclik(View v){
		int vid=v.getId();
		if(vid==R.id.changeinfo_back){
			Intent intent = new Intent(ChangeUserInfo.this, UserCenter.class);
			startActivity(intent);
			finish();
			
			overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
			return ;
		}
		if(vid==R.id.changeinfo_schoolname){
			
			Intent intent = new Intent(ChangeUserInfo.this,ChangeUserSchool.class);
			
			phone= changeinfo_phone.getText().toString().trim();
			intent.putExtra("phone", phone);
			
			name = changeinfo_name.getText().toString().trim();
			if(name!=null){
				intent.putExtra("name", name);
			}
			
			if(sex!=-1){
				intent.putExtra("sex", sex);
			}
			
			if(degree!=-1){
				intent.putExtra("degree", degree);
			}
			
			studentid = changeinfo_stuid.getText().toString().trim();
			if(studentid!=null){
				intent.putExtra("studentid", studentid);
			}
			
			if(grade!=-1){
				intent.putExtra("grade", grade);
			}
			
			email = changeinfo_mail.getText().toString().trim();
			if(email!=null){
				intent.putExtra("email", email);
			}
			
			startActivity(intent);
			finish();
			
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			
			return ;
		}
		
		if (vid == R.id.changeinfo_upload) {
			
			if(name==null||name.length()==0){
				Toast.makeText(getApplicationContext(), "请填写姓名",Toast.LENGTH_SHORT).show();
				return ;
			}
			if(schoolid==null||schoolid.length()==0){
				Toast.makeText(getApplicationContext(), "请选择学校",Toast.LENGTH_SHORT).show();
				return ;
			}
			if(sex==-1){
				Toast.makeText(getApplicationContext(), "请选择性别",Toast.LENGTH_SHORT).show();
				return ;
			}
			if(degree==-1){
				Toast.makeText(getApplicationContext(), "请选择学历",Toast.LENGTH_SHORT).show();
				return ;
			}
			if(studentid==null||studentid.length()==0){
				Toast.makeText(getApplicationContext(), "请填写学号",Toast.LENGTH_SHORT).show();
				return ;
			}
			if(grade==-1){
				Toast.makeText(getApplicationContext(), "请选择入学年份",Toast.LENGTH_SHORT).show();
				return ;
			}
			if(majorid==null||majorid.length()==0){
				Toast.makeText(getApplicationContext(), "请选择专业",Toast.LENGTH_SHORT).show();
				return ;
			}
			if(email==null||email.length()==0){
				Toast.makeText(getApplicationContext(), "请填写电子邮件",Toast.LENGTH_SHORT).show();
				return ;
			}
			if(utils.isEmailNO(email)==false){
				Toast.makeText(getApplicationContext(), "电子邮件格式错误",Toast.LENGTH_SHORT).show();
				return ;
			}
			
			SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
			String sch=set.getString("ShoolId", "");
			
			Builder builder=new AlertDialog.Builder(ChangeUserInfo.this);
			
			if(sch.equals(schoolid)){
				builder.setTitle("修改个人信息");
				builder.setMessage("确定修改个人信息么？？");
			}else{
				schooltag=true;
				builder.setTitle("修改个人信息");
				builder.setMessage("您即将修改所在学校，系统将会清除所有之前您与学校相关的数据，请谨慎修改");
			}

			builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){
					
					phone= changeinfo_phone.getText().toString().trim();			
					name = changeinfo_name.getText().toString().trim();						
					studentid = changeinfo_stuid.getText().toString().trim();
					email = changeinfo_mail.getText().toString().trim();

					progressDialog = ProgressDialog.show(ChangeUserInfo.this, "","正在上传信息,请稍候！");
					new Thread() {
						public void run() {
							String para="";
							SharedPreferences set1 = getSharedPreferences("schooltime", MODE_PRIVATE);
							String _id=set1.getString("Id","");
							try {
								para = "_id=" + _id +"&phone=" + phone + "&name="
										+ URLEncoder.encode(name,"UTF-8").toString() + "&sex=" + sex + "&schoolid=" + schoolid
										+ "&degree=" + degree + "&studentid=" + studentid
										+ "" + "&majorid=" + majorid + "&grade=" + grade
										+ "&email=" + email+"&majorname="+URLEncoder.encode(majorname,"UTF-8").toString();
							} catch (UnsupportedEncodingException e1) {
								e1.printStackTrace();
							}
							
							httpjson = HttpUtil.sendGet(StaticString.server + "changeinfo", para);
							
							Message msg_listData = new Message();
							if (httpjson.equals("error")) {
								msg_listData.what = 0;
							} else {
								if (httpjson.startsWith("wrong")) {
									msg_listData.what = Integer.parseInt(httpjson.substring(5));
								}else{
									SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
									SharedPreferences.Editor editor = set.edit();
									editor.putString("Phone", phone);
									editor.putString("Name", name);
									editor.putInt("Sex", sex);
									editor.putString("ShoolId", schoolid);
									editor.putString("SchoolName", schoolname);
									editor.putString("SchoolImg", schoolimg);
									editor.putInt("Degree", degree);
									editor.putString("StudentId", studentid);
									editor.putString("MajorId", majorid);
									editor.putString("MajorName", majorname);
									editor.putInt("Grade", grade);
									editor.putString("Email", email);	
									editor.commit();
									msg_listData.what = 2;
								}

							}
							handler.sendMessage(msg_listData);
						}
					}.start();	
				}
			});
			builder.setNegativeButton("放弃",null);
			builder.create().show();

		}
	}
}
