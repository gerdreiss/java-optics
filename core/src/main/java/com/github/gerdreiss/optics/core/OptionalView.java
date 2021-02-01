package com.github.gerdreiss.optics.core;

import java.util.Optional;
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
        return a == null ? Optional.empty() : getOptional(a);
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

    public <C> OptionalView<C, B> compose(final View<C, A> that) {
        return that.andThen(this);
    }

    public <C> OptionalView<C, B> compose(final OptionalView<C, A> that) {
        return that.andThen(this);
    }

    public <C> StreamView<C, Optional<B>> compose(final StreamView<C, A> that) {
        return that.andThen(this);
    }
}
