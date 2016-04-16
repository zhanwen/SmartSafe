package com.wolf.wolfsafe.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

public class LockScreen extends Service {

	// �豸���Է���
	private DevicePolicyManager dpm;

	@Override
	public void onCreate() {
		super.onCreate();
		dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
//		openAdmin();
		lockscreen();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * �ô��뿪������Ա
	 * 
	 * @param view
	 */
	public void openAdmin() {
		// ����һ��Intent
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

		// ��Ҫ����˭
		ComponentName mDeviceAdminSample = new ComponentName(this,
				MyAdmin.class);

		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mDeviceAdminSample);
		// Ȱ˵�û���������ԱȨ��
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				"�����ҿ���һ����������İ�ť�Ͳ��ᾭ��ʧ����");

		startActivity(intent);
	}

	/**
	 * һ������
	 */
	public void lockscreen() {

		ComponentName mDeviceAdminSample = new ComponentName(this,
				MyAdmin.class);
		if (dpm.isAdminActive(mDeviceAdminSample)) {
			dpm.lockNow();
			dpm.resetPassword("", 0); // ������������
			// ���sdcard�ϵ�����
			// dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
			// �ָ���������
			dpm.wipeData(0);
		} else {
			Toast.makeText(this, "��û�д򿪹���ԱȨ��", 0).show();
			return;
		}

	}

	// ж�ص�ǰ���
	public void uninstall(View view) {
		// 1.���������ԱȨ��
		ComponentName mDeviceAdminSample = new ComponentName(this,
				MyAdmin.class);
		dpm.removeActiveAdmin(mDeviceAdminSample);

		// 2.��ͨӦ�õ�ж��
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + getPackageName()));
		startActivity(intent);
	}

}
