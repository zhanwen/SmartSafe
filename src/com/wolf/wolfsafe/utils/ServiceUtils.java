package com.wolf.wolfsafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {
	
	
	
	/**
	 * У��ĳ�������Ƿ�������
	 * @param context
	 * @param serviceName �������ķ��������
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String serviceName) {
		
		//�������Activity�����Թ���Service
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(1000);
		
		for(RunningServiceInfo info : infos) {
			//�õ��������еķ��������
			String name = info.service.getClassName();
			if(serviceName.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	
}
