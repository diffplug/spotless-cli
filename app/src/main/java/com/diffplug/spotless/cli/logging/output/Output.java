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

import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Output {

    private static final Logger LOGGER = LoggerFactory.getLogger(Output.class);

    private static volatile Level _level = Level.INFO;

    public static void setLevel(Level level) {
        Output._level = level;
    }

    public static void output(@NotNull String message, Object... args) {
        Objects.requireNonNull(message);
        System.err.printf(slf4jMessageToPrintfMessage(message), args);
        LOGGER.info(message, args);
    }

    private static @NotNull String slf4jMessageToPrintfMessage(@NotNull String message) {
        Objects.requireNonNull(message);
        return message.replace("{}", "%s") + "%n";
    }

    public static DefaultOrDetailOutputBuilder eitherDefault(
            @NotNull Supplier<MessageWithArgs> defaultMessageSupplier) {
        Objects.requireNonNull(defaultMessageSupplier);
        return new DefaultOrDetailOutputBuilder(defaultMessageSupplier);
    }

    public static class DefaultOrDetailOutputBuilder {

        @NotNull private final Supplier<MessageWithArgs> defaultMessageWithArgs;

        private Supplier<MessageWithArgs> detailMessageWithArgs;

        public DefaultOrDetailOutputBuilder(@NotNull Supplier<MessageWithArgs> defaultMessageWithArgs) {
            this.defaultMessageWithArgs = defaultMessageWithArgs;
        }

        public void orDetail(@NotNull Supplier<MessageWithArgs> detailMessageWithArgs) {
            Objects.requireNonNull(detailMessageWithArgs);
            this.detailMessageWithArgs = detailMessageWithArgs;
            write();
        }

        private void write() {
            if (_level.intValue() <= Level.FINE.intValue()) {
                MessageWithArgs messageWithArgs = detailMessageWithArgs.get();
                System.err.printf(slf4jMessageToPrintfMessage(messageWithArgs.message()), messageWithArgs.args());
                LOGGER.debug(messageWithArgs.message(), messageWithArgs.args());
                return;
            }
            MessageWithArgs messageWithArgs = defaultMessageWithArgs.get();
            if (_level.intValue() <= Level.INFO.intValue()) {
                System.err.printf(slf4jMessageToPrintfMessage(messageWithArgs.message()), messageWithArgs.args());
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
