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

// TODO: This should really be move out as a plugin in
// trifork's gradle plugins.

// Only enable test coverage when we call the 'check' task.

gradle.taskGraph.whenReady { taskGraph ->
if (taskGraph.hasTask(":${project.name}:check")) {

configurations {
	emma
}

dependencies {
	emma "emma:emma:2.0.5312"
	emma "emma:emma_ant:2.0.5312"
}

def emmaDir = "${project.reportsDir.absolutePath}/emma"

test.configure {
	jvmArgs "-Demma.coverage.out.file=${emmaDir}/metadata.emma"
	jvmArgs "-Demma.coverage.out.merge=true"	
}

test.doFirst {
	logger.info(Logging.LIFECYCLE, ":${project.name}:testCoverage")
	
	ant.taskdef( resource:"emma_ant.properties", classpath: configurations.emma.asPath)
	ant.path(id:"run.classpath") {
		pathelement(location:sourceSets.main.classesDir.absolutePath) 
	}
	ant.emma(verbosity: "warning") {
		instr(merge: "true", destdir: "${emmaDir}/classes", instrpathref: "run.classpath", metadatafile: "${emmaDir}/metadata.emma"){
			instrpath{
				fileset(dir: sourceSets.main.classesDir.absolutePath, includes: "*.class")
			}
		}
	}
	setClasspath(files("${emmaDir}/classes") + configurations.emma +  getClasspath())
}

test.doLast {
	ant.path(id: "src.path") {
		sourceSets.main.java.srcDirs.each{
			pathelement(location:it.absolutePath ) 
		}
	}
	ant.emma(enabled: "true", verbosity: "warning") {
		report(sourcepathref:"src.path") {
			fileset(dir:"${emmaDir}", includes: "*.emma")
			html(outfile:"${project.reportsDir.absolutePath}/emma/coverage.html")
			xml(outfile:"${project.reportsDir.absolutePath}/emma/coverage.xml")
		}
	}
}

}}
