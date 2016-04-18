package com.wolf.wolfsafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.wolf.wolfsafe.service.AddressService;
import com.wolf.wolfsafe.ui.SettingClickView;
import com.wolf.wolfsafe.ui.SettingItemView;
import com.wolf.wolfsafe.utils.ServiceUtils;

public class SettingActivity extends Activity {

	// 设置是否自动更新
	private SettingItemView siv_update;
	/**
	 * 用来保存软件的参数
	 */
	private SharedPreferences sp;

	// 设置是否开启来电归属地显示
	private SettingItemView siv_show_address;

	private Intent showAddressIntent;
	
	//设置归属地显示框背景
	private SettingClickView scv_changbg;
	
	//黑名单的拦截设置
	private SettingItemView siv_callsms_safe;
	private Intent callSmsSafeIntent;
	
	@Override
	protected void onResume() {
		super.onResume();
		showAddressIntent = new Intent(this, AddressService.class);
		boolean isRunning = ServiceUtils.isServiceRunning(this,
				"com.wolf.wolfsafe.service.AddressService");
		if(isRunning) {
			//监听来电的服务是运行的
			siv_show_address.setChecked(true);
		}else {
			//监听来电的服务已经关闭
			siv_show_address.setChecked(false);
		}
		
		boolean isCallSmsRunning = ServiceUtils.isServiceRunning(this,
				"com.wolf.wolfsafe.service.CallSmsSafeService");
		siv_callsms_safe.setChecked(isCallSmsRunning);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 设置是否开启自动升级
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		boolean update = sp.getBoolean("update", false);
		if (update) {
			// 自动升级已经开启
			siv_update.setChecked(true);
			// siv_update.setDesc("自动升级已经开启");
		} else {
			// 自动升级已经关闭
			siv_update.setChecked(false);
			// siv_update.setDesc("自动升级已经关闭");

		}

		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				// 判断是否选中
				// 已经打开自动升级了
				if (siv_update.isChecked()) {
					siv_update.setChecked(false);
					// siv_update.setDesc("自动升级已经关闭");
					editor.putBoolean("update", false);
				} else {
					// 没有打开自动升级
					siv_update.setChecked(true);
					// siv_update.setDesc("自动升级已经开启");
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});

		// 设置号码归属地显示控件
		siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
		siv_show_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 服务监听来电来显示服务已经开启
				if (siv_show_address.isChecked()) {
					// 变为非选中状态
					siv_show_address.setChecked(false);
					stopService(showAddressIntent);
				} else {
					// 选择状态
					siv_show_address.setChecked(true);
					startService(showAddressIntent);
					
				}

			}
		});

		//黑名单拦截设置
			siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
			callSmsSafeIntent = new Intent(this, CallSmsSafeActivity.class);
			siv_callsms_safe.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 服务监听来电来显示服务已经开启
					if (siv_callsms_safe.isChecked()) {
						// 变为非选中状态
						siv_callsms_safe.setChecked(false);
						stopService(callSmsSafeIntent);
					} else {
						// 选择状态
						siv_callsms_safe.setChecked(true);
						startService(callSmsSafeIntent);
					}
				}
			});
		
		scv_changbg = (SettingClickView) findViewById(R.id.scv_changbg);
		scv_changbg.setTitle("归属地提示框风格");
		final String[] items = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
		int which = sp.getInt("which", 0);
		scv_changbg.setDesc(items[which]);
		scv_changbg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int dd = sp.getInt("which", 0);	
				//弹出一个对话框
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				builder.setSingleChoiceItems(items, dd, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//保存选择参数
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						
						scv_changbg.setDesc(items[which]);
						//取消对话框
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", null);
				builder.show();
			}
		});
		
	}

}
