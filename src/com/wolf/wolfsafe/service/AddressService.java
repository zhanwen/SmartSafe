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
	 * ���������
	 */
	private WindowManager wm;

	/**
	 * ��������
	 */
	private TelephonyManager tm;

	private MyPhoneStateListener listener;
	
	private View view;

	private SharedPreferences sp;
	private long[] mHits = new long[2];
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
	
	private WindowManager.LayoutParams params;
	
	/**
	 * �Զ�����˾
	 * 
	 * @param address
	 */
	private void myToast(String address) {
		view = View.inflate(this, R.layout.address_show, null);
		TextView textView = (TextView) view.findViewById(R.id.tv_address);
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG,"�ؼ��������=======");
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if(mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					//˫��������
					params.x = wm.getDefaultDisplay().getWidth()/2 - view.getWidth()/2;
					wm.updateViewLayout(view, params);
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.commit();
				}
			}
		});
		
		//��View��������һ�������ļ�����
		view.setOnTouchListener(new OnTouchListener() {
			//������ָ�ĳ�ʼλ��
			int startX; 
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: //��ָ������Ļ 
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
//					Log.i(TAG,"��ʼλ�ã�" + startX + "," + startY);
					Log.i(TAG,"��ָ�����ؼ�");
					break;
				case MotionEvent.ACTION_MOVE: //��ָ����Ļ�ƶ�
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					Log.i(TAG,"�µ�λ�ã�" + newX + "," + newY);
					int dx = newX - startX;
					int dy = newY - startY;
//					Log.i(TAG, "��ָ��ƫ������" + dx + "," + dy);
//					Log.i(TAG, "����ImageView�Ĵ����λ�ã� ƫ����" + dx + "," + dy);
					Log.i(TAG,"��ָ�ڿؼ����ƶ�");
					params.x += dx;
					params.y += dy;
					//���Ǳ߽�����
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
					//���³�ʼ����ָ�Ŀ�ʼ����λ��
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					break;
				case MotionEvent.ACTION_UP: //��ָ�뿪��Ļһ˲��
					Log.i(TAG,"��ָ�뿪�ؼ�");
					//��¼�ؼ�������Ļ���Ͻǵ�����
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.putInt("lasty", params.y);
					editor.commit();
					
					break;
				}
				return false; //�¼���������ˣ���Ҫ�ø��ؼ�     ��������Ӧ�����¼���
			}
		});
		
		//"��͸��","������","��ʿ��","������","ƻ����"
		int[] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue,
				R.drawable.call_locate_gray,R.drawable.call_locate_green};
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		textView.setBackgroundResource(ids[sp.getInt("which", 0)]);
		textView.setText(address);
		
		//����Ĳ��������ú�
		params = new WindowManager.LayoutParams();
		
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		//androidϵͳ������е绰���ȼ���һ�ִ�������,�ǵ����Ȩ��
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		//�봰�����ϽǶ���
		params.gravity = Gravity.TOP + Gravity.LEFT;
		//ָ������������200���ϱ�100����
		params.x = sp.getInt("lastx", 0);
		params.y = sp.getInt("lasty", 0);
		
		
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
