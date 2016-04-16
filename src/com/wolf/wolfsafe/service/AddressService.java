package com.wolf.wolfsafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.wolf.wolfsafe.R;
import com.wolf.wolfsafe.db.dao.NumberAddressQueryUtils;

public class AddressService extends Service {

	private static final String TAG = "AddressService";

	private OutCallReceiver receiver;
	/**
	 * 窗体管理者
	 */
	private WindowManager wm;

	/**
	 * 监听来电
	 */
	private TelephonyManager tm;

	private MyPhoneStateListener listener;
	
	private View view;

	@Override
	public void onCreate() {
		super.onCreate();
		// 代码注册一个广播接收者
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);

		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// 监听来电
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// 实例化窗体
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);

	}

	/**
	 * 自定义土司
	 * 
	 * @param address
	 */
	private void myToast(String address) {
		view = View.inflate(this, R.layout.address_show, null);
		TextView textView = (TextView) view.findViewById(R.id.tv_address);
		//"半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int[] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue,
				R.drawable.call_locate_gray,R.drawable.call_locate_green};
		
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		textView.setBackgroundResource(ids[sp.getInt("which", 0)]);
		textView.setText(address);
		
		//窗体的参数就设置好
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;

		wm.addView(view, params);
	}

	// 在服务里定义一个广播接收者监听去电，内部类
	// 广播接收者的生命周期和服务一样
	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "内部类的广播接收者");
			// 得到打出去的电话号码,
			String phone = getResultData();
			// 得到号码查询号码归属地
			String address = NumberAddressQueryUtils.queryNumber(phone);
			// Toast.makeText(context, address, 1).show();
			myToast(address);
		}

	}

	private class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// 第一个参数是状态，第二个参数是电话号码
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING: // 铃声响起的时候,其实也是来电的时候
				// 根据得到的电话号码，查询它的归属地，并显示在土司上面
				String address = NumberAddressQueryUtils
						.queryNumber(incomingNumber);
				// Toast.makeText(getApplicationContext(), address ,2).show();
				myToast(address);
				break;

			case TelephonyManager.CALL_STATE_IDLE:  //电话的空闲状态：挂断电话、来电拒绝了。
				//把这个view移除
				if(view != null) {
					wm.removeView(view);
				}
				break;
				
			default:
				break;
			}

		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 代码取消注册一个广播接收者
		unregisterReceiver(receiver);
		receiver = null;

		// 取消监听来电
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}

}
