package com.diffplug.spotless.cli.cp

import java.lang.reflect.Modifier
import java.util.function.Predicate
import java.util.jar.JarFile

import org.gradle.api.file.FileCollection

class ClasspathScanner {


	private final FileCollection classpath
	private final URLClassLoader urlClassLoader
	private final Predicate<String> classFilter

	ClasspathScanner(FileCollection classpath, Predicate<String> classFilter = { true }) {
		this.classpath = classpath
		this.urlClassLoader =new URLClassLoader(classpath.files.collect { it.toURI().toURL() } as URL[])
		this.classFilter = classFilter
	}

	private ClasspathScanner(FileCollection classpath, URLClassLoader urlClassLoader, Predicate<String> classFilter) {
		this.classpath = classpath
		this.urlClassLoader = urlClassLoader
		this.classFilter = classFilter
	}

	ClasspathScanner withClassFilter(Predicate<String> classFilter) {
		return new ClasspathScanner(classpath, urlClassLoader, classFilter)
	}

	ClasspathScanner withoutClassFilter() {
		return new ClasspathScanner(classpath, urlClassLoader, { true })
	}

	Class<?> loadClass(String className) {
		return urlClassLoader.loadClass(className)
	}

	List<Class<?>> findInstancesOf(String targetClassName) {
		Class<?> targetClass = loadClass(targetClassName)

		def instanceClassNames = []

		urlClassLoader.URLs.each { URL url ->
			def file = new File(url.toURI())
			if (file.isDirectory()) {
				// Scan classes from a directory
				instanceClassNames.addAll(findClassesInDirectory(file, file, targetClass))
			} else if (file.name.endsWith('.jar')) {
				// Scan classes from a JAR
				instanceClassNames.addAll(findClassesInJar(file, targetClass))
			}
		}
		return instanceClassNames.collect { loadClass(it) }
	}


	private List<String> findClassesInJar(File jarFile, Class targetClass) {
		List<String> implementationClassNames = []
		JarFile jar = new JarFile(jarFile)

		jar.entries().each { entry ->
			if (entry.name.endsWith('.class')) {
				String className = entry.name.replace('/', '.').replace('.class', '')
				if (isInstance(className, targetClass)) {
					implementationClassNames << className
				}
			}
		}
		return implementationClassNames
	}

	private List<String> findClassesInDirectory(File baseDir, File currentDir, Class targetClass) {
		List<String> implementationClassNames = []

		currentDir.eachFileRecurse { file ->
			if (file.isFile() && file.name.endsWith('.class')) {
				def relativePath = file.absolutePath.substring(baseDir.absolutePath.length() + 1)
				def className = relativePath.replace(File.separator, '.').replace('.class', '')
				if (isInstance(className, targetClass)) {
					implementationClassNames << className
				}
			}
		}
		return implementationClassNames
	}

	private boolean isInstance(String className, Class targetClass) {
		if (!classFilter.test(className)) {
			return false
		}
		Class<?> clazz = loadClass(className)
		return targetClass.isAssignableFrom(clazz) && !clazz.isInterface() && !Modifier.isAbstract(clazz.modifiers)
	}
}
