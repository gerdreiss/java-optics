package com.github.gerdreiss.optics.core;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;


/**
 * "A lens is basically a getter/setter that can be used for deep updates of immutable data."
 * http://davids-code.blogspot.de/2014/02/immutable-domain-and-lenses-in-java-8.html
 *
 * @param <A> The object into whose property we want to view or update
 * @param <B> The object property which we want to view or update
 */
public class Lens<A, B> extends View<A, B> {

    private final BiFunction<A, B, A> fset;

    Lens(Function<A, B> fget, BiFunction<A, B, A> fset) {
        super(fget);
        this.fset = fset;
    }

    public static <A, B> Lens<A, B> of(Function<A, B> fget, BiFunction<A, B, A> fset) {
        return new Lens<>(fget, fset);
    }

    public A set(A target, B value) {
        return target == null ? null : fset.apply(target, value);
    }

    public Function<B, A> set(A target) {
        return (B value) -> set(target, value);
    }

    public A modify(A target, Function<B, B> modifier) {
        return set(target, modifier.apply(get(target)));
    }

    public <C> Lens<A, C> andThen(Lens<B, C> that) {
        return Lens.of(
                (A a) -> that.get(get(a)),
                (A a, C c) -> set(a, that.set(get(a), c))
        );
    }

    public <C> OptionalLens<A, C> andThen(OptionalLens<B, C> that) {
        return OptionalLens.of(
                (A a) -> that.getOptional(get(a)),
                (A a, Optional<C> maybeC) -> set(a, that.set(get(a), maybeC))
        );
    }

    public <C> StreamLens<A, C> andThen(StreamLens<B, C> that) {
        return StreamLens.of(
                (A a) -> that.getStream(get(a)),
                (A a, Stream<C> maybeC) -> set(a, that.set(get(a), maybeC))
        );
    }

    public <C> Lens<C, B> compose(Lens<C, A> that) {
        return that.andThen(this);
    }

    public <C> OptionalLens<C, B> compose(OptionalLens<C, A> that) {
        return that.andThen(this);
    }

    public <C> StreamLens<C, B> compose(StreamLens<C, A> that) {
        return that.andThen(this);
    }
}