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

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CollectionView<A, B> implements Function<A, Collection<B>> {

    private final Function<A, Collection<B>> fget;

    CollectionView(Function<A, Collection<B>> fget) {
        this.fget = fget;
    }

    public static <A, B> CollectionView<A, B> of(final Function<A, Collection<B>> fget) {
        return new CollectionView<>(fget);
    }

    @Override
    public Collection<B> apply(A a) {
        return getCollection(a);
    }

    public Collection<B> getCollection(A a) {
        return a == null ? Collections.emptyList() : fget.apply(a);
    }

    public Stream<B> getStream(A a) {
        return a == null ? Stream.empty() : fget.apply(a).stream();
    }

    public Optional<B> getFirst(A a) {
        return getStream(a).findFirst();
    }

    public Optional<B> getLast(A a) {
        return getStream(a).reduce((first, second) -> second);
    }

    public Optional<B> getAt(A a, int n) {
        return getStream(a).skip(n).findFirst();
    }

    public Collection<B> findAll(A a, Predicate<B> predicate) {
        return getStream(a).filter(predicate).collect(toList());
    }

    public Optional<B> findFirst(A a, Predicate<B> predicate) {
        return findAll(a, predicate).stream().findFirst();
    }

    public <C> CollectionView<A, C> andThen(final View<B, C> that) {
        return CollectionView.of((A a) -> getCollection(a).stream().map(that::get).collect(toList()));
    }

    public <C> CollectionView<A, Optional<C>> andThen(final OptionalView<B, C> that) {
        return CollectionView.of((A a) -> getCollection(a).stream()
                .map(that::getOptional)
                .collect(toList()));
    }

    public <C> CollectionView<A, C> andThen(final CollectionView<B, C> that) {
        return CollectionView.of((A a) -> getCollection(a).stream()
                .flatMap(b -> that.getCollection(b).stream())
                .collect(toList()));
    }

    public <C> CollectionView<C, B> compose(final View<C, A> that) {
        return that.andThen(this);
    }

    public <C> CollectionView<C, B> compose(final OptionalView<C, A> that) {
        return that.andThen(this);
    }

    public <C> CollectionView<C, B> compose(final CollectionView<C, A> that) {
        return that.andThen(this);
    }
}
