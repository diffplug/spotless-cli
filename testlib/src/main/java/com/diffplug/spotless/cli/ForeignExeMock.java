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
                return new UnixForeignExeMockWriter(output); // TODO
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

    static Builder builder(@NotNull String name, @NotNull String version) {
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
                        .writeStringReturningOption("--version", version);
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

        private String nonPrefixed(String optionName) {
            if (optionName.startsWith("--")) {
                return optionName.substring(2);
            } else if (optionName.startsWith("-")) {
                return optionName.substring(1);
            } else {
                return optionName;
            }
        }

        @Override
        public ForeignExeMockWriter writeIntro(@NotNull Map<String, String> optionDefaults) {
            Objects.requireNonNull(optionDefaults);
            output.println("#!/bin/bash");
            output.println();
            optionDefaults.forEach((k, v) -> output.printf("%s=\"%s\"\n", nonPrefixed(k), v));
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
            output.printf("    %s)\n", optionName);
            output.printf("      %s=\"$2\"\n", nonPrefixed(optionName));
            output.println("      shift 2");
            output.println("      ;;");
            return this;
        }

        @Override
        public ForeignExeMockWriter writeStringConsumingOption(String optionName, @NotNull List<String> validValues) {
            // same as above, but with a list of valid values which are checked and failed if wrong
            output.printf("    %s)\n", optionName);
            output.printf("      %s=\"$2\"\n", nonPrefixed(optionName));
            output.println("      case \"$2\" in");
            output.printf("        %s)\n", validValues.stream().collect(Collectors.joining("|")));
            output.println("          ;;");
            output.println("        *)");
            output.printf("          echo \"Unknown %s: $2\"\n", nonPrefixed(optionName));
            output.println("          exit 1");
            output.println("          ;;");
            output.println("      esac");
            output.println("      shift 2");
            output.println("      ;;");
            return this;
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
            // write the stdin_value to stdout but add 4 spaces at the end of each line
            output.println("echo \"${stdin_value}\" | sed 's/$/    /'");
            return this;
        }

        @Override
        public ForeignExeMockWriter writeExitCode(int exitCode) {
            output.printf("exit %d\n", exitCode);
            return this;
        }
    }
}
