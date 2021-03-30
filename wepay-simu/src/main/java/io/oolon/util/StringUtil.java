package io.oolon.util;

public class StringUtil {
	/**
	 * 替换字符串中的{x}
	 * x可以是字符串或数字
	 * 替换是按照{}出现的顺序，与{}中的内容无关
	 * @param value
	 * @param args
	 * @return
	 */
	public static String fillingValue(String value, Object[] args) {
		String result = value;
		if(args == null || args.length == 0) {
			return result;
		}

		for (Object arg : args) {
			if(arg instanceof CharSequence) {
				result = result.replaceFirst("\\{[^\\{]*\\}", arg.toString());
			}
		}
		return result;
	}
}
