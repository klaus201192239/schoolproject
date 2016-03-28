package com.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class JoinTeamVerify extends Activity {

	private String activityid,teamid,from,name,leader;
	
	private int pwd;
	
	private TextView teamname,teamleader; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_team_verify);
		Intent intent=getIntent();
		activityid=intent.getStringExtra("activityid");
		from=intent.getStringExtra("from");
		teamid=intent.getStringExtra("teamid");
		pwd=intent.getIntExtra("pwd", -1);
		name=intent.getStringExtra("name");
		leader=intent.getStringExtra("leader");
		
		intiView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.join_team_verify, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(JoinTeamVerify.this,OtherTeamList.class);
			intent.putExtra("activityid",activityid);
			intent.putExtra("from", from);
			startActivity(intent);
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
	
	
	public void intiView(){
		teamname=(TextView)findViewById(R.id.TeamVerify_name);
		teamleader=(TextView)findViewById(R.id.TeamVerify_leader);
		teamname.setText("团队："+name);
		teamleader.setText("队长："+leader);
	}
	
	public void btonclick(View v){
		if(v.getId()==R.id.TeamVerify_back){
			Intent intent=new Intent(JoinTeamVerify.this,OtherTeamList.class);
			intent.putExtra("activityid",activityid);
			intent.putExtra("from", from);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}
		if(v.getId()==R.id.TeamVerify_finish){
			String input= ((EditText)findViewById(R.id.TeamVerify_pwd)).getText().toString().trim();
			if(input.length()==0){
				Toast.makeText(getApplicationContext(), "请输入验证口令～～", Toast.LENGTH_SHORT).show();
				return ;
			}
			
			if(Integer.parseInt(input)==pwd){
				Intent intent=new Intent(JoinTeamVerify.this,JoinTeam.class);				
				intent.putExtra("activityid",activityid);
				intent.putExtra("from", from);
				intent.putExtra("teamid", teamid);
				intent.putExtra("leader", leader);
				intent.putExtra("name", name);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
				return ;
			}else{
				Toast.makeText(getApplicationContext(), "验证口令不正确哦", Toast.LENGTH_SHORT).show();
				return ;
			}
		}
	}
}
