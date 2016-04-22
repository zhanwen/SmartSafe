package com.wolf.wolfsafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.wolf.wolfsafe.domain.TaskInfo;

/**
 * 提供手机里面的进程信息
 * @author Hanwen
 *
 */
public class TaskInfoProvider {

	/**
	 * 获取所有的进程信息
	 * @param context 上下文
	 * @return
	 */
	public static List<TaskInfo> getTaskInfos(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		
		for(RunningAppProcessInfo processInfo : processInfos) {
			TaskInfo taskInfo = new TaskInfo();
			
			String packname = processInfo.processName; //应用程序的包名
			taskInfo.setPackname(packname);
			MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[]{processInfo.pid});
			long memsize = memoryInfo[0].getTotalPrivateDirty()*1024;
			taskInfo.setMemsize(memsize);
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packname, 0);
				Drawable icon = applicationInfo.loadIcon(pm);
				taskInfo.setIcon(icon);
				String name = applicationInfo.loadLabel(pm).toString();
				taskInfo.setName(name);
				if((applicationInfo.flags & applicationInfo.FLAG_SYSTEM) == 0) {
					//用户进程
					taskInfo.setUserTask(true);
				}else {
					//系统进程
					taskInfo.setUserTask(false);
				}
				
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			taskInfos.add(taskInfo);
		}
		
		return taskInfos;
	}
	
	
	
}
