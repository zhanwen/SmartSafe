package com.wolf.wolfsafe;

import com.wolf.wolfsafe.db.dao.NumberAddressQueryUtils;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberAddressActivity extends Activity {
	
	private static final String TAG = "NumberAddressActivity";
	private EditText ad_phone;
	private TextView result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address_query);
		
		ad_phone = (EditText) findViewById(R.id.ad_phone);
		result = (TextView) findViewById(R.id.result);
	}
	
	
	/**
	 * 查询号码归属地
	 * @param view
	 */
	public void numberAddressQuery(View view) {
		String phone = ad_phone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "号码为空", 0).show();
			return;
		}else {
			String address = NumberAddressQueryUtils.queryNumber(phone);
			result.setText(address);
			
			//去数据库查询号码归属地
			//1.网络查询 2.本地的数据库---数据库
			//写一个工具类，去查询数据库
			
			Log.i(TAG,"您要查询的电话号码==" + phone);
		}
	}
	
	
	
	
	
	
	
	
	
	
}
