package io.oolon.simu.server.wepay.bean;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class RefundInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2818203073888516160L;

	public String out_refund_no;
	public String transaction_id;
	public String  refund_id;
	public String out_trade_no;
	public String notify_url;
	public Integer refund;
	public Integer total;
	public String currency;
	public String create_time;
	public String status;
	public Date refund_notice_time;
	public Integer is_pay_noticed;

}
