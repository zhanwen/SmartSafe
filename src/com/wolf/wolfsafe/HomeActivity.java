package com.wolf.wolfsafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity {
	
	private GridView list_home;
	private MyAdapter adapter;
	private SharedPreferences sp;
	
	
	private static String[] names = {
			"�ֻ�����","ͨѶ��ʿ","�������",
			"���̹���","����ͳ��","�ֻ�ɱ��",
			"��������","�߼�����","��������"
	};
	
	private static int[] ids = {
		R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
		R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
		R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		list_home = (GridView) findViewById(R.id.list_home);
		adapter = new MyAdapter();
		list_home.setAdapter(adapter);
		
		list_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					switch (position) {
					case 8: //������������ 
						Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
						startActivity(intent);
						break;
					case 0: //�����ֻ�����ҳ��
						showLostFindDialog();
					default:
						break;
					}
			}
			
		});
	}
	private void showLostFindDialog() {
		//�ж��Ƿ����ù�����
		if(isSetupPwd()) {
			//�Ѿ����������ˣ�������������Ի���
			showEnterDialog();
		}else {
			//û���������룬������������ĶԻ���
			showSetupPwdDialog();	
		}
			
	}
	/**
	 * ��������Ի���
	 */
	private void showSetupPwdDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		//�Զ���һ�������ļ�
		View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_password, null);
		builder.setView(view);
		
		builder.show();
		
	}
	/**
	 * ��������Ի���
	 */
	private void showEnterDialog() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * �ж��Ƿ����ù����� 
	 * @return
	 */
	private boolean isSetupPwd() {
		String password = sp.getString("password", null);
		
//		if(TextUtils.isEmpty(password)) {
//			return false;
//		} else {
//			return true;
//		}
		return !TextUtils.isEmpty(password);
		
	}
	
	
	private class MyAdapter extends BaseAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(HomeActivity.this, R.layout.list_item_home, null);
			
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);	
			TextView tv_item = (TextView)view.findViewById(R.id.tv_item);
			tv_item.setText(names[position]);
			iv_item.setImageResource(ids[position]);
			
			return view;
		}
		
		@Override
		public int getCount() {
			return names.length;
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
