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
		//listview注册一个滚动事件的监听器
		lv_callsms_safe.setOnScrollListener(new OnScrollListener() {
			//当滚动的状态发生变化的时候
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
					case OnScrollListener.SCROLL_STATE_IDLE: //空闲状态
						System.out.println("空闲状态");
						//判断当前listview滚动的位置
						int lastposition = lv_callsms_safe.getLastVisiblePosition();//获取最后一个可见条目在集合里面的位置
						//集合里面有20item 位置从0开始 最后一个位置是19
						if(lastposition == (infos.size()-1)){
							offset += maxnumber;
							fillData();
						}
						break;
					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: //手指触摸滚动
						break;
					case OnScrollListener.SCROLL_STATE_FLING: //惯性滑行状态
						break;
				}
			}
			//滚动的时候调用的方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
	}  
	//填充数据
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
	 * 黑名单号码 适配器
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
				//当view创建的时候找到它们的引用存放到tag中
				view.setTag(holder);
			}else{
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			holder.tv_number.setText(infos.get(position).getNumber());
			String mode = infos.get(position).getMode();
			if("1".equals(mode)){
				holder.tv_mode.setText("拦截电话");
			}else if("2".equals(mode)){
				holder.tv_mode.setText("拦截短信");
			}else if("3".equals(mode)){
				holder.tv_mode.setText("全部拦截");
			}
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
					builder.setTitle("警告");
					builder.setMessage("确定要删除这条记录么?");
					builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//删除数据库的内容
							dao.delete(infos.get(position).getNumber());
							//更新界面
							infos.remove(position);
							//通知listview数据适配器更新
							adpter.notifyDataSetChanged();
						}
					});
					builder.setNegativeButton("取消",null);
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
	 * View对象的容器
	 * 记录id  相当于一个记事本
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
	//添加黑名单号码
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
				dialog.dismiss();//关闭对话框
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String blacknumber = et_blacknumber.getText().toString().trim();
				if(TextUtils.isEmpty(blacknumber)){
					Toast.makeText(getApplication(),"黑名单号码不能为空！",0).show();
					return;
				}
				String mode = "3";
				if(cb_phone.isChecked()&&cb_sms.isChecked()){//全部拦截
					mode = "3";
				}else if(cb_phone.isChecked()){//电话拦截
					mode = "1";
				}else if(cb_sms.isChecked()){ //短信拦截
					mode = "2";
				}else{//没有选择任何拦截模式
					Toast.makeText(getApplicationContext(),"请选择拦截模式！", 0).show();
					return;
				}
				//黑名单号码加到数据库
				dao.add(blacknumber,mode);
				//更新listView里面的内容
				BlackNumberInfo info = new BlackNumberInfo();
				info.setNumber(blacknumber);
				info.setMode(mode);
				infos.add(0,info); //添加数据到第一条
				adpter.notifyDataSetChanged();//更新listview内容
				dialog.dismiss();
			}
		});
	}
}















