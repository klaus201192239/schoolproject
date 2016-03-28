package com.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;
import com.dbutil.DBHelper;
import com.http.HttpUtil;
import com.pagebean.InActivityBean;
import com.service.CoreService;
import com.service.ListenerService;
import com.staticdata.StaticBoolean;
import com.staticdata.StaticInt;
import com.staticdata.StaticList;
import com.staticdata.StaticString;

public class LoaderData extends Activity {
	
	private String schoolid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loader_data);
		if(StaticBoolean.NetLink==false){
			getLocalData();
			Toast.makeText(this, "您的网络连接不正常～", Toast.LENGTH_SHORT).show();	
			pagejump();
		}else{
			getNetData();
			
			Intent intt=new Intent(LoaderData.this,ListenerService.class);
			startService(intt);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.loader_data, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Toast.makeText(getApplicationContext(), "正在加载数据，请稍后", Toast.LENGTH_SHORT).show();  
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			Toast.makeText(getApplicationContext(), "1111", Toast.LENGTH_SHORT).show();  
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Toast.makeText(getApplicationContext(), "2222222", Toast.LENGTH_SHORT).show();  
			return false;
		}
		return false;

	}
	
	public void getLocalData(){
		
		StaticList.InActicitylist.clear();
		StaticList.InActicitylistb.clear();
		
		DBHelper dbhelper=new DBHelper(this);
		dbhelper.CreatOrOpen("schooltime");
		
		Cursor cur=dbhelper.selectInfo("select * from inactivity order by id desc;");
		while (cur.moveToNext()) {
			InActivityBean bean=new InActivityBean();
			bean.id=cur.getString(0);//获取第一列的值,第一列的索引从0开始
			bean.title=cur.getString(1);
			bean.imgurl=cur.getString(2);
			bean.category=cur.getString(3);
			SimpleDateFormat sfStart = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",java.util.Locale.ENGLISH) ;		
			try {
				bean.deadline=sfStart.parse(cur.getString(4));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bean.time=cur.getString(5);
			bean.pridenum=cur.getInt(6);
			bean.opposenum=cur.getInt(7);
			bean.commentnum=cur.getInt(8);
			StaticList.InActicitylist.add(bean);
		}
		dbhelper.closeDB();
	}
	
	public void getNetData(){
		
		StaticList.InActicitylist.clear();
		SharedPreferences settings = getSharedPreferences("schooltime",MODE_PRIVATE);
		schoolid = settings.getString("ShoolId","");
		new Thread(){
			public void run(){
				String httpjson=HttpUtil.sendGet(StaticString.server+"getinactivity","schoolid="+schoolid+"&currentid=0");
				Message msg_listData = new Message();
				if(httpjson.equals("error")){
					msg_listData.what=0;
				}else{
					if(httpjson.equals("nothing")){
						msg_listData.what=1;
					}
					else{
						try {
							msg_listData.what=2;
							JSONArray jsonarry=new JSONArray(httpjson);
							int size=jsonarry.length();
							for(int i=0;i<size;i++){
								JSONObject cur=jsonarry.getJSONObject(i);
								InActivityBean bean=new InActivityBean();
								bean.id=cur.getString("id");//获取第一列的值,第一列的索引从0开始
								bean.title=cur.getString("title");
								bean.imgurl=cur.getString("imgurl");
								bean.category=cur.getString("category");
								SimpleDateFormat sfStart = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",java.util.Locale.ENGLISH) ;		
								try {
									bean.deadline=sfStart.parse(cur.getString("deadline"));
								} catch (ParseException e) {
									
								}
								bean.time=cur.getString("time");
								bean.pridenum=cur.getInt("pridenum");
								bean.opposenum=cur.getInt("opposenum");
								bean.commentnum=cur.getInt("commentnum");
								bean.onlyteam=cur.getInt("onlyteam");
								StaticList.InActicitylist.add(bean);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				handler.sendMessage(msg_listData);
			}
		}.start();
	}
	
	public void pagejump(){
		Intent intent=new Intent(LoaderData.this,InActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
	}
	
	private Handler handler= new Handler(new Handler.Callback() {

        public boolean handleMessage(Message message) {
            switch (message.what) {
            case 0:
            	Toast.makeText(getApplicationContext(), "网络连接或其他意外错误", Toast.LENGTH_SHORT).show();  
            	pagejump();
            	break;
            case 1:
            	Toast.makeText(getApplicationContext(), "这个学校真是空空如也", Toast.LENGTH_SHORT).show();  
            	pagejump();
            	break;
            case 2:
            	Intent intent=new Intent(LoaderData.this,CoreService.class);
            	intent.putExtra("tag", StaticInt.downinactivity);
            	startService(intent);
            	pagejump();
            	break;
            }
        return false;
      }
	});
}
