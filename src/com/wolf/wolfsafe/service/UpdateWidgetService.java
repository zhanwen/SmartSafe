package com.wolf.wolfsafe.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
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
	 * widget�Ĺ�����
	 */
	private AppWidgetManager awm;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		awm = AppWidgetManager.getInstance(this);
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				Log.i(TAG,"����widget");
				//���ø��µ����
				ComponentName provider = new ComponentName(UpdateWidgetService.this, MyWidget.class);
				RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
				
				views.setTextViewText(R.id.process_count, "�������еĽ��̣�" + SystemInfoUtils.getRunningProcessCount(getApplicationContext()) + "��");
				views.setTextViewText(R.id.process_memory, "�����ڴ�:" + Formatter.formatFileSize(getApplicationContext(), SystemInfoUtils.getAvailMem(getApplicationContext())));
				
				//����һ���������������������һ��Ӧ�ó���ִ�е�
				//�Զ���һ���㲥�¼���ɱ����̨���ȵ��¼�
				Intent intent = new Intent();
				intent.setAction("com.wolf.wolfsafe.killall");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				
				awm.updateAppWidget(provider, views);
			}
		};
		timer.schedule(task, 0, 3000);
		
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		timer.cancel();
		task.cancel();
		timer = null;
		task = null;

	}

}
