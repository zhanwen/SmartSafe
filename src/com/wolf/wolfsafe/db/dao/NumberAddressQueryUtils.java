package com.wolf.wolfsafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {

	private static String path = "data/data/com.wolf.wolfsafe/files/address.db";

	/**
	 * ��һ���������������һ�����ػ�ȥ
	 * 
	 * @param number
	 * @return
	 */
	public static String queryNumber(String number) {
		
		String address = number;
		
		// path ��address.db������ݿ⿽����/data/data/<����>/files/address.db

		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);

		Cursor cursor =  database.rawQuery(
				"select location from data2 where id = (select outkey from data1 where id = ?)",
				new String[] { number.substring(0, 7)});
		
		while(cursor.moveToNext()) {
			String location = cursor.getString(0);
			address = location;
		}
		cursor.close();

		return address;
	}

}
