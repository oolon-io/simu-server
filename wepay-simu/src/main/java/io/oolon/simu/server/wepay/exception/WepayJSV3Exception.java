package io.oolon.simu.server.wepay.exception;

import io.oolon.util.StringUtil;

/**
 * 
 * @author squall
 *
 */
public class WepayJSV3Exception extends RuntimeException {
	private static final long serialVersionUID = 1381325479896057076L;
	
	private int httpCode;

	private String code;

	private String message;

	public WepayJSV3Exception(int httpCode,String errorCode, String errorMsg, Object... args) {
		this.httpCode = httpCode;
		this.code = errorCode;
		this.message = StringUtil.fillingValue(errorMsg, args);
	}
	

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}
	
	

}

