package com.activity;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.pagebean.MyTeamBean;
import com.staticdata.StaticBoolean;

public class MyTeams extends Activity {

	private DataAdapter mAdapter;
	private ListView mListView;
	private List<MyTeamBean> list=new ArrayList<MyTeamBean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_teams);
		
		if(StaticBoolean.NetLink==false){
			Toast.makeText(getApplicationContext(), "网络连接不正常，无法获取最新数据～", Toast.LENGTH_SHORT).show();
		}
		
		intidata();		
		mListView = (ListView) findViewById(R.id.myteams_listView);
		mAdapter = new DataAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Intent intent=new Intent(MyTeams.this,ExamineTeam.class);
				intent.putExtra("teamid", list.get(arg2).id);
				intent.putExtra("power", list.get(arg2).power);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_teams, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(MyTeams.this, UserCenter.class));
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
	
	public void intidata(){
		
		//myteam(id int,name text,leaderid int,leadername text,activityname text);");
		//myteam(id text,name text,leaderid text,idcard text,leadername text,activityname text);");
		
		String userid,idcard;
		SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
		userid=set.getString("Id", "");
		idcard=set.getString("StudentId", "");
		
		DBHelper dbhelper=new DBHelper(this);
		dbhelper.CreatOrOpen("schooltime");

		Cursor cur=dbhelper.selectInfo("select * from myteam order by id desc;");
		
		while(cur.moveToNext()){
			MyTeamBean bean=new MyTeamBean();
			bean.id=cur.getString(0);
			bean.activityname=cur.getString(5);
			bean.name=cur.getString(1)+"("+cur.getString(4)+")";
			Cursor cc=dbhelper.selectInfo("select count(*) from teammember where teamid='"+bean.id+"';");
			while(cc.moveToNext()){
				bean.memberSum=cc.getInt(0);
			}
			if(userid.equals(cur.getString(2))||idcard.equals(cur.getString(3))){
				bean.power=1;
			}else{
				bean.power=0;
			}
			list.add(bean);
		}
		dbhelper.closeDB();
	}
	
	public void btonclick(View v){
		startActivity(new Intent(MyTeams.this, UserCenter.class));
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

		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView==null){
				convertView = inflater.inflate(R.layout.myteam_item,null);
				cache = new ViewCache();
				cache.teamname = (TextView) convertView.findViewById(R.id.myteam_name);		
				cache.activity= (TextView) convertView.findViewById(R.id.myteam_activity);		
				cache.members= (TextView) convertView.findViewById(R.id.myteam_member);		
				convertView.setTag(cache);
			}
			else{
				cache = new ViewCache();
				cache = (ViewCache) convertView.getTag();
			}
			
			MyTeamBean bean=list.get(position);
			cache.teamname.setText(bean.name);
			cache.activity.setText(bean.activityname);
			cache.members.setText("共有"+bean.memberSum+"人申请加入");
			
			return convertView;
		}
		private final class ViewCache {
			public TextView teamname;
			public TextView activity;
			public TextView members;
		}
	}
}
