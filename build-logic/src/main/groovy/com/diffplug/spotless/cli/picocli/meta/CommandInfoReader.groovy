package com.diffplug.spotless.cli.picocli.meta

import java.lang.annotation.Annotation

import com.diffplug.spotless.cli.cp.ClasspathScanner

class CommandInfoReader {

	ClasspathScanner scanner

	CommandInfoReader(ClasspathScanner scanner) {
		this.scanner = scanner
	}

	String readCommandName(Class<?> commandClass) {
		Class<? extends Annotation> annotationClass = scanner.loadClass('picocli.CommandLine$Command') as Class<? extends Annotation>
		Object commandAnnotation = commandClass.getAnnotation(annotationClass)
		if (commandAnnotation == null) {
			throw new IllegalArgumentException("Class $commandClass is not annotated with @Command")
		}
		String commandName = annotationClass.getMethod("name").invoke(commandAnnotation) as String
		return commandName
	}
}
