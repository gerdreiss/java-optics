package com.github.gerdreiss.optics;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * "A lens is basically a getter/setter that can be used for deep updates of immutable data."
 * http://davids-code.blogspot.de/2014/02/immutable-domain-and-lenses-in-java-8.html
 *
 * @param <A> The object into whose property we want to view
 * @param <B> The object property which we want to view
 */
public class StreamView<A, B> implements Function<A, Stream<B>> {

    private final Function<A, Stream<B>> fget;

    StreamView(Function<A, Stream<B>> fget) {
        this.fget = fget;
    }

    public static <A, B> StreamView<A, B> of(final Function<A, Stream<B>> fget) {
        return new StreamView<>(fget);
    }

    @Override
    public Stream<B> apply(A a) {
        return a == null ? Stream.empty() : getStream(a);
    }

    public Stream<B> getStream(A a) {
        return a == null ? Stream.empty() : fget.apply(a);
    }

    public <C> StreamView<A, C> andThen(final StreamView<B, C> that) {
        return new StreamView<>((A a) -> getStream(a).flatMap(that::getStream));
    }

    public <C> StreamView<A, C> andThen(final View<B, C> that) {
        return new StreamView<>((A a) -> getStream(a).map(that::get));
    }

    public <C> StreamView<C, B> compose(final StreamView<C, A> that) {
        return that.andThen(this);
    }

    public <C> StreamView<C, B> compose(final View<C, A> that) {
        return that.andThen(this);
    }
}
