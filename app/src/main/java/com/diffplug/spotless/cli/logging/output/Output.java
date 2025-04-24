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

import java.io.PrintWriter;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Output {

    private static final Logger LOGGER = LoggerFactory.getLogger(Output.class);

    private final Level level;

    private final PrintWriter stdErr;

    public Output() {
        this(Level.INFO, new PrintWriter(System.err, true));
    }

    public Output(@NotNull Level level, @NotNull PrintWriter stdErr) {
        this.level = Objects.requireNonNull(level);
        this.stdErr = Objects.requireNonNull(stdErr);
    }

    public Output with(@NotNull PrintWriter writer) {
        Objects.requireNonNull(writer);
        return new Output(this.level, writer);
    }

    public Output with(@NotNull Level newLevel) {
        Objects.requireNonNull(newLevel);
        return new Output(newLevel, this.stdErr);
    }

    public void output(@NotNull String message, Object... args) {
        Objects.requireNonNull(message);
        stdErr.printf(slf4jMessageToPrintfMessage(message), args);
        LOGGER.info(message, args);
    }

    private static @NotNull String slf4jMessageToPrintfMessage(@NotNull String message) {
        Objects.requireNonNull(message);
        return message.replace("{}", "%s") + "%n";
    }

    public DefaultOrDetailOutputBuilder eitherDefault(@NotNull Supplier<MessageWithArgs> defaultMessageSupplier) {
        Objects.requireNonNull(defaultMessageSupplier);
        return new DefaultOrDetailOutputBuilder(level, stdErr, defaultMessageSupplier);
    }

    public static class DefaultOrDetailOutputBuilder {

        @NotNull private final Level level;

        @NotNull private final PrintWriter out;

        @NotNull private final Supplier<MessageWithArgs> defaultMessageWithArgs;

        private Supplier<MessageWithArgs> detailMessageWithArgs;

        public DefaultOrDetailOutputBuilder(
                @NotNull Level level,
                @NotNull PrintWriter out,
                @NotNull Supplier<MessageWithArgs> defaultMessageWithArgs) {
            this.level = Objects.requireNonNull(level);
            this.out = Objects.requireNonNull(out);
            this.defaultMessageWithArgs = Objects.requireNonNull(defaultMessageWithArgs);
        }

        public void orDetail(@NotNull Supplier<MessageWithArgs> detailMessageWithArgs) {
            Objects.requireNonNull(detailMessageWithArgs);
            this.detailMessageWithArgs = detailMessageWithArgs;
            write();
        }

        private void write() {
            if (level.intValue() <= Level.FINE.intValue()) {
                MessageWithArgs messageWithArgs = detailMessageWithArgs.get();
                out.printf(slf4jMessageToPrintfMessage(messageWithArgs.message()), messageWithArgs.args());
                LOGGER.debug(messageWithArgs.message(), messageWithArgs.args());
                return;
            }
            MessageWithArgs messageWithArgs = defaultMessageWithArgs.get();
            if (level.intValue() <= Level.INFO.intValue()) {
                out.printf(slf4jMessageToPrintfMessage(messageWithArgs.message()), messageWithArgs.args());
            }
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(messageWithArgs.message(), messageWithArgs.args());
            }
        }
    }

    public record MessageWithArgs(String message, Object... args) {
        static MessageWithArgs create(String message, Object... args) {
            return new MessageWithArgs(message, args);
        }
    }
}
