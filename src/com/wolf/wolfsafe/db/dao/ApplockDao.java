package com.wolf.wolfsafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wolf.wolfsafe.db.ApplockDBOpenHelper;

/**
 * ��������dao
 * @author hanwen
 *
 */
public class ApplockDao {
	private ApplockDBOpenHelper helper;
	
	/**
	 * ���췽��
	 * @param context ������
	 */
	public ApplockDao(Context context) {
		helper = new ApplockDBOpenHelper(context);
	}
	
	/**
	 * ���һ��Ҫ����Ӧ�ó���İ���
	 */
	public void add(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("packname", packname);
		db.insert("applock", null, values);
		db.close();
		
	}
	/**
	 * ɾ��һ��Ҫ����Ӧ�ó���İ���
	 */
	public void delete(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applock", "packname=?", new String[]{packname});
		db.close();
		
	}
	
	/**
	 * ��ѯһ��������������¼�Ƿ����
	 * @param packname
	 * @return
	 */
	public boolean find(String packname) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", null, "packname=?", new String[]{packname}, null, null, null);
		if(cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
	
}
