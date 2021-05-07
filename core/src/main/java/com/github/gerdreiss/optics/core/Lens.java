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
package com.github.gerdreiss.optics.core;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * "A lens is basically a getter/setter that can be used for deep updates of immutable data."
 * http://davids-code.blogspot.de/2014/02/immutable-domain-and-lenses-in-java-8.html
 *
 * @param <A> The object into whose property we want to view or update
 * @param <B> The object property which we want to view or update
 */
public class Lens<A, B> extends View<A, B> {

    private final BiFunction<A, B, A> fset;

    Lens(Function<A, B> fget, BiFunction<A, B, A> fset) {
        super(fget);
        this.fset = fset;
    }

    public static <A, B> Lens<A, B> of(Function<A, B> fget, BiFunction<A, B, A> fset) {
        return new Lens<>(fget, fset);
    }

    public A set(A target, B value) {
        return target == null ? null : fset.apply(target, value);
    }

    public Function<B, A> set(A target) {
        return (B value) -> set(target, value);
    }

    public A modify(A target, Function<B, B> modifier) {
        return set(target, modifier.apply(get(target)));
    }

    public <C> Lens<A, C> andThen(Lens<B, C> that) {
        return Lens.of((A a) -> that.get(get(a)), (A a, C c) -> set(a, that.set(get(a), c)));
    }

    public <C> OptionalLens<A, C> andThen(OptionalLens<B, C> that) {
        return OptionalLens.of(
                (A a) -> that.getOptional(get(a)),
                (A a, Optional<C> maybeC) -> set(a, that.set(get(a), maybeC)));
    }

    public <C> ListLens<A, C> andThen(ListLens<B, C> that) {
        return ListLens.of(
                (A a) -> that.getList(get(a)),
                (A a, List<C> cs) -> set(a, that.set(get(a), cs)));
    }

    public <C> SetLens<A, C> andThen(SetLens<B, C> that) {
        return SetLens.of(
                (A a) -> that.getSet(get(a)),
                (A a, Set<C> cs) -> set(a, that.set(get(a), cs)));
    }

    public <C> Lens<C, B> compose(Lens<C, A> that) {
        return that.andThen(this);
    }

    public <C> OptionalLens<C, B> compose(OptionalLens<C, A> that) {
        return that.andThen(this);
    }

    public <C> ListLens<C, B> compose(ListLens<C, A> that) {
        return that.andThen(this);
    }

    public <C> SetLens<C, B> compose(SetLens<C, A> that) {
        return that.andThen(this);
    }

}
