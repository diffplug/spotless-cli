/*
 * Copyright 2024 DiffPlug
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.diffplug.spotless.ThrowingEx;

import picocli.CommandLine;

public class SpotlessCLIRunnerInSameThread extends SpotlessCLIRunner {

    static final String SPOTLESS_CLI_IN_SAME_THREAD = "spotless.cli.inSameThread";

    protected Result executeCommand(List<String> args) {
        CommandLine commandLine = createCommandLine();

        StringWriter out = new StringWriter();
        StringWriter err = new StringWriter();

        try (PrintWriter outWriter = new PrintWriter(out);
                PrintWriter errWriter = new PrintWriter(err)) {
            commandLine.setOut(outWriter);
            commandLine.setErr(errWriter);
            Exception executionException = null;
            Integer exitCode = null;
            try {
                exitCode = commandLine.execute(argsWithBaseDir(args));
            } catch (Exception e) {
                executionException = e;
            }

            // finalize
            outWriter.flush();
            errWriter.flush();
            return new Result(exitCode, executionException, out.toString(), err.toString());
        }
    }

    private static CommandLine createCommandLine() {
        return ThrowingEx.get(() -> {
            Class<?> cliClass = Class.forName("com.diffplug.spotless.cli.SpotlessCLI");
            Object cliInstance = cliClass.getDeclaredMethod("createInstance").invoke(null);
            return (CommandLine)
                    cliClass.getDeclaredMethod("createCommandLine", cliClass).invoke(null, cliClass.cast(cliInstance));
        });
    }

    private String[] argsWithBaseDir(List<String> args) {
        // prepend the base dir
        String[] argsWithBaseDir = new String[args.size() + 2];
        argsWithBaseDir[0] = "--basedir";
        argsWithBaseDir[1] = workingDir().getAbsolutePath();
        System.arraycopy(args.toArray(new String[0]), 0, argsWithBaseDir, 2, args.size());
        return argsWithBaseDir;
    }
}
