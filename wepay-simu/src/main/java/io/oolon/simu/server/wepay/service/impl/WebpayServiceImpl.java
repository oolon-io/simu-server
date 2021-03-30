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
import io.oolon.simu.server.wepay.bean.PayInfo;
import io.oolon.simu.server.wepay.exception.WepayJSV3Exception;
import io.oolon.simu.server.wepay.mapper.PayInfoMapper;
import io.oolon.simu.server.wepay.service.WebpayService;
import io.oolon.simu.server.wepay.util.Constants;
import io.oolon.simu.util.CheckUtil;
import io.oolon.util.JsonUtil;
import io.oolon.util.MapNumberUtil;
import squall.http.HttpUtil;

@Service
public class WebpayServiceImpl implements WebpayService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebpayServiceImpl.class);

	@Resource
	private PayInfoMapper payInfoMapper;

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
		LOGGER.error("queryPayJSV3 query response {}", response);
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
		LOGGER.error("queryPayJSV3 query responseMap {}", map);
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

}
