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

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * "A lens is basically a getter/setter that can be used for deep updates of immutable data."
 * http://davids-code.blogspot.de/2014/02/immutable-domain-and-lenses-in-java-8.html
 *
 * @param <A> The object into whose property we want to view or update
 * @param <B> The object property which we want to view or update
 */
public class StreamLens<A, B> extends StreamView<A, B> {

    private final BiFunction<A, Stream<B>, A> fset;

    StreamLens(Function<A, Stream<B>> fget, BiFunction<A, Stream<B>, A> fset) {
        super(fget);
        this.fset = fset;
    }

    public static <A, B> StreamLens<A, B> of(
            Function<A, Stream<B>> fget, BiFunction<A, Stream<B>, A> fset) {
        return new StreamLens<>(fget, fset);
    }

    public A set(A target, Stream<B> value) {
        return target == null ? null : fset.apply(target, value);
    }

    public A set(A target, B value) {
        return target == null ? null : fset.apply(target, Stream.of(value));
    }

    public A append(A target, B value) {
        return target == null
                ? null
                : fset.apply(target, Stream.concat(getStream(target), Stream.of(value)));
    }

    public A prepend(A target, B value) {
        return target == null
                ? null
                : fset.apply(target, Stream.concat(Stream.of(value), getStream(target)));
    }

    public A modify(A target, Function<B, B> modifier) {
        return set(target, getStream(target).map(modifier));
    }

    public <C> StreamLens<A, C> andThen(Lens<B, C> that) {
        return StreamLens.of(
                (A a) -> getStream(a).map(that::get),
                (A a, Stream<C> cStream) ->
                        set(a, getStream(a).flatMap(b -> cStream.map(c -> that.set(b, c)))));
    }

    public <C> StreamLens<A, Optional<C>> andThen(OptionalLens<B, C> that) {
        return StreamLens.of(
                (A a) -> getStream(a).map(that::getOptional),
                (A a, Stream<Optional<C>> maybeCStream) ->
                        set(a, getStream(a).flatMap(b -> maybeCStream.map(c -> that.set(b, c)))));
    }

    public <C> StreamLens<A, C> andThen(StreamLens<B, C> that) {
        return StreamLens.of(
                (A a) -> getStream(a).flatMap(that::getStream),
                (A a, Stream<C> cStream) -> set(a, getStream(a).map(b -> that.set(b, cStream))));
    }

    public <C> StreamLens<C, B> compose(Lens<C, A> that) {
        return that.andThen(this);
    }

    public <C> StreamLens<C, B> compose(OptionalLens<C, A> that) {
        return that.andThen(this);
    }

    public <C> StreamLens<C, B> compose(StreamLens<C, A> that) {
        return that.andThen(this);
    }
}
