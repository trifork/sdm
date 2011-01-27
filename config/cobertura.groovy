def cobSerFile="${project.projectDir}/cobertura.ser"
def classesOriginal="${sourceSets.main.classesDir}"
def classesCopy="${classesOriginal}-copy"


dependencies {
	testRuntime 'net.sourceforge.cobertura:cobertura:1.9.4'
}


test.doFirst {
	ant {
		// delete cobertura data file, otherwise coverage would be added. (TODO: huh?)
		delete(file:cobSerFile, failonerror:false)
		
		// delete any copy of the class files.
		delete(dir: classesCopy, failonerror:false)
		
		// Import cobertura ant tasks, from cobertura.jar.
		taskdef(resource:'tasks.properties', classpath: configurations.testRuntime.asPath)
		
		// Create copy of original .class files.
		copy(todir: classesCopy) { fileset(dir: classesOriginal) }
		
		// Instrument the class files.
		'cobertura-instrument'(datafile:cobSerFile) {
			fileset(dir: classesOriginal, includes:"**/*.class")
		}
	}
}

test {
	systemProperties ["net.sourceforge.cobertura.datafile"] = cobSerFile
}

test.doLast {
	if (new File(classesCopy).exists()) {

		// Replace instrumented classes with the copy.
		ant {
			delete(file: classesOriginal)
			move(file: classesCopy, tofile: classesOriginal)
		}
		
		def outputFormat = 'html'
		
		if (settings.test.cobertura != null && settings.test.cobertura.format != null) {
			outputFormat = settings.test.cobertura.format
		}
		
		// Create cobertura HTML reports.
		ant.'cobertura-report'(destdir:"${project.reportsDir}/coverage",
			format:outputFormat, srcdir:'src/main/java', datafile:cobSerFile)
		
		//TODO: Figure out why we can't have the ser file in the build dir
		// If we do so it creates two files...
		
		ant.delete(file: cobSerFile, failonerror:false)
    }
}
