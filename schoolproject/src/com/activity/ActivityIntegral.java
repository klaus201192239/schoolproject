package com.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.http.HttpUtil;
import com.pagebean.IntegralDetail;
import com.pagebean.objBean;
import com.staticdata.StaticString;

public class ActivityIntegral extends Activity {

	private int year;
	private ListView mListView1;
	private ListView mListView2;
	
	private TextView titleT,sumT;
	private Spinner spinner;
	private String obj[],currentClass="0";

	private List<objBean> listClass = new ArrayList<objBean>();	
	private List<IntegralDetail> listDetail = new ArrayList<IntegralDetail>();
	private List<IntegralDetail> listDetailSum = new ArrayList<IntegralDetail>();
	
	private  ClassAdapter classAdapter;
	private DetailAdapter detailAdapter;
	
	private ProgressDialog progressDialog; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_integral);

		year=Calendar.getInstance().get(Calendar.YEAR);
		int mon=Calendar.getInstance().get(Calendar.MONTH)+1;
		if(mon<9){
			year--;
		}
	
		intiView();		
		intiData();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_integral, menu);
		return true;
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(ActivityIntegral.this,UserCenter.class);
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
			Toast.makeText(getApplicationContext(), "2222222", Toast.LENGTH_SHORT).show();  
			return false;
		}
		return false;

	}
	
	public void to_back(View view){
		Intent intent=new Intent(ActivityIntegral.this,UserCenter.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
	}
	
	public void intiView(){
		titleT =(TextView)findViewById(R.id.text_title);
		sumT =(TextView)findViewById(R.id.integal_sum);
		spinner=(Spinner)findViewById(R.id.activityinte_year);
		
		Calendar ca=Calendar.getInstance();
		
		int y=ca.get(Calendar.YEAR);
		int m=ca.get(Calendar.MONTH)+1;
		
		if(m>8){

			y++;
		}

		obj= new String[y-2010];
		int j=0;
		for(int i=y;i>2010;i--){
			obj[j]=(i-1)+"~"+i+"学年";
			j++;
		}

		
		ArrayAdapter<String> ada = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, obj);
		ada.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		spinner.setAdapter(ada);
		spinner.setSelection(0,true);
				
		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				
				String yy=arg0.getItemAtPosition(arg2).toString();
				year=Integer.parseInt(yy.substring(0, 4));
				intiData();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		
		mListView1 = (ListView) findViewById(R.id.activityinte_list1);
		mListView2 = (ListView) findViewById(R.id.activityinte_list2);
		
		classAdapter=new ClassAdapter(this);
		detailAdapter=new DetailAdapter(this);

		mListView1.setAdapter(classAdapter);
		mListView2.setAdapter(detailAdapter);
	}
	
	public void intiData(){

		progressDialog = ProgressDialog.show(ActivityIntegral.this, "", "正在获取数据,请稍候！");
		
		new Thread(){
			public void run(){
				
				SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
				String userid=set.getString("Id","");
				String schoolid=set.getString("ShoolId","");
				String idcard=set.getString("StudentId","");
				
				String httpjson=HttpUtil.sendGet(StaticString.server+"getActivityIntegral", "userid="+userid+"&year="+year+"&schoolid="+schoolid+"&idcard="+idcard);
				Message msg_listData = new Message();
				if(httpjson.equals("error")){
					msg_listData.what=0;
				}else{
					try{
						listDetailSum.clear();
						listClass.clear();
						JSONArray array=new  JSONArray(httpjson);
						int len=array.length();
						for(int i=0;i<len;i++){
							JSONObject obj=array.getJSONObject(i);
							objBean bean=new objBean();							
							bean.id=obj.getString("id");
							bean.name=obj.getString("name");
							JSONArray arrayB=obj.getJSONArray("detail");
							int lenB=arrayB.length();
							for(int j=0;j<lenB;j++){
								JSONObject objB=arrayB.getJSONObject(j);
								IntegralDetail detail=new IntegralDetail();
								detail.Classid=bean.id;
								detail.Grade= Double.parseDouble(objB.getString("grade"));
								detail.Level=objB.getString("level");
								detail.name=objB.getString("name");
								detail.Scope=objB.getString("scope");
								listDetailSum.add(detail);
							}						
							listClass.add(bean);
						}
						msg_listData.what=1;
					}catch(Exception e){
						msg_listData.what=0;
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
            	refreshView("0");
            	break;
            }
		}
	};
	
	public void refreshView(String classid){
		double sumInte=0.0;
		
		if(classid.equals("0")){
			listDetail.clear();
			int len=listDetailSum.size();
        	for(int i=0;i<len;i++){
        		listDetail.add(listDetailSum.get(i));
        		sumInte+=listDetailSum.get(i).Grade;
        	}
		}else{
			listDetail.clear();
			int le=listDetailSum.size();
        	for(int i=0;i<le;i++){
        		if(listDetailSum.get(i).Classid.equals(classid)){
        			listDetail.add(listDetailSum.get(i));
        			sumInte+=listDetailSum.get(i).Grade;
        		}
        	}
		}
		sumT.setText("积分总计："+sumInte);
		if(classid.equals("0")){
			titleT.setText("所有类别");
		}else{			
			for(int i=0;i<listClass.size();i++){
				if(listClass.get(i).id.equals(classid)){
					titleT.setText(listClass.get(i).name+"积分");
					break;
				}
			}
		}
		classAdapter.notifyDataSetChanged();
    	detailAdapter.notifyDataSetChanged();
	}

	private class ClassAdapter extends BaseAdapter {


		private LayoutInflater inflater;
		private ViewCache cache;

		public ClassAdapter(Context ctx) {

			inflater = LayoutInflater.from(ctx);
		}

		@Override
		public int getCount() {
			return listClass.size()+1;
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.integral_item1, null);
				
				cache = new ViewCache();

				cache.btclassbt = (ImageView) convertView.findViewById(R.id.integral_classbt);
				cache.btclass = (TextView) convertView.findViewById(R.id.integral_class);

				cache.btclassbt.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View arg0) {
						
						String clid=arg0.getTag().toString();
						
						currentClass=clid;
						
						refreshView(clid);

					}
				});
				
				convertView.setTag(cache);

			} else {
				cache = new ViewCache();
				cache = (ViewCache) convertView.getTag();
			}
			
			if(position==0){
				cache.btclass.setText("所有类别");
				cache.btclassbt.setTag("0");
				if(currentClass.equals("0")){
					cache.btclassbt.setImageResource(R.drawable.integral22);
				}else{
					cache.btclassbt.setImageResource(R.drawable.integral33);
				}
			}else{
				cache.btclass.setText(listClass.get(position-1).name);
				cache.btclassbt.setTag(listClass.get(position-1).id);
				if(currentClass.equals(listClass.get(position-1).id)){
					cache.btclassbt.setImageResource(R.drawable.integral22);
				}else{
					cache.btclassbt.setImageResource(R.drawable.integral33);
				}
			}
			
			return convertView;
		}

		private final class ViewCache {
			public ImageView btclassbt;
			public TextView btclass;
		}
	}
	
	private class DetailAdapter extends BaseAdapter {


		private LayoutInflater inflater;
		private ViewCacheb cache;

		public DetailAdapter(Context ctx) {

			inflater = LayoutInflater.from(ctx);
		}

		@Override
		public int getCount() {
			return listDetail.size();
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.integral_item2, null);
				
				cache = new ViewCacheb();

				cache.text = (TextView) convertView.findViewById(R.id.integral_detail);

				convertView.setTag(cache);

			} else {
				cache = new ViewCacheb();
				cache = (ViewCacheb) convertView.getTag();
			}
			
			cache.text.setText(listDetail.get(position).name+"\n"+listDetail.get(position).Scope+"  "+listDetail.get(position).Level+"  "+listDetail.get(position).Grade+"积分");

			return convertView;
		}

		private final class ViewCacheb {
			public TextView text;
		}
	}

}
