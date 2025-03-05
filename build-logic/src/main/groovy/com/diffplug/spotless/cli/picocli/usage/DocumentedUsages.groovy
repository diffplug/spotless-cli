package com.diffplug.spotless.cli.picocli.usage

enum DocumentedUsages {

	MAIN(""),
	GOOGLE_JAVA_FORMAT(),
	LICENSE_HEADER(),
	PRETTIER()

	private final String fileName

	private final String formatterStepName

	DocumentedUsages(){
		this(null)
	}

	DocumentedUsages(String formatterStepName){
		this.formatterStepName = formatterStepName ?: name().toLowerCase().replace('_', '-')
		this.fileName = "${name().toLowerCase().replace('_', '-')}.spotless.usage.txt"
	}

	String getFileName() {
		return fileName
	}

	String getFormatterStepName() {
		return formatterStepName
	}

	static List<String> getFormatterNames() {
		return values().collect { it.formatterStepName }
	}

	static List<String> getFileNames() {
		return values().collect { it.fileName }
	}
}
