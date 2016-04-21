package com.wolf.wolfsafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wolf.wolfsafe.domain.AppInfo;
import com.wolf.wolfsafe.engine.AppInfoProvider;
import com.wolf.wolfsafe.utils.DensityUtil;

public class AppManagerActivity extends Activity implements OnClickListener {
	private static final String TAG = "AppManagerActivity";
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private ListView lv_app_manager;
	private LinearLayout ll_loading;

	/**
	 * ���õ�Ӧ�ó������Ϣ
	 */
	private List<AppInfo> appInfos;

	/**
	 * �û�Ӧ�ó���ļ���
	 */
	private List<AppInfo> userAppInfos;

	/**
	 * ϵͳӦ�ó���ļ���
	 */
	private List<AppInfo> systemAppInfos;

	// ��ǰ������Ϣ��״̬
	private TextView tv_status;

	// ��������������
	private PopupWindow popupWindow;

	// ����
	private LinearLayout ll_start;
	// ����
	private LinearLayout ll_share;
	// ж��
	private LinearLayout ll_uninstall;
	
	//���������Ŀ
	private AppInfo appInfo;
	
	private AppManagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		long sdsize = getAvailSpace(Environment.getExternalStorageDirectory()
				.getAbsolutePath());
		long romsize = getAvailSpace(Environment.getDataDirectory()
				.getAbsolutePath());
		tv_avail_sd
				.setText("SD�����ÿռ�:" + Formatter.formatFileSize(this, sdsize));
		tv_avail_rom.setText("�ڴ���ÿռ�:"
				+ Formatter.formatFileSize(this, romsize));

