package com.wolf.wolfsafe;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wolf.wolfsafe.domain.AppInfo;
import com.wolf.wolfsafe.engine.AppInfoProvider;

public class AppManagerActivity extends Activity {
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private ListView lv_app_manager;
	private LinearLayout ll_loading;
	
	private List<AppInfo> appInfos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		
		tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		long sdsize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
		long romsize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());
		tv_avail_sd.setText("SD�����ÿռ�:" + Formatter.formatFileSize(this, sdsize));
		tv_avail_rom.setText("�ڴ���ÿռ�:" + Formatter.formatFileSize(this, romsize));
		
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(){
			@Override
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				//����listview������������
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						lv_app_manager.setAdapter(new AppManagerAdapter());
						ll_loading.setVisibility(View.INVISIBLE);
					}
				});
			}
		}.start();
	}
	
	private class AppManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return appInfos.size();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if(convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else {
				view = View.inflate(getApplicationContext(), R.layout.list_item_appinfo, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.tv_location = (TextView) view.findViewById(R.id.tv_app_location);
				view.setTag(holder);
			}
			
			AppInfo appInfo = appInfos.get(position);
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getName());
			
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
	 * @param path
	 * @return
	 */
	private long getAvailSpace(String path) {
		StatFs statf = new StatFs(path);
		statf.getBlockCount(); //��ȡ�����ĸ���
		long size = statf.getBlockSize();  //��ȡ�����Ĵ�С
		long count = statf.getAvailableBlocks();//��ȡ���õ�����ĸ���
		return size * count;
		
	}
	
	
	
	
	
}
