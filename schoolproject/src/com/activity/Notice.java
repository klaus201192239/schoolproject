package com.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.dbutil.DBHelper;
import com.pagebean.NoticeBean;
import com.staticdata.StaticBoolean;

public class Notice extends Activity {

	private DataAdapter mAdapter;
	private ListView mListView;
	private List<NoticeBean> list=new ArrayList<NoticeBean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);
		
		if(StaticBoolean.NetLink==false){
			Toast.makeText(getApplicationContext(), "网络连接不正常，无法获取最新数据～", Toast.LENGTH_SHORT).show();
		}
		
		intidata();		
		mListView = (ListView) findViewById(R.id.noticelist_listView);
		mAdapter = new DataAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Intent intent=new Intent(Notice.this,NoticeDetail.class);
				intent.putExtra("title", list.get(arg2).title);
				intent.putExtra("content", list.get(arg2).content);
				intent.putExtra("publisher", list.get(arg2).publisher);
				intent.putExtra("time", list.get(arg2).time);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.notice, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(Notice.this, UserCenter.class));
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
	
	@SuppressLint("SimpleDateFormat")
	public void intidata(){
		
		//notice(id int,title text,publisher text,content text,time text);");
		
		DBHelper dbhelper=new DBHelper(this);
		dbhelper.CreatOrOpen("schooltime");
		
		dbhelper.excuteInfo("(id INTEGER PRIMARY KEY,title text,publisher text,content text,time text,cid text,type int);");

		Cursor cur=dbhelper.selectInfo("select * from notice order by id desc;");
		while(cur.moveToNext()){
			NoticeBean bean=new NoticeBean();
			bean.id=cur.getInt(0);
			bean.title=cur.getString(1);
			bean.publisher=cur.getString(2);
			bean.content=cur.getString(3);
			
			SimpleDateFormat sfStart = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",java.util.Locale.ENGLISH) ;		
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				bean.time=format.format(sfStart.parse(cur.getString(4))).toString();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			list.add(bean);
		}
		dbhelper.closeDB();
	}
	
	public void btonclick(View v){
		startActivity(new Intent(Notice.this, UserCenter.class));
		finish();
		overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
		return ;
	}
	
	private class DataAdapter extends BaseAdapter {
		@SuppressWarnings("unused")
		private Context ctx;
		private LayoutInflater inflater;
		private ViewCache cache;
		public DataAdapter(Context ctx) {
			this.ctx = ctx;
			inflater = LayoutInflater.from(ctx);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		
		@SuppressLint("SimpleDateFormat")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView==null){
				convertView = inflater.inflate(R.layout.notice_item,null);
				cache = new ViewCache();
				cache.teamname = (TextView) convertView.findViewById(R.id.notice_item_name);		
				cache.time= (TextView) convertView.findViewById(R.id.notice_item_time);			
				convertView.setTag(cache);
			}
			else{
				cache = new ViewCache();
				cache = (ViewCache) convertView.getTag();
			}
			
			NoticeBean bean=list.get(position);
			cache.teamname.setText(bean.title);
			cache.time.setText(bean.time);

			return convertView;
		}
		private final class ViewCache {
			public TextView teamname;
			public TextView time;
		}
	}
}

