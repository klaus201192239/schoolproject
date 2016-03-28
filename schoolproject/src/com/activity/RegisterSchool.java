package com.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.bean.SerializableMap;
import com.http.HttpUtil;
import com.staticdata.StaticString;

public class RegisterSchool extends Activity {

	private String mypro,mycity;
	private Intent intent;
	private int sex, degree, grade;
	private String phone, pwd, name, schoolname, email, studentid, schoolid;

	private ListView lv;
	private ArrayAdapter<String> myArrayAdapter;
	private List<String> list = new ArrayList<String>();
	private Map<String, String> mapschool = new HashMap<String, String>();// 用来存放所有学校的信息
	private Map<String, String> schoolimg = new HashMap<String, String>();// 用来存放所有学校的图片
	private Map<String, Map<String, String>> mapmajor = new HashMap<String, Map<String, String>>();
	// 用来存放所有的学校的专业信息，每一个Map里的Map，都存储着一个学校的专业
	private String httpjson;
	private ProgressDialog progressDialog;

	private JSONArray jsonCity;
	private ArrayAdapter<String> adapterpro;
	private ArrayAdapter<String> adaptercity;
	private String objpro[];
	private String objcity[];
	private Spinner changepro;
	private Spinner changecity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_school);
		intent = getIntent();
		intiView();
		intiData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_school, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(RegisterSchool.this, LoginActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
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
		lv = (ListView) findViewById(R.id.chooseSchool_nameList);
		myArrayAdapter = new ArrayAdapter<String>(this,R.layout.register_school_item, list);
		lv.setAdapter(myArrayAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				//schoolname = list.get(arg2);
				//schoolid = mapschool.get(schoolname);
				//pagejump(mapmajor.get(schoolid + ""));
				
				
				String str="该城市还未开放服务，敬请期待～";
				
				schoolname = list.get(arg2);
				
				if(!schoolname.equals(str)){
					schoolid = mapschool.get(schoolname);

					pagejump(mapmajor.get(schoolid));
				}
				
			}
		});
		
		
		changecity=(Spinner)findViewById(R.id.reinfosc_city);
		changepro=(Spinner)findViewById(R.id.reinfosc_pro);

		
		changecity.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				
				mycity= arg0.getItemAtPosition(arg2).toString();

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		changepro.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				
				mypro= arg0.getItemAtPosition(arg2).toString();

				try{
					int k=0;
					for (int i = 0; i < jsonCity.length(); i++) {

						if(jsonCity.getJSONObject(i).getString("pro").equals(mypro)){
							k=i;
							break;							
						}
						
					}
	
					JSONArray city=jsonCity.getJSONObject(k).getJSONArray("city");

					objcity=null;
					objcity = new String[city.length()];
					for (int i = 0; i < city.length(); i++) {
						objcity[i] = city.getString(i);
					}
					adaptercity = new ArrayAdapter<String>(RegisterSchool.this,android.R.layout.simple_spinner_item, objcity);
					adaptercity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					changecity.setAdapter(adaptercity);
					changecity.setSelection(0, true);
					
				}catch(Exception e){
					
				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});

	}

	public void intiData() {

		progressDialog = ProgressDialog.show(RegisterSchool.this, "","正在查询,请稍候！");

		new Thread() {
			public void run() {
				String cityJson = HttpUtil.sendGet(StaticString.server
						+ "registercity", "schoolname=aaa");

				Message msg_listData = new Message();
				if (cityJson.equals("error")) {

				} else {
					if (cityJson.equals("nothing")) {
						msg_listData.what = 1;
					} else {
						try {
							jsonCity = new JSONArray(cityJson);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						msg_listData.what = 3;
					}
				}
				handler.sendMessage(msg_listData);
			}
		}.start();

	}

	public void btonclik(View v) {

		int vid = v.getId();
		if (vid == R.id.registerinfo_school_giveup) {
			
			Intent intent = new Intent(RegisterSchool.this, LoginActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			
			return;
		}
		if (vid == R.id.choose_school_ok) {

			progressDialog = ProgressDialog.show(RegisterSchool.this, "","正在查询,请稍候！");
			new Thread() {
				public void run() {
					try {

						httpjson = HttpUtil.sendGet(StaticString.server+ "registerschool", "schoolpro="+ URLEncoder.encode(mypro, "UTF-8").toString()+"&schoolcity="+URLEncoder.encode(mycity, "UTF-8").toString());
						
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						
						
						System.out.println("WXCE");
						
						e1.printStackTrace();
					}
					
					
					System.out.println("LALA"+httpjson);
					
					Message msg_listData = new Message();
					if (httpjson.equals("error")) {
						msg_listData.what = 0;
					} else {
						if (httpjson.equals("nothing")) {
							msg_listData.what = 1;
						} else {
							try {
								JSONArray jsonarry = new JSONArray(httpjson);
								list.clear();
								for (int i = 0; i < jsonarry.length(); i++) {
									JSONObject obj = jsonarry.getJSONObject(i);

									mapschool.put(obj.get("Name").toString(),obj.get("_id").toString());
									schoolimg.put(obj.get("_id").toString(),obj.get("ShowUrl").toString());
									
									list.add(obj.get("Name").toString());

									JSONArray jsonarry1 = obj.getJSONArray("Major");

									Map<String, String> major = new HashMap<String, String>();// 用来存放单个学校的专业信息

									for (int j = 0; j < jsonarry1.length(); j++) {
										JSONObject obj1 = jsonarry1.getJSONObject(j);

										if (obj1 != null) {

											major.put(obj1.get("Name").toString(), obj1.get("_id").toString());
										}
									}
									mapmajor.put(obj.get("_id").toString(),major);
								}
								msg_listData.what = 2;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					handler.sendMessage(msg_listData);
				}
			}.start();

			return;
		}

	}

	public int getlocation() {
		return 0;
	}

	public void pagejump(Map<String, String> mapmap) {
		Intent intenta = new Intent(RegisterSchool.this, RegisterInfo.class);
		SerializableMap myMap = new SerializableMap();
		myMap.setMap(mapmap);
		Bundle bundle = new Bundle();
		bundle.putSerializable("majormap", myMap);
		intenta.putExtras(bundle);
		phone = intent.getStringExtra("phone");
		pwd = intent.getStringExtra("pwd");
		intenta.putExtra("phone", phone);
		intenta.putExtra("pwd", pwd);
		if (intent.getStringExtra("name") != null) {
			name = intent.getStringExtra("name");
			intenta.putExtra("name", name);
		}
		if (intent.getIntExtra("sex", -1) != -1) {
			sex = intent.getIntExtra("sex", -1);
			intenta.putExtra("sex", sex);
		}

		if (intent.getIntExtra("degree", -1) != -1) {
			degree = intent.getIntExtra("degree", -1);
			intenta.putExtra("degree", degree);
		}

		if (intent.getStringExtra("studentid") != null) {
			studentid = intent.getStringExtra("studentid");
			intenta.putExtra("studentid", studentid);
		}

		if (intent.getIntExtra("grade", -1) != -1) {
			grade = intent.getIntExtra("grade", -1);
			intenta.putExtra("grade", grade);
		}

		if (intent.getStringExtra("email") != null) {
			email = intent.getStringExtra("email");
			intenta.putExtra("email", email);
		}
		intenta.putExtra("schoolid", schoolid);
		intenta.putExtra("schoolname", schoolname);
		intenta.putExtra("SchoolImg", schoolimg.get(schoolid).toString());
		intenta.putExtra("From", "04");
		startActivity(intenta);
		finish();
		overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
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
				list.clear();
				list.add("该城市还未开放服务，敬请期待～");
				myArrayAdapter.notifyDataSetChanged();
				lv.setAdapter(myArrayAdapter);
				break;
			case 2:
				progressDialog.dismiss(); // 关闭进度条
				myArrayAdapter.notifyDataSetChanged();
				lv.setAdapter(myArrayAdapter);
				break;
			case 3:
				
				try {

					objpro = new String[jsonCity.length()];
					for (int i = 0; i < jsonCity.length(); i++) {

						objpro[i] = jsonCity.getJSONObject(i).getString("pro");
						
					}
					
					JSONArray city=jsonCity.getJSONObject(0).getJSONArray("city");
					objcity = new String[city.length()];
					for (int i = 0; i < city.length(); i++) {
						objcity[i] = city.getString(i);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				progressDialog.dismiss(); // 关闭进度条
				
				adapterpro = new ArrayAdapter<String>(RegisterSchool.this,android.R.layout.simple_spinner_item, objpro);
				adapterpro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				changepro.setAdapter(adapterpro);
				changepro.setSelection(0, true);

				adaptercity = new ArrayAdapter<String>(RegisterSchool.this,android.R.layout.simple_spinner_item, objcity);
				adaptercity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				changecity.setAdapter(adaptercity);
				changecity.setSelection(0, true);
				break;
			}
		}
	};
}
