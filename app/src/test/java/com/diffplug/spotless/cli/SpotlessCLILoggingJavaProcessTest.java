/*
 * Copyright 2025 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.spotless.cli;

import java.util.function.Function;
import java.util.regex.Pattern;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import com.diffplug.spotless.cli.steps.GoogleJavaFormat;
import com.diffplug.spotless.tag.CliNativeTest;
import com.diffplug.spotless.tag.CliProcessTest;

@CliProcessTest
@CliNativeTest
public class SpotlessCLILoggingJavaProcessTest extends CLIIntegrationHarness {

    public static final String NEEDS_REFORMATTING_STATEMENT = "needs reformatting";
    public static final String FILE_NAME = "Java.java";
    public static final String LOGFMT_KEYWORD = "timestamp=";
    public static final Pattern FILE_DIFF_MARKER_PATTERN = Pattern.compile("^\\Q******\\E", Pattern.MULTILINE);
    public static final String LOGGER_SOURCE_NAME_FROM_SPOTLESS_CLI_PACKAGE =
            "source.logger.name=com.diffplug.spotless.cli";
    public static final String LOGGER_SOURCE_NAME_SPOTLESS_BUT_NOT_CLI_PACKAGE_PATTERN =
            "source\\.logger\\.name=com\\.diffplug\\.spotless\\.(?!cli\\.)";
    public static final String LOGFMT_DEBUG_LOG_LEVEL = "level=FINE";
    public static final String LOGGER_SOURCE_NAME_OUTSIDE_SPOTLESS_PATTERN =
            "source\\.logger\\.name=(?!com\\.diffplug\\.spotless\\.)";
    public static final String LOGGER_SOURCE_NAME_OUTSIDE_SPOTLESS_ON_INFO_LEVEL_PATTERN =
            "level=INFO .* " + LOGGER_SOURCE_NAME_OUTSIDE_SPOTLESS_PATTERN;
    public static final String LOGGER_SOURCE_NAME_OUTSIDE_SPOTLESS_ON_DEBUG_LEVEL_PATTERN =
            "level=FINE .* " + LOGGER_SOURCE_NAME_OUTSIDE_SPOTLESS_PATTERN;

    private SpotlessCLIRunner.Result runTestWith(Function<SpotlessCLIRunner, SpotlessCLIRunner> configurer) {
        setFile(FILE_NAME).toResource("java/googlejavaformat/JavaCodeUnformatted.test");

        SpotlessCLIRunner spotlessCLIRunner = cliRunner().withTargets(FILE_NAME).withOption("--mode", "check");
        spotlessCLIRunner = configurer.apply(spotlessCLIRunner);

        SpotlessCLIRunner.Result result =
                spotlessCLIRunner.withStep(GoogleJavaFormat.class).runAndFail();

        return result;
    }

    @Test
    void defaultLogLevelGivesSomeOutput() {
        SpotlessCLIRunner.Result result = runTestWith(runner -> runner);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.stdErr())
                    .contains(NEEDS_REFORMATTING_STATEMENT)
                    .contains(FILE_NAME)
                    .doesNotContain(LOGFMT_KEYWORD)
                    .doesNotContainPattern(FILE_DIFF_MARKER_PATTERN);
            softly.assertThat(result.exitCode()).isEqualTo(1);
        });
    }

    //    @Override
    //    protected SpotlessCLIRunner createRunnerForTag() {
    //        return SpotlessCLIRunner.create();
    //    }

    @Test
    void quietLogLevelDoesGiveNoOutput() {
        SpotlessCLIRunner.Result result = runTestWith(runner -> runner.withOption("--quiet"));
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.stdErr())
                    .doesNotContain(NEEDS_REFORMATTING_STATEMENT)
                    .doesNotContain(FILE_NAME)
                    .doesNotContain(LOGFMT_KEYWORD)
                    .doesNotContain(LOGGER_SOURCE_NAME_FROM_SPOTLESS_CLI_PACKAGE)
                    .doesNotContainPattern(LOGGER_SOURCE_NAME_SPOTLESS_BUT_NOT_CLI_PACKAGE_PATTERN)
                    .doesNotContainPattern(LOGGER_SOURCE_NAME_OUTSIDE_SPOTLESS_PATTERN)
                    .doesNotContain(LOGFMT_DEBUG_LOG_LEVEL)
                    .doesNotContainPattern(FILE_DIFF_MARKER_PATTERN);
            softly.assertThat(result.exitCode()).isEqualTo(1);
        });
    }

    @Test
    void verbose1LogLevelShowsCLIInfo() {
        SpotlessCLIRunner.Result result = runTestWith(runner -> runner.withOption("-v"));
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.stdErr())
                    .contains(NEEDS_REFORMATTING_STATEMENT)
                    .contains(FILE_NAME)
                    .contains(LOGFMT_KEYWORD)
                    .contains(LOGGER_SOURCE_NAME_FROM_SPOTLESS_CLI_PACKAGE)
                    .doesNotContainPattern(LOGGER_SOURCE_NAME_SPOTLESS_BUT_NOT_CLI_PACKAGE_PATTERN)
                    .doesNotContainPattern(LOGGER_SOURCE_NAME_OUTSIDE_SPOTLESS_PATTERN)
                    .doesNotContain(LOGFMT_DEBUG_LOG_LEVEL)
                    .doesNotContainPattern(FILE_DIFF_MARKER_PATTERN); // no diffs
            softly.assertThat(result.exitCode()).isEqualTo(1);
        });
    }

    @Test
    void verbose2LogLevelShowsCLIAndAllSpotlessOnLevelInfo() {
        SpotlessCLIRunner.Result result = runTestWith(runner -> runner.withOption("-vv"));
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.stdErr())
                    .contains(NEEDS_REFORMATTING_STATEMENT)
                    .contains(FILE_NAME)
                    .contains(LOGFMT_KEYWORD)
                    .containsPattern(LOGGER_SOURCE_NAME_SPOTLESS_BUT_NOT_CLI_PACKAGE_PATTERN)
                    .doesNotContainPattern(LOGGER_SOURCE_NAME_OUTSIDE_SPOTLESS_PATTERN)
                    .doesNotContain(LOGFMT_DEBUG_LOG_LEVEL)
                    .doesNotContainPattern(FILE_DIFF_MARKER_PATTERN); // no diffs
            softly.assertThat(result.exitCode()).isEqualTo(1);
        });
    }

    @Test
    void verbose3LogLevelShowsCLIAndSpotlessOnLevelDebugIncludingDiffs() {
        SpotlessCLIRunner.Result result = runTestWith(runner -> runner.withOption("-vvv"));
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.stdErr())
                    .contains(NEEDS_REFORMATTING_STATEMENT)
                    .contains(FILE_NAME)
                    .contains(LOGFMT_KEYWORD)
                    .containsPattern(LOGGER_SOURCE_NAME_SPOTLESS_BUT_NOT_CLI_PACKAGE_PATTERN)
                    .doesNotContainPattern(LOGGER_SOURCE_NAME_OUTSIDE_SPOTLESS_PATTERN)
                    .contains(LOGFMT_DEBUG_LOG_LEVEL)
                    .containsPattern(FILE_DIFF_MARKER_PATTERN);
            softly.assertThat(result.exitCode()).isEqualTo(1);
        });
    }

    @Test
    void verbose4LogLevelShowsCliAndSpotlessOnDebugAndEverythingElseOnInfo() {
        SpotlessCLIRunner.Result result = runTestWith(runner -> runner.withOption("-vvvv"));
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.stdErr())
                    .contains(NEEDS_REFORMATTING_STATEMENT)
                    .contains(FILE_NAME)
                    .contains(LOGFMT_KEYWORD)
                    .containsPattern(LOGGER_SOURCE_NAME_OUTSIDE_SPOTLESS_ON_INFO_LEVEL_PATTERN)
                    .doesNotContainPattern(LOGGER_SOURCE_NAME_OUTSIDE_SPOTLESS_ON_DEBUG_LEVEL_PATTERN)
                    .contains(LOGFMT_DEBUG_LOG_LEVEL)
                    .containsPattern(FILE_DIFF_MARKER_PATTERN);
            softly.assertThat(result.exitCode()).isEqualTo(1);
        });
    }

    @Test
    void verbose5LogLevelShowsCliAndSpotlessAndEverythingElseOnDebug() {
        SpotlessCLIRunner.Result result = runTestWith(runner -> runner.withOption("-vvvvv"));
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.stdErr())
                    .contains(NEEDS_REFORMATTING_STATEMENT)
                    .contains(FILE_NAME)
                    .contains(LOGFMT_KEYWORD)
                    .containsPattern(LOGGER_SOURCE_NAME_OUTSIDE_SPOTLESS_ON_INFO_LEVEL_PATTERN)
                    .containsPattern(LOGGER_SOURCE_NAME_OUTSIDE_SPOTLESS_ON_DEBUG_LEVEL_PATTERN)
                    .contains(LOGFMT_DEBUG_LOG_LEVEL)
                    .containsPattern(FILE_DIFF_MARKER_PATTERN);
            softly.assertThat(result.exitCode()).isEqualTo(1);
        });
    }
}
