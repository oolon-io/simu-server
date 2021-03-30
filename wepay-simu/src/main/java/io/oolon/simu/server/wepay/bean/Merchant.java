package io.oolon.simu.server.wepay.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class Merchant implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4962806958123048997L;

	private String mchid;
	
	private boolean autoPayNotify;
	
	private boolean autoRefundNotify;
	
	//priavte String mch
	
	

}
