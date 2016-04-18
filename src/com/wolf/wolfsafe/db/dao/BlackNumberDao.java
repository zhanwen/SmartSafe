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
 * ���������ݿ����ɾ�Ĳ�ҵ��
 * @author Hanwen
 *
 */
public class BlackNumberDao {
	
	private BlackNumberDBOpenHelper helper;
	
	
	/**
	 * ���췽��
	 * @param context ������
	 */
	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}
	
	/**
	 * ��ѯ�����������Ƿ����
	 * @param number
	 * @return
	 */
	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber where number=?", new String[]{number});
		
		if(cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * ��ѯȫ������������
	 * @param number
	 * @return
	 */
	public List<BlackNumberInfo> findAll() {
		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc", null);
		
		while(cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setNumber(number);
			info.setMode(mode);
			result.add(info);
		}
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * ��Ӻ���������
	 * @param number ����������
	 * @param mode ����ģʽ 1.�绰���� 2.�������� 3.ȫ������
	 */
	public void add(String number, String mode) {
		SQLiteDatabase db = helper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		db.close();
	}
	
	/**
	 * �޸ĺ��������������ģʽ
	 * @param number Ҫ�޸ĵĺ���������
	 * @param mode �µ�����ģʽ
	 */
	public void update(String number, String newmode) {
		SQLiteDatabase db = helper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", newmode);
		db.update("blacknumber", values, "number=?", new String[]{number});
		db.close();
	}
	
	/**
	 * ɾ������������
	 * @param number Ҫɾ���ĺ���������
	 */
	public void delete(String number) {
		SQLiteDatabase db = helper.getReadableDatabase();
		db.delete("blacknumber", "number=?", new String[]{number});
		db.close();
	}
	
	
	
	
	
	
	
	
	
	
	
}
