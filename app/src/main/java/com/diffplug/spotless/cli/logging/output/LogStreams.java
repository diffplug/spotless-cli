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

import java.io.OutputStream;
import java.io.PrintWriter;

final class LogStreams {

    static OutputStream asOutputStream(PrintWriter printWriter) {
        return new OutputStream() {
            @Override
            public void write(int b) {
                printWriter.write(b);
                printWriter.flush();
            }

            @Override
            public void write(byte[] b, int off, int len) {
                printWriter.write(new String(b, off, len));
                printWriter.flush();
            }
        };
    }
}
