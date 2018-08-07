package cn.hudun.wifi.pwd.utils;

import java.io.UnsupportedEncodingException;

public class CharacterUtil {
	public static String toGBK(String unicodeStr) {
		try {
			String gbkStr = new String(unicodeStr.getBytes("UTf-8"), "UTF-8");
			return gbkStr;
		} catch (UnsupportedEncodingException e) {
			return unicodeStr;
		}
	}
}
