package com.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.http.HttpUtil;
import com.staticdata.StaticString;
import com.utilt.EmojiFilter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Suggestion extends Activity {

	private EditText ETcontent;
	private String content;
	private ProgressDialog progressDialog; 
	private String tmp = "";
    // 是否重置了EditText的内容
    private boolean resetText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggestion);
		
		ETcontent=(EditText)findViewById(R.id.suggestion_content);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.suggestion, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(Suggestion.this,UserCenter.class);
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

	public void btonclick(View v){
		int vid=v.getId();
		if(vid==R.id.suggestion_back){
			Intent intent=new Intent(Suggestion.this,UserCenter.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			return ;
		}
		
		if(vid==R.id.suggestion_ok){
			content=ETcontent.getText().toString();
			if(content.length()==0||content==null){
				Toast.makeText(getApplicationContext(), "1111", Toast.LENGTH_SHORT).show();  
				return ;
			}
			
			progressDialog = ProgressDialog.show(Suggestion.this, "", "正在上传,请稍候！");
			
			new Thread(){
				public void run(){
					SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
					String userid=set.getString("Id","");
					String httpjson=null;
					try {
						httpjson = HttpUtil.sendGet(StaticString.server+"submitsuggestion", "userid="+userid+"&content="+URLEncoder.encode(content,"UTF-8").toString());
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					Message msg_listData = new Message();
					if(httpjson.equals("ok")){
						msg_listData.what =1;						
					}else{
						msg_listData.what =0;
					}
					handler.sendMessage(msg_listData);
				}
			}.start();
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
            switch (message.what) {
            case 0:
            	progressDialog.dismiss(); //关闭进度条
            	Toast.makeText(getApplicationContext(), "网络连接或其他意外错误,请重新提交奥", Toast.LENGTH_SHORT).show();  
            	break;
            case 1:
            	progressDialog.dismiss(); //关闭进度条
            	Toast.makeText(getApplicationContext(), "提交成功，感谢您的宝贵意见~我们互及时与您联系", Toast.LENGTH_SHORT).show();  
            	Intent intent=new Intent(Suggestion.this,UserCenter.class);
    			startActivity(intent);
    			finish();
    			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            	break;
            }
		}
	};
	
}
