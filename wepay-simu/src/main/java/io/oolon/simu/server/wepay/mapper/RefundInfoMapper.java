package io.oolon.simu.server.wepay.mapper;

import java.util.List;

import io.oolon.simu.server.wepay.bean.RefundInfo;

public interface RefundInfoMapper {
	
	int saveRefundInfo(RefundInfo refundInfo);

	RefundInfo queryRefundInfo(RefundInfo queryTmp);

	List<RefundInfo> queryNeedNoticeList();

	void changeRefundNoticedFlag(RefundInfo refundInfo);

	
}
