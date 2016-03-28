package com.utilt;

import android.annotation.SuppressLint;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class timeutil {
	public static Timestamp StringToTimestamp(String tsStr) {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		// String tsStr = "2011-05-09 11:49:45";
		try {
			ts = Timestamp.valueOf(tsStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ts;
	}

	@SuppressLint("SimpleDateFormat")
	public static String TimestampToString(Timestamp ts) {
		// Timestamp ts = new Timestamp(System.currentTimeMillis());
		String tsStr = "";
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			// 方法一
			tsStr = sdf.format(ts);
			// System.out.println(tsStr);
			// 方法二
			// tsStr = ts.toString();
			// System.out.println(tsStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tsStr;
	}
}
