/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50729
 Source Host           : localhost:3306
 Source Schema         : dao

 Target Server Type    : MySQL
 Target Server Version : 50729
 File Encoding         : 65001

 Date: 30/03/2021 09:37:32
*/

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
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
