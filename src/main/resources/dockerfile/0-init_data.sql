CREATE DATABASE IF NOT EXISTS `db`;
USE `db`;


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- Table structure for pt_category
-- ----------------------------
DROP TABLE IF EXISTS `pt_category`;
CREATE TABLE `pt_category`  (
  `PKID` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `category_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典类型ID，0表示根级字典项',
  `chn_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '中文名',
  `eng_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '英文名',
  `value_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编号',
  `sort_flag` decimal(5, 0) NULL DEFAULT NULL COMMENT '排序号',
  `use_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否可用：0为不可用，1为可用',
  `rel_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联其他字典值',
  `create_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  `create_time` varchar(19) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `create_ip` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建IP',
  `update_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人id',
  `update_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人姓名',
  `update_time` varchar(19) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新时间',
  `update_ip` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新ip',
  PRIMARY KEY (`PKID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '平台--字典信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pt_category
-- ----------------------------
INSERT INTO `pt_category` VALUES ('c00002', '0', '性别', 'SEX', 'c00002', 2, '1', NULL, NULL, NULL, NULL, NULL, 'admin', '管理员', '2017-08-02 09:54:56', '0:0:0:0:0:0:0:1');
INSERT INTO `pt_category` VALUES ('c0000200001', 'c00002', '男', 'SEX_MAN', '1', 1, '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `pt_category` VALUES ('c0000200002', 'c00002', '女', 'SEX_WOMAN', '2', 2, '1', NULL, NULL, NULL, NULL, NULL, 'admin', '管理员', '2017-08-31 11:25:27', '32.1.2.75');