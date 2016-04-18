package com.wolf.wolfsafe;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wolf.wolfsafe.db.dao.BlackNumberDao;
import com.wolf.wolfsafe.domain.BlackNumberInfo;

public class CallSmsSafeActivity extends Activity {
	
	private static final String TAG = "CallSmsSafeActivity";
	
	private ListView lv_callsms_safe;
	private List<BlackNumberInfo> infos;
	private BlackNumberDao dao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		
		lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
		
		dao = new BlackNumberDao(this);
		infos = dao.findAll();
		lv_callsms_safe.setAdapter(new CallSmsSafeAdapter());
	}
	
	
	private class CallSmsSafeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
		} 

		
		 //�ж��ٸ���Ŀ����ʾ����������ͻᱻ���ö��ٴ�
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			//1.�����ڴ���view���󴴽��ĸ���
			if(convertView == null) {
				Log.i(TAG,"�����µ�view����" + position);
				//��һ�������ļ�ת����view����
			    view = View.inflate(getApplicationContext(), R.layout.list_item_callsms, null);
			    
			  //2.���ٺ��Ӳ�ѯ�Ĵ���//�ڴ��ж���ĵ�ַ
				holder = new ViewHolder();
				holder.tv_number = (TextView) view.findViewById(R.id.tv_black_number);
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_block_mode);
				//��������������ʱ���ҵ����ǵ����ã�����ڼ��±������ڸ��׵Ŀڴ�
				view.setTag(holder);
			}else {
				Log.i(TAG,"��ǰ��view���󣬸�����ʷ�����view����" + position);
				view = convertView;
				holder = (ViewHolder) view.getTag(); //5%
			}
			holder.tv_number.setText(infos.get(position).getNumber());
			String mode = infos.get(position).getMode();
			
			if("1".equals(mode)) {
				holder.tv_mode.setText("�绰����");
			}else if("2".equals(mode)) {
				holder.tv_mode.setText("��������");
			}else {
				holder.tv_mode.setText("ȫ������");
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
	
	/**
	 * view ���������
	 * ��¼���ӵ��ڴ��ַ
	 * �൱��һ�����±�
	 * @author Hanwen
	 *
	 */
	static class ViewHolder {
		TextView tv_number;
		TextView tv_mode;
	}
}
