#!/bin/bash
set -u
set -e

for confFile in *_config.cfg 
do	
	echo "Reading properties from '${confFile}'"
	source "${confFile}"
done


echo "Stopping JBoss server instance before deploying"

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


echo "Clearing JBoss log directory: '${jboss_log_dir}'"
rm -f ${jboss_log_dir}/*


for file in *_*_install.sh
do
 # do something on "$file"
 	echo "Executing file: ${file}"
	source "${file}"
done

echo "Copying properties to jboss conf folder"
find conf/ -name "[^\.]*.properties" -exec cp {} ${jboss_conf_dir}/ \;


# Recreate the original properties so the installer can be run again
for file in conf/*.properties.orig
do
 # do something on "$file"
 new_file_name=${file%.orig}
 mv -f "${file}" "${new_file_name}"
done

echo "Copying war files to ${jboss_deploy_dir}"
cp deploy/* ${jboss_deploy_dir}/


echo "Starting jboss"
/etc/init.d/jboss start

echo
echo "Done deploying - check the jboss log files in ${jboss_log_dir}"