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

import java.util.function.Function;

/**
 * "A lens is basically a getter/setter that can be used for deep updates of immutable data."
 * http://davids-code.blogspot.de/2014/02/immutable-domain-and-lenses-in-java-8.html
 *
 * @param <A> The object into whose property we want to view
 * @param <B> The object property which we want to view
 */
public class View<A, B> implements Function<A, B> {

    private final Function<A, B> fget;

    View(Function<A, B> fget) {
        this.fget = fget;
    }

    public static <A, B> View<A, B> of(Function<A, B> fget) {
        return new View<>(fget);
    }

    @Override
    public B apply(A a) {
        return get(a);
    }

    public B get(A a) {
        return a == null ? null : fget.apply(a);
    }

    public <C> View<A, C> andThen(View<B, C> that) {
        return new View<>((A a) -> that.get(get(a)));
    }

    public <C> OptionalView<A, C> andThen(OptionalView<B, C> that) {
        return new OptionalView<>((A a) -> that.getOptional(get(a)));
    }

    public <C> StreamView<A, C> andThen(StreamView<B, C> that) {
        return StreamView.of((A a) -> that.getStream(get(a)));
    }

    public <C> CollectionView<A, C> andThen(CollectionView<B, C> that) {
        return CollectionView.of((A a) -> that.getCollection(get(a)));
    }

    public <K, V> MapView<A, K, V> andThen(MapView<B, K, V> that) {
        return MapView.of((A a) -> that.getMap(get(a)));
    }

    public <C> View<C, B> compose(final View<C, A> that) {
        return that.andThen(this);
    }

    public <C> OptionalView<C, B> compose(final OptionalView<C, A> that) {
        return that.andThen(this);
    }

    public <C> StreamView<C, B> compose(final StreamView<C, A> that) {
        return that.andThen(this);
    }

    public <C> CollectionView<C, B> compose(final CollectionView<C, A> that) {
        return that.andThen(this);
    }

    public <K, V> MapView<V, K, B> compose(final MapView<V, K, A> that) {
        return that.andThen(this);
    }
}
