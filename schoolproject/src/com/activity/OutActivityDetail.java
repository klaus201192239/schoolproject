package com.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OutActivityDetail extends Activity {

	private DataAdapter mAdapter;
	private ListView mListView;
	private int  pride, oppose, comment;
	private String activityid,title, orgnization, img,  time, contenthtml,attachmentname,currentpage, clas;
	private Date deadline;
	private ProgressDialog progressDialog;
	private DBHelper dbhelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_out_activity_detail);
		intiData();
		mListView = (ListView) findViewById(R.id.out_detail_listView);
		mAdapter = new DataAdapter(this);
		//mListView.setAdapter(mAdapter);
		
		getNetData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.out_activity_detail, menu);
		return true;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(OutActivityDetail.this, OutActivity.class).putExtra("CurrentId", activityid));
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
		currentpage=intent.getStringExtra("currentpage");
		for (int i = 0; i < StaticList.OutActicitylist.size(); i++) {
			if (StaticList.OutActicitylist.get(i).id.equals(activityid)) {
				title = StaticList.OutActicitylist.get(i).title;
				orgnization = "";
				img = StaticList.OutActicitylist.get(i).imgurl;
				clas = StaticList.OutActicitylist.get(i).category;
				deadline = StaticList.OutActicitylist.get(i).deadline;
				time = StaticList.OutActicitylist.get(i).time;
				pride = StaticList.OutActicitylist.get(i).pridenum;
				oppose = StaticList.OutActicitylist.get(i).opposenum;
				comment = StaticList.OutActicitylist.get(i).commentnum;
				contenthtml = "";
				attachmentname = "";
				break;
			}
		}
	}

	public void getNetData() {
		progressDialog = ProgressDialog.show(OutActivityDetail.this, "","正在获取活动详细信息,请稍候！");
		new Thread() {
			public void run() {
				
				String httpjson = HttpUtil.sendGet(StaticString.server+"getoutactivitydetail","activityid="+activityid);
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
            	mListView.setAdapter(mAdapter);
            	Toast.makeText(getApplicationContext(), "网络连接或其他意外错误", Toast.LENGTH_SHORT).show();  
            	break;
            case 1:
            	progressDialog.dismiss(); //关闭进度条
            	mListView.setAdapter(mAdapter);
            	//mAdapter.notifyDataSetChanged();
            	break;
            }
		}
	};
	
	public void btonclik(View v) {
		int vid = v.getId();
		if(vid==R.id.out_detail_back){
			startActivity(new Intent(OutActivityDetail.this, OutActivity.class).putExtra("CurrentId", activityid));
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}
		if(vid==R.id.out_detail_share){
			
			return ;
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
		
		@SuppressWarnings("deprecation")
		@SuppressLint("SimpleDateFormat")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.outactivity_detail_item,null);

			TextView item_title = (TextView) convertView.findViewById(R.id.outactivity_detail_title);
			TextView org = (TextView) convertView.findViewById(R.id.outactivity_detail_orgnization);
			ImageView item_img = (ImageView) convertView.findViewById(R.id.outactivity_detail_img);
			InclineTextView team_item_class = (InclineTextView) convertView.findViewById(R.id.outactivity_detail_class);
			TextView item_deadline = (TextView) convertView.findViewById(R.id.outactivity_detail_deadline);
			TextView item_time = (TextView) convertView.findViewById(R.id.outactivity_detail_time);
			ImageView item_like = (ImageView) convertView.findViewById(R.id.outactivity_detail_like);
			ImageView item_dislike = (ImageView) convertView.findViewById(R.id.outactivity_detail_dislike);
			ImageView item_comment = (ImageView) convertView.findViewById(R.id.outactivity_detail_comment);
			TextView like_num = (TextView) convertView.findViewById(R.id.outactivity_detail_like_num);
			TextView dislike_num = (TextView) convertView.findViewById(R.id.outactivity_detail_dislike_num);
			TextView comment_num = (TextView) convertView.findViewById(R.id.outactivity_detail_comment_num);
			TextView content = (TextView) convertView.findViewById(R.id.outactivity_detail_content);
			TextView attachment = (TextView) convertView.findViewById(R.id.outactivity_detail_attachment);

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
			Cursor cursor = dbhelper.selectInfo("select type from takepartout where activityid='"+ activityid + "';");
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
					Toast.makeText(getApplicationContext(), "网络连接异常～无法执行此操作!",Toast.LENGTH_SHORT).show();
					return ;
				}
				int id=view.getId();
				switch(id){
				case R.id.outactivity_detail_like:
					attentActivity(activityid,1);
					break;
				case R.id.outactivity_detail_dislike:
					attentActivity(activityid,0);
					break;
				case R.id.outactivity_detail_comment:
					Intent intent1=new Intent(OutActivityDetail.this,OutActivityComment.class);
					
					Map<String, Object> map = new HashMap<String, Object>();

					map.put("activityid", activityid);
					map.put("from", "014");
					map.put("currentpage", currentpage);
					
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
				case R.id.outactivity_detail_attachment:
					SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);				
					Builder builder=new AlertDialog.Builder(OutActivityDetail.this);
					builder.setTitle("发送附件请求");
					builder.setMessage("系统将会发送该附件至您的"+set.getString("Email", "")+"邮箱中～");
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which){
							Intent intent2 = new Intent(OutActivityDetail.this, CoreService.class);
							intent2.putExtra("tag", StaticInt.emailattachmentout);
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
				Cursor cursor = dbhelper.selectInfo("select * from takepartout where activityid='"+ id + "';");
				if (cursor.moveToNext()) {
					dbhelper.closeDB();
					Toast.makeText(getApplicationContext(), "您已经评价过了哦～",Toast.LENGTH_SHORT).show();
					return;
				}
				dbhelper.excuteInfo("insert into takepartout values('"+ id + "','"+flag+"');");
				dbhelper.closeDB();
				
				if (flag == 1) {
					for(int i=0;i<StaticList.OutActicitylist.size();i++){
						if(StaticList.OutActicitylist.get(i).id.equals(id)){
							StaticList.OutActicitylist.get(i).pridenum++;
							break;
						}
					}
					pride++;
				} else {
					for(int i=0;i<StaticList.OutActicitylist.size();i++){
						if(StaticList.OutActicitylist.get(i).id.equals(id)){
							StaticList.OutActicitylist.get(i).opposenum++;
							break;
						}
					}
					oppose++;
				}
				
				notifyDataSetChanged();
				
				Intent intent = new Intent(OutActivityDetail.this, CoreService.class);
				intent.putExtra("tag", StaticInt.takepartoutactivity);
				intent.putExtra("activityid", id);
				intent.putExtra("type",flag);
				startService(intent);
				
				return;
			}
		}
	}
}
