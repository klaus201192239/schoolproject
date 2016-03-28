package com.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import com.activity.NoticeDetail;
import com.activity.R;
import com.activity.UpdateSystems;
import com.dbutil.DBHelper;
import com.http.HttpUtil;
import com.pagebean.NoticeBean;
import com.pagebean.TeamAll;
import com.pagebean.TeamMemberAll;
import com.staticdata.StaticInt;
import com.staticdata.StaticString;

public class ListenerService extends Service {

	private DBHelper dbhelper;

	private List<NoticeBean> list = new ArrayList<NoticeBean>();
	private List<NoticeBean> listTT = new ArrayList<NoticeBean>();
	private List<TeamAll> listTeam = new ArrayList<TeamAll>();
	private List<TeamMemberAll> listMember = new ArrayList<TeamMemberAll>();

	private NotificationManager manager;
	private NotificationCompat.Builder notifyBuilder;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub		
		timer.schedule(UpdateNotice, 1000, 120000); // 1s��ִ��task,����120s�ٴ�ִ��
		timer.schedule(UpdateTeam, 10000, 1200000); // 1s��ִ��task,����20�����ٴ�ִ��
		getNewVersion();
		//super.onCreate();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return super.onStartCommand(intent, flags, startId);

	}

	private Handler handler = new Handler(new Handler.Callback() {
		@SuppressLint("SimpleDateFormat")
		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {
			case 1:

				SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
				int noticeFirst= set.getInt("NoticeFirst",-1);
				if(noticeFirst==-1){
					SharedPreferences.Editor editor = set.edit();
					editor.putInt("NoticeFirst", 1);
					editor.commit();
				}else{
					for (int i = 0; i < list.size(); i++) {
						NoticeBean bean = list.get(i);
						Intent intent = new Intent(ListenerService.this,NoticeDetail.class);
						intent.putExtra("ServiceFrom", 1);
						intent.putExtra("title", bean.title);
						intent.putExtra("content", bean.content);
						intent.putExtra("publisher", bean.publisher);

						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						intent.putExtra("time",format.format(new Date()));
						
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//�ؼ���һ������������ģʽ
						if(bean.content.length()>10){
							showNotice(R.drawable.icon1, bean.title,bean.content.substring(0, 10), intent);
						}else{
							showNotice(R.drawable.icon1, bean.title,bean.content, intent);
						}
						
					}
				}
				
				list.clear();
				break;
			case 2:

				SharedPreferences sett = getSharedPreferences("schooltime", MODE_PRIVATE);
				int teamFirst= sett.getInt("TeamFirst",-1);
				if(teamFirst==-1){
					SharedPreferences.Editor editorr = sett.edit();
					editorr.putInt("TeamFirst", 1);
					editorr.commit();
				}else{
					for (int i = 0; i < listTT.size(); i++) {
						NoticeBean bean = listTT.get(i);
						Intent intent = new Intent(ListenerService.this,NoticeDetail.class);
						intent.putExtra("ServiceFrom", 1);
						intent.putExtra("title", bean.title);
						intent.putExtra("content", bean.content);
						intent.putExtra("publisher", bean.publisher);

						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						intent.putExtra("time",format.format(new Date()));
						
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//�ؼ���һ������������ģʽ
						if(bean.content.length()>10){
							showNotice(R.drawable.icon1, bean.title,bean.content.substring(0, 10), intent);
						}else{
							showNotice(R.drawable.icon1, bean.title,bean.content, intent);
						}
						
					}
				}

				listTT.clear();
				
				break;
			case 3:
				Intent intent = new Intent(ListenerService.this,UpdateSystems.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//�ؼ���һ������������ģʽ
				showNotice(R.drawable.icon1, "�������","Ϊ�˸��õط�������������N�η������", intent);
				break;
			default:
				break;
			}

			return false;
		}
	});

	Timer timer = new Timer();

	TimerTask UpdateNotice = new TimerTask() {
		@Override
		public void run() {

			SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
			String userid = set.getString("Id","");
			
			String cid0="0" , cid1="0" ;
			
			dbhelper = new DBHelper(ListenerService.this);
			
			dbhelper.CreatOrOpen("schooltime");
			
			Cursor cur = dbhelper.selectInfo("select max(cid) from notice where type=1;");//У��֪ͨѧ��
			while (cur.moveToNext()) {
				
				cid0 = cur.getString(0);
				
				if(cid0==null){
					cid0="0";
				}
			}
			Cursor curcur = dbhelper.selectInfo("select max(cid) from notice where type=2;");//ϵͳ֪ͨѧ��
			while (curcur.moveToNext()) {

				cid1 = curcur.getString(0);
				
				if(cid1==null){
					cid1="0";
				}
			}
			dbhelper.closeDB();
		
			String httpjson = HttpUtil.sendGet(StaticString.server+ "getschoolnotice", "userid=" + userid + "&currentid="+ cid0);
			
			if (httpjson.equals("error")) {

			} else {
				try {
					JSONArray array = new JSONArray(httpjson);
					int len = array.length();
					for (int i = 0; i < len; i++) {
						NoticeBean bean = new NoticeBean();
						JSONObject obj = array.getJSONObject(i);
						bean.cid = obj.getString("_id");
						bean.content = obj.getString("Content");
						bean.publisher = obj.getString("OrganizationName");
						bean.time = obj.getString("ReleaseTime");
						bean.title = obj.getString("Title");
						bean.type = 1;
						list.add(bean);
					}
				} catch (Exception e) {

				}
			}
			httpjson = HttpUtil.sendGet(StaticString.server + "getsystemnotice", "userid=" + userid+ "&currentid=" + cid1);
			
			if (httpjson.equals("error")) {

			} else {
				try {
					JSONArray array = new JSONArray(httpjson);
					int len = array.length();
					for (int i = 0; i < len; i++) {
						NoticeBean bean = new NoticeBean();
						JSONObject obj = array.getJSONObject(i);
						bean.cid = obj.getString("_id");
						bean.content = obj.getString("Content");
						bean.publisher = obj.getString("Publisher");
						bean.time = obj.getString("ReleaseTime");
						bean.title = obj.getString("Title");
						bean.type = 2;
						list.add(bean);
					}
				} catch (Exception e) {

				}
			}
			if (list.size() != 0) {
				
				dbhelper.CreatOrOpen("schooltime");
				for (int i = 0; i < list.size(); i++) {
					NoticeBean bean = list.get(i);
					dbhelper.excuteInfo("insert into notice values(null,'"+bean.title+"','" + bean.publisher + "','"+ bean.content + "','" + bean.time + "','"+ bean.cid + "','" + bean.type + "')");
				}
				dbhelper.closeDB();

				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		}
	};



	TimerTask UpdateTeam = new TimerTask() {

		@Override
		public void run() {
			// ��Ҫ������:������Ϣ

			SharedPreferences set = getSharedPreferences("schooltime", MODE_PRIVATE);
			String userid = set.getString("Id","");
			String idcard=set.getString("StudentId", "");
			String schoolid=set.getString("ShoolId", "");
						
			String httpjson = HttpUtil.sendGet(StaticString.server+ "getupdateteam", "userid=" + userid + "&idcard="+ idcard+"&schoolid="+schoolid);
			
			int tag=0;
			
			if(httpjson.equals("error")){
				
			}else{
				
				try{
					
					JSONArray array=new JSONArray(httpjson);
					
					for(int i=0;i<array.length();i++){
						
						JSONObject obj=array.getJSONObject(i);
						
						TeamAll team=new TeamAll();
						team._id=obj.getString("_id");
						team.LeaderName=obj.getString("LeaderName");
						team.Name=obj.getString("Name");
						team.ActivityName=obj.getString("ActivityName");
						team.TeamLeader=obj.getString("TeamLeader");
						team.IdCard=obj.getString("IdCard");
						
						
						JSONArray arrayMem=obj.getJSONArray("Member");
						for(int j=0;j<arrayMem.length();j++){
							
							JSONObject objMem=arrayMem.getJSONObject(j);
							TeamMemberAll member=new TeamMemberAll();
							
							member.id=obj.getString("_id");
							member.Degree=objMem.getInt("Degree");
							member.Grade=objMem.getInt("Grade");							
							member.MajorName=objMem.getString("MajorName");
							member.Name=objMem.getString("Name");
							member.Phone=objMem.getString("Phone");
							member.State=objMem.getInt("State");
							member.StuId=objMem.getString("StuId");
							member.Abstract=objMem.getString("Abstract");
							member.IdCard=objMem.getString("IdCard");
							SimpleDateFormat sfStart = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",java.util.Locale.ENGLISH);
							member.NowTeam = sfStart.parse(objMem.getString("NowTime"));
							listMember.add(member);
						}
						listTeam.add(team);
					}	
					
					tag=1;
					
				}catch(Exception e){
					tag=0;
				}			
				
				if(tag==1&&listTeam.size()>0){
					
					dbhelper=new DBHelper(ListenerService.this);
					dbhelper.CreatOrOpen("schooltime");
					
					for(int i=0;i<listTeam.size();i++){
					
						String idd=null;
						Cursor cur=dbhelper.selectInfo("select id from myteam where id='"+listTeam.get(i)._id+"';");
						while(cur.moveToNext()){						
							idd=cur.getString(0);
						}
						
						if(idd==null){
							NoticeBean bean = new NoticeBean();
							bean.publisher="У԰ʱ��ٷ�֪ͨ";
		
							if(userid.equals(listTeam.get(i).TeamLeader)||idcard.equals(listTeam.get(i).IdCard)){
								bean.title="�Ŷ��ѳɹ�����";
								bean.content="���ã��������Ŷӣ�"+listTeam.get(i).Name+" �Ѿ��ɹ������ˣ��뼰ʱ��<�ҵ��Ŷ�>�������У��������Ŷӽ��й���ף���ˣ�";
							}else{
								bean.title="���ѳ�Ϊ�Ŷ�һԱ";
								bean.content="���ã����Ѿ��ɹ������Ŷӣ�"+listTeam.get(i).Name+"��������<�ҵ��Ŷ�>�������У��鿴���Ķ�����Ϣ��ף���ˣ�";
							}
							
							listTT.add(bean);
							
						}else{
							for(int j=0;j<listMember.size();j++){
								
								if(listMember.get(j).id.equals(idd)){
									
									Cursor cur1=dbhelper.selectInfo("select teamid,state from teammember where teamid='"+idd+"' and userid='"+listMember.get(j).StuId+"' and idcard='"+listMember.get(j).IdCard+"';");
									
									String sre=null;
									int sta=-1;
									while(cur1.moveToNext()){

										sre=cur1.getString(0);
										sta=cur1.getInt(1);
									}
									if(sre==null){
										NoticeBean bean = new NoticeBean();
										bean.publisher="У԰ʱ��ٷ�֪ͨ";
										bean.title="�Ŷ����³�Ա����";
										bean.content="���ã�"+listTeam.get(i).Name+"�Ŷ����³�Ա���룬������<�ҵ��Ŷ�>�������У��鿴���Ķ�����Ϣ��ף���ˣ�";
										listTT.add(bean);
									}else{																
										if(sta!=-1&&(listMember.get(j).State!=sta)){	

											NoticeBean bean = new NoticeBean();
											bean.publisher="У԰ʱ��ٷ�֪ͨ";
											bean.title="�ŶԳ�Ա״̬�仯";
											bean.content="���ã�"+listTeam.get(i).Name+"�Ŷ��еĳ�Ա��"+listMember.get(j).Name+"�Ѿ����ͨ��������ʽ��Ϊ����֮�е�һԱ��������<�ҵ��Ŷ�>�������У��鿴���Ķ�����Ϣ��ף���ˣ�";
											listTT.add(bean);
										}
									}
								}
							}
						}
					}

					///�鿴���ݿ��еĳ�Ա�Ƿ���Array�У����ڵĻ�����ô����
					
					String sql="select id,name,leadername,activityname from myteam where ";
					for(int k=0;k<listTeam.size();k++){
						sql=sql+"id<>'"+listTeam.get(k)._id+"' and ";
					}
					sql=sql.substring(0,sql.lastIndexOf("and"));
					Cursor cur=dbhelper.selectInfo(sql);
					while(cur.moveToNext()){
						String id=cur.getString(0);
						if(id!=null){
							NoticeBean bean = new NoticeBean();
							bean.publisher="У԰ʱ��ٷ�֪ͨ";
							bean.title="�ŶԽ�ɢ֪ͨ";
							bean.content="���ã���"+cur.getString(2)+"�����"+cur.getString(1)+"�Ŷ�,�Ѿ�����ɢ�����������μӻ���뼰ʱ�齨�����Ŷӣ�ף����~!";
							listTT.add(bean);
						}
					}
					
					sql="select id,name,leadername,activityname from myteam where ";
					for(int k=0;k<listTeam.size();k++){
						sql=sql+"id='"+listTeam.get(k)._id+"' or ";
					}
					sql=sql.substring(0,sql.lastIndexOf("or"));					
					cur=dbhelper.selectInfo(sql);
					while(cur.moveToNext()){
						String id=cur.getString(0);
						if(id!=null){
							String memSql="select name,major,degree,grade,phone from teammember where teamid='"+id+"' and ";
							for(int h=0;h<listMember.size();h++){
								if(listMember.get(h).id.equals(id)){
									
									if(listMember.get(h).StuId.equals("000000000000000000000000")){
										memSql=memSql+"idcard<>'"+listMember.get(h).IdCard+"' and ";										
									}
									else{
										memSql=memSql+"userid<>'"+listMember.get(h).StuId+"' and ";
									}
									
								}
							}
							memSql=memSql.substring(0,memSql.lastIndexOf("and"));							
							Cursor curMember=dbhelper.selectInfo(memSql);
							while(curMember.moveToNext()){
								NoticeBean bean = new NoticeBean();
								bean.publisher="У԰ʱ��ٷ�֪ͨ";
								bean.title="�ŶԳ�Ա�˳�֪ͨ";
								bean.content="���ã��Ŷӳ�Ա"+curMember.getString(1)+"��"+curMember.getString(0)+"("+curMember.getString(3)+" "+curMember.getInt(2)+")"+"�Ѿ��˳�"+cur.getString(1)+"�Ŷ�";
								listTT.add(bean);
							}
						}
					}
					
				}	
			}
			
			
			if (listTeam.size() != 0) {

				dbhelper.CreatOrOpen("schooltime");
				
				dbhelper.excuteInfo("delete from myteam;");
				
				for (int i = 0; i < listTeam.size(); i++) {
					TeamAll bean = listTeam.get(i);
					dbhelper.excuteInfo("insert into myteam values('"+bean._id+"','"+bean.Name+"','" + bean.TeamLeader + "','"+ bean.IdCard + "','" + bean.LeaderName + "','"+ bean.ActivityName+ "');");
				}

				dbhelper.excuteInfo("delete from teammember;");
				
				Calendar ca=Calendar.getInstance();
				ca.add(Calendar.DAY_OF_YEAR, -6);
				
				for (int i = 0; i < listMember.size(); i++) {
					TeamMemberAll bean = listMember.get(i);
					
					if(bean.State==0){
						Date date=bean.NowTeam;
						if(ca.getTime().after(date)){
							
							HttpUtil.sendGet(StaticString.server+ "teammanager","teamid="+bean.id+"&userid="+bean.StuId+"&type="+0+"&schoolid="+schoolid+"&idcard="+bean.IdCard);
							
							NoticeBean beann = new NoticeBean();
							beann.publisher="У԰ʱ��ٷ�֪ͨ";
							beann.title="�ŶԳ�Ա�˳�֪ͨ";
							beann.content="���ã������Ŷӳ�Ա"+bean.Name+"��ʱ��δ��ˣ��ö�Ա�Ѿ����Զ��޳��Ŷӣ��ش�֪ͨ��";
							listTT.add(beann);
						}
						else{
							dbhelper.excuteInfo("insert into teammember values('"+bean.id+"','"+bean.StuId+"','" + bean.IdCard + "','"+ bean.Name + "','" + bean.MajorName + "','"+ bean.Degree+ "','"+bean.Grade+"','"+bean.Phone+"','"+bean.Abstract+"','"+bean.State+"');");
						}
					}
					else{
						dbhelper.excuteInfo("insert into teammember values('"+bean.id+"','"+bean.StuId+"','" + bean.IdCard + "','"+ bean.Name + "','" + bean.MajorName + "','"+ bean.Degree+ "','"+bean.Grade+"','"+bean.Phone+"','"+bean.Abstract+"','"+bean.State+"');");
					}
					
				}
				
				dbhelper.closeDB();
			}
			
			listTeam.clear();
			listMember.clear();

			if (listTT.size() != 0) {
				
				dbhelper.CreatOrOpen("schooltime");
				
				Date da=new Date();
				
				for (int i = 0; i < listTT.size(); i++) {
					NoticeBean bean = listTT.get(i);
					dbhelper.excuteInfo("insert into notice values(null,'"+bean.title+"','" + bean.publisher + "','"+ bean.content + "','" + da.toString() + "','nothing',3)");
				}
				dbhelper.closeDB();

				Message message = new Message();
				message.what = 2;
				handler.sendMessage(message);
			}
			
		}
	};

	
	TimerTask taskA = new TimerTask() {

		@Override
		public void run() {
			// ��Ҫ������:������Ϣ
			Message message = new Message();
			message.what = 2;
			handler.sendMessage(message);
		}
	};
	
	public void showNotice(int icon, String title, String content, Intent intent) {

		manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// �������ͼACTION����ת��Intent
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(this,StaticInt.NoticeId,intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), icon);

		notifyBuilder = new NotificationCompat.Builder(this)
		/* ����large icon */
		.setLargeIcon(bitmap)
		/* ����small icon */
		.setSmallIcon(icon)
		/* ����title */
		.setContentTitle(title)
		/* ������ϸ�ı� */
		.setContentText(content)
		/* ���÷���֪ͨ��ʱ��Ϊ����֪ͨʱ��ϵͳʱ�� */
		.setWhen(System.currentTimeMillis())
		/* ���÷���֪ͨʱ��status bar�������� */
		// .setTicker("�������µ�ף��")
				/*
				 * setOngoing(boolean)��Ϊtrue,notification���޷�ͨ�����һ����ķ�ʽ��� *
				 * ��������ӳ�פ֪ͨ���������cancle���������
				 */
				//.setOngoing(true)
				/* ���õ����֪ͨ��ʧ */
				.setAutoCancel(true)
				/* ����֪ͨ��������ʾ������QQ���֣�����ͬ־�ĺϲ� */
				// .setNumber(2)
				.setDefaults(Notification.DEFAULT_ALL)
				/* �����ת��MainActivity */
				.setContentIntent(pendingIntent);

		manager.notify(StaticInt.NoticeId, notifyBuilder.build());
		
		StaticInt.NoticeId++;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void getNewVersion(){
		
		new Thread(){
			public void run(){
				
				String httpjson = HttpUtil.sendGet(StaticString.server+ "getversion", "userid=str");
				
				if(httpjson.equals("error")){
					
				}else{
					try{
						JSONObject jsonObject=new JSONObject(httpjson);
					
						if(getVersion().equals(jsonObject.getString("Version"))){
							
						}else{
							SharedPreferences setting = getSharedPreferences("schooltime", MODE_PRIVATE); 
							SharedPreferences.Editor editor = setting.edit();
							editor.putString("Version", jsonObject.getString("Version"));
							editor.putString("VersionGood",jsonObject.getString("Goodness"));
							editor.putString("DownloadUrl",jsonObject.getString("DownloadUrl"));
							editor.commit();	
							Message message = new Message();
							message.what = 3;
							handler.sendMessage(message);
						}
				
					}catch(Exception e){
						
					}
					
				}
				
				
			}
		}.start();
	}
	public String getVersion() {
		String ver="error";
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			ver = info.versionName;
		} catch (Exception e) {
			
		}
		return ver;
	}
}

