package com.wolf.wolfsafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

/**
 * 短信的工具类
 * 
 * @author Hanwen
 * 
 */
public class SmsUtils {

	/**
	 * 备份短信回调接口
	 * @author Hanwen
	 *
	 */
	public interface BackUpCallBack{
		/**
		 * 设置进度条的最大值
		 * @param max 总进度
		 */
		public void beforeBackUp(int max);
		/**
		 * 备份过程中，增加进度
		 * @param progress 当前进度
		 */
		public void onSmsBackUp(int progress);
	}
	
	
	/**
	 * 备份用户的短信
	 * 
	 * @param context 上下文
	 * @param BackUpCallBack 备份短信的接口
	 */
	public static void backupSms(Context context, BackUpCallBack callBack) throws Exception {

		ContentResolver resolver = context.getContentResolver();
		File file = new File(Environment.getExternalStorageDirectory(),
				"backup.xml");
		FileOutputStream fos = new FileOutputStream(file);

		// 把用户的短信一条一条的读出来，按照一定的格式写道文件里
		XmlSerializer serializer = Xml.newSerializer(); // 获取xml文件的生成器（序列化器）
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[] { "body", "address",
				"type", "date" }, null, null, null);
		//开始备份时,设置进度条的最大值
		int max = cursor.getCount();
		serializer.attribute(null, "max", max+"");
//		pb.setMax(max);
//		pd.setMax(max);
		callBack.beforeBackUp(max);
		
		int process = 0;
		while (cursor.moveToNext()) {
			Thread.sleep(500);
			String body = cursor.getString(0);
			String address = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);
			serializer.startTag(null, "sms");
			
			serializer.startTag(null, "body");
			serializer.text(body);
			serializer.endTag(null, "body");
			
			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");
			
			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");
			
			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");

			serializer.endTag(null, "sms");
			//备份过程中，增加进度
			process++;
//			pb.setProgress(process);
//			pd.setProgress(process);
			callBack.onSmsBackUp(process);
		}
		cursor.close();
		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();

	}
	/**
	 * 还原短信
	 * @param context
	 * @param flag 是否保留原来的短信
	 * @throws Exception 
	 */
	public static void restoreSms(Context context, boolean flag) throws Exception {
		Uri uri = Uri.parse("content://sms/");
		if(flag) {//true则清除本身的短信
			context.getContentResolver().delete(uri, null, null);
		}		
		
		//1.读取sd卡上的xml文件
		//Xml.newPullParser();
		XmlPullParser pull = Xml.newPullParser();
		File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");
		FileInputStream fis = new FileInputStream(file);
		pull.setInput(fis,"utf-8");
		int eventType = pull.getEventType();
		String body = null;
		String date = null;
		String type = null;
		String address = null;
		ContentValues values = null;
		//2.读取max
		
		
		//3.读取每一条短信信息，body，data，type，address
		while(eventType != XmlPullParser.END_DOCUMENT){
			String tagName = pull.getName();
			switch (eventType) {
				case XmlPullParser.START_TAG: //如果是开始标签
					if("body".equals(tagName)){
						body = pull.nextText();
					}else if("date".equals(tagName)){
						date = pull.nextText();
					}else if("type".equals(tagName)){
						type = pull.nextText();
					}else if("address".equals(tagName)){
						address = pull.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					if("sms".equals(tagName)){
						values = new ContentValues();
						values.put("body",body);
						values.put("date",date);
						values.put("type",type);
						values.put("address",address);
						context.getContentResolver().insert(uri, values);
					}
					break;
			}
			eventType = pull.next();
		}
		fis.close();
		//4.把短信插入到系统的短信应用
		values = new ContentValues();
		values.put("body", "wo shi duan xin de neirong");
		values.put("data", "1395045035573");
		values.put("type", "1");
		values.put("address", "5558");
		context.getContentResolver().insert(uri, values);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
