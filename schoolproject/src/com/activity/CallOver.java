package com.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.http.HttpUtil;
import com.staticdata.StaticString;

public class CallOver extends Activity {

	private String activityid,orgnization, contenthtml,attachmentname,oganizationid,title;
	private TextView callover_title;
	private CheckBox callover_use,callover_check;
	private Spinner callover_hour,callover_minu;
	private RelativeLayout rlv;
	private boolean use=false,lbs=true;
	private int hour=0,minute=0;
	private double x=0.0,y=0.0;
	private ProgressDialog progressDialog; 
	
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_over);
		
		intiData();
		intiView();
		intiViewData();
		LBS();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.call_over, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(CallOver.this,ManagerActivityDetail.class);
			intent.putExtra("orgnization", orgnization);
			intent.putExtra("contenthtml",contenthtml);
			intent.putExtra("attachmentname",attachmentname);
			intent.putExtra("activityid",activityid);
			intent.putExtra("oganizationid",oganizationid);
			intent.putExtra("callover", 0);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			Toast.makeText(getApplicationContext(), "1111", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Toast.makeText(getApplicationContext(), "2222",Toast.LENGTH_SHORT).show();
			return false;
		}
		return false;

	}
	
	public void intiView(){
		callover_title=(TextView)findViewById(R.id.callover_title);
		callover_use=(CheckBox)findViewById(R.id.callover_usel);
		callover_check=(CheckBox)findViewById(R.id.callover_checkl);
		callover_hour=(Spinner)findViewById(R.id.callover_hour);
		callover_minu=(Spinner)findViewById(R.id.callover_minu);
		rlv=(RelativeLayout)findViewById(R.id.callover_set);
		
	}
	
	public void intiData(){
		Intent intent = getIntent();
		activityid = intent.getStringExtra("activityid");
		oganizationid=intent.getStringExtra("oganizationid");
		orgnization=intent.getStringExtra("orgnization");
		contenthtml = intent.getStringExtra("contenthtml");
		attachmentname =intent.getStringExtra("attachmentname");
		title=intent.getStringExtra("title");
	}
	
	public void intiViewData(){
		callover_title.setText(title);
		callover_use.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				use=arg1;
				if(arg1==true){
					rlv.setVisibility(View.GONE);
				}else{
					rlv.setVisibility(View.VISIBLE);
				}
			}
		});
		callover_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				lbs=arg1;
			}
		});
		callover_hour.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				
				hour=Integer.parseInt(CallOver.this.getResources().getStringArray(R.array.caloverH)[arg2]);

			}
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		callover_minu.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				
				minute =Integer.parseInt(CallOver.this.getResources().getStringArray(R.array.caloverM)[arg2]);

			}
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	public void btonclick(View view){
		int id=view.getId();
		if(id==R.id.callover_back){
			Intent intent=new Intent(CallOver.this,ManagerActivityDetail.class);
			intent.putExtra("orgnization", orgnization);
			intent.putExtra("contenthtml",contenthtml);
			intent.putExtra("attachmentname",attachmentname);
			intent.putExtra("activityid",activityid);
			intent.putExtra("oganizationid",oganizationid);
			intent.putExtra("callover", 0);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}
		if(id==R.id.callover_finish){
			if(use==true){
				hour=0;
				minute=0;
			}

			if(lbs==false){
				if(x==0.0||y==0.0){
					mLocationClient.stop();	
				}
				x=0.0;
				y=0.0;
			}else{
				if(x==0.0||y==0.0){
					Toast.makeText(getApplicationContext(), "定位失败，请重试，或者不要选择基于地理位置定位的方式", Toast.LENGTH_SHORT).show();
					return ;
				}
			}
			
			progressDialog = ProgressDialog.show(CallOver.this, "", "正在发起点名，请稍后～,请稍候！");
			
			new Thread(){
				public void run(){
					String httpjson=HttpUtil.sendGet(StaticString.server+"callover", "activityid="+activityid+"&hour="+hour+"&minute="+minute+"&localx="+x+"&localy="+y);
					Message msg_listData = new Message();
					if(httpjson.equals("ok")){
						msg_listData.what =1;
					}else{
						if(httpjson.equals("wrong")){
							msg_listData.what =2;
						}else{
							msg_listData.what =0;
						}					
					}
					handler.sendMessage(msg_listData);
				}
			}.start();
			
			return ;
		}
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
            	Toast.makeText(getApplicationContext(), "点名行为发起成功～", Toast.LENGTH_SHORT).show();  
            	Intent intent=new Intent(CallOver.this,ManagerActivityDetail.class);
    			intent.putExtra("orgnization", orgnization);
    			intent.putExtra("contenthtml",contenthtml);
    			intent.putExtra("attachmentname",attachmentname);
    			intent.putExtra("activityid",activityid);
    			intent.putExtra("oganizationid",oganizationid);
    			intent.putExtra("callover", 0);
    			startActivity(intent);
    			finish();
    			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            	break;
            case 2:
            	progressDialog.dismiss(); //关闭进度条
            	Toast.makeText(getApplicationContext(), "亲，该活动已经点过名了～", Toast.LENGTH_SHORT).show();  
            	break;
            }
		}
	};
	
	public void LBS(){
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		initLocation();
		mLocationClient.start();
	}
	
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 1000;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);
		mLocationClient.setLocOption(option);
	}
	
	public class MyLocationListener implements BDLocationListener {
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			double xx=location.getLongitude();
			double yy=location.getLatitude();
			if(xx!=0.0&&yy!=0.0){
				x=xx;
				y=yy;
				mLocationClient.stop();	
			}
		}
	}
}
