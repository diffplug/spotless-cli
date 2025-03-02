package com.diffplug.spotless.cli.logging.output;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A simple formatter that just returns the message of the log record (without any additional information).
 */
class PlainMessageFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return formatMessage(record);
    }
}
