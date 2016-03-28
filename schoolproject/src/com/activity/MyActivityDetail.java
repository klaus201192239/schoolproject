package com.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
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

import com.bean.DateBundle;
import com.dbutil.DBHelper;
import com.http.HttpUtil;
import com.http.ImgLoader;
import com.service.CoreService;
import com.staticdata.StaticBoolean;
import com.staticdata.StaticInt;
import com.staticdata.StaticList;
import com.staticdata.StaticString;
import com.view.InclineTextView;

public class MyActivityDetail extends Activity {
	
	private DataAdapter mAdapter;
	private ListView mListView;
	private int  pride, oppose, comment;
	private String activityid,title, orgnization, img, clas,  time, contenthtml,attachmentname,userid;
	private Date deadline;
	private ProgressDialog progressDialog;
	private DBHelper dbhelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_activity_detail);
		
		intiData();
		mListView = (ListView) findViewById(R.id.myactivitydetail_listView);
		mAdapter = new DataAdapter(this);
		mListView.setAdapter(mAdapter);
		getNetData();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_activity_detail, menu);
		return true;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(MyActivityDetail.this, MyActivity.class).putExtra("CurrentId", activityid));
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
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
		
		dbhelper = new DBHelper(this);
		Intent intent = getIntent();
		activityid = intent.getStringExtra("activityid");
		SharedPreferences set = getSharedPreferences("schooltime",MODE_PRIVATE);
		userid=set.getString("Id","");
		
		for (int i = 0; i < StaticList.MyActicitylist.size(); i++) {
			if (StaticList.MyActicitylist.get(i).id .equals(activityid)) {
				title = StaticList.MyActicitylist.get(i).title;
				orgnization = "";
				img = StaticList.MyActicitylist.get(i).imgurl;
				clas = StaticList.MyActicitylist.get(i).category;
				deadline = StaticList.MyActicitylist.get(i).deadline;
				time = StaticList.MyActicitylist.get(i).time;
				pride = StaticList.MyActicitylist.get(i).pridenum;
				oppose = StaticList.MyActicitylist.get(i).opposenum;
				comment = StaticList.MyActicitylist.get(i).commentnum;
				contenthtml = "";
				attachmentname = "";
				break;
			}
		}
	}

	public void getNetData() {
		progressDialog = ProgressDialog.show(MyActivityDetail.this, "","正在获取活动详细信息,请稍候！");
		new Thread() {
			public void run() {
				String httpjson = HttpUtil.sendGet(StaticString.server+"getinactivitydetail","activityid="+activityid);
				Message msg_listData = new Message();
				if(httpjson.equals("error")){
					msg_listData.what=0;
				}else{
					try {
						JSONObject obj=new JSONObject(httpjson);
						orgnization=obj.getString("organization");
						contenthtml = obj.getString("content");
						attachmentname = obj.getString("attachment");
						msg_listData.what=1;
					} catch (Exception e) {
						// TODO Auto-generated catch block
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
            	progressDialog.dismiss(); //关闭进度条
            	Toast.makeText(getApplicationContext(), "网络连接或其他意外错误", Toast.LENGTH_SHORT).show();  
            	break;
            case 1:
            	progressDialog.dismiss(); //关闭进度条
            	mAdapter.notifyDataSetChanged();
            	break;
            case 2:
            	progressDialog.dismiss(); //关闭进度条
            	Toast.makeText(getApplicationContext(), "取消报名成功", Toast.LENGTH_SHORT).show();  
            	
            	startActivity(new Intent(MyActivityDetail.this, MyActivity.class));
    			finish();
    			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            	break;
            }
            
		}
	};
	
	public void btonclik(View v) {
		int vid = v.getId();
		if(vid==R.id.myactivitydetail_back){
			startActivity(new Intent(MyActivityDetail.this, MyActivity.class).putExtra("CurrentId", activityid));
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}
		if(vid==R.id.myactivitydetail_signin){
			Intent intent=new Intent(MyActivityDetail.this,SignIn.class);
			intent.putExtra("activityid", activityid);
			intent.putExtra("title", title);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return ;
		}
		if(vid==R.id.myactivitydetail_quit){
			
			
			Builder builder=new AlertDialog.Builder(MyActivityDetail.this);
			builder.setTitle("取消报名");
			builder.setMessage("确定不参加本次活动了么？");
			builder.setPositiveButton("不参加了", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){
					progressDialog = ProgressDialog.show(MyActivityDetail.this, "","正在取消报名,请稍候！");
					new Thread() {
						public void run() {
							String httpjson = HttpUtil.sendGet(StaticString.server+"quitmyactivity","activityid="+activityid+"&userid="+userid);
							Message msg_listData = new Message();
							if(httpjson.equals("error")){
								msg_listData.what=0;
							}else{
								if(httpjson.equals("ok")){
									dbhelper.CreatOrOpen("schooltime");
									dbhelper.excuteInfo("delete from attendactivity where activityid='"+ activityid + "';");
									dbhelper.excuteInfo("delete from myactivity where id='"+activityid+"';");
									dbhelper.closeDB();
									for(int i=0;i<StaticList.MyActicitylist.size();i++){
										if(StaticList.MyActicitylist.get(i).id.equals(activityid)){
											StaticList.MyActicitylist.remove(i);
										}
									}
									
									msg_listData.what=2;
								}
								
							}
							handler.sendMessage(msg_listData);
						}
					}.start();
				}
			});
			builder.setNegativeButton("继续参加",null);
			builder.create().show();
		}
	}

	private class DataAdapter extends BaseAdapter {
		@SuppressWarnings("unused")
		private Context ctx;
		private LayoutInflater inflater;
		private ImgLoader imgloader;
		public DataAdapter(Context ctx) {
			this.ctx = ctx;
			inflater = LayoutInflater.from(ctx);
			this.imgloader = new ImgLoader(ctx);
		}

		@Override
		public int getCount() {
			return 1;
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

		public void update(){
			this.notifyDataSetChanged();
		}

		@SuppressWarnings("deprecation")
		@SuppressLint("SimpleDateFormat")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.inactivity_detail_item,null);

			TextView item_title = (TextView) convertView.findViewById(R.id.inactivity_detail_title);
			TextView org = (TextView) convertView.findViewById(R.id.inactivity_detail_orgnization);
			ImageView item_img = (ImageView) convertView.findViewById(R.id.inactivity_detail_img);
			InclineTextView team_item_class = (InclineTextView) convertView.findViewById(R.id.inactivity_detail_class);
			TextView item_deadline = (TextView) convertView.findViewById(R.id.inactivity_detail_deadline);
			TextView item_time = (TextView) convertView.findViewById(R.id.inactivity_detail_time);
			ImageView item_like = (ImageView) convertView.findViewById(R.id.inactivity_detail_like);
			ImageView item_dislike = (ImageView) convertView.findViewById(R.id.inactivity_detail_dislike);
			ImageView item_comment = (ImageView) convertView.findViewById(R.id.inactivity_detail_comment);
			TextView like_num = (TextView) convertView.findViewById(R.id.inactivity_detail_like_num);
			TextView dislike_num = (TextView) convertView.findViewById(R.id.inactivity_detail_dislike_num);
			TextView comment_num = (TextView) convertView.findViewById(R.id.inactivity_detail_comment_num);
			TextView content = (TextView) convertView.findViewById(R.id.inactivity_detail_content);
			TextView attachment = (TextView) convertView.findViewById(R.id.inactivity_detail_attachment);

			item_like.setOnClickListener(new myListener());
			item_dislike.setOnClickListener(new myListener());
			item_comment.setOnClickListener(new myListener());
			attachment.setOnClickListener(new myListener());
			
			item_title.setText(title);
			org.setText(orgnization);
			imgloader.display(item_img, img);
			team_item_class.setText(clas);
			
			if(deadline.getYear()==0){
				item_deadline.setText(" ");
			}else{
				Date date = new Date();
				if(date.before(deadline)){
					item_deadline.setTextColor(Color.BLUE);
					SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
					item_deadline.setText("报名截止："+format.format(deadline)+"(还未截止)");
				}else{
					item_deadline.setTextColor(Color.RED);
					SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
					item_deadline.setText("报名截止："+format.format(deadline)+"(已经截止)");
				}
			}
			
			
			int x=time.indexOf("~");
			String str1=time.substring(0, x);
			String str2=time.substring(x+1);
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
				item_time.setText(" ");
			}else{
				SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
				item_time.setText("活动时间："+format.format(d1).toString()+"~"+format.format(d2).toString());
			}

			like_num.setText(pride + "");
			dislike_num.setText(oppose + "");
			comment_num.setText(comment + "");	
			content.setText(Html.fromHtml(contenthtml));
			
			if(attachmentname.length()==0||attachmentname==null||attachmentname.equals("nothing")){
				attachment.setVisibility(View.GONE);
			}else{
				
				attachment.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
				attachment.setText("附件："+attachmentname);
				attachment.setTextColor(Color.parseColor("#E6E6FA"));
				
			}
			
			
			int ty=-1;
			dbhelper.CreatOrOpen("schooltime");
			Cursor cursor = dbhelper.selectInfo("select type from takepart where activityid='"+activityid+ "';");
			if (cursor.moveToNext()) {				
				ty=cursor.getInt(0);
			}
			dbhelper.closeDB();
			
			if(ty!=-1){
				if(ty==0){
					item_dislike.setImageResource(R.drawable.against1);
					item_like.setImageResource(R.drawable.support);
				}else{
					if(ty==1){
						item_like.setImageResource(R.drawable.support1);
						item_dislike.setImageResource(R.drawable.against);
					}
				}
			}else{
				item_dislike.setImageResource(R.drawable.against);
				item_like.setImageResource(R.drawable.support);
			}
			
			
			return convertView;
		}
		
		public class myListener implements OnClickListener {
			@Override
			public void onClick(View view) {

				if(StaticBoolean.NetLink==false){
					Toast.makeText(getApplicationContext(), "网络连接异常～无法评价!",Toast.LENGTH_SHORT).show();
					return ;
				}
				int id=view.getId();
				switch(id){
				case R.id.inactivity_detail_like:
					attentActivity(activityid,1);
					break;
				case R.id.inactivity_detail_dislike:
					attentActivity(activityid,0);
					break;
				case R.id.inactivity_detail_comment:
					Intent intent1=new Intent(MyActivityDetail.this,InActivityComment.class);
					
					Map<String, Object> map = new HashMap<String, Object>();
					
					map.put("activityid", activityid);
					map.put("from", "035");
								
					map.put("title", title);
					map.put("img", img);
					map.put("clas", clas);
					map.put("deadline", deadline);
					map.put("time", time);

					DateBundle  myMap= new DateBundle();
					myMap.setMap(map);
					Bundle bundle = new Bundle();
					bundle.putSerializable("map", myMap);
					intent1.putExtras(bundle); 
					
					startActivity(intent1);
					finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
					break;
				case R.id.inactivity_detail_attachment:
					SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);				
					Builder builder=new AlertDialog.Builder(MyActivityDetail.this);
					builder.setTitle("发送附件请求");
					builder.setMessage("系统将会发送该附件至您的"+set.getString("Email", "")+"邮箱中～");
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which){
							Intent intent2 = new Intent(MyActivityDetail.this, CoreService.class);
							intent2.putExtra("tag", StaticInt.emailattachment);
							intent2.putExtra("activityid", activityid);
							startService(intent2);
						}
					});
					builder.setNegativeButton("NO",null);
					builder.create().show();
					break;
				}
			}
			public void attentActivity(String id,int flag) {
				
				dbhelper.CreatOrOpen("schooltime");
				Cursor cursor = dbhelper.selectInfo("select * from takepart where activityid='"+ id + "';");
				if (cursor.moveToNext()) {
					dbhelper.closeDB();
					Toast.makeText(getApplicationContext(), "您已经评价过了哦～",Toast.LENGTH_SHORT).show();
					return;
				}
				dbhelper.excuteInfo("insert into takepart values('"+ id + "');");
				dbhelper.closeDB();
				
				if (flag == 1) {
					for(int i=0;i<StaticList.InActicitylist.size();i++){
						if(StaticList.InActicitylist.get(i).id==id){
							StaticList.InActicitylist.get(i).pridenum++;
							break;
						}
					}
					for(int i=0;i<StaticList.MyActicitylist.size();i++){
						if(StaticList.MyActicitylist.get(i).id==id){
							StaticList.MyActicitylist.get(i).pridenum++;
							break;
						}
					}
					pride++;
				} else {
					for(int i=0;i<StaticList.InActicitylist.size();i++){
						if(StaticList.InActicitylist.get(i).id==id){
							StaticList.InActicitylist.get(i).opposenum++;
							break;
						}
					}
					for(int i=0;i<StaticList.MyActicitylist.size();i++){
						if(StaticList.MyActicitylist.get(i).id==id){
							StaticList.MyActicitylist.get(i).opposenum++;
							break;
						}
					}
					oppose++;
				}
				
				update();
				
				Intent intent = new Intent(MyActivityDetail.this, CoreService.class);
				intent.putExtra("tag", StaticInt.takepartactivity);
				intent.putExtra("activityid", activityid);
				intent.putExtra("type",flag);
				startService(intent);
				return;
			}
		}
	}
}
