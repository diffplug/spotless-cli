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
import java.util.function.Supplier;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import static com.diffplug.spotless.cli.logging.output.LogStreams.asOutputStream;

/**
 * This is a variant of the default {@link java.util.logging.ConsoleHandler} that does not log
 * to System.err but to a {@link PrintWriter} that is passed in retrieved from Picocli.
 *
 * This is useful for testing where we want to grab all output for a specific invocation.
 */
public class PicocliConsoleHandler extends StreamHandler {
    public PicocliConsoleHandler(Supplier<PrintWriter> out) {
        super(asOutputStream(out.get()), new SimpleFormatter());
    }

    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

    /**
     * Override {@code StreamHandler.close} to do a flush but not
     * to close the output stream.  That is, we do <b>not</b>
     * close {@code System.err}.
     */
    @Override
    public void close() {
        flush();
    }
}