		fillData();
		// ��listviewע��һ�������ļ�����
		lv_app_manager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			// ������ʱ����õķ���
			// firstVisibleItem ��һ���ɼ���Ŀ��listview���������λ��
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size()) {
						tv_status.setText("ϵͳ����:" + systemAppInfos.size() + "��");
					} else {
						tv_status.setText("�û�����:" + userAppInfos.size() + "��");
					}
				}
			}
		});
		/**
		 * ����listview�ĵ���¼�
		 */
		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					return;
				} else if (position == userAppInfos.size() + 1) {
					return;
				} else if (position <= userAppInfos.size()) {
					// �û��ĳ���
					int newPosition = position - 1;
					appInfo = userAppInfos.get(newPosition);
				} else {
					// ϵͳ����
					int newPosition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newPosition);
				}
				// System.out.println(appInfo.getPackname());
				dismissPopupWindow();

				View contentView = View.inflate(getApplicationContext(),
						R.layout.popup_app_item, null);
				ll_start = (LinearLayout) contentView
						.findViewById(R.id.ll_start);
				ll_share = (LinearLayout) contentView
						.findViewById(R.id.ll_share);
				ll_uninstall = (LinearLayout) contentView
						.findViewById(R.id.ll_uninstall);

				ll_start.setOnClickListener(AppManagerActivity.this);
				ll_share.setOnClickListener(AppManagerActivity.this);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);

				popupWindow = new PopupWindow(contentView, -2, -2);
				// ����Ч���Ĳ��ű���Ҫ�����б�����ɫ
				// ͸����ɫҲ����ɫ
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				int[] location = new int[2];
				view.getLocationInWindow(location);
				// �ڴ����������õĿ��ֵ ��������-----> dip
				int dip = 60;
				int px = DensityUtil.dip2px(getApplicationContext(), dip);
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
						px, location[1]);

				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(300);
				AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
				aa.setDuration(300);
				AnimationSet set = new AnimationSet(false);
				set.addAnimation(sa);
				set.addAnimation(aa);
				contentView.startAnimation(set);
			}
		});
	}

	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo info : appInfos) {
					if (info.isUserApp()) {
						userAppInfos.add(info);
					} else {
						systemAppInfos.add(info);
					}
				}
				// ����listview������������
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if(adapter == null) {
							adapter = new AppManagerAdapter();
							lv_app_manager.setAdapter(adapter);
							
						}else {
							adapter.notifyDataSetChanged();
						}
						ll_loading.setVisibility(View.INVISIBLE);
					}
				});
			}
		}.start();
	}

	private void dismissPopupWindow() {
		// �Ѿɵĵ�������رյ�
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	@Override
	protected void onDestroy() {
		dismissPopupWindow();
		super.onDestroy();
	}

	private class AppManagerAdapter extends BaseAdapter {

		// ����listview�ж��ٸ���Ŀ
		@Override
		public int getCount() {
			// return appInfos.size();
			return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo;
			if (position == 0) {
				// ��ʾ�û������ж��ٸ�С��ǩ
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("�û�����:" + userAppInfos.size() + "��");
				return tv;
			} else if (position == (userAppInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("ϵͳ����:" + systemAppInfos.size() + "��");
				return tv;
			} else if (position <= userAppInfos.size()) {
				// �û�����
				int newPosition = position - 1;// ��Ϊ����һ��textview���ı�ռ����λ��
				appInfo = userAppInfos.get(newPosition);
			} else {// ϵͳ����
				int newPosition = position - 1 - userAppInfos.size() - 1;
				appInfo = systemAppInfos.get(newPosition);
			}

			View view;
			ViewHolder holder;

			// if(position < userAppInfos.size()) {
			// //λ���������û�������ʾ��
			// appInfo = userAppInfos.get(position);
			// }else {
			// //��Щλ��������ϵͳ�����
			// int newPosition = position - userAppInfos.size();
			// appInfo = systemAppInfos.get(newPosition);
			// }
			//
			if (convertView != null && convertView instanceof RelativeLayout) {
				// ������Ҫ����Ƿ�Ϊ�գ���Ҫ�ж��Ƿ��Ǻ��ʵ�����ȥ����
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_appinfo, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.tv_location = (TextView) view
						.findViewById(R.id.tv_app_location);
				view.setTag(holder);
			}

			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getName());
			if (appInfo.isInRom()) {
				holder.tv_location.setText("�ֻ��ڴ�");
			} else {
				holder.tv_location.setText("�ⲿ�洢");
			}

			return view;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	static class ViewHolder {
		TextView tv_name;
		TextView tv_location;
		ImageView iv_icon;
	}

	/**
	 * ��ȡĳ��Ŀ¼�Ŀ��ÿռ�
	 * 
	 * @param path
	 * @return
	 */
	private long getAvailSpace(String path) {
		StatFs statf = new StatFs(path);
		statf.getBlockCount(); // ��ȡ�����ĸ���
		long size = statf.getBlockSize(); // ��ȡ�����Ĵ�С
		long count = statf.getAvailableBlocks();// ��ȡ���õ�����ĸ���
		return size * count;

	}

	/**
	 * ���ֶ�Ӧ�ĵ���¼�
	 */
	@Override
	public void onClick(View v) {
		
		dismissPopupWindow();
		
		switch (v.getId()) {
		case R.id.ll_start:
			Log.i(TAG,"����" + appInfo.getName());
			startApplication();
			break;
		case R.id.ll_share:
			Log.i(TAG,"����" + appInfo.getName());
			shareApplication();
			break;
		case R.id.ll_uninstall:
			if(appInfo.isUserApp()) {
				Log.i(TAG,"ж��" + appInfo.getName());
				uninstallApplication();	
			}else {
				Toast.makeText(this, "ϵͳӦ��ֻ�л�ȡrootȨ�޲ſ���ж��", 0).show();
				//Runtime.getRuntime().exec("");
			}
			break;
		}
	}
	/**
	 * ����һ��Ӧ�ó���
	 */
	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "�Ƽ���ʹ��һ����������ƽ�:" + appInfo.getName());
		startActivity(intent);
	}

	/**
	 * ж��Ӧ��
	 */
	private void uninstallApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + appInfo.getPackname()));
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//ˢ��ҳ��
		fillData();
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * ����һ��Ӧ��
	 */
	private void startApplication() {
		//��ѯ���Ӧ�ó�������activity��������������
		PackageManager pm = getPackageManager();
//		Intent intent = new Intent();
//		intent.setAction("android.intent.action.MAIN");
//		intent.addCategory("android.intent.category.LAUNCHER");
//		//��ѯ�����������ֻ��Ͼ�������������activity
//		List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);	
		
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
		if(intent != null) {
			startActivity(intent);
		}else {
			Toast.makeText(this, "����������ǰӦ��", 0).show();
		}
		
		
	}

}
