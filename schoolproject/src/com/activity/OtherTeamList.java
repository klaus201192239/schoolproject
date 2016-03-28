package com.activity;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.http.HttpUtil;
import com.pagebean.otherteambean;
import com.staticdata.StaticString;
import com.utilt.utils;

public class OtherTeamList extends Activity  {

	private ListView mListView;
	private MyAdapter mAdapter;
	private ProgressDialog progressDialog;
	private String from,activityid;
	private List<otherteambean> list=new ArrayList<otherteambean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_team_list);
		intiData() ;
		intiView() ;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.other_team_list, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(from.equals("05")){
				startActivity(new Intent(OtherTeamList.this, InActivity.class).putExtra("CurrentId", activityid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			}else{
				startActivity(new Intent(OtherTeamList.this, InActivityDetail.class).putExtra("id", activityid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			}
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

	public void intiData() {
		Intent intent = getIntent();
		activityid=intent.getStringExtra("activityid");
		from=intent.getStringExtra("from");
		geneItems() ;
	}

	public void intiView() {		
		mListView = (ListView) findViewById(R.id.teamlist_listView);
		mAdapter = new MyAdapter(this);
	//	mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				// TODO Auto-generated method stub
				int pwd=list.get(arg2).password;
				if(pwd==0){
					Intent intent=new Intent(OtherTeamList.this,JoinTeam.class);
					intent.putExtra("activityid",activityid);
					intent.putExtra("from", from);
					intent.putExtra("teamid", list.get(arg2).id);
					intent.putExtra("leader", list.get(arg2).leader);
					intent.putExtra("name", list.get(arg2).name);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
				}else{
					Intent intent=new Intent(OtherTeamList.this,JoinTeamVerify.class);
					intent.putExtra("activityid",activityid);
					intent.putExtra("from", from);
					intent.putExtra("teamid", list.get(arg2).id);
					intent.putExtra("pwd", list.get(arg2).password);
					intent.putExtra("leader", list.get(arg2).leader);
					intent.putExtra("name", list.get(arg2).name);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
				}
			}
			
		});
	}

	private int geneItems() {
		
		progressDialog = ProgressDialog.show(OtherTeamList.this, "","正在获取团队信息,请稍候！");
		new Thread(){
			public void run(){
				String httpjson=HttpUtil.sendGet(StaticString.server+"getteam", "activityid="+activityid);
				Message msg_listData = new Message();
				if(httpjson.equals("error")){
					msg_listData.what=0;
				}else{
					try {
						JSONArray array=new JSONArray(httpjson);
						for(int i=0;i<array.length();i++){
							otherteambean bean=new otherteambean();
							bean.abstractinfo =array.getJSONObject(i).getString("Abstract");
							bean.id=array.getJSONObject(i).getString("_id");
							bean.leader=array.getJSONObject(i).getString("Leader");
							bean.name=array.getJSONObject(i).getString("Name");
							bean.need=array.getJSONObject(i).getString("Need");
							bean.slogan=array.getJSONObject(i).getString("Slogan");
							bean.password=Integer.parseInt(array.getJSONObject(i).getString("Password"));
							list.add(bean);
						}
						msg_listData.what=1;
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				handler.sendMessage(msg_listData);
			}
		}.start();
		
		return 1;
	}
	
	public void btonclik(View v){
		int vid=v.getId();
		if(vid==R.id.teamlist_back){
			if(from.equals("05")){
				startActivity(new Intent(OtherTeamList.this, InActivity.class).putExtra("CurrentId", activityid));
				finish();
				overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
			}else{
				startActivity(new Intent(OtherTeamList.this, InActivityDetail.class).putExtra("id", activityid));
				finish();
				overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
			}
			return ;
		}
		if(vid==R.id.teamlist_thisbt){
			return ;
		}
		if(vid==R.id.teamlist_addbt){
			startActivity(new Intent(OtherTeamList.this,CreateTeam.class).putExtra("activityid", activityid).putExtra("from", from));
			finish();
			overridePendingTransition(R.anim.slide_clam, R.anim.slide_clam);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
            switch (message.what) {
            case 0:
            	progressDialog.dismiss(); //关闭进度条
            	
            	mListView.setAdapter(mAdapter);
            	
            	Toast.makeText(getApplicationContext(), "网络连接或其他意外错误", Toast.LENGTH_SHORT).show();  
            	break;
            case 1:
            	progressDialog.dismiss(); //关闭进度条
            	
            	mListView.setAdapter(mAdapter);
            	
 //           	mAdapter.notifyDataSetChanged();
            	break;
            }
		}
	};
	
	public class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private ViewCache cache;

		public MyAdapter(Context context) {
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView=inflater.inflate(R.layout.teamlist_item,null);
				cache=new ViewCache();
				cache.teamlist_item_name=(TextView)convertView.findViewById(R.id.teamlist_item_name);
				cache.teamlist_item_leader=(TextView)convertView.findViewById(R.id.teamlist_item_leader);
				cache.teamlist_item_password=(TextView)convertView.findViewById(R.id.teamlist_item_password);
				cache.teamlist_item_abstract=(TextView)convertView.findViewById(R.id.teamlist_item_abstract);
				cache.teamlist_item_need=(TextView)convertView.findViewById(R.id.teamlist_item_need);
				cache.teamlist_item_slogan=(TextView)convertView.findViewById(R.id.teamlist_item_slogan);
				cache.teamlist_item_img=(ImageView)convertView.findViewById(R.id.teamlist_item_img);
				convertView.setTag(cache);
			}
			else{
				cache=new ViewCache();
				cache=(ViewCache)convertView.getTag();
			}
			
			otherteambean bean=list.get(position);
						
			cache.teamlist_item_name.setText("团队名称:"+bean.name);
			cache.teamlist_item_leader.setText("队长信息:"+bean.leader);
			if(bean.password==0){
				cache.teamlist_item_password.setVisibility(View.GONE);
			}else{
				cache.teamlist_item_password.setText("需要口令验证");
			}
			
			cache.teamlist_item_abstract.setText("团队简介:"+bean.abstractinfo);
			cache.teamlist_item_need.setText("团队需求:"+bean.need);
			cache.teamlist_item_slogan.setText("团队口号:"+bean.slogan);
			cache.teamlist_item_img.setImageResource(utils.getOtherTeamIcon(position));
			
			
			return convertView;
		}
		
		private final class ViewCache {
			public TextView teamlist_item_name;
			public TextView teamlist_item_leader;
			public TextView teamlist_item_password;
			public TextView teamlist_item_abstract;
			public TextView teamlist_item_need;
			public TextView teamlist_item_slogan;
			public ImageView teamlist_item_img;
		}
	}
}
