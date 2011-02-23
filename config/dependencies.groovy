//////////////////////////////////////////////////////////////////////////////
// This file contains a handy list of dependencies names and versions.
// This can be used to make sure dependencies are consistently versioned 
// in your projects.
//////////////////////////////////////////////////////////////////////////////

slf4jVersion = '1.6.1'

libraries = [

	// Javassist

	javassist: 'javassist:javassist:3.12.0.GA',

	// Logging

	slf4j_api: 'org.slf4j:slf4j-api:' + slf4jVersion,
	jcl_slf4j: 'org.slf4j:jcl-over-slf4j:' + slf4jVersion,
	log4j_slf4j: 'org.slf4j:log4j-over-slf4j:' + slf4jVersion,

	// Testing

	testng: 'org.testng:testng:5.14.6'
]