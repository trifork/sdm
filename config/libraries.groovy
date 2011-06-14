// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

//////////////////////////////////////////////////////////////////////////////
// This file contains a handy list of dependencies' names and versions.
// This can be used to make sure dependencies are consistently versioned 
// in your projects.
//////////////////////////////////////////////////////////////////////////////

def guiceVersion = '3.0'
def slf4jVersion = '1.5.8'
def jerseyVersion = '1.7'

libs = [
	commons_io: 'commons-io:commons-io:2.0',
	commons_codec: 'commons-codec:commons-codec:1.4',
	commons_lang: 'commons-lang:commons-lang:2.5',
	commons_configuration: 'commons-configuration:commons-configuration:1.6',
	commons_cli: 'commons-cli:commons-cli:1.2',

	slf4j_api: 'org.slf4j:slf4j-api:' + slf4jVersion,
	log4j_over_slf4j: 'org.slf4j:log4j-over-slf4j:' + slf4jVersion,
	jcl_over_slf4j: 'org.slf4j:jcl-over-slf4j:' + slf4jVersion,
	slf4j_log4j: 'org.slf4j:slf4j-log4j12:' + slf4jVersion,
	jul_to_slf4j: 'org.slfj:jul-to-slf4j:' + slf4jVersion,
	log4j: 'log4j:log4j:1.2.16',

	logback_core: 'ch.qos.logback:logback-core:0.9.27',
	logback_classic: 'ch.qos.logback:logback-classic:0.9.27',

	servlet_api: 'javax.servlet:servlet-api:2.4@jar',

	reflections: 'org.reflections:reflections:0.9.5-RC3',

	// JBoss is fickle when it comes to class loading and you will
	// have to override all the hibernate libraries if you want to
	// upgrade to a newer version.

	hibernate_entitymanager: 'org.hibernate:hibernate-entitymanager:3.6.0.Final',
	hibernate_c3p0: 'org.hibernate:hibernate-c3p0:3.6.0.Final',

	mysql_driver: 'mysql:mysql-connector-java:5.1.15@jar',

	junit: 'junit:junit:4.8.2',
	mockito: 'org.mockito:mockito-core:1.8.5',

	hamcrest: 'org.hamcrest:hamcrest-library:1.2',

	bouncycastle: 'bouncycastle:bcprov-jdk14:136@jar',
	
	bouncycastle15: 'bouncycastle:bcprov-jdk15:',
	
	ooapi: 'org.openoces:ooapi:1.81.2',

	jodi_time: 'joda-time:joda-time:1.6',

	seal: 'dk.sosi:seal:1.5.13',
	oiosaml: 'dk.itst.oiosaml:oiosaml.java:5987',
	
	velocity: 'apache-velocity:velocity:1.5',
	
	jaxb_api: 'javax.xml.bind:jaxb-api:2.2.2',
	jaxb_impl: 'com.sun.xml.bind:jaxb-impl:2.2.2',

	stax_api: 'javax.xml.stream:stax-api:1.0-2',

	javax_activation: 'javax.activation:activation:1.1',
	
	guice: 'com.google.inject.guice:guice:' + guiceVersion,
	guice_servlet: 'com.google.inject.extensions:guice-servlet:' + guiceVersion,
	
	google_collections: 'com.google.collections:google-collections:1.0',
	
	jersey_core: 'com.sun.jersey:jersey-core:' + jerseyVersion,
	jersey_server: 'com.sun.jersey:jersey-server:' + jerseyVersion,
	jersey_guice: 'com.sun.jersey.contribs:jersey-guice:' + jerseyVersion,
]
