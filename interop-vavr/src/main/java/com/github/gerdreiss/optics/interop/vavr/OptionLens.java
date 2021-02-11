/*
 * Copyright 2021 DiffPlug
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
package com.github.gerdreiss.optics.interop.vavr;

import com.github.gerdreiss.optics.core.Lens;
import io.vavr.control.Option;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * "A lens is basically a getter/setter that can be used for deep updates of immutable data."
 * http://davids-code.blogspot.de/2014/02/immutable-domain-and-lenses-in-java-8.html
 *
 * @param <A> The object into whose property we want to view or update
 * @param <B> The object property which we want to view or update
 */
public class OptionLens<A, B> extends OptionView<A, B> {

    private final BiFunction<A, Option<B>, A> fset;

    OptionLens(Function<A, Option<B>> fget, BiFunction<A, Option<B>, A> fset) {
        super(fget);
        this.fset = fset;
    }

    public static <A, B> OptionLens<A, B> of(
            Function<A, Option<B>> fget, BiFunction<A, Option<B>, A> fset) {
        return new OptionLens<>(fget, fset);
    }

    public A set(A target, Option<B> value) {
        return target == null ? null : fset.apply(target, value);
    }

    public A set(A target, B value) {
        return target == null ? null : fset.apply(target, Option.of(value));
    }

    public Function<Option<B>, A> set(A target) {
        return (Option<B> value) -> set(target, value);
    }

    public A modify(A target, Function<B, B> modifier) {
        return set(target, getOption(target).map(modifier));
    }

    public <C> OptionLens<A, C> andThen(Lens<B, C> that) {
        return OptionLens.of(
                (A a) -> getOption(a).map(that::get),
                (A a, Option<C> maybeC) ->
                        set(a, getOption(a).flatMap(b -> maybeC.map(c -> that.set(b, c)))));
    }

    public <C> OptionLens<A, C> andThen(OptionLens<B, C> that) {
        return OptionLens.of(
                (A a) -> getOption(a).flatMap(that::getOption),
                (A a, Option<C> maybeC) -> set(a, getOption(a).map(b -> that.set(b, maybeC))));
    }

    public <C> OptionLens<C, B> compose(OptionLens<C, A> that) {
        return that.andThen(this);
    }
}
