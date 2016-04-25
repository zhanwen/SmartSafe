package com.wolf.wolfsafe;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CleanCacheActivity extends Activity {
	
	private ProgressBar pb;
	private TextView tv_scan_status;
	private PackageManager pm;
	private LinearLayout ll_container;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_clean_cache);
		
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		pb = (ProgressBar)findViewById(R.id.pb);
		
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		scanCache();
		
	}

	
	
	/**
	 * 扫描手机里面所有一个用程序的缓存信息
	 */
	private void scanCache() {
		pm = getPackageManager();
		
		new Thread(){
			public void run() {
				Method getPackageSizeInfoMethod = null;
				List<PackageInfo> packInfos = pm.getInstalledPackages(0);
				Method[] methods = PackageManager.class.getMethods();
				
				for(Method method : methods) {
					if("getPackageSizeInfo".equals(method.getName())) {
						getPackageSizeInfoMethod = method;
					}
				} 
				
				pb.setMax(packInfos.size());
				int progress  = 0;
				
				
				for(PackageInfo packInfo : packInfos) {
					try {
						getPackageSizeInfoMethod.invoke(pm, packInfo.packageName, new MyDataObserver());
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
					}
					progress++;
					pb.setProgress(progress);
				}
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						tv_scan_status.setText("扫描完毕");
					}
				});
			};
		}.start();
		
	}
	
	
	private class MyDataObserver extends IPackageStatsObserver.Stub {
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			final long cache = pStats.cacheSize;
			long code = pStats.codeSize;
			long data = pStats.dataSize;
			
			String packname = pStats.packageName;
			final ApplicationInfo appInfo;
			try {
				appInfo = pm.getApplicationInfo(packname, 0);
				
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							tv_scan_status.setText("正在扫描:" + appInfo.loadLabel(pm));
							if(cache > 0) {
								View view = View.inflate(getApplicationContext(), R.layout.list_item_cacheinfo, null);
								TextView tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
								tv_cache_size.setText("缓存大小:" + Formatter.formatFileSize(getApplicationContext(), cache));
								TextView tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
								tv_app_name.setText(appInfo.loadLabel(pm));
								
								ll_container.addView(view);
							}
						}
					});
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			
			
		}
	}
	
	
	
	
}
