package com.activity;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.dbutil.DBHelper;
import com.http.HttpUtil;
import com.pagebean.TeamMemberBean;
import com.staticdata.StaticBoolean;
import com.staticdata.StaticString;
import com.utilt.utils;

public class ExamineTeam extends Activity {

	private int power,type;
	private String teamid,stuid,idcard,schoolid; 
	private DataAdapter mAdapter;
	private ListView mListView;
	private ProgressDialog progressDialog;
	private ImageView bt;
	private List<TeamMemberBean> list=new ArrayList<TeamMemberBean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_examine_team);
		
		if(StaticBoolean.NetLink==false){
			Toast.makeText(getApplicationContext(), "网络连接不正常，无法获取最新数据～", Toast.LENGTH_SHORT).show();
		}
		
		intidata();		
		mListView = (ListView) findViewById(R.id.teammember_listView);
		mAdapter = new DataAdapter(this);
		mListView.setAdapter(mAdapter);
		bt=(ImageView)findViewById(R.id.teammember_upload);
		if(power==0){
			//bt.setText("退 出 该 团 队");
			bt.setImageResource(R.drawable.tuichutuan);
		}else{
			//bt.setText("解 散 该 团 队");
			bt.setImageResource(R.drawable.jiesantuan);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.examine_team, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(ExamineTeam.this,MyTeams.class));
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
		
		Intent intent=getIntent();
		power=intent.getIntExtra("power", -1);
		teamid=intent.getStringExtra("teamid");
		
		DBHelper dbhlper=new DBHelper(this);
		dbhlper.CreatOrOpen("schooltime");
		Cursor cur=dbhlper.selectInfo("select * from teammember where teamid='"+teamid+"';");
		while(cur.moveToNext()){
			TeamMemberBean bean=new TeamMemberBean();
			bean.abstracts=cur.getString(8);
			bean.degree=cur.getInt(5);
			bean.grade=cur.getInt(6);
			bean.major=cur.getString(4);
			bean.name=cur.getString(3);
			bean.phone=cur.getString(7);
			bean.state=cur.getInt(9);
			bean.userid=cur.getString(1);
			bean.idcard=cur.getString(2);
			list.add(bean);
		}
	}
	
	public void btonclick(View v){
		if(v.getId()==R.id.teammember_upload){
			
			if(StaticBoolean.NetLink==false){
				Toast.makeText(getApplicationContext(), "网络连接不正常～", Toast.LENGTH_SHORT).show();
				return ;
			}
			
			String Btitle="",Bcontent="",BOk="",BNO="";
			if(power==0){
				Btitle="退出团队";
				Bcontent="您确定退出该团队么？";
				BOk="确定退出";
				BNO="暂不退出";
			}else{
				Btitle="解散团队";
				Bcontent="您确定解散该团队么";
				BOk="确定解散";
				BNO="暂不解散";
			}
			
			Builder builder=new AlertDialog.Builder(ExamineTeam.this);
			builder.setTitle(Btitle);
			builder.setMessage(Bcontent);
			builder.setPositiveButton(BOk, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){
					teamManager();
				}
			});
			builder.setNegativeButton(BNO,null);
			builder.create().show();
			
		}else{
			startActivity(new Intent(ExamineTeam.this,MyTeams.class));
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}
	}
	
	public void teamManager(){
		if(power==0){
			SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
			stuid=set.getString("Id","");
			idcard=set.getString("StudentId","");
			schoolid=set.getString("ShoolId","");
			type=0;
			progressDialog = ProgressDialog.show(ExamineTeam.this, "", "正在执行操作,请稍候！");
			new Thread(){
				public void run(){
					String httpjson = HttpUtil.sendGet(StaticString.server+ "teammanager","teamid="+teamid+"&userid="+stuid+"&type="+type+"&schoolid="+schoolid+"&idcard="+idcard);
					Message msg_listData = new Message();
					if(httpjson.equals("error")){
						msg_listData.what=0;
					}else{
						if(httpjson.equals("ok")){
							msg_listData.what=2;
						}
						else{
							msg_listData.what=0;
						}
					}
					handler.sendMessage(msg_listData);
				}
			}.start();
		}else{
			SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
			stuid=set.getString("Id", "");
			type=0;
			progressDialog = ProgressDialog.show(ExamineTeam.this, "", "正在执行操作,请稍候！");
			new Thread(){
				public void run(){
					String httpjson = HttpUtil.sendGet(StaticString.server+ "quitteam","teamid="+teamid);
					Message msg_listData = new Message();
					if(httpjson.equals("error")){
						msg_listData.what=0;
					}else{
						if(httpjson.equals("ok")){
							msg_listData.what=2;
						}
						else{
							msg_listData.what=0;
						}
					}
					handler.sendMessage(msg_listData);
				}
			}.start();
		}
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
			
			if(power==0){
				if(convertView==null){
					convertView = inflater.inflate(R.layout.teamber_item1,null);
					cache = new ViewCache();
					cache.timg= (ImageView) convertView.findViewById(R.id.memeber1_imghead);	
					cache.tname = (TextView) convertView.findViewById(R.id.memeber1_name);		
					cache.tinfo= (TextView) convertView.findViewById(R.id.memeber1_info);		
					cache.tel= (TextView) convertView.findViewById(R.id.memeber1_hisTel);	
					cache.tabstract= (TextView) convertView.findViewById(R.id.memeber1_abstract);		
					cache.astate= (TextView) convertView.findViewById(R.id.memeber1_state);		
					convertView.setTag(cache);
				}
				else{
					cache = new ViewCache();
					cache = (ViewCache) convertView.getTag();
				}
				
				TeamMemberBean bean=list.get(position);
				cache.timg.setImageResource(utils.getTeamMemberIcon(position));
				cache.tname.setText(bean.name+"("+utils.getDegreeName(bean.degree)+"-"+bean.grade+"级)");
				cache.tel.setText("联系方式："+bean.phone);
				if(bean.major.length()>15){
					cache.tinfo.setText(bean.major.substring(0, 15));
				}else{
					cache.tinfo.setText(bean.major);
				}

				cache.tabstract.setText("自我简介："+bean.abstracts+"fdsfdsfs");
				if(bean.state==0){
					cache.astate.setText("待审核");
				}else{
					cache.astate.setText("已审核通过");
				}
		
				return convertView;
			}
			else{
				if(convertView==null){
					convertView = inflater.inflate(R.layout.teamber_item2,null);
					cache = new ViewCache();
					cache.timg= (ImageView) convertView.findViewById(R.id.memeber2_imghead);		
					cache.tname = (TextView) convertView.findViewById(R.id.memeber2_name);		
					cache.tinfo= (TextView) convertView.findViewById(R.id.memeber2_info);	
					cache.tel= (TextView) convertView.findViewById(R.id.memeber2_hisTel);	
					cache.tabstract= (TextView) convertView.findViewById(R.id.memeber2_abstract);		
					cache.ok= (ImageView) convertView.findViewById(R.id.memeber2_ok);		
					cache.no= (ImageView) convertView.findViewById(R.id.memeber2_no);
					
					cache.ok.setOnClickListener(new myListener());
					cache.no.setOnClickListener(new myListener());
					
					convertView.setTag(cache);
				}
				else{
					cache = new ViewCache();
					cache = (ViewCache) convertView.getTag();
				}
				
				TeamMemberBean bean=list.get(position);
				
				cache.timg.setImageResource(utils.getTeamMemberIcon(position));
				cache.tname.setText(bean.name+"("+utils.getDegreeName(bean.degree)+"-"+bean.grade+"级)");
				cache.tel.setText("联系方式："+bean.phone);
				if(bean.major.length()>15){
					cache.tinfo.setText(bean.major.substring(0, 15));
				}else{
					cache.tinfo.setText(bean.major);
				}

				cache.tabstract.setText("自我简介："+bean.abstracts+"fdsfdsfs");
				if(bean.state==0){
					//cache.astate.setText("待审核");
					cache.ok.setTag(bean.userid+"~"+bean.idcard);
					cache.no.setTag(bean.userid+"~"+bean.idcard);
				}else{
					cache.ok.setVisibility(View.GONE);
					cache.no.setVisibility(View.GONE);
				}
		
				return convertView;
			}
			
			
		}
		private final class ViewCache {
			public ImageView timg;
			public TextView tname;
			public TextView tinfo;
			public TextView tel;
			public TextView tabstract;
			public TextView astate;
			public ImageView ok;
			public ImageView no;
		}
		
		public class myListener implements OnClickListener {
			@Override
			public void onClick(View view) {
				
				if(StaticBoolean.NetLink==false){
					Toast.makeText(getApplicationContext(), "网络连接不正常～", Toast.LENGTH_SHORT).show();
					return ;
				}
				
				int vid=view.getId();
				
				String tagStr=view.getTag().toString();
				int index=tagStr.indexOf("~");
				stuid=tagStr.substring(0,index);
				idcard=tagStr.substring(index+1);
				
				
				SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
				schoolid=set.getString("ShoolId","");
				
				if(vid==R.id.memeber2_ok){
					type=1;
				}else{
					type=0;
				}
				
				progressDialog = ProgressDialog.show(ExamineTeam.this, "", "正在执行操作,请稍候！");
				
				new Thread(){
					public void run(){
						String httpjson = HttpUtil.sendGet(StaticString.server+ "teammanager","teamid="+teamid+"&userid="+stuid+"&type="+type+"&schoolid="+schoolid+"&idcard="+idcard);
						Message msg_listData = new Message();
						if(httpjson.equals("error")){
							msg_listData.what=0;
						}else{
							if(httpjson.equals("ok")){
								msg_listData.what=1;
							}
							else{
								msg_listData.what=0;
							}
						}
						handler.sendMessage(msg_listData);
					}
				}.start();
			}
		}
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
				dataChange();
				progressDialog.dismiss(); // 关闭进度条
				Toast.makeText(getApplicationContext(), "操作成功～",Toast.LENGTH_SHORT).show();
				mAdapter.notifyDataSetChanged();
				break;
			case 2:
				backteam();
				progressDialog.dismiss(); // 关闭进度条
				Toast.makeText(getApplicationContext(), "操作成功～",Toast.LENGTH_SHORT).show();
				startActivity(new Intent(ExamineTeam.this,MyTeams.class));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				break;
			}
		}
	};
	public void dataChange(){
		DBHelper dbHelper=new DBHelper(this);
		dbHelper.CreatOrOpen("schooltime");
		if(type==0){
			dbHelper.excuteInfo("delete from teammember where teamid='"+teamid+"' and userid='"+stuid+"' and idcard='"+idcard+"';");
			for(int i=0;i<list.size();i++){
				if(list.get(i).userid.equals(stuid)){
					list.remove(i);
					break;
				}
			}
		}else{
			dbHelper.excuteInfo("update teammember set state=1 where teamid='"+teamid+"' and userid='"+stuid+"' and idcard='"+idcard+"';");
			for(int i=0;i<list.size();i++){
				if(list.get(i).userid.equals(stuid)){
					list.get(i).state=1;
					break;
				}
			}
		}
		dbHelper.closeDB();
	}
	public void backteam(){
		DBHelper dbHelper=new DBHelper(this);
		dbHelper.CreatOrOpen("schooltime");
		dbHelper.excuteInfo("delete from teammember where teamid='"+teamid+"';");
		dbHelper.excuteInfo("delete from myteam where id='"+teamid+"';");		
		dbHelper.closeDB();
	}
}
