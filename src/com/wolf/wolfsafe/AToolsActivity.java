package com.wolf.wolfsafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.wolf.wolfsafe.utils.SmsUtils;
import com.wolf.wolfsafe.utils.SmsUtils.BackUpCallBack;

public class AToolsActivity extends Activity {
	
	
	private ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_atools);
		
	}
	
	
	/**
	 * 点击事件，进入号码归属地查询页面
	 * @param view
	 */
	public void numberQuery(View view) {
		Intent intent = new Intent(this, NumberAddressActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 点击事件，短信的备份
	 * @param view
	 */
	public void smsBackup(View view) {
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在备份短信");
		pd.show();
		new Thread() {
			@Override
			public void run() {
				try {
					SmsUtils.backupSms(AToolsActivity.this, new BackUpCallBack() {
						
						public void onSmsBackUp(int progress) {
							pd.setProgress(progress);
						}
						
						public void beforeBackUp(int max) {
							pd.setMax(max);
						}
					});
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AToolsActivity.this, "备份成功", 0).show();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(AToolsActivity.this, "备份失败", 0).show();
						}
					});
					
				}finally {
					pd.dismiss();
				}
			}
		}.start();
	
	}

	

	/**
	 * 点击事件，短信的还原
	 * @param view
	 */
	public void smsRestore(View view) {
		
		try {
			SmsUtils.restoreSms(this,true);
			Toast.makeText(AToolsActivity.this, "还原成功", 0).show();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	
	/**
	 * 常用号码查询
	 * @param view
	 */
	public void commonNumberQuery(View view) {
		Intent intent = new Intent(this, CommonNumberQueryActivity.class);
		startActivity(intent);
	}
	
	
	
}
