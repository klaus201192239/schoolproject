package com.activity;

import org.json.JSONArray;
import org.json.JSONObject;
import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import com.http.HttpUtil;
import com.pagebean.SignInBean;
import com.service.CoreService;
import com.staticdata.StaticBoolean;
import com.staticdata.StaticInt;
import com.staticdata.StaticList;
import com.staticdata.StaticString;
import com.utilt.utils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ManagerActivityPerson extends Activity implements IXListViewListener {

	private Intent intent;
	private String activityid, oganizationid, oganization;
	private String title;
	private XListView mListView;
	private MyAdapter mAdapter;
	private Handler mHandler;
	private ProgressDialog progressDialog;

	private int firstPage=0;
	private int onFreshTag=0;
	private int loadMoreTag=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager_activity_person);

		intiData();

		if (StaticBoolean.NetLink == false) {
			Toast.makeText(getApplicationContext(), "网络连接不可用",Toast.LENGTH_SHORT).show();
		} else {
			getNetData();
		}

	//	intiView();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.manager_activity_person, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent inte = new Intent(ManagerActivityPerson.this, ManagerActivityDetail.class);
			inte.putExtra("activityid", activityid);
			inte.putExtra("oganizationid", oganizationid);
			startActivity(inte);
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

	public void intiData() {
		intent = getIntent();
		activityid = intent.getStringExtra("activityid");
		title = intent.getStringExtra("title");
		oganizationid=intent.getStringExtra("oganizationid");
		oganization=intent.getStringExtra("oganization");

	}
	
	public void intiView() {
		mHandler = new Handler();
		mListView = (XListView) findViewById(R.id.manageall_xListView);
		mListView.setPullLoadEnable(true);
		mAdapter = new MyAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(this);
	}
	
	public void btonclik(View v) {
		int vid = v.getId();
		if (vid == R.id.manageall_back) {
			Intent inte = new Intent(ManagerActivityPerson.this, ManagerActivityDetail.class);
			inte.putExtra("activityid", activityid);
			inte.putExtra("oganizationid", oganizationid);
			startActivity(inte);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return;
		}
		if (vid == R.id.manageall_notice) {
			Intent inte = new Intent(ManagerActivityPerson.this, ManagerActivityNotice.class);
			inte.putExtra("activityid", activityid);
			inte.putExtra("oganizationid", oganizationid);
			inte.putExtra("oganization", oganization);
			inte.putExtra("title", title);
			startActivity(inte);
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return ;
		}
	}

	public void getNetData() {

		StaticList.SignInlist.clear();

		progressDialog = ProgressDialog.show(ManagerActivityPerson.this, "", "正在获取数据,请稍候！");
		new Thread() {
			public void run() {

				String httpjson = HttpUtil.sendGet(StaticString.server+ "getsigninlist", "activityid=" + activityid+ "&currentstuid=0");

				Message msg_listData = new Message();

				if (httpjson.equals("error")) {
					msg_listData.what = 0;
				} else {
					msg_listData.what = 1;
					try {
						JSONArray array = new JSONArray(httpjson);
						int len = array.length();
						for (int i = 0; i < len; i++) {
							SignInBean bean = new SignInBean();
							JSONObject obj = array.getJSONObject(i);
							bean.degree = obj.getInt("degree");
							bean.grade = obj.getInt("grade");
							bean.id = obj.getString("id");
							bean.major = obj.getString("major");
							bean.name = obj.getString("name");
							bean.studentid = obj.getString("studentid");
							bean.state=obj.getInt("state");
							StaticList.SignInlist.add(bean);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
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
				
				if(firstPage==0){
					intiView();
					firstPage++;
				}
				
				progressDialog.dismiss(); // 关闭进度条
				Toast.makeText(getApplicationContext(), "网络连接或其他意外错误",Toast.LENGTH_SHORT).show();
				break;
			case 1:
				progressDialog.dismiss(); // 关闭进度条
				
				if(firstPage==0){
					intiView();
					firstPage++;
				}else{
					mAdapter.notifyDataSetChanged();
				}

	//			mAdapter.notifyDataSetChanged();
				loadNext();
				break;
			case 2:
				if(firstPage==0){
					intiView();
					firstPage++;
				}
				progressDialog.dismiss(); // 关闭进度条
				Toast.makeText(getApplicationContext(), "签到成功，刷新列表后可查看～",Toast.LENGTH_SHORT).show();
				break;
			case 3:
				if(firstPage==0){
					intiView();
					firstPage++;
				}
				progressDialog.dismiss(); // 关闭进度条
				Toast.makeText(getApplicationContext(), "组织者还未发起签到环节",Toast.LENGTH_SHORT).show();
				break;
			}

		}
	};

	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime("");
	}

	// 下拉刷新
	@Override
	public void onRefresh() {
		
		if(onFreshTag==1){
			onLoad();
			return ;
		}
		
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				onFreshTag=1;
				
				if (StaticBoolean.NetLink == true) {
					int x = geneItems(1);
					if (x == 0) {
						onLoad();
						Toast.makeText(getApplicationContext(), "网络连接不可用",Toast.LENGTH_SHORT).show();
					} else {
						mAdapter.notifyDataSetChanged();
						onLoad();
					}
				} else {
					onLoad();
					Toast.makeText(getApplicationContext(), "网络连接不可用",Toast.LENGTH_SHORT).show();
				}
				
				onFreshTag=0;
			}
		}, 2000);
	}

	// 加载更多
	@Override
	public void onLoadMore() {
		
		if(loadMoreTag==1){
			onLoad();
			return ;
		}
		
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				
				loadMoreTag=1;
				
				if (StaticBoolean.NetLink == true) {
					int x = geneItems(0);
					if (x == 0) {
						onLoad();
						Toast.makeText(getApplicationContext(), "网络连接不可用",Toast.LENGTH_SHORT).show();
					} else {
						mAdapter.notifyDataSetChanged();
						onLoad();
					}
				} else {
					onLoad();
					Toast.makeText(getApplicationContext(), "网络连接不可用",Toast.LENGTH_SHORT).show();
				}
				
				loadMoreTag=0;
			}
		}, 2000);
	}

	private int geneItems(int type) {
		if (type == 1) {
			if (StaticBoolean.NetLink == false) {
				return 0;
			}
			Thread onFresh = new Thread() {
				public void run() {

					String httpjson = HttpUtil.sendGet(StaticString.server+ "getsigninlist", "activityid=" + activityid+ "&currentstuid=0");

					if (httpjson.equals("error")) {

					} else {
						try {

							StaticList.SignInlist.clear();

							JSONArray array = new JSONArray(httpjson);
							int len = array.length();
							for (int i = 0; i < len; i++) {
								SignInBean bean = new SignInBean();
								JSONObject obj = array.getJSONObject(i);
								bean.degree = obj.getInt("degree");
								bean.grade = obj.getInt("grade");
								bean.id = obj.getString("id");
								bean.major = obj.getString("major");
								bean.name = obj.getString("name");
								bean.studentid = obj.getString("studentid");
								bean.state=obj.getInt("state");
								StaticList.SignInlist.add(bean);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					loadNext();
				}
			};
			
			onFresh.start();
			try {
				onFresh.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			StaticList.SignInlistb.clear();
			loadNext();
			
			return 1;
			
			
		} else {

			if (StaticBoolean.NetLink == false) {
				return 0;
			}
			int x = StaticList.SignInlistb.size();

			if (x != 0) {

				int y = StaticList.SignInlist.size();
				for (int i = 0; i < x; i++) {

					if (StaticList.SignInlistb.get(i).studentid.compareTo(StaticList.SignInlist.get(y - 1 + i).studentid) <0) {
						StaticList.SignInlist.add(StaticList.SignInlistb.get(i));
					}
				}
				
				StaticList.SignInlistb.clear();
				loadNext();
				
				return 1;
				
			} else {

				if(StaticList.SignInlist.size()<=0){
					return 0; 
				}
				
				Thread onLoadMore=new Thread() {
					public void run() {
						String httpjson = HttpUtil.sendGet(StaticString.server + "getsigninlist","activityid="+ activityid+ "&currentstuid="+ 
						        StaticList.SignInlist.get(StaticList.SignInlist.size() - 1).studentid);
						if (httpjson.equals("error")) {

						} else {
							try {
								JSONArray array = new JSONArray(httpjson);
								int len = array.length();
								for (int i = 0; i < len; i++) {
									SignInBean bean = new SignInBean();
									JSONObject obj = array.getJSONObject(i);
									bean.degree = obj.getInt("degree");
									bean.grade = obj.getInt("grade");
									bean.id = obj.getString("id");
									bean.major = obj.getString("major");
									bean.name = obj.getString("name");
									bean.studentid = obj.getString("studentid");
									bean.state=obj.getInt("state");
									StaticList.SignInlist.add(bean);
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}
				};
				
				onLoadMore.start();
				try {
					onLoadMore.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				StaticList.SignInlistb.clear();
				loadNext();
				
				return 1;				
			}
		}

	}

	public void loadNext() {

		Intent intent = new Intent(ManagerActivityPerson.this, CoreService.class);
		intent.putExtra("tag", StaticInt.downsignin);
		intent.putExtra("activityid", activityid);
		startService(intent);
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private ViewCache cache;

		public MyAdapter(Context context) {
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return StaticList.SignInlist.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public int getItemViewType(int position) {
			return position == 0 ? 0 : 1;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if (convertView == null) {
				switch (type) {
				case 0:
					convertView = inflater.inflate(R.layout.signin_head, null);// 生成条目界面对象
					((TextView) convertView.findViewById(R.id.signin_head_title)).setText(title);
					return convertView;
				case 1:
					convertView = inflater.inflate(R.layout.signin_item, null);// 生成条目界面对象
					cache = new ViewCache();
					cache.item_text= (TextView) convertView.findViewById(R.id.signin_item_text);
					cache.icon= (ImageView) convertView.findViewById(R.id.signin_item_headicon);
					cache.itemmajor= (TextView) convertView.findViewById(R.id.signin_text_major);
					convertView.setTag(cache);
					break;
				}
			} else {
				switch (type) {
				case 0:
					return convertView;
				case 1:
					cache = new ViewCache();
					cache = (ViewCache) convertView.getTag();
					break;
				}
			}

			SignInBean bean = StaticList.SignInlist.get(position - 1);
			if(bean.state==0){
				cache.item_text.setText(bean.name+" "+bean.studentid);
			}else{
				cache.item_text.setText(bean.name+" "+bean.studentid);
			}
			
			cache.itemmajor.setText(bean.major);
			
			cache.icon.setImageResource(utils.getSignInIcon(bean.state));
			return convertView;
		}

		private final class ViewCache {
			public TextView item_text;
			public ImageView icon;
			public TextView itemmajor;
		}
	}
}
