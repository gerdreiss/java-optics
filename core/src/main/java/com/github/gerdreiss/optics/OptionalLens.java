package com.github.gerdreiss.optics;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * "A lens is basically a getter/setter that can be used for deep updates of immutable data."
 * http://davids-code.blogspot.de/2014/02/immutable-domain-and-lenses-in-java-8.html
 *
 * @param <A> The object into whose property we want to view
 * @param <B> The object property which we want to view
 */
public class OptionalLens<A, B> extends OptionalView<A, B> {

    private final BiFunction<A, B, A> fset;

    OptionalLens(Function<A, Optional<B>> fget, BiFunction<A, B, A> fset) {
        super(fget);
        this.fset = fset;
    }

    public static <A, B> OptionalLens<A, B> of(Function<A, Optional<B>> fget, BiFunction<A, B, A> fset) {
        return new OptionalLens<A, B>(fget, fset);
    }

    public A set(A target, B value) {
        return target == null ? null : fset.apply(target, value);
    }

    public Function<B, A> set(A target) {
        return (B value) -> set(target, value);
    }

    public <C> OptionalLens<A, C> andThen(OptionalLens<B, C> that) {
        return new OptionalLens<>(
                (A a) -> getOptional(a).flatMap(that::getOptional),
                (A a, C c) -> getOptional(a).map(b -> that.set(b, c)).map(b -> fset.apply(a, b)).orElse(a)
        );
    }

    public <C> OptionalLens<C, B> compose(OptionalLens<C, A> that) {
        return that.andThen(this);
    }

}
