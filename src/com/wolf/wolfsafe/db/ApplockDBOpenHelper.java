package com.wolf.wolfsafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ApplockDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * 数据库创建的构造方法 
	 * @param context 上下文
	 * @param name 数据库的名称
	 * @param factory 对应的工厂
	 * @param version 版本号
	 */
	public ApplockDBOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
	}

	/**
	 * 初始化数据表的结构
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
