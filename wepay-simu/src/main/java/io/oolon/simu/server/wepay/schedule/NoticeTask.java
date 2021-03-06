package io.oolon.simu.server.wepay.schedule;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.oolon.simu.server.wepay.bean.PayInfo;
import io.oolon.simu.server.wepay.bean.RefundInfo;
import io.oolon.simu.server.wepay.mapper.PayInfoMapper;
import io.oolon.simu.server.wepay.mapper.RefundInfoMapper;
import io.oolon.simu.server.wepay.service.WepayService;

@Component
@EnableScheduling
public class NoticeTask {
	    private static final Logger LOGGER = LoggerFactory.getLogger(NoticeTask.class);
	    
	    @Resource
	    private PayInfoMapper payInfoMapper;
	    @Resource
	    private RefundInfoMapper refundInfoMapper;
	    
	    @Resource
	    private WepayService webpayService;
	    
	    @Scheduled(fixedDelay = 10000)
		public void payNoticeTask() {
			LOGGER.info("支付成功推送....");
			
			List<PayInfo> todoList  = payInfoMapper.queryNeedNoticeList();
			
			if(todoList != null && todoList.size() > 0) {
				LOGGER.info(" {} 条支付成功推送记录需要处理",todoList.size());
				for(PayInfo payinfo : todoList) {
					webpayService.noticeOnePay(payinfo);
				}
			}
			LOGGER.info("支付成功推送结束....");
		}
	    
	    @Scheduled(fixedDelay = 10000)
		public void refundNoticeTask() {
			LOGGER.info("退款成功推送....");
			
			List<RefundInfo> todoList  = refundInfoMapper.queryNeedNoticeList();
			
			if(todoList != null && todoList.size() > 0) {
				LOGGER.info(" {} 条退款成功推送记录需要处理",todoList.size());
				for(RefundInfo refundInfo : todoList) {
					webpayService.noticeOneRefund(refundInfo);
				}
			}
			LOGGER.info("退款成功推送结束....");
		}

}
