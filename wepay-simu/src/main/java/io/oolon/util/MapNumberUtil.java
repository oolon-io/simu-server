package io.oolon.util;

import java.util.Map;

public class MapNumberUtil {

	public static Long mapElement2Long(Map<String, Object> map, String key) {
		Object obj = map.get(key);
		Long returnLong = null;
		if (obj == null) {
			return null;
		}
		if (obj instanceof Number) {
			if (obj instanceof Byte || obj instanceof Short || obj instanceof Integer || obj instanceof Long) {
				returnLong = ((Number) obj).longValue();
			} else {
				throw new RuntimeException("mapElement2Long error " + key + " 精度损失");
			}
		} else if (obj instanceof String) {
			returnLong = Long.valueOf(obj.toString());
		} else {
			throw new RuntimeException("mapElement2Long error " + key + " 数据类型错误");
		}
		return returnLong;
	}
	
	public static Integer mapElement2Integer(Map<String, Object> map, String key) {
		Object obj = map.get(key);
		Integer returnInteger = null;
		if (obj == null) {
			return null;
		}
		if (obj instanceof Number) {
			if (obj instanceof Byte || obj instanceof Short || obj instanceof Integer) {
				returnInteger = ((Number) obj).intValue();
			} else {
				throw new RuntimeException("mapElement2Long error " + key + " 精度损失");
			}
		} else if (obj instanceof String) {
			returnInteger = Integer.valueOf(obj.toString());
		} else {
			throw new RuntimeException("mapElement2Long error " + key + " 数据类型错误");
		}
		return returnInteger;
	}

}
