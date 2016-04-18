package com.wolf.wolfsafe.test;

import java.util.List;
import java.util.Random;

import android.test.AndroidTestCase;

import com.wolf.wolfsafe.db.BlackNumberDBOpenHelper;
import com.wolf.wolfsafe.db.dao.BlackNumberDao;
import com.wolf.wolfsafe.domain.BlackNumberInfo;

public class TestBlackNumberDB extends AndroidTestCase {
	
	public void testCreateDB() {
		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
		
		helper.getWritableDatabase();
	}
	
	public void testAdd() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		long baseNumber = 13500000000l;
		Random random = new Random();
		for(int i = 0; i < 50; i++) {
			dao.add(String.valueOf(baseNumber + i), String.valueOf(random.nextInt(3)+1));
		}
		
	}
	
	public void testFindAll() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		List<BlackNumberInfo> infos = dao.findAll();
		for(BlackNumberInfo info : infos) {
			System.out.println(info.toString());
		}
	}
	
	public void testDelete() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.delete("110");
	}
	public void testupdate() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.update("110", "2");
	}
	public void tesFind() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		boolean result = dao.find("110");
		assertEquals(true, result);
	}
	
	
}
