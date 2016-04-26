package com.wolf.wolfsafe;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CleanCacheActivity extends Activity {
	
	protected static final int SCANING = 1;
	public static final int SHOW_CACHE_INFO = 2;
	protected static final int SCAN_FINISH = 3;
	private ProgressBar pb;
	private TextView tv_scan_status;
	private PackageManager pm;
	private LinearLayout ll_container;

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SCANING:
				String text = (String) msg.obj;
				tv_scan_status.setText("正在扫描："+text);
				break;

			case SHOW_CACHE_INFO:
				View view = View.inflate(getApplicationContext(), R.layout.list_item_cacheinfo, null);
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
				TextView tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
				final CacheInfo info = (CacheInfo) msg.obj;
				iv_icon.setImageDrawable(info.icon);
				tv_app_name.setText(info.name);
				tv_cache_size.setText("缓存大小："+Formatter.formatFileSize(getApplicationContext(), info.size));
				ImageView iv_cache_delete = (ImageView) view.findViewById(R.id.iv_cache_delete);
				iv_cache_delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Method[] methods = PackageManager.class.getMethods();
						for(Method method : methods){
							try {
								if("deleteApplicationCacheFiles".equals(method.getName())){
									method.invoke(pm, info.packname, new IPackageDataObserver.Stub() {
										@Override
										public void onRemoveCompleted(String packageName, boolean succeeded)
												throws RemoteException {
											
										}
									});
								}
							} catch (Exception e) {
								Intent intent = new Intent();
								intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
								intent.addCategory(Intent.CATEGORY_DEFAULT);
								intent.setData(Uri.parse("package:"+info.packname));
								startActivity(intent);
								e.printStackTrace();
							}
						}
					}
				});
				ll_container.addView(view, 0);
				break;
			case SCAN_FINISH:
				tv_scan_status.setText("扫描完毕");
				break;
			}
		};
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_clean_cache);

		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		pb = (ProgressBar) findViewById(R.id.pb);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		scanCache();

	}

	/**
	 * 扫描手机里面所有一个用程序的缓存信息
	 */
	private void scanCache() {
		pm = getPackageManager();

		new Thread() {
			public void run() {
				Method getPackageSizeInfoMethod = null;
				List<PackageInfo> packInfos = pm.getInstalledPackages(0);
				Method[] methods = PackageManager.class.getMethods();

				for (Method method : methods) {
					if ("getPackageSizeInfo".equals(method.getName())) {
						getPackageSizeInfoMethod = method;
					}
				}

				pb.setMax(packInfos.size());
				int progress = 0;

				for (PackageInfo packInfo : packInfos) {
					try {
						getPackageSizeInfoMethod.invoke(pm,
								packInfo.packageName, new MyDataObserver());
						
						Message msg = Message.obtain();
						msg.what= SCANING;
						msg.obj = packInfo.applicationInfo.loadLabel(pm).toString();
						handler.sendMessage(msg);
						
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
					}
					progress++;
					pb.setProgress(progress);
				}
				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				handler.sendMessage(msg);
			};
		}.start();

	}

	private class MyDataObserver extends IPackageStatsObserver.Stub {
		@Override
		public void onGetStatsCompleted(final PackageStats pStats, boolean succeeded)
				throws RemoteException {
			final long cache = pStats.cacheSize;
			long code = pStats.codeSize;

			final String packname = pStats.packageName;
			final ApplicationInfo appInfo;
			try {
				appInfo = pm.getApplicationInfo(packname, 0);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (cache > 0) {
							try {
								Message msg = Message.obtain();
								msg.what = SHOW_CACHE_INFO;
								CacheInfo cacheInfo = new CacheInfo();
								cacheInfo.packname = pStats.packageName;
								cacheInfo.icon = pm.getApplicationInfo(pStats.packageName, 0).loadIcon(pm);
								cacheInfo.name = pm.getApplicationInfo(pStats.packageName, 0).loadLabel(pm).toString();
								cacheInfo.size = cache;
								msg.obj = cacheInfo;
								handler.sendMessage(msg);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private class MypackDataObserver extends IPackageDataObserver.Stub {
		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			System.out.println(packageName + succeeded);
		}
	}
	
	/**
	 * 清理手机的全部缓存
	 */
	public void clearAll(View view) {
		Method[] methods = PackageManager.class.getMethods();
		for(Method method : methods) {
			if("freeStorageAndNotify".equals(method.getName())) {
				try {
					method.invoke(pm, Integer.MAX_VALUE,new MypackDataObserver());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}
	}
	
	class CacheInfo{
		Drawable icon;
		String name;
		long size;
		String packname;
	}
	
	
}