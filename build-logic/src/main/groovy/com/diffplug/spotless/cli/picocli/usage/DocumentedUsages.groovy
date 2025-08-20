package com.diffplug.spotless.cli.picocli.usage

enum DocumentedUsages {

	CLANG_FORMAT(),
	CLEAN_THAT(),
	ECLIPSE_WTP(),
	FORMAT_ANNOTATIONS(),
	MAIN(""),
	GOOGLE_JAVA_FORMAT(),
	LICENSE_HEADER(),
	PALANTIR_JAVA_FORMAT(),
	PRETTIER(),
	REMOVE_UNUSED_IMPORTS(),

	private final String fileName

	private final String formatterStepName

	DocumentedUsages() {
		this(null)
	}

	DocumentedUsages(String formatterStepName) {
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
