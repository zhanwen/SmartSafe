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
	//�Զ���������
	private SettingItemView siv_update;
	//��������ʾ����
	private SettingItemView siv_showaddress;
	private Intent showAddressIntent;
	//���Ĺ����صı���
	private SettingClickView scv_changebg;
	
	//���Ĺ�������ʾ���λ��
	private SettingClickView scv_changeposition;
	
	//����������
	private SettingItemView siv_callsms_safe;
	private Intent callSmsSafeIntent;
	
	//����������
	private SettingItemView siv_applock;
	private Intent watchDogIntent;
	
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//�Զ���������
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
		//��������ʾ����
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
					//�رչ�������ʾ
					stopService(showAddressIntent);
					//�洢״̬��sp����
				}else{
					//������������ʾ
					startService(showAddressIntent);
					siv_showaddress.setChecked(true);
				}
				
			}
		});
		//���ı���
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("��������ʾ����");
		final int which = sp.getInt("which", 0);
		final String[] items = {"��͸��","������","��ʿ��","������","ƻ����"};
		scv_changebg.setDesc(items[which]);
		scv_changebg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder =new Builder(SettingActivity.this);
				builder.setTitle("��������ʾ����");
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
				builder.setNegativeButton("ȡ��", null);
				builder.show();
			}
		});
		//���Ĺ�������ʾ���λ��
		scv_changeposition = (SettingClickView) findViewById(R.id.scv_changeposition);
		scv_changeposition.setTitle("��������ʾ���λ��");
		scv_changeposition.setDesc("���ù�������ʾ���λ��");
		scv_changeposition.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,DragViewActivity.class);
				startActivity(intent);
			}
		});
		//��������������
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
		
		//���Ź�����
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
