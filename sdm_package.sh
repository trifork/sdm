#!/bin/bash
set -e
set -u

if [ -f "niab_config.cfg" ]; then
	source niab_config.cfg
fi


install_dir=_install
rm -rf ${install_dir}/*

dodi_dir=dodi
dodi_install_dir=${install_dir}/${dodi_dir}
dodi_sql_dir=${dodi_install_dir}/sql
dodi_sql_reg_noti_dir=${dodi_sql_dir}/register_notifications
dodi_sql_followup_dir=${dodi_sql_dir}/followup
dodi_sql_stamdata_dir=${dodi_sql_dir}/stamdata

dodi_install_conf_dir=${dodi_install_dir}/conf
dodi_install_deploy_dir=${dodi_install_dir}/deploy
mkdir -p ${dodi_install_conf_dir}
mkdir -p ${dodi_install_deploy_dir}
mkdir -p ${dodi_sql_reg_noti_dir}
mkdir -p ${dodi_sql_followup_dir}
mkdir -p ${dodi_sql_stamdata_dir}


nsp_dir=nsp
nsp_install_dir=${install_dir}/${nsp_dir}
nsp_sql_dir=${nsp_install_dir}/sql
nsp_sql_followup_dir=${nsp_sql_dir}/followup
nsp_sql_stamdata_dir=${nsp_sql_dir}/stamdata
nsp_install_conf_dir=${nsp_install_dir}/conf
nsp_install_deploy_dir=${nsp_install_dir}/deploy
mkdir -p ${nsp_install_conf_dir}
mkdir -p ${nsp_install_deploy_dir}
mkdir -p ${nsp_sql_followup_dir}
mkdir -p ${nsp_sql_stamdata_dir}

cp sdm_nsp_install.sh ${nsp_install_dir}
cp sdm_dodi_install.sh ${dodi_install_dir}
cp niab_config.cfg ${nsp_install_dir}
cp niab_config.cfg ${dodi_install_dir}
cp install.sh ${nsp_install_dir}
cp install.sh ${dodi_install_dir}

## On both DoDi-db and NSP-db
echo "CREATE USER '${db_username}'@'%' IDENTIFIED BY '${db_password}';" > ${nsp_sql_dir}/create_trifork_mysql_user.sql
echo "CREATE USER '${db_username}'@'%' IDENTIFIED BY '${db_password}';" > ${dodi_sql_dir}/create_trifork_mysql_user.sql


## Installer DoDi komponenter
# 1: Stamdata (stamdata-data-manager)
cp etc/slalog_config_files/nspslalog-stamdata-data-manager.properties ${dodi_install_conf_dir}
cp etc/slalog_config_files/log4j-nspslalog-stamdata-data-manager.properties ${dodi_install_conf_dir}
cp dodi/data-manager/target/stamdata-data-manager-*-SNAPSHOT.war ${dodi_install_deploy_dir}


#create database stamdata on dodi-mysql server
echo "CREATE DATABASE stamdata;" > ${dodi_sql_stamdata_dir}/create_db_stamdata.sql
echo "GRANT ALL ON stamdata.* TO '${db_username}'@'%';" >> ${dodi_sql_stamdata_dir}/create_db_stamdata.sql
cp db/schema.sql ${dodi_sql_stamdata_dir}
sed -i.orig "s|USE sdm_warehouse|-- USE sdm_warehouse|g" "${dodi_sql_stamdata_dir}/schema.sql"

echo "db.url = jdbc:mysql://${db_host_dodi}/
#db.user = stamdata_rw
db.user = ${db_username}
db.pwd = ${db_password}
db.database = stamdata
" > ${dodi_install_conf_dir}/stamdata-data-manager.properties


## create database stamdata
echo "CREATE DATABASE stamdata;" > ${nsp_sql_stamdata_dir}/create_db_stamdata.sql
echo "GRANT ALL ON stamdata.* TO '${db_username}'@'%';" >> ${nsp_sql_stamdata_dir}/create_db_stamdata.sql
cp db/schema.sql ${nsp_sql_stamdata_dir}
sed -i.orig "s|USE sdm_warehouse|-- USE sdm_warehouse|g" "${nsp_sql_stamdata_dir}/schema.sql"


## #Installer NSP komponenter


#1: Kopiregister
## Create stamdata database on NSP-db
## Create stamdata tables on NSP-db:
	#mysql –u root -p stamdata < db/schema.sql
cp etc/slalog_config_files/nspslalog-stamdata-batch-copy.properties ${nsp_install_conf_dir}
cp etc/slalog_config_files/log4j-nspslalog-stamdata-batch-copy.properties ${nsp_install_conf_dir}
cp nsp/batch-copy-ws/target/stamdata-batch-copy-ws-*-SNAPSHOT.war ${nsp_install_deploy_dir}

echo "db.connection.jdbcURL=jdbc:mysql://${db_host_nsp}/stamdata
db.connection.username=${db_username}
#stamdata_replication
db.connection.password=${db_password}
security=dgwsTest
" > ${nsp_install_conf_dir}/stamdata-batch-copy-ws.properties


#2: Enkeltopslag i autorisationsregistret
cp etc/slalog_config_files/nspslalog-stamdata-authorization-lookup.properties ${nsp_install_conf_dir}
cp etc/slalog_config_files/log4j-nspslalog-stamdata-authorization-lookup.properties ${nsp_install_conf_dir}
cp nsp/authorization-lookup-ws/target/stamdata-authorization-lookup-ws-*-SNAPSHOT.war ${nsp_install_deploy_dir}

echo "db.connection.jdbcURL=jdbc:mysql://${db_host_nsp}/stamdata
db.connection.username=${db_username}
#db.connection.username=authorization_r
db.connection.password=${db_password}
security=dgwsTest
" > ${nsp_install_conf_dir}/stamdata-authorization-lookup-ws.properties


#3: Enkeltopslag i CPR-registret
cp etc/slalog_config_files/nspslalog-stamdata-cpr.properties ${nsp_install_conf_dir}
cp etc/slalog_config_files/log4j-nspslalog-stamdata-cpr.properties ${nsp_install_conf_dir}
cp nsp/cpr-ws/target/stamdata-cpr-ws-*-SNAPSHOT.war ${nsp_install_deploy_dir}


echo "db.connection.jdbcURL=jdbc:mysql://${db_host_nsp}/stamdata
db.connection.username=${db_username}
#db.connection.username=cpr_ws_r
db.connection.password=${db_password}

// Denne URL definerer end-pointet hvor CPRABBS service kan nås.
cprabbs.service.endpoint.host=${db_host_nsp}
cprabbs.service.endpoint.port=8080
cprabbs.service.endpoint.path=/cprabbs/service/cprabbs

//Use the SOSI test federation
useSOSITestFederation=true
" > ${nsp_install_conf_dir}/stamdata-cpr-ws.properties


echo "log4j.rootLogger=INFO, FILE

log4j.appender.FILE=org.apache.log4j.RollingFileAppender
log4j.appender.FILE.File=\${jboss.server.log.dir}/stamdata-cpr-ws.log
log4j.appender.FILE.MaxFileSize=10MB
log4j.appender.FILE.MaxBackupIndex=200
log4j.appender.FILE.layout=org.apache.log4j.PatternLayout
log4j.appender.FILE.layout.ConversionPattern=%d %-5p [%t] %c - %m%n

log4j.logger.dk.nsi.stamdata.cpr.pvit=DEBUG
" > ${nsp_install_conf_dir}/log4j-stamdata-cpr-ws.properties


cd ${install_dir}
tar czf sdm_${nsp_dir}.tar.gz ${nsp_dir}
tar czf sdm_${dodi_dir}.tar.gz ${dodi_dir}

## register_notifications replikeres fra dodi


echo
echo "Done - results are available in ${install_dir}"
find . -name "*.tar.gz"

echo
echo "Copying files to vm's"
scp sdm_${dodi_dir}.tar.gz ${db_host_dodi}:~
scp sdm_${nsp_dir}.tar.gz ${db_host_nsp}:~

ssh ${db_host_dodi} "tar zxf sdm_${dodi_dir}.tar.gz"
#echo "Installing SDM Data Manager on DODI System"
#ssh ${db_host_dodi} "cd ${dodi_dir};sudo ./sdm_dodi_install.sh"

ssh ${db_host_nsp} "tar zxf sdm_${nsp_dir}.tar.gz"
#echo "Installing SDM Components on NSP System"
#ssh ${db_host_nsp} "cd ${nsp_dir};sudo ./sdm_nsp_install.sh"

echo
echo "Files uploaded to ${db_host_dodi} and ${db_host_nsp} 
	- Log in to the systems, go to the ${dodi_dir} or ${nsp_dir} folder 
	- and execute the sdm_*_install.sh scripts using sudo" 