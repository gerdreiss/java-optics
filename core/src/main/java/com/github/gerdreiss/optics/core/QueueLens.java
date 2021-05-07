package com.github.gerdreiss.optics.core;

import static com.github.gerdreiss.optics.core.util.CollectionUtils.zip;
import static java.util.stream.Collectors.toCollection;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Function;

public class QueueLens<A, B> extends QueueView<A, B> {

    private final BiFunction<A, Queue<B>, A> fQueue;

    QueueLens(Function<A, Queue<B>> fget, BiFunction<A, Queue<B>, A> fQueue) {
        super(fget);
        this.fQueue = fQueue;
    }

    public static <A, B> QueueLens<A, B> of(
            Function<A, Queue<B>> fget, BiFunction<A, Queue<B>, A> fQueue) {
        return new QueueLens<>(fget, fQueue);
    }

    public A set(A target, Queue<B> values) {
        return target == null ? null : fQueue.apply(target, values);
    }

    public Function<Queue<B>, A> set(A target) {
        return (Queue<B> value) -> set(target, value);
    }

    public A modify(A target, Function<B, B> modifier) {
        return set(target, getQueue(target).stream().map(modifier).collect(toCollection(PriorityQueue::new)));
    }

    public <C> QueueLens<A, C> andThen(Lens<B, C> that) {
        return QueueLens.of(
                (A a) -> getQueue(a).stream().map(that::get).collect(toCollection(PriorityQueue::new)),
                (A a, Queue<C> cs) -> set(a, zip(getQueue(a), cs).stream()
                        .map(entry -> that.set(entry.getKey(), entry.getValue()))
                        .collect(toCollection(PriorityQueue::new)))
        );
    }

    public <C> QueueLens<A, C> andThen(QueueLens<B, C> that) {
        return QueueLens.of(
                (A a) -> getQueue(a).stream()
                        .flatMap(b -> that.getQueue(b).stream()).collect(toCollection(PriorityQueue::new)),
                (A a, Queue<C> cs) -> set(a, getQueue(a).stream()
                        .map(b -> that.set(b, cs))
                        .collect(toCollection(PriorityQueue::new))));
    }

    public <C> QueueLens<C, B> compose(Lens<C, A> that) {
        return that.andThen(this);
    }

    public <C> QueueLens<C, B> compose(QueueLens<C, A> that) {
        return that.andThen(this);
    }

}
