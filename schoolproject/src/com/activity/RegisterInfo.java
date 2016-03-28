package com.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import org.json.JSONException;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bean.SerializableMap;
import com.http.HttpUtil;
import com.staticdata.StaticString;
import com.utilt.utils;

public class RegisterInfo extends Activity {

	private String httpjson;
	private ProgressDialog progressDialog;
	private EditText register2_name;
	private RadioGroup radioGroup;
	private RadioButton radioMale;
	private RadioButton radioFemale;
	private TextView register2_schoolname;
	private Spinner register2_education;
	private EditText register2_stuid;
	private Spinner register2_major;
	private Spinner register2_year;
	private EditText register2_mail;
	private String obj[] = {"请 选 择"};
	private Map<String, String> majorMap;
	private int sex=-1, degree=-1, grade=-1;
	private String phone, schoolid, pwd, majorname, name, schoolname, education, email,
			studentid,majorid,schoolimg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_info);
		intiView();
		intiViewData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_info, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(RegisterInfo.this, LoginActivity.class);
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

	public void intiView() {
		register2_name = (EditText) findViewById(R.id.register2_name);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioMale = (RadioButton) findViewById(R.id.radioMale);
		radioFemale = (RadioButton) findViewById(R.id.radioFemale);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if (arg0.getCheckedRadioButtonId() == R.id.radioFemale) {
					sex = 1;
					radioMale.setChecked(false);
				} else {
					sex = 0;
					radioFemale.setChecked(false);
				}
			}
		});

		radioFemale.setChecked(true);
		sex = 1;
		
		register2_schoolname = (TextView) findViewById(R.id.register2_schoolname);
		register2_education = (Spinner) findViewById(R.id.register2_education);
		register2_education.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						education = RegisterInfo.this.getResources().getStringArray(R.array.educationArray)[arg2];
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
		
		register2_stuid = (EditText) findViewById(R.id.register2_stuid);
		register2_major = (Spinner) findViewById(R.id.register2_major);
		register2_major.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						majorname = arg0.getItemAtPosition(arg2).toString();
						if (!majorname.equals("请 选 择")) {
							majorid = majorMap.get(majorname);
						}
					}
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
		
		register2_year = (Spinner) findViewById(R.id.register2_year);
		register2_year.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {						
						if(arg2==0){
							
						}else{
							String year = RegisterInfo.this.getResources().getStringArray(R.array.timeYearArray)[arg2];
							if(!year.equals("请  选  择")){
								grade = Integer.parseInt(year.substring(0, 4));
							}
						}
						
					}
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
		register2_mail = (EditText) findViewById(R.id.register2_mail);
	}

	public void intiViewData() {

		Intent intent = getIntent();
		String from = intent.getStringExtra("From");
		if (from.equals("02")) {
			phone = intent.getStringExtra("Phone");
			pwd = intent.getStringExtra("Pwd");
			
			ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, obj);
			adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			register2_major.setAdapter(adapter2);
			
			register2_major.setSelection(0, true);
		} else {
			phone = intent.getStringExtra("phone");
			pwd = intent.getStringExtra("pwd");
			if (intent.getStringExtra("name") != null) {
				name = intent.getStringExtra("name");
				register2_name.setText(name);
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
			if (intent.getStringExtra("schoolid").length()!=0&&intent.getStringExtra("schoolid")!=null) {
				schoolid = intent.getStringExtra("schoolid");
				schoolname = intent.getStringExtra("schoolname");
				schoolimg=intent.getStringExtra("SchoolImg");
				register2_schoolname.setText(schoolname);
			}

			if (intent.getIntExtra("degree", -1) != -1) {
				degree = intent.getIntExtra("degree", -1);
				if (degree == 0) {
					register2_education.setSelection(1, true);
				}
				if (degree == 1) {
					register2_education.setSelection(2, true);
				}
				if (degree == 2) {
					register2_education.setSelection(3, true);
				}
				if (degree == 3) {
					register2_education.setSelection(4, true);
				}
			}

			if (intent.getStringExtra("studentid") != null&&intent.getStringExtra("studentid").length()!=0) {
				studentid = intent.getStringExtra("studentid");
				register2_stuid.setText(studentid);
			}
			{
				Bundle bundle = intent.getExtras();
				SerializableMap serializableMap = (SerializableMap) bundle.get("majormap");
				majorMap = serializableMap.getMap();
				int i = 0;
				obj = new String[majorMap.size()];;
				for (String key : majorMap.keySet()) {
					obj[i] = key;
					i++;
				}
	//			ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,R.layout.register_school_item, obj);
//				adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
				
				ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, obj);
				adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				register2_major.setAdapter(adapter2);
				
				register2_major.setSelection(0, true);
			}
			if (intent.getIntExtra("grade", -1) != -1) {
				grade = intent.getIntExtra("grade", -1);
				register2_year.setSelection(grade-2005, true);
			}

			if (intent.getStringExtra("email") != null) {
				email = intent.getStringExtra("email");
				register2_mail.setText(email);
			}
		}
	}

	public void btonclik(View v) {
		int viewId = v.getId();		
		if(viewId==R.id.regisinfo_back){
			Intent intent = new Intent(RegisterInfo.this, LoginActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}

		if (viewId == R.id.registerinfo_upload) {
			// phone,pwd
			name = register2_name.getText().toString().trim();
			// sex schoolid schoolname degree
			studentid = register2_stuid.getText().toString().trim();
			// majorid majorname grade
			email = register2_mail.getText().toString().trim();
			
			if(name==null||name.length()==0){
				Toast.makeText(getApplicationContext(), "请填写姓名",Toast.LENGTH_SHORT).show();
				return ;
			}
			
			if(utils.isRealName(name)==false){
				Toast.makeText(getApplicationContext(), "姓名中只能包含汉字和英文",Toast.LENGTH_SHORT).show();
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
			
			if(utils.isIntegralNO(studentid)==false){
				
				Toast.makeText(getApplicationContext(), "学号只能由数字组成",Toast.LENGTH_SHORT).show();
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
			progressDialog = ProgressDialog.show(RegisterInfo.this, "","正在上传信息,请稍候！");
			new Thread() {
				public void run() {
					String para="";
					try {
						para = "phone=" + phone + "&pwd=" + pwd + "&name="
								+ URLEncoder.encode(name,"UTF-8").toString() + "&sex=" + sex + "&schoolid=" + schoolid
								+ "&degree=" + degree + "&studentid=" + studentid
								+ "" + "&majorid=" + majorid + "&grade=" + grade
								+ "&email=" + email+"&majorname="+URLEncoder.encode(majorname,"UTF-8").toString();
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					httpjson = HttpUtil.sendGet(StaticString.server + "registerinfo", para);
					Message msg_listData = new Message();
					if (httpjson.equals("error")) {
						msg_listData.what = 0;
					} else {
						if (httpjson.equals("Wrong")) {
							msg_listData.what = 1;
						} else {
							String id="";
							try {
								JSONObject ob=new JSONObject(httpjson);
								id=ob.getString("_id");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
							SharedPreferences.Editor editor = set.edit();
							editor.putString("Id", id);
							editor.putString("Phone", phone);
							editor.putString("Pwd", pwd);
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
							editor.putInt("RegisterFirst", 1);
							editor.putInt("LoginState", 1);
							editor.commit();
							msg_listData.what = 2;
						}
					}
					handler.sendMessage(msg_listData);
				}
			}.start();

		} else {
			if (viewId == R.id.register2_schoolname) {
				// phone,pwd
				name = register2_name.getText().toString().trim();
				// sex degree
				studentid = register2_stuid.getText().toString().trim();
				// grade
				email = register2_mail.getText().toString().trim();
				Intent inte = new Intent(RegisterInfo.this,RegisterSchool.class);
				inte.putExtra("phone", phone);
				inte.putExtra("pwd", pwd);
				if(name!=null){
					inte.putExtra("name", name);
				}
				if(sex!=-1){
					inte.putExtra("sex", sex);
				}
				if(degree!=-1){
					inte.putExtra("degree", degree);
				}
				if(studentid!=null){
					inte.putExtra("studentid", studentid);
				}
				if(grade!=-1){
					inte.putExtra("grade", grade);
				}
				if(email!=null){
					inte.putExtra("email", email);
				}
				startActivity(inte);
				finish();
				overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
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
				Toast.makeText(getApplicationContext(), "您的学号或邮箱已被他人使用，请仔细检查",Toast.LENGTH_SHORT).show();
				break;
			case 2:
				progressDialog.dismiss(); // 关闭进度条
				Toast.makeText(getApplicationContext(),"恭喜您～注册成功",Toast.LENGTH_SHORT).show();
				jump();
				break;
			}
		}
	};

	public void jump() {
		Intent inte = new Intent(RegisterInfo.this,LoaderData.class);
		startActivity(inte);
		finish();
		overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
	}

}
