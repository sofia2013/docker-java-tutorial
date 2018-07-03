#!/bin/bash
mysql -uroot --default-character-set=utf8 -p$MYSQL_ROOT_PASSWORD <<EOF
source $WORK_PATH/$FILE_0;