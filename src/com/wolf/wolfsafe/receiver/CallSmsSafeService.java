package com.wolf.wolfsafe.receiver;

import com.wolf.wolfsafe.db.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

public class CallSmsSafeService extends Service {

	private static final String TAG = "CallSmsSafeService";
	private InnerSmsReceiver receiver;
	private BlackNumberDao dao;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	
	private class InnerSmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG,"�ڲ��㲥������,���ŵ�����");
			//��鷢�����Ƿ��Ǻ��������룬���ö�������ȫ������
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for(Object obj : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])obj);
				//�õ����ŷ�����
				String sender = smsMessage.getOriginatingAddress();
				String result = dao.findMode(sender);
				if("2".equals(result) || "3".equals(result)) {
					Log.i(TAG,"���ض���");
					abortBroadcast();
				}
				String body = smsMessage.getMessageBody();
				//��ʾ����
				if(body.contains("fabiao")) {
					//���ͷ��Ʊ���ĺܣ����Էִʼ���
					Log.i(TAG,"���ط�Ʊ����");
					abortBroadcast();
				}
			}
		}
	}
	
	@Override
	public void onCreate() {
		dao = new BlackNumberDao(this);
		receiver = new InnerSmsReceiver();
		IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, filter);
		
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}
	
	
	
	
}
