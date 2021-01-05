package com.github.gerdreiss.optics;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * "A lens is basically a getter/setter that can be used for deep updates of immutable data."
 * http://davids-code.blogspot.de/2014/02/immutable-domain-and-lenses-in-java-8.html
 *
 * @param <A> The object into whose property we want to view
 * @param <B> The object property which we want to view
 */
public class Lens<A, B> extends View<A, B> {

    private final BiFunction<A, B, A> fset;

    Lens(Function<A, B> fget, BiFunction<A, B, A> fset) {
        super(fget);
        this.fset = fset;
    }

    public static <A, B> Lens<A, B> of(Function<A, B> fget, BiFunction<A, B, A> fset) {
        return new Lens<A, B>(fget, fset);
    }

    public A set(A target, B value) {
        return target == null ? null : fset.apply(target, value);
    }

    public Function<B, A> set(A target) {
        return (B value) -> set(target, value);
    }

    public <C> Lens<A, C> andThen(Lens<B, C> that) {
        return new Lens<>(
                (A a) -> that.get(get(a)),
                (A a, C c) -> set(a, that.set(get(a), c))
        );
    }

    public <C> Lens<C, B> compose(Lens<C, A> that) {
        return that.andThen(this);
    }
}
