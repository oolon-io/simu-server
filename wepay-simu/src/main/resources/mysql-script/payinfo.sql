SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for payinfo
-- ----------------------------
DROP TABLE IF EXISTS `payinfo`;
CREATE TABLE `payinfo`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `appId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '应用appid',
  `mchid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '微信商户号',
  `description` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '订单描述',
  `out_trade_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '商户订单号',
  `time_expire` datetime(0) NULL DEFAULT NULL COMMENT '订单失效时间',
  `notify_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '支付结果通知url',
  `total` int(11) NOT NULL COMMENT '总金额单位 分',
  `openid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '付款人openid',
  `prepay_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'prepay_id客户端调起付款用',
  `trade_state` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '微信端支付状态 SUCCESS：支付成功\r\nREFUND：转入退款\r\nNOTPAY：未支付\r\nCLOSED：已关闭\r\nREVOKED：已撤销（付款码支付）\r\nUSERPAYING：用户支付中（付款码支付）\r\nPAYERROR：支付失败(其他原因，如银行返回失败)\r\nACCEPT：已接收，等待扣款',
  `trade_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'JSAPI：公众号支付 NATIVE：扫码支付 APP：APP支付 MICROPAY：付款码支付 MWEB：H5支付 FACEPAY：刷脸支付',
  `transaction_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '微信支付订单号',
  `success_time` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '支付成功时间',
  `pay_notice_time` date NULL DEFAULT NULL,
  `is_pay_noticed` tinyint(4) NULL DEFAULT 0 COMMENT '1-已推送了',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `out_trade_no_idx`(`mchid`, `out_trade_no`) USING BTREE,
  UNIQUE INDEX `prepay_id_idx`(`prepay_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of payinfo
-- ----------------------------
INSERT INTO `payinfo` VALUES (1, 'wxfdb74947029fb4c9', '1578840051', '景区名称订票', 'D2104071911469260001', '2021-04-07 19:26:49', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 1900, '18616690823', '918680a503494d23be17e7d60c2aadb6', 'NOTPAY', 'JSAPI', NULL, NULL, NULL, 0);
INSERT INTO `payinfo` VALUES (2, 'wxfdb74947029fb4c9', '1578840051', '景区名称订票', 'D2104071920338820002', '2021-04-07 19:35:34', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 1900, '18844441111', '54bcca9a75cc45e0a5c7ca7ca7bd8c23', 'NOTPAY', 'JSAPI', NULL, NULL, NULL, 0);
INSERT INTO `payinfo` VALUES (3, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104081642362900003', '2021-04-08 16:57:36', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 300, '18516281289', 'f95f0473ed6f442ab49742520f364b55', 'NOTPAY', 'JSAPI', NULL, NULL, NULL, 0);
INSERT INTO `payinfo` VALUES (4, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104081706377710004', '2021-04-08 17:21:38', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 400, '18516281289', '03bd9db59219442fa936ffbcad7a3886', 'NOTPAY', 'JSAPI', NULL, NULL, NULL, 0);
INSERT INTO `payinfo` VALUES (5, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104081711427280005', '2021-04-08 17:26:43', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 600, '18516281289', '109c9019dd9d44f2aaacffe725b20254', 'SUCCESS', 'JSAPI', 'ba33ce26bbb34bfb933ffb1084489992', '2021-04-08T17:11:44+08:00', '2021-04-08', 1);
INSERT INTO `payinfo` VALUES (6, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104081719001430006', '2021-04-08 17:34:00', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 600, '18516281289', '48db462c61f94395876a57717323a979', 'SUCCESS', 'JSAPI', '67c2f0541fc2475e8155908b310f44ce', '2021-04-08T17:19:02+08:00', '2021-04-08', 1);
INSERT INTO `payinfo` VALUES (7, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104081722199710007', '2021-04-08 17:37:20', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 400, '18516281289', '492242f380e0414592bebe72f64d4cf3', 'SUCCESS', 'JSAPI', '77a651fd4e2f479eab037dec0f639a01', '2021-04-08T17:22:22+08:00', '2021-04-08', 1);
INSERT INTO `payinfo` VALUES (8, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104081731291650001', '2021-04-08 17:46:34', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 400, '18516281289', '01e6bc94e95c4db2b98868f66bd4a0d7', 'SUCCESS', 'JSAPI', 'd631dc9a4cd342138262b9b0828066fc', '2021-04-08T17:32:32+08:00', '2021-04-08', 1);
INSERT INTO `payinfo` VALUES (9, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104081938527330004', '2021-04-08 19:53:53', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 300, '13511111111', '6c4661d615f4469f8f4f478d2be4237d', 'SUCCESS', 'JSAPI', 'afdcc267b4624dff9814238768bb66ee', '2021-04-08T19:38:55+08:00', '2021-04-08', 1);
INSERT INTO `payinfo` VALUES (10, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104081944293320005', '2021-04-08 19:59:29', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 200, '13511111111', '2e33efa2620c42d7b9e81d5694da7cec', 'NOTPAY', 'JSAPI', NULL, NULL, NULL, 0);
INSERT INTO `payinfo` VALUES (11, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104090843158560001', '2021-04-09 08:58:19', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 900, 'oy-_CuFaYKttN05hMuetUXNlV674', '7c73cfbbd4a44c3dbc16646c487f86ed', 'NOTPAY', 'JSAPI', NULL, NULL, NULL, 0);
INSERT INTO `payinfo` VALUES (12, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104091021087940003', '2021-04-09 10:36:10', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 300, '13511111111', 'aaf099174d8a4c39a25379e261f4bfcf', 'SUCCESS', 'JSAPI', '5af2627b19944e4a9642d9272cb1b7b6', '2021-04-09T10:21:14+08:00', '2021-04-09', 1);
INSERT INTO `payinfo` VALUES (13, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104091042313690001', '2021-04-09 10:57:34', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 200, '13511111111', '2eddf80704be470aa16f29b567fcf988', 'SUCCESS', 'JSAPI', '7f3ae4dd955c4c318bf7505f2fce5f95', '2021-04-09T10:42:37+08:00', '2021-04-09', 1);
INSERT INTO `payinfo` VALUES (14, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104091100321820002', '2021-04-09 11:15:34', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 300, '13511111111', 'c2d549f0a1a84882ac50c95b640dfec1', 'NOTPAY', 'JSAPI', NULL, NULL, NULL, 0);
INSERT INTO `payinfo` VALUES (15, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104091100297370001', '2021-04-09 11:15:34', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 300, '13511111111', 'fd354ad5977f425285f47c8f319ab657', 'SUCCESS', 'JSAPI', 'e475eee07c2d4432afa972c8f4c47005', '2021-04-09T11:00:39+08:00', '2021-04-09', 1);
INSERT INTO `payinfo` VALUES (16, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104091358040900001', '2021-04-09 14:13:07', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 200, '13511111111', '7aaf4f18dab548c68b9f075c108dae3c', 'NOTPAY', 'JSAPI', NULL, NULL, NULL, 0);
INSERT INTO `payinfo` VALUES (17, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104091358110840002', '2021-04-09 14:13:11', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 200, '13511111111', '782a9e942f874629b71af478a1cf623a', 'NOTPAY', 'JSAPI', NULL, NULL, NULL, 0);
INSERT INTO `payinfo` VALUES (18, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104091449127850001', '2021-04-09 15:04:14', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 4000, '18516281289', 'a546c419619d4af3bbb9f0a536858a7b', 'NOTPAY', 'JSAPI', NULL, NULL, NULL, 0);
INSERT INTO `payinfo` VALUES (19, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104091508037140001', '2021-04-09 15:23:05', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 4000, '18516281289', 'f372d0c5dd254fe486e8438206f108ac', 'SUCCESS', 'JSAPI', 'c5c60b32886143668ae18698028e9a2d', '2021-04-09T15:08:06+08:00', '2021-04-09', 1);
INSERT INTO `payinfo` VALUES (20, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104091514287230003', '2021-04-09 15:29:31', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 4000, '13511111111', 'a775a2eb3081464a898f3776de5d0339', 'NOTPAY', 'JSAPI', NULL, NULL, NULL, 0);
INSERT INTO `payinfo` VALUES (21, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104091514254060001', '2021-04-09 15:29:31', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 4000, '13511111111', '4f7244ff17994ec39605b55d9d5b5e1d', 'NOTPAY', 'JSAPI', NULL, NULL, NULL, 0);
INSERT INTO `payinfo` VALUES (22, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104091514295350005', '2021-04-09 15:29:31', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 4000, '13511111111', '7340ac4578e74616bb42efbd385b5439', 'NOTPAY', 'JSAPI', NULL, NULL, NULL, 0);
INSERT INTO `payinfo` VALUES (23, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104091514295270004', '2021-04-09 15:29:31', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 4000, '13511111111', '6e89ee80ff2c4943bdf590b3fef6ce6a', 'SUCCESS', 'JSAPI', 'becd93c94cba4728a255da3f089241e9', '2021-04-09T15:14:34+08:00', '2021-04-09', 1);
INSERT INTO `payinfo` VALUES (24, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104091514272830002', '2021-04-09 15:29:31', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 4000, '13511111111', 'bb9734341a444c86b031117805f25e71', 'NOTPAY', 'JSAPI', NULL, NULL, NULL, 0);
INSERT INTO `payinfo` VALUES (25, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104091516233070006', '2021-04-09 15:31:23', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 4000, '13511111111', '3b986f055dd945239306d5829465e8fe', 'SUCCESS', 'JSAPI', 'b51231d577a04abcb3c49bed2f8a1018', '2021-04-09T15:16:45+08:00', '2021-04-09', 1);
INSERT INTO `payinfo` VALUES (26, 'wxfdb74947029fb4c9', '1578840051', '订票', 'L2104091531108190007', '2021-04-09 15:46:11', 'http://22.188.12.146/ctsp-ali/ftpay/thirdNotify/unlogin/jsapiPayNotify/1614152546583001', 8000, '13511111111', 'dee6a297ca2a4c9ab0fd0fd6e480af95', 'SUCCESS', 'JSAPI', '16ba3f6f8bb148b2b71b860987dbdc05', '2021-04-09T15:31:28+08:00', '2021-04-09', 1);

SET FOREIGN_KEY_CHECKS = 1;
