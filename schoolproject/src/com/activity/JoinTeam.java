package com.activity;

import com.utilt.EmojiFilter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class JoinTeam extends Activity {

	private String activityid,teamid,name,leader;
	
	private String from;
	
	private TextView teamname,teamleader; 
	
	private EditText ETcontent;
	private String tmp = "";
    // 是否重置了EditText的内容
    private boolean resetText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_team);
		Intent intent=getIntent();
		activityid=intent.getStringExtra("activityid");
		from=intent.getStringExtra("from");
		teamid=intent.getStringExtra("teamid");
		name=intent.getStringExtra("name");
		leader=intent.getStringExtra("leader");
		
		intiView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.join_team, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(JoinTeam.this,OtherTeamList.class);
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
			Toast.makeText(getApplicationContext(), "2222",Toast.LENGTH_SHORT).show();
			return false;
		}
		return false;

	}
	
	public void intiView(){
		teamname=(TextView)findViewById(R.id.jointeam_name);
		teamleader=(TextView)findViewById(R.id.jointeam_leader);
		teamname.setText("团队："+name);
		teamleader.setText("队长："+leader);
		
		
		ETcontent=((EditText)findViewById(R.id.jointeam_pwd));
		ETcontent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int arg2, int arg3) {
                if (!resetText) {
                    if ((arg3 == 2) && (!EmojiFilter.containsEmoji(s.toString().substring(start, start + 2)))) {
                        resetText = true;
                        ETcontent.setText(tmp);
                        ETcontent.invalidate();
                        if (ETcontent.getText().length() > 1){
                        	Selection.setSelection(ETcontent.getText(), ETcontent.getText().length());
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
	
	public void btonclick(View v){
		if(v.getId()==R.id.jointeam_back){
			Intent intent=new Intent(JoinTeam.this,OtherTeamList.class);
			intent.putExtra("activityid",activityid);
			intent.putExtra("from", from);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}
		if(v.getId()==R.id.jointeam_finish){
			
			
			String input=ETcontent.getText().toString();
			
			if(input.length()==0){
				Toast.makeText(getApplicationContext(), "请填写自我介绍", Toast.LENGTH_SHORT).show();
				return ;
			}			
			Intent intent=new Intent(JoinTeam.this,OnlySignUp.class);				
			intent.putExtra("activityid",activityid);
			intent.putExtra("from", from);
			intent.putExtra("teamid", teamid);
			intent.putExtra("showself", input);
			intent.putExtra("teamtag", 2);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return ;
		}
	}
}
