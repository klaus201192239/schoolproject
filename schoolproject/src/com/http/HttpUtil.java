package com.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	/**
	 * 向指定 URL 发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */

	public static String doGet(String urlstr){

		
		System.out.println(urlstr);
		
		String l="";		
		try{

			URL url = new URL(urlstr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5*1000);
			if (conn.getResponseCode() != 200)    //从Internet获取网页,发送请求,将网页以流的形式读回来
			{
				throw new RuntimeException("请求url失败");
			}

			InputStream is = conn.getInputStream();
			InputStreamReader reader = new InputStreamReader(is,"utf-8");
			BufferedReader br = new BufferedReader(reader);
			String s = null;
			while ((s = br.readLine()) != null) {
				if (s != null) {
					s.trim();
					l = l + s;
				}
			}

			br.close();
			reader.close();
			l.trim();
		    conn.disconnect();
    
		}catch(Exception e){
		
			l="error";
		}		
		return l;
	}

	
	public static String sendGet(String url, String param) {
		String urlNameString = url + "?" + param;
		return sendGet(urlNameString);
	}

	public static String sendGet(String neturl) {
		
		return doGet(neturl);
		/*int i=0;
		String l="";
		
	//	System.out.println("OKOKOKOK");
		try{
			URL url = new URL(neturl);
			InputStreamReader reader = new InputStreamReader(url.openStream(),"utf-8");
			BufferedReader br = new BufferedReader(reader);
			String s = null;
			while ((s = br.readLine()) != null) {
				if (s != null) {
					s.trim();
					l = l + s;
				}
			}
			
			br.close();
			reader.close();
			l.trim();
		}catch(Exception e){
			if(i<3){
				i++;
				sendGet(neturl);
			}
			return "error";
		}
		return l;*/
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
		return "";
	}

}
