package io.oolon.simu.server.wepay.service;

import java.util.Map;

import io.oolon.simu.server.wepay.bean.PayInfo;
import io.oolon.simu.server.wepay.bean.RefundInfo;

public interface WepayService {
	
	Map<String,Object> prePay(Map<String,Object> prePayMap);

	Map<String, Object> payed(Map<String, Object> requestMap);

	Map<String, Object> queryPayJSV3(String out_trade_no, String mchid);

	void noticeOnePay(PayInfo payinfo);

	Map<String, Object> refund(Map<String, Object> refundMap);

	Map<String, Object> queryRefundJSV3(String out_refund_no);

	void noticeOneRefund(RefundInfo refundInfo);

}
