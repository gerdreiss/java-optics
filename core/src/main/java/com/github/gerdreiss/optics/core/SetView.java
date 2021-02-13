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
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SetView<A, B> implements Function<A, Set<B>> {

    private final Function<A, Set<B>> fget;

    SetView(Function<A, Set<B>> fget) {
        this.fget = fget;
    }

    public static <A, B> SetView<A, B> of(final Function<A, Set<B>> fget) {
        return new SetView<>(fget);
    }

    @Override
    public Set<B> apply(A a) {
        return getSet(a);
    }

    public Set<B> getSet(A a) {
        return a == null ? Collections.emptySet() : fget.apply(a);
    }

    public Optional<B> getFirst(A a) {
        return a == null ? Optional.empty() : fget.apply(a).stream().findFirst();
    }

    public Optional<B> getLast(A a) {
        return a == null
                ? Optional.empty()
                : fget.apply(a).stream().reduce((first, second) -> second);
    }

    public Optional<B> getAt(A a, int n) {
        return a == null ? Optional.empty() : fget.apply(a).stream().skip(n).findFirst();
    }

    public Set<B> find(A a, Predicate<B> predicate) {
        return a == null
                ? Collections.emptySet()
                : fget.apply(a).stream().filter(predicate).collect(Collectors.toSet());
    }

    public Optional<B> findFirst(A a, Predicate<B> predicate) {
        return find(a, predicate).stream().findFirst();
    }

    public <C> SetView<A, C> andThen(final View<B, C> that) {
        return SetView.of((A a) -> getSet(a).stream().map(that::get).collect(Collectors.toSet()));
    }

    public <C> SetView<A, Optional<C>> andThen(final OptionalView<B, C> that) {
        return SetView.of(
                (A a) -> getSet(a).stream().map(that::getOptional).collect(Collectors.toSet()));
    }

    public <C> SetView<A, C> andThen(final SetView<B, C> that) {
        return SetView.of(
                (A a) ->
                        getSet(a).stream()
                                .flatMap(b -> that.getSet(b).stream())
                                .collect(Collectors.toSet()));
    }

    public <C> SetView<C, B> compose(final View<C, A> that) {
        return that.andThen(this);
    }

    public <C> SetView<C, B> compose(final OptionalView<C, A> that) {
        return that.andThen(this);
    }

    public <C> SetView<C, B> compose(final SetView<C, A> that) {
        return that.andThen(this);
    }
}
