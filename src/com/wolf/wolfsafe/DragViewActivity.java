package com.wolf.wolfsafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class DragViewActivity extends Activity {
	protected static final String TAG = "DragViewActivity";
	private ImageView iv_drag;
	private SharedPreferences sp;
	private WindowManager wm;
	private int windowWidth;
	private int windowHeight;
	private TextView tv_bottom;
	private TextView tv_top;
	long[] mHits = new long[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_view);
		tv_bottom = (TextView) findViewById(R.id.tv_bottom);
		tv_top = (TextView) findViewById(R.id.tv_top);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		windowWidth = wm.getDefaultDisplay().getWidth();
		windowHeight = wm.getDefaultDisplay().getHeight();
		int lastx = sp.getInt("lastx", 0);
		int lasty = sp.getInt("lasty", 0);

		if (lasty > windowHeight / 2) {
			tv_top.setVisibility(View.VISIBLE);
			tv_bottom.setVisibility(View.INVISIBLE);
		} else {
			tv_top.setVisibility(View.INVISIBLE);
			tv_bottom.setVisibility(View.VISIBLE);
		}

		Log.i(TAG, "��һ���϶��� ��������Ļ���棺" + lasty);
		Log.i(TAG, "��һ���϶��� ��������Ļ���棺" + lastx);

		iv_drag = (ImageView) findViewById(R.id.iv_drag);
		// ����һ�ε�λ�����õ�����ؼ��ϡ�
		// ��view������Ⱦ�ĵڶ����׶βŻ���Ч����һ���׶��ǲ���
		// System.out.println("��"+iv_drag.getWidth());
		// iv_drag.layout(lastx, lasty, lastx+iv_drag.getWidth(),
		// lasty+iv_drag.getHeight());
		// �õ�һ���׶ξ�����Ч��api
		// ��ȡimageview�Ĳ���
		LayoutParams params = (LayoutParams) iv_drag.getLayoutParams();
		params.leftMargin = lastx;
		params.topMargin = lasty;
		iv_drag.setLayoutParams(params);

		iv_drag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ʶ�����������ε��
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					Log.i(TAG, "˫���ˡ��������̾��С���");
					iv_drag.layout(windowWidth/2-iv_drag.getWidth()/2, iv_drag.getTop(), windowWidth/2+iv_drag.getWidth()/2, iv_drag.getBottom());
					int lastx = iv_drag.getLeft();
					int lasty = iv_drag.getTop();
					Editor editor = sp.edit();
					editor.putInt("lastx", lastx);
					editor.putInt("lasty", lasty);
					editor.commit();
				}
			}
		});

		// �����ͼƬע��һ�������ļ����¼�
		iv_drag.setOnTouchListener(new OnTouchListener() {
			// ��¼��ָ����Ļ�ϵĿ�ʼ����
			int startX;
			int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// ��ָ��һ�δ�����Ļ��Ӧ���¼�
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();// �������Ļ
					startY = (int) event.getRawY();
					Log.i(TAG, "����");
					break;
				// ��ָ����Ļ���ƶ�
				case MotionEvent.ACTION_MOVE:
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					int dx = newX - startX;
					int dy = newY - startY;
					Log.i(TAG, "�ƶ�");
					int newl = iv_drag.getLeft() + dx;
					int newt = iv_drag.getTop() + dy;
					int newr = iv_drag.getRight() + dx;
					int newb = iv_drag.getBottom() + dy;
					// �ж� λ���Ƿ�Ϸ���
					if (newl < 0 || newt < 0 || newr > windowWidth
							|| newb > (windowHeight - 50)) {
						break;
					}
					if (newt > windowHeight / 2) {
						// �ؼ�������
						tv_top.setVisibility(View.VISIBLE);
						tv_bottom.setVisibility(View.INVISIBLE);
					} else {
						// �ؼ�������
						tv_top.setVisibility(View.INVISIBLE);
						tv_bottom.setVisibility(View.VISIBLE);
					}

					iv_drag.layout(newl, newt, newr, newb);
					// ���¼�����ָ��ʼ����
					startX = (int) event.getRawX();// �������Ļ
					startY = (int) event.getRawY();
					break;
				// ��ָ�뿪��Ļ��˲��
				case MotionEvent.ACTION_UP:
					Log.i(TAG,"�뿪");
					int lastx = iv_drag.getLeft();
					int lasty = iv_drag.getTop();
					Editor editor = sp.edit();
					editor.putInt("lastx", lastx);
					editor.putInt("lasty", lasty);
					editor.commit();
					break;
				}
				return false;
			}
		});
	}
}
