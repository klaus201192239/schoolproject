package com.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;
import com.dbutil.DBHelper;
import com.http.HttpUtil;
import com.pagebean.SignUpOnlyBean;
import com.staticdata.StaticString;
import com.utilt.EmojiFilter;

public class OnlySignUp extends Activity {

	private String activityid,tableid,myname,myidcard;;
	private DataAdapter mAdapter;
	private ListView mListView;
	private View headview, footview;
	private DBHelper dbhelper;
	private JSONArray arrayinfo;
	private Intent intent;
	private ProgressDialog progressDialog;
	private List<SignUpOnlyBean> list = new ArrayList<SignUpOnlyBean>();
    private boolean resetTextna;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_only_sign_up);

		intidata();
		intiView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.only_sign_up, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			quit();
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

	public void quit() {
		Builder builder = new AlertDialog.Builder(OnlySignUp.this);
		builder.setTitle("取消报名");
		builder.setMessage("您确定取消报名信息么？？");
		builder.setPositiveButton("取消报名", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String from = intent.getStringExtra("from");
				if (from.equals("05")) {
					startActivity(new Intent(OnlySignUp.this, InActivity.class).putExtra("CurrentId", activityid));
					finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				} else {
					startActivity(new Intent(OnlySignUp.this,InActivityDetail.class).putExtra("id", activityid));
					finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				}
			}
		});
		builder.setNegativeButton("继续报名", null);
		builder.create().show();
	}

	public void btonclik(View v) {
		quit();
	}

	public void intiView() {

		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		footview = inflater.inflate(R.layout.only_signup_foot, null);
		ImageView img = (ImageView) footview.findViewById(R.id.signup_only_attent);
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				boolean tag = false;
				dbhelper.CreatOrOpen("schooltime");
				Cursor cur = dbhelper.selectInfo("select * from attendactivity where activityid='"+ activityid + "';");
	
				while (cur.moveToNext()) {
					tag = true;
				}
				dbhelper.closeDB();

				if (tag == true) {
					Toast.makeText(getApplicationContext(), "您已经报过名了哦～～",Toast.LENGTH_SHORT).show();
					return;
				}

				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).cloumntext.length() == 0) {
						Toast.makeText(getApplicationContext(), "请填写全部字段信息～～",Toast.LENGTH_SHORT).show();
						return;
					}
					
					if(list.get(i).cloumntext.length()>list.get(i).length){
						Toast.makeText(getApplicationContext(), list.get(i).cloumnname+"字段超过限定长度",Toast.LENGTH_SHORT).show();
						return;
					}
					
					if(list.get(i).cloumnname.equals("姓名")){
						
						if(!list.get(i).cloumntext.equals(myname)){
							Toast.makeText(getApplicationContext(), "姓名输入有误，您的姓名应为"+myname,Toast.LENGTH_SHORT).show();
							return;
						}

					}
					
					if(list.get(i).cloumnname.equals("学号")){
						
						if(!list.get(i).cloumntext.equals(myidcard)){
							Toast.makeText(getApplicationContext(), "学号输入有误，您的学号应为"+myidcard,Toast.LENGTH_SHORT).show();
							return;
						}

					}
				}

				try {
					arrayinfo = new JSONArray();
					for (int i = 0; i < list.size(); i++) {
						JSONObject obj = new JSONObject();
						obj.put("name", URLEncoder.encode(list.get(i).cloumnname,"UTF-8"));
						obj.put("txt",  URLEncoder.encode(EmojiFilter.filterEmoji(list.get(i).cloumntext),"UTF-8"));
						
						arrayinfo.put(obj);
					}
					
				} catch (Exception e) {

				}

				progressDialog = ProgressDialog.show(OnlySignUp.this, "","正在提交报名信息,请稍候！");

				new Thread() {
					public void run() {
						Message msg_listData = new Message();
						String httpjson = null;
						SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
						String userid=set.getString("Id","");
						String schoolid=set.getString("ShoolId","");
						String idcard=set.getString("StudentId", "");
						
						if (intent.getIntExtra("teamtag", -1) == 0) { 
							
							httpjson = HttpUtil.sendGet(StaticString.server+ "attendonly", "tableid=" + tableid+ "&userid="+userid+"&info="+arrayinfo.toString()+"&schoolid="+schoolid+"&idcard="+idcard);
							
						} else {
							if (intent.getIntExtra("teamtag", -1) == 1) {
												
								String str = intent.getStringExtra("teaminfo"); 

								String sname="",smajor="",activityname="";
								try {
									sname = URLEncoder.encode(set.getString("Name", ""),"UTF-8");

									smajor=URLEncoder.encode(set.getString("MajorName",""), "UTF-8");
													
									activityname=URLEncoder.encode(intent.getStringExtra("activityname"),"UTF-8");

								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
			
								int sdegree=set.getInt("Degree",-1);
								String sphone=set.getString("Phone", "");
								int sgrade=set.getInt("Grade", -1);

								httpjson = HttpUtil.sendGet(StaticString.server+ "attendcreateteam", str +"&tableid=" + tableid+ "&info="+ arrayinfo.toString()+"&idcard="+idcard+"&sname="+sname+"&smajor="+smajor+"&sdegree="+sdegree+"&sphone="+sphone+"&sgrade="+sgrade+"&activityname="+activityname+"&schoolid="+schoolid);

							} else {
								
								String teamid="",abstr="";
								String sname="",smajor="";
								int sdegree=set.getInt("Degree",-1);
								String sphone=set.getString("Phone", "");
								int sgrade=set.getInt("Grade", -1);
								try {
									sname = URLEncoder.encode(set.getString("Name", ""),"UTF-8");
									smajor=URLEncoder.encode(set.getString("MajorName",""), "UTF-8");
									teamid = intent.getStringExtra("teamid");									
									abstr= URLEncoder.encode(intent.getStringExtra("showself"),"UTF-8").toString() ;
								} catch (UnsupportedEncodingException e) {

								}								
								httpjson = HttpUtil.sendGet(StaticString.server+ "attendotherteam", "tableid=" + tableid+ "&userid="+userid+"&info="+arrayinfo.toString()+"&teamid="+teamid+"&abstra="+abstr+"&idcard="+idcard+"&sname="+sname+"&smajor="+smajor+"&sdegree="+sdegree+"&sphone="+sphone+"&sgrade="+sgrade+"&schoolid="+schoolid);
							}
						}
						
						if (httpjson.equals("error")) {
							msg_listData.what = 3;
						} else {
							
							dbhelper.CreatOrOpen("schooltime");
							dbhelper.excuteInfo("insert into attendactivity values('"+ activityid + "');");
							
							dbhelper.closeDB();
							msg_listData.what = 2;
						}
						handler.sendMessage(msg_listData);
					}
				}.start();

			}
		});
		headview = inflater.inflate(R.layout.only_signup_head, null);

		mListView = (ListView) findViewById(R.id.signup_only_listView);

		mListView.addHeaderView(headview);
		mListView.addFooterView(footview);

		
	}

	public void intidata() {
		
		SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
		myname=set.getString("Name", "");
		myidcard=set.getString("StudentId", "");						
		
		
		intent = getIntent();

		activityid = intent.getStringExtra("activityid");		

		dbhelper = new DBHelper(this);

		progressDialog = ProgressDialog.show(OnlySignUp.this, "","正在生成报名表,请稍候！");
		new Thread() {
			public void run() {
				String httpjson = HttpUtil.sendGet(StaticString.server+ "getsignupinfo", "activityid=" + activityid);
				Message msg_listData = new Message();
				if (httpjson.equals("error")) {
					msg_listData.what = 0;
				} else {
					try {
						
						JSONObject json = new JSONObject(httpjson);
						tableid=json.getString("_id");
						JSONArray array =json.getJSONArray("TableInfoColumn");
						for (int i = 0; i < array.length(); i++) {
							JSONObject obj = array.getJSONObject(i);
							SignUpOnlyBean bean = new SignUpOnlyBean();
							bean.cloumnname = obj.getString("Name");
							bean.cloumntext = "";
							bean.length=obj.getInt("Length");
							bean.Choose=obj.getBoolean("Choose");
							list.add(bean);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					msg_listData.what = 1;
				}
				handler.sendMessage(msg_listData);
			}
		}.start();

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
				
				mAdapter = new DataAdapter(OnlySignUp.this);
				mListView.setAdapter(mAdapter);
				
			//	mListView.setAdapter(mAdapter);
			//	mAdapter.notifyDataSetChanged();
				
				break;
			case 2:

				progressDialog.dismiss(); // 关闭进度条
				Toast.makeText(getApplicationContext(), "恭喜您，报名成功，祝好运哦！",Toast.LENGTH_SHORT).show();

				Intent intent = new Intent(OnlySignUp.this, InActivity.class);
				intent.putExtra("CurrentId", activityid);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				break;
			case 3:
				progressDialog.dismiss(); // 关闭进度条
				Toast.makeText(getApplicationContext(), "报名失败，请检查网络或其他错误～！",Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private class DataAdapter extends BaseAdapter {

		private Context ctx;

		public DataAdapter(Context ctx) {
			this.ctx = ctx;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private LayoutParams fillParentLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		private int index = -1;

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				convertView = new LinearLayout(ctx, null);
			} else {
				((LinearLayout) convertView).removeAllViews();
			}			
			
			EditText editText = new EditText(ctx, null);
			((LinearLayout) convertView).addView(editText,fillParentLayoutParams);

			editText.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View view, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						index = position;
					}
					return false;
				}
			});

			editText.addTextChangedListener(new TextWatcher() {

				public void afterTextChanged(Editable editable) {
					
				}

				public void beforeTextChanged(CharSequence text, int start,int count, int after) {
					
				}

				public void onTextChanged(CharSequence text, int start,int before, int count) {

					if (!resetTextna) {
	                    if ((count == 2) && (!EmojiFilter.containsEmoji(text.toString().substring(start, start + 2)))) {
	                        resetTextna = true;
	                        Toast.makeText(getApplicationContext(), "此处不能输入图片表情哦～",Toast.LENGTH_SHORT).show();
	                    }
	                } else {
	                    resetTextna = false;
	                }

					list.get(position).cloumntext = text.toString();
				}

			});

			if (list.get(position).Choose == true) {
				editText.setHint(list.get(position).cloumnname + "(必填,字符数不超过"+list.get(position).length+")");
			} else {
				editText.setHint(list.get(position).cloumnname + "(选填,字符数不超过"+list.get(position).length+")");
			}
			editText.setText(list.get(position).cloumntext);

			editText.clearFocus();
			if (index != -1 && index == position) {
				editText.requestFocus();
			}
			return convertView;

		}
	}
}
