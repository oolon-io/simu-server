package io.oolon.simu.server.wepay.util;

public class Constants {
	public static final String PAY_STATUS_SUCCESS = "SUCCESS"; //：支付成功
	public static final String PAY_STATUS_REFUND = "REFUND"; //：转入退款
	public static final String PAY_STATUS_NOTPAY = "NOTPAY"; //：未支付
	public static final String PAY_STATUS_CLOSED = "CLOSED"; //：已关闭
	public static final String PAY_STATUS_REVOKED = "REVOKED";//：已撤销（付款码支付）
	public static final String PAY_STATUS_USERPAYING = "USERPAYING";//：用户支付中（付款码支付）
	public static final String PAY_STATUS_PAYERROR = "PAYERROR"; //：支付失败(其他原因，如银行返回失败)
	public static final String PAY_STATUS_ACCEPT = "ACCEPT"; //：已接收，等待扣款
	
	public static final String REFUND_SUCCESS = "SUCCESS"; //：退款成功
	public static final String REFUND_CLOSED = "CLOSED"; //：退款关闭
	public static final String REFUND_PROCESSING = "PROCESSING"; //：退款处理中
	public static final String REFUND_ABNORMAL = "ABNORMAL"; //：退款异常 
	
	public static final String PAY_TYPE_JSAPI = "JSAPI"; //JSAPI 公众号H5支付
}
