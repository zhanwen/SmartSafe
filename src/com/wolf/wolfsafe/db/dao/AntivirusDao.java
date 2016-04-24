package com.wolf.wolfsafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wolf.wolfsafe.db.BlackNumberDBOpenHelper;
import com.wolf.wolfsafe.domain.BlackNumberInfo;

/**
 * �������ݿ��ѯҵ����
 * 
 * @author Hanwen
 * 
 */
public class AntivirusDao {

	/**
	 * ��ѯһ��md5�Ƿ��ڲ������ݿ������
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(String md5) {
		String path = "/data/data/com.wolf.wolfsafe/files/antivirus.db";
		boolean result = false;
		//�����ݿ��ļ�
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[]{md5});
		
		if(cursor.moveToNext()) {
			result = true;
		}
		
		cursor.close();
		db.close();
		return result;
	}
}
