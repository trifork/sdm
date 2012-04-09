#!/bin/bash
set -e
set -u


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


echo "Please verify that only the archives you expect to exist in the deploy dir is there"
find ${jboss_deploy_dir}/ -name "*.war"

