package io.oolon.simu.server.wepay.bean;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class PayInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8930950500308697383L;

	private String appid;
	
	private String mchid;
	
	private String description;
	
	private String out_trade_no;
	
	private String notify_url;
	
//	private Integer amount_total;
//	
//	private String payer_openid;
	
	private String prepay_id;
	
	private Date time_expire;
	
	private Integer total;
	
	private String openid;
	
	private String trade_state;
	
	private String trade_type;
	
	private String transaction_id;
	
	private String success_time;
	
	private Date pay_notice_time;

}
