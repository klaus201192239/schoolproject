package com.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.DateBundle;
import com.http.HttpUtil;
import com.http.ImgLoader;
import com.service.CoreService;
import com.staticdata.StaticInt;
import com.staticdata.StaticList;
import com.staticdata.StaticString;
import com.utilt.EmojiFilter;
import com.utilt.utils;
import com.view.InclineTextView;


public class InActivityComment extends Activity {

	private DataAdapter mAdapter;
	private ListView mListView;
	private List<String> list=new ArrayList<String>();
	private String activityid;
	private String title, img, clas, time,from;
	private Date deadline;
	private ProgressDialog progressDialog;
	
	private EditText input;
	
	private String tmp = "";
    // 是否重置了EditText的内容
    private boolean resetText;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_in_activity_comment);		
		intidata();		
		mListView = (ListView) findViewById(R.id.incomment_listVi);
		mAdapter = new DataAdapter(this);
		
		getNetData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.in_activity_comment, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			if(from.equals("05")){
				startActivity(new Intent(InActivityComment.this, InActivity.class).putExtra("CurrentId", activityid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				return false;
			}
					
			if(from.equals("06")){
				startActivity(new Intent(InActivityComment.this, InActivityDetail.class).putExtra("id", activityid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				return false;
			}
			
			if(from.equals("034")){
				startActivity(new Intent(InActivityComment.this, MyActivity.class).putExtra("CurrentId", activityid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				return false;
			}
			
			if(from.equals("035")){
				startActivity(new Intent(InActivityComment.this, MyActivityDetail.class).putExtra("activityid", activityid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				return false;
			}
			if(from.equals("024")){
				Intent intents=getIntent();
				String oganizationid=intents.getStringExtra("oganizationid");
				startActivity(new Intent(InActivityComment.this, ManagerActivity.class).putExtra("CurrentId", activityid).putExtra("oganizationid", oganizationid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				return false;
			}
			if(from.equals("025")){
				Intent intents=getIntent();
				String oganizationid=intents.getStringExtra("oganizationid");
				startActivity(new Intent(InActivityComment.this, ManagerActivityDetail.class).putExtra("activityid", activityid).putExtra("oganizationid", oganizationid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				return false;
			}
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
		
		Bundle bundle = intent.getExtras();
		DateBundle serializableMap = (DateBundle) bundle.get("map");
		Map<String, Object> map = serializableMap.getMap();
		
		activityid=map.get("activityid").toString();
		from=map.get("from").toString();		
		title = map.get("title").toString();
		img = map.get("img").toString();
		clas =map.get("clas").toString();
		deadline =(Date)map.get("deadline");
		time = map.get("time").toString();
		

		input=(EditText)findViewById(R.id.incomment_input);
		input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int arg2, int arg3) {
                if (!resetText) {
                    if ((arg3 == 2) && (!EmojiFilter.containsEmoji(s.toString().substring(start, start + 2)))) {
                        resetText = true;
                        input.setText(tmp);
                        input.invalidate();
                        if (input.getText().length() > 1){
                        	Selection.setSelection(input.getText(), input.getText().length());
                        }
                        Toast.makeText(getApplicationContext(), "不支持表情输入",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    resetText = false;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (!resetText)
                    tmp = arg0.toString();

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });
		
	}
	
	public void getNetData(){
		progressDialog = ProgressDialog.show(InActivityComment.this, "","正在获取评论信息,请稍候！");
		
		new Thread() {
			public void run() {
				String httpjson = HttpUtil.sendGet(StaticString.server+"getcomment","activityid="+activityid+"&activitytype=0");
				Message msg_listData = new Message();
				if(httpjson.equals("error")){
					msg_listData.what=0;
				}else{
					try {
						JSONArray array=new JSONArray(httpjson);
						int size=array.length();
						
						for(int i=0;i<size;i++){
							list.add(array.getJSONObject(i).getString("comment"));
						}
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
            	
//            	mAdapter.notifyDataSetChanged();
            	break;
            }
		}
	};
	
	public void btonclick(View v){
		int vid=v.getId();
		if(vid==R.id.incomment_back){
			if(from.equals("05")){
				startActivity(new Intent(InActivityComment.this, InActivity.class).putExtra("CurrentId", activityid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				return ;
			}
					
			if(from.equals("06")){
				startActivity(new Intent(InActivityComment.this, InActivityDetail.class).putExtra("id", activityid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				return ;
			}
			
			if(from.equals("034")){
				startActivity(new Intent(InActivityComment.this, MyActivity.class).putExtra("CurrentId", activityid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				return ;
			}
			
			if(from.equals("035")){
				startActivity(new Intent(InActivityComment.this, MyActivityDetail.class).putExtra("activityid", activityid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				return ;
			}
			if(from.equals("024")){
				Intent intents=getIntent();
				String oganizationid=intents.getStringExtra("oganizationid");
				startActivity(new Intent(InActivityComment.this, ManagerActivity.class).putExtra("CurrentId", activityid).putExtra("oganizationid", oganizationid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				return ;
			}
			if(from.equals("025")){
				Intent intents=getIntent();
				String oganizationid=intents.getStringExtra("oganizationid");
				startActivity(new Intent(InActivityComment.this, ManagerActivityDetail.class).putExtra("activityid", activityid).putExtra("oganizationid", oganizationid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				return ;
			}
		}
		

		
		if(vid==R.id.incomment_upload){

			String str=input.getText().toString();			
			if(str.length()==0){
				Toast.makeText(this, "请输入内容哦～～", Toast.LENGTH_SHORT).show();	
				return ;
			}
			list.add(str);
			mAdapter.notifyDataSetChanged();
			input.setText("");
			Toast.makeText(this, "发表成功", Toast.LENGTH_SHORT).show();	
			Intent intent=new Intent(InActivityComment.this,CoreService.class);
			intent.putExtra("tag", StaticInt.addcomment) ;
			intent.putExtra("comment", str);
			intent.putExtra("activityid", activityid) ;
			intent.putExtra("activitytype", 0) ;
			startService(intent);
			for(int i=0;i<StaticList.InActicitylist.size();i++){
				if(activityid.equals(StaticList.InActicitylist.get(i).id)){
					StaticList.InActicitylist.get(i).commentnum++;
					break;
				}
			}
			for(int i=0;i<StaticList.MyActicitylist.size();i++){
				if(activityid.equals(StaticList.MyActicitylist.get(i).id)){
					StaticList.MyActicitylist.get(i).commentnum++;
					break;
				}
			}
			for(int i=0;i<StaticList.Managelist.size();i++){
				if(activityid.equals(StaticList.Managelist.get(i).id)){
					StaticList.Managelist.get(i).commentnum++;
					break;
				}
			}			
			
		}
	}
	
	private class DataAdapter extends BaseAdapter {
		@SuppressWarnings("unused")
		private Context ctx;
		private LayoutInflater inflater;
		private ImgLoader imgloader;
		private ViewCache cache;
		public DataAdapter(Context ctx) {
			this.ctx = ctx;
			inflater = LayoutInflater.from(ctx);
			this.imgloader = new ImgLoader(ctx);
		}

		@Override
		public int getCount() {
			
			return list.size()+1;
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
		public int getItemViewType(int position) {

			return position == 0 ? 0 : 1;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}
		
		@SuppressWarnings("deprecation")
		@SuppressLint("SimpleDateFormat")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			int positiontype = getItemViewType(position);
			
			if(convertView==null){
				switch (positiontype) {
				case 0:
					convertView = inflater.inflate(R.layout.incomment_head,null);
					TextView item_title = (TextView) convertView.findViewById(R.id.incomment_head_title);
					ImageView item_img = (ImageView) convertView.findViewById(R.id.incomment_head_img);
					InclineTextView team_item_class = (InclineTextView) convertView.findViewById(R.id.incomment_head_class);
					TextView item_deadline = (TextView) convertView.findViewById(R.id.incomment_head_deadline);
					TextView item_time = (TextView) convertView.findViewById(R.id.incomment_head_time);
					item_title.setText(title);
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
							item_deadline.setText("报名截止："+format.format(deadline)+"(还未截止)");				
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
					
					return convertView;				
				case 1:
					convertView = inflater.inflate(R.layout.incomment_item,null);// 生成条目界面对象
					cache = new ViewCache();
					cache.itemtext=(TextView) convertView.findViewById(R.id.incomment_text);
					cache.itemlou=(TextView) convertView.findViewById(R.id.incomment_lou);
					cache.icon=(ImageView) convertView.findViewById(R.id.incomment_headicon);
					convertView.setTag(cache);
				}
			}else
			{
				switch(positiontype){
				case 0:
					return convertView;
				case 1:
					cache = new ViewCache();
					cache = (ViewCache) convertView.getTag();
					break;
				}
			}

			cache.itemtext.setText(list.get(position-1));
			cache.itemlou.setText(position+"楼童鞋");
			cache.icon.setImageResource(utils.getCommentIcon(position));

			return convertView;
		}
		private final class ViewCache {
			public TextView itemtext;
			public ImageView icon;
			public TextView itemlou;
		}
	}
}
