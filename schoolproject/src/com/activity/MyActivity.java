package com.activity;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.DateBundle;
import com.dbutil.DBHelper;
import com.http.HttpUtil;
import com.http.ImgLoader;
import com.pagebean.InActivityBean;
import com.service.CoreService;
import com.staticdata.StaticBoolean;
import com.staticdata.StaticInt;
import com.staticdata.StaticList;
import com.staticdata.StaticLong;
import com.staticdata.StaticString;
import com.view.InclineTextView;

public class MyActivity extends Activity implements IXListViewListener {

	private Intent intent;
	private DBHelper dbhelper;
	private XListView mListView;
	private MyAdapter mAdapter;
	private Handler mHandler;
	private ProgressDialog progressDialog;
	
	private int firstPage=0;
	
	private long onfreshTime=0;
	private int loadMoreTag=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);

		intiData();

		if (StaticBoolean.MyActivityFirst == true) {
			if (StaticBoolean.NetLink == false) {
				Toast.makeText(getApplicationContext(), "网络连接不可用",Toast.LENGTH_SHORT).show();
				getLocalData();
				intiView();
				firstPage++;
			} else {
				getNetData();
			}
			StaticBoolean.MyActivityFirst = false;
		} else {
			intiView();
		}

		//intiView();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			StaticBoolean.MyActivityFirst = true;
			Intent intent = new Intent(MyActivity.this, UserCenter.class);
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

	public void getLocalData() {

		StaticList.MyActicitylist.clear();
		StaticList.MyActicitylistb.clear();
		DBHelper dbhelper = new DBHelper(this);
		dbhelper.CreatOrOpen("schooltime");
		Cursor cur = dbhelper.selectInfo("select * from myactivity order by id desc;");
		while (cur.moveToNext()) {
			InActivityBean bean = new InActivityBean();
			bean.id = cur.getString(0);// 获取第一列的值,第一列的索引从0开始
			bean.title = cur.getString(1);
			bean.imgurl = cur.getString(2);
			bean.category = cur.getString(3);
			SimpleDateFormat sfStart = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",java.util.Locale.ENGLISH) ;	
			try {
				bean.deadline=sfStart.parse(cur.getString(4));
			} catch (ParseException e) {

			}
			bean.time = cur.getString(5);
			bean.pridenum = cur.getInt(6);
			bean.opposenum = cur.getInt(7);
			bean.commentnum = cur.getInt(8);
			StaticList.MyActicitylist.add(bean);
		}
		dbhelper.closeDB();
	}

	public void getNetData() {

		StaticList.MyActicitylist.clear();
		progressDialog = ProgressDialog.show(MyActivity.this, "", "正在获取数据,请稍候！");
		new Thread() {
			public void run() {
				
				JSONArray aar=new JSONArray();
				
				dbhelper.CreatOrOpen("schooltime");
				Cursor cursor=dbhelper.selectInfo("select DISTINCT * from attendactivity order by activityid desc limit 3;");
				while (cursor.moveToNext()) {
					aar.put(cursor.getString(0));
				}
				dbhelper.closeDB();
				Message msg_listData = new Message();
				
				if(aar.length()==0){
					SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
					String userid=set.getString("Id", "");
					String stuid=set.getString("StudentId", "");
					String schoolid=set.getString("ShoolId", "");
					String jsonmyactivity = HttpUtil.sendGet(StaticString.server+ "getmyactivityid", "userid="+userid+"&stuid="+stuid+"&schoolid="+schoolid);
					if (jsonmyactivity.equals("error")) {
						msg_listData.what = 0;
						handler.sendMessage(msg_listData);
						return ;
					} else {
						
						try{
							dbhelper.CreatOrOpen("schooltime");
							JSONArray myArray=new JSONArray(jsonmyactivity);
							int myA=myArray.length();
							for(int k=0;k<myA;k++){
								dbhelper.excuteInfo("insert into attendactivity values('"+ myArray.getString(k).toString() + "');");
							}
							Cursor curr=dbhelper.selectInfo("select DISTINCT * from attendactivity order by activityid desc limit 3;");
							while (curr.moveToNext()) {
								aar.put(curr.getString(0));
							}
							
							dbhelper.closeDB();
							
						}catch(Exception e){
							
						}
					}
				}
				
				if(aar.length()==0){
					msg_listData.what = 2;
				}else{
					String httpjson = HttpUtil.sendGet(StaticString.server+ "getmyactivity", "jsonid="+aar.toString());
					
					if (httpjson.equals("error")) {
						msg_listData.what = 0;
					} else {
						msg_listData.what = 1;
						try {
							JSONArray jsonarry = new JSONArray(httpjson);
							int size = jsonarry.length();
							for (int i = 0; i < size; i++) {
								JSONObject cur = jsonarry.getJSONObject(i);
								InActivityBean bean = new InActivityBean();
								bean.id = cur.getString("id");
								bean.title = cur.getString("title");
								bean.imgurl = cur.getString("imgurl");
								bean.category = cur.getString("category");
								SimpleDateFormat sfStart = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",java.util.Locale.ENGLISH) ;		
								bean.deadline=sfStart.parse(cur.getString("deadline"));
								bean.time = cur.getString("time");
								bean.pridenum = cur.getInt("pridenum");
								bean.opposenum = cur.getInt("opposenum");
								bean.commentnum = cur.getInt("commentnum");
								StaticList.MyActicitylist.add(bean);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
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
				
				progressDialog.dismiss(); // 关闭进度条
				
				if(firstPage==0){
					intiView();
					firstPage++;
				}
				
				Toast.makeText(getApplicationContext(), "还未参加活动或网络连接错误",Toast.LENGTH_SHORT).show();
				break;
			case 1:
				progressDialog.dismiss(); // 关闭进度条
				
				if(firstPage==0){
					intiView();
					firstPage++;
				}else{
					mAdapter.notifyDataSetChanged();
				}
				
				StaticLong.MyActicity_onfreshTime=System.currentTimeMillis();

				loadNext();
				break;
			case 2:
				progressDialog.dismiss(); // 关闭进度条
				
				if(firstPage==0){
					intiView();
					firstPage++;
				}
				
				Toast.makeText(getApplicationContext(), "您还未参加任何活动哦～加油",Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	public void intiData() {
		dbhelper = new DBHelper(this);
		intent = getIntent();
		onfreshTime=System.currentTimeMillis();
	}

	public void intiView() {
		mHandler = new Handler();
		mListView = (XListView) findViewById(R.id.myactivity_xListView);
		mListView.setPullLoadEnable(true);
		mAdapter = new MyAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(this);
		if (intent != null) {
			String y = intent.getStringExtra("CurrentId");

			if (y != null&&y.length()!=0) {
				int tempIndex = 0;
				for (int i = 0; i < StaticList.MyActicitylist.size() - 1; i++) {
					tempIndex++;
					if (StaticList.MyActicitylist.get(i).id .equals(y) ) {
						break;
					}
				}
				mListView.setSelection(tempIndex + 1);
			}
		}
	}

	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime("");
	}

	// 下拉刷新
	@Override
	public void onRefresh() {
		
		long nowTime=System.currentTimeMillis();;
		
		if((nowTime-onfreshTime)<3000){
			Toast.makeText(this, "亲，前一秒刚刷新过哦～", Toast.LENGTH_SHORT).show();
			onLoad();
			return ;
		}
		
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (StaticBoolean.NetLink == true) {
					int x = geneItems(1);
					if (x == 0) {
						onLoad();
						Toast.makeText(getApplicationContext(), "网络连接不可用",Toast.LENGTH_SHORT).show();
					} else {
						mAdapter.notifyDataSetChanged();
						onLoad();
						onfreshTime=System.currentTimeMillis();
					}
				} else {
					onLoad();
					Toast.makeText(getApplicationContext(), "网络连接不可用",Toast.LENGTH_SHORT).show();
				}
			}
		}, 1000);
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
			Thread onFresh =new Thread() {
				public void run() {
					
					JSONArray aar=new JSONArray();
					
					dbhelper.CreatOrOpen("schooltime");
					Cursor cursor=dbhelper.selectInfo("select * from attendactivity order by activityid desc limit 3;");
					while (cursor.moveToNext()) {
						aar.put(cursor.getString(0));
					}
					dbhelper.closeDB();
					String httpjson = HttpUtil.sendGet(StaticString.server+ "getmyactivity", "jsonid="+aar.toString());
					if (httpjson.equals("error")) {
					} else {
						if (httpjson.equals("nothing")) {
						} else {
							try {
								JSONArray jsonarry = new JSONArray(httpjson);
								int size = jsonarry.length();

								StaticList.MyActicitylist.clear();// 此处是不一样的

								for (int i = 0; i < size; i++) {
									JSONObject cur = jsonarry.getJSONObject(i);
									InActivityBean bean = new InActivityBean();
									bean.id = cur.getString("id");// 获取第一列的值,第一列的索引从0开始
									bean.title = cur.getString("title");
									bean.imgurl = cur.getString("imgurl");
									bean.category = cur.getString("category");
									SimpleDateFormat sfStart = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",java.util.Locale.ENGLISH) ;		
									bean.deadline=sfStart.parse(cur.getString("deadline"));
									bean.time = cur.getString("time");
									bean.pridenum = cur.getInt("pridenum");
									bean.opposenum = cur.getInt("opposenum");
									bean.commentnum = cur.getInt("commentnum");
									StaticList.MyActicitylist.add(bean);
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			};
			
			onFresh.start();
			try {
				onFresh.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			StaticList.MyActicitylistb.clear();
			loadNext();
			
			return 1;
			
		} else {
			if (StaticBoolean.NetLink == false) {
				return 0;
			}
			int x = StaticList.MyActicitylistb.size();
			if (x != 0) {
				int y = StaticList.MyActicitylist.size();
				for (int i = 0; i < x; i++) {
					if (StaticList.MyActicitylistb.get(i).id .compareTo(StaticList.MyActicitylist.get(y - 1 + i).id)<0) {
						StaticList.MyActicitylist.add(StaticList.MyActicitylistb.get(i));
					}
				}
				
				StaticList.MyActicitylistb.clear();
				loadNext();
				
				return 1;
				
			} else {
				if(StaticList.MyActicitylist.size()>0){
					Thread onLoadMore=new Thread() {
						public void run() {
							
							if(StaticList.MyActicitylist.size()<=0){
								return ;
							}
							
							String cid=StaticList.MyActicitylist.get(StaticList.MyActicitylist.size() - 1).id;
							
							JSONArray aar=new JSONArray();
							
							dbhelper.CreatOrOpen("schooltime");
							Cursor cursor=dbhelper.selectInfo("select * from attendactivity where activityid<'"+cid+"' order by activityid desc limit 3;");
							while (cursor.moveToNext()) {
								aar.put(cursor.getString(0));
							}
							dbhelper.closeDB();
							String httpjson = HttpUtil.sendGet(StaticString.server+ "getmyactivity", "jsonid="+aar.toString());
							
							if (httpjson.equals("error")) {
							} else {
								if (httpjson.equals("nothing")) {
								} else {
									try {
										JSONArray jsonarry = new JSONArray(httpjson);
										int size = jsonarry.length();
										for (int i = 0; i < size; i++) {
											JSONObject cur = jsonarry.getJSONObject(i);
											InActivityBean bean = new InActivityBean();
											bean.id = cur.getString("id");// 获取第一列的值,第一列的索引从0开始
											bean.title = cur.getString("title");
											bean.imgurl = cur.getString("imgurl");
											bean.category = cur.getString("category");										
											SimpleDateFormat sfStart = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",java.util.Locale.ENGLISH) ;		
											bean.deadline=sfStart.parse(cur.getString("deadline"));
											bean.time = cur.getString("time");
											bean.pridenum = cur.getInt("pridenum");
											bean.opposenum = cur.getInt("opposenum");
											bean.commentnum = cur.getInt("commentnum");
											StaticList.MyActicitylist.add(bean);
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
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
					
					StaticList.MyActicitylistb.clear();
					loadNext();
					
					return 1;
				}
			}
		}
		return 1;
	}

	public void loadNext() {
		Intent intent = new Intent(MyActivity.this, CoreService.class);
		intent.putExtra("tag", StaticInt.downmyactivity);
		startService(intent);
	}

	public void to_back(View v) {
		// int vid=v.getId();
		StaticBoolean.MyActivityFirst = true;
		Intent intent = new Intent(MyActivity.this, UserCenter.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private ImgLoader imgloader;
		private ViewCache cache;

		public MyAdapter(Context context) {
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.imgloader = new ImgLoader(context);
		}

		@Override
		public int getCount() {
			return StaticList.MyActicitylist.size() + 1;
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
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("deprecation")
		@SuppressLint("SimpleDateFormat")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if (convertView == null) {
				switch (type) {
				case 0:
					convertView = inflater.inflate(R.layout.inactivity_head,null);// 生成条目界面对象
				//	((ImageView) convertView.findViewById(R.id.inactivityschool)).setImageResource(R.drawable.school);
					return convertView;
				case 1:
					convertView = inflater.inflate(R.layout.outactivity_item,null);// 生成条目界面对象
					cache = new ViewCache();
					cache.item_title = (TextView) convertView
							.findViewById(R.id.outactivity_item_title);
					cache.item_img = (ImageView) convertView
							.findViewById(R.id.outactivity_item_img);
					cache.team_item_class = (InclineTextView) convertView
							.findViewById(R.id.outactivity_item_class);
					cache.item_deadline = (TextView) convertView
							.findViewById(R.id.outactivity_item_deadline);
					cache.item_time = (TextView) convertView
							.findViewById(R.id.outactivity_item_time);
					cache.item_like = (ImageView) convertView
							.findViewById(R.id.outactivity_item_like);
					cache.item_dislike = (ImageView) convertView
							.findViewById(R.id.outactivity_item_dislike);
					cache.item_comment = (ImageView) convertView
							.findViewById(R.id.outactivity_item_comment);
					cache.like_num = (TextView) convertView
							.findViewById(R.id.outactivity_item_like_num);
					cache.dislike_num = (TextView) convertView
							.findViewById(R.id.outactivity_item_dislike_num);
					cache.comment_num = (TextView) convertView
							.findViewById(R.id.outactivity_item_comment_num);
					cache.sum_num = (TextView) convertView
							.findViewById(R.id.outactivity_item_sumnum);

					cache.item_title.setOnClickListener(new myListener());
					cache.item_img.setOnClickListener(new myListener());
					cache.item_like.setOnClickListener(new myListener());
					cache.item_dislike.setOnClickListener(new myListener());
					cache.item_comment.setOnClickListener(new myListener());

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

			InActivityBean bean = StaticList.MyActicitylist.get(position - 1);
			cache.item_title.setText(bean.title);
			imgloader.display(cache.item_img, bean.imgurl);
			cache.team_item_class.setText(bean.category);

			if(bean.deadline.getYear()==0){
				cache.item_deadline.setText(" ");
			}else{
				Date date = new Date();
				
				if(date.before(bean.deadline)){
					cache.item_deadline.setTextColor(Color.BLUE);
					SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
					cache.item_deadline.setText("报名截止："+format.format(bean.deadline)+"(还未截止)");
				}else{
					cache.item_deadline.setTextColor(Color.RED);
					SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
					cache.item_deadline.setText("报名截止："+format.format(bean.deadline)+"(已经截止)");
				}
			}
			
			
			int x=bean.time.indexOf("~");
			String str1=bean.time.substring(0, x);
			String str2=bean.time.substring(x+1);
			SimpleDateFormat sfStart = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",java.util.Locale.ENGLISH) ;	
			Date d1=null,d2=null;
			try {
				d1=sfStart.parse(str1);
				d2=sfStart.parse(str2);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			if(d1.getYear()==0){
				cache.item_time.setText(" ");
			}else{
				SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
				cache.item_time.setText("活动时间："+format.format(d1).toString()+"~"+format.format(d2).toString());
			}

			cache.like_num.setText(bean.pridenum + "");
			cache.dislike_num.setText(bean.opposenum + "");
			cache.comment_num.setText(bean.commentnum + "");
			double t1=bean.pridenum+0.0+bean.commentnum*2+bean.opposenum,t=0.0;
			if(t1!=0){
				t=((bean.pridenum+0.0+bean.commentnum*2-bean.opposenum)*10)/t1;
			}
			BigDecimal bg = new BigDecimal(t);  
            double f = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
			cache.sum_num.setText("受欢迎程度："+f);
			
			
			int ty=-1;
			dbhelper.CreatOrOpen("schooltime");
			Cursor cursor = dbhelper.selectInfo("select type from takepart where activityid='"+bean.id+ "';");
			if (cursor.moveToNext()) {				
				ty=cursor.getInt(0);
			}
			dbhelper.closeDB();
			
			if(ty!=-1){
				if(ty==0){
					cache.item_dislike.setImageResource(R.drawable.against1);
					cache.item_like.setImageResource(R.drawable.support);
				}else{
					if(ty==1){
						cache.item_like.setImageResource(R.drawable.support1);
						cache.item_dislike.setImageResource(R.drawable.against);
					}
				}
			}else{
				cache.item_dislike.setImageResource(R.drawable.against);
				cache.item_like.setImageResource(R.drawable.support);
			}
			
			
			cache.item_title.setTag(bean.id);
			cache.item_img.setTag(bean.id);
			cache.item_like.setTag(bean.id);
			cache.item_dislike.setTag(bean.id);
			cache.item_comment.setTag(bean.id);
			return convertView;

		}

		private final class ViewCache {
			public TextView item_title;
			public TextView item_time;
			public ImageView item_img;
			public TextView item_deadline;
			public TextView like_num;
			public TextView dislike_num;
			public TextView comment_num;
			public TextView sum_num;
			public ImageView item_like;
			public ImageView item_dislike;
			public ImageView item_comment;
			public InclineTextView team_item_class;
		}

		public class myListener implements OnClickListener {

			@Override
			public void onClick(View view) {

				if (StaticBoolean.NetLink == false) {
					Toast.makeText(getApplicationContext(), "网络连接异常～无法执行此操作!",Toast.LENGTH_SHORT).show();
					return;
				}
				int id = view.getId();
				String activityid =view.getTag().toString();
				switch (id) {
				case R.id.outactivity_item_title:
					Intent intent = new Intent(MyActivity.this,MyActivityDetail.class);
					intent.putExtra("activityid", activityid);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
					break;
				case R.id.outactivity_item_img:
					Intent intent32 = new Intent(MyActivity.this,MyActivityDetail.class);
					intent32.putExtra("activityid", activityid);
					startActivity(intent32);
					finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
					break;
				case R.id.outactivity_item_like:
					attentActivity(activityid, 1);
					break;
				case R.id.outactivity_item_dislike:
					attentActivity(activityid, 0);
					break;
				case R.id.outactivity_item_comment:
					Intent intent1 = new Intent(MyActivity.this,InActivityComment.class);
					
					Map<String, Object> map = new HashMap<String, Object>();
					
					map.put("activityid", activityid);
					map.put("from", "034");
					
					for (int i = 0; i < StaticList.MyActicitylist.size(); i++) {
						if (StaticList.MyActicitylist.get(i).id.equals(activityid)) {
														
							map.put("title", StaticList.MyActicitylist.get(i).title);
							map.put("img", StaticList.MyActicitylist.get(i).imgurl);
							map.put("clas", StaticList.MyActicitylist.get(i).category);
							map.put("deadline", StaticList.MyActicitylist.get(i).deadline);
							map.put("time", StaticList.MyActicitylist.get(i).time);

							break;
						}
					}

					
					DateBundle  myMap= new DateBundle();
					myMap.setMap(map);
					Bundle bundle = new Bundle();
					bundle.putSerializable("map", myMap);
					intent1.putExtras(bundle); 
					
					startActivity(intent1);
					finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
					break;
				}
			}

			public void attentActivity(String id, int flag) {
				dbhelper.CreatOrOpen("schooltime");
				Cursor cursor = dbhelper.selectInfo("select * from takepart where activityid='"+ id + "';");
				if (cursor.moveToNext()) {
					dbhelper.closeDB();
					Toast.makeText(getApplicationContext(), "您已经评价过了哦～",Toast.LENGTH_SHORT).show();
					return;
				}
				dbhelper.excuteInfo("insert into takepart values('" + id+ "','"+flag+"');");
				dbhelper.closeDB();

				if (flag == 1) {
					for (int i = 0; i < StaticList.InActicitylist.size(); i++) {
						if (StaticList.InActicitylist.get(i).id.equals(id)) {
							StaticList.InActicitylist.get(i).pridenum++;
							break;
						}
					}
					for (int i = 0; i < StaticList.MyActicitylist.size(); i++) {
						if (StaticList.MyActicitylist.get(i).id .equals(id)) {
							StaticList.MyActicitylist.get(i).pridenum++;
							break;
						}
					}
				} else {
					for (int i = 0; i < StaticList.InActicitylist.size(); i++) {
						if (StaticList.InActicitylist.get(i).id .equals(id)) {
							StaticList.InActicitylist.get(i).opposenum++;
							break;
						}
					}
					for (int i = 0; i < StaticList.MyActicitylist.size(); i++) {
						if (StaticList.MyActicitylist.get(i).id .equals(id)) {
							StaticList.MyActicitylist.get(i).opposenum++;
							break;
						}
					}
				}

				notifyDataSetChanged();

				Intent intent = new Intent(MyActivity.this, CoreService.class);
				intent.putExtra("tag", StaticInt.takepartactivity);
				intent.putExtra("activityid", id);
				intent.putExtra("type", flag);
				startService(intent);
				
				return;
			}
		}
	}
}
