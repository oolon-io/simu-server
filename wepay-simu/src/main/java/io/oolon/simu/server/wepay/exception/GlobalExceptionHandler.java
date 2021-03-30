package io.oolon.simu.server.wepay.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import io.oolon.util.JsonUtil;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(WepayJSV3Exception.class)
	@ResponseBody
	public ResponseEntity<String> handle(WepayJSV3Exception e) {
		LOGGER.error("出错了",e);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("code", e.getCode());
		responseMap.put("message", e.getMessage());
		return new ResponseEntity<String>(JsonUtil.getJson(responseMap), null, HttpStatus.valueOf(e.getHttpCode()));
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseBody
	public ResponseEntity<String> handle(MissingServletRequestParameterException e) {
		LOGGER.error("出错了",e);
		Map<String,Object> responseMap = new HashMap<>();
		responseMap.put("code", "PARAM_ERROR");
		responseMap.put("message", e.getMessage());
		return new ResponseEntity<String>(JsonUtil.getJson(responseMap), null, HttpStatus.valueOf(400));
	}

	@ExceptionHandler()
	public ResponseEntity<String> exceptionHandle(Exception e) {
		LOGGER.error("出错了",e);
		return new ResponseEntity<String>("SomeThingWrong", null, HttpStatus.INTERNAL_SERVER_ERROR);
	}


}
