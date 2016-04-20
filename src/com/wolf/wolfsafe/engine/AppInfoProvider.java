package com.wolf.wolfsafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.wolf.wolfsafe.domain.AppInfo;

/**
 * 业务方法，提供手机里面安装的所有的应用程序信息
 * @author Hanwen
 *
 */
public class AppInfoProvider {
	
	/**
	 * 获取所有安装的应用程序信息
	 * @param context 上下文
	 * @return
	 */
	public static  List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		//所有安装在系统上的应用程序包信息.
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		AppInfo appInfo = null;
		for(PackageInfo packInfo : packInfos) {
			//packInfo 相当于一个应用程序apk包的清单文件
			String packname = packInfo.packageName;
			Drawable icon = packInfo.applicationInfo.loadIcon(pm); 
			String name = packInfo.applicationInfo.loadLabel(pm).toString();
			int flags = packInfo.applicationInfo.flags; //应用程序的标记,相当于用户提交的答卷
			appInfo = new AppInfo();
			if((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				//用户程序
				appInfo.setUserApp(true);
			}else {
				//系统程序
				appInfo.setUserApp(false);
			}
			if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				//手机的内存
				appInfo.setInRom(true);
			}else {
				//手机外存储设备
				appInfo.setInRom(false);
			}
			
			
			
			appInfo.setPackname(packname);
			appInfo.setIcon(icon);
			appInfo.setName(name);
			appInfos.add(appInfo);
			
		}
		return appInfos;
	}
	

}
