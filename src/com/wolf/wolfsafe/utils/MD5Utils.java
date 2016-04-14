package com.wolf.wolfsafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

	/**
	 * md5���ܷ���
	 * @param password
	 * @return
	 */
	public static String md5Password(String password) {
		try {
			// �õ�һ����ϢժҪ��
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] result = digest.digest(password.getBytes());
			StringBuffer buffer = new StringBuffer();
			// Ҫ��ÿһ��btye��һ��λ����0xff
			for (byte b : result) {
				// ������
				int number = b & 0xff;  //���Σ����ǲ��ǷǴ�ͳ�ļ����ˣ����� b & 0xfff
				String str = Integer.toHexString(number);
				// System.out.println(str);
				if (str.length() == 1) {
					buffer.append("0");
				}
				buffer.append(str);
			}
			// ��׼��md5���ܺ�Ľ��
			return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}

	}

}
