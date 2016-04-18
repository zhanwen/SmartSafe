package com.wolf.wolfsafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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

	private SharedPreferences sp;
	private long[] mHits = new long[2];
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
	
	private WindowManager.LayoutParams params;
	
	/**
	 * 自定义土司
	 * 
	 * @param address
	 */
	private void myToast(String address) {
		view = View.inflate(this, R.layout.address_show, null);
		TextView textView = (TextView) view.findViewById(R.id.tv_address);
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG,"控件被点击了=======");
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if(mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					//双击居中了
					params.x = wm.getDefaultDisplay().getWidth()/2 - view.getWidth()/2;
					wm.updateViewLayout(view, params);
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.commit();
				}
			}
		});
		
		//给View对象设置一个触摸的监听器
		view.setOnTouchListener(new OnTouchListener() {
			//定义手指的初始位置
			int startX; 
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: //手指按下屏幕 
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
//					Log.i(TAG,"开始位置：" + startX + "," + startY);
					Log.i(TAG,"手指摸到控件");
					break;
				case MotionEvent.ACTION_MOVE: //手指在屏幕移动
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					Log.i(TAG,"新的位置：" + newX + "," + newY);
					int dx = newX - startX;
					int dy = newY - startY;
//					Log.i(TAG, "手指的偏移量：" + dx + "," + dy);
//					Log.i(TAG, "更新ImageView的窗体的位置， 偏移量" + dx + "," + dy);
					Log.i(TAG,"手指在控件上移动");
					params.x += dx;
					params.y += dy;
					//考虑边界问题
					if(params.x < 0) {
						params.x = 0;
					}
					if(params.y < 0) {
						params.y = 0;
					}
					if(params.x > wm.getDefaultDisplay().getWidth() - view.getWidth()) {
						params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
					}
					if(params.y > wm.getDefaultDisplay().getHeight() - view.getHeight()) {
						params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
					}
					wm.updateViewLayout(view, params);
					//重新初始化手指的开始结束位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					break;
				case MotionEvent.ACTION_UP: //手指离开屏幕一瞬间
					Log.i(TAG,"手指离开控件");
					//记录控件距离屏幕左上角的坐标
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.putInt("lasty", params.y);
					editor.commit();
					
					break;
				}
				return false; //事件处理完毕了，不要让父控件     父布局响应触摸事件了
			}
		});
		
		//"半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int[] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue,
				R.drawable.call_locate_gray,R.drawable.call_locate_green};
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		textView.setBackgroundResource(ids[sp.getInt("which", 0)]);
		textView.setText(address);
		
		//窗体的参数就设置好
		params = new WindowManager.LayoutParams();
		
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		//android系统里面具有电话优先级的一种窗体类型,记得添加权限
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		//与窗体左上角对齐
		params.gravity = Gravity.TOP + Gravity.LEFT;
		//指定窗体距离左边200，上边100像素
		params.x = sp.getInt("lastx", 0);
		params.y = sp.getInt("lasty", 0);
		
		
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
