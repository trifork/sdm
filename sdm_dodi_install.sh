#!/bin/bash
set -e

db_host_dodi=tri-test-niab81
db_host_nsp=tri-test-niab82


db_admin_user=root
db_admin_password=nspnetic

db_username=trifork
db_password=CrevCuen

jboss_dir=/pack/jboss/server/default/
jboss_conf_dir=${jboss_dir}/conf
jboss_conf_url=file:${jboss_conf_dir}/
jboss_log_dir=${jboss_dir}/log
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
if [ "${num_users}" != "0" ]; then
	#echo "DROP USER 'trifork'@'%';" > sql/drop_trifork_user.sql
	#${mysql_admin_connect} < sql/drop_trifork_user.sql
	echo "User trifork already present on this MySQL instance"
else 
	echo "Creating user trifork"
	${mysql_admin_connect} < sql/create_trifork_mysql_user.sql
fi


echo "DROP DATABASE IF EXISTS stamdata;" > sql/stamdata/drop_db_stamdata.sql
echo "Dropping database stamdata"
${mysql_admin_connect} -v < sql/stamdata/drop_db_stamdata.sql

echo "Creating database stamdata"
## Create stamdata database
${mysql_admin_connect} < sql/stamdata/create_db_stamdata.sql

## Create schema in stamdata database
${mysql_user_connect} stamdata < sql/stamdata/schema.sql


echo "Replacing jboss.server.config.url placeholder i nspslalog properties with path to jboss conf folder"
find . -type f -name "nspslalog*.properties" -exec sed -i.orig -e "s|\\\${jboss.server.config.url}|${jboss_conf_url}|g" {} \;
echo "Copying properties to jboss conf folder"
find . -type f -name "nspslalog*.properties" -exec cp {} ${jboss_conf_dir}/ \;

echo "Reverting to original nspslalog files"
for file in conf/nspslalog*.properties.orig
do
 # do something on "$file"
 new_file_name=${file%.orig}
 mv -f "${file}" "${new_file_name}"
done


jboss_pid=$(/pack/jdk/bin/jps -l | grep jboss | cut -d " " -f 1) || true
if [ -n "${jboss_pid}" ]; then
	echo "Stopping jboss"
	/etc/init.d/jboss stop || true
	
	
	jboss_pid_2=$(/pack/jdk/bin/jps -l | grep jboss | cut -d " " -f 1) || true
	if [ -n "${jboss_pid_2}" ]; then
		echo "Shutdown of jboss failed, killing process"
		kill -9 ${jboss_pid_2}
	fi
fi

rm ${jboss_log_dir}/*
cp deploy/* ${jboss_deploy_dir}/

echo "Starting jboss"
/etc/init.d/jboss start

echo
echo "Done deploying - check the jboss log files in ${jboss_log_dir}"