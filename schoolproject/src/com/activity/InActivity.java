package com.activity;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

public class InActivity extends Activity implements IXListViewListener {

	private String schoolid;
	private String schoolimg;
	private DBHelper dbhelper;
	private XListView mListView;
	private MyAdapter mAdapter;
	private Intent intent;
	private Handler mHandler;
	private static Boolean isExit = false;
	
	private int loadMoreTag=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_in);
		intiData();
		intiView();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.in, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click();
		}
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			Toast.makeText(getApplicationContext(), "1111", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Toast.makeText(getApplicationContext(), "2222222",
					Toast.LENGTH_SHORT).show();
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
			Intent intent = new Intent(InActivity.this, CoreService.class);
			intent.putExtra("tag", StaticInt.saveinactivity);
			startService(intent);
			finish();
			// System.exit(0);
		}
	}

	public void intiData() {
		SharedPreferences settings = getSharedPreferences("schooltime",MODE_PRIVATE);
		schoolid = settings.getString("ShoolId", "");
		schoolimg = settings.getString("SchoolImg", "");
		dbhelper = new DBHelper(this);
		intent = getIntent();
	}

	public void intiView() {
		mHandler = new Handler();
		mListView = (XListView) findViewById(R.id.activity_team_xListView);
		mListView.setPullLoadEnable(true);
		mAdapter = new MyAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(this);
		if (intent != null) {
			String y = intent.getStringExtra("CurrentId");
			if (y != null && y.length() != 0) {
				int tempIndex = 0;
				for (int i = 0; i < StaticList.InActicitylist.size() - 1; i++) {
					tempIndex++;
					if (StaticList.InActicitylist.get(i).id.equals(y)) {
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
		
		if((nowTime-StaticLong.InActicity_onfreshTime)<3000){
			onLoad();
			Toast.makeText(this, "亲，前一秒刚刷新过哦～", Toast.LENGTH_SHORT).show();
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
						  StaticLong.InActicity_onfreshTime=System.currentTimeMillis();
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
						  Toast.makeText(getApplicationContext(),"网络连接不可用",Toast.LENGTH_SHORT).show(); 
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

					String httpjson = HttpUtil.sendGet(StaticString.server+ "getinactivity", "schoolid=" + schoolid+ "&currentid=0");

					if (httpjson.equals("error")) {
					} else {
						if (httpjson.equals("nothing")) {

						} else {
							try {
								JSONArray jsonarry = new JSONArray(httpjson);
								int size = jsonarry.length();
								StaticList.InActicitylist.clear();// 此处是不一样的
								for (int i = 0; i < size; i++) {
									JSONObject cur = jsonarry.getJSONObject(i);
									InActivityBean bean = new InActivityBean();
									bean.id = cur.getString("id");// 获取第一列的值,第一列的索引从0开始
									bean.title = cur.getString("title");
									bean.imgurl = cur.getString("imgurl");
									bean.category = cur.getString("category");
									SimpleDateFormat sfStart = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",java.util.Locale.ENGLISH);
									bean.deadline = sfStart.parse(cur.getString("deadline"));
									bean.time = cur.getString("time");
									bean.pridenum = cur.getInt("pridenum");
									bean.opposenum = cur.getInt("opposenum");
									bean.commentnum = cur.getInt("commentnum");
									bean.onlyteam = cur.getInt("onlyteam");
									StaticList.InActicitylist.add(bean);
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

				}
			};// .start();

			onFresh.start();
			try {
				onFresh.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			StaticList.InActicitylistb.clear();
			loadNext();
			
			return 1;
			
		} else {
			if (StaticBoolean.NetLink == false) {
				return 0;
			}

			int x = StaticList.InActicitylistb.size();

			if (x != 0) {
				int y = StaticList.InActicitylist.size();

				for (int i = 0; i < x; i++) {

					if (StaticList.InActicitylistb.get(i).id.compareTo(StaticList.InActicitylist.get(y - 1 + i).id) < 0) {

						StaticList.InActicitylist.add(StaticList.InActicitylistb.get(i));

					}
				}

				StaticList.InActicitylistb.clear();

				loadNext();

				return 1;
				
			} else {
				Thread onLoadMore=new Thread() {
					public void run() {
						String httpjson = HttpUtil.sendGet(
								StaticString.server + "getinactivity","schoolid="+ schoolid
										+ "&currentid="+ StaticList.InActicitylist.get(StaticList.InActicitylist.size() - 1).id);

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
										SimpleDateFormat sfStart = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",java.util.Locale.ENGLISH);
										try {
											bean.deadline = sfStart.parse(cur.getString("deadline"));
										} catch (ParseException e) {

										}
										bean.time = cur.getString("time");
										bean.pridenum = cur.getInt("pridenum");
										bean.opposenum = cur.getInt("opposenum");
										bean.commentnum = cur.getInt("commentnum");
										bean.onlyteam = cur.getInt("onlyteam");
										StaticList.InActicitylist.add(bean);
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

						
					}
				};//.start();
				
				onLoadMore.start();
				try {
					onLoadMore.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				StaticList.InActicitylistb.clear();
				loadNext();
				
				return 1;
			}

		}

	}

	public void loadNext() {
		Intent intent = new Intent(InActivity.this, CoreService.class);
		intent.putExtra("tag", StaticInt.downinactivity);
		startService(intent);
	}

	public void to_table(View v) {
		int vid = v.getId();
		if (vid == R.id.activity_team_leftView) {
			Intent intent = new Intent(InActivity.this, OutActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_clam, R.anim.slide_clam);
			return;
		}
		if (vid == R.id.activity_team_rightView) {
			Intent intent = new Intent(InActivity.this, UserCenter.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_clam, R.anim.slide_clam);
			return;
		}
		if (vid == R.id.activity_teame_centerView) {
			return;
		}
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

			// return
			// list.size();//************************************************
			return StaticList.InActicitylist == null ? 1: StaticList.InActicitylist.size() + 1;
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
			// return StaticList.InActicitylist.get(position-1);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void dataFresh() {
			notifyDataSetChanged();
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
					imgloader.display((ImageView) convertView.findViewById(R.id.inactivityschool), schoolimg);
					// convertView.findViewById(R.id.inactivityschool)).setImageResource(R.drawable.school);
					return convertView;
				case 1:
					convertView = inflater.inflate(R.layout.inactivity_item,null);// 生成条目界面对象
					cache = new ViewCache();
					cache.item_title = (TextView) convertView
							.findViewById(R.id.team_item_title);
					cache.item_img = (ImageView) convertView
							.findViewById(R.id.team_item_img);
					cache.team_item_class = (InclineTextView) convertView
							.findViewById(R.id.team_item_class);
					cache.item_deadline = (TextView) convertView
							.findViewById(R.id.team_item_deadline);
					cache.item_time = (TextView) convertView
							.findViewById(R.id.team_item_time);
					cache.item_like = (ImageView) convertView
							.findViewById(R.id.team_item_like);
					cache.item_dislike = (ImageView) convertView
							.findViewById(R.id.team_item_dislike);
					cache.item_comment = (ImageView) convertView
							.findViewById(R.id.team_item_comment);
					cache.item_join = (ImageView) convertView
							.findViewById(R.id.team_item_join);
					cache.like_num = (TextView) convertView
							.findViewById(R.id.team_item_like_num);
					cache.dislike_num = (TextView) convertView
							.findViewById(R.id.team_item_dislike_num);
					cache.comment_num = (TextView) convertView
							.findViewById(R.id.team_item_comment_num);
					cache.sum_num = (TextView) convertView
							.findViewById(R.id.team_item_sumnum);

					cache.item_title.setOnClickListener(new myListener());
					cache.item_like.setOnClickListener(new myListener());
					cache.item_dislike.setOnClickListener(new myListener());
					cache.item_comment.setOnClickListener(new myListener());
					cache.item_join.setOnClickListener(new myListener());
					cache.item_img.setOnClickListener(new myListener());
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

			InActivityBean bean = StaticList.InActicitylist.get(position - 1);

			cache.item_title.setText(bean.title);
			imgloader.display(cache.item_img, bean.imgurl);
			cache.team_item_class.setText(bean.category);

			if (bean.deadline.getYear() == 0) {
				cache.item_deadline.setText(" ");
			} else {
				Date date = new Date();

				if (date.before(bean.deadline)) {
					cache.item_deadline.setTextColor(Color.BLUE);

					SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
					cache.item_deadline.setText("报名截止："+ format.format(bean.deadline) + "(还未截止)");
				} else {
					cache.item_deadline.setTextColor(Color.RED);

					SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
					cache.item_deadline.setText("报名截止："+ format.format(bean.deadline) + "(已经截止)");
				}
			}

			int x = bean.time.indexOf("~");
			String str1 = bean.time.substring(0, x);
			String str2 = bean.time.substring(x + 1);
			SimpleDateFormat sfStart = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.ENGLISH);
			Date d1 = null, d2 = null;
			try {
				d1 = sfStart.parse(str1);
				d2 = sfStart.parse(str2);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (d1.getYear() == 0) {
				cache.item_time.setText(" ");
			} else {
				SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
				cache.item_time.setText("活动时间：" + format.format(d1).toString()+ "~" + format.format(d2).toString());
			}

			cache.like_num.setText(bean.pridenum + "");
			cache.dislike_num.setText(bean.opposenum + "");
			cache.comment_num.setText(bean.commentnum + "");
			double t1 = bean.pridenum + 0.0 + bean.commentnum * 2+ bean.opposenum, t = 0.0;
			if (t1 != 0) {
				t = ((bean.pridenum + 0.0 + bean.commentnum * 2 - bean.opposenum) * 10)/ t1;
			}
			BigDecimal bg = new BigDecimal(t);
			double f = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			cache.sum_num.setText("受欢迎程度：" + f);

			int ty = -1;
			dbhelper.CreatOrOpen("schooltime");
			Cursor cursor = dbhelper.selectInfo("select type from takepart where activityid='"+ bean.id + "';");
			if (cursor.moveToNext()) {
				ty = cursor.getInt(0);
			}
			dbhelper.closeDB();

			if (ty != -1) {
				if (ty == 0) {
					cache.item_dislike.setImageResource(R.drawable.against1);
					cache.item_like.setImageResource(R.drawable.support);
				} else {
					if (ty == 1) {
						cache.item_like.setImageResource(R.drawable.support1);
						cache.item_dislike.setImageResource(R.drawable.against);
					}
				}
			} else {
				cache.item_dislike.setImageResource(R.drawable.against);
				cache.item_like.setImageResource(R.drawable.support);
			}

			cache.item_title.setTag(bean.id);
			cache.item_img.setTag(bean.id);
			cache.item_like.setTag(bean.id);
			cache.item_dislike.setTag(bean.id);
			cache.item_comment.setTag(bean.id);
			cache.item_join.setTag(bean.id + "&" + bean.onlyteam);

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
			public ImageView item_join;
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
				String activityid = "";
				if (id == R.id.team_item_join) {
					int str = view.getTag().toString().indexOf("&");
					activityid = view.getTag().toString().substring(0, str);
				} else {
					activityid = view.getTag().toString();
				}
				switch (id) {
				case R.id.team_item_title:
					Intent intent = new Intent(InActivity.this,InActivityDetail.class);
					intent.putExtra("id", activityid);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
					break;
				case R.id.team_item_img:
					Intent intent11 = new Intent(InActivity.this,InActivityDetail.class);
					intent11.putExtra("id", activityid);
					startActivity(intent11);
					finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

					break;
				case R.id.team_item_like:
					attentActivity(activityid, 1);
					break;
				case R.id.team_item_dislike:
					attentActivity(activityid, 0);
					break;
				case R.id.team_item_comment:
					Intent intent1 = new Intent(InActivity.this,InActivityComment.class);
					
					Map<String, Object> map = new HashMap<String, Object>();
					
					map.put("activityid", activityid);
					map.put("from", "05");

					for (int i = 0; i < StaticList.InActicitylist.size(); i++) {
						if (StaticList.InActicitylist.get(i).id.equals(activityid)) {

							map.put("title",StaticList.InActicitylist.get(i).title);
							map.put("img",StaticList.InActicitylist.get(i).imgurl);
							map.put("clas",StaticList.InActicitylist.get(i).category);
							map.put("deadline",StaticList.InActicitylist.get(i).deadline);
							map.put("time",StaticList.InActicitylist.get(i).time);
							
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
				case R.id.team_item_join:
					int index = view.getTag().toString().indexOf("&");
					activityid = view.getTag().toString().substring(0, index);
					int type = Integer.parseInt(view.getTag().toString().substring(index + 1));
					if (type == 2) {
						Toast.makeText(getApplicationContext(), "此活动无需通过本软件报名",Toast.LENGTH_SHORT).show();
						break;
					} else {
						boolean tag = false;
						dbhelper.CreatOrOpen("schooltime");
						Cursor cur = dbhelper.selectInfo("select * from attendactivity where activityid='"+ activityid + "';");

						while (cur.moveToNext()) {
							tag = true;
						}
						dbhelper.closeDB();
						if (tag == true) {
							Toast.makeText(getApplicationContext(),"您已经报过名了哦～～", Toast.LENGTH_SHORT).show();
							return;
						} else {
							if (type == 0) {
								Intent intent2 = new Intent(InActivity.this,OnlySignUp.class);
								intent2.putExtra("activityid", activityid);
								intent2.putExtra("from", "05");
								intent2.putExtra("teamtag", 0);
								startActivity(intent2);
								finish();
								overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
								break;
							}
							if (type == 1) {
								
								String nname="";
								
								for(int ii=0;ii<StaticList.InActicitylist.size();ii++){
									if(StaticList.InActicitylist.get(ii).id.equals(activityid)){
										nname=StaticList.InActicitylist.get(ii).title;
										break;
									}
								}
								
								Intent intent3 = new Intent(InActivity.this,CreateTeam.class);
								intent3.putExtra("activityid", activityid);
								intent3.putExtra("from", "05");
								intent3.putExtra("activityname", nname);
								startActivity(intent3);
								finish();
								
								overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
								
								break;
							}
						}
					}
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
				dbhelper.excuteInfo("insert into takepart values('" + id+ "','" + flag + "');");
				dbhelper.closeDB();

				if (flag == 1) {
					for (int i = 0; i < StaticList.InActicitylist.size(); i++) {
						if (StaticList.InActicitylist.get(i).id.equals(id)) {
							StaticList.InActicitylist.get(i).pridenum++;
							break;
						}
					}
				} else {
					for (int i = 0; i < StaticList.InActicitylist.size(); i++) {
						if (StaticList.InActicitylist.get(i).id.equals(id)) {
							StaticList.InActicitylist.get(i).opposenum++;
							break;
						}
					}
				}

				dataFresh();

				Intent intent = new Intent(InActivity.this, CoreService.class);
				intent.putExtra("tag", StaticInt.takepartactivity);
				intent.putExtra("activityid", id);
				intent.putExtra("type", flag);
				startService(intent);
				return;
			}
		}
	}
}
