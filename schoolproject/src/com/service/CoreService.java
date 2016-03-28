package com.service;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import com.dbutil.DBHelper;
import com.http.HttpUtil;
import com.pagebean.InActivityBean;
import com.pagebean.OutActivityBean;
import com.pagebean.SignInBean;
import com.staticdata.StaticBoolean;
import com.staticdata.StaticInt;
import com.staticdata.StaticList;
import com.staticdata.StaticString;
import com.utilt.timeutil;

public class CoreService extends Service {

	private int type, activitytype;
	private String activityid, comment, oganizationid, outcategory;
	private DBHelper dbhelper;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent != null) {
			int tag = intent.getIntExtra("tag", -1);
			if (tag != -1) {
				switch (tag) {
				case StaticInt.downinactivity:
					downinactivity();
					break;
				case StaticInt.saveinactivity:
					saveinactivity();
					break;
				case StaticInt.takepartactivity:
					activityid = intent.getStringExtra("activityid");
					type = intent.getIntExtra("type", -1);
					takepartactivity();
					break;
				case StaticInt.emailattachment:
					activityid = intent.getStringExtra("activityid");
					sendattachment();
					break;
				case StaticInt.addcomment:
					activityid = intent.getStringExtra("activityid");
					activitytype = intent.getIntExtra("activitytype", -1);
					comment = intent.getStringExtra("comment");
					addcomment();
					break;
				case StaticInt.downoutactivity:
					outcategory = intent.getStringExtra("category");
					activityid = intent.getStringExtra("activityid");
					downoutactivity();
					break;
				case StaticInt.takepartoutactivity:
					activityid = intent.getStringExtra("activityid");
					type = intent.getIntExtra("type", -1);
					takepartoutactivity();
					break;
				case StaticInt.emailattachmentout:
					activityid = intent.getStringExtra("activityid");
					sendattachmentout();
					break;
				case StaticInt.downmyactivity:
					activityid = intent.getStringExtra("activityid");
					downmyactivity();
					break;
				case StaticInt.downsignin:
					activityid = intent.getStringExtra("activityid");
					downsigninlist();
					break;
				case StaticInt.downManagerActivity:
					oganizationid = intent.getStringExtra("activityid");
					downmanagerlist();
					break;
				case StaticInt.getbackpwd:
					getback();
					break;
				case StaticInt.getmyactivityid:
					getmyactivity();
					break;
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public void downinactivity() {
		StaticList.InActicitylistb.clear();
		if (StaticBoolean.NetLink == false) {
			return;
		}
		if (StaticList.InActicitylist.size() <= 0) {
			return;
		}
		new Thread() {
			public void run() {
			
				try{

					SharedPreferences settings = getSharedPreferences("schooltime",MODE_PRIVATE);
					String schoolid = settings.getString("ShoolId", "");
					
					int indexxx=StaticList.InActicitylist.size() - 1;
					String iddd= StaticList.InActicitylist.get(indexxx).id;
					
					String httpjson = HttpUtil.sendGet(StaticString.server + "getinactivity","schoolid="+ schoolid+ "&currentid="+ iddd);
					if (httpjson.equals("error")) {
					} else {
						if (httpjson.equals("nothing")) {
						} else {
							try {
								JSONArray jsonarry = new JSONArray(httpjson);
								int size = jsonarry.length();
								for (int i = 0; i < size; i++) {
									JSONObject cur = jsonarry.getJSONObject(i);
									InActivityBean bean = new InActivityBean();
									bean.id = cur.getString("id");// 获取第一列的值,第一列的索引从0开始
									bean.title = cur.getString("title");
									bean.imgurl = cur.getString("imgurl");
									bean.category = cur.getString("category");
									SimpleDateFormat sfStart = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",java.util.Locale.ENGLISH);
									bean.deadline = sfStart.parse(cur.getString("deadline"));
									bean.time = cur.getString("time");
									bean.pridenum = cur.getInt("pridenum");
									bean.opposenum = cur.getInt("opposenum");
									bean.commentnum = cur.getInt("commentnum");
									bean.onlyteam = cur.getInt("onlyteam");
									StaticList.InActicitylistb.add(bean);
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}catch(Exception e){
					System.out.println("RERESDSESSDERFDSDDDDF");
				}
			}
		}.start();
	}

	public void downoutactivity() {
		StaticList.OutActicitylistb.clear();
		if (StaticBoolean.NetLink == false) {
			return;
		}

		if (StaticList.OutActicitylist.size() <= 0) {
			return;
		}

		new Thread() {
			public void run() {

				String httpjson = HttpUtil
						.sendGet(
								StaticString.server + "getoutactivity",
								"classid="
										+ outcategory
										+ "&currentid="
										+ (StaticList.OutActicitylist
												.get(StaticList.OutActicitylist
														.size() - 1).id));
				if (httpjson.equals("error")) {
				} else {
					if (httpjson.equals("nothing")) {
					} else {
						try {
							JSONArray jsonarry = new JSONArray(httpjson);
							int size = jsonarry.length();
							for (int i = 0; i < size; i++) {
								JSONObject cur = jsonarry.getJSONObject(i);
								OutActivityBean bean = new OutActivityBean();
								bean.id = cur.getString("id");// 获取第一列的值,第一列的索引从0开始
								bean.title = cur.getString("title");
								bean.imgurl = cur.getString("imgurl");
								bean.category = cur.getString("category");
								SimpleDateFormat sfStart = new SimpleDateFormat(
										"EEE MMM dd HH:mm:ss zzz yyyy",
										java.util.Locale.ENGLISH);
								bean.deadline = sfStart.parse(cur
										.getString("deadline"));
								bean.time = cur.getString("time");
								bean.pridenum = cur.getInt("pridenum");
								bean.opposenum = cur.getInt("opposenum");
								bean.commentnum = cur.getInt("commentnum");
								StaticList.OutActicitylistb.add(bean);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
	}

	public void downmyactivity() {
		StaticList.MyActicitylistb.clear();
		if (StaticBoolean.NetLink == false) {
			return;
		}

		if (StaticList.MyActicitylist.size() <= 0) {
			return;
		}

		new Thread() {
			public void run() {
				if (StaticList.MyActicitylist.size() <= 0) {
					return;
				}

				String cid = StaticList.MyActicitylist.get(StaticList.MyActicitylist.size() - 1).id;

				JSONArray aar = new JSONArray();

				DBHelper dbhelper = new DBHelper(CoreService.this);
				dbhelper.CreatOrOpen("schooltime");
				Cursor cursor = dbhelper.selectInfo("select * from attendactivity where activityid<'"+ cid + "' order by activityid desc limit 3;");
				while (cursor.moveToNext()) {
					aar.put(cursor.getString(0));
				}
				dbhelper.closeDB();
				String httpjson = HttpUtil.sendGet(StaticString.server
						+ "getmyactivity", "jsonid=" + aar.toString());

				if (httpjson.equals("error")) {
				} else {
					if (httpjson.equals("nothing")) {
					} else {
						try {
							JSONArray jsonarry = new JSONArray(httpjson);
							int size = jsonarry.length();
							for (int i = 0; i < size; i++) {
								JSONObject cur = jsonarry.getJSONObject(i);
								InActivityBean bean = new InActivityBean();
								bean.id = cur.getString("id");// 获取第一列的值,第一列的索引从0开始
								bean.title = cur.getString("title");
								bean.imgurl = cur.getString("imgurl");
								bean.category = cur.getString("category");
								bean.deadline = timeutil.StringToTimestamp(cur.getString("deadline"));
								bean.time = cur.getString("time");
								bean.pridenum = cur.getInt("pridenum");
								bean.opposenum = cur.getInt("opposenum");
								bean.commentnum = cur.getInt("commentnum");
								StaticList.MyActicitylistb.add(bean);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
	}

	public void downmanagerlist() {
		StaticList.Managelistb.clear();
		if (StaticBoolean.NetLink == false) {
			return;
		}

		if (StaticList.Managelist.size() <= 0) {
			return;
		}

		new Thread() {
			public void run() {
				String httpjson = HttpUtil
						.sendGet(
								StaticString.server + "getmanagelist",
								"oganizationid="
										+ oganizationid
										+ "&currentid="
										+ StaticList.Managelist
												.get(StaticList.Managelist
														.size() - 1).id);
				if (httpjson.equals("error")) {
				} else {
					if (httpjson.equals("nothing")) {
					} else {
						try {
							JSONArray jsonarry = new JSONArray(httpjson);
							int size = jsonarry.length();
							for (int i = 0; i < size; i++) {
								JSONObject cur = jsonarry.getJSONObject(i);
								InActivityBean bean = new InActivityBean();
								bean.id = cur.getString("id");// 获取第一列的值,第一列的索引从0开始
								bean.title = cur.getString("title");
								bean.imgurl = cur.getString("imgurl");
								bean.category = cur.getString("category");
								SimpleDateFormat sfStart = new SimpleDateFormat(
										"EEE MMM dd HH:mm:ss zzz yyyy",
										java.util.Locale.ENGLISH);
								bean.deadline = sfStart.parse(cur
										.getString("deadline"));
								bean.time = cur.getString("time");
								bean.pridenum = cur.getInt("pridenum");
								bean.opposenum = cur.getInt("opposenum");
								bean.commentnum = cur.getInt("commentnum");
								StaticList.Managelistb.add(bean);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
	}

	public void downsigninlist() {

		StaticList.SignInlistb.clear();

		if (StaticList.SignInlist.size() <= 0) {
			return;
		}

		new Thread() {
			public void run() {

				String httpjson = HttpUtil
						.sendGet(
								StaticString.server + "getsigninlist",
								"activityid="
										+ activityid
										+ "&currentstuid="
										+ StaticList.SignInlist
												.get(StaticList.SignInlist
														.size() - 1).studentid
												.trim());

				if (httpjson.equals("error")) {

				} else {
					try {
						JSONArray array = new JSONArray(httpjson);
						int len = array.length();
						for (int i = 0; i < len; i++) {
							SignInBean bean = new SignInBean();
							JSONObject obj = array.getJSONObject(i);
							bean.degree = obj.getInt("degree");
							bean.grade = obj.getInt("grade");
							bean.id = obj.getString("id");
							bean.major = obj.getString("major");
							bean.name = obj.getString("name");
							bean.studentid = obj.getString("studentid");
							StaticList.SignInlistb.add(bean);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void saveinactivity() {

		StaticBoolean.OutActivityFirst = true;
		StaticBoolean.MyActivityFirst = true;

		DBHelper dbHelper = new DBHelper(this);
		dbHelper.CreatOrOpen("schooltime");
		dbHelper.excuteInfo("delete from inactivity;");
		int len = StaticList.InActicitylist.size();
		if (len > 20) {
			len = 20;
		}
		for (int i = 0; i < len; i++) {
			String id = StaticList.InActicitylist.get(i).id;
			String title = StaticList.InActicitylist.get(i).title;
			String img = StaticList.InActicitylist.get(i).imgurl;
			String cate = StaticList.InActicitylist.get(i).category;
			String deadline = StaticList.InActicitylist.get(i).deadline
					.toString();
			String time = StaticList.InActicitylist.get(i).time;
			int pride = StaticList.InActicitylist.get(i).pridenum;
			int oppose = StaticList.InActicitylist.get(i).opposenum;
			int comment = StaticList.InActicitylist.get(i).commentnum;
			int onlyteam = StaticList.InActicitylist.get(i).onlyteam;
			String sql = "insert into inactivity values('" + id + "','" + title
					+ "','" + img + "','" + cate + "','" + deadline + "','"
					+ time + "','" + pride + "','" + oppose + "','" + comment
					+ "','" + onlyteam + "')";
			dbHelper.excuteInfo(sql);
		}

		len = StaticList.OutActicitylistTemp.size();

		if (len != 0) {
			if (len > 20) {
				len = 20;
			}
			dbHelper.excuteInfo("delete from outactivity;");
			for (int i = 0; i < len; i++) {
				String id = StaticList.OutActicitylistTemp.get(i).id;
				String title = StaticList.OutActicitylistTemp.get(i).title;
				String img = StaticList.OutActicitylistTemp.get(i).imgurl;
				String cate = StaticList.OutActicitylistTemp.get(i).category;
				String deadline = StaticList.OutActicitylistTemp.get(i).deadline
						.toString();
				String time = StaticList.OutActicitylistTemp.get(i).time;
				int pride = StaticList.OutActicitylistTemp.get(i).pridenum;
				int oppose = StaticList.OutActicitylistTemp.get(i).opposenum;
				int comment = StaticList.OutActicitylistTemp.get(i).commentnum;
				String sql = "insert into outactivity values('" + id + "','"
						+ title + "','" + img + "','" + cate + "','" + deadline
						+ "','" + time + "','" + pride + "','" + oppose + "','"
						+ comment + "')";
				dbHelper.excuteInfo(sql);
			}
			StaticList.OutActicitylistTemp.clear();
			StaticList.OutActicitylist.clear();
			StaticList.OutActicitylistb.clear();

		} else {
			len = StaticList.OutActicitylist.size();
			if (len > 20) {
				len = 20;
			}
			if (len != 0) {
				dbHelper.excuteInfo("delete from outactivity;");
				for (int i = 0; i < len; i++) {
					String id = StaticList.OutActicitylist.get(i).id;
					String title = StaticList.OutActicitylist.get(i).title;
					String img = StaticList.OutActicitylist.get(i).imgurl;
					String cate = StaticList.OutActicitylist.get(i).category;
					String deadline = StaticList.OutActicitylist.get(i).deadline
							.toString();
					String time = StaticList.OutActicitylist.get(i).time;
					int pride = StaticList.OutActicitylist.get(i).pridenum;
					int oppose = StaticList.OutActicitylist.get(i).opposenum;
					int comment = StaticList.OutActicitylist.get(i).commentnum;
					String sql = "insert into outactivity values('" + id
							+ "','" + title + "','" + img + "','" + cate
							+ "','" + deadline + "','" + time + "','" + pride
							+ "','" + oppose + "','" + comment + "')";
					dbHelper.excuteInfo(sql);
				}
				StaticList.OutActicitylistTemp.clear();
				StaticList.OutActicitylist.clear();
				StaticList.OutActicitylistb.clear();
			}
		}

		len = StaticList.MyActicitylist.size();

		if (len != 0) {
			dbHelper.excuteInfo("delete from myactivity;");
		}

		if (len > 20) {
			len = 20;
		}

		if (len != 0) {
			for (int i = 0; i < len; i++) {
				String id = StaticList.MyActicitylist.get(i).id;
				String title = StaticList.MyActicitylist.get(i).title;
				String img = StaticList.MyActicitylist.get(i).imgurl;
				String cate = StaticList.MyActicitylist.get(i).category;
				String deadline = StaticList.MyActicitylist.get(i).deadline.toString();
				String time = StaticList.MyActicitylist.get(i).time;
				int pride = StaticList.MyActicitylist.get(i).pridenum;
				int oppose = StaticList.MyActicitylist.get(i).opposenum;
				int comment = StaticList.MyActicitylist.get(i).commentnum;

				String sql = "insert into myactivity values('" + id + "','"
						+ title + "','" + img + "','" + cate + "','" + deadline
						+ "','" + time + "','" + pride + "','" + oppose + "','"
						+ comment + "')";
				dbHelper.excuteInfo(sql);
			}

			StaticList.MyActicitylist.clear();
			StaticList.MyActicitylistb.clear();
		}

		dbHelper.closeDB();
		
		
		new Thread(){
			
			public void run(){
				
				System.out.println("AAAAAAAAA");
				
				Date date=new Date();
				StaticString.exitTime=date.toString();
				
				SharedPreferences settings = getSharedPreferences("schooltime",MODE_PRIVATE);
				String uid = settings.getString("Id", "");
				
				String str=HttpUtil.sendGet(StaticString.server+ "logintime", "userid=" + uid+ "&logintime=" + StaticString.loginTime+"~"+StaticString.exitTime);
				
				System.out.println(str);
				
				System.out.println("BBBBBBBB");
				
			}
		}.start();
	}

	public void takepartactivity() {
		new Thread() {
			public void run() {
				SharedPreferences settings = getSharedPreferences("schooltime",
						MODE_PRIVATE);
				String userid = settings.getString("Id", "");
				String httpjson = HttpUtil.sendGet(StaticString.server
						+ "takepartinactivity", "userid=" + userid
						+ "&activityid=" + activityid + "&type=" + type);
				if (httpjson.equals("error")) {

				} else {

				}
			}
		}.start();
	}

	public void takepartoutactivity() {
		new Thread() {
			public void run() {
				SharedPreferences settings = getSharedPreferences("schooltime",
						MODE_PRIVATE);
				String userid = settings.getString("Id", "");
				String httpjson = HttpUtil.sendGet(StaticString.server
						+ "takepartoutactivity", "id=" + userid
						+ "&activityid=" + activityid + "&type=" + type);
				if (httpjson.equals("error")) {

				} else {

				}
			}
		}.start();
	}

	public void sendattachment() {
		new Thread() {
			public void run() {
				SharedPreferences settings = getSharedPreferences("schooltime",
						MODE_PRIVATE);
				String name = "";
				try {
					name = URLEncoder.encode(settings.getString("Name", ""),
							"UTF-8").toString();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String email = settings.getString("Email", "");
				String httpjson = HttpUtil.sendGet(StaticString.server
						+ "sendattachment", "activityid=" + activityid
						+ "&name=" + name + "&email=" + email);
				if (httpjson.equals("error")) {

				} else {

				}
			}
		}.start();
	}

	public void sendattachmentout() {
		new Thread() {
			public void run() {
				SharedPreferences settings = getSharedPreferences("schooltime",
						MODE_PRIVATE);
				String name = "";
				try {
					name = URLEncoder.encode(settings.getString("Name", ""),
							"UTF-8").toString();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String email = settings.getString("Email", "");
				String httpjson = HttpUtil.sendGet(StaticString.server
						+ "sendattachmentout", "activityid=" + activityid
						+ "&name=" + name + "&email=" + email);

				if (httpjson.equals("error")) {

				} else {

				}
			}
		}.start();
	}

	public void getback() {
		new Thread() {
			public void run() {
				SharedPreferences settings = getSharedPreferences("schooltime",MODE_PRIVATE);
				String name = "";
				try {
					name = URLEncoder.encode(settings.getString("Name", ""),
							"UTF-8").toString();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String email = settings.getString("Email", "");
				String pwd = settings.getString("Pwd", "");
				String httpjson = HttpUtil.sendGet(StaticString.server
						+ "getbackpwd", "pwd=" + pwd + "&name=" + name
						+ "&email=" + email);
				if (httpjson.equals("error")) {

				} else {

				}
			}
		}.start();
	}

	public void addcomment() {
		new Thread() {
			public void run() {
				SharedPreferences settings = getSharedPreferences("schooltime",
						MODE_PRIVATE);
				String userid = settings.getString("Id", "");
				String com = "";
				try {
					com = URLEncoder.encode(comment, "UTF-8").toString();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				HttpUtil.sendGet(StaticString.server + "addcomment",
						"activityid=" + activityid + "&userid=" + userid
								+ "&activitytype=" + activitytype + "&comment="
								+ com);

			}
		}.start();
	}

	public void getmyactivity() {

		dbhelper = new DBHelper(this);

		new Thread() {
			public void run() {

				SharedPreferences set = getSharedPreferences("schooltime",
						MODE_PRIVATE);
				String useridl = set.getString("Id", "");
				String stuidl = set.getString("StudentId", "");
				String schoolidl = set.getString("ShoolId", "");
				String jsonmyactivity = HttpUtil.sendGet(StaticString.server
						+ "getmyactivityid", "userid=" + useridl + "&stuid="
						+ stuidl + "&schoolid=" + schoolidl);

				if (jsonmyactivity.equals("error")) {
					return;
				} else {
					try {

						dbhelper.CreatOrOpen("schooltime");

						JSONArray myArray = new JSONArray(jsonmyactivity);
						int myA = myArray.length();

						for (int k = 0; k < myA; k++) {
							dbhelper.excuteInfo("insert into attendactivity values('"
									+ myArray.getString(k).toString() + "');");
						}
						dbhelper.closeDB();
						dbhelper = null;

					} catch (Exception e) {

					}
				}
			}
		}.start();
	}

}
