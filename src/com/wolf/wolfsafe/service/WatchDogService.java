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
 * 看门狗代码 监视系统程序的运行状态
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
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public class InnerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("接收到了临时停止保护的广播事件");
			
			tempStopProtectPackname = intent.getStringExtra("packname");
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
		flag = true;
		new Thread() {
			
			@Override
			public void run() {
				while(flag) {
					List<RunningTaskInfo> infos = am.getRunningTasks(100);
					String packname = infos.get(0).topActivity.getPackageName();
					System.out.println("当前用户操作的应用程序:" + packname); //培训好了
					if(dao.find(packname)) {
						//判断这个应用程序是否需要临时停止保护
						if(packname.equals(tempStopProtectPackname)) {
							
						}else {
							//当前应用需要保护，蹦出来，弹出来一个输入密码的界面 
							Intent intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
							//服务是没有任务栈信息的，在服务开启activity，要指定这个activity运行的任务栈
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							//设置要保护的包名 
							intent.putExtra("packname", packname);
							startActivity(intent);
						}
						
					}
					try {
						Thread.sleep(50);
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
		innerReceiver = null;
		offreceiver = null;
		super.onDestroy();
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			tempStopProtectPackname = null;
			}
		}

}
