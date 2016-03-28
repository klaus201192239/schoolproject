package com.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadApk extends Activity {

	private TextView textview;
	private ProgressBar pro;
	private String downurl="";
	
	private int downState=0;
	private int fileSize;
	int downLoadFileSize;
	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// ����һ��Handler�����ڴ��������߳���UI��ͨѶ
			switch (msg.what) {
			case 1:
				int result = downLoadFileSize * 100 / fileSize;
				textview.setText(result + "%");
				pro.setProgress(result);
				break;
			case 2:
				downState=2;
				Toast.makeText(getApplicationContext(), "������ɣ��ȴ���װ��",Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new
			    File(Environment.getExternalStorageDirectory()+ "/schooltimellt/schoolproject.apk")),"application/vnd.android.package-archive");
				startActivity(intent);
				break;
			case -1:
				downState=-1;
				Toast.makeText(getApplicationContext(), "���س��������ԡ�",Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_apk);

		textview = (TextView) findViewById(R.id.downapk_text);
		pro = (ProgressBar) findViewById(R.id.downapk_progress);
		
		SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
		downurl=set.getString("DownloadUrl", "nourl");

		new Thread() {
			public void run() {
				try {
					
					if(downurl.equals("nourl")){
						downState=-1;
						sendMsg(-1);
					}else{
						down_file(downurl,Environment.getExternalStorageDirectory()+ "/schooltimellt/");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.download_apk, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
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

	public void btonclick(View v) {
		back();
	}

	public void back() {
		if(downState==0||downState==-1){
			Builder builder=new AlertDialog.Builder(DownloadApk.this);
			builder.setTitle("��������");
			builder.setMessage("��ȷ����ʱ�����������°汾����");
			builder.setPositiveButton("�ݲ�����", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){
					Intent intent2 = new Intent(DownloadApk.this, UpdateSystems.class);
					startActivity(intent2);
					finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				}
			});
			builder.setNegativeButton("��������",null);
			builder.create().show();
		}
			
		if(downState==1){
			Builder builder=new AlertDialog.Builder(DownloadApk.this);
			builder.setTitle("��������");
			builder.setMessage("�ļ������У�����ȴ�������ϣ�����ϵͳ�������������");
			builder.setPositiveButton("�˳�ҳ��", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){
					Intent intent2 = new Intent(DownloadApk.this, UpdateSystems.class);
					startActivity(intent2);
					finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				}
			});
			builder.setNegativeButton("��������",null);
			builder.create().show();
		}
		if(downState==2){
			Builder builder=new AlertDialog.Builder(DownloadApk.this);
			builder.setTitle("��������");
			builder.setMessage("�ļ������Ѿ���ɣ�����ȴ���װ��ϣ�����ϵͳ�������������");
			builder.setPositiveButton("�˳�ҳ��", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){
					Intent intent2 = new Intent(DownloadApk.this, UpdateSystems.class);
					startActivity(intent2);
					finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
				}
			});
			builder.setNegativeButton("��������",null);
			builder.create().show();
		}
		
	}

	private void sendMsg(int flag) {
		Message msg = new Message();
		msg.what = flag;
		handler.sendMessage(msg);
	}

	public void down_file(String url, String path) throws Exception {

		try {
			// ���غ���
			String filename = url.substring(url.lastIndexOf("/") + 1);
			// ��ȡ�ļ���
			URL myURL = new URL(url);
			URLConnection conn = myURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			this.fileSize = conn.getContentLength();// ������Ӧ��ȡ�ļ���С
			if (this.fileSize <= 0)
				throw new RuntimeException("�޷���֪�ļ���С ");
			if (is == null)
				throw new RuntimeException("stream is null");
			FileOutputStream fos = new FileOutputStream(path + filename);
			// �����ݴ���·��+�ļ���
			byte buf[] = new byte[1024];
			downLoadFileSize = 0;
			int numread = 0;

			while ((numread = is.read(buf)) != -1) {

				fos.write(buf, 0, numread);
				downLoadFileSize += numread;

				downState=1;
				sendMsg(1);// ���½�����
			}
			
			fos.close();
			is.close();
			
			
			sendMsg(2);// ֪ͨ�������
		} catch (Exception ex) {
			downState=-1;
			sendMsg(-1);// ֪ͨ�������
		}

	}
}
