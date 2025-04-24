package com.diffplug.spotless.cli.io

import javax.inject.Inject

import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

import groovy.transform.CompileStatic

/**
 * The default copy task snapshots the whole targetDir for up-to-date checks, so
 * if we copy a single file into the project directory of the root project, the whole project is snapshoted
 * and so up-to-date checks are not working as expected.
 * For this case, use a custom task which only snapshots the single file (and tokens).
 */
@CacheableTask
@CompileStatic
abstract class CopySingleTemplateFileTask extends DefaultTask{

	@InputFile
	@PathSensitive(PathSensitivity.RELATIVE)
	abstract RegularFileProperty getTemplate()

	/** Map of token → value.  Use Provider<String> for CC-safety. */
	@Input
	abstract MapProperty<String, Object> getTokens()

	@OutputFile
	final RegularFileProperty outputFile = project.objects.fileProperty()

	@Inject
	abstract FileSystemOperations getFs()

	@TaskAction
	void copy() {
		fs.copy { spec ->
			spec.from(template)
			spec.into(outputFile.get().asFile.parentFile)
			spec.rename { outputFile.get().asFile.name }
			spec.filter(ReplaceTokens, tokens: tokens.get())
		}
	}
}
