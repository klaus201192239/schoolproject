package com.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.utilt.EmojiFilter;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CreateTeam extends Activity {

	private boolean check;
	private String activityid,userid;
	private String studentname,from,name,slogan,Abstract,need,Password,activityname;
	private CheckBox createteam_check;
	private EditText createteam_name,createteam_slogan,createteam_Abstract,createteam_need,createteam_Password;
	private TextView createteam_leader;
	private LinearLayout createteam_lv_Password;

	private String tmpna = "";
    private boolean resetTextna;
    
    private String tmpsl = "";
    private boolean resetTextsl;
    
    private String tmpsh = "";
    private boolean resetTextsh;
    
    private String tmpne = "";
    private boolean resetTextne;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_team);
		intiData();
		intiView();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.create_team, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(from.equals("05")){
				startActivity(new Intent(CreateTeam.this, InActivity.class).putExtra("CurrentId", activityid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			}else{
				startActivity(new Intent(CreateTeam.this, InActivityDetail.class).putExtra("id", activityid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			}
		}
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			Toast.makeText(getApplicationContext(), "1111", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Toast.makeText(getApplicationContext(), "2222222",Toast.LENGTH_SHORT).show();
			return false;
		}
		return false;

	}
	
	public void intiData(){
		Intent intent=getIntent();
		activityid=intent.getStringExtra("activityid");
		from =intent.getStringExtra("from");
		activityname=intent.getStringExtra("activityname");
		SharedPreferences setting = getSharedPreferences("schooltime", MODE_PRIVATE);
		userid=setting.getString("Id","");
		studentname=setting.getString("Name", "");
	}
	
	public void intiView(){
		createteam_name=(EditText)findViewById(R.id.createteam_name);
		createteam_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int arg2, int arg3) {
                if (!resetTextna) {
                    if ((arg3 == 2) && (!EmojiFilter.containsEmoji(s.toString().substring(start, start + 2)))) {
                        resetTextna = true;
                        createteam_name.setText(tmpna);
                        createteam_name.invalidate();
                        if (createteam_name.getText().length() > 1){
                        	Selection.setSelection(createteam_name.getText(), createteam_name.getText().length());
                        }
                        Toast.makeText(getApplicationContext(), "此处不能输入图片表情哦～",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    resetTextna = false;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (!resetTextna)
                    tmpna = arg0.toString();

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });
		createteam_leader=(TextView)findViewById(R.id.createteam_leader);
		
		createteam_slogan=(EditText)findViewById(R.id.createteam_slogan);
		createteam_slogan.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int arg2, int arg3) {
                if (!resetTextsl) {
                    if ((arg3 == 2) && (!EmojiFilter.containsEmoji(s.toString().substring(start, start + 2)))) {
                        resetTextsl = true;
                        createteam_slogan.setText(tmpsl);
                        createteam_slogan.invalidate();
                        if (createteam_slogan.getText().length() > 1){
                        	Selection.setSelection(createteam_slogan.getText(), createteam_slogan.getText().length());
                        }
                        Toast.makeText(getApplicationContext(), "此处不能输入图片表情哦～",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    resetTextsl = false;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (!resetTextsl)
                    tmpsl = arg0.toString();

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });
		createteam_Abstract=(EditText)findViewById(R.id.createteam_Abstract);
		createteam_Abstract.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int arg2, int arg3) {
                if (!resetTextsh) {
                    if ((arg3 == 2) && (!EmojiFilter.containsEmoji(s.toString().substring(start, start + 2)))) {
                        resetTextsh = true;
                        createteam_Abstract.setText(tmpsh);
                        createteam_Abstract.invalidate();
                        if (createteam_Abstract.getText().length() > 1){
                        	Selection.setSelection(createteam_Abstract.getText(), createteam_Abstract.getText().length());
                        }
                        Toast.makeText(getApplicationContext(), "此处不能输入图片表情哦～",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    resetTextsh = false;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (!resetTextsh)
                    tmpsh = arg0.toString();

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });
		createteam_need=(EditText)findViewById(R.id.createteam_need);
		createteam_need.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int arg2, int arg3) {
                if (!resetTextne) {
                    if ((arg3 == 2) && (!EmojiFilter.containsEmoji(s.toString().substring(start, start + 2)))) {
                        resetTextne = true;
                        createteam_need.setText(tmpne);
                        createteam_need.invalidate();
                        if (createteam_need.getText().length() > 1){
                        	Selection.setSelection(createteam_need.getText(), createteam_need.getText().length());
                        }
                        Toast.makeText(getApplicationContext(), "此处不能输入图片表情哦～",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    resetTextne = false;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (!resetTextne)
                    tmpne = arg0.toString();

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });

		createteam_check=(CheckBox)findViewById(R.id.createteam_check);
		createteam_lv_Password=(LinearLayout)findViewById(R.id.createteam_lv_Password);
		createteam_Password=(EditText)findViewById(R.id.createteam_Password);
		
		createteam_leader.setText(studentname);
		check=true;
		createteam_lv_Password.setVisibility(View.GONE);
		
		createteam_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				check=arg1;
				if(arg1){
					createteam_lv_Password.setVisibility(View.GONE);
				}else{
					createteam_lv_Password.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	public void btonclik(View v){
		int vid=v.getId();
		if(vid==R.id.createteam_back){
			if(from.equals("05")){
				startActivity(new Intent(CreateTeam.this, InActivity.class).putExtra("CurrentId", activityid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			}else{
				startActivity(new Intent(CreateTeam.this, InActivityDetail.class).putExtra("id", activityid));
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			}
			return ;
		}
		if(vid==R.id.createteam_bt){
			return ;
		}
		if(vid==R.id.jointeam_bt){
			startActivity(new Intent(CreateTeam.this,OtherTeamList.class).putExtra("activityid", activityid).putExtra("from", from));
			finish();
			overridePendingTransition(R.anim.slide_clam, R.anim.slide_clam);
		}
		if(vid==R.id.createteam_upload){
			name=createteam_name.getText().toString();
			slogan=createteam_slogan.getText().toString();
			Abstract=createteam_Abstract.getText().toString();
			need=createteam_need.getText().toString();
			Password="0";
			if(check==false){
				Password=createteam_Password.getText().toString();
				if(Password.length()==0){
					Toast.makeText(getApplicationContext(), "请填写全部信息", Toast.LENGTH_SHORT).show();  
					return ;
				}
				if(Password.length()!=4){
					Toast.makeText(getApplicationContext(), "请设置四位整数的验证口令", Toast.LENGTH_SHORT).show();  
					return ;
				}
			}
			if(name.length()==0||slogan.length()==0||Abstract.length()==0||need.length()==0){
				Toast.makeText(getApplicationContext(), "请填写全部信息", Toast.LENGTH_SHORT).show();  
				return ;
			}
			
			if(name.length()>10){
				Toast.makeText(getApplicationContext(), "团队名称太长了～", Toast.LENGTH_SHORT).show();  
				return ;
			}
			if(slogan.length()>12){
				Toast.makeText(getApplicationContext(), "团队口号不超过12个字符～", Toast.LENGTH_SHORT).show();  
				return ;
			}
			
			String uploadteaminfo="";
			try {
				uploadteaminfo = "userid="+userid+"&activityid="+activityid+
						"&name="+URLEncoder.encode(name,"UTF-8").toString()+
						"&slogan="+URLEncoder.encode(slogan,"UTF-8").toString()+
						"&abstract="+URLEncoder.encode(Abstract,"UTF-8").toString()+
						"&need="+URLEncoder.encode(need,"UTF-8").toString()+"&password="+Password;
			} catch (UnsupportedEncodingException e) {
				Toast.makeText(getApplicationContext(), "发生意外错误，请重试", Toast.LENGTH_SHORT).show();  
				return ;
			}
			Intent intent=new Intent(CreateTeam.this,OnlySignUp.class);
			intent.putExtra("activityid",activityid);
        	intent.putExtra("from","09");
        	intent.putExtra("activityname", activityname);
        	intent.putExtra("teaminfo", uploadteaminfo);
        	intent.putExtra("teamtag", 1);
        	startActivity(intent);
        	finish();
        	overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
		}
	}
}
