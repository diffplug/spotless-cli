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

import java.nio.charset.Charset;
import java.util.LinkedHashSet;

import com.diffplug.spotless.Formatter;
import com.diffplug.spotless.LineEnding;
import com.diffplug.spotless.cli.execution.FormatterStepsSupplier;

public class ThreadLocalFormatterFactory implements FormatterFactory {
    private final LineEnding.Policy policy;
    private final Charset encoding;
    private final FormatterStepsSupplier formatterSteps;

    private final ThreadLocal<Formatter> threadLocalFormatter = new ThreadLocal<>();
    private final java.util.Set<Formatter> createdFormatters = new LinkedHashSet<>();

    public ThreadLocalFormatterFactory(
            LineEnding.Policy policy, Charset encoding, FormatterStepsSupplier formatterSteps) {
        this.policy = policy;
        this.encoding = encoding;
        this.formatterSteps = formatterSteps;
    }

    @Override
    public Formatter createFormatter() {
        if (threadLocalFormatter.get() == null) {
            synchronized (this) {
                if (threadLocalFormatter.get() == null) {
                    System.out.println("ThreadLocalFormatterFactory.createFormatter() thread.name: "
                            + Thread.currentThread().getName());
                    Formatter formatter = Formatter.builder()
                            .lineEndingsPolicy(policy)
                            .encoding(encoding)
                            .steps(formatterSteps.getFormatterSteps())
                            .build();
                    createdFormatters.add(formatter);
                    threadLocalFormatter.set(formatter);
                }
            }
        }
        return threadLocalFormatter.get();
    }

    @Override
    public void close() {
        createdFormatters.forEach(Formatter::close);
    }
}
