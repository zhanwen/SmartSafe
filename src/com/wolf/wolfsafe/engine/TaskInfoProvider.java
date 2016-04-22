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
 * �ṩ�ֻ�����Ľ�����Ϣ
 * @author Hanwen
 *
 */
public class TaskInfoProvider {

	/**
	 * ��ȡ���еĽ�����Ϣ
	 * @param context ������
	 * @return
	 */
	public static List<TaskInfo> getTaskInfos(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		
		for(RunningAppProcessInfo processInfo : processInfos) {
			TaskInfo taskInfo = new TaskInfo();
			
			String packname = processInfo.processName; //Ӧ�ó���İ���
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
					//�û�����
					taskInfo.setUserTask(true);
				}else {
					//ϵͳ����
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
