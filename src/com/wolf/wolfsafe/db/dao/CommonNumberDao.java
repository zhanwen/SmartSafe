package com.wolf.wolfsafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumberDao {
	public static final String path = "/data/data/com.itheima.mobilesafe/files/commonnum.db";
	/**
	 * ��ȡ�ж��ٸ�����
	 * @return
	 */
	public static int getGroupCount(SQLiteDatabase db ){
		//SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select count(*) from classlist", null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		//db.close();
		return count;
	}
	
	/**
	 * ��ȡĳ�����������ж��ٸ�����
	 * @return
	 */
	public static int getChildCountByPosition(SQLiteDatabase db ,int groupPosition){
		int newposition = groupPosition +1;
		//SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select count(*) from table"+newposition, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		//db.close();
		return count;
	}
	/**
	 * ��ȡĳ�����������
	 */
	public static String getGroupName(SQLiteDatabase db ,int groupPosition){
		int newposition = groupPosition +1;
		//SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select name from classlist where idx=?", new String[]{newposition+""});
		cursor.moveToFirst();
		String name = cursor.getString(0);
		cursor.close();
		//db.close();
		return name;
	}
	/**
	 * ��ȡĳ�������ĳ�����ӵ�����
	 */
	public static String getChildNameByPosition(SQLiteDatabase db ,int groupPosition,int childPosition){
		int newGroupPosition = groupPosition + 1;
		int newChildPosition = childPosition + 1;
		//SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select name,number from table"+newGroupPosition+" where _id=?", new String[]{newChildPosition+""});
		cursor.moveToFirst();
		String name = cursor.getString(0);
		String number = cursor.getString(1);
		cursor.close();
		//db.close();
		return name+"\n"+number;
	}
	
}
