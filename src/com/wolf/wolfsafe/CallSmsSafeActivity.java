package com.wolf.wolfsafe;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wolf.wolfsafe.db.dao.BlackNumberDao;
import com.wolf.wolfsafe.domain.BlackNumberInfo;

public class CallSmsSafeActivity extends Activity {
	private ListView lv_callsms_safe;
	private List<BlackNumberInfo> infos;
	private BlackNumberDao dao;
	private CallSmsSafeAdapter adpter;
	private LinearLayout ll_loading;
	private int offset = 0;
	private int maxnumber = 20;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_callsms_safe	= (ListView) findViewById(R.id.lv_callsms_safe);
		dao = new BlackNumberDao(this);
		fillData();
		//listviewע��һ�������¼��ļ�����
		lv_callsms_safe.setOnScrollListener(new OnScrollListener() {
			//��������״̬�����仯��ʱ��
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
					case OnScrollListener.SCROLL_STATE_IDLE: //����״̬
						System.out.println("����״̬");
						//�жϵ�ǰlistview������λ��
						int lastposition = lv_callsms_safe.getLastVisiblePosition();//��ȡ���һ���ɼ���Ŀ�ڼ��������λ��
						//����������20item λ�ô�0��ʼ ���һ��λ����19
						if(lastposition == (infos.size()-1)){
							offset += maxnumber;
							fillData();
						}
						break;
					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: //��ָ��������
						break;
					case OnScrollListener.SCROLL_STATE_FLING: //���Ի���״̬
						break;
				}
			}
			//������ʱ����õķ���
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
	}  
	//�������
	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(){
			public void run() {
				if(infos == null){
					infos = dao.findPart(offset,maxnumber);
				}else{
					infos.addAll(dao.findPart(offset, maxnumber));
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if(adpter == null){
							adpter = new CallSmsSafeAdapter();
							lv_callsms_safe.setAdapter(adpter);
						}else{
							adpter.notifyDataSetChanged();
						}
					}
				});
			};
		}.start();
	}
	/**
	 * ���������� ������
	 */
 	private class CallSmsSafeAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return infos.size();
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if(convertView == null){
				view = View.inflate(getApplication(),R.layout.list_item_callsms,null);
				holder  = new ViewHolder();
				holder.tv_number = (TextView) view.findViewById(R.id.tv_black_number);
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_block_mode);
				holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				//��view������ʱ���ҵ����ǵ����ô�ŵ�tag��
				view.setTag(holder);
			}else{
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			holder.tv_number.setText(infos.get(position).getNumber());
			String mode = infos.get(position).getMode();
			if("1".equals(mode)){
				holder.tv_mode.setText("���ص绰");
			}else if("2".equals(mode)){
				holder.tv_mode.setText("���ض���");
			}else if("3".equals(mode)){
				holder.tv_mode.setText("ȫ������");
			}
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
					builder.setTitle("����");
					builder.setMessage("ȷ��Ҫɾ��������¼ô?");
					builder.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//ɾ�����ݿ������
							dao.delete(infos.get(position).getNumber());
							//���½���
							infos.remove(position);
							//֪ͨlistview��������������
							adpter.notifyDataSetChanged();
						}
					});
					builder.setNegativeButton("ȡ��",null);
					builder.show();
				}
			});
			return view;
		}
		@Override
		public Object getItem(int position) {
			return null;
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
	}
	/**
	 * View���������
	 * ��¼id  �൱��һ�����±�
	 */ 
	static class ViewHolder{
		TextView tv_number;
		TextView tv_mode;
		ImageView iv_delete;
	}
	
	private EditText et_blacknumber;
	private CheckBox cb_phone;
	private CheckBox cb_sms;
	private Button bt_ok;
	private Button bt_cancal;
	//��Ӻ���������
	public void addBlackNumber(View view){
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View contentView = View.inflate(this,R.layout.dialog_add_blacknumber,null);
		et_blacknumber = (EditText) contentView.findViewById(R.id.et_blacknumber);
		cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
		cb_sms = (CheckBox) contentView.findViewById(R.id.cb_sms);
		bt_ok = (Button) contentView.findViewById(R.id.ok);
		bt_cancal = (Button) contentView.findViewById(R.id.cancel);
		dialog.setView(contentView,0,0,0,0);
		dialog.show();
		bt_cancal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();//�رնԻ���
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String blacknumber = et_blacknumber.getText().toString().trim();
				if(TextUtils.isEmpty(blacknumber)){
					Toast.makeText(getApplication(),"���������벻��Ϊ�գ�",0).show();
					return;
				}
				String mode = "3";
				if(cb_phone.isChecked()&&cb_sms.isChecked()){//ȫ������
					mode = "3";
				}else if(cb_phone.isChecked()){//�绰����
					mode = "1";
				}else if(cb_sms.isChecked()){ //��������
					mode = "2";
				}else{//û��ѡ���κ�����ģʽ
					Toast.makeText(getApplicationContext(),"��ѡ������ģʽ��", 0).show();
					return;
				}
				//����������ӵ����ݿ�
				dao.add(blacknumber,mode);
				//����listView���������
				BlackNumberInfo info = new BlackNumberInfo();
				info.setNumber(blacknumber);
				info.setMode(mode);
				infos.add(0,info); //������ݵ���һ��
				adpter.notifyDataSetChanged();//����listview����
				dialog.dismiss();
			}
		});
	}
}















