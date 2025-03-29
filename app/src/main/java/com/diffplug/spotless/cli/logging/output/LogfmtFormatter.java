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
package com.diffplug.spotless.cli.logging.output;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

public class LogfmtFormatter extends Formatter {

    private static final String TIMESTAMP = "timestamp";
    private static final String LEVEL = "level";
    private static final String MESSAGE = "message";
    private static final String SOURCE_LOGGER_NAME = "source.logger.name";
    private static final String SOURCE_CLASS = "source.class";
    private static final String SOURCE_METHOD = "source.method";
    private static final String THREAD_ID = "thread.id";
    private static final String SEQUENCE_NO = "sequenceNo";
    private static final String THROWN = "thrown";

    @NotNull private final KeyDecorator keyDecorator;

    public LogfmtFormatter(@NotNull KeyDecorator keyDecorator) {
        this.keyDecorator = keyDecorator;
    }

    public LogfmtFormatter() {
        this(KeyDecorator.NONE);
    }

    @Override
    public String format(LogRecord record) {

        Map<String, String> attributes = new LinkedHashMap<>();
        intoMap(record, attributes);
        String logLine = toString(attributes);
        if (logLine != null && !logLine.isEmpty()) {
            return logLine + "\n";
        }
        return logLine;
    }

    private void intoMap(LogRecord record, Map<String, String> attributes) {
        attributes.put(TIMESTAMP, record.getInstant().toString());
        attributes.put(LEVEL, record.getLevel().getName());
        attributes.put(MESSAGE, formatMessage(record));
        attributes.put(SOURCE_LOGGER_NAME, record.getLoggerName());
        attributes.put(SOURCE_CLASS, record.getSourceClassName());
        attributes.put(SOURCE_METHOD, record.getSourceMethodName());
        attributes.put(THREAD_ID, Long.toHexString(record.getLongThreadID()));
        attributes.put(SEQUENCE_NO, Long.toString(record.getSequenceNumber()));
        attributes.put(THROWN, formatThrown(record.getThrown()));
    }

    private String formatThrown(Throwable thrown) {
        if (thrown == null) {
            return null;
        }

        try (StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw)) {
            thrown.printStackTrace(pw);
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String toString(Map<String, String> attributes) {
        return attributes.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> decorateKey(entry.getKey()) + "=" + quoteIfNeeded(escapeValue(entry.getValue())))
                .collect(Collectors.joining(" "));
    }

    private String decorateKey(String key) {
        return keyDecorator.decorateKey(key);
    }

    private String escapeValue(String value) {
        return value.replace("\\", "\\\\")
                .replace("=", "\\=")
                .replace("\"", "\\\"")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\b", "\\b")
                .replace("\f", "\\f");
    }

    private String quoteIfNeeded(String value) {
        if ("".equals(value)) {
            return "\"\"";
        }
        if (!needsQuoting(value)) {
            return value;
        }
        return quote(value);
    }

    /**
     * It needs quoting if it contains any char outside a-z, A-Z, 0-9, or - or .
     *
     * @param value the value to check
     * @return true if the value needs quoting, false otherwise
     */
    private boolean needsQuoting(String value) {
        return value.chars().anyMatch(c -> !Character.isLetterOrDigit(c) && c != '-' && c != '.');
    }

    private String quote(String value) {
        return "\"" + value + "\"";
    }

    private static final class Colors {
        private Colors() {}

        public static final String RESET = "\u001B[0m";

        // Foreground colors
        public static final String FG_BLACK = "\u001B[30m";
        public static final String FG_RED = "\u001B[31m";
        public static final String FG_GREEN = "\u001B[32m";
        public static final String FG_YELLOW = "\u001B[33m";
        public static final String FG_BLUE = "\u001B[34m";
        public static final String FG_PURPLE = "\u001B[35m";
        public static final String FG_CYAN = "\u001B[36m";
        public static final String FG_WHITE = "\u001B[37m";

        // Background colors
        public static final String BG_BLACK = "\u001B[40m";
        public static final String BG_RED = "\u001B[41m";
        public static final String BG_GREEN = "\u001B[42m";
        public static final String BG_YELLOW = "\u001B[43m";
        public static final String BG_BLUE = "\u001B[44m";
        public static final String BG_PURPLE = "\u001B[45m";
        public static final String BG_CYAN = "\u001B[46m";
        public static final String BG_WHITE = "\u001B[47m";
    }

    public enum KeyDecorator {
        NONE {
            @Override
            String decorateKey(String key) {
                return key;
            }
        },
        SINGLE_COLOR {
            @Override
            String decorateKey(String key) {
                return decorateKey(key, Colors.FG_CYAN);
            }
        },
        MULTI_COLOR {
            @Override
            String decorateKey(String key) {
                return switch (key) {
                    case TIMESTAMP -> decorateKey(key, Colors.FG_GREEN);
                    case LEVEL -> decorateKey(key, Colors.FG_YELLOW);
                    case MESSAGE -> decorateKey(key, Colors.FG_CYAN);
                    case SOURCE_LOGGER_NAME, SOURCE_CLASS, SOURCE_METHOD -> decorateKey(key, Colors.FG_BLUE);
                    case THREAD_ID -> decorateKey(key, Colors.FG_RED);
                    case SEQUENCE_NO -> decorateKey(key, Colors.FG_PURPLE);
                    case THROWN -> decorateKey(key, Colors.FG_WHITE, Colors.BG_RED);
                    default -> throw new IllegalArgumentException("Unknown key: " + key);
                };
            }
        };

        abstract String decorateKey(String key);

        protected String decorateKey(String key, String... colors) {
            return String.join(" ", colors) + key + Colors.RESET;
        }
    }
}
