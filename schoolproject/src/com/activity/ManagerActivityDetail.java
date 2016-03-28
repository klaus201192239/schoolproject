package com.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.http.HttpUtil;
import com.http.ImgLoader;
import com.staticdata.StaticBoolean;
import com.staticdata.StaticList;
import com.staticdata.StaticString;
import com.view.InclineTextView;

public class ManagerActivityDetail extends Activity {

	private DataAdapter mAdapter;
	private ListView mListView;
	private Date deadline;
	private int  pride, oppose, comment;
	private String activityid,title, orgnization, img, clas,  time, contenthtml,attachmentname,oganizationid;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager_activity_detail);
		
		intiData();
		mListView = (ListView) findViewById(R.id.managerdetail_listView);
		mAdapter = new DataAdapter(this);
		mListView.setAdapter(mAdapter);
		getNetData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manager_activity_detail, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(ManagerActivityDetail.this, ManagerActivity.class).putExtra("CurrentId", activityid).putExtra("oganizationid", oganizationid));
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
		
		Intent intent = getIntent();
		activityid = intent.getStringExtra("activityid");
		oganizationid=intent.getStringExtra("oganizationid");
		
		for (int i = 0; i < StaticList.Managelist.size(); i++) {
			if (StaticList.Managelist.get(i).id.equals( activityid)) {
				title = StaticList.Managelist.get(i).title;
				orgnization = "";
				img = StaticList.Managelist.get(i).imgurl;
				clas = StaticList.Managelist.get(i).category;
				deadline = StaticList.Managelist.get(i).deadline;
				time = StaticList.Managelist.get(i).time;
				pride = StaticList.Managelist.get(i).pridenum;
				oppose = StaticList.Managelist.get(i).opposenum;
				comment = StaticList.Managelist.get(i).commentnum;
				contenthtml = "";
				attachmentname = "";
				break;
			}
		}
		
		if(intent.getIntExtra("callover", -1)==0){
			orgnization=intent.getStringExtra("orgnization");
			contenthtml = intent.getStringExtra("contenthtml");
			attachmentname = intent.getStringExtra("attachmentname");
		}
	}

	public void getNetData() {
		Intent intent = getIntent();
		if(intent.getIntExtra("callover", -1)==0){
			return ;
		}
		progressDialog = ProgressDialog.show(ManagerActivityDetail.this, "","正在获取活动详细信息,请稍候！");
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
            }
            
		}
	};
	
	public void btonclik(View v) {
		int vid = v.getId();
		if(vid==R.id.managerdetail_back){
			startActivity(new Intent(ManagerActivityDetail.this, ManagerActivity.class)
			.putExtra("CurrentId", activityid)
			.putExtra("oganizationid", oganizationid));
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}
		if(vid==R.id.managerdetail_signin){
			startActivity(new Intent(ManagerActivityDetail.this, ManagerActivityPerson.class)
			.putExtra("activityid", activityid)
			.putExtra("oganizationid", oganizationid)
			.putExtra("title", title)
			.putExtra("oganization", orgnization));
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return ;
		}
		if(vid==R.id.managerdetail_dianming){
			//orgnization,contenthtml,attachmentname,activityid,oganizationid
			Intent intent=new Intent(ManagerActivityDetail.this,CallOver.class);
			intent.putExtra("orgnization", orgnization);
			intent.putExtra("contenthtml",contenthtml);
			intent.putExtra("attachmentname",attachmentname);
			intent.putExtra("activityid",activityid);
			intent.putExtra("oganizationid",oganizationid);
			intent.putExtra("title",title);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
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

			
			item_like.setVisibility(View.GONE);
			item_dislike.setVisibility(View.GONE);
			
			//item_like.setOnClickListener(new myListener());
			//item_dislike.setOnClickListener(new myListener());
			item_comment.setOnClickListener(new myListener());

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
				e.printStackTrace();
			}
									
			if(d1.getYear()==0){
				item_time.setText(" ");
			}else{
				SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
				item_time.setText("活动时间："+format.format(d1).toString()+"~"+format.format(d2).toString());
			}
			
			like_num.setText("支持"+pride + "  ");
			like_num.setTextColor(Color.RED);
			dislike_num.setText("反对"+oppose + "");
			dislike_num.setTextColor(Color.BLUE);
			
			comment_num.setText(comment + "");	
			content.setText(Html.fromHtml(contenthtml));
			attachment.setText( attachmentname);
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
					Toast.makeText(getApplicationContext(), "您处于管理界面～管理者无法评价自己发布的活动!",Toast.LENGTH_SHORT).show();
					return;
				case R.id.inactivity_detail_dislike:
					Toast.makeText(getApplicationContext(), "您处于管理界面～管理者无法评价自己发布的活动!",Toast.LENGTH_SHORT).show();
					return;
				case R.id.inactivity_detail_comment:
					Intent intent1=new Intent(ManagerActivityDetail.this,InActivityComment.class);
					
					Map<String, Object> map = new HashMap<String, Object>();
					
					map.put("activityid", activityid);
					map.put("from", "025");
					intent1.putExtra("oganizationid", oganizationid);
					
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
				}
			}			
		}
	}
}
