package com.diffplug.spotless.cli.logging.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public final class Output {

    public static final String OUTPUT_LOGGER_NAME = "com.diffplug.spotless.cli.outputs.FORCED_OUTPUT";
    static final Logger CLI_OUTPUT = LoggerFactory.getLogger(OUTPUT_LOGGER_NAME);

    public static void out(String message, Object... args) {
        CLI_OUTPUT.info(message, args);
    }

    public static void out(Supplier<MessageWithArgs> defaultMessageSupplier, Supplier<MessageWithArgs> detailMessageSupplier) {
        if (CLI_OUTPUT.isDebugEnabled()) {
            MessageWithArgs detailMessage = detailMessageSupplier.get();
            CLI_OUTPUT.debug(detailMessage.message(), detailMessage.args());
            return;
        }
        MessageWithArgs defaultMessage = defaultMessageSupplier.get();
        CLI_OUTPUT.info(defaultMessage.message(), defaultMessage.args());
    }


    public record MessageWithArgs(String message, Object... args) {
    }
}

