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

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Output {

    public static final String OUTPUT_LOGGER_NAME = "com.diffplug.spotless.cli.outputs.FORCED_OUTPUT";
    static final Logger CLI_OUTPUT = LoggerFactory.getLogger(OUTPUT_LOGGER_NAME);

    public static void write(String message, Object... args) {
        CLI_OUTPUT.info(message, args);
    }

    public static DefaultOrDetailOutputBuilder eitherDefault(Supplier<MessageWithArgs> defaultMessageSupplier) {
        return new DefaultOrDetailOutputBuilder(defaultMessageSupplier);
    }

    public static class DefaultOrDetailOutputBuilder implements ToOutputWriter {

        @NotNull private final Supplier<MessageWithArgs> defaultMessageWithArgs;

        private Supplier<MessageWithArgs> detailMessageWithArgs;

        public DefaultOrDetailOutputBuilder(@NotNull Supplier<MessageWithArgs> defaultMessageWithArgs) {
            this.defaultMessageWithArgs = defaultMessageWithArgs;
        }

        public ToOutputWriter orDetail(@NotNull Supplier<MessageWithArgs> detailMessageWithArgs) {
            this.detailMessageWithArgs = detailMessageWithArgs;
            return this;
        }

        @Override
        public void write() {
            if (CLI_OUTPUT.isDebugEnabled()) {
                MessageWithArgs messageWithArgs = defaultMessageWithArgs.get();
                CLI_OUTPUT.debug(messageWithArgs.message(), messageWithArgs.args());
                return;
            }
            MessageWithArgs messageWithArgs = detailMessageWithArgs.get();
            CLI_OUTPUT.info(messageWithArgs.message(), messageWithArgs.args());
        }
    }

    public interface ToOutputWriter {
        void write();
    }

    public record MessageWithArgs(String message, Object... args) {

        static MessageWithArgs create(String message, Object... args) {
            return new MessageWithArgs(message, args);
        }
    }
}
