//////////////////////////////////////////////////////////////////////////////
// This file contains settings for the Maven POM files that are deployed to
// our repository. You SHOULD edit them to fit your project, with name and
// different URLs.
//////////////////////////////////////////////////////////////////////////////

def PROJECT_NAME = 'example'

// Contents of the deployed POM files.

def pomConfig = {
	url 'http://trifork.com'
	organization {
		name 'Trifork'
		url 'http://trifork.com'
	}

	// If your are not using Jira or Git you should ofcourse leave the
	// appropriate sections out or edit them accordingly.

	issueManagement {
		system 'jira'
		url 'http://jira.trifork.com/projects/${PROJECT_NAME}/browse/HHH'
	}
	scm {
		url 'http://github.com/trifork/${PROJECT_NAME}'
		connection 'scm:git:http://github.com/trifork/${PROJECT_NAME}.git'
		developerConnection 'scm:git:git@github.com:trifork/${PROJECT_NAME}.git'
	}

	// You MUST have to update this section to point to any embedded licenses
	// or link to additional online licenses.

	licenses {
		license {
			name 'GNU Lesser General Public License'
			url 'http://www.gnu.org/licenses/lgpl-2.1.html'
			distribution 'repo'
		}
	}
	developers {
	}
}

//////////////////////////////////////////////////////////////////////////////
// You SHOULD not have to edit this file below this point,
// unless you need special behaviour.
//////////////////////////////////////////////////////////////////////////////

apply plugin: 'maven'

basePomConfig = pomConfig

configure(install.repositories.mavenInstaller) {
	pom.project pomConfig
}

// Settings for deploying to the repository.

// Usually when we have assembled a project we also want to
// install it in our local repo.
// TODO: These two lines might be removed or at least commented
// out in the future.

assemble.doLast( { install } )
uploadArchives.dependsOn install

// In order to deploy the artifacts we need some dependencies.
// The actually jars needed depend on how we are going to deploy.
// By default we just use HTTP to deploy.

configurations {
    deployerJars
}

dependencies {
    deployerJars "org.apache.maven.wagon:wagon-http:1.0-beta-2"
}

uploadArchives {
	repositories.mavenDeployer {
		configuration = configurations.deployerJars
		pom.project pomConfig
		repository(id: 'trifork-releases', 
			url: 'http://nexus.ci81.trifork.com/content/repositories/releases/')
		snapshotRepository(id: 'trifork-snapshots',
			url: 'http://nexus.ci81.trifork.com/content/repositories/snapshots/')
	}
}

// Make a jar containing the sources.

task sourcesJar(type: Jar, dependsOn: compileJava) {
	from sourceSets.main.allSource
	classifier = 'sources'
}

artifacts {
	archives sourcesJar
}

uploadArchives.dependsOn sourcesJar

// Standard information for the MANIFEST.MF files.
// Use the 'osgi' plugin if you need osgi support.

manifest.mainAttributes(
	provider: 'gradle',
	'Implementation-Url': 'http://trifork.com',
	'Implementation-Version': version,
	'Implementation-Vendor': 'Trifork',
	'Implementation-Vendor-Id': 'com.trifork'
)
