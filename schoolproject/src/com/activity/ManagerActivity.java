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

import com.bean.DateBundle;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ManagerActivity extends Activity implements IXListViewListener {

	private String oganizationid,imgschool;
	private Intent intent;
	private XListView mListView;
	private MyAdapter mAdapter;
	private Handler mHandler;
	private ProgressDialog progressDialog;


	private int loadMoreTag=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);
		
		intiData();
				
		if (StaticBoolean.NetLink == false) {
			Toast.makeText(getApplicationContext(), "网络连接不可用",Toast.LENGTH_SHORT).show();
			intiView();
		} else {
			if(StaticList.Managelist.size()==0){
				getNetData();
			}else{
				intiView();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.my, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			StaticList.Managelist.clear();
			StaticList.Managelistb.clear();
			Intent intent = new Intent(ManagerActivity.this, UserCenter.class);
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
	
	public void to_back(View v){
		if(v.getId()==R.id.myactivity_back){
			StaticList.Managelist.clear();
			StaticList.Managelistb.clear();
			Intent intent = new Intent(ManagerActivity.this, UserCenter.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}
	}

	public void getNetData() {

		StaticList.Managelist.clear();
		progressDialog = ProgressDialog.show(ManagerActivity.this, "", "正在获取数据,请稍候！");
		new Thread() {
			public void run() {
				String httpjson = HttpUtil.sendGet(StaticString.server + "getmanagelist","oganizationid="+oganizationid+ "&currentid=0");
				Message msg_listData = new Message();
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
							StaticList.Managelist.add(bean);
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
				progressDialog.dismiss(); // 关闭进度条
				Toast.makeText(getApplicationContext(), "网络连接或其他意外错误",Toast.LENGTH_SHORT).show();
				break;
			case 1:
				progressDialog.dismiss(); // 关闭进度条
				
				intiView();
				
				StaticLong.Manage_onfreshTime=System.currentTimeMillis();
				//mAdapter.notifyDataSetChanged();
				loadNext();
				break;
			}
		}
	};

	public void intiData() {
		intent = getIntent();
		oganizationid=intent.getStringExtra("oganizationid");
		
		SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
		imgschool=set.getString("SchoolImg","");
		
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
				
				for (int i = 0; i < StaticList.Managelist.size() - 1; i++) {
					tempIndex++;
					if (StaticList.Managelist.get(i).id .equals(y)) {
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
		
		if((nowTime-StaticLong.Manage_onfreshTime)<3000){
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
						StaticLong.Manage_onfreshTime=System.currentTimeMillis();
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
					String httpjson = HttpUtil.sendGet(StaticString.server + "getmanagelist","oganizationid="+oganizationid+ "&currentid=0");
					if (httpjson.equals("error")) {
					} else {
						if (httpjson.equals("nothing")) {
						} else {
							try {
								JSONArray jsonarry = new JSONArray(httpjson);
								int size = jsonarry.length();

								StaticList.Managelist.clear();// 此处是不一样的

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
									StaticList.Managelist.add(bean);
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

			StaticList.Managelistb.clear();
			loadNext();
			
			return 1;
			
		} else {
			if (StaticBoolean.NetLink == false) {
				return 0;
			}
			int x = StaticList.Managelistb.size();
			if (x != 0) {
				int y = StaticList.Managelist.size();
				for (int i = 0; i < x; i++) {
					if (StaticList.Managelistb.get(i).id .compareTo(StaticList.Managelist.get(y - 1 + i).id)<0) {
						StaticList.Managelist.add(StaticList.Managelistb.get(i));
					}
				}
				
				StaticList.Managelistb.clear();
				loadNext();

				return 1;
				
			} else {
				if(StaticList.Managelist.size()<=0){
					return 0;
				}
				
				Thread onLoadMore=new Thread() {
					public void run() {
						String httpjson = HttpUtil.sendGet(StaticString.server + "getmanagelist","oganizationid="+oganizationid+"&currentid="+ StaticList.Managelist.get(StaticList.Managelist.size() - 1).id);
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
										StaticList.Managelist.add(bean);
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
				
				StaticList.Managelistb.clear();
				loadNext();
				return 1;
			}
		}	
	}

	public void loadNext() {
		Intent intent = new Intent(ManagerActivity.this, CoreService.class);
		intent.putExtra("tag", StaticInt.downManagerActivity);
		intent.putExtra("oganizationid",oganizationid);
		startService(intent);
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
			return StaticList.Managelist.size() + 1;
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
					imgloader.display((ImageView) convertView.findViewById(R.id.inactivityschool), imgschool);
					return convertView;
				case 1:
					convertView = inflater.inflate(R.layout.outactivity_item,null);// 生成条目界面对象
					cache = new ViewCache();
					cache.item_title = (TextView) convertView.findViewById(R.id.outactivity_item_title);
					cache.item_img = (ImageView) convertView.findViewById(R.id.outactivity_item_img);
					cache.team_item_class = (InclineTextView) convertView.findViewById(R.id.outactivity_item_class);
					cache.item_deadline = (TextView) convertView.findViewById(R.id.outactivity_item_deadline);
					cache.item_time = (TextView) convertView.findViewById(R.id.outactivity_item_time);
					cache.item_like = (ImageView) convertView.findViewById(R.id.outactivity_item_like);
					cache.item_dislike = (ImageView) convertView.findViewById(R.id.outactivity_item_dislike);
					cache.item_comment = (ImageView) convertView.findViewById(R.id.outactivity_item_comment);
					cache.like_num = (TextView) convertView.findViewById(R.id.outactivity_item_like_num);
					cache.dislike_num = (TextView) convertView.findViewById(R.id.outactivity_item_dislike_num);
					cache.comment_num = (TextView) convertView.findViewById(R.id.outactivity_item_comment_num);
					cache.sum_num = (TextView) convertView.findViewById(R.id.outactivity_item_sumnum);

					cache.item_title.setOnClickListener(new myListener());
					cache.item_comment.setOnClickListener(new myListener());
					cache.item_img.setOnClickListener(new myListener());
					
					cache.item_like.setVisibility(View.GONE);
					cache.item_dislike.setVisibility(View.GONE);
					
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

			InActivityBean bean = StaticList.Managelist.get(position - 1);

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
				e.printStackTrace();
			}
						
			if(d1.getYear()==0){
				cache.item_time.setText(" ");
			}else{
				SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
				cache.item_time.setText("活动时间："+format.format(d1).toString()+"~"+format.format(d2).toString());
			}

			cache.like_num.setText("支持 "+bean.pridenum + "");
			cache.like_num.setTextColor(Color.RED);
			cache.dislike_num.setText("反对 "+bean.opposenum + "");
			cache.dislike_num.setTextColor(Color.BLUE);
			cache.comment_num.setText(+bean.commentnum + "");
			double t1=bean.pridenum+0.0+bean.commentnum*2+bean.opposenum,t=0.0;
			if(t1!=0){
				t=((bean.pridenum+0.0+bean.commentnum*2-bean.opposenum)*10)/t1;
			}
			BigDecimal bg = new BigDecimal(t);  
            double f = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
			cache.sum_num.setText("受欢迎程度："+f);

			cache.item_title.setTag(bean.id);
			cache.item_comment.setTag(bean.id);
			cache.item_img.setTag(bean.id);
			
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
				String activityid = view.getTag().toString();
				switch (id) {
				case R.id.outactivity_item_like:
					Toast.makeText(getApplicationContext(), "您处于管理界面～管理者无法评价自己发布的活动!",Toast.LENGTH_SHORT).show();
					return;
				case R.id.outactivity_item_dislike:
					Toast.makeText(getApplicationContext(), "您处于管理界面～管理者无法评价自己发布的活动!",Toast.LENGTH_SHORT).show();
					return;
				
				case R.id.outactivity_item_title:
					Intent intent = new Intent(ManagerActivity.this,ManagerActivityDetail.class);
					intent.putExtra("activityid", activityid);
					intent.putExtra("oganizationid", oganizationid);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
					break;
				case R.id.outactivity_item_img:
					Intent intentn = new Intent(ManagerActivity.this,ManagerActivityDetail.class);
					intentn.putExtra("activityid", activityid);
					intentn.putExtra("oganizationid", oganizationid);
					startActivity(intentn);
					finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
					break;
				
				case R.id.outactivity_item_comment:
					Intent intent1 = new Intent(ManagerActivity.this,InActivityComment.class);
					
					Map<String, Object> map = new HashMap<String, Object>();
					
					map.put("activityid", activityid);
					intent1 .putExtra("oganizationid", oganizationid);
					map.put("from", "024");
					
					for (int i = 0; i < StaticList.Managelist.size(); i++) {
						if (StaticList.Managelist.get(i).id.equals(activityid)) {
														
							map.put("title", StaticList.Managelist.get(i).title);
							map.put("img", StaticList.Managelist.get(i).imgurl);
							map.put("clas", StaticList.Managelist.get(i).category);
							map.put("deadline", StaticList.Managelist.get(i).deadline);
							map.put("time", StaticList.Managelist.get(i).time);

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
		}
	}
}
