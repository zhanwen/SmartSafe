package com.wolf.wolfsafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

import com.wolf.wolfsafe.domain.AppInfo;
import com.wolf.wolfsafe.engine.AppInfoProvider;

public class AppManagerActivity extends Activity {
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
	
	//��ǰ������Ϣ��״̬
	private TextView tv_status;
	
	//��������������
	private PopupWindow popupWindow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		tv_status = (TextView) findViewById(R.id.tv_status);
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
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for(AppInfo info : appInfos){
					if(info.isUserApp()) {
						userAppInfos.add(info);
					}else {
						systemAppInfos.add(info);
					}
				}
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
		//��listviewע��һ�������ļ�����
		lv_app_manager.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			//������ʱ����õķ���
			//firstVisibleItem ��һ���ɼ���Ŀ��listview���������λ��
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if(userAppInfos != null && systemAppInfos != null) {
					if(firstVisibleItem > userAppInfos.size()) {
						tv_status.setText("ϵͳ����:" + systemAppInfos.size() + "��");
					}else {
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
				AppInfo appInfo;
				if(position == 0) {
					return;
				}else if(position == userAppInfos.size() + 1) {
					return;
				}else if(position <= userAppInfos.size()) {
					//�û��ĳ���
					int newPosition = position - 1;
					appInfo = userAppInfos.get(newPosition);
				}else {
					//ϵͳ����
					int newPosition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newPosition);
				}
//				System.out.println(appInfo.getPackname());
				dismissPopupWindow();
				
				View contentView = View.inflate(getApplicationContext(), R.layout.popup_app_item, null);
				popupWindow = new PopupWindow(contentView, -2, -2);
//				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.RED));
				int[] location = new int[2];
				view.getLocationInWindow(location);
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, location[0], location[1]);
			}
		});
	}
	
	private void dismissPopupWindow() {
		//�Ѿɵĵ�������رյ�
		if(popupWindow != null && popupWindow.isShowing()) {
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
		
		//����listview�ж��ٸ���Ŀ
		@Override
		public int getCount() {
			//return appInfos.size();
			return userAppInfos.size() + 1 + systemAppInfos.size()+1;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo;
			if(position == 0) {
				//��ʾ�û������ж��ٸ�С��ǩ
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("�û�����:" + userAppInfos.size() + "��");
				return tv;
			}else if(position == (userAppInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("ϵͳ����:" + systemAppInfos.size() + "��");
				return tv;
			}else if(position <= userAppInfos.size()){
				//�û����� 
				int newPosition	= position - 1;//��Ϊ����һ��textview���ı�ռ����λ��
				appInfo = userAppInfos.get(newPosition);
			}else {//ϵͳ����
				int newPosition = position - 1 - userAppInfos.size() - 1;
				appInfo = systemAppInfos.get(newPosition);
			}
			
			
			View view;
			ViewHolder holder;
			
//			if(position < userAppInfos.size()) {
//				//λ���������û�������ʾ��
//				appInfo = userAppInfos.get(position);
//			}else {
//				//��Щλ��������ϵͳ�����
//				int newPosition = position - userAppInfos.size();
//				appInfo = systemAppInfos.get(newPosition);
//			}
//			
			if(convertView != null && convertView instanceof RelativeLayout) {
				//������Ҫ����Ƿ�Ϊ�գ���Ҫ�ж��Ƿ��Ǻ��ʵ�����ȥ����
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
			
		
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getName());
			if(appInfo.isInRom()) {
				holder.tv_location.setText("�ֻ��ڴ�");
			}else {
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
