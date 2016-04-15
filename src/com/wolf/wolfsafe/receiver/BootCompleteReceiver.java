package com.wolf.wolfsafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		boolean protecting = sp.getBoolean("protecting", false);
		if(protecting) {
			//�������������Ż�ִ������ط�
			tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
			
			//��ȡ֮ǰ�����sim��Ϣ
			String saveSim = sp.getString("sim", "") + "hanwen";
			
			//��ȡ��ǰ��sim����Ϣ
			String realSim = tm.getSimSerialNumber();
			
			//�Ƚ��Ƿ�һ��
			if(saveSim.equals(realSim)) {
				//sim��û�б��,����ͬһ��sim��
			}else {
				//sim���Ѿ����,��һ�����Ÿ�һ����ȫ����
				System.out.println("sim���Ѿ����,��һ�����Ÿ�һ����ȫ����");
				Toast.makeText(context, "sim���Ѿ����", 1).show();
				
				SmsManager.getDefault().sendTextMessage(sp.getString("safenumber", ""), null, "sim changing...", null, null);
				
			}
		}
		
		
	}

}
