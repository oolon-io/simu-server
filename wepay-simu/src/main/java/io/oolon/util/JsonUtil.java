package io.oolon.util;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
	
	private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
	
	public static Map<String,Object> json2Map(String jsonstr){
		Map<String,Object> rspMap = null;
		try {
			rspMap = DEFAULT_MAPPER.readValue(jsonstr, new TypeReference<Map<String, Object>>(){
			});
		} catch (IOException e) {
			LOGGER.error("Json " + jsonstr + " trans2map failed!",e);
		}
		return rspMap;
	}
	
	public static String getJson(Object obj){
		String json = null;
		try {
			json =  DEFAULT_MAPPER.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			LOGGER.error("json format error {} {}", obj,e);
			throw new RuntimeException(e.getMessage());
		}
		return json;
	}

}
