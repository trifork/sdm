#!/bin/bash
set -e

db_host_dodi=192.168.72.142
db_host_nsp=192.168.72.141

db_admin_user=root
db_admin_password=nspnetic

db_username=trifork
db_password=CrevCuen

jboss_dir=/pack/jboss/server/default/
jboss_conf_dir=${jboss_dir}/conf
jboss_deploy_dir=${jboss_dir}/deploy

mysql_dir=/pack/mysql
if [ -n "${db_admin_password}" ]; then 
	mysql_admin_connect="${mysql_dir}/bin/mysql -u ${db_admin_user} -p${db_admin_password}"
else 
	mysql_admin_connect="${mysql_dir}/bin/mysql -u ${db_admin_user}"
fi
if [ -n "${db_password}" ]; then
	mysql_user_connect="${mysql_dir}/bin/mysql -u ${db_username} -p${db_password}"
else
	mysql_user_connect="${mysql_dir}/bin/mysql -u ${db_username}"
fi



## Create the trifork database user
num_users=$(${mysql_admin_connect} -D mysql -N -e "select count(*) from user where user='${db_username}';" | grep 0 || true)
if [ "${num_users}" -eq 0 ]; then
	echo "No trifork user found"
else
	echo "DROP USER 'trifork'@'%';" > sql/drop_trifork_user.sql
	echo "Dropping existing user trifork"
	${mysql_admin_connect} < sql/drop_trifork_user.sql
fi

echo "Creating user trifork"
${mysql_admin_connect} < sql/create_trifork_mysql_user.sql



echo "DROP DATABASE IF EXISTS stamdata;" > sql/stamdata/drop_db_stamdata.sql
echo "Dropping database stamdata"
${mysql_admin_connect} -v < sql/stamdata/drop_db_stamdata.sql

echo "Creating database stamdata"
## Create stamdata database
${mysql_admin_connect} < sql/stamdata/create_db_stamdata.sql

## Create schema in stamdata database
${mysql_user_connect} stamdata < sql/stamdata/schema.sql




cp conf/* ${jboss_conf_dir}/

echo "Stopping jboss"
/etc/init.d/jboss stop

cp deploy/* ${jboss_deploy_dir}/

echo "Starting jboss"
/etc/init.d/jboss start

echo "Done deploying - check the jboss log files in ${jboss_log_dir}"