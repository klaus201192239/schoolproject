package com.activity;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.adapter.GuideViewAdapter;
import com.dbutil.DBHelper;

public class GuideActivity extends Activity {
	private View view1, view2, view3;
	private List<View> viewList;
	ViewPager mViewPager;
	private TextView in;
	private int tag = 0;
	private DBHelper dbhelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		dbhelper = new DBHelper(this);
		initView();
		createDatabase();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return false;
	}

	private void initView() {

		mViewPager = (ViewPager) findViewById(R.id.viewpager);

		@SuppressWarnings("static-access")
		LayoutInflater lf = getLayoutInflater().from(this);
		view1 = lf.inflate(R.layout.guide1, null);
		view2 = lf.inflate(R.layout.guide2, null);
		view3 = lf.inflate(R.layout.guide3, null);

		viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
		viewList.add(view1);
		viewList.add(view2);
		viewList.add(view3);

		mViewPager.setAdapter(new GuideViewAdapter(viewList));

		in = (TextView) view3.findViewById(R.id.guideIn);
		in.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (tag == 1) {
					startActivity(new Intent(GuideActivity.this,LoginActivity.class));
					finish();
				} else {
					tag = 2;
					Toast.makeText(getApplicationContext(),"正在注册系统服务～完成后界面会自动跳转", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void createDatabase() {
		new Thread() {
			public void run() {
				dbhelper.CreatOrOpen("schooltime");
				dbhelper.excuteInfo("create table inactivity(id text,title text,imgurl text,category text,deadline text,time text,pridenum int,opposenum int,commentnum int,onlyteam int);");
				dbhelper.excuteInfo("create table takepart(activityid text,type int);");
				dbhelper.excuteInfo("create table attendactivity(activityid text);");
				dbhelper.excuteInfo("create table outactivity(id text,title text,imgurl text,category text,deadline text,time text,pridenum int,opposenum int,commentnum int);");
				dbhelper.excuteInfo("create table takepartout(activityid text,type int);");
				dbhelper.excuteInfo("create table myactivity(id text,title text,imgurl text,category text,deadline text,time text,pridenum int,opposenum int,commentnum int);");
				dbhelper.excuteInfo("create table myteam(id text,name text,leaderid text,idcard text,leadername text,activityname text);");
				dbhelper.excuteInfo("create table teammember(teamid text,userid text,idcard text,name text,major text,degree int,grade int,phone text,abstract text,state int);");
				dbhelper.excuteInfo("create table notice(id INTEGER PRIMARY KEY,title text,publisher text,content text,time text,cid text,type int);");
				dbhelper.excuteInfo("create table signin(activityid text);");

			
				dbhelper.closeDB();
				Message msg_listData = new Message();
				msg_listData.what = 0;
				handler.sendMessage(msg_listData);
			}
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.guide, menu);
		return true;
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case 0:
				if (tag == 0) {
					tag = 1;
				}
				if (tag == 2) {
					startActivity(new Intent(GuideActivity.this,
							LoginActivity.class));
					finish();
				}
				break;
			}
		}
	};
}
