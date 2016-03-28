package com.utilt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.activity.R;
import android.widget.EditText;

public class utils {
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean isPwdNO(String password) {
		Pattern p = Pattern.compile("[a-zA-Z0-9_]+$");
		Matcher m = p.matcher(password);
		return m.matches();
	}
	
	public static boolean isIntegralNO(String number) {
		Pattern p = Pattern.compile("[0-9]+$");
		Matcher m = p.matcher(number);
		return m.matches();
	}
	
	public static boolean isRealName(String name) {
		Pattern p = Pattern.compile("[a-zA-Z\u4e00-\u9fa5]+$");
		Matcher m = p.matcher(name);
		return m.matches();
	}
	
	public static boolean isEmailNO(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public static String deleEmoji(EditText text) {
		
		
		
		return text.getText().toString();
	}

	public static String getOutClassName(String x) {
		if (x.equals("1")) {
			return "讲座";
		}
		if (x.equals("2")) {
			return "比赛";
		}
		if (x.equals("3")) {
			return "公益";
		}
		if (x.equals("4")) {
			return "其他";
		}
		if (x.equals("5")) {
			return "其他";
		}
		return "";
	}

	public static int getSexHead(int sexId) {
		if (sexId == 0) {
			return R.drawable.head_picture;
		}
		return R.drawable.head_picture;
	}

	public static String getDegreeName(int degreeId) {
		if (degreeId == 0) {
			return "专科";
		}
		if (degreeId == 1) {
			return "本科";
		}
		if (degreeId == 2) {
			return "硕士";
		}
		if (degreeId == 3) {
			return "博士";
		}
		return "";
	}

	public static int getCommentIcon(int Position) {

		int commentPosition = Position % 9;

		if (commentPosition == 0) {
			return R.drawable.commentni1;
		}
		if (commentPosition == 1) {
			return R.drawable.commentni2;
		}
		if (commentPosition == 2) {
			return R.drawable.commentni3;
		}
		if (commentPosition == 3) {
			return R.drawable.commentni4;
		}
		if (commentPosition == 4) {
			return R.drawable.commentni5;
		}
		if (commentPosition == 5) {
			return R.drawable.commentni6;
		}
		if (commentPosition == 6) {
			return R.drawable.commentni7;
		}
		if (commentPosition == 7) {
			return R.drawable.commentni8;
		}
		if (commentPosition == 8) {
			return R.drawable.commentni9;
		}
		return R.drawable.commentni1;
	}

	public static int getTeamMemberIcon(int Position) {

		int commentPosition = Position % 7;

		if (commentPosition == 0) {
			return R.drawable.membe1;
		}
		if (commentPosition == 1) {
			return R.drawable.membe2;
		}
		if (commentPosition == 2) {
			return R.drawable.membe3;
		}
		if (commentPosition == 3) {
			return R.drawable.membe4;
		}
		if (commentPosition == 4) {
			return R.drawable.membe5;
		}
		if (commentPosition == 5) {
			return R.drawable.membe6;
		}
		if (commentPosition == 6) {
			return R.drawable.membe7;
		}
		return R.drawable.membe5;
	}

	public static int getOtherTeamIcon(int Position) {

		int commentPosition = Position % 9;

		if (commentPosition == 0) {
			return R.drawable.oteam1;
		}
		if (commentPosition == 1) {
			return R.drawable.oteam2;
		}
		if (commentPosition == 2) {
			return R.drawable.oteam3;
		}
		if (commentPosition == 3) {
			return R.drawable.oteam4;
		}
		if (commentPosition == 4) {
			return R.drawable.oteam5;
		}
		if (commentPosition == 5) {
			return R.drawable.oteam6;
		}
		if (commentPosition == 6) {
			return R.drawable.oteam7;
		}
		if (commentPosition == 7) {
			return R.drawable.oteam8;
		}
		if (commentPosition == 8) {
			return R.drawable.oteam9;
		}
		return R.drawable.oteam1;
	}

	public static int getSignInIcon(int commentPosition) {

		if (commentPosition == 1) {
			return R.drawable.yiqiandao;
		}
		if (commentPosition == 0) {
			return R.drawable.weiqiandao;
		}
		return R.drawable.weiqiandao;
	}

}
