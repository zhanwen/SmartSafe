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
import com.wolf.wolfsafe.service.CallSmsSafeService;
import com.wolf.wolfsafe.service.WatchDogService;
import com.wolf.wolfsafe.ui.SettingClickView;
import com.wolf.wolfsafe.ui.SettingItemView;
import com.wolf.wolfsafe.utils.ServiceUtils;

public class SettingActivity extends Activity {
	//自动更新设置
	private SettingItemView siv_update;
	//归属地显示设置
	private SettingItemView siv_showaddress;
	private Intent showAddressIntent;
	//更改归属地的背景
	private SettingClickView scv_changebg;
	
	//更改归属地提示框的位置
	private SettingClickView scv_changeposition;
	
	//黑名单设置
	private SettingItemView siv_callsms_safe;
	private Intent callSmsSafeIntent;
	
	//程序锁设置
	private SettingItemView siv_applock;
	private Intent watchDogIntent;
	
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//自动更新设置
		boolean update = sp.getBoolean("update", false);
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		if(update){
			siv_update.setChecked(true);
		}else{
			siv_update.setChecked(false);
		}
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(siv_update.isChecked()){
					siv_update.setChecked(false);
					editor.putBoolean("update", false);
				}else{
					siv_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		//归属地显示设置
		siv_showaddress = (SettingItemView) findViewById(R.id.siv_showaddress);
		showAddressIntent = new Intent(this,AddressService.class);
		if(ServiceUtils.isServiceRunning(this, "com.wolf.wolfsafe.service.AddressService")){
			siv_showaddress.setChecked(true);
		}else{
			siv_showaddress.setChecked(false);
		}
		siv_showaddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(siv_showaddress.isChecked()){
					siv_showaddress.setChecked(false);
					//关闭归属地显示
					stopService(showAddressIntent);
					//存储状态到sp里面
				}else{
					//开启归属地显示
					startService(showAddressIntent);
					siv_showaddress.setChecked(true);
				}
				
			}
		});
		//更改背景
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("归属地提示框风格");
		final int which = sp.getInt("which", 0);
		final String[] items = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
		scv_changebg.setDesc(items[which]);
		scv_changebg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder =new Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				int tt = sp.getInt("which", 0);
				builder.setSingleChoiceItems(items, tt, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						scv_changebg.setDesc(items[which]);
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", null);
				builder.show();
			}
		});
		//更改归属地提示框的位置
		scv_changeposition = (SettingClickView) findViewById(R.id.scv_changeposition);
		scv_changeposition.setTitle("归属地提示框的位置");
		scv_changeposition.setDesc("设置归属地提示框的位置");
		scv_changeposition.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,DragViewActivity.class);
				startActivity(intent);
			}
		});
		//黑名单拦截设置
		siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
		callSmsSafeIntent = new Intent(this,CallSmsSafeService.class);
		if(ServiceUtils.isServiceRunning(this, "com.wolf.wolfsafe.service.CallSmsSafeService")){
			siv_callsms_safe.setChecked(true);
		}else{
			siv_callsms_safe.setChecked(false);
		}
		siv_callsms_safe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(siv_callsms_safe.isChecked()){
					siv_callsms_safe.setChecked(false);
					stopService(callSmsSafeIntent);
				}else{
					siv_callsms_safe.setChecked(true);
					startService(callSmsSafeIntent);
				}
			}
		});
		
		//看门狗设置
		siv_applock = (SettingItemView) findViewById(R.id.siv_applock);
		watchDogIntent  = new Intent(this,WatchDogService.class);
		if(ServiceUtils.isServiceRunning(this, "com.wolf.wolfsafe.service.WatchDogService")){
			siv_applock.setChecked(true);
		}else{
			siv_applock.setChecked(false);
		}
		siv_applock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(siv_applock.isChecked()){
					siv_applock.setChecked(false);
					stopService(watchDogIntent);
				}else{
					siv_applock.setChecked(true);
					startService(watchDogIntent);
				}
			}
		});
	}
}
