package io.oolon.simu.server.wepay.mapper;

import java.util.List;

import io.oolon.simu.server.wepay.bean.PayInfo;

public interface PayInfoMapper {
	
	int savePayInfo(PayInfo payInfo);


	int updatePayInfoInPayed(PayInfo payInfo);
	
	PayInfo queryPayInfo(PayInfo payInfo);


	List<PayInfo> queryNeedNoticeList();


	void changePayNoticedFlag(PayInfo payinfo);
	
}
