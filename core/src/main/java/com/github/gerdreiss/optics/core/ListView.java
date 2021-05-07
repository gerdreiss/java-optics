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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListView<A, B> implements Function<A, List<B>> {

    private final Function<A, List<B>> fget;

    ListView(Function<A, List<B>> fget) {
        this.fget = fget;
    }

    public static <A, B> ListView<A, B> of(final Function<A, List<B>> fget) {
        return new ListView<>(fget);
    }

    @Override
    public List<B> apply(A a) {
        return getList(a);
    }

    public List<B> getList(A a) {
        return a == null ? Collections.emptyList() : fget.apply(a);
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

    public List<B> find(A a, Predicate<B> predicate) {
        return a == null
                ? Collections.emptyList()
                : fget.apply(a).stream().filter(predicate).collect(toList());
    }

    public Optional<B> findFirst(A a, Predicate<B> predicate) {
        return find(a, predicate).stream().findFirst();
    }

    public <C> ListView<A, C> andThen(final View<B, C> that) {
        return ListView.of((A a) -> getList(a).stream().map(that::get).collect(toList()));
    }

    public <C> ListView<A, Optional<C>> andThen(final OptionalView<B, C> that) {
        return ListView.of((A a) -> getList(a).stream().map(that::getOptional).collect(toList()));
    }

    public <C> ListView<A, C> andThen(final ListView<B, C> that) {
        return ListView.of((A a) -> getList(a).stream()
                .flatMap(b -> that.getList(b).stream())
                .collect(toList()));
    }

    public <C> ListView<C, B> compose(final View<C, A> that) {
        return that.andThen(this);
    }

    public <C> ListView<C, B> compose(final OptionalView<C, A> that) {
        return that.andThen(this);
    }

    public <C> ListView<C, B> compose(final ListView<C, A> that) {
        return that.andThen(this);
    }
}
