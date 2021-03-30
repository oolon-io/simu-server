package io.oolon.simu.util;

import java.util.Collection;

import io.oolon.simu.server.wepay.exception.WepayJSV3Exception;

public class CheckUtil {
	public static CheckUtil check(Object obj, String tagName) {
		if(obj == null) {
			throw new WepayJSV3Exception(400,"PARAM_ERROR ", tagName + "未上送");
		}
		if(obj instanceof String) {
			if("".equals(obj)) {
				throw new WepayJSV3Exception(400,"PARAM_ERROR ", tagName + "未上送");
			}
		}
		if(obj instanceof Collection )
		{
			if(((Collection) obj).size() == 0) {
				throw new WepayJSV3Exception(400,"PARAM_ERROR ", tagName + "未上送");
			}
		}
		return null;
	}
}
