# simu-server
微信支付 服务端 模拟 
目前优先实现JSAPIV3 供公众号H5 在 内网开发测试时无法连接外网的情况下使用
目前未做加解密 验签，支付推送通知chipertext未做加密，退款推送通知chipertext未做加密
目前进度JSAPIV3 实现了支付、退款流程相关模拟服务，支付和退款支持流程已支持。支付中定时关闭，交易关闭功能未实现。

需要使用mysql作为数据库,建表语句在simu-server\wepay-simu\src\main\resources\mysql-script目录下
前端模拟提交支付请求需要调用 /v3/pay/transactions/jsapi/payed 接口
报文为json
{"appid":"wxfdb74947029fb4c9","timeStamp":"1414561699","nonceStr":"dadadada","package":"prepay_id=3769b0de6d4d4cc8b54c367b3ff1a15a","signType":"RSA","paySign":"aaa"}

package中的prepay_id为模拟器返回的数据
