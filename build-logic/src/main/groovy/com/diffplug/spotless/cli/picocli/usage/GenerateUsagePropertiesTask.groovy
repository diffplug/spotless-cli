package com.diffplug.spotless.cli.picocli.usage

import java.nio.charset.StandardCharsets
import javax.inject.Inject

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult

import com.diffplug.spotless.cli.cp.ClasspathScanner
import com.diffplug.spotless.cli.picocli.meta.CommandInfoReader

/**
 * This task dumps usage helps for the main CLI and all formatters into a file each.
 */
@CacheableTask
abstract class GenerateUsagePropertiesTask extends DefaultTask {

	@Classpath
	abstract Property<FileCollection> getRuntimeClasspath()

	@Input
	abstract Property<String> getMainClassName()

	@OutputDirectory
	abstract Property<File> getOutputDir()

	@Inject
	abstract ExecOperations getExecOperations()

	GenerateUsagePropertiesTask() {
		def mainSourceSet = project.getExtensions().getByType(JavaPluginExtension.class).sourceSets.named('main').get()
		runtimeClasspath.convention(mainSourceSet.runtimeClasspath + mainSourceSet.output)
		mainClassName.convention("com.diffplug.spotless.cli.SpotlessCLI")
	}

	@TaskAction
	void generate() {
		// remove all files from outputDir if any
		outputDir.get().deleteDir()

		List<String> formatterNames = DocumentedUsages.formatterNames
		Map<String, String> formatterUsages = [:]
		formatterNames.each { formatterName ->
			formatterUsages.put(formatterName, getUsage(formatterName))
		}

		formatterUsages.each { formatterName, usage ->
			writeUsageFile(usage, formatterName)
		}
	}

	String getUsage(String formatterName) {
		ByteArrayOutputStream output = new ByteArrayOutputStream()
		ExecResult result = execOperations.javaexec {
			it.classpath = runtimeClasspath.get()
			it.standardOutput = output

			it.mainClass.set(mainClassName.get())
			it.args(formatterName, "--help")
		}

		return output.toString(StandardCharsets.UTF_8)
	}

	void writeUsageFile(String usage, String formatterName) {
		File usageFile = new File(outputDir.get(), "${formatterName}.spotless.usage.txt")
		usageFile.parentFile.mkdirs()
		usageFile.withPrintWriter(StandardCharsets.UTF_8.toString()) { writer ->
			writer.write(usage)
		}
	}
}
