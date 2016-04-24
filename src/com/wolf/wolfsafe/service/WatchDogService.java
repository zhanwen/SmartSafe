package com.wolf.wolfsafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.wolf.wolfsafe.EnterPwdActivity;
import com.wolf.wolfsafe.db.dao.ApplockDao;

/**
 * ���Ź����� ����ϵͳ���������״̬
 * @author Hanwen
 *
 */
public class WatchDogService extends Service {
	
	private ActivityManager am;

	private boolean flag;
	
	private ApplockDao dao;
	
	private InnerReceiver innerReceiver;
	
	private String tempStopProtectPackname;
	
	private ScreenOffReceiver offreceiver;
	
	private List<String> protectPacknames;
	
	private Intent intent;
	
	private DataChangeReceiver dataChangeReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private class InnerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("���յ�����ʱֹͣ�����Ĺ㲥�¼�");
			
			tempStopProtectPackname = intent.getStringExtra("packname");
		}
	}
	
	private class DataChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("���ݿ�����ݱ仯��");
			protectPacknames = dao.findAll();
		}
	}
	
	
	
	
	
	
	
	@Override
	public void onCreate() {
		offreceiver = new ScreenOffReceiver();
		registerReceiver(offreceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new ApplockDao(this);
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, new IntentFilter("com.wolf.wolfsafe.tempstop"));
		
		dataChangeReceiver = new DataChangeReceiver();
		registerReceiver(dataChangeReceiver, new IntentFilter("com.wolf.wolfsafe.applockchange"));
		
		protectPacknames = dao.findAll();
		flag = true;
		
		intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
		//������û������ջ��Ϣ�ģ��ڷ�����activity��Ҫָ�����activity���е�����ջ
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		new Thread() {
			
			@Override
			public void run() {
				while(flag) {
					List<RunningTaskInfo> infos = am.getRunningTasks(1);
					String packname = infos.get(0).topActivity.getPackageName();
//					System.out.println("��ǰ�û�������Ӧ�ó���:" + packname); //��ѵ����
					//if(dao.find(packname)) { //��ѯ���ݿ�̫���ˣ�������Դ���ĳɲ�ѯ�ڴ�
					if(protectPacknames.contains(packname)) {//��ѯ�ڴ�Ч�ʸߺܶ�
						//�ж����Ӧ�ó����Ƿ���Ҫ��ʱֹͣ����
						if(packname.equals(tempStopProtectPackname)) {
							
						}else {
							//��ǰӦ����Ҫ�������ĳ�����������һ����������Ľ��� 
							//����Ҫ�����İ��� 
							intent.putExtra("packname", packname);
							startActivity(intent);
						}
						
					}
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		
		super.onCreate();
	}
	
	
	@Override
	public void onDestroy() {
		flag = false;
		unregisterReceiver(innerReceiver);
		unregisterReceiver(offreceiver);
		unregisterReceiver(dataChangeReceiver);
		
		innerReceiver = null;
		offreceiver = null;
		dataChangeReceiver = null;
		
		super.onDestroy();
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			tempStopProtectPackname = null;
			}
		}

}
