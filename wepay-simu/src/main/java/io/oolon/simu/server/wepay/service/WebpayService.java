package io.oolon.simu.server.wepay.service;

import java.util.Map;

import io.oolon.simu.server.wepay.bean.PayInfo;

public interface WebpayService {
	
	Map<String,Object> prePay(Map<String,Object> prePayMap);

	Map<String, Object> payed(Map<String, Object> requestMap);

	Map<String, Object> queryPayJSV3(String out_trade_no, String mchid);

	void noticeOnePay(PayInfo payinfo);

}
