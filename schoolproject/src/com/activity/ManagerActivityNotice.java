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
import android.widget.TextView;
import android.widget.Toast;

public class ManagerActivityNotice extends Activity {

	private String activityid,oganizationid,oganization;
	private String title,ntitle,ncontent;
	private TextView actitle;
	private EditText noticetitle,content;
	private ProgressDialog progressDialog;
	
	private String tmpt = "";
    // 是否重置了EditText的内容
    private boolean resetTextt;
    
    private String tmpc = "";
    // 是否重置了EditText的内容
    private boolean resetTextc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager_activity_notice);
		
		intidata();
		intiview();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manager_activity_notice, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
		}
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			Toast.makeText(getApplicationContext(), "1111", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Toast.makeText(getApplicationContext(), "2222222",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return false;

	}
	
	public void intidata(){
		Intent intent=getIntent();
		activityid=intent.getStringExtra("activityid");
		oganizationid=intent.getStringExtra("oganizationid");
		oganization=intent.getStringExtra("oganization");
		title=intent.getStringExtra("title");
		
	}
	
	public void intiview(){
		actitle =(TextView)findViewById(R.id.managenotice_title);
		noticetitle =(EditText)findViewById(R.id.managenotice_notice_title);		
		content=(EditText)findViewById(R.id.managenotice_content);		
		actitle.setText(title);
		
		noticetitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int arg2, int arg3) {
                if (!resetTextt) {
                    if ((arg3 == 2) && (!EmojiFilter.containsEmoji(s.toString().substring(start, start + 2)))) {
                        resetTextt = true;
                        noticetitle.setText(tmpt);
                        noticetitle.invalidate();
                        if (noticetitle.getText().length() > 1){
                        	Selection.setSelection(noticetitle.getText(), noticetitle.getText().length());
                        }
                        Toast.makeText(getApplicationContext(), "不支持表情输入",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    resetTextt = false;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (!resetTextt)
                    tmpt = arg0.toString();

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });
		
		content.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int arg2, int arg3) {
                if (!resetTextc) {
                    if ((arg3 == 2) && (!EmojiFilter.containsEmoji(s.toString().substring(start, start + 2)))) {
                        resetTextc = true;
                        content.setText(tmpc);
                        content.invalidate();
                        if (content.getText().length() > 1){
                        	Selection.setSelection(content.getText(), content.getText().length());
                        }
                        Toast.makeText(getApplicationContext(), "不支持表情输入",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    resetTextc = false;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (!resetTextc)
                    tmpc = arg0.toString();

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });
		
	}

	public void back(){
		Intent intent1=new Intent(ManagerActivityNotice.this,ManagerActivityDetail.class);
		intent1.putExtra("activityid", activityid);
		intent1.putExtra("oganizationid", oganizationid);
		startActivity(intent1);
		finish();
		overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
		return ;
	}
	
	public void btonclik(View v){
		int vid=v.getId();
		if(vid==R.id.managenotice_upload){
			
			ntitle=noticetitle.getText().toString().trim();
			ncontent=content.getText().toString().trim();

			if(ntitle==null||ntitle.length()==0){
				Toast.makeText(getApplicationContext(), "请填写通知标题", Toast.LENGTH_SHORT).show();  
				return ;
			}
			
			if(ntitle.length()>12){
				Toast.makeText(getApplicationContext(), "通知标题太长了～", Toast.LENGTH_SHORT).show();  
				return ;
			}
			
			if(ncontent==null||ncontent.length()==0){
				Toast.makeText(getApplicationContext(), "请填写通知内容", Toast.LENGTH_SHORT).show();  
				return ;
			}
			
			progressDialog = ProgressDialog.show(ManagerActivityNotice.this, "","正在群发消息,请稍候！");
			new Thread(){
				public void run(){
					
					
					
					String para="";
					try {
						para = "activityid="+activityid+"&oganizationid="+oganizationid+
								"&oganization="+URLEncoder.encode(oganization,"UTF-8").toString()+
								"&title="+URLEncoder.encode(ntitle,"UTF-8").toString()+
								"&content="+URLEncoder.encode(ncontent,"UTF-8").toString();
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}

					String httpjson = HttpUtil.sendGet(StaticString.server+"managernotice",para);
					
					Message msg_listData = new Message();
					if(httpjson.equals("error")){
						msg_listData.what=0;
					}else{
						if(httpjson.equals("ok")){
							msg_listData.what=1;
						}else{
							msg_listData.what=0;
						}
					}
					handler.sendMessage(msg_listData);
				}
			}.start();
			
		}else{
			back();
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
            	Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
            	back();
            	break;
            }
		}
	};
}
