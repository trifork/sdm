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
