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

import java.util.Collections;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * "A lens is basically a getter/setter that can be used for deep updates of immutable data."
 * http://davids-code.blogspot.de/2014/02/immutable-domain-and-lenses-in-java-8.html
 *
 * @param <A> The object into whose property we want to view
 * @param <B> The object property which we want to view
 */
public class OptionalView<A, B> implements Function<A, Optional<B>> {

    private final Function<A, Optional<B>> fget;

    OptionalView(Function<A, Optional<B>> fget) {
        this.fget = fget;
    }

    public static <A, B> OptionalView<A, B> of(final Function<A, Optional<B>> fget) {
        return new OptionalView<>(fget);
    }

    @Override
    public Optional<B> apply(A a) {
        return getOptional(a);
    }

    public Optional<B> getOptional(A a) {
        return a == null ? Optional.empty() : fget.apply(a);
    }

    public <C> OptionalView<A, C> andThen(final View<B, C> that) {
        return OptionalView.of((A a) -> getOptional(a).map(that::get));
    }

    public <C> OptionalView<A, C> andThen(final OptionalView<B, C> that) {
        return OptionalView.of((A a) -> getOptional(a).flatMap(that::getOptional));
    }

    public <C> StreamView<A, C> andThen(final StreamView<B, C> that) {
        return StreamView.of((A a) -> getOptional(a).map(that::getStream).orElse(Stream.empty()));
    }

    public <C> CollectionView<A, C> andThen(final CollectionView<B, C> that) {
        return CollectionView.of((A a) -> getOptional(a).map(that::getCollection).orElse(Collections.emptyList()));
    }

    public <C> ListView<A, C> andThen(final ListView<B, C> that) {
        return ListView.of((A a) -> getOptional(a).map(that::getList).orElse(Collections.emptyList()));
    }

    public <C> SetView<A, C> andThen(final SetView<B, C> that) {
        return SetView.of((A a) -> getOptional(a).map(that::getSet).orElse(Collections.emptySet()));
    }

    public <C> QueueView<A, C> andThen(final QueueView<B, C> that) {
        return QueueView.of(
                (A a) -> getOptional(a).map(that::getQueue).orElse(new PriorityQueue<>()));
    }

    public <C> OptionalView<C, B> compose(final View<C, A> that) {
        return that.andThen(this);
    }

    public <C> OptionalView<C, B> compose(final OptionalView<C, A> that) {
        return that.andThen(this);
    }

    public <C> StreamView<C, Optional<B>> compose(final StreamView<C, A> that) {
        return that.andThen(this);
    }

    public <C> CollectionView<C, Optional<B>> compose(final CollectionView<C, A> that) {
        return that.andThen(this);
    }

    public <C> ListView<C, Optional<B>> compose(final ListView<C, A> that) {
        return that.andThen(this);
    }

    public <C> SetView<C, Optional<B>> compose(final SetView<C, A> that) {
        return that.andThen(this);
    }
}
