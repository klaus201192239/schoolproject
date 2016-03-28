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
					Toast.makeText(getApplicationContext(), "��λʧ�ܣ������ԣ����߲�Ҫѡ����ڵ���λ�ö�λ�ķ�ʽ", Toast.LENGTH_SHORT).show();
					return ;
				}
			}
			
			progressDialog = ProgressDialog.show(CallOver.this, "", "���ڷ�����������Ժ�,���Ժ�");
			
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
            	progressDialog.dismiss(); //�رս�����
            	Toast.makeText(getApplicationContext(), "�������ӻ������������", Toast.LENGTH_SHORT).show();  
            	break;
            case 1:
            	progressDialog.dismiss(); //�رս�����
            	Toast.makeText(getApplicationContext(), "������Ϊ����ɹ���", Toast.LENGTH_SHORT).show();  
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
            	progressDialog.dismiss(); //�رս�����
            	Toast.makeText(getApplicationContext(), "�ף��û�Ѿ�������ˡ�", Toast.LENGTH_SHORT).show();  
            	break;
            }
		}
	};
	
	public void LBS(){
		mLocationClient = new LocationClient(getApplicationContext()); // ����LocationClient��
		mLocationClient.registerLocationListener(myListener); // ע���������
		initLocation();
		mLocationClient.start();
	}
	
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// ��ѡ��Ĭ�ϸ߾��ȣ����ö�λģʽ���߾��ȣ��͹��ģ����豸
		option.setCoorType("bd09ll");// ��ѡ��Ĭ��gcj02�����÷��صĶ�λ�������ϵ
		int span = 1000;
		option.setScanSpan(span);// ��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
		option.setIsNeedAddress(true);// ��ѡ�������Ƿ���Ҫ��ַ��Ϣ��Ĭ�ϲ���Ҫ
		option.setOpenGps(true);// ��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
		option.setLocationNotify(true);// ��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
		option.setIsNeedLocationDescribe(true);// ��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
		option.setIsNeedLocationPoiList(true);// ��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
		option.setIgnoreKillProcess(false);// ��ѡ��Ĭ��false����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ��ɱ��
		option.SetIgnoreCacheException(false);// ��ѡ��Ĭ��false�������Ƿ��ռ�CRASH��Ϣ��Ĭ���ռ�
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
