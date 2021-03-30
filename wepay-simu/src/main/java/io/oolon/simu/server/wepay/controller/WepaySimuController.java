package io.oolon.simu.server.wepay.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.oolon.simu.server.wepay.service.WebpayService;
import io.oolon.simu.server.wepay.util.WepaySignChecker;
import io.oolon.util.JsonUtil;

@RestController
public class WepaySimuController {
	private static final Logger LOGGER = LoggerFactory.getLogger(WepaySimuController.class);

	@Resource
	private WebpayService webpayService;

	@Resource
	private WepaySignChecker wepaySignChecker;

	@RequestMapping(value = "/v3/pay/transactions/jsapi", method = { RequestMethod.POST })
	public ResponseEntity<String> prePayJSV3(@RequestHeader HttpHeaders header, @RequestBody String body) {
		wepaySignChecker.check(header);
		Map<String, Object> requestMap = JsonUtil.json2Map(body);
		Map<String, Object> responseMap = webpayService.prePay(requestMap);
		ResponseEntity<String> rsp = new ResponseEntity<String>(JsonUtil.getJson(responseMap), null, HttpStatus.OK);
		return rsp;

	}

	@RequestMapping(value = "/v3/pay/transactions/jsapi/payed", method = { RequestMethod.POST })
	public ResponseEntity<String> payedJSV3(@RequestHeader HttpHeaders header, @RequestBody String body) {
		wepaySignChecker.check(header);
		Map<String, Object> requestMap = JsonUtil.json2Map(body);
		Map<String, Object> responseMap = webpayService.payed(requestMap);
		responseMap.put("return", "通过校验");
		ResponseEntity<String> rsp = new ResponseEntity<String>(JsonUtil.getJson(responseMap), null, HttpStatus.OK);
		return rsp;

	}

	@GetMapping("/v3/pay/transactions/out-trade-no/{out_trade_no}")
	public ResponseEntity<String> queryPayJSV3(@RequestHeader HttpHeaders header,@PathVariable("out_trade_no") String out_trade_no,@RequestParam(name = "mchid") String mchid) {
		wepaySignChecker.check(header);
		Map<String, Object> responseMap = webpayService.queryPayJSV3(out_trade_no,mchid);
		ResponseEntity<String> rsp = new ResponseEntity<String>(JsonUtil.getJson(responseMap), null, HttpStatus.OK);
		return rsp;

	}
	
	
	@RequestMapping(value = "/v3/pay/notice", method = { RequestMethod.POST })
	public ResponseEntity<String> payNotice(@RequestHeader HttpHeaders header, @RequestBody String body) {
		wepaySignChecker.check(header);
        LOGGER.debug("payNotice recv {}",body);
		Map<String, Object> responseMap = new HashMap<>();
		ResponseEntity<String> rsp = new ResponseEntity<String>(JsonUtil.getJson(responseMap), null, HttpStatus.OK);
		return rsp;

	}

}
