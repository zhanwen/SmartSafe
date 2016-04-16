package com.wolf.wolfsafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wolf.wolfsafe.R;

/**
 * �Զ������Ͽؼ���������������TextView������һ��ImageView������һ��View
 * @author Hanwen
 *
 */
public class SettingClickView extends RelativeLayout {

	private TextView tv_desc;
	private TextView tv_title;
	
	private String desc_on;
	private String desc_off;
	
	/**
	 * ��ʼ�������ļ�
	 */
	private void initView(Context context) {
		//��һ�������ļ�--->>View������SettingItemView
		View.inflate(context, R.layout.setting_click_view, SettingClickView.this);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
	}
	
	
	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}


	/**
	 * �������������Ĺ��췽���������ļ���ʹ�õ�ʱ�����
	 * @param context
	 * @param attrs
	 */
	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.wolf.wolfsafe", "title");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.wolf.wolfsafe", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.wolf.wolfsafe", "desc_off");
		tv_title.setText(title);
		setDesc(desc_off);
		
	}

	public SettingClickView(Context context) {
		super(context);
		initView(context);
	}
	
	
	
	/**
	 * ������Ͽؼ���״̬
	 */
	public void setChecked(boolean checked) {
		if(checked) {
			setDesc(desc_on);
		}else {
			setDesc(desc_off);
		}
		
	}
	
	/**
	 * ��Ͽؼ���������Ϣ
	 */
	public void setDesc(String text) {
		tv_desc.setText(text);
	}
	
	/**
	 * ������Ͽؼ��ı���
	 */
	public void setTitle(String text) {
		tv_title.setText(text);
	}
	
}
