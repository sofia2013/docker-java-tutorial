use mysql;
select host, user from user;
-- 因为mysql版本是5.7，因此新建用户为如下命令：
create user ecm_cloud identified by 'ecm_cloud@Skytech18';

grant all on ecm_cloud.* to ecm_cloud@'%' identified by 'ecm_cloud@Skytech18' with grant option;
-- 这一条命令一定要有：
flush privileges;


-- 创建数据库
DROP DATABASE IF EXISTS ecm_cloud;
create database `ecm_cloud` default character set utf8 collate utf8_general_ci;

use ecm_cloud;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for business_attachment_rel
-- ----------------------------
DROP TABLE IF EXISTS `business_attachment_rel`;
CREATE TABLE `business_attachment_rel` (
  `ID` varchar(50) NOT NULL,
  `BUSINESSID` varchar(50) DEFAULT NULL COMMENT '业务主键',
  `ATTACHMENTIDS` varchar(500) DEFAULT NULL COMMENT '附件主键',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;