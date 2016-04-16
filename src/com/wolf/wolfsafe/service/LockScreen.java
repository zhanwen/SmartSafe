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

	// 设备策略服务
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
	 * 用代码开启管理员
	 * 
	 * @param view
	 */
	public void openAdmin() {
		// 创建一个Intent
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

		// 我要激活谁
		ComponentName mDeviceAdminSample = new ComponentName(this,
				MyAdmin.class);

		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mDeviceAdminSample);
		// 劝说用户开启管理员权限
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				"开启我可以一键锁屏，你的按钮就不会经常失灵了");

		startActivity(intent);
	}

	/**
	 * 一键锁屏
	 */
	public void lockscreen() {

		ComponentName mDeviceAdminSample = new ComponentName(this,
				MyAdmin.class);
		if (dpm.isAdminActive(mDeviceAdminSample)) {
			dpm.lockNow();
			dpm.resetPassword("", 0); // 设置屏蔽密码
			// 清除sdcard上的数据
			// dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
			// 恢复出厂设置
			dpm.wipeData(0);
		} else {
			Toast.makeText(this, "还没有打开管理员权限", 0).show();
			return;
		}

	}

	// 卸载当前软件
	public void uninstall(View view) {
		// 1.先清除管理员权限
		ComponentName mDeviceAdminSample = new ComponentName(this,
				MyAdmin.class);
		dpm.removeActiveAdmin(mDeviceAdminSample);

		// 2.普通应用的卸载
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + getPackageName()));
		startActivity(intent);
	}

}
