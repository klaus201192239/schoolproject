package com.activity;

import org.json.JSONArray;
import org.json.JSONObject;
import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.dbutil.DBHelper;
import com.http.HttpUtil;
import com.pagebean.SignInBean;
import com.service.CoreService;
import com.staticdata.StaticBoolean;
import com.staticdata.StaticInt;
import com.staticdata.StaticList;
import com.staticdata.StaticString;
import com.utilt.utils;

public class SignIn extends Activity implements IXListViewListener {

	private Intent intent;
	private String activityid,userid;
	private String title;
	private XListView mListView;
	private MyAdapter mAdapter;
	private Handler mHandler;
	private ProgressDialog progressDialog; 
	private double x=0.0,y=0.0;
	
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	private int onfreshTag=0;
	private int loadMoreTag=0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		
		intiData() ;
	
		if(StaticBoolean.NetLink==false){
			Toast.makeText(getApplicationContext(), "�������Ӳ�����", Toast.LENGTH_SHORT).show();
			intiView() ;
		}else{
			
			getNetData();
			
			intiView() ;
			
			LBS();

		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sign_in, menu);
		return true;
	}
	
	public void btonclik(View v){
		int vid=v.getId();
		if(vid==R.id.signin_back){
			Intent inte=new Intent(SignIn.this,MyActivityDetail.class);
			inte.putExtra("activityid", activityid);
			startActivity(inte);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}
		if(vid==R.id.signin_attent){

			int tag=0;
			
			DBHelper dbhelper=new DBHelper(this);  
			dbhelper.CreatOrOpen("schooltime");
			Cursor cursor = dbhelper.selectInfo("select * from signin where activityid='"+ activityid+ "';");
			if (cursor.moveToNext()) {
				tag=1;
	
			}
			dbhelper.closeDB();
			if(tag==1){
				Toast.makeText(getApplicationContext(), "���Ѿ�ǩ������Ŷ��",Toast.LENGTH_SHORT).show();
				return ;
			}

			if(x==0.0||y==0.0){
				mLocationClient.stop();	
				Toast.makeText(getApplicationContext(), "��λʧ��,���Զ�ѡ��ǻ��ڵ���λ�ö�λ�ķ�ʽ", Toast.LENGTH_SHORT).show();
			}
			
			progressDialog = ProgressDialog.show(SignIn.this, "", "����ǩ��,���Ժ�");
			
			new Thread(){
				public void run(){
					String httpjson=HttpUtil.sendGet(StaticString.server+"mysignin","activityid="+activityid+"&userid="+userid+"&localx="+x+"&localy="+y);
					Message msg_listData = new Message();
					if(httpjson.equals("ok")){
						msg_listData.what=2;//ǩ���ɹ�
					}else{
						if(httpjson.equals("wrong")){
							msg_listData.what=3;//δ����ǩ��
						}else{
							if(httpjson.equals("long")){
								msg_listData.what=4;//error ����ʧ��
							}else{
								if(httpjson.equals("timeover")){
									msg_listData.what=5;//ǩ��ʱ���Ѿ���ȥ��
								}else{
									msg_listData.what=0;//error ����ʧ��
								}
							}
						}
					}
					handler.sendMessage(msg_listData);
				}
			}.start();
			return ;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent inte=new Intent(SignIn.this,MyActivityDetail.class);
			inte.putExtra("activityid", activityid);
			startActivity(inte);
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

	public void getNetData(){
		
		StaticList.SignInlist.clear();
		
		progressDialog = ProgressDialog.show(SignIn.this, "", "���ڻ�ȡ����,���Ժ�");
		new Thread(){
			public void run(){
				
				String httpjson=HttpUtil.sendGet(StaticString.server+"getsigninlist","activityid="+activityid+"&currentstuid=0");
				
				Message msg_listData = new Message();
				
				if(httpjson.equals("error")){
					msg_listData.what=0;
				}else{
					msg_listData.what=1;
					try {
						JSONArray array=new JSONArray(httpjson);
						int len=array.length();
						for(int i=0;i<len;i++){
							SignInBean bean=new SignInBean();
							JSONObject obj=array.getJSONObject(i);
							bean.degree=obj.getInt("degree");
							bean.grade=obj.getInt("grade");
							bean.id=obj.getString("id");
							bean.major=obj.getString("major");
							bean.name=obj.getString("name");
							bean.studentid=obj.getString("studentid");
							bean.state=obj.getInt("state");
							StaticList.SignInlist.add(bean);
						}
						
					} catch (Exception e) {
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
            	progressDialog.dismiss(); //�رս�����
            	
            	intiView();
            	
            	Toast.makeText(getApplicationContext(), "�������ӻ������������", Toast.LENGTH_SHORT).show();  
            	break;
            case 1:
            	progressDialog.dismiss(); //�رս�����
            	
            	intiView();
            	
            	//mAdapter.notifyDataSetChanged();
            	loadNext();           	
            	break;
            case 2:
            	progressDialog.dismiss(); //�رս�����
            	
            	DBHelper dbhelper=new DBHelper(SignIn.this); 
            	dbhelper.CreatOrOpen("schooltime");
				dbhelper.excuteInfo("insert into signin values('"+ activityid + "');");
				dbhelper.closeDB();
            	
            	Toast.makeText(getApplicationContext(), "ǩ���ɹ���ˢ���б��ɲ鿴��", Toast.LENGTH_SHORT).show();        	
            	break;
            case 3:
            	progressDialog.dismiss(); //�رս�����
            	Toast.makeText(getApplicationContext(), "��֯�߻�δ����ǩ������", Toast.LENGTH_SHORT).show();       	
            	break;
            case 4:
            	progressDialog.dismiss(); //�رս�����
            	Toast.makeText(getApplicationContext(), "�����뷢��ǩ���ĵص��Զ��δ�ܳɹ�ǩ��", Toast.LENGTH_SHORT).show();       	
            	break;
            case 5:
            	progressDialog.dismiss(); //�رս�����
            	Toast.makeText(getApplicationContext(), "ǩ��ʱ���Ѿ���ֹ��δ�ܳɹ�ǩ��", Toast.LENGTH_SHORT).show();       	
            	break;
            }
            
		}
	};
	
	public void intiData() {
		intent = getIntent();
		activityid=intent.getStringExtra("activityid");
		title=intent.getStringExtra("title");
		SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
		userid=set.getString("Id","");
	}

	public void intiView() {
		mHandler = new Handler();
		mListView = (XListView) findViewById(R.id.signin_xListView);
		mListView.setPullLoadEnable(true);
		mAdapter = new MyAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(this);
	}

	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime("");
	}

	// ����ˢ��
	@Override
	public void onRefresh() {
		
		
		if(onfreshTag==1){
			onLoad();
			return ;
					
		}
		
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				
				onfreshTag=1;
				
				if (StaticBoolean.NetLink == true) {
					int x = geneItems(1);
					if (x == 0) {
						onLoad();
						Toast.makeText(getApplicationContext(), "�������Ӳ�����",Toast.LENGTH_SHORT).show();
					} else {
						mAdapter.notifyDataSetChanged();
						onLoad();
					}
				} else {
					onLoad();
					Toast.makeText(getApplicationContext(), "�������Ӳ�����",Toast.LENGTH_SHORT).show();
				}
				
				onfreshTag=0;
				
			}
		}, 2000);
	}

	// ���ظ���
	@Override
	public void onLoadMore() {
		
		if(loadMoreTag==1){
			onLoad();
			return ;
		}
		
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				
				loadMoreTag=1;
				
				if (StaticBoolean.NetLink == true) {

					int x = geneItems(0);
					if (x == 0) {
						onLoad();
						Toast.makeText(getApplicationContext(), "�������Ӳ�����",Toast.LENGTH_SHORT).show();
					} else {
						mAdapter.notifyDataSetChanged();
						onLoad();
					}
				} else {
					onLoad();
					Toast.makeText(getApplicationContext(), "�������Ӳ�����",Toast.LENGTH_SHORT).show();
				}
				
				loadMoreTag=0;
				
			}
		}, 2000);
	}

	private int geneItems(int type) {
		
		if (type == 1) {
			
			if (StaticBoolean.NetLink == false) {
				return 0;
			}
			
			Thread onFresh = new Thread() {
				public void run() {
					
					String httpjson=HttpUtil.sendGet(StaticString.server+"getsigninlist","activityid="+activityid+"&currentstuid=0");
										
					if(httpjson.equals("error")){

					}else{
						try {
							
							StaticList.SignInlist.clear();
							
							JSONArray array=new JSONArray(httpjson);
							int len=array.length();
							for(int i=0;i<len;i++){
								SignInBean bean=new SignInBean();
								JSONObject obj=array.getJSONObject(i);
								bean.degree=obj.getInt("degree");
								bean.grade=obj.getInt("grade");
								bean.id=obj.getString("id");
								bean.major=obj.getString("major");
								bean.name=obj.getString("name");
								bean.studentid=obj.getString("studentid");
								bean.state=obj.getInt("state");
								StaticList.SignInlist.add(bean);
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			};
			
			
			onFresh.start();
			try {
				onFresh.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			StaticList.SignInlistb.clear();
			loadNext();
			
			return 1;
			
		} else {
						
			if (StaticBoolean.NetLink == false) {
				return 0;
			}
			int x = StaticList.SignInlistb.size();

			if (x != 0) {

				int y = StaticList.SignInlist.size();
				
				for (int i = 0; i < x; i++) {
					
					if (StaticList.SignInlistb.get(i).studentid.compareTo( StaticList.SignInlist.get(y - 1 + i).studentid)<0) {
						StaticList.SignInlist.add(StaticList.SignInlistb.get(i));
					}
				}
				
				StaticList.SignInlistb.clear();
				loadNext();
				
				return 1;
			} else {

				if(StaticList.SignInlist.size()>0){
					Thread onLoadMore=new Thread(){
						public void run(){
							
							String httpjson=HttpUtil.sendGet(StaticString.server+"getsigninlist","activityid="+activityid+"&currentstuid="+StaticList.SignInlist.get(StaticList.SignInlist.size() - 1).studentid);
							
							if(httpjson.equals("error")){

							}else{
								try {
									JSONArray array=new JSONArray(httpjson);
									int len=array.length();
									for(int i=0;i<len;i++){
										SignInBean bean=new SignInBean();
										JSONObject obj=array.getJSONObject(i);
										bean.degree=obj.getInt("degree");
										bean.grade=obj.getInt("grade");
										bean.id=obj.getString("id");
										bean.major=obj.getString("major");
										bean.name=obj.getString("name");
										bean.studentid=obj.getString("studentid");
										bean.state=obj.getInt("state");
										StaticList.SignInlist.add(bean);
									}
									
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					};
					
					onLoadMore.start();
					try {
						onLoadMore.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					StaticList.SignInlistb.clear();
					loadNext();
					
					return 1;
				}
				return 0;
			}
		}
	}

	public void loadNext() {

		Intent intent = new Intent(SignIn.this, CoreService.class);
		intent.putExtra("tag", StaticInt.downsignin);
		intent.putExtra("activityid", activityid);
		startService(intent);
	}
	
	public class MyAdapter extends BaseAdapter {
		
		private LayoutInflater inflater;
		private ViewCache cache;
				
		public MyAdapter(Context context) {
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return StaticList.SignInlist.size()+1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public int getItemViewType(int position) {
			return position == 0 ? 0 : 1;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if (convertView == null) {
				switch (type) {
				case 0:
					convertView = inflater.inflate(R.layout.signin_head,null);// ������Ŀ�������
					((TextView)convertView.findViewById(R.id.signin_head_title)).setText(title);
					return convertView;
				case 1:
					convertView = inflater.inflate(R.layout.signin_item,null);// ������Ŀ�������
					cache = new ViewCache();
					cache.item_text= (TextView) convertView.findViewById(R.id.signin_item_text);
					cache.icon= (ImageView) convertView.findViewById(R.id.signin_item_headicon);
					cache.itemmajor= (TextView) convertView.findViewById(R.id.signin_text_major);
					convertView.setTag(cache);
					break;
				}
			} else {
				switch (type) {
				case 0:
					return convertView;
				case 1:
					cache = new ViewCache();
					cache = (ViewCache) convertView.getTag();
					break;
				}
			}
			
			SignInBean bean=StaticList.SignInlist.get(position-1);
			if(bean.state==0){
				cache.item_text.setText(bean.name+" "+bean.studentid);
			}else{
				cache.item_text.setText(bean.name+" "+bean.studentid);
			}
			
			cache.itemmajor.setText(bean.major);
			
			cache.icon.setImageResource(utils.getSignInIcon(bean.state));
			
			return convertView;
		}
		
		private final class ViewCache {
			public TextView item_text;
			public ImageView icon;
			public TextView itemmajor;
		}
	}
	
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
				
				System.out.println("X:"+x);
				System.out.println("Y:"+y);

				mLocationClient.stop();	
			}
		}
	}
	
	
	public double Distance(double long1, double lat1, double long2,  double lat2) {  
	    double a, b, R;  
	    R = 6378137; // ����뾶  
	    lat1 = lat1 * Math.PI / 180.0;  
	    lat2 = lat2 * Math.PI / 180.0;  
	    a = lat1 - lat2;  
	    b = (long1 - long2) * Math.PI / 180.0;  
	    double d;  
	    double sa2, sb2;  
	    sa2 = Math.sin(a / 2.0);  
	    sb2 = Math.sin(b / 2.0);  
	    d = 2  
	            * R  
	            * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)  
	                    * Math.cos(lat2) * sb2 * sb2));  
	    return d;  
	}  
}
