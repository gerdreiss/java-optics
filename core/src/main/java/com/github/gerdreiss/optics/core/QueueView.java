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

import static java.util.stream.Collectors.toCollection;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Predicate;

public class QueueView<A, B> implements Function<A, Queue<B>> {

    private final Function<A, Queue<B>> fget;

    QueueView(Function<A, Queue<B>> fget) {
        this.fget = fget;
    }

    public static <A, B> QueueView<A, B> of(final Function<A, Queue<B>> fget) {
        return new QueueView<>(fget);
    }

    @Override
    public Queue<B> apply(A a) {
        return getQueue(a);
    }

    public Queue<B> getQueue(A a) {
        return a == null ? new PriorityQueue<>() : fget.apply(a);
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

    public Queue<B> findAll(A a, Predicate<B> predicate) {
        return a == null
                ? new PriorityQueue<>()
                : fget.apply(a).stream()
                        .filter(predicate)
                        .collect(toCollection(PriorityQueue::new));
    }

    public Optional<B> findFirst(A a, Predicate<B> predicate) {
        return findAll(a, predicate).stream().findFirst();
    }

    public <C> QueueView<A, C> andThen(final View<B, C> that) {
        return QueueView.of((A a) -> getQueue(a).stream()
                .map(that::get)
                .collect(toCollection(PriorityQueue::new)));
    }

    public <C> QueueView<A, Optional<C>> andThen(final OptionalView<B, C> that) {
        return QueueView.of((A a) -> getQueue(a).stream()
                .map(that::getOptional)
                .collect(toCollection(PriorityQueue::new)));
    }

    public <C> QueueView<A, C> andThen(final QueueView<B, C> that) {
        return QueueView.of((A a) -> getQueue(a).stream()
                .flatMap(b -> that.getQueue(b).stream())
                .collect(toCollection(PriorityQueue::new)));
    }

    public <C> QueueView<C, B> compose(final View<C, A> that) {
        return that.andThen(this);
    }

    public <C> QueueView<C, B> compose(final OptionalView<C, A> that) {
        return that.andThen(this);
    }

    public <C> QueueView<C, B> compose(final QueueView<C, A> that) {
        return that.andThen(this);
    }
}
