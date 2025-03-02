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
package com.diffplug.spotless.cli.execution;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.cli.core.SpotlessActionContext;
import com.diffplug.spotless.cli.core.SpotlessCommandLineStream;

public class ThreadLocalFormatterStepsFactory implements FormatterStepsSupplierFactory {

    @Override
    public FormatterStepsSupplier createFormatterStepsSupplier(
            SpotlessCommandLineStream commandLineStream, SpotlessActionContext context) {
        return new ThreadLocalFormatterStepsSupplier(commandLineStream, context);
    }

    static class ThreadLocalFormatterStepsSupplier implements FormatterStepsSupplier {
        private static final AtomicInteger threadCounter = new AtomicInteger(0);
        private final ThreadLocal<Integer> threadId = ThreadLocal.withInitial(threadCounter::getAndIncrement);
        private final ThreadLocal<List<FormatterStep>> threadLocalFormatterSteps = new ThreadLocal<>();
        private final SpotlessCommandLineStream commandLineStream;
        private final SpotlessActionContext context;

        ThreadLocalFormatterStepsSupplier(SpotlessCommandLineStream commandLineStream, SpotlessActionContext context) {
            this.commandLineStream = commandLineStream;
            this.context = context;
        }

        @Override
        public List<FormatterStep> getFormatterSteps() {
            if (threadLocalFormatterSteps.get() == null) {
                synchronized (this) {
                    if (threadLocalFormatterSteps.get() == null) {
                        SpotlessActionContext threadContext = context.deriveContext(threadId.get());
                        threadLocalFormatterSteps.set(commandLineStream
                                .formatterSteps()
                                .flatMap(step -> step.prepareFormatterSteps(threadContext).stream())
                                .toList());
                    }
                }
            }
            return threadLocalFormatterSteps.get();
        }
    }
}
