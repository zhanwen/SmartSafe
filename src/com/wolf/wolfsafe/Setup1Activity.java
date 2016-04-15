package com.wolf.wolfsafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;

public class Setup1Activity extends BaseSetupActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		
	}
	
	@Override
	public void showNext() {
		Intent intent = new Intent(Setup1Activity.this,Setup2Activity.class);
		startActivity(intent);
		//�رյ�ǰҳ
		finish();
		
		/**
		 * Ҫ����finish��������startActivity��intent��������ִ��
		 */
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}
	
	
	@Override
	public void showPre() {
		// TODO Auto-generated method stub
		
	}
	
}
