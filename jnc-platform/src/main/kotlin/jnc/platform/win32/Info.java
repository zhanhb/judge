/*
 * Copyright 2018 ZJNU ACM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jnc.platform.win32;

import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import jnc.foreign.Struct;
import kotlin.jvm.internal.Intrinsics;

/**
 * @author zhanhb
 */
@ParametersAreNonnullByDefault
public final class Info {

    @Nonnull
    public static <T extends Struct> Info of(Supplier<T> supplier, Function<T, Struct> anySizeArrayElement) {
        Intrinsics.checkParameterIsNotNull(supplier, "supplier");
        Intrinsics.checkParameterIsNotNull(anySizeArrayElement, "anySizeArrayElement");
        T struct = supplier.get();
        Struct apply = anySizeArrayElement.apply(struct);
        return new Info(struct.size(), apply.size());
    }

    private final int a;
    private final int e;

    private Info(int a, int e) {
        this.a = a;
        this.e = e;
    }

    public final int toCount(int size) {
        return Math.max((size - this.a + this.e - 1) / this.e, 0) + 1;
    }

}