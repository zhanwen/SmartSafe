package com.wolf.wolfsafe.test;

import java.util.List;

import android.test.AndroidTestCase;

import com.wolf.wolfsafe.domain.TaskInfo;
import com.wolf.wolfsafe.engine.TaskInfoProvider;

public class TestTaskInfoProvider extends AndroidTestCase {

	public void testGetTaskInfos() throws Exception {
		List<TaskInfo> infos = TaskInfoProvider.getTaskInfos(getContext());
		
		for(TaskInfo info : infos) {
			
			System.out.println(info.toString());
		}
		
	}
	
	
}
