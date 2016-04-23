package com.wolf.wolfsafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ApplockDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * ���ݿⴴ���Ĺ��췽�� 
	 * @param context ������
	 * @param name ���ݿ������
	 * @param factory ��Ӧ�Ĺ���
	 * @param version �汾��
	 */
	public ApplockDBOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
	}

	/**
	 * ��ʼ�����ݱ�Ľṹ
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table applock (_id integer primary key autoincrement,packname varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
