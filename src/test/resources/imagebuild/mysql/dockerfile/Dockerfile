﻿FROM mysql:5.7

#定义工作目录
ENV WORK_PATH /usr/local/work

#定义会被容器自动执行的目录
ENV AUTO_RUN_DIR /docker-entrypoint-initdb.d

#定义sql文件名
ENV FILE_0 0-init_data.sql
ENV MYSQL_ROOT_PASSWORD p@ssw0rd

#定义shell文件名
ENV INSTALL_DATA_SHELL install_data.sh

#1)创建文件夹 2)调整时区 3)设置表名参数名等忽略大小写 
RUN mkdir -p $WORK_PATH && ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo Asia/Shanghai > /etc/timezone && echo 'log-timestamps  = SYSTEM'>>/etc/mysql/mysql.conf.d/mysqld.cnf  && echo ' lower-case-table-names  = 1 '>>/etc/mysql/mysql.conf.d/mysqld.cnf

#把数据库初始化数据的文件复制到工作目录下
COPY ./$FILE_0 $WORK_PATH/

#把要执行的shell文件放到/docker-entrypoint-initdb.d/目录下，容器会自动执行这个shell
COPY ./$INSTALL_DATA_SHELL $AUTO_RUN_DIR/

#给执行文件增加可执行权限
RUN chmod a+x $AUTO_RUN_DIR/$INSTALL_DATA_SHELL