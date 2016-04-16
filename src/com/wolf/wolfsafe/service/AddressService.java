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
	 * ���������
	 */
	private WindowManager wm;

	/**
	 * ��������
	 */
	private TelephonyManager tm;

	private MyPhoneStateListener listener;
	
	private View view;

	@Override
	public void onCreate() {
		super.onCreate();
		// ����ע��һ���㲥������
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);

		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// ��������
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// ʵ��������
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);

	}

	/**
	 * �Զ�����˾
	 * 
	 * @param address
	 */
	private void myToast(String address) {
		view = View.inflate(this, R.layout.address_show, null);
		TextView textView = (TextView) view.findViewById(R.id.tv_address);
		//"��͸��","������","��ʿ��","������","ƻ����"
		int[] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue,
				R.drawable.call_locate_gray,R.drawable.call_locate_green};
		
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		textView.setBackgroundResource(ids[sp.getInt("which", 0)]);
		textView.setText(address);
		
		//����Ĳ��������ú�
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

	// �ڷ����ﶨ��һ���㲥�����߼���ȥ�磬�ڲ���
	// �㲥�����ߵ��������ںͷ���һ��
	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "�ڲ���Ĺ㲥������");
			// �õ����ȥ�ĵ绰����,
			String phone = getResultData();
			// �õ������ѯ���������
			String address = NumberAddressQueryUtils.queryNumber(phone);
			// Toast.makeText(context, address, 1).show();
			myToast(address);
		}

	}

	private class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// ��һ��������״̬���ڶ��������ǵ绰����
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING: // ���������ʱ��,��ʵҲ�������ʱ��
				// ���ݵõ��ĵ绰���룬��ѯ���Ĺ����أ�����ʾ����˾����
				String address = NumberAddressQueryUtils
						.queryNumber(incomingNumber);
				// Toast.makeText(getApplicationContext(), address ,2).show();
				myToast(address);
				break;

			case TelephonyManager.CALL_STATE_IDLE:  //�绰�Ŀ���״̬���Ҷϵ绰������ܾ��ˡ�
				//�����view�Ƴ�
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
		// ����ȡ��ע��һ���㲥������
		unregisterReceiver(receiver);
		receiver = null;

		// ȡ����������
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}

}
