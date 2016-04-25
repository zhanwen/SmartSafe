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
 * 病毒数据库查询业务类
 * 
 * @author Hanwen
 * 
 */
public class AntivirusDao {

	/**
	 * 查询一个md5是否在病毒数据库里存在
	 * @param md5
	 * @return
	 */
	public static String isVirus(String md5) {
		String path = "/data/data/com.wolf.wolfsafe/files/antivirus.db";
		String result = null;
		//打开数据库文件
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[]{md5});
		
		if(cursor.moveToNext()) {
			result = cursor.getString(0);
		}
		
		cursor.close();
		db.close();
		return result;
	}
}
