/*
 * Copyright 2024 DiffPlug
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
package com.diffplug.spotless.cli.steps;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.diffplug.common.base.Suppliers;

public class OptionDefaultUse<T> {

    @Nullable private final T obj;

    private OptionDefaultUse(@Nullable T obj) {
        this.obj = obj;
    }

    public static <T> OptionDefaultUse<T> use(@Nullable T obj) {
        return new OptionDefaultUse<>(obj);
    }

    public T orIfNullGet(Supplier<T> supplier) {
        return obj != null ? obj : supplier.get();
    }

    public T orIfNull(T other) {
        return orIfNullGet(Suppliers.ofInstance(other));
    }
}
