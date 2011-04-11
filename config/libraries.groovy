//////////////////////////////////////////////////////////////////////////////
// This file contains a handy list of dependencies' names and versions.
// This can be used to make sure dependencies are consistently versioned 
// in your projects.
//////////////////////////////////////////////////////////////////////////////

def guiceVersion = '3.0'
def slf4jVersion = '1.6.1'

libs = [
	commons_io: 'commons-io:commons-io:2.0',
	commons_codec: 'commons-codec:commons-codec:1.4',
	commons_lang: 'commons-lang:commons-lang:2.5',
	commons_configuration: 'commons-configuration:commons-configuration:1.6',

	slf4j_api: 'org.slf4j:slf4j-api:' + slf4jVersion,
	log4j_over_slf4j: 'org.slf4j:jcl-over-slf4j:' + slf4jVersion,
	jcl_over_slf4j: 'org.slf4j:jcl-over-slf4j:' + slf4jVersion,
	log4j_slf4j: 'org.slf4j:slf4j-log4j12:' + slf4jVersion,

	logback_core: 'ch.qos.logback:logback-core:0.9.27',
	logback_classic: 'ch.qos.logback:logback-classic:0.9.27',

	servlet_api: 'javax.servlet:servlet-api:2.4@jar',

	reflections: 'org.reflections:reflections:0.9.5-RC3',

	hibernate: 'org.hibernate:hibernate-entitymanager:3.6.2.Final',

	mysql_driver: 'mysql:mysql-connector-java:5.1.13@jar',

	junit: 'junit:junit:4.8.2',
	mockito: 'org.mockito:mockito-core:1.8.5',

	hamcrest: 'org.hamcrest:hamcrest-library:1.2',

	bouncycastle: 'bouncycastle:bcprov-jdk16:140@jar',

	jodi_time: 'joda-time:joda-time:1.6',

	seal: 'dk.sosi:seal:1.5.13',
	oiosaml: 'dk.itst.oiosaml:oiosaml.java:5987',
	
	velocity: 'apache-velocity:velocity:1.5',
	
	jaxb: 'com.sun.xml.bind:jaxb-impl:2.2.2',
	
	guice: 'com.google.inject.guice:guice:' + guiceVersion,
	guice_servlet: 'com.google.inject.extensions:guice-servlet:' + guiceVersion
]
