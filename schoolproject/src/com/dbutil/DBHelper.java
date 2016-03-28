package com.dbutil;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper {
	public Context context;
	public SQLiteDatabase my_DataBase;
	public DBHelper(Context con){
		this.context=con;
	}
	public SQLiteDatabase CreatOrOpen(String dbName){
		my_DataBase=this.context.openOrCreateDatabase(dbName+".db",0, null);
		return my_DataBase;
	}
	public boolean closeDB(){
		try{
			my_DataBase.close();
		}catch(Exception e){
			return false;
		}
		return true;	
	}
	public boolean findTable(String tableName){
		try{
			String sqlstr="SELECT count(*) FROM sqlite_master WHERE type='table' AND name='"+tableName+"';";
			Cursor cursor = my_DataBase.rawQuery(sqlstr, null);
			if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                	return true;
                }
			}
		}catch(Exception e){
			return false;
		}
		return false;	
	}
	public boolean excuteInfo(String sqlstr){
		try{
			my_DataBase.execSQL(sqlstr);
		}catch(Exception e){
			return false;
		}
		return true;	
	}
	public Cursor selectInfo(String sqlstr){
		return my_DataBase.rawQuery(sqlstr, null);
	}
	
	/*public List<Map<String, Object>> selectInfo(String sqlstr){	
		Cursor cursor=my_DataBase.rawQuery(sqlstr, null); 
		while (cursor.moveToNext()) {
			Map<String, Object> map=new HashMap<String, Object>(); 
			for(int i=0;i<cursor.getColumnCount();i++){
				map.put(cursor.getColumnName(i), cursor.get)
			}
			
			
			   int personid = cursor.getInt(0); //��ȡ��һ�е�ֵ,��һ�е�������0��ʼ
			   String name = cursor.getString(1);//��ȡ�ڶ��е�ֵ
			   int age = cursor.getInt(2);//��ȡ�����е�ֵ
			}
    	if(cursor.moveToNext()){
            int count = cursor.getInt(0);
            if(count>0){
            	dbhelper.closeDB();
            	Toast.makeText(getApplicationContext(), "���Ѿ��������ˣ���ȴ���Ϣ", Toast.LENGTH_SHORT).show(); 
            	return ;
            }
		}
		return null;
	}*/
}

