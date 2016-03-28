package com.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ChooseOrganization extends Activity {

	private ListView lv;
	private ArrayAdapter<String> myArrayAdapter;
	private List<String> list = new ArrayList<String>();
	private JSONArray array;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_organization);
		intiData();
		intiView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_organization, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(ChooseOrganization.this,UserCenter.class);
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
			Toast.makeText(getApplicationContext(), "2222222",Toast.LENGTH_SHORT).show();
			return false;
		}
		return false;

	}
	
	public void intiData() {
		Intent intent=getIntent();
		try{
			array=new JSONArray(intent.getStringExtra("oganization"));
			for(int i=0;i<array.length();i++){
				JSONObject obj=array.getJSONObject(i);
				list.add(obj.getString("name"));
			}
		}catch(Exception e){
			
		}
		
	}

	public void intiView() {
		lv = (ListView) findViewById(R.id.chooseorg_Listview);
		myArrayAdapter = new ArrayAdapter<String>(this,R.layout.register_school_item, list);
		lv.setAdapter(myArrayAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				String or=list.get(arg2);
				int len=array.length();
				for(int i=0;i<len;i++){
					try {
						JSONObject obj = array.getJSONObject(i);
						if(obj.getString("name").equals(or)){
							Intent intent=new Intent(ChooseOrganization.this,ManagerActivity.class);
			            	intent.putExtra("oganizationid", obj.getString("id"));
							startActivity(intent);
							finish();
							overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
						}
					} catch (JSONException e) {

					}				
				}
			}
		});

	}

}
