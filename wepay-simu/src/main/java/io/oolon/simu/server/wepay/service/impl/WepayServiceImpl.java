package io.oolon.simu.server.wepay.service.impl;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import io.oolon.http.HttpUtil;
import io.oolon.simu.server.wepay.bean.PayInfo;
import io.oolon.simu.server.wepay.bean.RefundInfo;
import io.oolon.simu.server.wepay.exception.WepayJSV3Exception;
import io.oolon.simu.server.wepay.mapper.PayInfoMapper;
import io.oolon.simu.server.wepay.mapper.RefundInfoMapper;
import io.oolon.simu.server.wepay.service.WepayService;
import io.oolon.simu.server.wepay.util.Constants;
import io.oolon.simu.util.CheckUtil;
import io.oolon.util.JsonUtil;
import io.oolon.util.MapNumberUtil;

@Service
public class WepayServiceImpl implements WepayService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WepayServiceImpl.class);

	@Resource
	private PayInfoMapper payInfoMapper;
	@Resource
	private RefundInfoMapper refundInfoMapper;

	@Override
	public Map<String, Object> prePay(Map<String, Object> prePayMap) {
		String mchid = (String) prePayMap.get("mchid");
		String appid = (String) prePayMap.get("appid");
		String description = (String) prePayMap.get("description");
		String out_trade_no = (String) prePayMap.get("out_trade_no");
		String notify_url = (String) prePayMap.get("notify_url");
		Map<String, Object> amount = (Map<String, Object>) prePayMap.get("amount");
		CheckUtil.check(amount, "amount");
		Map<String, Object> payer = (Map<String, Object>) prePayMap.get("payer");
		CheckUtil.check(payer, "payer");
		Integer amount_total = MapNumberUtil.mapElement2Integer(amount, "total");
		String payer_openid = (String) payer.get("openid");
		CheckUtil.check(mchid, "mchid").check(appid, "appid").check(description, "description")
				.check(out_trade_no, "out_trade_no").check(notify_url, "notify_url").check(amount_total, "amount_total")
				.check(payer_openid, "openid");

		prePayMap.remove("amount");
		prePayMap.remove("payer");
		prePayMap.put("total", amount_total);
		prePayMap.put("openid", payer_openid);

		String time_expire_str = (String) prePayMap.get("time_expire");
		prePayMap.remove("time_expire");
		PayInfo payInfo = BeanUtil.toBean(prePayMap, PayInfo.class);

		LOGGER.debug("payInfo first: {}", payInfo);
		Date time_expire = null;
		if (time_expire_str != null && !"".equals(time_expire_str)) {
			try {
				time_expire = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(time_expire_str);
			} catch (ParseException e) {
				LOGGER.error("时间格式错误" + time_expire_str, e);
			}
		}
		payInfo.setTime_expire(time_expire);
		payInfo.setPrepay_id(UUID.fastUUID().toString().replaceAll("-", ""));
		payInfo.setTrade_state(Constants.PAY_STATUS_NOTPAY);
		payInfo.setTrade_type(Constants.PAY_TYPE_JSAPI);
		LOGGER.debug("payInfo last: {}", payInfo);
		PayInfo now = payInfoMapper.queryPayInfo(payInfo);
		LOGGER.debug("payInfo in DB: {}", now);
		if (now == null || Constants.PAY_STATUS_NOTPAY.equals(now.getTrade_state())
				|| Constants.PAY_STATUS_PAYERROR.equals(now.getTrade_state()))
		{
			try {
				payInfoMapper.savePayInfo(payInfo);
			} catch (Exception e) {
				LOGGER.error("数据库错误", e);
				// 先统一返回重复的订单
				throw new WepayJSV3Exception(403, "OUT_TRADE_NO_USED", "订单号{1}重复", out_trade_no);
			}
		}else {
			LOGGER.error("已经有此记录且状态不是未支付和支付错误 {}", payInfo);
			// 先统一返回重复的订单
			throw new WepayJSV3Exception(403, "OUT_TRADE_NO_USED", "订单号{1}重复已经有此记录且状态不是未支付和支付错误", out_trade_no);
		}

		Map<String, Object> rspMap = new HashMap<>();
		rspMap.put("prepay_id", payInfo.getPrepay_id());
		return rspMap;
	}

	@Override
	public Map<String, Object> payed(Map<String, Object> requestMap) {
		String appid = (String) requestMap.get("appid");
		String timeStamp = (String) requestMap.get("timeStamp");
		String nonceStr = (String) requestMap.get("nonceStr");
		String packageStr = (String) requestMap.get("package");
		String signType = (String) requestMap.get("signType");
		String paySign = (String) requestMap.get("paySign");

		CheckUtil.check(appid, "appid").check(timeStamp, "timeStamp").check(nonceStr, "nonceStr")
				.check(packageStr, "packageStr").check(signType, "signType").check(paySign, "paySign");

		if (!"RSA".equals(signType)) {
			throw new WepayJSV3Exception(400, "PARAM_ERROR ", "signType非RSA");
		}
		if (!packageStr.startsWith("prepay_id=")) {
			throw new WepayJSV3Exception(400, "PARAM_ERROR ", "package不是以prepay_id=开头");
		}
		// TODO 验签名未实现
		String prepay_id = packageStr.substring(10);
		PayInfo payInfo = new PayInfo();
		payInfo.setPrepay_id(prepay_id);
		
		PayInfo now = payInfoMapper.queryPayInfo(payInfo);
		if(!Constants.PAY_STATUS_NOTPAY.equals(now.getTrade_state())) {
			throw new WepayJSV3Exception(400, "PARAM_ERROR ", "订单状态非未支付!!!");
		}
		// TODO realCheck 支付时间是否过期未实现 
		payInfo.setAppid(appid);
		payInfo.setTrade_state(Constants.PAY_STATUS_SUCCESS);
		payInfo.setTransaction_id(UUID.fastUUID().toString().replaceAll("-", ""));
		payInfo.setSuccess_time(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date()));
        //TODO 先统一设置为30秒 后推送成功
		payInfo.setPay_notice_time(new Date(new Date().getTime() + 1000*30));
		int rows = payInfoMapper.updatePayInfoInPayed(payInfo);
		if (rows != 1) {
			throw new WepayJSV3Exception(400, "PARAM_ERROR ", "prepay_id或者appid不对");
		}

		return new HashMap<String, Object>();
	}

	@Override
	public Map<String, Object> queryPayJSV3(String out_trade_no,String mchid) {
		CheckUtil.check(out_trade_no, "商户订单号").check(mchid, "商户号");
		PayInfo queryTmp = new PayInfo();
		queryTmp.setOut_trade_no(out_trade_no);
		queryTmp.setMchid(mchid);
		PayInfo response = payInfoMapper.queryPayInfo(queryTmp);
		LOGGER.info("queryPayJSV3 query response {}", response);
		if(response == null) {
			throw new WepayJSV3Exception(404, "ORDERNOTEXIST ", "商户号{} 订单号{} 未找到订单",mchid,out_trade_no);
		}
		//TODO 订单已关闭报错返回
		Map<String, Object> map = genPayResponseMap(response);

		return map;
	}

	@Override
	public void noticeOnePay(PayInfo payinfo) {
		Map<String, Object>  ciphertextMap= genPayResponseMap(payinfo);
		//String ciphertext = JsonUtil.getJson(map);
		Map<String, Object> sendMap = new HashMap<>();
		sendMap.put("id", UUID.fastUUID().toString().replaceAll("-", ""));
		sendMap.put("create_time",new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date()));
		sendMap.put("event_type","TRANSACTION.SUCCESS");
		sendMap.put("resource_type","encrypt-resource");
		sendMap.put("summary","支付成功");
		Map<String,Object> resource = new HashMap<>();
		resource.put("algorithm", "AEAD_AES_256_GCM");
		resource.put("original_type", "transaction");
		//等我太闲了把加密做了 BEGIN
		resource.put("associated_data", "TODO");
		resource.put("nonce", "nonceTODO");
		resource.put("ciphertext", ciphertextMap);
		sendMap.put("resource", resource);
		//等我太闲了把加密做了 END
		try {
			HttpUtil.doPost(payinfo.getNotify_url(), JsonUtil.getJson(sendMap));
			payInfoMapper.changePayNoticedFlag(payinfo);
		} catch (URISyntaxException e) {
			LOGGER.error("回调失败" + payinfo.getNotify_url() + "  by \n",e);
		}
	}
	
	private Map<String,Object> genPayResponseMap(PayInfo payinfo){
		Map<String, Object> map  = BeanUtil.beanToMap(payinfo,false,true);
		LOGGER.info("queryPayJSV3 query responseMap {}", map);
		map.remove("notify_url");
		String openid = (String) map.get("openid");
		map.remove("openid");
		Map<String,Object> payerMap = new HashMap<>();
		payerMap.put("openid", openid);
		map.put("payer", payerMap);
		if(Constants.PAY_STATUS_SUCCESS.equals(payinfo.getTrade_state())) {
			Object total =  map.get("total");
			map.remove("total");
			Map<String,Object> amount = new HashMap<>();
			amount.put("total", total);
			//写死一样吧，如果你有优惠券。。。这个目前不支持也不计划支持
			amount.put("payer_total", total);
			map.put("amount", amount);
		}else {
			map.remove("total");
		}
        map.remove("time_expire");
        map.remove("prepay_id");
        map.remove("pay_notice_time");
        return map;
	}
	
	@Override
	public Map<String, Object> refund(Map<String, Object> refundMap) {
		String transaction_id = (String) refundMap.get("transaction_id");
		String out_trade_no = (String) refundMap.get("out_trade_no");
		String out_refund_no = (String) refundMap.get("out_refund_no");
		String notify_url = (String) refundMap.get("notify_url");
		Map<String, Object> amount = (Map<String, Object>) refundMap.get("amount");
		CheckUtil.check(amount, "amount");
		Integer amount_total = MapNumberUtil.mapElement2Integer(amount, "total");
		Integer amount_refund = MapNumberUtil.mapElement2Integer(amount, "refund");
		String currency = (String) amount.get("currency");
		CheckUtil.check(out_refund_no, "out_refund_no").check(amount_total, "amount_total")
				.check(amount_refund, "amount_refund");
		if((transaction_id == null || "".equals(transaction_id)) && (out_trade_no == null || "".equals(out_trade_no))) {
			throw new WepayJSV3Exception(400,"PARAM_ERROR ", "transaction_id 或 out_trade_no 必上送一个");
		}
		if(notify_url == null || "".equals(notify_url)) {
			throw new WepayJSV3Exception(400,"PARAM_ERROR ", "模拟器尚未支持配置 notify_url 请上送。。。");
		}
		if(!"CNY".equals(currency)) {
			throw new WepayJSV3Exception(400,"PARAM_ERROR ", "微信只支持人民币退款");
		}

		refundMap.remove("amount");
		refundMap.put("total", amount_total);
		refundMap.put("refund", amount_refund);
		refundMap.put("currency",currency);
		PayInfo payInfo = new PayInfo();
		payInfo.setTransaction_id(transaction_id);
		payInfo.setOut_trade_no(out_trade_no);
		payInfo.setTrade_state(Constants.PAY_STATUS_SUCCESS);
		payInfo = payInfoMapper.queryPayInfo(payInfo);
		LOGGER.debug("payInfo now: {}", payInfo);
		if(payInfo == null) {
			throw new WepayJSV3Exception(404,"RESOURCE_NOT_EXISTS ", "请检查你的订单号是否正确且是否已支付，未支付的订单不能发起退款");
		}
		
		RefundInfo refundInfo = BeanUtil.toBean(refundMap, RefundInfo.class);
		refundInfo.setTransaction_id(payInfo.getTransaction_id());
		refundInfo.setOut_trade_no(payInfo.getOut_trade_no());
		refundInfo.setRefund_id(UUID.fastUUID().toString().replaceAll("-", ""));
		refundInfo.setCreate_time(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date()));
		refundInfo.setIs_pay_noticed(0);
		//先设置成成功，简单点
		refundInfo.setStatus(Constants.REFUND_SUCCESS);
		//TODO 先统一30秒推送退款成功吧
		
		refundInfo.setRefund_notice_time(new Date(new Date().getTime() + 1000*30));
		LOGGER.debug("refundInfo before insert: {}", refundInfo);
		refundInfoMapper.saveRefundInfo(refundInfo);
		
        //TODO 抽个烟
		Map<String, Object> rspMap = new HashMap<>();
		rspMap.putAll(refundMap);
		rspMap.remove("total");
		rspMap.remove("refund");
		rspMap.remove("currency",currency);
		rspMap.remove("notify_url");
		
		rspMap.put("refund_id",refundInfo.getRefund_id());
	    //偷懒
		amount.put("payer_total",amount_total);
		amount.put("payer_refund",amount_total);
		amount.put("settlement_refund",amount_total);
		amount.put("settlement_total",amount_total);
		amount.put("discount_refund",amount_total);
		
		rspMap.put("amount", amount);
		rspMap.put("channel", "ORIGINAL");
		rspMap.put("user_received_account", "IDONTKNOW");
		rspMap.put("status", Constants.REFUND_PROCESSING);

		return rspMap;
	}

	@Override
	public Map<String, Object> queryRefundJSV3(String out_refund_no) {
		CheckUtil.check(out_refund_no, "商户退款单号");
		RefundInfo queryTmp = new RefundInfo();
		queryTmp.setOut_refund_no(out_refund_no);
		RefundInfo response = refundInfoMapper.queryRefundInfo(queryTmp);
		LOGGER.info("queryRefundJSV3 query response {}", response);
		if(response == null) {
			throw new WepayJSV3Exception(404, "ORDERNOTEXIST ", "退款单号{} 未找到订单",out_refund_no);
		}
		Map<String, Object> map  = BeanUtil.beanToMap(response,false,true);
		map.remove("notify_url");
		Integer refund = MapNumberUtil.mapElement2Integer(map, "refund");
		Integer total = MapNumberUtil.mapElement2Integer(map, "total");
		String currency = (String) map.get("currency");
		Map<String, Object> amount = new HashMap<>();
		amount.put("refund", refund);
		amount.put("total", total);
		amount.put("currency", currency);
		amount.put("payer_total",refund);
		amount.put("payer_refund",refund);
		amount.put("settlement_refund",refund);
		amount.put("settlement_total",refund);
		amount.put("discount_refund",refund);
		map.put("amount", amount);
		map.remove("refund");
		map.remove("total");
		map.remove("currency");
		map.remove("refund_notice_time");
		map.remove("is_pay_noticed");
		map.put("success_time", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date()));
		map.put("channel", "ORIGINAL");
		map.put("user_received_account", "IDONTKNOW");
		
		
		
		return map;
	}

	@Override
	public void noticeOneRefund(RefundInfo refundInfo) {
		Map<String, Object>  ciphertextMap= genRefundResponseMap(refundInfo);
		//String ciphertext = JsonUtil.getJson(map);
		Map<String, Object> sendMap = new HashMap<>();
		sendMap.put("id", UUID.fastUUID().toString().replaceAll("-", ""));
		sendMap.put("create_time",new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date()));
		sendMap.put("event_type","REFUND.SUCCESS");
		sendMap.put("resource_type","encrypt-resource");
		sendMap.put("summary","退款成功");
		Map<String,Object> resource = new HashMap<>();
		resource.put("algorithm", "AEAD_AES_256_GCM");
		resource.put("original_type", "refund");
		//等我太闲了把加密做了 BEGIN
		resource.put("associated_data", "TODO");
		resource.put("nonce", "nonceTODO");
		resource.put("ciphertext", ciphertextMap);
		sendMap.put("resource", resource);
		//等我太闲了把加密做了 END
		try {
			HttpUtil.doPost(refundInfo.getNotify_url(), JsonUtil.getJson(sendMap));
			refundInfoMapper.changeRefundNoticedFlag(refundInfo);
		} catch (URISyntaxException e) {
			LOGGER.error("回调失败" + refundInfo.getNotify_url() + "  by \n",e);
		}
		
	}

	private Map<String, Object> genRefundResponseMap(RefundInfo refundInfo) {
		PayInfo payInfo = new PayInfo();
		payInfo.setOut_trade_no(refundInfo.getOut_trade_no());
		payInfo = payInfoMapper.queryPayInfo(payInfo);
		Map<String, Object> map  = new HashMap<>();
		map.put("mchid", payInfo.getMchid());
		map.put("transaction_id", refundInfo.getTransaction_id());
		map.put("out_trade_no", refundInfo.getOut_trade_no());
		map.put("out_refund_no", refundInfo.getOut_refund_no());
		map.put("refund_status", refundInfo.getStatus());
		if(Constants.REFUND_SUCCESS.equals(refundInfo.getStatus())) {
			map.put("success_time", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date()));
		}
		Map<String, Object> amount  = new HashMap<>();
		amount.put("total", payInfo.getTotal());
		amount.put("payer_total", payInfo.getTotal());
		amount.put("refund", refundInfo.getRefund());
		amount.put("payer_refund", refundInfo.getRefund());
		map.put("amount", amount);
		map.put("user_received_account", "INEEDSLEEP");
        return map;
	}

}
