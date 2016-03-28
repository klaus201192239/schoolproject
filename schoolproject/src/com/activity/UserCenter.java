package com.activity;

import java.util.Timer;
import java.util.TimerTask;
import com.service.CoreService;
import com.staticdata.StaticInt;
import com.utilt.utils;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserCenter extends Activity {

	private static Boolean isExit = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center);
		intiData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_center, menu);
		return true;
	}
			
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click();
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

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "Baby，再按就出去了～", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			Intent intent = new Intent(UserCenter.this, CoreService.class);
			intent.putExtra("tag", StaticInt.saveinactivity);
			startService(intent);
			finish();
			//System.exit(0);
		}
	}
	
	
	public void intiData(){
		ImageView user_center_head=(ImageView)findViewById(R.id.user_center_head);
		TextView user_center_name=(TextView)findViewById(R.id.user_center_name);
		TextView user_center_info=(TextView)findViewById(R.id.user_center_info);
		
		SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
		user_center_head.setBackgroundResource(utils.getSexHead(set.getInt("Sex", -1)));
		user_center_name.setText(set.getString("Name", "")+"("+utils.getDegreeName(set.getInt("Degree", -1))+")");
		user_center_info.setText(set.getString("SchoolName", "")+" "+set.getString("MajorName", "")+" "+set.getInt("Grade", -1)+"级");
	}
	
	public void onclick(View v){
		
		int vid=v.getId();

		if(vid==R.id.usercenter_table_leftView){
			Intent intent=new Intent(UserCenter.this,OutActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_clam, R.anim.slide_clam);
			return ;
		}
		if(vid==R.id.usercenter_table_rightView){
			return ;
		}
		if(vid==R.id.usercenter_table_centerView){
			Intent intent=new Intent(UserCenter.this,InActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_clam, R.anim.slide_clam);
			return ;
		}
		
		if(vid==R.id.user_center_lv_one){
			Intent intent=new Intent(UserCenter.this,ChangeUserInfo.class);
			intent.putExtra("from","016");
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return ;
		}
		
		if(vid==R.id.user_center_lv_two){
			Intent intent=new Intent(UserCenter.this,MyActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return ;
		}
		
		if(vid==R.id.user_center_lv_three){
			Intent intent=new Intent(UserCenter.this,ActivityIntegral.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return ;
		}
		
		if(vid==R.id.user_center_lv_four){
			Intent intent=new Intent(UserCenter.this,ManagerTS.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return ;
		}
		
		if(vid==R.id.user_center_lv_five){
			Intent intent=new Intent(UserCenter.this,MyTeams.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return ;
		}
		
		if(vid==R.id.user_center_lv_six){
			Intent intent=new Intent(UserCenter.this,Notice.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return ;
		}
		
		if(vid==R.id.user_center_lv_seven){
			Intent intent=new Intent(UserCenter.this,SystemSettings.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return ;
		}
		
		if(vid==R.id.user_center_lv_eight){
			Intent intent=new Intent(UserCenter.this,Suggestion.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return ;
		}
	}
}
