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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

public class ForeignExeMock {

    enum TargetOs {
        WINDOWS {
            @Override
            protected ForeignExeMockWriter mockWriter(PrintWriter output) {
                return new WindowsForeignExeMockWriter(output);
            }

            @Override
            protected String asFileName(String name) {
                return name + ".bat";
            }
        },
        UNIX {
            @Override
            protected ForeignExeMockWriter mockWriter(PrintWriter output) {
                return new UnixForeignExeMockWriter(output);
            }

            @Override
            protected String asFileName(String name) {
                return name;
            }
        };

        static TargetOs current() {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                return WINDOWS;
            } else {
                return UNIX;
            }
        }

        protected abstract ForeignExeMockWriter mockWriter(PrintWriter output);

        protected abstract String asFileName(String name);
    }

    private final String name;

    private final String content;

    private final TargetOs targetOs;

    private ForeignExeMock(@NotNull String name, @NotNull String content, @NotNull TargetOs targetOs) {
        this.name = Objects.requireNonNull(name);
        this.content = Objects.requireNonNull(content);
        this.targetOs = Objects.requireNonNull(targetOs);
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getFileName() {
        return targetOs.asFileName(name);
    }

    public static Builder builder(@NotNull String name, @NotNull String version) {
        return new Builder(Objects.requireNonNull(name), Objects.requireNonNull(version));
    }

    public static class Builder {
        @NotNull private final String name;

        @NotNull private final String version;

        private int exitCode = 0;

        private final Map<String, String> stringReturningOptions = new LinkedHashMap<>();

        private final Map<String, List<String>> stringConsumingOptions = new LinkedHashMap<>();

        private boolean readFromStdin = false;

        private boolean writeToStdout = false;

        private Builder(@NotNull String name, @NotNull String version) {
            this.name = Objects.requireNonNull(name);
            this.version = Objects.requireNonNull(version);
        }

        @NotNull public Builder exitCode(int exitCode) {
            this.exitCode = exitCode;
            return this;
        }

        @NotNull public Builder withStringReturningOption(@NotNull String optionName, @NotNull String returnValue) {
            stringReturningOptions.put(Objects.requireNonNull(optionName), Objects.requireNonNull(returnValue));
            return this;
        }

        @NotNull public Builder withStringConsumingOption(@NotNull String optionName) {
            stringConsumingOptions.put(Objects.requireNonNull(optionName), List.of());
            return this;
        }

        @NotNull public Builder withStringConsumingOption(@NotNull String optionName, @NotNull List<String> values) {
            stringConsumingOptions.put(Objects.requireNonNull(optionName), Objects.requireNonNull(values));
            return this;
        }

        @NotNull public Builder withReadFromStdin() {
            this.readFromStdin = true;
            return this;
        }

        @NotNull public Builder withWriteToStdout() {
            this.writeToStdout = true;
            return this;
        }

        private Map<String, String> optionDefaults() {
            Map<String, String> defaults = new LinkedHashMap<>();
            stringConsumingOptions.forEach((k, v) -> defaults.put(k, ""));
            return defaults;
        }

        private String content(TargetOs os) {
            try (StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter)) {
                ForeignExeMockWriter writer = os.mockWriter(printWriter);
                writer = writer.writeIntro(optionDefaults())
                        .writeOptionParserIntro()
                        .writeStringReturningOption("--version", "version " + version);
                for (Map.Entry<String, String> entry : stringReturningOptions.entrySet()) {
                    writer = writer.writeStringReturningOption(entry.getKey(), entry.getValue());
                }

                for (Map.Entry<String, List<String>> entry : stringConsumingOptions.entrySet()) {
                    if (entry.getValue().isEmpty()) {
                        writer = writer.writeStringConsumingOption(entry.getKey());
                    } else {
                        writer = writer.writeStringConsumingOption(entry.getKey(), entry.getValue());
                    }
                }

                writer = writer.writeOptionParserOutro();

                if (readFromStdin) {
                    writer = writer.writeReadFromStdin();
                }
                if (writeToStdout) {
                    writer = writer.writeWriteToStdout();
                }
                writer = writer.writeExitCode(exitCode);
                return stringWriter.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public ForeignExeMock build() {
            return build(TargetOs.current());
        }

        ForeignExeMock build(TargetOs os) {
            return new ForeignExeMock(name, content(os), os);
        }
    }

    private interface ForeignExeMockWriter {
        ForeignExeMockWriter writeIntro(@NotNull Map<String, String> optionDefaults);

        ForeignExeMockWriter writeStringReturningOption(String optionName, @NotNull String optionValue);

        ForeignExeMockWriter writeStringConsumingOption(String optionName);

        ForeignExeMockWriter writeStringConsumingOption(String optionName, @NotNull List<String> validValues);

        ForeignExeMockWriter writeReadFromStdin();

        ForeignExeMockWriter writeWriteToStdout();

        ForeignExeMockWriter writeExitCode(int exitCode);

        ForeignExeMockWriter writeOptionParserIntro();

        ForeignExeMockWriter writeOptionParserOutro();
    }

    private static class UnixForeignExeMockWriter implements ForeignExeMockWriter {

        private final PrintWriter output;

        UnixForeignExeMockWriter(@NotNull PrintWriter output) {
            this.output = Objects.requireNonNull(output);
        }

        private String asVarName(String optionName) {
            return optionName.replaceAll("[^a-zA-Z0-9]", "_");
        }

        @Override
        public ForeignExeMockWriter writeIntro(@NotNull Map<String, String> optionDefaults) {
            Objects.requireNonNull(optionDefaults);
            output.println("#!/bin/bash");
            output.println();
            optionDefaults.forEach((k, v) -> output.printf("%s=\"%s\"\n", asVarName(k), v));
            // collect value from stdin
            output.println("stdin_value=\"\"");
            return this;
        }

        @Override
        public ForeignExeMockWriter writeOptionParserIntro() {
            output.println("while [[ \"$#\" -gt 0 ]]; do");
            output.println("  case \"$1\" in");
            return this;
        }

        @Override
        public ForeignExeMockWriter writeStringReturningOption(String optionName, @NotNull String optionValue) {
            output.printf("    %s)\n", optionName);
            output.printf("      echo \"%s\"\n", optionValue);
            output.println("      exit 0");
            output.println("      ;;");
            return this;
        }

        @Override
        public ForeignExeMockWriter writeStringConsumingOption(String optionName) {
            output.printf("    %s=*)\n", optionName);
            output.printf("      %s=\"${1#%s=}\"\n", asVarName(optionName), optionName);
            output.println("      shift");
            output.println("      ;;");
            output.printf("    %s)\n", optionName);
            output.printf("      %s=\"$2\"\n", asVarName(optionName));
            output.println("      shift 2");
            output.println("      ;;");
            return this;
        }

        @Override
        public ForeignExeMockWriter writeStringConsumingOption(String optionName, @NotNull List<String> validValues) {
            // same as above, but with a list of valid values which are checked and failed if wrong
            output.println("# check for --x=y writing variant");
            output.printf("    %s=*)\n", optionName);
            output.printf("      %s=\"${1#%s=}\"\n", asVarName(optionName), optionName);
            writeValidValuesSubCheck(optionName, validValues);
            output.println("      shift");
            output.println("      ;;");
            output.println("#check for --x y writing variant");
            output.printf("    %s)\n", optionName);
            output.printf("      %s=\"$2\"\n", asVarName(optionName));
            writeValidValuesSubCheck(optionName, validValues);
            output.println("      shift 2");
            output.println("      ;;");
            return this;
        }

        private void writeValidValuesSubCheck(String optionName, @NotNull List<String> validValues) {
            output.printf("      case \"$%s\" in\n", asVarName(optionName));
            output.printf("        %s)\n", validValues.stream().collect(Collectors.joining("|")));
            output.println("          ;;");
            output.println("        *)");
            output.printf("          echo \"Unknown %s: $2\"\n", asVarName(optionName));
            output.println("          exit 1");
            output.println("          ;;");
            output.println("      esac");
        }

        @Override
        public ForeignExeMockWriter writeOptionParserOutro() {
            output.println("    *)");
            output.println("      echo \"Unknown parameter: $1\"");
            output.println("      exit 1");
            output.println("      ;;");
            output.println("  esac");
            output.println("done");
            output.println();
            return this;
        }

        @Override
        public ForeignExeMockWriter writeReadFromStdin() {
            // collect the stdin to stdin_value
            output.println("while IFS= read -r line; do");
            output.println("  stdin_value+=\"${line}\"$'\\n'");
            output.println("done");
            output.println();
            return this;
        }

        @Override
        public ForeignExeMockWriter writeWriteToStdout() {
            // write the stdin_value to stdout but add 4 spaces at the end of each line that does not already end with 4
            // spaces
            //            output.println("echo \"${stdin_value}\" | sed '/    $/! s/$/    /'");
            output.println("printf \"%s\" \"$stdin_value\" | sed '/    $/! s/$/    /'");
            return this;
        }

        @Override
        public ForeignExeMockWriter writeExitCode(int exitCode) {
            output.printf("exit %d\n", exitCode);
            return this;
        }
    }

    private static class WindowsForeignExeMockWriter implements ForeignExeMockWriter {

        private final PrintWriter output;

        WindowsForeignExeMockWriter(@NotNull PrintWriter output) {
            this.output = Objects.requireNonNull(output);
        }

        private String asVarName(String optionName) {
            return optionName.replaceAll("[^a-zA-Z0-9]", "_").toUpperCase();
        }

        // ───────────────────────── intro ─────────────────────────
        @Override
        public ForeignExeMockWriter writeIntro(@NotNull Map<String, String> optionDefaults) {
            output.println("@echo off");
            output.println("rem --------------- AUTO‑GENERATED MOCK ---------------");
            output.println("setlocal EnableDelayedExpansion");
            output.println();

            optionDefaults.forEach((k, v) -> output.printf("set \"%s=%s\"%n", asVarName(k), v));

            output.println("set \"stdin_file=%TEMP%\\foreign_mock_%RANDOM%%RANDOM%.tmp\"");
            output.println();
            return this;
        }

        // ───────────────────── option parser ─────────────────────
        @Override
        public ForeignExeMockWriter writeOptionParserIntro() {
            output.println(":parse_args");
            output.println("if \"%~1\"==\"\" goto end_parse_args");
            output.println("set \"arg=%~1\"");
            output.println();
            return this;
        }

        @Override
        public ForeignExeMockWriter writeStringReturningOption(String optionName, @NotNull String optionValue) {
            output.printf("if /i \"!arg!\"==\"%s\" (%n", optionName);
            output.printf("    echo %s%n", optionValue.replace("\"", "\"\""));
            output.println("    exit /b 0");
            output.println(")");
            output.println();
            return this;
        }

        @Override
        public ForeignExeMockWriter writeStringConsumingOption(String optionName) {
            consumeOption(optionName, null);
            return this;
        }

        @Override
        public ForeignExeMockWriter writeStringConsumingOption(String optionName, @NotNull List<String> validValues) {
            consumeOption(optionName, validValues);
            return this;
        }

        /* ── helper ──────────────────────────────────────────── */
        private void consumeOption(String optionName, List<String> validValues) {
            final String var = asVarName(optionName);
            final int len = optionName.length() + 1; // option + '='

            /* --opt=value --------------------------------------------------------- */
            output.printf("if /i \"!arg:~0,%d!\"==\"%s=\" (%n", len, optionName);
            output.printf("    set \"%1$s=!arg:~%2$d!\"%n", var, len);
            writeValidation(var, validValues);
            output.println("    shift");
            output.println("    goto parse_args");
            output.println(")");
            output.println();

            /* --opt  value -------------------------------------------------------- */
            output.printf("if /i \"!arg!\"==\"%s\" (%n", optionName);
            output.printf("    set \"%1$s=%%~2\"%n", var);
            writeValidation(var, validValues);
            output.println("    shift & shift");
            output.println("    goto parse_args");
            output.println(")");
            output.println();
        }

        private void writeValidation(String var, List<String> validValues) {
            if (validValues == null || validValues.isEmpty()) return;

            output.println("    set \"_valid=0\"");
            output.printf(
                    "    for %%%%v in (%s) do if /i \"!%s!\"==\"%%%%v\" set \"_valid=1\"%n",
                    String.join(" ", validValues), var);
            output.println("    if \"!_valid!\"==\"0\" (");
            output.printf("        echo Unknown %s: !%s!%n", var.toLowerCase(), var);
            output.println("        exit /b 1");
            output.println("    )");
        }

        // ───────────────────── parser outro ─────────────────────
        @Override
        public ForeignExeMockWriter writeOptionParserOutro() {
            output.println("echo Unknown parameter: !arg!");
            output.println("exit /b 1");
            output.println();
            output.println(":end_parse_args");
            output.println();
            return this;
        }

        // ───────────────────── stdin → file ─────────────────────
        @Override
        public ForeignExeMockWriter writeReadFromStdin() {
            output.println("rem -- read everything the caller pipes into us ------------");
            output.println("more > \"!stdin_file!\"");
            output.println();
            return this;
        }

        // ─────────────── file → stdout (pad lines) ──────────────
        @Override
        public ForeignExeMockWriter writeWriteToStdout() {
            output.println("for /f \"usebackq delims=\" %%L in (\"!stdin_file!\") do (");
            output.println("    set \"line=%%L\"");
            output.println("    echo(!line!| findstr /r \"    $\" >nul");
            output.println("    if errorlevel 1 (");
            output.println("        echo(!line!    ");
            output.println("    ) else (");
            output.println("        echo(!line!");
            output.println("    )");
            output.println(")");
            output.println("del \"!stdin_file!\" >nul 2>&1");
            output.println();
            return this;
        }

        // ───────────────────────── exit ─────────────────────────
        @Override
        public ForeignExeMockWriter writeExitCode(int exitCode) {
            output.printf("exit /b %d%n", exitCode);
            return this;
        }
    }
}
