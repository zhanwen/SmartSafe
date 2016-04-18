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
			Log.i(TAG,"内部广播接收者,短信到来了");
			//检查发件人是否是黑名单号码，设置短信拦截全部拦截
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for(Object obj : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])obj);
				//得到短信发件人
				String sender = smsMessage.getOriginatingAddress();
				String result = dao.findMode(sender);
				if("2".equals(result) || "3".equals(result)) {
					Log.i(TAG,"拦截短信");
					abortBroadcast();
				}
				String body = smsMessage.getMessageBody();
				//演示代码
				if(body.contains("fabiao")) {
					//你的头发票亮的很，语言分词技术
					Log.i(TAG,"拦截发票短信");
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
