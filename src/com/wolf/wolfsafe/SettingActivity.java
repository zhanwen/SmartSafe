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

	// �����Ƿ��Զ�����
	private SettingItemView siv_update;
	/**
	 * ������������Ĳ���
	 */
	private SharedPreferences sp;

	// �����Ƿ��������������ʾ
	private SettingItemView siv_show_address;

	private Intent showAddressIntent;
	
	//���ù�������ʾ�򱳾�
	private SettingClickView scv_changbg;
	
	//����������������
	private SettingItemView siv_callsms_safe;
	private Intent callSmsSafeIntent;
	
	@Override
	protected void onResume() {
		super.onResume();
		showAddressIntent = new Intent(this, AddressService.class);
		boolean isRunning = ServiceUtils.isServiceRunning(this,
				"com.wolf.wolfsafe.service.AddressService");
		if(isRunning) {
			//��������ķ��������е�
			siv_show_address.setChecked(true);
		}else {
			//��������ķ����Ѿ��ر�
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
		// �����Ƿ����Զ�����
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		boolean update = sp.getBoolean("update", false);
		if (update) {
			// �Զ������Ѿ�����
			siv_update.setChecked(true);
			// siv_update.setDesc("�Զ������Ѿ�����");
		} else {
			// �Զ������Ѿ��ر�
			siv_update.setChecked(false);
			// siv_update.setDesc("�Զ������Ѿ��ر�");

		}

		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				// �ж��Ƿ�ѡ��
				// �Ѿ����Զ�������
				if (siv_update.isChecked()) {
					siv_update.setChecked(false);
					// siv_update.setDesc("�Զ������Ѿ��ر�");
					editor.putBoolean("update", false);
				} else {
					// û�д��Զ�����
					siv_update.setChecked(true);
					// siv_update.setDesc("�Զ������Ѿ�����");
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});

		// ���ú����������ʾ�ؼ�
		siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
		siv_show_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ���������������ʾ�����Ѿ�����
				if (siv_show_address.isChecked()) {
					// ��Ϊ��ѡ��״̬
					siv_show_address.setChecked(false);
					stopService(showAddressIntent);
				} else {
					// ѡ��״̬
					siv_show_address.setChecked(true);
					startService(showAddressIntent);
					
				}

			}
		});

		//��������������
			siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
			callSmsSafeIntent = new Intent(this, CallSmsSafeActivity.class);
			siv_callsms_safe.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// ���������������ʾ�����Ѿ�����
					if (siv_callsms_safe.isChecked()) {
						// ��Ϊ��ѡ��״̬
						siv_callsms_safe.setChecked(false);
						stopService(callSmsSafeIntent);
					} else {
						// ѡ��״̬
						siv_callsms_safe.setChecked(true);
						startService(callSmsSafeIntent);
					}
				}
			});
		
		scv_changbg = (SettingClickView) findViewById(R.id.scv_changbg);
		scv_changbg.setTitle("��������ʾ����");
		final String[] items = {"��͸��","������","��ʿ��","������","ƻ����"};
		int which = sp.getInt("which", 0);
		scv_changbg.setDesc(items[which]);
		scv_changbg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int dd = sp.getInt("which", 0);	
				//����һ���Ի���
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("��������ʾ����");
				builder.setSingleChoiceItems(items, dd, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//����ѡ�����
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						
						scv_changbg.setDesc(items[which]);
						//ȡ���Ի���
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("ȡ��", null);
				builder.show();
			}
		});
		
	}

}
