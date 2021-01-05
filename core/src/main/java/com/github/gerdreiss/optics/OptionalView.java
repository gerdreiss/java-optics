package com.github.gerdreiss.optics;

import java.util.Optional;
import java.util.function.Function;

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

    public static <A, B> OptionalView<A, B> of(Function<A, Optional<B>> fget) {
        return new OptionalView<>(fget);
    }

    @Override
    public Optional<B> apply(A a) {
        return getOptional(a);
    }

    public Optional<B> getOptional(A a) {
        return fget.apply(a);
    }

    public <C> OptionalView<A, C> andThen(OptionalView<B, C> that) {
        return new OptionalView<>((A a) -> getOptional(a).flatMap(that::getOptional));
    }

    public <C> OptionalView<C, B> compose(final OptionalView<C, A> that) {
        return that.andThen(this);
    }
}
