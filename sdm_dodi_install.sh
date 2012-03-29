#!/bin/bash
set -e

db_host_dodi=192.168.72.139
db_host_nsp=192.168.72.140

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
#num_users=$(${mysql_admin_connect} -D mysql -N -e "select count(*) from user where user='${db_username}';") | grep 0
#if [ "${num_users}" -eq 0 ]; then
#	echo "No trifork user found, creating user trifork"
	echo "DROP USER 'trifork'@'%';" > sql/drop_trifork_user.sql
	if [ -n "${DROP_USER}" ]; then
		echo "Dropping user trifork"
		${mysql_admin_connect} < sql/drop_trifork_user.sql
	fi
	echo "Creating user trifork"
	${mysql_admin_connect} < sql/create_trifork_mysql_user.sql
#else
#	echo "User trifork already exists"	
#fi


#stamdata_exists=$(${mysql_admin_connect} -D mysql -N -e "select count(db) from db where db='stamdata';") | grep 0
#if [ "${stamdata_exists}" -eq 0 ]; then
	echo "DROP DATABASE IF EXISTS stamdata;" > sql/stamdata/drop_db_stamdata.sql
	echo "Dropping database stamdata"
	${mysql_admin_connect} -v < sql/stamdata/drop_db_stamdata.sql
	
	echo "Creating database stamdata"
	## Create stamdata database
	${mysql_admin_connect} < sql/stamdata/create_db_stamdata.sql

	## Create schema in stamdata database
	${mysql_user_connect} stamdata < sql/stamdata/schema.sql
#else
#	echo "stamdata database already exists - please drop this database and let the script create it"
#	exit 1
#fi


cp conf/* ${jboss_conf_dir}/

cp deploy/* ${jboss_deploy_dir}/

echo "Done creating stamdata database, and copying files to ${jboss_conf_dir} and ${jboss_deploy_dir}"
echo "Restart jboss or the entire vm"