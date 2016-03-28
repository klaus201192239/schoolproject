package com.utilt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class jsonUtil {

	public static JSONArray ListToJSONArray(List<Map<String, Object>> list) {
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			JSONObject jsonobject = MapToJSONObject(map);
			jsonArray.put(jsonobject);
		}
		return jsonArray;
	}

	public static List<Map<String, Object>> JsonArrayToList(String json) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		JSONArray jsonArray = null;

		try {

			jsonArray = new JSONArray(json);

		} catch (JSONException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int iSize = jsonArray.length();

		System.out.println(iSize);

		for (int i = 0; i < iSize; i++) {
			JSONObject jsonObj = null;
			try {
				jsonObj = (JSONObject) jsonArray.getJSONObject(i);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			list.add(JsonObjToMap(jsonObj));
		}
		return list;
	}

	public static Map<String, Object> JsonObjToMap(JSONObject jsonObj) {
		Map<String, Object> map = new HashMap<String, Object>();
		@SuppressWarnings("rawtypes")
		Iterator it = jsonObj.keys();
		// 遍历jsonObject数据，添加到Map对象
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			Object value = null;
			try {
				value = (Object) jsonObj.get(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			map.put(key, value);
		}
		return map;
	}

	public static JSONObject MapToJSONObject(Map<String, Object> map) {
		return new JSONObject(map);
	}
}

