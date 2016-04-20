package com.wolf.wolfsafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.wolf.wolfsafe.domain.AppInfo;

/**
 * ҵ�񷽷����ṩ�ֻ����氲װ�����е�Ӧ�ó�����Ϣ
 * @author Hanwen
 *
 */
public class AppInfoProvider {
	
	/**
	 * ��ȡ���а�װ��Ӧ�ó�����Ϣ
	 * @param context ������
	 * @return
	 */
	public static  List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		//���а�װ��ϵͳ�ϵ�Ӧ�ó������Ϣ.
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		
		for(PackageInfo packInfo : packInfos) {
			//packInfo �൱��һ��Ӧ�ó���apk�����嵥�ļ�
			String packname = packInfo.packageName;
			Drawable icon = packInfo.applicationInfo.loadIcon(pm); 
			String name = packInfo.applicationInfo.loadLabel(pm).toString();
			AppInfo appInfo = new AppInfo();
			appInfo.setPackname(packname);
			appInfo.setIcon(icon);
			appInfo.setName(name);
			appInfos.add(appInfo);
			
		}
		return appInfos;
	}
	

}
