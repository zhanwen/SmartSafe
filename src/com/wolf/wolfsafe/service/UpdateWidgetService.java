package com.wolf.wolfsafe.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.wolf.wolfsafe.R;
import com.wolf.wolfsafe.receiver.MyWidget;
import com.wolf.wolfsafe.utils.SystemInfoUtils;

public class UpdateWidgetService extends Service {

	protected static final String TAG = "UpdateWidgetService";
	private Timer timer;
	private TimerTask task;
	/**
	 * widget的管理器
	 */
	private AppWidgetManager awm;

	private ScreenOffReceiver offreceiver;
	
	private ScreenOnReceiver onreceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		offreceiver = new ScreenOffReceiver();
		onreceiver = new ScreenOnReceiver();
		registerReceiver(onreceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		registerReceiver(offreceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		
		awm = AppWidgetManager.getInstance(this);
		
		startTimer();
		
		super.onCreate();
	}

	private void startTimer() {
		if(timer == null && task == null) {
		
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				Log.i(TAG,"更新widget");
				//设置更新的组件
				ComponentName provider = new ComponentName(UpdateWidgetService.this, MyWidget.class);
				RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
				
				views.setTextViewText(R.id.process_count, "正在运行的进程：" + SystemInfoUtils.getRunningProcessCount(getApplicationContext()) + "个");
				views.setTextViewText(R.id.process_memory, "可用内存:" + Formatter.formatFileSize(getApplicationContext(), SystemInfoUtils.getAvailMem(getApplicationContext())));
				
				//描述一个动作，这个动作由另外一个应用程序执行的
				//自定义一个广播事件，杀死后台进度的事件
				Intent intent = new Intent();
				intent.setAction("com.wolf.wolfsafe.killall");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				
				awm.updateAppWidget(provider, views);
			}
		};
		timer.schedule(task, 0, 3000);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(offreceiver);
		unregisterReceiver(onreceiver);
		offreceiver = null;
		onreceiver = null;
		stopTimer();
	}

	private void stopTimer() {
		if(timer != null && task != null) {
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
	}
	
	
	private class ScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG,"屏幕锁屏了");
			stopTimer();
			}
		}
	
	private class ScreenOnReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG,"屏幕解锁了");
			startTimer();
			}
		}

}
